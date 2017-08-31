package com.akbar.dev.movieudacity.view;

import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.akbar.dev.movieudacity.R;
import com.akbar.dev.movieudacity.adapter.FavoriteAdapter;
import com.akbar.dev.movieudacity.database.MoviesContract;
import com.akbar.dev.movieudacity.database.MoviesOpenHelper;
import com.akbar.dev.movieudacity.model.Movie;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int FAVORITE_MOVIES_LOADER = 0;

    @BindView(R.id.recycler_view_favorite)RecyclerView mRecyclerViewFavorite;
    @BindView(R.id.tv_message_display)TextView mTextViewMessage;

    private LinearLayoutManager layoutManager;

    private MoviesOpenHelper movieDB;
    private List<Movie> mMovies = new ArrayList<>();
    private FavoriteAdapter favoriteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        ButterKnife.bind(this);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getString(R.string.title_activity_favorite));

        getSupportLoaderManager().initLoader(FAVORITE_MOVIES_LOADER, null, this);

        initRecyclerView();
    }

    private List<Movie> getMoviesFromCursor(Cursor cursor) {

        List<Movie> movies = new ArrayList<>();

        if (cursor != null) {
            /*Log.e("cursor length","->"+cursor.getCount());
            Log.e("column length","->"+cursor.getColumnCount());*/

            if (cursor.moveToFirst()){
                do{

                    int movie_id = cursor.getInt(cursor.getColumnIndex(MoviesContract.MoviesEntry.MOVIE_ID));
                    boolean movie_adult = cursor.getInt(cursor.getColumnIndex(MoviesContract.MoviesEntry.MOVIE_ADULT))==1;
                    String movie_poster_path = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.MOVIE_POSTER_PATH));
                    String movie_overview = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.MOVIE_OVERVIEW));
                    String movie_release_date = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.MOVIE_RELEASE_DATE));
                    String genre = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.MOVIE_GENRES));

                    List<Integer> movie_genre = new ArrayList<>();
                    for (String s : genre.split(","))
                        movie_genre.add(Integer.parseInt(s));

                    String movie_title = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.MOVIE_TITLE));
                    String movie_original_title = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.MOVIE_ORIGINAL_TITLE));
                    String movie_language = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.MOVIE_LANGUAGE));
                    String movie_backdrop_path = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.MOVIE_BACKDROP_PATH));
                    String movie_popularity = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.MOVIE_POPULARITY));
                    boolean movie_video = cursor.getInt(cursor.getColumnIndex(MoviesContract.MoviesEntry.MOVIE_VIDEO))==1;
                    String movie_vote_average = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.MOVIE_VOTE_AVERAGE));
                    int movie_vote_count = cursor.getInt(cursor.getColumnIndex(MoviesContract.MoviesEntry.MOVIE_VOTE_COUNT));

                    Movie movie = new Movie(movie_id, movie_poster_path,
                            movie_adult, movie_overview, movie_release_date,
                            movie_genre, movie_original_title, movie_language,
                            movie_title, movie_backdrop_path, Double.parseDouble(movie_popularity),
                            movie_vote_count, movie_video, Double.parseDouble(movie_vote_average));

                    movies.add(movie);

                }while(cursor.moveToNext());
            }
        }

        return movies;
    }

    private void initRecyclerView(){
        layoutManager = new LinearLayoutManager(this);
        mRecyclerViewFavorite.setHasFixedSize(true);
        mRecyclerViewFavorite.setLayoutManager(layoutManager);
        mRecyclerViewFavorite.setItemAnimator(new DefaultItemAnimator());
        initAdapter(mMovies);
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

    private void initAdapter(List<Movie> movies) {
        favoriteAdapter = new FavoriteAdapter(this, movies);
        mRecyclerViewFavorite.setAdapter(favoriteAdapter);
    }

    private void showFavoriteDataView() {
        /* First, make sure the error is invisible */
        mTextViewMessage.setVisibility(View.INVISIBLE);
        /* Then, make sure the movie data is visible */
        mRecyclerViewFavorite.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerViewFavorite.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mTextViewMessage.setVisibility(View.VISIBLE);
        mTextViewMessage.setText(R.string.message_no_favorite);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getApplicationContext(),
                MoviesContract.MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() > 0){
            showFavoriteDataView();
            initAdapter(getMoviesFromCursor(data));
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}