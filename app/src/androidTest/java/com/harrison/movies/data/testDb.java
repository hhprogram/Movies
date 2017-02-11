package com.harrison.movies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashSet;

import static android.content.ContentValues.TAG;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static org.junit.Assert.assertTrue;

//this import is needed in order to get a Context instance to run tests. This replaces the
//AndroidTestCase...now can just call getContext()

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class testDb {

    final Integer TEST_ROW_ID = 1;
    final Double TEST_VOTE = 5.5;
    final Integer TEST_MOVIE_ID = 2938;
    final String TEST_DATE = "1/1/2017";
    final String TEST_EN_TITLE = "Test Movie";
    final String TEST_BIG_URL = "Big URL test";
    final String TEST_URL = "URL test";
    final String TEST_ORIG_TITLE = "Original title test";
    final String TEST_OVERVIEW = "Test overview";
    final String TEST_REVIEW = "Good if it works and not if it doesn't";
    final String TEST_YOUTUBE = "youtube key";
    final String[] COLUMNS = {movieContract.TopRated._ID, movieContract.TopRated.BIG_POSTER_URL
        , movieContract.TopRated.EN_TITLE, movieContract.TopRated.MOVIE_DB_ID
        , movieContract.TopRated.ORIG_TITLE, movieContract.TopRated.OVERVIEW
        , movieContract.TopRated.POSTER_URL, movieContract.TopRated.REL_DATE
        , movieContract.TopRated.REVIEW, movieContract.TopRated.TRAILER_YOUTUBE_KEY
        , movieContract.TopRated.VOTE};


    @Test
    public void testCreateTables() throws Exception {
        getTargetContext().deleteDatabase(movieDBHelper.DATABASE_NAME);
        movieDBHelper dbHelper = new movieDBHelper(getTargetContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        HashSet<String> tables = new HashSet<>();
        tables.add(movieContract.TopRated.TABLE_NAME);
        tables.add(movieContract.FavoriteEntries.TABLE_NAME);
        tables.add(movieContract.MostPopular.TABLE_NAME);
        tables.add(movieContract.Upcoming.TABLE_NAME);
        tables.add(movieContract.NowPlaying.TABLE_NAME);
        Cursor cursor;
        // this SQL queries returns a table of all names of tables in DB. First column is the name
        //of each table, each row is a different table
        cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        //note: i need to call moveToFirst before i do anythign with cursor anyways as the cursor
        //starts at -1 (the position before the first row) so in order to avoid errors need to first
        //call moveToFirst
        assertTrue("rawQuery didn't work for getting all tables", cursor.moveToFirst());
        int table_count = 0;
        boolean c = true;
        //loop through hashset - to ensure that each table is actually listed in the system
        do {

            if (tables.contains(cursor.getString(0))) {
                table_count++;
            }
        } while (cursor.moveToNext());
        assertTrue("Not all tables created", tables.size() == table_count);

        HashSet<String> table_columns = new HashSet<>();
        table_columns.add(movieContract.TopRated._ID);
        table_columns.add(movieContract.TopRated.BIG_POSTER_URL);
        table_columns.add(movieContract.TopRated.EN_TITLE);
        table_columns.add(movieContract.TopRated.MOVIE_DB_ID);
        table_columns.add(movieContract.TopRated.ORIG_TITLE);
        table_columns.add(movieContract.TopRated.OVERVIEW);
        table_columns.add(movieContract.TopRated.POSTER_URL);
        table_columns.add(movieContract.TopRated.REL_DATE);
        table_columns.add(movieContract.TopRated.REVIEW);
        table_columns.add(movieContract.TopRated.TRAILER_YOUTUBE_KEY);
        table_columns.add(movieContract.TopRated.VOTE);

        //this Pragma SQL statement returns a table where the rows are the columns, and the columns
        //of this table describe the attributes of each column. Example the first column is titled
        //"name" and is the name of the column, then there is type which denotes the data type
        //held in this column
        cursor = db.rawQuery("PRAGMA table_info(" + movieContract.TopRated.TABLE_NAME + ");",
                null);
        assertTrue("Pragma table info error in TopRated for getting all columns"
                , cursor.moveToFirst());
        //this is the column label for the name of each column, the pragma result will have the
        //name of each column in the original table in this column and each row in the pragma is
        //a detailed breakdown of each column in the orginal table. Note: getString(0) will just
        //give me the unique ID which is just 0,1,2...which i did before and it wasn't matching with
        //table_columns obviously and giving me an erroneous error
        String column_label = "name";
        int columnIndex = cursor.getColumnIndex(column_label);
        String column_name;
        ArrayList<String> pragma = new ArrayList<>();
        do {
            column_name = cursor.getString(columnIndex);
            pragma.add(column_name);
            if (table_columns.contains(column_name)) {
                table_columns.remove(column_name);
            }
        } while (cursor.moveToNext());
        assertTrue("Not all columns present in Top rated" + movieContract.TopRated.TABLE_NAME
                + table_columns.size() + " columns remaining "
                + pragma
                , table_columns.isEmpty());
        //repopulate the hashset and do same thing for the other 3 tables - should be better way to
        //do this

        table_columns.add(movieContract.TopRated._ID);
        table_columns.add(movieContract.TopRated.BIG_POSTER_URL);
        table_columns.add(movieContract.TopRated.EN_TITLE);
        table_columns.add(movieContract.TopRated.MOVIE_DB_ID);
        table_columns.add(movieContract.TopRated.ORIG_TITLE);
        table_columns.add(movieContract.TopRated.OVERVIEW);
        table_columns.add(movieContract.TopRated.POSTER_URL);
        table_columns.add(movieContract.TopRated.REL_DATE);
        table_columns.add(movieContract.TopRated.REVIEW);
        table_columns.add(movieContract.TopRated.TRAILER_YOUTUBE_KEY);
        table_columns.add(movieContract.TopRated.VOTE);

        //this Pragma SQL statement returns a table where the rows are the columns, and the columns
        //of this table describe the attributes of each column. Example the first column is titled
        //"name" and is the name of the column, then there is type which denotes the data type
        //held in this column
        cursor = db.rawQuery("PRAGMA table_info(" + movieContract.MostPopular.TABLE_NAME + ");",
                null);
        assertTrue("Pragma table info error in Most Popular for getting all columns"
                , cursor.moveToFirst());

        do {
            column_name = cursor.getString(columnIndex);
            if (table_columns.contains(column_name)) {
                table_columns.remove(column_name);
            }
        } while (cursor.moveToNext());
        assertTrue("Not all columns present in " + movieContract.MostPopular.TABLE_NAME
                + table_columns.size() + " columns remaining"
                , table_columns.isEmpty());

        table_columns.add(movieContract.TopRated._ID);
        table_columns.add(movieContract.TopRated.BIG_POSTER_URL);
        table_columns.add(movieContract.TopRated.EN_TITLE);
        table_columns.add(movieContract.TopRated.MOVIE_DB_ID);
        table_columns.add(movieContract.TopRated.ORIG_TITLE);
        table_columns.add(movieContract.TopRated.OVERVIEW);
        table_columns.add(movieContract.TopRated.POSTER_URL);
        table_columns.add(movieContract.TopRated.REL_DATE);
        table_columns.add(movieContract.TopRated.REVIEW);
        table_columns.add(movieContract.TopRated.TRAILER_YOUTUBE_KEY);
        table_columns.add(movieContract.TopRated.VOTE);

        //this Pragma SQL statement returns a table where the rows are the columns, and the columns
        //of this table describe the attributes of each column. Example the first column is titled
        //"name" and is the name of the column, then there is type which denotes the data type
        //held in this column
        cursor = db.rawQuery("PRAGMA table_info(" + movieContract.FavoriteEntries.TABLE_NAME + ");",
                null);
        assertTrue("Pragma table info error in Favorites for getting all columns"
                , cursor.moveToFirst());

        do {
            column_name = cursor.getString(columnIndex);
            if (table_columns.contains(column_name)) {
                table_columns.remove(column_name);
            }
        } while (cursor.moveToNext());
        assertTrue("Not all columns present in " + movieContract.FavoriteEntries.TABLE_NAME
                + table_columns.size() + " columns remaining"
                , table_columns.isEmpty());

        table_columns.add(movieContract.Upcoming._ID);
        table_columns.add(movieContract.Upcoming.BIG_POSTER_URL);
        table_columns.add(movieContract.Upcoming.EN_TITLE);
        table_columns.add(movieContract.Upcoming.MOVIE_DB_ID);
        table_columns.add(movieContract.Upcoming.ORIG_TITLE);
        table_columns.add(movieContract.Upcoming.OVERVIEW);
        table_columns.add(movieContract.Upcoming.POSTER_URL);
        table_columns.add(movieContract.Upcoming.REL_DATE);
        table_columns.add(movieContract.Upcoming.REVIEW);
        table_columns.add(movieContract.Upcoming.TRAILER_YOUTUBE_KEY);
        table_columns.add(movieContract.Upcoming.VOTE);

        //this Pragma SQL statement returns a table where the rows are the columns, and the columns
        //of this table describe the attributes of each column. Example the first column is titled
        //"name" and is the name of the column, then there is type which denotes the data type
        //held in this column
        cursor = db.rawQuery("PRAGMA table_info(" + movieContract.Upcoming.TABLE_NAME + ");",
                null);
        assertTrue("Pragma table info error in Favorites for getting all columns"
                , cursor.moveToFirst());

        do {
            column_name = cursor.getString(columnIndex);
            if (table_columns.contains(column_name)) {
                table_columns.remove(column_name);
            }
        } while (cursor.moveToNext());
        assertTrue("Not all columns present in " + movieContract.Upcoming.TABLE_NAME
                        + table_columns.size() + " columns remaining"
                , table_columns.isEmpty());

        table_columns.add(movieContract.NowPlaying._ID);
        table_columns.add(movieContract.NowPlaying.BIG_POSTER_URL);
        table_columns.add(movieContract.NowPlaying.EN_TITLE);
        table_columns.add(movieContract.NowPlaying.MOVIE_DB_ID);
        table_columns.add(movieContract.NowPlaying.ORIG_TITLE);
        table_columns.add(movieContract.NowPlaying.OVERVIEW);
        table_columns.add(movieContract.NowPlaying.POSTER_URL);
        table_columns.add(movieContract.NowPlaying.REL_DATE);
        table_columns.add(movieContract.NowPlaying.REVIEW);
        table_columns.add(movieContract.NowPlaying.TRAILER_YOUTUBE_KEY);
        table_columns.add(movieContract.NowPlaying.VOTE);

        //this Pragma SQL statement returns a table where the rows are the columns, and the columns
        //of this table describe the attributes of each column. Example the first column is titled
        //"name" and is the name of the column, then there is type which denotes the data type
        //held in this column
        cursor = db.rawQuery("PRAGMA table_info(" + movieContract.NowPlaying.TABLE_NAME + ");",
                null);
        assertTrue("Pragma table info error in Favorites for getting all columns"
                , cursor.moveToFirst());

        do {
            column_name = cursor.getString(columnIndex);
            if (table_columns.contains(column_name)) {
                table_columns.remove(column_name);
            }
        } while (cursor.moveToNext());
        assertTrue("Not all columns present in " + movieContract.NowPlaying.TABLE_NAME
                        + table_columns.size() + " columns remaining"
                , table_columns.isEmpty());
    }

    /**
     * this just tests to make sure the underlying DB helper and tables work - not testing
     * the provider functionality yet. Another test file will include this
     */
    @Test
    public void testInsert() {
        long rowID;
        getTargetContext().deleteDatabase(movieDBHelper.DATABASE_NAME);
        movieDBHelper dbHelper = new movieDBHelper(getTargetContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        rowID = insertTestRow(db, movieContract.MostPopular.TABLE_NAME);
        assertTrue("Error during most popular insert", rowID != -1);

        rowID = insertTestRow(db, movieContract.FavoriteEntries.TABLE_NAME);
        assertTrue("Error during favorites insert", rowID != -1);

        rowID = insertTestRow(db, movieContract.TopRated.TABLE_NAME);
        assertTrue("Error during insert top rate", rowID != -1);

        rowID = insertTestRow(db, movieContract.Upcoming.TABLE_NAME);
        assertTrue("Error during insert upcoming", rowID != -1);

        rowID = insertTestRow(db, movieContract.NowPlaying.TABLE_NAME);
        assertTrue("Error during insert now playing", rowID != -1);
    }

    @Test
    public void testDelete() {
        getTargetContext().deleteDatabase(movieDBHelper.DATABASE_NAME);
        movieDBHelper dbHelper = new movieDBHelper(getTargetContext());
        Integer rowsDeleted;
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long rowID = insertTestRow(db, movieContract.FavoriteEntries.TABLE_NAME);
        assertTrue("Error during insert phase of delete test", rowID != -1);
        rowsDeleted = deleteTestRow(db, movieContract.FavoriteEntries.TABLE_NAME);
        Log.d(TAG, "testDelete: " + rowsDeleted.toString());
        assertTrue("Error during favorites delete test", rowID != 0);

        rowID = insertTestRow(db, movieContract.MostPopular.TABLE_NAME);
        assertTrue("Error during insert phase of delete test", rowID != -1);
        rowsDeleted = deleteTestRow(db, movieContract.MostPopular.TABLE_NAME);
        Log.d(TAG, "testDelete: " + rowsDeleted.toString());
        assertTrue("Error during most popular delete test", rowID != 0);

        rowID = insertTestRow(db, movieContract.TopRated.TABLE_NAME);
        assertTrue("Error during insert phase of delete test", rowID != -1);
        rowsDeleted = deleteTestRow(db, movieContract.TopRated.TABLE_NAME);
        Log.d(TAG, "testDelete: " + rowsDeleted.toString());
        assertTrue("Error during most popular delete test", rowID != 0);

        rowID = insertTestRow(db, movieContract.Upcoming.TABLE_NAME);
        assertTrue("Error during insert phase of delete test", rowID != -1);
        rowsDeleted = deleteTestRow(db, movieContract.Upcoming.TABLE_NAME);
        Log.d(TAG, "testDelete: " + rowsDeleted.toString());
        assertTrue("Error during most popular delete test", rowID != 0);

        rowID = insertTestRow(db, movieContract.NowPlaying.TABLE_NAME);
        assertTrue("Error during insert phase of delete test", rowID != -1);
        rowsDeleted = deleteTestRow(db, movieContract.NowPlaying.TABLE_NAME);
        Log.d(TAG, "testDelete: " + rowsDeleted.toString());
        assertTrue("Error during most popular delete test", rowID != 0);
    }

//    /**
//     * test function to ensure the row data contents are what i intended to put in there
//     */
//    @Test
//    public void testContents() {
//        Cursor cursor;
//        String column_name;
//        int column_index;
//        getTargetContext().deleteDatabase(movieDBHelper.DATABASE_NAME);
//        movieDBHelper dbHelper = new movieDBHelper(getTargetContext());
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        long rowID = insertTestRow(db, movieContract.FavoriteEntries.TABLE_NAME);
//        assertTrue("error during insert phase of Fav contents test", rowID != -1);
//        //get the all of the data - which should only be one row
//        cursor = db.query(movieContract.FavoriteEntries.TABLE_NAME, null, null, null, null, null
//                , null);
//        assertTrue("Too many rows",cursor.getCount() == 1);
//
//    }

    /**
     * helper function to be called by test methods - so can reuse test rows
     * @param db - the sqlitydb instance to insert the row in
     * @param tableName - the name of table we are inserting into
     * @return return row ID or -1 if error just like normal insert()
     */
    public long insertTestRow(SQLiteDatabase db, String tableName) {
        Class ca;
        ArrayList<String> table_ref = new ArrayList<>();
        table_ref.add(movieContract.TopRated.TABLE_NAME);
        table_ref.add(movieContract.MostPopular.TABLE_NAME);
        table_ref.add(movieContract.FavoriteEntries.TABLE_NAME);
        table_ref.add(movieContract.Upcoming.TABLE_NAME);
        table_ref.add(movieContract.NowPlaying.TABLE_NAME);
        if (!table_ref.contains(tableName)) {
            Log.d(TAG, "insertTestRow: Invalid Class name");
            return -1;
        } else {
            ContentValues values = new ContentValues();
            values.put(COLUMNS[0], TEST_ROW_ID);
            values.put(COLUMNS[1], TEST_BIG_URL);
            values.put(COLUMNS[2], TEST_EN_TITLE);
            values.put(COLUMNS[3], TEST_MOVIE_ID);
            values.put(COLUMNS[4], TEST_ORIG_TITLE);
            values.put(COLUMNS[5], TEST_OVERVIEW);
            values.put(COLUMNS[6], TEST_URL);
            values.put(COLUMNS[7], TEST_DATE);
            values.put(COLUMNS[8], TEST_REVIEW);
            values.put(COLUMNS[9], TEST_YOUTUBE);
            values.put(COLUMNS[10], TEST_VOTE);
            return db.insert(tableName, null, values);
        }
    }


    //helper function used by test methods - so multiple tests can re-use these. returns -1 if error
    public int deleteTestRow(SQLiteDatabase db, String tableName) {
        ArrayList<String> table_ref = new ArrayList<>();
        table_ref.add(movieContract.TopRated.TABLE_NAME);
        table_ref.add(movieContract.MostPopular.TABLE_NAME);
        table_ref.add(movieContract.FavoriteEntries.TABLE_NAME);
        table_ref.add(movieContract.Upcoming.TABLE_NAME);
        table_ref.add(movieContract.NowPlaying.TABLE_NAME);
        if (!table_ref.contains(tableName)) {
            Log.d(TAG, "deleteTestRow: Invalid table name");
            return -1;
        }
        String selection = movieContract.TopRated._ID + "=?";
        String[] args = {TEST_ROW_ID.toString()};
        return db.delete(tableName, selection, args);
    }
}
