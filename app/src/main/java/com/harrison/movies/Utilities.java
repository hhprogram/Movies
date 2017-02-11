package com.harrison.movies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.harrison.movies.data.movieContract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

/**
 * Created by harrison on 1/26/17.
 * trying to make a class to help organization of helpful methods that don't really belong to
 * any class or instance
 *
 * Note if using an activity or context you can just directly call getString but if not in one
 * of those then need to call the static method I created in MainActivity that retrieves an instance
 * of a context so that i can access this app's resources
 *
 * Think i can't use Resources.getSystem().getResources because this only gets system wide resources
 * and not the local app resources that i have defined
 */

public class Utilities {
    static String YOUTUBE_BASE =  MainActivity.getContext().getResources()
            .getString(R.string.youtube_base);
    //the strings used for preset web requests that align with our list preferences
    static final String TOP_RATED = MainActivity.getContext().getResources()
            .getString(R.string.tr_api);
    static final String POPULAR = MainActivity.getContext().getResources()
            .getString(R.string.pop_api);
    static final String NOW_PLAYING = MainActivity.getContext().getResources()
            .getString(R.string.np_api);
    static final String UPCOMING = MainActivity.getContext().getResources()
            .getString(R.string.upcoming_api);
    //requests for the getVideo and getReview web requests through the movieDB API
    static final String VIDEOS = MainActivity.getContext().getResources()
            .getString(R.string.videos_api);
    static final String REVIEWS = MainActivity.getContext().getResources()
            .getString(R.string.review_api);
    static final String INDIVIDUAL_MOVIE = MainActivity.getContext().getResources()
            .getString(R.string.movie_api);
    //attribute field names in the JSON for each movie that will be used in this app
    static final String MOVIE_TITLE = MainActivity.getContext().getResources()
            .getString(R.string.movie_title_json);
    static final String VOTE_AVG = MainActivity.getContext().getResources().getString(R.string.vote_json);
    static final String OVERVIEW = MainActivity.getContext().getResources()
            .getString(R.string.overview_json);
    static final String MOVIE_EN_TITLE = MainActivity.getContext().getResources()
            .getString(R.string.en_title_json);
    static final String ORIG_LANG = MainActivity.getContext().getResources()
            .getString(R.string.orig_lang_json);
    static final String RELEASE = MainActivity.getContext().getResources()
            .getString(R.string.release_json);
    static final String ID = MainActivity.getContext().getResources()
            .getString(R.string.id_json);
    static final String YOUTUBE_KEY = MainActivity.getContext().getResources()
            .getString(R.string.key_json);
    static final String REVIEW_URL = MainActivity.getContext().getResources()
            .getString(R.string.url_json);
    static final String REVIEW_AUTHOR = MainActivity.getContext().getResources()
            .getString(R.string.author_json);
    static final String TRAILER_NAME = MainActivity.getContext().getResources()
            .getString(R.string.name_json);
    //key to obtain the array in the JSONObject that contains the list of movies associated with the
    ///web request sent
    static final String RESULTS = MainActivity.getContext().getResources()
            .getString(R.string.results_json);

    static final String DATE_FORMAT = MainActivity.getContext().getResources()
            .getString(R.string.date_frmt_api);
    //the number of pages gathered in a moviedb query.
//    static final String NUM_PAGES = "50";
//    static final String QUERY = "?";
    static final String BEGIN_DATE = MainActivity.getContext().getResources()
            .getString(R.string.beg_date_api);
    static final String END_DATE = MainActivity.getContext().getResources()
            .getString(R.string.end_date_api);
    static final String API_KEY = MainActivity.getContext().getResources()
            .getString(R.string.api_key_api);
    static final String MOVIE = MainActivity.getContext().getResources()
            .getString(R.string.movie_api);
    static final String SEARCH = MainActivity.getContext().getResources()
            .getString(R.string.search_api);
    //the base URL for every movieDB request
    static final String SCHEME = MainActivity.getContext().getResources()
            .getString(R.string.scheme_api);
    static final String AUTHORITY = MainActivity.getContext().getResources()
            .getString(R.string.authority_api);
    static final String IMAGE_BASE = MainActivity.getContext().getResources()
            .getString(R.string.image_api);
    static final String IMAGE_SIZE = MainActivity.getContext().getResources()
            .getString(R.string.grid_img_api);
    static final String BIG_IMAGE = MainActivity.getContext().getResources()
            .getString(R.string.big_img_api);
    //just the number that comes after AUTHORITY, think it denotes the movieDb API version
    static final String MOVIEDB_V3 = MainActivity.getContext().getResources()
            .getString(R.string.version_api);
    //this is actually the string for if i want the movie credits associated with this person which
    //is the only thing I would want for this app
    static final String PERSON = MainActivity.getContext().getResources()
            .getString(R.string.person_api);
    static final String PERSON_MOVIE = MainActivity.getContext().getResources()
            .getString(R.string.persom_movie_api);
    //v3 API key for movieDB
    static final String KEY_V3 = "???use your own";
    static final String POSTER_KEY = MainActivity.getContext().getResources()
            .getString(R.string.poster_api);
    static final String DELIMITER = MainActivity.getContext().getResources()
            .getString(R.string.delimiter);
    static HashMap<String, String> queries = new HashMap<>();
    static final String DETAIL_YOUTUBE_LABEL = MainActivity.getContext().getResources()
            .getString(R.string.trailer_label);
    static final String DETAIL_REVIEW_LABEL = MainActivity.getContext().getResources()
            .getString(R.string.review_label);

    /**
     * Helper method to take an arraylist and iterate through it and combine elements. Used
     * collection as it is more general.
     * @param list that we want to put into one string (any collection that holds strings)
     * @return one string that is comma delimited between LIST elements
     */
    static String convertListToString(Collection<String> list) {
        StringBuilder sBuilder = new StringBuilder();
        //add a period to denote the beginning
        sBuilder.append(".");
        for (String url : list) {
            sBuilder.append(url);
            sBuilder.append(DELIMITER);
        }
        return sBuilder.toString();
    }

    /**
     * helper method that takes a string delimited by DELIMITER and then returns an ArrayList
     * @param lst - the string that represents a an arraylist
     * @param delimiter - the char that delimits between each element in LST
     * @return an arraylist
     */
    static ArrayList<String> convertStringToList(String lst, String delimiter) {
        ArrayList<String> arraylst = new ArrayList<>();
        int i = 1;
        int position;
        String element;
        while (i < lst.length()){
            position = lst.indexOf(delimiter, i);
            Log.d(TAG, "convertStringToList: " + i + " : " + position + "length: " + lst.length());
            element = lst.substring(i, position);
            arraylst.add(element);
            i += (position+1);
        }
        return arraylst;
    }

    /**
     *
     * @param youtubeKey - the string that is the youtube key for the complete URL
     *                   ex.)key = 'abc' full youtube url is ->
     *                   https://www.youtube.com/watch?v=abc
     * @return
     */
    static String youtubeViaKey(String youtubeKey) {
        return YOUTUBE_BASE+youtubeKey;
    }

    static String getRawJSON(String path) {
        System.out.println(path);
        HttpURLConnection urlConnection;
        URL url;
        BufferedReader reader;
        StringBuilder str = new StringBuilder();
        try {
            url = new URL(path);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream input = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
            String line;
            while ((line = reader.readLine()) != null) {
                str.append(line);
            }
        } catch (MalformedURLException e) {
            Log.d(TAG, "getRawJSON: Malformed URL");
        } catch (IOException e) {
            Log.d(TAG, "getRawJSON: Connection error - either request method or open connection: "
                    + path);
        }
        if (str.length() == 0) {
            Log.d(TAG, "getRawJSON: No JSON received");
        }
        return str.toString();
    }



    /**
     *
     * @return URI builder with the scheme and authority set to SCHEME and AUTHORITY static vars
     */
    static Uri.Builder buildBase() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME);
        builder.authority(AUTHORITY);
        builder.appendPath(MOVIEDB_V3);
        builder.appendQueryParameter(API_KEY, KEY_V3);
        return builder;
    }

    /**
     * Calls on the base builder first then adds on the more specific path
     * @return a URI Builder, with the base corresponding to the NowPlaying web request
     */
    static Uri.Builder getNowPlayingURL() {
        Uri.Builder builder = buildBase();
        builder.appendPath(MOVIE);
        builder.appendPath(NOW_PLAYING);
        return builder;
    }

    /**
     * Calls on the base builder first then adds on the more specific path, upcoming defined as
     * movies with release dates from tomorrow to up to a month in the future
     * @return a URI Builder, with the base corresponding to the Upcoming web request
     */
    static Uri.Builder getUpcomingURL() {
        Uri.Builder builder = buildBase();
        builder.appendPath(MOVIE);
        builder.appendPath(UPCOMING);
        return builder;
    }

    /**
     * Calls on the base builder first then adds on the more specific path
     * @return a URI Builder, with the base corresponding to the TopRated web request
     */
    static Uri.Builder getTopRatedURL() {
        Uri.Builder builder = buildBase();
        builder.appendPath(MOVIE);
        builder.appendPath(TOP_RATED);
        return builder;
    }

    /**
     * Calls on the base builder first then adds on the more specific path
     * @return a URI Builder, with the base corresponding to the Popular web request
     */
    static Uri.Builder getPopularURL() {
        Uri.Builder builder = buildBase();
        builder.appendPath(MOVIE);
        builder.appendPath(POPULAR);
        return builder;
    }


    /**
     * Used to build the base URL for retrieving a movie poster image. Size of the image is just
     * determined by the class variable IMAGE_SIZE.
     * @return a URI Builder that has the base URL for a poster built already
     */
    static  Uri.Builder getPosterBaseURL(String size) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME);
        builder.authority(IMAGE_BASE);
        builder.appendPath("t");
        builder.appendPath("p");
        builder.appendPath(size);
        return builder;
    }

    /**
     * Method that builds the base URL for when you want to request the API for movie specific
     * details
     * @return a builder
     */
    static Uri.Builder getMovieSpecificBase() {
        Uri.Builder builder = buildBase();
        builder.appendPath(INDIVIDUAL_MOVIE);
        return builder;
    }

    /**
     * helper that returns a builder that is the complete URL for obtaining info
     * for videos related to MOVIEDBID
     * @param movieDBid - the movieDBid that we want to find videos for
     * @return builder URL
     */
    static Uri.Builder getMovieVideoURL(String movieDBid) {
        Uri.Builder videoURL = getMovieSpecificBase();
        videoURL.appendPath(movieDBid);
        videoURL.appendPath(VIDEOS);
        return videoURL;
    }

    /**
     *
     * @param movieDBid - the movieDBid that we want to find videos for
     * @return a URI Builder of the complete URL for getting reviews related to MOVIEDBID
     */
    static Uri.Builder getMovieReviewURL(String movieDBid) {
        Uri.Builder reviewsURL = getMovieSpecificBase();
        reviewsURL.appendPath(movieDBid);
        reviewsURL.appendPath(REVIEWS);
        return reviewsURL;
    }



    /**NEEDS work
     * Calls on the base builder first then adds on the more specific path
     * @return a URI Builder, with the base corresponding to the search movie web request
     */
    static Uri.Builder getSearchMovieURL() {
        Uri.Builder builder = buildBase();
        builder.appendPath(SEARCH);
        builder.appendPath(MOVIE);
        return builder;
    }

    /** NEEDS work - need to search for person, get their person ID then search movie details
     * using this person ID (int)
     * Calls on the base builder first then adds on the more specific path
     * @return a URI Builder, with the base corresponding to the search by actor web request
     */
    static Uri.Builder getSeachActorURL() {
        Uri.Builder builder = buildBase();
        builder.appendPath(PERSON);
        return builder;
    }


    /**
     * Inserts a query key, query value pair into QUERIES hashmap for later reference
     */
    static void insertQuery(String query_key, String query_value) {
        queries.put(query_key, query_value);
    }

    /**
     * Takes in a builder (that already has some base URL) then adds a query parameter (ie. like
     * API key or release dates boundaries)
     * @param builder - the Uri.Builder to add on the query to
     * @param  query_key - the queryKey to add onto the URL.
     * @return a URI Builder, with now query indicator added on to it
     */
    static Uri.Builder setQuery(Uri.Builder builder, String query_key) {
        builder.appendQueryParameter(query_key, queries.get(query_key));
        return builder;
    }

    /**
     *
     * @param date - the string repr of the date we want to make into a gregorian calendar object
     * @param format - the date format used in the string DATE
     * @return a gregorian calendar object that represents DATE
     */
    static GregorianCalendar stringToGregorian(String date, String format) {
        GregorianCalendar cal = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            cal.setTime(sdf.parse(date));
        } catch (ParseException e) {
            Log.d(TAG, "stringToGregorian: error trying to parse string date");
        }
        return cal;
    }

    /**
     * Little helper function that gets the date yyyy-mm-dd. Calendar's getInstance() method gets
     * a calendar object related to the system settings of the phone. The get() method of Calendar
     * gets the value of the calendar field (which is the parameter of get()). This is an int
     * and there are a bunch of int constants in the Calendar class where you can get different
     * values from the calendar object (like the year, day of the month, month etc..)
     * @param i - {0,1} - is zero gets tomorrow's date, if 1 gets date one month in the future,
     *          if any other number gets today's date
     * @return
     */
    static String get_Date(int i) {
        Calendar c = Calendar.getInstance();
        if (i == 0) {
            //this takes the calendar object C and then increments it by a day correctly, as in
            //takes into account the different # of days in each month etc..
            c.add(Calendar.DAY_OF_MONTH, 1);
        }
        if (i == 1) {
            //this takes the calendar object C and then increments it by a day correctly, as in
            //takes into account the different # of days in each month etc..NOTE:Calendar objects
            //the constant MONTH is like computer indexing, January has value 0 and get() methods
            //of calendar objects return this int value (note corrresponding
            // conventional date number)
            c.add(Calendar.MONTH, 1);
        }
        Date date = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        System.out.println(sdf.format(date));
        return sdf.format(date);
    }

    /**
     * helper function to make it easier to put all relevant info from a movie object into a
     * content values object to be then placed into a table
     * @param movie - object that we want its details to be put into a content Value
     * @return a content values object with all relevant data about the movie to be put into DB
     */
    static ContentValues bulk_insert_movie_values(Movie movie) {
        ContentValues values = new ContentValues();
        values.put(movieContract.TopRated.OVERVIEW, movie.getOverview());
        values.put(movieContract.TopRated.ORIG_TITLE, movie.getTitle());
        values.put(movieContract.TopRated.EN_TITLE, movie.getEnTitle());
        values.put(movieContract.TopRated.MOVIE_DB_ID, movie.getMovieDBid());
        values.put(movieContract.TopRated.REL_DATE, movie.getStringDate());
        values.put(movieContract.TopRated.POSTER_URL, movie.getPosterURL());
        values.put(movieContract.TopRated.BIG_POSTER_URL, movie.getBigPosterURL());
        values.put(movieContract.TopRated.VOTE, movie.getVoteAvg());
        values.put(movieContract.TopRated.REVIEW, convertListToString(movie.getReviewUrls()));
        values.put(movieContract.TopRated.TRAILER_YOUTUBE_KEY
                , convertListToString(movie.getYoutubeKeys()));
        return values;
    }

    /**
     * creates a movie object with all of its attributes filled from DATA's columns
     * @param data cursor object that refers to the movie in the DB
     * @return a movie object with all relevant attributes found in the table filled out
     */
    static Movie create_movie_from_cursor(Cursor data) {
        int columnIndex = data.getColumnIndex(movieContract.FavoriteEntries.ORIG_TITLE);
        Movie movie = new Movie();
        movie.setTitle(data.getString(columnIndex));
        columnIndex = data.getColumnIndex(movieContract.FavoriteEntries.BIG_POSTER_URL);
        movie.setBigPosterURL(data.getString(columnIndex));
        columnIndex = data.getColumnIndex(movieContract.FavoriteEntries.POSTER_URL);
        movie.setPosterURL(data.getString(columnIndex));
        columnIndex = data.getColumnIndex(movieContract.FavoriteEntries.EN_TITLE);
        movie.setEnTitle(data.getString(columnIndex));
        columnIndex = data.getColumnIndex(movieContract.FavoriteEntries.MOVIE_DB_ID);
        movie.setMovieDBid(data.getInt(columnIndex));
        columnIndex = data.getColumnIndex(movieContract.FavoriteEntries.OVERVIEW);
        movie.setOverview(data.getString(columnIndex));
        columnIndex = data.getColumnIndex(movieContract.FavoriteEntries.REL_DATE);
        GregorianCalendar cal = stringToGregorian(data.getString(columnIndex), DATE_FORMAT);
        movie.setReleaseDate(cal);
        columnIndex = data.getColumnIndex(movieContract.FavoriteEntries.VOTE);
        movie.setVoteAvg(data.getDouble(columnIndex));
        columnIndex = data.getColumnIndex(movieContract.FavoriteEntries.REVIEW);
        ArrayList<String> reviews = convertStringToList(data.getString(columnIndex), DELIMITER);
        movie.setReviewUrlList(reviews);
        columnIndex = data.getColumnIndex(movieContract.FavoriteEntries.TRAILER_YOUTUBE_KEY);
        ArrayList<String> youtube_keys = convertStringToList(data.getString(columnIndex)
                , DELIMITER);
        movie.setYoutubeUrlList(youtube_keys);
        return movie;
    }

    /**
     * helper method that returns an onCLickListener to be associated with the given argument BUTTON
     * Need this as we are dynamically creating buttons and each needs a different URL destination
     * (which is the argument STRURI) when they send an intent. Note have to make the arguments
     * final to allow the inner anonymous class be able to refer to each given arugment (a Java
     * thing - think it is because stated it is final doesn't allow that variable to be
     * reassigned to anything else thus inner classes are safe to refer to it as when compiled
     * the variable binding won't potentially switch on them if a coder reassigns variable causing
     * some odd run time errors)
     * @param button - the button to which we want to set this onClickListener to.
     * @param strUri - the destination in which the button will take you
     * @param context - the context which we need to in order to know from which context to launch
     *                the new activity and call a couple CONTEXT methods
     * @return an OnClickListener
     */
    static View.OnClickListener getClickListener(final Button button, final String strUri
            , final Context context) {
        final Uri uri = Uri.parse(strUri);
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this is an implicit intent where the action is we want to view something and the
                //destination is the URI
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                //check if the phone has any app that can handle this intent
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                } else {
                    Log.d(TAG, "onClick: no app can handle this intent" + button.getText());
                }
            }
        };
    }

    /**
     * helper function that creates an OnClickListener for the favorite button. Creates different
     * function depending on whether or not this movie is already in the favorites table or not.
     * @param button - the fav button that we will be setting this onClickListener to
     * @param flag - int to tell us if want this button to have insert or delete functionality
     *             0 : insert (movie not in favorites)
     *             1: delete (movie is already in favorites)
     * @param movie - the specific movie that we want this button to act on
     * @param context - need reference to the context to interact with DB and also intiate an intent
     *                if we successfully delete a movie from favorites to back to main favorites
     *                page if we are on the favorites page
     * @return an OnClickListener
     */
    static View.OnClickListener getFavListener(final Button button, int flag, final Movie movie
            , final Context context) {
        final String[] columns = {movieContract.FavoriteEntries.MOVIE_DB_ID};
        final String select = movieContract.FavoriteEntries.MOVIE_DB_ID + "= ?";
        final String[] selectionArgs = {String.valueOf(movie.getMovieDBid())};
        Log.d(TAG, "getFavListener: " + movie.getMovieDBid());
        final Uri favTable = movieContract
                .buildMovieUri(movieContract.FavoriteEntries.TABLE_NAME);
        switch(flag)

        {
            case 0:
                return new View.OnClickListener() {
                    Cursor cursor;
                    @Override
                    public void onClick(View v) {
                        ContentValues movieValues;
                        //puts all relevant attributes to go into DB table in a content values
                        movieValues = Utilities.bulk_insert_movie_values(movie);
                        context.getContentResolver().insert(favTable, movieValues);
                        cursor = context.getContentResolver().query(favTable, columns, select
                                , selectionArgs, null);
                        if (cursor.moveToFirst()) {
                            Toast.makeText(context, movie.getTitle()
                                    + " successfully added to favorites", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "error when trying to add " + movie.getTitle()
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                };

            case 1:
                return new View.OnClickListener() {
                    int rowsDeleted;
                    @Override
                    public void onClick(View v) {
                        rowsDeleted = context.getContentResolver().
                                delete(favTable, select, selectionArgs);
                        if (rowsDeleted == 1) {
                            Toast.makeText(context, movie.getTitle()
                                    + " successfully deleted from favorites", Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            Toast.makeText(context, movie.getTitle()
                                    + " error when deleting from favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
            default:
                Log.d(TAG, "getFavListener: invalid case");
                return null;
        }
    }
}
