package com.harrison.movies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.harrison.movies.data.movieContract;
import com.squareup.picasso.Picasso;

/**
 * Cursor adapter to be used for the favorites DB as we want that to be DB based because we want
 * the favorites to persist
 *
 */

public class ImageListAdapter extends CursorAdapter {
    private LayoutInflater inflater;


    public ImageListAdapter (Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        inflater = LayoutInflater.from(context);

    }

    @Override
    public void bindView (View view, Context context, Cursor cursor) {
        int columnIndex = cursor.getColumnIndex(movieContract.TopRated.POSTER_URL);
        Picasso.with(context)
                .load(cursor.getString(columnIndex))
                .into((ImageView) view);
        }

    @Override
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.single_poster, parent, false);
        return view;
    }
    //old constructor when we just used an ArrayAdapter
//    /**
//     * Constructor of an ImageListAdapter that calls the super() constructor of an array adapter
//     * with 3 parameters. Sets the context to be the given context, and then assigns the given
//     * String[] to this ArrayAdapter object. Then I tell this imageListAdapter object to use
//     * the layout SINGLE_POSTER
//     * @param context - the context that creates this adapter
//     * @param movies - this will be a string array of URLS for the movie posters, this array
//     *                   will be generated in another function that interacts with movieDB based
//     *                   on the type of preference loaded to generate this list
//     */
//    public ImageListAdapter(Context context, ArrayList<Movie> movies) {
//        super(context, R.layout.single_poster, movies);
//        this.context = context;
//        this._movies = movies;
//        inflater = LayoutInflater.from(context);
//    }


    //old method to populate the view when we just used an ArrayAdapter
//    /**
//     * A method that overrides the ArrayAdapter's method. This returns a View that displays the
//     * data in this ArrayAdapter associated with the index POSITION. CONVERTVIEW is a view that we
//     * can possible re-use (already loaded) and re-populate with data in POSITION, if CONVERTVIEW
//     * is null (ie no View available already loaded) then must inflate a View from scratch
//     * @param position - index in Adapter of desired data we want to display in CONVERTVIEW
//     * @param convertView - View to be re-used
//     * @param parent - parent of CONVERTVIEW
//     * @return a View with data from the POSITION in this imagelistadapter.
//     */
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            convertView = inflater.inflate(R.layout.single_poster, parent, false);
//        }
//        Picasso.with(context)
//                .load(_movies.get(position).getPosterURL())
//                .into((ImageView) convertView);
//
//        return convertView;
//    }
}
