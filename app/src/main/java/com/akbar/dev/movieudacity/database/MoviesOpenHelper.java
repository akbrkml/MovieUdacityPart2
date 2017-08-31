package com.akbar.dev.movieudacity.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.akbar.dev.movieudacity.model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akbar on 19/07/17.
 */

public class MoviesOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME ="MOVIES_DB";
    public static final int DATABASE_VERSION = 2;

    public static final String CREATE_TABLE =
            "create table " + MoviesContract.MoviesEntry.TABLE_MOVIES + " ("
                    + MoviesContract.MoviesEntry.ID + " integer primary key autoincrement, "
                    + MoviesContract.MoviesEntry.MOVIE_ID + " integer , "
                    + MoviesContract.MoviesEntry.MOVIE_POSTER_PATH + " text , "
                    + MoviesContract.MoviesEntry.MOVIE_ADULT + " integer default 0 , "
                    + MoviesContract.MoviesEntry.MOVIE_OVERVIEW + " text , "
                    + MoviesContract.MoviesEntry.MOVIE_RELEASE_DATE + " text , "
                    + MoviesContract.MoviesEntry.MOVIE_GENRES + " text , "
                    + MoviesContract.MoviesEntry.MOVIE_ORIGINAL_TITLE + " text , "
                    + MoviesContract.MoviesEntry.MOVIE_LANGUAGE + " text , "
                    + MoviesContract.MoviesEntry.MOVIE_TITLE + " text , "
                    + MoviesContract.MoviesEntry.MOVIE_BACKDROP_PATH + " text , "
                    + MoviesContract.MoviesEntry.MOVIE_POPULARITY + " text , "
                    + MoviesContract.MoviesEntry.MOVIE_VOTE_COUNT + " integer , "
                    + MoviesContract.MoviesEntry.MOVIE_VIDEO + " integer default 0 , "
                    + MoviesContract.MoviesEntry.MOVIE_VOTE_AVERAGE + " text ) ; ";

    public MoviesOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("MoviesOpenHelper", "Upgrading database from version " + oldVersion + " to " + newVersion + ". OLD DATA WILL BE DESTROYED");

        // Drop the table
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_MOVIES);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + MoviesContract.MoviesEntry.TABLE_MOVIES + "'");

        // re-create database
        onCreate(db);
    }

    public static ContentValues getMovieContentValues(Movie movies) {

        ContentValues values = new ContentValues();
        values.put(MoviesContract.MoviesEntry.MOVIE_ID, movies.getId());
        values.put(MoviesContract.MoviesEntry.MOVIE_ADULT, movies.isAdult()?1:0);
        values.put(MoviesContract.MoviesEntry.MOVIE_POSTER_PATH, movies.getPosterPath());
        values.put(MoviesContract.MoviesEntry.MOVIE_OVERVIEW, movies.getOverview());

        String genres = "";
        String prefix = "";

        for (int i=0; i<movies.getGenreIds().size(); ++i) {
            genres += prefix + movies.getGenreIds().get(i).toString();
            prefix = ",";
        }

        values.put(MoviesContract.MoviesEntry.MOVIE_GENRES, genres);
        values.put(MoviesContract.MoviesEntry.MOVIE_RELEASE_DATE, movies.getReleaseDate());
        values.put(MoviesContract.MoviesEntry.MOVIE_TITLE, movies.getTitle());
        values.put(MoviesContract.MoviesEntry.MOVIE_ORIGINAL_TITLE, movies.getOriginalTitle());
        values.put(MoviesContract.MoviesEntry.MOVIE_LANGUAGE, movies.getOriginalLanguage());
        values.put(MoviesContract.MoviesEntry.MOVIE_BACKDROP_PATH, movies.getBackdropPath());
        values.put(MoviesContract.MoviesEntry.MOVIE_POPULARITY, movies.getPopularity());
        values.put(MoviesContract.MoviesEntry.MOVIE_VIDEO, movies.getVideo()?1:0);
        values.put(MoviesContract.MoviesEntry.MOVIE_VOTE_AVERAGE, movies.getVoteAverage());
        values.put(MoviesContract.MoviesEntry.MOVIE_VOTE_COUNT, movies.getVoteCount());

        return values;
    }

    public List<Movie> getMovieById(String id){
        List<Movie> movieList = new ArrayList<Movie>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + MoviesContract.MoviesEntry.TABLE_MOVIES + " WHERE movies_id=" + id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
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

                // Adding movie to list
                movieList.add(movie);
            } while (cursor.moveToNext());
        }

        // return movie list
        return movieList;
    }
}