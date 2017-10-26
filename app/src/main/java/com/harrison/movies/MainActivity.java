package com.harrison.movies;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.harrison.movies.data.movieContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;

/**
 * Main Activity where the gridView is activated and hosted. Doesn't launch a fragment as I didn't
 * think it necessary as there really is only one view ever in the main Actiity. Implements a
 * shared preferences listener as want to refresh the gridview when go into settings from the
 * homepage.
 *
 * Note: that getSupportLoaderManager() works with the import statements:
 *
 import android.support.v4.app.LoaderManager;
 import android.support.v4.content.CursorLoader;
 import android.support.v4.content.Loader;

 And if I use getLoaderManager() then just use
 import android.app.LoaderManager;
 import android.content.CursorLoader;
 import android.content.Loader;
 *
 * Checks if there is network connection if none, then doesn't populate gridView and just shows
 * a simple textview saying no network connection.
 *
 *
 */

public class MainActivity extends AppCompatActivity implements AsyncListener
        , LoaderManager.LoaderCallbacks<Cursor>{

    //Hashmap that maps the values of the list preferences to the strings required for the
    //corresponding movieDB web request
    HashMap<Integer, String> list = new HashMap<>();
    //hashmap that holds the titlebar titles to be shown in different searches
    HashMap<String, String> titles = new HashMap<>();

    //arraylist that contains Movie objects that holds details of each movie
    ArrayList<Movie> movies;
    ImageArrayAdapter adapter;
    ImageListAdapter favAdapter;
    ArrayList<String> noConnection;
    Parcelable gridState;
    GridView grid;
    Uri mUri;
    int CURSOR_ID = 0;
    private static Context mContext;

    //old oncreate method before using cursorloaders
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.i(TAG, "onCreate: Main Created");
//        setContentView(R.layout.posters);
//        //putting the key value pairs of the 4 list preferences in a hashmap so i can link their
//        //string array values (seen in array.xml) to their corresponding strings required for a
//        //proper corresponding movieDB request
//        popMaps();
//        movies = new ArrayList<>();
//        if (!isOnline()) {
//            noConnection = new ArrayList<>();
//            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
//            setContentView(R.layout.no_connection);
//        } else {
//            popMovies();
//            grid = (GridView) this.findViewById(R.id.posters);
//            grid.setAdapter(adapter);
//            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                            @Override
//                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                //just a toast to make sure we the click and details are working
//                Toast.makeText(MainActivity.this, movies.get(i).getTitle()
//                        ,Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
//                //note our bundle contains the whole arraylist and the position within arraylist
//                //or specific movie clicked
//                Bundle bundle = new Bundle();
//                bundle = popBundle(bundle, i);
//                intent.putExtras(bundle);
//                if (bundle.isEmpty() || bundle.containsKey(getString(R.string.bundle_movies))
//                        || bundle.containsKey(getString(R.string.click_position))) {
//                    Log.d(TAG, "onCreate (MainActivity): Bundle being sent to Detail Activity " +
//                            "is either empty or missing an element");
//                }
//                startActivity(intent);
//            }
//        }
//            );
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.posters);
        mContext = this;
        popMaps();
        grid = (GridView) this.findViewById(R.id.posters);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String selectedPref = pref.getString(getString(R.string.options), "0");
        String titleSearch = pref.getString(getString(R.string.search_title),
                getString(R.string.default_value));
        String actorSearch = pref.getString(getString(R.string.search_actor),
                getString(R.string.default_value));
//        this gets the table_name for the desired list preference selected
        String webRequest = list.get(Integer.parseInt(selectedPref));
//        this sets the title of the action bar to corresponding title
        getSupportActionBar().setTitle(titles.get(webRequest));
        //declaring an item click listener class right here. Method's argument is a onItem click
        //listener object, and just declaring the whole class object within the argument for ease

        if (titleSearch != getString(R.string.default_value)) {
//            call helper function to retrieve movies based on a movie title search. Add the string
//            search as logic for a MovieDB object is if the doinBackground() method has params > 1
//            then it assumes it is an editText search and adding a string String will gurantee
//            that this will be true
            popMovieGrid(getString(R.string.movie_api) + " " + titleSearch, 1);
        } else if (actorSearch != getString(R.string.default_value)) {
            popMovieGrid(getString(R.string.person_api) + " " + actorSearch, 1);
        }
//      checking if not editText preference then check if not favorites set a gridView listener
//        to launch the details activity (note: we set up a gridView listener on onLoadFinished
//        if it is favorites.
        else if (selectedPref != "4") {
            popMovieGrid(selectedPref, 0);
        }
    }

    /**
     * method to first check for network connection before trying to execute an AsyncTask so we can
     * gracefully handle lack of network connection when refreshing the homepage. Putting a method
     * in each activity.
     * @return
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

    /**
     * Method from my custom interface to help update adapter once AsyncTask is completed to avoid
     * blank screen load, this method is called via the onPostExecute method in the MOVIEDB
     * asyncTask which gurantees it gets called when the asyncTask has completed and ready to
     * serve up its data
     */
    @Override
    public void onTaskCompletion() {
        adapter = new ImageArrayAdapter(this, movies);
        grid.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Method to be overriden that actually creates the loader with a reference to a cursor that
     * retrieves data. We return a loader that has been created to a Cursor with a specific
     * query, this query is built by taking the value in the sharedPreferences and then using
     * that to query the associated DB table. We also save the instance variable mUri to that
     * specific URI value so that we can refer to it and pass it into our AsyncTask to populate
     * the DB directly (don't have to populate DB on main thread)
     * @param id - id of the particular cursor loader we are loading. Each loader has a locally
     *           unique ID
     * @param args
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String favorites = movieContract.FavoriteEntries.TABLE_NAME;
        //build URI to use as an argument to initialize cursor loader to the desired table
        mUri = movieContract.buildMovieUri(favorites);
        Log.d(TAG, "onCreateLoader: " + mUri);
        return new CursorLoader(this, mUri, null, null, null, null);
    }

    /**
     * Method that needs to be overridden as this MainActivity is a loader - so when a loader
     * instance has finished loading its data this method get called automatically. Since i'm only
     * using a cursor for the favorites table - we just grab w/e data in the favorites table. The
     * one check we do is if the cursor points to no data (ie favorites table is empty) then we
     * set the view to say no favorites made yet so user knows it is not broken
     * @param loader - would be this instance object
     * @param data - the data that was retrieved and now we need to do something with it
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            setContentView(R.layout.no_favorites);
        } else {
            favAdapter = new ImageListAdapter(this, data
                    , CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            grid.setAdapter(favAdapter);
            final Cursor data2 = data;
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                data2.moveToPosition(i);
                                                int title_column = data2.getColumnIndex(movieContract.FavoriteEntries.ORIG_TITLE);
                                                Toast.makeText(MainActivity.this, data2.getString(title_column), Toast.LENGTH_SHORT)
                                                        .show();
                                                Bundle bundle;
                                                bundle = popBundleFromCursor(data2);
                                                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                                                intent.putExtras(bundle);
                                                if (bundle.isEmpty() || !bundle.containsKey(getString(R.string.bundle_movies))
                                                        || !bundle.containsKey(getString(R.string.click_position))) {
                                                    Log.d(TAG, "onItemClick: Bundle being sent to detailed activity is "
                                                            + "either emtpy or missing an item");
                                                    Log.d(TAG, "onItemClick: movies key: " + getString(R.string.bundle_movies) + " click key: " + getString(R.string.click_position));
                                                }
                                                startActivity(intent);
                                            }
                                        }
            );
        }
    }

    /**
     * Method required to be overriden - once the Cursor that holds the data in this loader is
     * about to be closed we usually swap the loader to a null reference so its not taking up
     * memory and referring to a cursor with no valid data anymore. Note: swapCursor method is
     * inherited from the base class CURSORADAPTER that my custom adapter extends
     * @param loader - would be this class
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        favAdapter.swapCursor(null);
    }

    /**
     * Helper function that populates the movie grid if using the movieDB api (vs populating from
     * local mysql database)
     * @param input - the string input used to help populate the movie grid
     * @param type - type of data to populate the grid with. '0' - denotes it is populating the
     *             grid using pre defined lists like topRated vs. '1' denotes populating grid via
     *             search (actor or movie title)
     */
    private void popMovieGrid (String input, int type) {
        if (type == 0) {
            //only initilized the cursorLoader if preference is to favorites
            if (input.equals(getString(R.string.fav_pref_value))) {
                getLoaderManager().initLoader(CURSOR_ID, null, this);
            } else if (!isOnline()) {
                Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
                setContentView(R.layout.no_connection);
            } else {
                popMovies(input);
            }
        } else if (type == 1) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = pref.edit();
            int startSearchIndex = input.indexOf(' ');
            String searchType = input.substring(0, startSearchIndex);
            if (searchType.equals(getString(R.string.person_api))) {
                editor.putString(getString(R.string.search_actor), getString(R.string.default_value));
            } else if (searchType.equals(getString(R.string.movie_api))) {
                editor.putString(getString(R.string.search_title), getString(R.string.default_value));
            }
            MovieDB searchTask = new MovieDB(this, this, mUri);
            try {
                ArrayList<Movie> searchedMovies = searchTask.execute(input).get();
            } catch (InterruptedException e) {
                Log.d(TAG, "popMoviesSearch: asyncTask failed");
            } catch (ExecutionException e) {
                Log.d(TAG, "popMoviesSearch: asyncTask failed");
            }
        }
//        set the onItem listeners the same way no matter if using pre set list or search
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            Toast.makeText(MainActivity.this, movies.get(i).getTitle(), Toast.LENGTH_SHORT)
                                                    .show();
                                            Bundle bundle = new Bundle();
                                            bundle = popBundle(bundle, i);
                                            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                                            intent.putExtras(bundle);
                                            if (bundle.isEmpty() || !bundle.containsKey(getString(R.string.bundle_movies))
                                                    || !bundle.containsKey(getString(R.string.click_position))) {
                                                Log.d(TAG, "onItemClick: Bundle being sent to detailed activity is "
                                                        + "either emtpy or missing an item");
                                                Log.d(TAG, "onItemClick: movies key: " + getString(R.string.bundle_movies) + " click key: " + getString(R.string.click_position));
                                            }
                                            startActivity(intent);
                                        }
                                    }
        );
    }


    /**
     * Method that populates the adapter with the requested movies
     */
    private void popMovies(String selectedPref) {
        //get the value of the list preference currently selected. See ARRAY.XML for the 3 options
        //if there is no value then i set default to 0 which is the Now Playing Option
        //Note: need to do getString then parseInt because seems that android shared List
        //preferences (maybe all Sharedpreferences?) come out as Strings even if the array type
        //is declared as integer-array in the xml file where the preferences xml grabs its values
        //from
        int prefValue = Integer.parseInt(selectedPref);
        //gets the web request equivalent to be used to access the movieDB API
        String listPref = list.get(prefValue);
        fetchData(listPref, mUri);

        //sets the text seen in the 'action bar' in this activity to the string in TITLES
    }

    //old fetch data method when we weren't using cursors etc..
//    /**
//     * helper method called on my popMovies and popMOviesSearch to take which ever preference
//     * and then use that to do a movieDb webrequest and then receive the rawJson response to
//     * populate the adapter that gridview uses and the poster url list as well as the movie details
//     * array
//     * @param pref - the preference that we want to sort the gridView by
//     * @return - the JSON string to be used to then populate POSTERS and MOVIEDETAILS
//     */
//    private void fetchData(String pref) {
//        MovieDB movie = new MovieDB(this);
//        //the execute take String as parameter which tells it which preference it wants to order /
//        //how to populate the posters grid
//        try {
//            String JSONresponse = movie.execute(pref).get();
//            movies = movie.getMovieDetails(JSONresponse);
//            adapter = new ImageListAdapter(this, movies);
//        } catch (InterruptedException e) {
//            Log.d(TAG, "popMovies: Something wrong with Async task interrupted exception");
//        } catch (ExecutionException e) {
//            Log.d(TAG, "popMovies: Something wrong with Async task execution exception");
//        }
//    }

    /**
     * new fetch data that takes into that we now are making use of a DB and cursor adapter. the
     * AsyncTask does the work in another thread and then in populates the DB with the data it
     * gets from the web
     * @param pref - the selected preference on how we'd like to view our gridView
     * @param uri - the uri to which we want to insert our web requested data
     */
    private void fetchData(String pref, Uri uri) {
//        note: don't actually need to feed in the URI into the MovieDB task anymore (that was with
//        older version
        MovieDB movieTask = new MovieDB(this, this, uri);
        try {
            movies = movieTask.execute(pref).get();
        } catch (InterruptedException e) {
            Log.d(TAG, "fetchData: error interrupted exception");
        } catch (ExecutionException e) {
            Log.d(TAG, "fetchData: error execution exeception");
        }
    }

    /**
     * helper method that just populates the 3 hashmaps used in this class
     */
    private void popMaps() {
        list.put(0, Utilities.NOW_PLAYING);
        list.put(1, Utilities.TOP_RATED);
        list.put(2, Utilities.UPCOMING);
        list.put(3, Utilities.POPULAR);
        list.put(4, movieContract.FavoriteEntries.TABLE_NAME);
        titles.put(Utilities.NOW_PLAYING, getString(R.string.np));
        titles.put(Utilities.TOP_RATED, getString(R.string.tr));
        titles.put(Utilities.UPCOMING, getString(R.string.up));
        titles.put(Utilities.POPULAR, getString(R.string.pop));
        titles.put(movieContract.FavoriteEntries.TABLE_NAME, getString(R.string.fav));
    }

    /**
     * Helper method to populate the bundle put inside intents
     * @param  - POSITION is the int position of the movie clicked in gridView
     * @param  - BUNDLE is the bundle to be populated
     * @return - returns a bundle object with it populated with a movie object
     */
    private Bundle popBundle(Bundle bundle, int position) {
        bundle.putParcelable(getString(R.string.single_movie_bundle), movies.get(position));
        return bundle;
    }

    /**
     * Helper method to populate a bundle to be sent to the detail activity with info about the
     * Movie selected in the favorites gridView. Since favorites runs on a cursor had to make it
     * slightly different than POPBUNDLE as that relies on the MOVIES arrayList being populated
     * @param data - the cursor (at the location of the click) that holds the data
     * @return a bundle with a movie object with all necessary attributes
     */
    private Bundle popBundleFromCursor(Cursor data) {
        Movie movie = Utilities.create_movie_from_cursor(data);
        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.single_movie_bundle), movie);
        return bundle;
    }

    /**
     * Helper method that converts the list preference selection to the corresponding DB table
     * @param prefIndex - the value of the corresponding list preference. Look at 'preferences.xml'
     *                  . 'array.xml'file for which preferences align with which numbers
     *                  (remember these are always strings)
     * @return a string that is the table name that we need to query
     */
    private String getTableFromListPref(Integer prefIndex) {
        switch (prefIndex) {
            case 0:
                return movieContract.NowPlaying.TABLE_NAME;
            case 1:
                return movieContract.TopRated.TABLE_NAME;
            case 2:
                return movieContract.Upcoming.TABLE_NAME;
            case 3:
                return movieContract.MostPopular.TABLE_NAME;
            case 4:
                return movieContract.FavoriteEntries.TABLE_NAME;
            default:
                throw new UnsupportedOperationException("List preference not valid");
        }
    }

    //helper method to be used so that non app / context classes can access local resources
    //by retrieving a context instance to call method like getResources() on
    public static Context getContext() {
        return mContext;
    }



}
