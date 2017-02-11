package com.harrison.movies.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by harrison on 1/16/17.
 */

public class movieProvider extends ContentProvider{

    private static UriMatcher sUriMatcher = buildUriMatcher();
    //int code for URI that wants to access the TopRated database
    static final int TOP_RATED = 100;
    //int code for URI that wants to access the most popular database
    static final int MOST_POPULAR = 101;
    static final int UPCOMING = 102;
    static final int NOW_PLAYING = 103;
    //int code for URI that wants to access the Favorites database
    static final int FAVORITE = 105;
    static final int TOP_RATED_DETAIL = 200;
    static final int MOST_POPULAR_DETAIL = 201;
    static final int UPCOMING_DETAIL = 202;
    static final int NOW_PLAYING_DETAIL = 203;
    static final int FAVORITE_DETAIL = 204;
    private movieDBHelper mDBhelper;

    //When we create a content provider we need to create the associated DBhelper instance object
    //so we can interact with the underlying DB and make sure the table actually is created first
    //if this is the first time creating it. Use this helper instance to get instance objects
    //of the database so we can actually interact with it via query, insert etc...
    @Override
    public boolean onCreate() {
        mDBhelper = new movieDBHelper(getContext());
        return true;
    }

    static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(movieContract.CONTENT_AUTHORITY, movieContract.TopRated.TABLE_NAME
                , TOP_RATED);
        matcher.addURI(movieContract.CONTENT_AUTHORITY, movieContract.FavoriteEntries.TABLE_NAME
                , FAVORITE);
        matcher.addURI(movieContract.CONTENT_AUTHORITY, movieContract.MostPopular.TABLE_NAME
                , MOST_POPULAR);
        matcher.addURI(movieContract.CONTENT_AUTHORITY, movieContract.Upcoming.TABLE_NAME
                , UPCOMING);
        matcher.addURI(movieContract.CONTENT_AUTHORITY, movieContract.NowPlaying.TABLE_NAME
                , NOW_PLAYING);

        matcher.addURI(movieContract.CONTENT_AUTHORITY, movieContract.TopRated.TABLE_NAME + "/#"
                , TOP_RATED_DETAIL);
        matcher.addURI(movieContract.CONTENT_AUTHORITY
                , movieContract.FavoriteEntries.TABLE_NAME + "/#", FAVORITE_DETAIL);
        matcher.addURI(movieContract.CONTENT_AUTHORITY, movieContract.MostPopular.TABLE_NAME + "/#"
                , MOST_POPULAR_DETAIL);
        matcher.addURI(movieContract.CONTENT_AUTHORITY, movieContract.Upcoming.TABLE_NAME + "/#"
                , UPCOMING_DETAIL);
        matcher.addURI(movieContract.CONTENT_AUTHORITY, movieContract.NowPlaying.TABLE_NAME + "/#"
                , NOW_PLAYING_DETAIL);
        return matcher;
    }

    /**
     *
     * @param uri - the URI whose type we want to return
     * @return - the String that denotes the MIME type of the URI, whether it is a directory type
     * (ie could have 0 or infinite rows) vs. a single row. Used to determine the cursor to be
     * returned when used in turn with Cursors
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TOP_RATED:
                return ContentResolver.CURSOR_DIR_BASE_TYPE;
            case MOST_POPULAR:
                return ContentResolver.CURSOR_DIR_BASE_TYPE;
            case FAVORITE:
                return ContentResolver.CURSOR_DIR_BASE_TYPE;
            case UPCOMING:
                return ContentResolver.CURSOR_DIR_BASE_TYPE;
            case NOW_PLAYING:
                return ContentResolver.CURSOR_DIR_BASE_TYPE;
            case TOP_RATED_DETAIL:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE;
            case MOST_POPULAR_DETAIL:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE;
            case FAVORITE_DETAIL:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE;
            case UPCOMING_DETAIL:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE;
            case NOW_PLAYING_DETAIL:
                return ContentResolver.CURSOR_ITEM_BASE_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

    }

    /**
     * Method that must be implemented when extending the contentProvider class. Updates the content
     * provider's current data (ie changing the values in some rows and in some columns). Assumes
     * the values is filled out like the user intended (ie does no checks)
     * @param uri - the URI denotes which table needs updating
     * @param values - the new values to be put. ContentValues is basically a wrapper class of
     *               HashMap. Basically the contentValues is a HashMap, in its entirety is just
     *               one row of data. Each key should be a column Name of the table and each value
     *               associated with their key should be a value that pertains to that specific row
     *               of data for that column
     * @param selection - the string that is the SQL clause for what to filter for
     * @param selectionArgs - the string array where each value is a column and in order replaces
     *                      the '?''s in the SELECTION parameter
     * @return an int value which is the number of rows that have been updated
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated = 0;
        SQLiteDatabase db = mDBhelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        //note: I need the break statements after each case because once I match one case, the flow
        //executes each case in order even if the original case didn't match. Therefore, need
        //manually control the flow to ensure only one is executed
        switch (match) {
            case TOP_RATED:
                rowsUpdated = db.update(movieContract.TopRated.TABLE_NAME, values, selection
                        , selectionArgs);
                break;
            case MOST_POPULAR:
                rowsUpdated = db.update(movieContract.MostPopular.TABLE_NAME, values, selection
                        , selectionArgs);
                break;
            case FAVORITE:
                rowsUpdated = db.update(movieContract.FavoriteEntries.TABLE_NAME, values, selection
                        , selectionArgs);
                break;
            case UPCOMING:
                rowsUpdated = db.update(movieContract.Upcoming.TABLE_NAME, values, selection
                        , selectionArgs);
                break;
            case NOW_PLAYING:
                rowsUpdated = db.update(movieContract.NowPlaying.TABLE_NAME, values, selection
                        , selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI" + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    /**
     * required method to override when extending contentProviders. Note: the SQLITEDATABASE
     * insert() method returns a long ID
     * @param uri - the URI to the location of the database and table that we want to insert into
     *            note this URI is just necessarily the exact URI to use to find the actual location
     *            of the database. Just the URI to tell the content provider where to look. A URI
     *            used by the App to communicate with movieProvider (can be only of the form
     *            given by the buildUri methods in movieContract)
     * @param values - the values in the columns that we want to insert of this row of data
     * @return the URI that points to the new row's location in the table
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri new_uri;
        long rowId;
        int movieDbId;
        //this gets the integer value (determined by our build uri Matcher method) to help us
        //determine which table the user is trying to access
        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mDBhelper.getWritableDatabase();
        //note: if null can still cast to int
        Object id = values.get(movieContract.TopRated.MOVIE_DB_ID);
        if (id == null) {
            throw new UnsupportedOperationException("No value fo MovieDB ID - invalid data");
        } else {
            //if it isn't null then we can cast this object to an Int value as we know the ID will
            //be an valid type
            movieDbId = (int) id;
        }
        //see the comments in the update method on the switch statement
        switch (match) {
            case TOP_RATED:
                rowId = db.insertOrThrow(movieContract.TopRated.TABLE_NAME, null, values);
                new_uri = movieContract.buildMovieUri(movieContract.TopRated.TABLE_NAME, movieDbId);
                break;
            case MOST_POPULAR:
                rowId = db.insertOrThrow(movieContract.MostPopular.TABLE_NAME, null, values);
                new_uri = movieContract.buildMovieUri(movieContract.MostPopular.TABLE_NAME
                        , movieDbId);
                break;
            case FAVORITE:
                rowId = db.insertOrThrow(movieContract.FavoriteEntries.TABLE_NAME, null, values);
                new_uri = movieContract.buildMovieUri(movieContract.FavoriteEntries.TABLE_NAME
                        , movieDbId);
                break;
            case UPCOMING:
                rowId = db.insertOrThrow(movieContract.Upcoming.TABLE_NAME, null, values);
                new_uri = movieContract.buildMovieUri(movieContract.Upcoming.TABLE_NAME
                        , movieDbId);
                break;
            case NOW_PLAYING:
                rowId = db.insertOrThrow(movieContract.NowPlaying.TABLE_NAME, null, values);
                new_uri = movieContract.buildMovieUri(movieContract.NowPlaying.TABLE_NAME
                        , movieDbId);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI" + uri);
        }
        //this checks if the insert into the underlying database was unsucessful
        if (rowId == -1) {
            throw new SQLException("Unsuccessful sql DB insert" + uri + " " + match);
        } else {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return new_uri;
    }

    /**
     * method to perform a batch of inserts, more efficient than doing multiple individual inserts.
     * More efficient as do in one DB 'transaction' allowing for faster writing. Vs. individual
     * inserts would have to do a transaction for each insert
     * @param uri
     * @param values
     * @return
     */
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mDBhelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        long insertCheck;
        int rowsInserted = 0;
        switch (match) {
            case TOP_RATED:
                db.beginTransaction();
                for (ContentValues value : values) {
                    insertCheck = db.insert(movieContract.TopRated.TABLE_NAME, null, value);
                    if (insertCheck != -1) {
                        rowsInserted++;
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                break;
            case MOST_POPULAR:
                db.beginTransaction();
                for (ContentValues value : values) {
                    insertCheck = db.insert(movieContract.MostPopular.TABLE_NAME, null, value);
                    if (insertCheck != -1) {
                        rowsInserted++;
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                break;
            case FAVORITE:
                db.beginTransaction();
                for (ContentValues value : values) {
                    insertCheck = db.insert(movieContract.FavoriteEntries.TABLE_NAME, null, value);
                    if (insertCheck != -1) {
                        rowsInserted++;
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                break;
            case UPCOMING:
                db.beginTransaction();
                for (ContentValues value : values) {
                    insertCheck = db.insert(movieContract.Upcoming.TABLE_NAME, null, value);
                    if (insertCheck != -1) {
                        rowsInserted++;
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                break;
            case NOW_PLAYING:
                db.beginTransaction();
                for (ContentValues value : values) {
                    insertCheck = db.insert(movieContract.NowPlaying.TABLE_NAME, null, value);
                    if (insertCheck != -1) {
                        rowsInserted++;
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                break;
            default:
                return super.bulkInsert(uri, values);
        }
        return rowsInserted;
    }

    /**
     *
     * @param uri - the URI given by app that will be used by provider to tell which pieces of data
     *            should be deleted in actual DB
     * @param selection - the clause in SQL that will be used to delete on given rows
     * @param selectionArgs - the selection criteria by which to delete rows
     * @return the number of rows deleteds
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted = 0;
        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mDBhelper.getWritableDatabase();
        //we do this per the documentation of delete() in SQLiteDatabase. Basically, if selection is
        //null then we assume app wants to delete all rows and therefore we set selection to "1",
        //and per the android documentation when we set the SELECTION parameter to "1" the
        //sqLiteDatabase object will delete all rows and return the number of rows that were in
        //the table (ie the number of rows deleted)
        if (selection == null) {
            selection = "1";
        }
        switch (match) {
            case TOP_RATED:
                rowsDeleted = db.delete(movieContract.TopRated.TABLE_NAME, selection, selectionArgs);
                break;
            case MOST_POPULAR:
                rowsDeleted = db.delete(movieContract.MostPopular.TABLE_NAME, selection
                        , selectionArgs);
                break;
            case FAVORITE:
                rowsDeleted = db.delete(movieContract.FavoriteEntries.TABLE_NAME, selection
                        , selectionArgs);
                break;
            case UPCOMING:
                rowsDeleted = db.delete(movieContract.Upcoming.TABLE_NAME, selection
                        , selectionArgs);
                break;
            case NOW_PLAYING:
                rowsDeleted = db.delete(movieContract.NowPlaying.TABLE_NAME, selection
                        , selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI" + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(TAG, "delete: " + rowsDeleted + ": " + uri);
        Log.d(TAG, "delete: " + selection + " :" + selectionArgs[0]);
        return rowsDeleted;
    }

    /**
     *
     * @param uri- the URI given by app that will be used by provider to tell which pieces of data
     *            should be deleted in actual DB
     * @param projection - the specific columns that we want returned
     * @param selection - the clause in SQL that will be used to delete on given rows
     * @param selectionArgs - the selection criteria by which to delete rows
     * @param sortOrder - the sort order of the rows
     * @return
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs
            , String sortOrder) {
        SQLiteDatabase db = mDBhelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor cursor = null;
        switch (match) {
            case TOP_RATED:
                cursor = db.query(movieContract.TopRated.TABLE_NAME, projection, selection
                        , selectionArgs, null, null, sortOrder);
                break;
            case MOST_POPULAR:
                cursor = db.query(movieContract.MostPopular.TABLE_NAME, projection, selection
                        , selectionArgs, null, null, sortOrder);
                break;
            case FAVORITE:
                cursor = db.query(movieContract.FavoriteEntries.TABLE_NAME, projection, selection
                    , selectionArgs, null, null, sortOrder);
                break;
            case UPCOMING:
                cursor = db.query(movieContract.Upcoming.TABLE_NAME, projection, selection
                        , selectionArgs, null, null, sortOrder);
                break;
            case NOW_PLAYING:
                cursor = db.query(movieContract.NowPlaying.TABLE_NAME, projection, selection
                        , selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * A test package can call this to get a handle to the database underlying movieProvider,
     * so it can insert test data into the database. The test case class is responsible for
     * instantiating the provider in a test context; {@link android.test.ProviderTestCase2} does
     * this during the call to setUp()
     * @return
     */
    public movieDBHelper getmDBhelperForTest() {
        return mDBhelper;
    }
}
