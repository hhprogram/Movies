package com.harrison.movies.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by harrison on 1/16/17.
 * contract class - this is the class that I need to implement that in order to define my database
 * that I will be using for the movies app and how the content provider movieProvider or any
 * other provider can interact with the database.
 * Defines the tables and columns within each table, also add methods that build / retrieve URIs
 * so that other classes know how to interact with the db through the content providers
 *
 *
 */

public class movieContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device. (the package name for the app is just com.harrison.movies
    public static final String CONTENT_AUTHORITY = "com.harrison.movies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //a valid path for a content URI. Therfore the URI content://com.harrison.movies/movies will be
    //a valid path to look at the movies data (ie the table of data). Only really need 3 for now
    //as main functionality of app is to just pull data related to movies and then have a table
    //hold the user's 'favorites' (this is a stage 2 addition). Will have a TopRated table and a
    //most Popular table so that I can cache results for either of those queries
    public static final String PATH_TOP_RATED = "top_rated";
    public static final String PATH_MOST_POPULAR = "most_popular";
    public static final String PATH_UPCOMING = "upcoming";
    public static final String PATH_NOW_PLAYING = "now_playing";
    public static final String PATH_FAVORITES = "favorites";
    public static final String[] ATTR_COLUMNS = {TopRated.ORIG_TITLE, TopRated.OVERVIEW
            , TopRated.POSTER_URL, TopRated.REL_DATE, TopRated.VOTE, TopRated.BIG_POSTER_URL
            , TopRated.EN_TITLE, TopRated.TRAILER_YOUTUBE_KEY, TopRated.REVIEW
            , TopRated.MOVIE_DB_ID};

    /**
     * static class that defines the schema of my TOPRATED table and what columns it will contain. It
     * implements BaseColumns which has a default _ID variable for primary key usage (which auto
     * increments) as well as a count variable to keep track of number of data rows. Which means
     * I do not have to explicitly define either one in the static class
     */
    public static final class TopRated implements BaseColumns {
        //buildUpon creates a new URI builder object that starts from the URI contents of
        // BASE_CONTENT_URI, then BUILD() returns a URI
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOP_RATED)
                .build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED;

        //doesn't have to be like this, can have table name be different than the actual path to
        //the db file - but doing this for simplicity so that my build URI methods can be just in
        //the MovieContract class and not in each table class, can just refer to the table name
        //and that will just be the actual path as well
        public static final String TABLE_NAME = PATH_TOP_RATED;


        public static final String ORIG_TITLE = "title";
        public static final String OVERVIEW = "overview";
        public static final String POSTER_URL = "poster_url";
        public static final String REL_DATE = "release_date";
        public static final String VOTE = "vote_avg";
        public static final String BIG_POSTER_URL = "big_poster_url";
        public static final String EN_TITLE = "en_title";
        //this is the 'key' for the trailer of movie that is hosted on youtube
        //ie make a youtube trailer URL https://www.youtube.com/watch?v=<YOUTUBE_KEY>.
        public static final String TRAILER_YOUTUBE_KEY = "youtube_key";
        //the URL to the movieDB hosted review of the movie if there exists one
        public static final String REVIEW = "review";
        //movie ID per movieDB - unique
        public static final String MOVIE_DB_ID = "movieDB_id";

    }

    public static final class MostPopular implements BaseColumns {
        //buildUpon creates a new URI builder object that starts from the URI contents of
        // BASE_CONTENT_URI, then BUILD() returns a URI
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOP_RATED)
                .build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED;

        public static final String TABLE_NAME = "most_popular";

        //id of this movie in the FAVORITES table - so we can do joins. not a foreign key though as
        //should allow entries within the movie table even if the favorites table is empty
        //commented this out as don't need it. Decided to add the unique movieID from movieDB and
        //then if we want movies from TopRated or MostPopular then can just join on that column
//        public static final String FAV_KEY = "fav_id";

        public static final String ORIG_TITLE = "title";
        public static final String OVERVIEW = "overview";
        public static final String POSTER_URL = "poster_url";
        public static final String REL_DATE = "release_date";
        public static final String VOTE = "vote_avg";
        public static final String BIG_POSTER_URL = "big_poster_url";
        public static final String EN_TITLE = "en_title";
        //this is the 'key' for the trailer of movie that is hosted on youtube
        //ie make a youtube trailer URL https://www.youtube.com/watch?v=<YOUTUBE_KEY>.
        public static final String TRAILER_YOUTUBE_KEY = "youtube_key";
        //the URL to the movieDB hosted review of the movie if there exists one
        public static final String REVIEW = "review";
        //movie ID per movieDB - unique
        public static final String MOVIE_DB_ID = "movieDB_id";

    }

    /**
     * static class that defines the schema of my FAVORITES table and what columns it will contain.
     * It implements BaseColumns which has a default _ID variable for primary key usage (which auto
     * increments) as well as a count variable to keep track of number of data rows. Which means
     * I do not have to explicitly define either one in the static class.
     *
     * Will contain all the same columns as the MOVIES table as we need to contain all relevant info
     * in the database as we don't wont to do a web query just for favorites per specs. Except we
     * take out FAV_ID which we don't need because don't need a foreign key column
     */
    public static final class FavoriteEntries implements BaseColumns {
        //buildUpon creates a new URI builder object that starts from the URI contents of
        // BASE_CONTENT_URI, then BUILD() returns a URI
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES)
                .build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_FAVORITES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_FAVORITES;

        public static final String TABLE_NAME = "favorites";

        public static final String ORIG_TITLE = "title";
        public static final String OVERVIEW = "overview";
        public static final String POSTER_URL = "poster_url";
        public static final String REL_DATE = "release_date";
        public static final String VOTE = "vote_avg";
        public static final String BIG_POSTER_URL = "big_poster_url";
        public static final String EN_TITLE = "en_title";
        //this is the 'key' for the trailer of movie that is hosted on youtube
        //ie make a youtube trailer URL https://www.youtube.com/watch?v=<YOUTUBE_KEY>.
        public static final String TRAILER_YOUTUBE_KEY = "youtube_key";
        //the URL to the movieDB hosted review of the movie if there exists one
        public static final String REVIEW = "review";
        //movie ID per movieDB - unique
        public static final String MOVIE_DB_ID = "movieDB_id";

    }

    public static final class Upcoming implements BaseColumns {
        //buildUpon creates a new URI builder object that starts from the URI contents of
        // BASE_CONTENT_URI, then BUILD() returns a URI
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOP_RATED)
                .build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED;

        public static final String TABLE_NAME = "upcoming";


        public static final String ORIG_TITLE = "title";
        public static final String OVERVIEW = "overview";
        public static final String POSTER_URL = "poster_url";
        public static final String REL_DATE = "release_date";
        public static final String VOTE = "vote_avg";
        public static final String BIG_POSTER_URL = "big_poster_url";
        public static final String EN_TITLE = "en_title";
        //this is the 'key' for the trailer of movie that is hosted on youtube
        //ie make a youtube trailer URL https://www.youtube.com/watch?v=<YOUTUBE_KEY>.
        public static final String TRAILER_YOUTUBE_KEY = "youtube_key";
        //the URL to the movieDB hosted review of the movie if there exists one
        public static final String REVIEW = "review";
        //movie ID per movieDB - unique
        public static final String MOVIE_DB_ID = "movieDB_id";
    }

    public static final class NowPlaying implements BaseColumns {
        //buildUpon creates a new URI builder object that starts from the URI contents of
        // BASE_CONTENT_URI, then BUILD() returns a URI
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOP_RATED)
                .build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED;

        public static final String TABLE_NAME = "now_playing";


        public static final String ORIG_TITLE = "title";
        public static final String OVERVIEW = "overview";
        public static final String POSTER_URL = "poster_url";
        public static final String REL_DATE = "release_date";
        public static final String VOTE = "vote_avg";
        public static final String BIG_POSTER_URL = "big_poster_url";
        public static final String EN_TITLE = "en_title";
        //this is the 'key' for the trailer of movie that is hosted on youtube
        //ie make a youtube trailer URL https://www.youtube.com/watch?v=<YOUTUBE_KEY>.
        public static final String TRAILER_YOUTUBE_KEY = "youtube_key";
        //the URL to the movieDB hosted review of the movie if there exists one
        public static final String REVIEW = "review";
        //movie ID per movieDB - unique
        public static final String MOVIE_DB_ID = "movieDB_id";
    }

    /**
     * Method that builds for the content provider to return when inserting, this URI is returned
     * and potentially used later to denote what piece of data from the contentprovider we want
     * (coupled with get methods to decipher the incoming URI which would come from a query method
     * call for example). In the Weather app example - they had these methods within each staticn
     * class of each table but since all these tables are the same can just make methods for each
     * one
     * @param table - the table that we are specifically building the URI for
     * @param movieId - the movie ID of this piece of data in TABLE (used to identify the movie
     *                i want went the URI is sent to the content Provider - ex. during a click
     *                to go to the detail view, use it to query and can be easily passed down from
     *                the movie instance object attribute to the movieProvider) Or else can't easily
     *                do that if just using the rowId within the DB - as the movie object has no
     *                knowledge of what row it is put into the DB
     * @return returns a URI that can be later used by the application to tell the content provider
     * which piece of data it wants from the provider
     */
    public static Uri buildMovieUri(String table, int movieId) {
        //first I take the BASE_CONTENT_URI and make it into a URI builder with buildupon(). Then
        //I add the path segments TABLE and ROWID to give the URI enough info for next use as
        //TABLE tells me which db table it sits in and rowID tells me the exact row in which it
        //sits in
        return BASE_CONTENT_URI.buildUpon().appendPath(table).appendPath(Integer.toString(movieId))
                .build();
    }

    /**
     * Method to build a URI for just the main view that shows multiple movies - ie we want a URI
     * to the table of the particular search preference
     * @param table - the desired table where the movies are
     * @return URI to be used with the movieProvider to tell provider which table to obtain the data
     */
    public static Uri buildMovieUri(String table) {
        //first I take the BASE_CONTENT_URI and make it into a URI builder with buildupon(). Then
        //I add the path segments TABLE and ROWID to give the URI enough info for next use as
        //TABLE tells me which db table it sits in and rowID tells me the exact row in which it
        //sits in
        return BASE_CONTENT_URI.buildUpon().appendPath(table).build();
    }

    /**
     * Gets the unique movieDB id from the given URI
     * @param uri_request - the requested uri to content provider to obtain the data associated
     *                    with this specific URI
     * @return
     */
    public static int getMovieUri(Uri uri_request) {
        //the first indext of getPathSegments is the TABLE (see above buildMovieUri method
        return  Integer.parseInt(uri_request.getPathSegments().get(1));
    }

    /**
     * Method to retrieve a URI for just the main view that shows multiple movies - ie we want a URI
     * to the table of the particular search preference
     * @param uri - the desired table where the movies are
     * @return table name
     */
    public static String getTableUri(Uri uri) {
        //first I take the BASE_CONTENT_URI and make it into a URI builder with buildupon(). Then
        //I add the path segments TABLE and ROWID to give the URI enough info for next use as
        //TABLE tells me which db table it sits in and rowID tells me the exact row in which it
        //sits in
        return uri.getPathSegments().get(0);
    }


}
