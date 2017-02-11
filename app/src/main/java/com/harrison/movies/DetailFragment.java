package com.harrison.movies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.harrison.movies.data.movieContract;
import com.squareup.picasso.Picasso;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements AsyncListener {

    GetVideosAndReviews videosAndReviews;
    //specific bundle sent that started the activity that loaded this fragment
    Bundle myBundle;
    //the specific movie object that this fragment is showing
    Movie movie;
    HashMap<String, ArrayList<String>> vidAndRevUrls;
    //contentValues to potentially be put into the favorites table
    ContentValues movieValues = new ContentValues();
    View rootView;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_detail_fragment, container, false);
        Intent incomingIntent = getActivity().getIntent();
        myBundle = incomingIntent.getExtras();
        movie = myBundle.getParcelable(getString(R.string.single_movie_bundle));
        //creates the new AsyncTask
        videosAndReviews = new GetVideosAndReviews(this);
        //actually kicks off the new AsyncTask's execution to obtain the extra info required
        //when in the detailed view
        videosAndReviews.execute(String.valueOf(movie.getMovieDBid()));
        //put the rootView as an argument as if I feed in the CONTAINER and try to find the R.id
        //values I will not find them because it doesn't know that ROOTVIEW is in the hierarchy
        //of CONTAINER, so in order to find the R.id values, need to make the parent the rootView
        //which is the layout that contains all the actual views whose IDs i refer to in the
        //setViewValues method
        setIntialViewValues((ViewGroup) rootView);
        Button favButton = (Button) rootView.findViewById(R.id.fav_button);
        //first i get the selection criteria so I can query the favorites table for this specific
        //movies
        final String[] columns = {movieContract.FavoriteEntries.MOVIE_DB_ID};
        final String select = movieContract.FavoriteEntries.MOVIE_DB_ID + "= ?";
        final String[] selectionArgs = {String.valueOf(movie.getMovieDBid())};
        final Uri favTable = movieContract
                .buildMovieUri(movieContract.FavoriteEntries.TABLE_NAME);
        Cursor cursor = getContext().getContentResolver().query(favTable, columns, select
                , selectionArgs, null);
        //if this movie is in the table already I set the text to delete favorite and call
        //the utility method for the onclick listener to the delete tag
        //if not in favorites then i insert it into the favorites table
        if (cursor.moveToFirst()) {
            favButton.setText(MainActivity.getContext().getResources()
                    .getString(R.string.delete_favorite));
            favButton.setOnClickListener(Utilities.getFavListener(favButton, 1, movie
                    , getContext()));
        } else {
            favButton.setText(MainActivity.getContext().getResources()
                    .getString(R.string.add_favorites));
            favButton.setOnClickListener(Utilities.getFavListener(favButton, 0, movie
                    , getContext()));
        }



        return rootView;
    }

    /**
     * Method that sets all the views in the Detail movie view. Populates with Title, poster image,
     * release date, avg score and overview
     * PARENT param used so we can reference the viewgroup that the detailed view is contained in
     * and then find the specific views within it
     * BUNDLE is the bundle sent in the intent that includes the complete arraylist of movies and
     * the corresponding position of the specific movie of whose details we want. The if statement
     * checks if the ORIGINAL_TITLE (original lanugage no_connection) equals the TITLE
     * (field for english translated no_connection) - if they don't they put the original
     * no_connection and then
     * the english translated no_connection next to it in parantheses
     *
     * used to set the original values for stage 1 needed for detail view just so the viewer has
     * something if asynctask takes a little bit
     */
    public void setIntialViewValues(ViewGroup parent) {
        TextView view = (TextView) parent.findViewById(R.id.titleView);
        String txt;
        if (!(movie.getTitle().equals(movie.getEnTitle()))) {
            view.setText(movie.getTitle() + getString(R.string.left_paran)
                    + movie.getEnTitle() + getString(R.string.right_paran));
        } else {
            view.setText(movie.getTitle());
        }
        ImageView posterImage = (ImageView) parent.findViewById(R.id.imageView);
        Picasso.with(getActivity())
                .load(movie.getBigPosterURL())
                .into((ImageView) posterImage);
        view = (TextView) parent.findViewById(R.id.releaseDateView);
        txt = getString(R.string.detail_date_label) + movie.getStringDate();
        //spannable str builder so I can make one string into different types of fonts
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(txt);
        //the first arg to setSpan is the SPAN we want to set the text between the 2nd arg and the
        //3rd arg indices of the string.
        // The last arg is a flag of how the span is applied (unsure really)
        //make the title part of the text bold
        stringBuilder.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), 0
                , getString(R.string.detail_date_label).length()
                , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(stringBuilder);
        view = (TextView) parent.findViewById(R.id.voteAvgView);
        txt = getString(R.string.detail_vote) + movie.getStringVote();
        stringBuilder = new SpannableStringBuilder(txt);
        stringBuilder.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), 0
                , getString(R.string.detail_vote).length()
                ,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(stringBuilder);
        view = (TextView) parent.findViewById(R.id.overviewView);
        txt = getString(R.string.detail_overview)
                + getString(R.string.new_line)
                + movie.getOverview();
        stringBuilder = new SpannableStringBuilder(txt);
        stringBuilder.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), 0
                , getString(R.string.detail_overview).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(stringBuilder);
    }

    /**
     * Helper function to dyanmically add buttons for each element in BUTTONS to the bottom of the
     * detailed view of a movie. Changes the view to a new view with these added buttons.
     * Method that is called and then decides to call helper private methods
     * @param buttonLinks - an array list where each element requires its own button
     * @param labels - can be null, 'optional' arraylist that are the labels to be used for the
     *               button text
     * @param section - the string that is the label for the buttons to be added
     */
    private void addButtons(ArrayList<String> buttonLinks, ArrayList<String> labels
            , String section) {
        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.detail_linear_layout);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (buttonLinks.size() != labels.size()) {
            throw new InvalidParameterException("Labels and buttonLinks aren't same size");
        }
        TextView txtView = new TextView(getContext());
        txtView.setText(section);
        txtView.setTypeface(null, Typeface.BOLD);
        txtView.setTextColor(Color.BLACK);
        layout.addView(txtView);
        for (int i = 0; i < buttonLinks.size(); i++) {
            Button button = new Button(getContext());
            button.setText(labels.get(i));
            //set an onClickListener to this specific button which is created by my Utilities
            //function which create a specific destination to it (determined by the BUTTONLINKS)
            //arraylist elements
            button.setOnClickListener(Utilities.getClickListener(button, buttonLinks.get(i)
                    , getContext()));
            layout.addView(button, params);
        }
    }



    //when my asyncTask completes (ie gets the required youtube and review info, then I make sure
    //to populate the relevant movie object with this new information so that the user can access
    //it now
    @Override
    public void onTaskCompletion() {
        try {
            vidAndRevUrls = videosAndReviews.get();
            movie.setReviewUrlList(vidAndRevUrls.get(Movie.REVIEW_KEY));
            movie.setYoutubeUrlList(vidAndRevUrls.get(Movie.YOUTUBE_KEY));
//            Toast.makeText(getContext(), movie.getYoutubeKeys().get(0), Toast.LENGTH_SHORT).show();
//            addButtons(movie.getReviewUrls());
            //need to use the get method as I do not store the trailer labels or review labels
            //in the movie objects
            addButtons(movie.getYoutubeKeys(), vidAndRevUrls.get(Utilities.TRAILER_NAME)
                    , Utilities.DETAIL_YOUTUBE_LABEL);
            addButtons(movie.getReviewUrls() , vidAndRevUrls.get(Utilities.REVIEW_AUTHOR)
                    , Utilities.DETAIL_REVIEW_LABEL);
        } catch (InterruptedException e) {
            Log.d(TAG, "onTaskCompletion: Interrupted exception for video and review task");
        } catch (ExecutionException e) {
            Log.d(TAG, "onTaskCompletion: Execution exception for video and review task");
        }
    }
}
