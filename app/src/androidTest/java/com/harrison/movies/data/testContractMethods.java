package com.harrison.movies.data;

import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;


/**
 * Instrumentation test, which will execute on an Android device. Tests the functionality of the
 * build and get URI methods of MovieContract directly. Need to make sure these pass and then
 * can test if provider works
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class testContractMethods {
    String testGridView_TR_URI = "content://com.harrison.movies/"
            + movieContract.TopRated.TABLE_NAME;
    String testGridView_UP_URI = "content://com.harrison.movies/"
            + movieContract.Upcoming.TABLE_NAME;
    String testGridView_MP_URI = "content://com.harrison.movies/"
            + movieContract.MostPopular.TABLE_NAME;
    String testGridView_FAV_URI = "content://com.harrison.movies/"
            + movieContract.FavoriteEntries.TABLE_NAME;
    String testGridView_NP_URI = "content://com.harrison.movies/"
            + movieContract.NowPlaying.TABLE_NAME;
    Integer testMovieID = 100;
    String testDetail_TR_URI = "content://com.harrison.movies/"
            + movieContract.TopRated.TABLE_NAME+"/"+testMovieID;
    String testDetail_UP_URI = "content://com.harrison.movies/"
            + movieContract.Upcoming.TABLE_NAME+"/"+testMovieID;
    String testDetail_MP_URI = "content://com.harrison.movies/"
            + movieContract.MostPopular.TABLE_NAME+"/"+testMovieID;
    String testDetail_FAV_URI = "content://com.harrison.movies/"
            + movieContract.FavoriteEntries.TABLE_NAME+"/"+testMovieID;
    String testDetail_NP_URI = "content://com.harrison.movies/"
            + movieContract.NowPlaying.TABLE_NAME+"/"+testMovieID;

    @Test
    public void testContractURIBuilderGridView() {
        String tr_URI = movieContract.buildMovieUri(movieContract.TopRated.TABLE_NAME).toString();
        assertTrue("URI for top rated built incorrectly", tr_URI.equals(testGridView_TR_URI));

        String UP_URI = movieContract.buildMovieUri(movieContract.Upcoming.TABLE_NAME).toString();
        assertTrue("URI for upcoming built incorrectly", UP_URI.equals(testGridView_UP_URI));

        String MP_URI = movieContract.buildMovieUri(movieContract.MostPopular.TABLE_NAME)
                .toString();
        assertTrue("URI for most popular built incorrectly", MP_URI.equals(testGridView_MP_URI));

        String FAV_URI = movieContract.buildMovieUri(movieContract.FavoriteEntries.TABLE_NAME)
                .toString();
        assertTrue("URI for favorites built incorrectly", FAV_URI.equals(testGridView_FAV_URI));

        String NP_URI = movieContract.buildMovieUri(movieContract.NowPlaying.TABLE_NAME).toString();
        assertTrue("URI for now playing built incorrectly", NP_URI.equals(testGridView_NP_URI));
    }

    @Test
    public void testContractURIGetGridView() {
        Uri uri = movieContract.BASE_CONTENT_URI.buildUpon()
                .appendPath(movieContract.TopRated.TABLE_NAME).build();
        String table = movieContract.getTableUri(uri);
        assertTrue("URI for top rated built incorrectly"
                , table.equals(movieContract.TopRated.TABLE_NAME));

        uri = movieContract.BASE_CONTENT_URI.buildUpon()
                .appendPath(movieContract.Upcoming.TABLE_NAME).build();
        table = movieContract.getTableUri(uri);
        assertTrue("URI for upcoming built incorrectly"
                , table.equals(movieContract.Upcoming.TABLE_NAME));

        uri = movieContract.BASE_CONTENT_URI.buildUpon()
                .appendPath(movieContract.MostPopular.TABLE_NAME).build();
        table = movieContract.getTableUri(uri);
        assertTrue("URI for most popular built incorrectly"
                , table.equals(movieContract.MostPopular.TABLE_NAME));

        uri = movieContract.BASE_CONTENT_URI.buildUpon()
                .appendPath(movieContract.FavoriteEntries.TABLE_NAME).build();
        table = movieContract.getTableUri(uri);
        assertTrue("URI for favorites built incorrectly"
                , table.equals(movieContract.FavoriteEntries.TABLE_NAME));

        uri = movieContract.BASE_CONTENT_URI.buildUpon()
                .appendPath(movieContract.NowPlaying.TABLE_NAME).build();
        table = movieContract.getTableUri(uri);
        assertTrue("URI for now playing built incorrectly"
                , table.equals(movieContract.NowPlaying.TABLE_NAME));
    }

    @Test
    public void testContractURIBuilderDetail() {
        String tr_URI = movieContract.buildMovieUri(movieContract.TopRated.TABLE_NAME, testMovieID)
                .toString();
        assertTrue("URI for top rated built incorrectly", tr_URI.equals(testDetail_TR_URI));

        String UP_URI = movieContract.buildMovieUri(movieContract.Upcoming.TABLE_NAME, testMovieID)
                .toString();
        assertTrue("URI for upcoming built incorrectly", UP_URI.equals(testDetail_UP_URI));

        String MP_URI = movieContract.buildMovieUri(movieContract.MostPopular.TABLE_NAME
                , testMovieID).toString();
        assertTrue("URI for most popular built incorrectly", MP_URI.equals(testDetail_MP_URI));

        String FAV_URI = movieContract.buildMovieUri(movieContract.FavoriteEntries.TABLE_NAME
                , testMovieID).toString();
        assertTrue("URI for favorites built incorrectly", FAV_URI.equals(testDetail_FAV_URI));

        String NP_URI = movieContract.buildMovieUri(movieContract.NowPlaying.TABLE_NAME
                , testMovieID).toString();
        assertTrue("URI for now playing built incorrectly", NP_URI.equals(testDetail_NP_URI));
    }

    @Test
    public void testContractURIGetDetail() {
        String idString = testMovieID.toString();
        Uri uri = movieContract.BASE_CONTENT_URI.buildUpon()
                .appendPath(movieContract.TopRated.TABLE_NAME).appendPath(idString)
                .build();
        int id = movieContract.getMovieUri(uri);
        assertTrue("URI for top rated built incorrectly", id == testMovieID);

        uri = movieContract.BASE_CONTENT_URI.buildUpon()
                .appendPath(movieContract.Upcoming.TABLE_NAME).appendPath(idString).build();
        id = movieContract.getMovieUri(uri);
        assertTrue("URI for upcoming built incorrectly", id == testMovieID);

        uri = movieContract.BASE_CONTENT_URI.buildUpon()
                .appendPath(movieContract.MostPopular.TABLE_NAME).appendPath(idString).build();
        id = movieContract.getMovieUri(uri);
        assertTrue("URI for most popular built incorrectly", id == testMovieID);

        uri = movieContract.BASE_CONTENT_URI.buildUpon()
                .appendPath(movieContract.FavoriteEntries.TABLE_NAME).appendPath(idString).build();
        id = movieContract.getMovieUri(uri);
        assertTrue("URI for favorites built incorrectly", id == testMovieID);

        uri = movieContract.BASE_CONTENT_URI.buildUpon()
                .appendPath(movieContract.NowPlaying.TABLE_NAME).appendPath(idString).build();
        id = movieContract.getMovieUri(uri);
        assertTrue("URI for now playing built incorrectly", id == testMovieID);
    }

}
