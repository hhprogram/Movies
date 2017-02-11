package com.harrison.movies;

/**
 * Created by harrison on 1/8/17.
 * simple custom interface to make it easier to update my main homepage view when the AsyncTask is
 * completed (ie relevant data to populate the homepage is available). Before - it would show a
 * blank screen if network connection is spotty / slow so setting the adapter happens before the
 * asynctask is done and therefore the adapter is empty
 */

public interface AsyncListener {
    public void onTaskCompletion();
}
