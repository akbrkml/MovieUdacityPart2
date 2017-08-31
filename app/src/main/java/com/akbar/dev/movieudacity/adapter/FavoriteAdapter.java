package com.akbar.dev.movieudacity.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akbar.dev.movieudacity.R;
import com.akbar.dev.movieudacity.model.Movie;
import com.akbar.dev.movieudacity.utils.Constant;
import com.akbar.dev.movieudacity.view.DetailMovieActivity;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.akbar.dev.movieudacity.utils.Constant.EXTRA_MOVIE;

/**
 * Created by akbar on 20/07/17.
 */

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteHolder> {

    private Context context;
    private List<Movie> movies;

    public FavoriteAdapter(Context context, List<Movie> movies) {
        this.movies = new ArrayList<Movie>();
        this.context = context;
        this.movies = movies;
    }


    public class FavoriteHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_view_title)TextView mTextViewTitle;
        @BindView(R.id.text_view_vote)TextView mTextViewVote;
        @BindView(R.id.text_view_vote_count)TextView mTextViewVoteCount;
        @BindView(R.id.text_view_date)TextView mTextViewDate;
        @BindView(R.id.image_view_backdrop)ImageView mImageViewBackdrop;

        public FavoriteHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public FavoriteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_favorite, parent, false);
        FavoriteHolder favoriteHolder = new FavoriteHolder(rowView);
        return favoriteHolder;
    }

    @Override
    public void onBindViewHolder(FavoriteHolder holder, int position) {
        Movie movie = movies.get(position);

        holder.mTextViewTitle.setText(movie.getOriginalTitle());
        holder.mTextViewVote.setText(String.valueOf(movie.getVoteAverage()));
        holder.mTextViewVoteCount.setText(String.valueOf(movie.getVoteCount()));
        holder.mTextViewDate.setText(movie.getReleaseDate());
        Picasso.with(context).load(Constant.MOVIE_BACKDROP_LINK + movie.getBackdropPath())
                .placeholder(R.drawable.backdrop)
                .into(holder.mImageViewBackdrop);

        final String extraMovie = new Gson().toJson(movie, Movie.class);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailMovieActivity.class);
                intent.putExtra(EXTRA_MOVIE, extraMovie);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }
}