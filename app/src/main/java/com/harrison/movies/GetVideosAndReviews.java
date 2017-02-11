package com.harrison.movies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;
import static com.harrison.movies.Utilities.RESULTS;
import static com.harrison.movies.Utilities.getMovieReviewURL;
import static com.harrison.movies.Utilities.getMovieVideoURL;
import static com.harrison.movies.Utilities.getRawJSON;

/**
 * Created by harrison on 2/3/17.
 */

public class GetVideosAndReviews extends AsyncTask<String, Void
        , HashMap<String, ArrayList<String>>> {
    AsyncListener _listener;

    public GetVideosAndReviews(AsyncListener listener) {
        _listener = listener;
    }

    @Override
    protected HashMap<String, ArrayList<String>> doInBackground(String... params) {
        String movieDBid = params[0];
        HashMap<String, ArrayList<String>> videosAndReviews = new HashMap<>();
        try {
            Integer.parseInt(movieDBid);
            videosAndReviews = addYoutubeAndReview(movieDBid);
        } catch (NumberFormatException e) {
            Log.d(TAG, "doInBackground (GetVid Async): not a movieID");
        }
        return videosAndReviews;
    }

    //call the listener's on task completion method so that it'll refresh the data once the
    //asynctask completes
    @Override
    public void onPostExecute(HashMap<String, ArrayList<String>> results) {
        _listener.onTaskCompletion();
    }


    /**
     *
     * @param movieID
     * @return a hashmap where the key denotes if the value is an arrayList of reviews or youtube
     *          trailer urls
     */
    private HashMap<String, ArrayList<String>> addYoutubeAndReview(String movieID) {
        Uri.Builder videoURL = getMovieVideoURL(movieID);
        Uri.Builder reviewURL = getMovieReviewURL(movieID);
        String videoRawJson = getRawJSON(videoURL.toString());
        String reviewRawJson = getRawJSON(reviewURL.toString());
        HashMap<String, ArrayList<String>> videosAndReviews = new HashMap<>();
        ArrayList<String> lst = new ArrayList<>();
        //lst that corresponds to each value and is the NAME of each trailer
        ArrayList<String> lst2 = new ArrayList<>();
        try {
            JSONObject videoObj = new JSONObject(videoRawJson);
            JSONArray videoResults = videoObj.getJSONArray(RESULTS);
            //loops through results, grabbing the string value for KEY
            for (int i = 0; i < videoResults.length(); i++) {
                JSONObject specificVideo = videoResults.getJSONObject(i);
                String completeURL = Utilities.youtubeViaKey(specificVideo
                        .getString(Utilities.YOUTUBE_KEY));
                lst.add(completeURL);
                lst2.add(specificVideo.getString(Utilities.TRAILER_NAME));
            }
            //actual URLS
            videosAndReviews.put(Movie.YOUTUBE_KEY, lst);
            //the labels of these URLS
            videosAndReviews.put(Utilities.TRAILER_NAME, lst2);

        } catch (JSONException e) {
            Log.d(TAG, "addYoutubeAndReview: video json object error");
        }
        lst = new ArrayList<String>();
        //list that corresponds with the lst of URLs of the reviews. Fill it with the "AUTHOR"
        //value provided by movieDB API. (Used mostly for button label)
        lst2 = new ArrayList<>();
        try {
            JSONObject reviewObj = new JSONObject(reviewRawJson);
            JSONArray reviewResults = reviewObj.getJSONArray(RESULTS);
            for (int i = 0; i < reviewResults.length(); i++) {
                JSONObject specificReview = reviewResults.getJSONObject(i);
                lst.add(specificReview.getString(Utilities.REVIEW_URL));
                lst2.add(specificReview.getString(Utilities.REVIEW_AUTHOR));
            }
            //actual URLs
            videosAndReviews.put(Movie.REVIEW_KEY, lst);
            //URL labels
            videosAndReviews.put(Utilities.REVIEW_AUTHOR, lst2);
        } catch (JSONException e) {
            Log.d(TAG, "addYoutubeAndReview: review json object error");
        }
        return videosAndReviews;
    }
}
