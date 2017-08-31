package com.akbar.dev.movieudacity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akbar.dev.movieudacity.R;
import com.akbar.dev.movieudacity.model.Review;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by akbar on 10/07/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.VideoHolder> {

    private List<Review> mReviewList;
    private Context mContext;
    private final SparseBooleanArray mCollapsedStatus;

    public ReviewAdapter(List<Review> reviews, Context context){
        this.mContext = context;
        this.mReviewList = reviews;
        mCollapsedStatus = new SparseBooleanArray();
    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtAuthor)TextView mTextViewAuthor;
        @BindView(R.id.expand_text_view)ExpandableTextView mTextViewContent;

        public VideoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_reviews, parent, false);
        VideoHolder videoHolder = new VideoHolder(rowView);
        return videoHolder;
    }

    @Override
    public void onBindViewHolder(VideoHolder holder, int position) {
        final Review review = mReviewList.get(position);

        holder.mTextViewAuthor.setText(review.getAuthor());
        holder.mTextViewContent.setText(review.getContent(), mCollapsedStatus, position);
    }

    @Override
    public int getItemCount() {
        return mReviewList.size();
    }
}