package com.harrison.movies.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.HashSet;

import static android.content.ContentValues.TAG;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

//this import is needed in order to get a Context instance to run tests. This replaces the
//AndroidTestCase...now can just call getContext()

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class testProvider {
    String TITLE = "Test Movie: Test";
    String OVERVIEW = "This is a test overview";
    String POSTER_URL = "/poster";
    String DATE = "1/1/2017";
    Double VOTE = 7.5;
    String BIG_POSTER= "/big_poster";
    String EN_TITLE = "Test Movie: Test - English";
    String YOUTUBE = "youtubeKey";
    String REVIEW = "https://someReviewURL.com";
    Integer MOVIEDB_ID = 88888;
    Uri testTopRatedDetail = movieContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(movieContract.TopRated.TABLE_NAME)
            .appendPath(MOVIEDB_ID.toString()).build();
    Uri testUpcomingDetail = movieContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(movieContract.Upcoming.TABLE_NAME)
            .appendPath(MOVIEDB_ID.toString()).build();
    Uri testMostPopularDetail = movieContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(movieContract.MostPopular.TABLE_NAME)
            .appendPath(MOVIEDB_ID.toString()).build();
    Uri testNowPlayingDetail = movieContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(movieContract.NowPlaying.TABLE_NAME)
            .appendPath(MOVIEDB_ID.toString()).build();
    Uri testFavDetail = movieContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(movieContract.FavoriteEntries.TABLE_NAME)
            .appendPath(MOVIEDB_ID.toString()).build();

    Uri testTopRated = movieContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(movieContract.TopRated.TABLE_NAME).build();
    Uri testUpcoming = movieContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(movieContract.Upcoming.TABLE_NAME).build();
    Uri testMostPopular = movieContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(movieContract.MostPopular.TABLE_NAME).build();
    Uri testNowPlaying = movieContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(movieContract.NowPlaying.TABLE_NAME).build();
    Uri testFav = movieContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(movieContract.FavoriteEntries.TABLE_NAME).build();
    String[] names = {movieContract.TopRated.TABLE_NAME
            ,movieContract.MostPopular.TABLE_NAME, movieContract.Upcoming.TABLE_NAME
            ,movieContract.NowPlaying.TABLE_NAME, movieContract.FavoriteEntries.TABLE_NAME};
    movieDBHelper oneHelper = new movieDBHelper(getTargetContext());


    @Test
    public void testGetType(){
        //note: we do not directly instantiate the content provider. The content provder is created
        //by the app when the app decides to create the content provider (it knows to do this
        //as we have registered the provider in the AndroidManifest file. Then there is one global
        //instance of ContentResolver that manages all providers - so you call its method
        // (and it runs its own resolver version of the method) and then
        //it parses the parameters and then sends it to the approriate providers and then the
        //provider calls its own version of the method that was called and run by content resolver
        String cursorType;
        cursorType = getTargetContext().getContentResolver().getType(testTopRatedDetail);
        assertTrue("top rated detail uri type incorrect"
                , cursorType.equals(ContentResolver.CURSOR_ITEM_BASE_TYPE));

        cursorType = getTargetContext().getContentResolver().getType(testMostPopularDetail);
        assertTrue("most popular detail uri type incorrect"
                , cursorType.equals(ContentResolver.CURSOR_ITEM_BASE_TYPE));

        cursorType = getTargetContext().getContentResolver().getType(testNowPlayingDetail);
        assertTrue("now playing detail uri type incorrect"
                , cursorType.equals(ContentResolver.CURSOR_ITEM_BASE_TYPE));

        cursorType = getTargetContext().getContentResolver().getType(testUpcomingDetail);
        assertTrue("upcoming detail uri type incorrect"
                , cursorType.equals(ContentResolver.CURSOR_ITEM_BASE_TYPE));

        cursorType = getTargetContext().getContentResolver().getType(testFavDetail);
        assertTrue("favorite detail uri type incorrect"
                , cursorType.equals(ContentResolver.CURSOR_ITEM_BASE_TYPE));

        //
        cursorType = getTargetContext().getContentResolver().getType(testTopRated);
        assertTrue("top rated uri type incorrect"
                , cursorType.equals(ContentResolver.CURSOR_DIR_BASE_TYPE));

        cursorType = getTargetContext().getContentResolver().getType(testMostPopular);
        assertTrue("most popular uri type incorrect"
                , cursorType.equals(ContentResolver.CURSOR_DIR_BASE_TYPE));

        cursorType = getTargetContext().getContentResolver().getType(testNowPlaying);
        assertTrue("now playing uri type incorrect"
                , cursorType.equals(ContentResolver.CURSOR_DIR_BASE_TYPE));

        cursorType = getTargetContext().getContentResolver().getType(testUpcoming);
        assertTrue("upcoming uri type incorrect"
                , cursorType.equals(ContentResolver.CURSOR_DIR_BASE_TYPE));

        cursorType = getTargetContext().getContentResolver().getType(testFav);
        assertTrue("favorite uri type incorrect"
                , cursorType.equals(ContentResolver.CURSOR_DIR_BASE_TYPE));
    }

//    @Test
    public void testUpdate(){
        ContentValues values = new ContentValues();
        ContentValues orig_values = new ContentValues();
        orig_values = popTestValues(orig_values);
        getTargetContext().deleteDatabase(movieDBHelper.DATABASE_NAME);
        movieDBHelper helper = new movieDBHelper(getTargetContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        values = popTestValues(values);
        values.put(movieContract.TopRated.EN_TITLE, "new updated title");
        for (String name: names) {
            Uri test_insert = movieContract.buildMovieUri(name);
            getTargetContext().getContentResolver().insert(test_insert, orig_values);
        }
        int rowsUpdated = 0;
        for (String name: names) {
            Uri test_uri = movieContract.buildMovieUri(name);
            rowsUpdated = getTargetContext().getContentResolver().update(test_uri, values, null
                    , null);
            assertTrue("row not updated", rowsUpdated == 1);
        }
        helper.close();
    }

//    @Test
    public void testQuery(){
        ContentValues values = new ContentValues();
        getTargetContext().deleteDatabase(movieDBHelper.DATABASE_NAME);
        movieDBHelper helper = new movieDBHelper(getTargetContext());
        values = popTestValues(values);
        HashMap<String, Uri> tests = new HashMap<>();
        //populate the tables
        Cursor cursor = null;
        for (String name : names) {
            Uri test_uri = movieContract.buildMovieUri(name);
            getTargetContext().getContentResolver().insert(test_uri, values);
            tests.put(name, test_uri);
        }
        //query with no filters
        for (String name : names) {
            cursor = getTargetContext().getContentResolver().query(tests.get(name), null
                    , null, null, null);
            assertTrue("Incorrect query", cursor.moveToFirst());
            assertTrue("Incorrect size somehow", cursor.getCount() == 1);
        }
        //query with fewer columns
        HashSet<String> columns = new HashSet<>();
        columns.add(movieContract.TopRated.TRAILER_YOUTUBE_KEY);
        columns.add(movieContract.TopRated.EN_TITLE);
        columns.add(movieContract.TopRated.MOVIE_DB_ID);
        String[] arrayColumns = {movieContract.TopRated.TRAILER_YOUTUBE_KEY
                , movieContract.TopRated.EN_TITLE, movieContract.TopRated.MOVIE_DB_ID};
        for (String name: names) {
            cursor = getTargetContext().getContentResolver().query(tests.get(name)
                    , arrayColumns, null, null, null);
            assertTrue("Didn't return a row", cursor.moveToFirst());
            assertTrue("Columns not filtered, returned" + cursor.getColumnCount() + " columns"
                    , cursor.getColumnCount() == 3);
            for (String column : cursor.getColumnNames()) {
                assertTrue("Didn't get the right columns", column.contains(column));
            }

        }
        //query with filter returns no rows
        String selection = movieContract.TopRated.EN_TITLE + "= ?";
        String[] selectionargs = {"jibberish"};
        for (String name: names) {
            cursor = getTargetContext().getContentResolver().query(tests.get(name), null ,selection
                    , selectionargs, null);
            assertFalse("Shouldn't have any rows returned", cursor.moveToFirst());
        }
        //query filter on vote value
        selection = movieContract.TopRated.VOTE + " > ?";
        String[] selectionarg1 = {"9"};
        for (String name: names) {
            cursor = getTargetContext().getContentResolver().query(tests.get(name), null ,selection
                    , selectionarg1, null);
            assertFalse("Shouldn't have any rows returned", cursor.moveToFirst());
        }

        //query filter on vote value
        selection = movieContract.TopRated.VOTE + " > ?";
        String[] selectionarg2 = {"5"};
        for (String name: names) {
            cursor = getTargetContext().getContentResolver().query(tests.get(name), null ,selection
                    , selectionarg2, null);
            assertTrue("Should return rows", cursor.moveToFirst());
        }
        helper.close();

    }

    @Test
    public void testDelete(){
        ContentValues values = new ContentValues();
        getTargetContext().deleteDatabase(movieDBHelper.DATABASE_NAME);
        SQLiteDatabase db = oneHelper.getWritableDatabase();
        values = popTestValues(values);
        HashMap<String, Uri> tests = new HashMap<>();
        //populate the tables
        Cursor cursor = null;
        for (String name : names) {
            Uri test_uri = movieContract.buildMovieUri(name);
            getTargetContext().getContentResolver().insert(test_uri, values);
        }
        //test a complete table delete
        for (String name : names) {
            Uri test_uri = movieContract.buildMovieUri(name);
            int rowsDeleted = getTargetContext().getContentResolver().delete(test_uri, null, null);
            assertTrue("no rows deleted", rowsDeleted == 1);
            cursor = db.query(name, null, null, null, null, null, null);
            assertFalse("Table still not empty", cursor.moveToFirst());
        }
        //reinsert data
        for (String name : names) {
            Uri test_uri = movieContract.buildMovieUri(name);
            getTargetContext().getContentResolver().insert(test_uri, values);
        }
        //delete on a column filter - should be deleted
        String selection = movieContract.TopRated.BIG_POSTER_URL + "= ?";
        String[] selectionarg = {BIG_POSTER};
        for (String name : names) {
            Uri test_uri = movieContract.buildMovieUri(name);
            int rowsDeleted = getTargetContext().getContentResolver().delete(test_uri
                    , selection, selectionarg);
            assertTrue("no rows deleted", rowsDeleted == 1);
            cursor = db.query(name, null, null, null, null, null, null);
            assertFalse("Table still not empty", cursor.moveToFirst());
        }
        //reinsert data
        for (String name : names) {
            Uri test_uri = movieContract.buildMovieUri(name);
            getTargetContext().getContentResolver().insert(test_uri, values);
        }
        //delete on a column filter - should not be deleted
        selection = movieContract.TopRated.BIG_POSTER_URL + "= ?";
        selectionarg[0] = "skaldjkl";
        for (String name : names) {
            Uri test_uri = movieContract.buildMovieUri(name);
            int rowsDeleted = getTargetContext().getContentResolver().delete(test_uri
                    , selection, selectionarg);
            assertTrue("no rows deleted", rowsDeleted == 0);
            cursor = db.query(name, null, null, null, null, null, null);
            assertTrue("Table is wrongly empty", cursor.moveToFirst());
        }

    }

//    @Test
    public void testInsert(){
        ContentValues values = new ContentValues();
        getTargetContext().deleteDatabase(movieDBHelper.DATABASE_NAME);
        SQLiteDatabase db = oneHelper.getWritableDatabase();
        values = popTestValues(values);
        Log.d(TAG, "testInsert: " + values.size());

        for (String name : names) {
            int rowsdeleted = db.delete(name, null, null);
            Log.d(TAG, "testInsert: rows deleted " + rowsdeleted );
            Uri test_uri = movieContract.buildMovieUri(name);
            Cursor cursor2 = getTargetContext().getContentResolver().query(test_uri, null, null
                    , null, null);
            if (cursor2.moveToFirst()) {
                Log.d(TAG, "testInsert: " + cursor2.getColumnNames());
                Log.d(TAG, "testInsert: " + cursor2.getString(1));
                Log.d(TAG, "testInsert: " + cursor2.getCount());
            }
            Uri blah = getTargetContext().getContentResolver().insert(test_uri, values);
            Cursor cursor = db.query(name, null, null, null, null
                    , null, null);
            assertTrue(blah+"query error as cursor is empty: "+name, cursor.moveToFirst());
            assertTrue(name +" table size incorrect ", cursor.getCount() == 1);
            int column;
            int columnType;
            String value;
            int idValue;
            double voteValue;
            for (String columnName : cursor.getColumnNames()) {
                column = cursor.getColumnIndex(columnName);
                columnType = cursor.getType(column);
                if (columnName.equals("_id")) {
                    continue;
                }
                switch (columnType) {
                    case Cursor.FIELD_TYPE_INTEGER:
                        idValue = cursor.getInt(column);
                        assertTrue("incorrect movie ID: should be: " + MOVIEDB_ID + ", is: " + idValue
                                , idValue == MOVIEDB_ID);
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        value = cursor.getString(column);
                        assertTrue("incorrect " + columnName + " value"
                                , value.equals(values.get(columnName)));
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        voteValue = cursor.getDouble(column);
                        assertTrue("incorrect " + columnName + " value"
                                , voteValue == VOTE);
                        break;
                }
            }
        }

    }


    /**
     * helper function that populates TESTVALUES with values to put in our content provider
     */
    public ContentValues popTestValues(ContentValues testValues) {
        testValues.put(movieContract.TopRated.ORIG_TITLE, TITLE);
        testValues.put(movieContract.TopRated.OVERVIEW, OVERVIEW);
        testValues.put(movieContract.TopRated.POSTER_URL, POSTER_URL);
        testValues.put(movieContract.TopRated.REL_DATE, DATE);
        testValues.put(movieContract.TopRated.VOTE, VOTE);
        testValues.put(movieContract.TopRated.BIG_POSTER_URL, BIG_POSTER);
        testValues.put(movieContract.TopRated.EN_TITLE, EN_TITLE);
        testValues.put(movieContract.TopRated.TRAILER_YOUTUBE_KEY, YOUTUBE);
        testValues.put(movieContract.TopRated.REVIEW, REVIEW);
        testValues.put(movieContract.TopRated.MOVIE_DB_ID, MOVIEDB_ID);
        return testValues;
    }

//    /**
//     * helper method that inserts data into provider to prevent redundant code
//     * @return the long of the row id of the newly inserted data
//     */
//    public void insertTestData(Uri uri, ContentValues values ) {
//        getTargetContext().getContentResolver().insert(uri, values);
//    }

}
