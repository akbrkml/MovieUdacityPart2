package com.akbar.dev.movieudacity.view;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.akbar.dev.movieudacity.R;
import com.akbar.dev.movieudacity.database.MoviesContract;
import com.akbar.dev.movieudacity.database.MoviesOpenHelper;
import com.akbar.dev.movieudacity.model.Movie;
import com.akbar.dev.movieudacity.utils.Constant;
import com.akbar.dev.movieudacity.utils.GenreHelper;
import com.akbar.dev.movieudacity.utils.ShareUtils;
import com.akbar.dev.movieudacity.utils.ViewUtils;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.akbar.dev.movieudacity.utils.Constant.EXTRA_MOVIE;

public class DetailMovieActivity extends AppCompatActivity {

    private static final String TAG = DetailMovieActivity.class.getSimpleName();

    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.img_poster_detail)ImageView mImageViewPoster;
    @BindView(R.id.backdrop)ImageView mImageViewBackdrop;
    @BindView(R.id.year)TextView mTextViewYear;
    @BindView(R.id.genre)TextView mTextViewGenre;
    @BindView(R.id.rating)TextView mTextViewRating;
    @BindView(R.id.overview)TextView mTextViewOverview;
    @BindView(R.id.fab)FloatingActionButton fab;

    private Movie mMovie;
    private MoviesOpenHelper movieDB;
    private List<Movie> movies;

    private String extraMovie;
    private boolean isFavoriteChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getDataIntent();

        bindData();

        initDatabase();

        isMovieHasData();
    }

    private void initDatabase(){ movieDB = new MoviesOpenHelper(this); }

    private void isMovieHasData(){
        movies = movieDB.getMovieById(String.valueOf(mMovie.getId()));
        if (movies.size() == 0){
            fab.setImageResource(R.drawable.heart_outline);
        }else {
            fab.setImageResource(R.drawable.heart);
        }
    }

    private void getDataIntent(){
        if (getIntent().hasExtra(EXTRA_MOVIE)) {
            extraMovie = getIntent().getStringExtra(EXTRA_MOVIE);
        } else {
            throw new IllegalArgumentException(getString(R.string.intent_warning));
        }

        mMovie = new Gson().fromJson(extraMovie, Movie.class);
    }

    private void bindData(){
        if (mMovie != null){
            setTitle(mMovie.getOriginalTitle());
            Picasso.with(this).load(Constant.MOVIE_POSTER_LINK + mMovie.getPosterPath())
                    .placeholder(R.drawable.backdrop)
                    .into(mImageViewPoster);
            Picasso.with(this).load(Constant.MOVIE_BACKDROP_LINK + mMovie.getBackdropPath())
                    .placeholder(R.drawable.backdrop)
                    .into(mImageViewBackdrop);
            mTextViewYear.setText(mMovie.getReleaseDate());
            mTextViewRating.setText(String.valueOf(mMovie.getVoteAverage()));
            mTextViewOverview.setText(mMovie.getOverview());
            mTextViewGenre.setText(GenreHelper.getGenreNamesList(mMovie.getGenreIds()));
        }
    }

    @OnClick(R.id.fab)
    public void saveToFavorite(View view){
        saveFav();
    }

    private void saveFav(){
        movies = movieDB.getMovieById(String.valueOf(mMovie.getId()));
        if (movies.size() == 0){
            addToFav();
        } else {
            removeFav(mMovie.getId());
        }
    }

    private void addToFav(){
        ContentValues values = MoviesOpenHelper.getMovieContentValues(mMovie);
        getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, values);

        ViewUtils.showSnackbarAction(getString(R.string.add_favorite_message), findViewById(android.R.id.content));
        isMovieHasData();
    }

    private void removeFav(int id){
        getContentResolver().delete(MoviesContract.MoviesEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build(), null, null);

        ViewUtils.showSnackbar(getString(R.string.remove_favorite_message), findViewById(android.R.id.content));
        isMovieHasData();
    }

    @OnClick(R.id.btn_trailer_review)
    public void showTrailerAndReview(){
        moveActivity();
    }

    private void moveActivity(){
        Intent intent = new Intent(getApplicationContext(), TrailerReviewActivity.class);
        intent.putExtra(getString(R.string.intent_id), mMovie.getId());
        intent.putExtra(getString(R.string.intent_title), mMovie.getOriginalTitle());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_share:
                onClickShare();
                break;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_movie, menu);
        return true;
    }

    private void onClickShare() {
        if (mMovie != null) {
            String message = "#Movista# \n\nMovie title : " + mMovie.getOriginalTitle() + "\n\n" +
                    mMovie.getOverview() + "\n\n" +
                    "Rating : \u2605 " + mMovie.getVoteAverage() + "\n" +
                    "Release date : " + mMovie.getReleaseDate() + "\n";
            ShareUtils.shareCustom(message, this);
        }
    }
}