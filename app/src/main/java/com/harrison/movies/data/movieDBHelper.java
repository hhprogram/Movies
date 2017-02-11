package com.harrison.movies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by harrison on 1/16/17.
 */

public class movieDBHelper extends SQLiteOpenHelper{
    //just the name of the file where the database will be stored. So the SQLITE open helper knows
    //where to access to go get the actual database. needs a .db extension or else onCreate not
    //called
    public static String DATABASE_NAME = "movies.db";
    public static final int DATABASE_VERSION = 2;

    public movieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database doesn't exist and there has been a call to write or query it.
     * Not auto called when an instance of movieDBHelper is created. Just the code for the initial
     * setup of the actual table laid out in the movieContract file
     * @param sqLiteDatabase the writable or readable DB instance object that we are getting via
     *                       creating a movieDBHelper instance and then calling getWritabledatabase
     *                       /getReadableDatabase on it
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        System.out.println("ehlloo");
        //string that is the SQL statement to be used to actually create the table for MOVIE_ENTRIES
        //note need to remember the parans around the columns and that there needs to be a space
        //between the column name and its type
        final String SQL_CREATE_TOP_RATED_TABLE = "CREATE TABLE "
                + movieContract.TopRated.TABLE_NAME
                + " ("
                + movieContract.TopRated._ID + " integer PRIMARY KEY AUTOINCREMENT,"
                + movieContract.TopRated.EN_TITLE + " text NOT NULL, "
                + movieContract.TopRated.ORIG_TITLE + " text NOT NULL, "
                + movieContract.TopRated.OVERVIEW + " text NOT NULL, "
                + movieContract.TopRated.POSTER_URL + " text NOT NULL, "
                + movieContract.TopRated.BIG_POSTER_URL + " text NOT NULL, "
                + movieContract.TopRated.REL_DATE + " text NOT NULL, "
                + movieContract.TopRated.REVIEW + " text NOT NULL, "
                + movieContract.TopRated.VOTE + " real NOT NULL, "
                + movieContract.TopRated.TRAILER_YOUTUBE_KEY + " text NOT NULL, "
                + movieContract.TopRated.MOVIE_DB_ID + " integer NOT NULL "
                + " );";

        final String SQL_CREATE_FAV_TABLE = "CREATE TABLE "
                + movieContract.FavoriteEntries.TABLE_NAME
                + " ("
                + movieContract.FavoriteEntries._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + movieContract.FavoriteEntries.EN_TITLE + " TEXT NOT NULL, "
                + movieContract.FavoriteEntries.ORIG_TITLE + " TEXT NOT NULL, "
                + movieContract.FavoriteEntries.OVERVIEW + " TEXT NOT NULL, "
                + movieContract.FavoriteEntries.POSTER_URL + " TEXT NOT NULL, "
                + movieContract.FavoriteEntries.BIG_POSTER_URL + " TEXT NOT NULL, "
                + movieContract.FavoriteEntries.REL_DATE + " TEXT NOT NULL, "
                + movieContract.FavoriteEntries.REVIEW + " TEXT NOT NULL, "
                + movieContract.FavoriteEntries.VOTE + " REAL NOT NULL, "
                + movieContract.FavoriteEntries.TRAILER_YOUTUBE_KEY + " TEXT NOT NULL, "
                + movieContract.FavoriteEntries.MOVIE_DB_ID + " INTEGER NOT NULL "
                + " );";

        final String SQL_CREATE_MOST_POPULAR = "CREATE TABLE "
                + movieContract.MostPopular.TABLE_NAME
                + " ("
                + movieContract.MostPopular._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + movieContract.MostPopular.EN_TITLE + " TEXT NOT NULL, "
                + movieContract.MostPopular.ORIG_TITLE + " TEXT NOT NULL, "
                + movieContract.MostPopular.OVERVIEW + " TEXT NOT NULL, "
                + movieContract.MostPopular.POSTER_URL + " TEXT NOT NULL, "
                + movieContract.MostPopular.BIG_POSTER_URL + " TEXT NOT NULL, "
                + movieContract.MostPopular.REL_DATE + " TEXT NOT NULL, "
                + movieContract.MostPopular.REVIEW + " TEXT NOT NULL, "
                + movieContract.MostPopular.VOTE + " REAL NOT NULL, "
                + movieContract.MostPopular.TRAILER_YOUTUBE_KEY + " TEXT NOT NULL, "
                + movieContract.MostPopular.MOVIE_DB_ID + " INTEGER NOT NULL "
                + " );";

        final String SQL_CREATE_UPCOMING = "CREATE TABLE "
                + movieContract.Upcoming.TABLE_NAME
                + " ("
                + movieContract.Upcoming._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + movieContract.Upcoming.EN_TITLE + " TEXT NOT NULL, "
                + movieContract.Upcoming.ORIG_TITLE + " TEXT NOT NULL, "
                + movieContract.Upcoming.OVERVIEW + " TEXT NOT NULL, "
                + movieContract.Upcoming.POSTER_URL + " TEXT NOT NULL, "
                + movieContract.Upcoming.BIG_POSTER_URL + " TEXT NOT NULL, "
                + movieContract.Upcoming.REL_DATE + " TEXT NOT NULL, "
                + movieContract.Upcoming.REVIEW + " TEXT NOT NULL, "
                + movieContract.Upcoming.VOTE + " REAL NOT NULL, "
                + movieContract.Upcoming.TRAILER_YOUTUBE_KEY + " TEXT NOT NULL, "
                + movieContract.Upcoming.MOVIE_DB_ID + " INTEGER NOT NULL "
                + " );";

        final String SQL_CREATE_NOW_PLAYING = "CREATE TABLE "
                + movieContract.NowPlaying.TABLE_NAME
                + " ("
                + movieContract.NowPlaying._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + movieContract.NowPlaying.EN_TITLE + " TEXT NOT NULL, "
                + movieContract.NowPlaying.ORIG_TITLE + " TEXT NOT NULL, "
                + movieContract.NowPlaying.OVERVIEW + " TEXT NOT NULL, "
                + movieContract.NowPlaying.POSTER_URL + " TEXT NOT NULL, "
                + movieContract.NowPlaying.BIG_POSTER_URL + " TEXT NOT NULL, "
                + movieContract.NowPlaying.REL_DATE + " TEXT NOT NULL, "
                + movieContract.NowPlaying.REVIEW + " TEXT NOT NULL, "
                + movieContract.NowPlaying.VOTE + " REAL NOT NULL, "
                + movieContract.NowPlaying.TRAILER_YOUTUBE_KEY + " TEXT NOT NULL, "
                + movieContract.NowPlaying.MOVIE_DB_ID + " INTEGER NOT NULL "
                + " );";


        sqLiteDatabase.execSQL(SQL_CREATE_TOP_RATED_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAV_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOST_POPULAR);
        sqLiteDatabase.execSQL(SQL_CREATE_UPCOMING);
        sqLiteDatabase.execSQL(SQL_CREATE_NOW_PLAYING);
    }

    /**
     * Not implemented yet as I just want to alter the favorites table
     * @param sqLiteDatabase
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //
        Log.d(TAG, "onUpgrade: not implemented yet");
    }
}
