package com.akbar.dev.movieudacity.view;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.akbar.dev.movieudacity.BuildConfig;
import com.akbar.dev.movieudacity.R;
import com.akbar.dev.movieudacity.adapter.ReviewAdapter;
import com.akbar.dev.movieudacity.api.ApiClient;
import com.akbar.dev.movieudacity.api.ApiInterface;
import com.akbar.dev.movieudacity.model.Review;
import com.akbar.dev.movieudacity.model.ReviewResponse;
import com.akbar.dev.movieudacity.model.Video;
import com.akbar.dev.movieudacity.model.VideoResponse;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.crosswall.lib.coverflow.CoverFlow;
import me.crosswall.lib.coverflow.core.PageItemClickListener;
import me.crosswall.lib.coverflow.core.PagerContainer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.akbar.dev.movieudacity.utils.Constant.API_KEY;

public class TrailerReviewActivity extends AppCompatActivity {

    private static final String TAG = TrailerReviewActivity.class.getSimpleName();

    @BindView(R.id.pager_container)PagerContainer mContainer;
    @BindView(R.id.recycler_view_review)RecyclerView mRecyclerViewReviews;
    @BindView(R.id.tv_message_review_display)TextView mTextViewReviewMessage;
    @BindView(R.id.tv_message_trailer_display)TextView mTextViewTrailerMessage;

    ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

    private ViewPager pager;
    private LinearLayoutManager layoutManager;

    private Integer mIdFilm;
    private String mTitleFilm;
    private List<Video> video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer_review);

        ButterKnife.bind(this);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initComponents();

        getDataIntent();

        loadVideoTrailers();
        loadReviewMovies();
    }

    private void initComponents(){
        pager = mContainer.getViewPager();

        pager.setClipChildren(false);
        //
        pager.setOffscreenPageLimit(15);

        mContainer.setPageItemClickListener(new PageItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(TrailerReviewActivity.this,"position:" + position, Toast.LENGTH_SHORT).show();
            }
        });

        boolean showTransformer = getIntent().getBooleanExtra("showTransformer",false);

        if(showTransformer){

            new CoverFlow.Builder()
                    .with(pager)
                    .scale(0.3f)
                    .pagerMargin(getResources().getDimensionPixelSize(R.dimen.pager_margin))
                    .spaceSize(0f)
                    .build();

        }else{
            pager.setPageMargin(30);
        }

        initRecyclerView();
    }

    private void initRecyclerView(){
        layoutManager = new LinearLayoutManager(this);
        mRecyclerViewReviews.setHasFixedSize(true);
        mRecyclerViewReviews.setLayoutManager(layoutManager);
        mRecyclerViewReviews.setItemAnimator(new DefaultItemAnimator());
    }

    private void loadVideoTrailers(){
        callVideoTrailers().enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                video = response.body().getResults();
                Log.d(TAG + " onResponse: ", video.toString());
                if (video.size() < 1){
                    mTextViewTrailerMessage.setVisibility(View.VISIBLE);
                    mTextViewTrailerMessage.setText(getString(R.string.message_no_trailer) + " " + mTitleFilm);
                    pager.setVisibility(View.GONE);
                }else {
                    pager.setAdapter(new TrailerPagerAdapter(video));
                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                Log.d(TAG + " onFailure: ", t.getMessage());
            }
        });
    }

    private void loadReviewMovies(){
        callReviewMovies().enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                List<Review> reviews = response.body().getResults();
                Log.d(TAG + " onResponse: ", reviews.toString());
                if (reviews.size() < 1){
                    mTextViewReviewMessage.setVisibility(View.VISIBLE);
                    mTextViewReviewMessage.setText(getString(R.string.message_no_review) + " " + mTitleFilm);
                    mRecyclerViewReviews.setVisibility(View.GONE);
                }else {
                    mRecyclerViewReviews.setAdapter(new ReviewAdapter(reviews, getApplicationContext()));
                }

            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                Log.d(TAG + " onFailure: ", t.getMessage());
            }
        });
    }

    private Call<VideoResponse> callVideoTrailers(){
        return apiService.getVideoTrailers(
                mIdFilm, API_KEY
        );
    }

    private Call<ReviewResponse> callReviewMovies(){
        return apiService.getReviewMovies(
                mIdFilm, API_KEY
        );
    }

    public class TrailerPagerAdapter extends PagerAdapter implements YouTubeThumbnailView.OnInitializedListener {

        YouTubeThumbnailLoader.OnThumbnailLoadedListener onThumbnailLoadedListener;

        @BindView(R.id.imageView_thumbnail)YouTubeThumbnailView mYoutubeThumbnail;

        List<Video> mVideos;

        public TrailerPagerAdapter(List<Video> videoList){
            this.mVideos = videoList;
        }

        @Override
        public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
            youTubeThumbnailLoader.setVideo((String) youTubeThumbnailView.getTag());
            youTubeThumbnailLoader.setOnThumbnailLoadedListener(onThumbnailLoadedListener);
        }

        @Override
        public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
            final String errorMessage = youTubeInitializationResult.toString();
            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final View view = LayoutInflater.from(TrailerReviewActivity.this).inflate(R.layout.item_trailer,null);
            ButterKnife.bind(this, view);

            mYoutubeThumbnail.setTag(video.get(position).getKey());
            mYoutubeThumbnail.initialize(BuildConfig.API_YT, this);
            mYoutubeThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_youtube) + video.get(position).getKey()));
                    v.getContext().startActivity(intent);
                }
            });

            onThumbnailLoadedListener = new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                @Override
                public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                    youTubeThumbnailView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {

                }
            };

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            mContainer.removeView((View)object);
        }

        @Override
        public int getCount() {
            return mVideos.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }
    }

    private void getDataIntent(){
        if (getIntent().hasExtra(getString(R.string.intent_id)) && getIntent().hasExtra(getString(R.string.intent_title))) {
            mIdFilm = getIntent().getIntExtra(getString(R.string.intent_id), 0);
            mTitleFilm = getIntent().getStringExtra(getString(R.string.intent_title));
        } else {
            throw new IllegalArgumentException(getString(R.string.intent_warning));
        }
        setTitle(getString(R.string.title_activity_trailer_review) + " " + mTitleFilm);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }
}