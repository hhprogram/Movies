package com.harrison.movies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

import static android.content.ContentValues.TAG;
import static com.harrison.movies.Utilities.API_KEY;
import static com.harrison.movies.Utilities.BEGIN_DATE;
import static com.harrison.movies.Utilities.BIG_IMAGE;
import static com.harrison.movies.Utilities.END_DATE;
import static com.harrison.movies.Utilities.IMAGE_SIZE;
import static com.harrison.movies.Utilities.KEY_V3;
import static com.harrison.movies.Utilities.MOVIE_EN_TITLE;
import static com.harrison.movies.Utilities.MOVIE_TITLE;
import static com.harrison.movies.Utilities.NOW_PLAYING;
import static com.harrison.movies.Utilities.POPULAR;
import static com.harrison.movies.Utilities.POSTER_KEY;
import static com.harrison.movies.Utilities.RESULTS;
import static com.harrison.movies.Utilities.TOP_RATED;
import static com.harrison.movies.Utilities.UPCOMING;
import static com.harrison.movies.Utilities.VOTE_AVG;
import static com.harrison.movies.Utilities.getNowPlayingURL;
import static com.harrison.movies.Utilities.getPopularURL;
import static com.harrison.movies.Utilities.getPosterBaseURL;
import static com.harrison.movies.Utilities.getRawJSON;
import static com.harrison.movies.Utilities.getTopRatedURL;
import static com.harrison.movies.Utilities.getUpcomingURL;
import static com.harrison.movies.Utilities.get_Date;


/**
 * Created by harrison on 12/22/16. Class that handles all API calls to TMDB (the movie DB)
 * Remember for an AsyncTask<Paramter type of AsyncTask input, return type for output when
 * returning progress udpates, return type of the AsyncTask> For us input will be String (what kind
 * of API query we are trying to do), no progress updates, return type is ArrayList of movies
 * This class does all the processing of the web request and parsing JSON to keep as little work
 * as possible off the main UI thread
 * Also contains methods for:
 *         Generating URLs for certain types of webrequests (DONE)
 *         Retrieving a list of movie poster URLs to be used to populate ImageListAdapter (DONE)
 *         Methods that actually connect to movieDB and returns JSONresponse to be used (DONE)
 *         Retrieving movie details associated with the list of movie poster URLs and keeping them
 *            ready in order to load data for when a movie poster image is clicked (TBD)
 */

public class MovieDB extends AsyncTask<String, Void, ArrayList<Movie>>{


    //forgot to initialize this to an actual object so queries requiring query parameters were
    //crashing because I was trying to execute a get method on this in one of the class methods
    //but had a null pointer reference error
    HashMap<String, String> queries = new HashMap<>();
    //LISTENER object is MainActivity as it implements AsyncListener
    private AsyncListener _listener;
    final Context mContext;
    private Uri mUri;

    /**
     * New constructor that needs the context that launched this AsyncTask so it can be used to
     * get its content resolver and interact with the DB through the registered content provider
     * @param listener - this LISTENER object is the activity that creates this AsyncTask object
     *                 and then needs it to notify is when an AsyncTask instance is done
     *                 executing and the LISTENER needs to immediately refresh data (ie the
     *                 MainActivity and updating the gridView with all of the movies)
     * @param context - the context so we can get the CONTENT RESOLVER and then interact with the
     *                provider. Want to be able to insert in an AsyncTask for better fluid UI
     * @param uri - the uri where we want to insert the retrieved web data into
     */
    public MovieDB(AsyncListener listener, Context context, Uri uri) {
        //create a hashmap, used to refer to build the full web request URL with query keys and
        //values. Puts the API_KEY and preset begin and end dates to be used for queries that
        //require dates
        queries.put(API_KEY, KEY_V3);
        String today = get_Date(0);
        queries.put(BEGIN_DATE, today);
        String monthInFuture = get_Date(1);
        queries.put(END_DATE, monthInFuture);
        _listener = listener;
        mContext = context;
        mUri = uri;
    }

    /**
     * Old constructor that doesn't need a reference to the context
     * @param listener - this LISTENER object is the activity that creates this AsyncTask object
     *                 and then needs it to notify is when an AsyncTask instance is done
     *                 executing and the LISTENER needs to immediately refresh data (ie the
     *                 MainActivity and updating the gridView with all of the movies)
     */
    public MovieDB(AsyncListener listener) {
        //create a hashmap, used to refer to build the full web request URL with query keys and
        //values. Puts the API_KEY and preset begin and end dates to be used for queries that
        //require dates
        queries.put(API_KEY, KEY_V3);
        String today = get_Date(0);
        queries.put(BEGIN_DATE, today);
        String monthInFuture = get_Date(1);
        queries.put(END_DATE, monthInFuture);
        _listener = listener;
        mContext = null;
    }

    //old doInbackground where I just returned the rawJson to then be parsed later for data
//    /**TO DO: NEED to implement the editText possibilities
//     * Method takes PARAMS and sends a web request. Making it able to take in multiple strings
//     * as that's how i'll discern if it's an edit text preference or a list preference
//     * @param params - parameter that is supposed to be the type of filtering/ordering selected
//     *               in the preferences window
//     * @return a String which is the rawJson response of the specific movieDB web request
//     */
//    @Override
//    protected String doInBackground(String... params) {
//        String path, response;
//        Uri.Builder builder = null;
//        if (params.length > 1) {
//            //this will be the EditText string
//        } else {
//            //each option just builds the URL specific to the web request
//            String option = params[0];
//            if (option == NOW_PLAYING) {
//                builder = getNowPlayingURL();
//                path = builder.toString();
//                response = getRawJSON(path);
//                return response;
//            } else if (option == UPCOMING) {
//                //note the query parameters don't actually do anything - incorrectly read the
//                //movieDB API - upcoming takes no release date parameters, always a canned report,
//                //can filter for region only
//                builder = getUpcomingURL();
//                builder = setQuery(builder, BEGIN_DATE);
//                builder = setQuery(builder, END_DATE);
//                path = builder.toString();
//                System.out.println(path);
//                response = getRawJSON(path);
//                return response;
//            } else if (option == TOP_RATED) {
//                builder = getTopRatedURL();
//                path = builder.toString();
//                response = getRawJSON(path);
//                return response;
//            } else if (option == POPULAR) {
//                builder = getPopularURL();
//                path = builder.toString();
//                response = getRawJSON(path);
//                return response;
//            } else {
//                Log.d(TAG, "doInBackground: Invalid List Option");
//                return "";
//            }
//        }
//        Log.d(TAG, "doInBackground: Somehow missed all cases - not correct");
//        return "";
//    }

    /**
     *
     * @param params - if pararms is length 1 then it is just the asyncTask call to populate the
     *               main grid view
     *                  (if when we parse string it is a number then it is assumed to be a movieDB
     *                   id in which we want the YOUTUBE and REVIEW data on that movie, if not
     *                  then it is assumed to be a string for movieDB web request)
     *               - later will implement if length > 1 then it is assumed to be custom search
     *
     * @return
     */
    @Override
    protected ArrayList<Movie> doInBackground(String... params) {
        String path, response;
        Uri.Builder builder;
        HashMap<String, ArrayList<String>> videosAndReviews;
        if (params.length > 1) {
            Log.d(TAG, "doInBackground: params length > 1");
            //this will be the EditText string
            return null;
        } else {
            String option = params[0];
            if (option == NOW_PLAYING) {
                builder = getNowPlayingURL();
                path = builder.toString();
                response = getRawJSON(path);
            } else if (option == UPCOMING) {
                //note the query parameters don't actually do anything - incorrectly read the
                //movieDB API - upcoming takes no release date parameters, always a canned report,
                //can filter for region only
                builder = getUpcomingURL();
//                builder = Utilities.setQuery(builder, BEGIN_DATE);
//                builder = Utilities.setQuery(builder, END_DATE);
                path = builder.toString();
                response = getRawJSON(path);
            } else if (option == TOP_RATED) {
                builder = getTopRatedURL();
                path = builder.toString();
                response = getRawJSON(path);
            } else if (option == POPULAR) {
                builder = getPopularURL();
                path = builder.toString();
                response = getRawJSON(path);
            } else {
                Log.d(TAG, "doInBackground: Invalid List Option");
                return null;
            }
            //            rowsInserted = popDBwithJSON(response);
            System.out.println(path);
            return getMovieDetails(response);
            }
        }


    //Note: The object type in the onPostExecute() method is the same object type of the AsyncTask's
    //return type which in this case is a string
    @Override
    public void onPostExecute(ArrayList<Movie> result) {
        _listener.onTaskCompletion();
    }

    /**
     * Connects to MovieDB and receives the get request JSON response
     * @param path - the URL that we want to connect to and  send a GET request to
     * @return a string that is the raw JSON response
     */


    /**
     * helper method that takes a string that is a relative path. Note, I use a string builder to
     * append the string of poster because the relative path contains "\/" therefore the Uri.Builder
     * gets confused and doesn't add the correct relative path. But when I get the string of
     * the relative path POSTER from the JSON it correctly gets the relative path to the image
     * therefore I just append it like a normal string
     * @param poster - relative path
     * @return absolute path related to the given relative path
     */
    private String getPath(String poster) {
        Uri.Builder builder = getPosterBaseURL(IMAGE_SIZE);
        StringBuilder url = new StringBuilder(builder.toString());
        url.append(poster);
        return url.toString();
    }

    /**
     * Helper function to obtain the number of movies in a JSON response from movieDB.
     * @param rawJSON - the JSON input in which I want to determine the number of movies contained
     *                in this resposne
     * @return the number of movies contained in the JSON response. Returns -1 if there was an error
     */
    private int getNumMovies(String rawJSON) {
        try {
            JSONObject json = new JSONObject(rawJSON);
            JSONArray results = json.getJSONArray(RESULTS);
            return results.length();
        } catch (JSONException e) {
            Log.d(TAG, "getNumMovies: Invalid rawJSON");
            return -1;
        }
    }

    /**DEPRECATED as now getMovieDetails creates an ArrayList of Movie objects that also includes
     * poster URLS and the ImageListAdapter will work with Movie Objects.
     *
     * Method to execute the task of populating a list of absolute poster paths associate with the
     * JSONresponse contained in the parameter RAWJSON (calls helper method to make each element
     * in arraylist an absolute path)
     * @param rawJSON - the JSONresponse related to whatever query we are interested in
     * @return a string arrayList with absolute paths to each movie poster in same order as the JSON
     * list
     */
//    public ArrayList<String> getMoviePosters(String rawJSON) {
//        ArrayList<String> moviePosters = new ArrayList<>();
//        try {
//            JSONObject json = new JSONObject(rawJSON);
//            JSONArray results = json.getJSONArray(RESULTS);
//            for (int i = 0; i < results.length(); i++) {
//                JSONObject movie = results.getJSONObject(i);
//                String poster = movie.getString(POSTER_KEY);
//                poster = getPath(poster);
//                moviePosters.add(poster);
//                System.out.println(poster);
//            }
//        } catch (JSONException e) {
//            Log.d(TAG, "getMoviePosters: Invalid rawJSON or array key");
//        }
//        System.out.println(moviePosters.size());
//        return moviePosters;
//    }

    /**
     * gets all movie details that can be retrieved via preset API calls like :
     * https://api.themoviedb.org/3/movie/now_playing, https://api.themoviedb.org/3/movie/upcoming
     * etc..
     * calls another method that retrieves the corresponding youtube key and review URL for
     * each movie as we need to do a seperate API call and need to make an API call by each
     * specifc movie ID
     * Method that takes in a JSON string response from a certain movieDB web request, then parses
     * it to extract the Title, release date, overview and vote average from the JSON response. The
     * order of the arraylist is the order of how it is shown in the gridView. Returns an empty
     * arraylist if the jsonresponse returns no movies
     * @param jsonResponse - the JSON response from a type of movieDB web query
     * @return an ArrayList of movie objects with all attributes assigned
     */
    public ArrayList<Movie> getMovieDetails(String jsonResponse) {
        ArrayList<Movie> movieDetails = new ArrayList<>();
        StringBuilder posterURL, bigPosterURL;
        try {
            JSONObject object = new JSONObject(jsonResponse);
            JSONArray movies = object.getJSONArray(RESULTS);
            if (movies == null) {
                return movieDetails;
            }
            for (int i = 0; i < movies.length(); i++) {
                //this loop goes through each results in the RESULTS array and then creates
                //a movie object with the desired details and then puts them all into an arraylist
                JSONObject movieInfo = movies.getJSONObject(i);
                Movie movie = new Movie();
                movie.setOverview(movieInfo.getString(Utilities.OVERVIEW));
                movie.setTitle(movieInfo.getString(MOVIE_TITLE));
                Uri.Builder builder = getPosterBaseURL(IMAGE_SIZE);
                //using string builder here because the string associated with POSTER_KEY is already
                //a proper complete relative path so if use appendPath method from Uri.Builder class
                //then adds special characters which messes it up. Also, I do two posterURLs
                //as one is for the smaller home page grid view and the other is for the bigger
                //detailed view
                posterURL = new StringBuilder(builder.toString());
                posterURL.append(movieInfo.getString(POSTER_KEY));
                movie.setPosterURL(posterURL.toString());
                Uri.Builder builder1 = getPosterBaseURL(BIG_IMAGE);
                bigPosterURL = new StringBuilder(builder1.toString());
                bigPosterURL.append(movieInfo.getString(POSTER_KEY));
                movie.setBigPosterURL(bigPosterURL.toString());
                GregorianCalendar cal = Utilities.stringToGregorian(movieInfo
                        .getString(Utilities.RELEASE), Utilities.DATE_FORMAT);
                movie.setReleaseDate(cal);
                movie.setVoteAvg(movieInfo.getDouble(VOTE_AVG));
                movie.setEnTitle(movieInfo.getString(MOVIE_EN_TITLE));
                movie.setMovieDBid(movieInfo.getInt(Utilities.ID));
                movieDetails.add(movie);
            }
        } catch (JSONException e) {
            Log.d(TAG, "getMovieDetails: invalid JSON in MovieDB method");
        }
        //commented this out as since to get review and trailer details requires 1 request to the
        //API per movie and there is a limit of 40 in a short interval of time. Therefore, changed
        //to only get youtube and review details when user clicks on movie(will lighten load of
        //just loading the gridview anyways
//        movieDetails = addYoutubeAndReview(movieDetails);
        return movieDetails;
        
    }



    //old method when I was trying to get all movies youtube and review info.
//    /**
//     * helper method called by getMovieDetails to add the youtube keys and review urls to each
//     * movie object. grabs all of the Youtube keys and review urls
//     * @param movies - the arraylist that has movies already partially completed by getMovieDetails
//     * @return updated ArrayList that should have the final ArrayList that getMovieDetails will
//     * be returning
//     */
//    private ArrayList<Movie> addYoutubeAndReview(ArrayList<Movie> movies) {
//        JSONArray reviews = null;
//        for (Movie movie : movies) {
//            Uri.Builder builder = buildBase();
//            Uri.Builder builder2 = buildBase();
//            try {
//                builder.appendPath(INDIVIDUAL_MOVIE);
//                builder.appendPath(String.valueOf(movie.getMovieDBid()));
//                builder.appendPath(VIDEOS);
//                String rawJson = getRawJSON(builder.toString());
//                JSONObject obj = new JSONObject(rawJson);
//                JSONArray trailers = obj.getJSONArray(RESULTS);
//                //loop through all availabe youtube trailer keys and add to the Movie object
//                for (int i = 0; i < trailers.length(); i++) {
//                    JSONObject trailer = trailers.getJSONObject(i);
//                    movie.addYoutubeKey(trailer.getString(YOUTUBE_KEY));
//                }
//                //then go find the review URLs and do the same thing with these
//                builder2.appendPath(INDIVIDUAL_MOVIE);
//                builder2.appendPath(String.valueOf(movie.getMovieDBid()));
//                builder2.appendPath(REVIEWS);
//                rawJson = getRawJSON(builder2.toString());
//                obj = new JSONObject(rawJson);
//                reviews = obj.getJSONArray(RESULTS);
//                for (int i = 0; i < reviews.length(); i++) {
//                    obj = reviews.getJSONObject(i);
//                    movie.addReviewUrl(obj.getString(REVIEW_URL));
//                }
//
//            } catch (JSONException e) {
//                Log.d(TAG, "addYoutubeAndReview: somethings wrong with " + movie.getTitle()
//                        + " results in review has length: " + reviews.length());
//            }
//        }
//        return movies;
//    }


    //commented out - a good practice - but don't need to put web requested data into a data
    //persistent DB
//    /**
//     * Method that puts the JSON response from the movieDB into the DB so that we can use the
//     * database and cursors etc. Leverages already created GETMOVIEDETAILS method
//     * to get an arraylist of movie objects, then iterates through each of these movie objects to
//     * make a arraylist full of content values in which we then bulkinsert into the content provider
//     *
//     * Note: delete all rows in the corresponding table before inserting. Wasn't doing this before,
//     * and my table just kept growing after each run
//     * @param rawJson
//     */
//    public Integer popDBwithJSON(String rawJson) {
//        ArrayList<Movie> movies;
//        movies = getMovieDetails(rawJson);
//        //arraylist that will hold the contentvalues to be used to actually populate the DB
//        ArrayList<ContentValues> movieValues = new ArrayList<>();
//        for (Movie movie : movies) {
//            //each content value for each movie
//            ContentValues values = new ContentValues();
//            //call utility function to be able to put all URLS int he DB
//            String reviewsForDB = Utilities.convertListToString(movie.getReviewUrls());
//            //make the YOUTUBE urls complete
//            ArrayList<String> fullYoutubeUrls = new ArrayList<>();
//            String fullYoutube;
//            for (String key : movie.getYoutubeKeys()) {
//                fullYoutube = Utilities.youtubeViaKey(key);
//                fullYoutubeUrls.add(fullYoutube);
//            }
//            String youtubeForDB = Utilities.convertListToString(fullYoutubeUrls);
//            values.put(movieContract.TopRated.EN_TITLE, movie.getEnTitle());
//            values.put(movieContract.TopRated.MOVIE_DB_ID, movie.getMovieDBid());
//            values.put(movieContract.TopRated.VOTE, movie.getVoteAvg());
//            values.put(movieContract.TopRated.REVIEW, reviewsForDB);
//            values.put(movieContract.TopRated.TRAILER_YOUTUBE_KEY, youtubeForDB);
//            values.put(movieContract.TopRated.ORIG_TITLE, movie.getTitle());
//            values.put(movieContract.TopRated.POSTER_URL, movie.getPosterURL());
//            values.put(movieContract.TopRated.BIG_POSTER_URL, movie.getBigPosterURL());
//            values.put(movieContract.TopRated.REL_DATE, movie.getStringDate());
//            values.put(movieContract.TopRated.OVERVIEW, movie.getOverview());
//            movieValues.add(values);
//        }
//        ContentValues[] contentArray = new ContentValues[movieValues.size()];
//        Log.d(TAG, "popDBwithJSON: size of movieValues arraylist" + movieValues.size());
//        contentArray = movieValues.toArray(contentArray);
//        Integer rowsInserted = mContext.getContentResolver().bulkInsert(mUri, contentArray);
//        return rowsInserted;
//    }





}
