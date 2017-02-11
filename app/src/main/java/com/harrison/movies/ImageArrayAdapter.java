package com.harrison.movies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Use a new class that extends the ArrayAdapter class (custom ArrayAdapter).
 * Use this as I don't want to use an
 * ImageAdapter because I do not want to download movie poster images into my drawables folder
 * want to just load them from the movieDB server. So my array will hold movie objects. Then when
 * I use picasso to load an image I just call MOVIE.GETPOSTERURL to get me the URL string
 * Note: ArrayAdapter doesn't have a default constructor so needed to call super() on a constructor
 * with arguments or else it by default tries to call default constructor ArrayAdapter() which
 * doesn't exist
 *
 * This adapter used for the web request data as don't need it to be persistent and therefore
 * can just be a normal array adapter
 * Created by harrison on 12/29/16.
 */

public class ImageArrayAdapter extends ArrayAdapter<Movie> {
    private Context context;
    private ArrayList<Movie> _movies;
    private LayoutInflater inflater;
        /**
     * Constructor of an ImageListAdapter that calls the super() constructor of an array adapter
     * with 3 parameters. Sets the context to be the given context, and then assigns the given
     * String[] to this ArrayAdapter object. Then I tell this imageListAdapter object to use
     * the layout SINGLE_POSTER
     * @param context - the context that creates this adapter
     * @param movies - this will be a string array of URLS for the movie posters, this array
     *                   will be generated in another function that interacts with movieDB based
     *                   on the type of preference loaded to generate this list
     */
    public ImageArrayAdapter(Context context, ArrayList<Movie> movies) {
        super(context, R.layout.single_poster, movies);
        this.context = context;
        this._movies = movies;
        inflater = LayoutInflater.from(context);
    }

    /**
     * A method that overrides the ArrayAdapter's method. This returns a View that displays the
     * data in this ArrayAdapter associated with the index POSITION. CONVERTVIEW is a view that we
     * can possible re-use (already loaded) and re-populate with data in POSITION, if CONVERTVIEW
     * is null (ie no View available already loaded) then must inflate a View from scratch
     * @param position - index in Adapter of desired data we want to display in CONVERTVIEW
     * @param convertView - View to be re-used
     * @param parent - parent of CONVERTVIEW
     * @return a View with data from the POSITION in this imagelistadapter.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.single_poster, parent, false);
        }
        Picasso.with(context)
                .load(_movies.get(position).getPosterURL())
                .into((ImageView) convertView);

        return convertView;
    }
}
