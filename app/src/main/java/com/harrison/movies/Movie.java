package com.harrison.movies;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Custom object to make it easier to store movie details vs. doing some sort of hashmap. Use case
 * is when I have an array of movies and I want the order to align with the gridView order I put
 * in the array just like i do for gridView and now the array is full of MOVIE objects, then i can
 * access the specific details by getting the instance attributes of each one. Natural ordering
 * is by release date with the most current being "first".
 *
 * Note: Object has normal constructors and then a 'Parcel' constructor to allow for it to be
 * Parceable - ie create a symantically identical Movie Object from a Parcel. The order of the
 * reading parcelable and writing parcel must be the same.
 * Created by harrison on 1/3/17.
 */

public class Movie implements Comparable<Movie>, Parcelable {
    private String _title, _overview, _posterURL, _detailPosterURL, _enTitle;
    private ArrayList<String> _review, _youtube;
    private GregorianCalendar _releaseDate;
    private Double _voteAvg;
    private int _movieDBid;
    //static strings used in hashmaps to easily populate movie objects with their youtube trailer
    //URLS and review URLs (referenced by movieDB methods)
    static final String YOUTUBE_KEY = "youtube";
    static final String REVIEW_KEY = "review";

    Movie() {
        this("Default Movie", "Default Movie Constructed", "No poster URL"
                , Calendar.getInstance(),0.0, "No poster URL", "Default Translate Title");
    }

    Movie(String title, String overview, String poster, Calendar date, Double vote,
          String posterBig, String enTitle) {
        this._title = title;
        this._overview = overview;
        this._posterURL = poster;
        this._releaseDate = (GregorianCalendar) date;
        this._voteAvg = vote;
        this._detailPosterURL = posterBig;
        this._enTitle = enTitle;
        this._review = new ArrayList<>();
        this._youtube = new ArrayList<>();
    }

    //constructor for stage 2 where I need to be able to watch the trailer and read the review
    Movie(String title, String overview, String poster, Calendar date, Double vote,
          String posterBig, String enTitle, ArrayList<String> review, ArrayList<String> youtube
            , int id) {
        this._title = title;
        this._overview = overview;
        this._posterURL = poster;
        this._releaseDate = (GregorianCalendar) date;
        this._voteAvg = vote;
        this._detailPosterURL = posterBig;
        this._enTitle = enTitle;
        this._review = review;
        this._youtube = youtube;
        this._movieDBid = id;
    }

    Movie (Parcel in) {
        this._title = in.readString();
        this._overview = in.readString();
        this._posterURL = in.readString();
        this._releaseDate = (GregorianCalendar) in.readSerializable();
        this._voteAvg = in.readDouble();
        this._detailPosterURL = in.readString();
        this._enTitle = in.readString();
        //to read in an ArrayList need to pass in a classLoader for the class of objects that the
        //arraylist holds (made a bookmark under Android tab)
        this._youtube = in.readArrayList(String.class.getClassLoader());
        this._review = in.readArrayList(String.class.getClassLoader());
        this._movieDBid = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._title);
        dest.writeString(this._overview);
        dest.writeString(this._posterURL);
        dest.writeSerializable(this._releaseDate);
        dest.writeDouble(this._voteAvg);
        dest.writeString(this._detailPosterURL);
        dest.writeString(this._enTitle);
        dest.writeList(this._youtube);
        dest.writeList(this._review);
        dest.writeInt(this._movieDBid);
    }

    static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel (Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };


    public void setMovieDBid(int id) {
        this._movieDBid = id;
    }

    public void addReviewUrl(String review) {
        this._review.add(review);
    }

    public void addYoutubeUrl(String youtube) {
        this._youtube.add(youtube);
    }

    public void setReviewUrlList(ArrayList<String> review) {
        this._review= review;
    }

    public void setYoutubeUrlList(ArrayList<String> youtube) {
        this._youtube = youtube;
    }

    public void setTitle(String title) {
        this._title = title;
    }

    public void setOverview(String overview) {
        this._overview = overview;
    }

    public void setPosterURL(String poster) {

        this._posterURL = poster;
    }

    public void setBigPosterURL(String poster) {
        this._detailPosterURL = poster;
    }

    public void setReleaseDate(Calendar date) {

        this._releaseDate = (GregorianCalendar) date;
    }

    public void setVoteAvg(Double vote) {

        this._voteAvg = vote;
    }

    public void setEnTitle(String enTitle) {
        this._enTitle = enTitle;
    }

    public int getMovieDBid() {
        return this._movieDBid;
    }

    public ArrayList<String> getReviewUrls() {
        return this._review;
    }

    public ArrayList<String> getYoutubeKeys() {
        return this._youtube;
    }

    //needed as some movies the ORIGINAL_TITLE field is in original lanugage
    public String getEnTitle() {
        return this._enTitle;
    }

    public String getTitle() {
        return this._title;
    }

    public String getOverview() {
        return this._overview;
    }

    public String getPosterURL() {
        return this._posterURL;
    }

    public String getBigPosterURL() {
        return this._detailPosterURL;
    }

    public GregorianCalendar getReleaseDate() {
        return this._releaseDate;
    }

    //returns string version of the Gregorian Date. Have this so not to break abstraction barrier
    //note need the .getTime() or else this wasn't working as getTime() returns a Date object which
    //format requires as its parameter - and note a Gregorian Calendar Object
    public String getStringDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(Utilities.DATE_FORMAT);
        return sdf.format(this._releaseDate.getTime());
    }

    public Double getVoteAvg() {
        return this._voteAvg;
    }

    public String getStringVote() {
        return this._voteAvg.toString();
    }

    //Date orders it with date closer to present as 'bigger' than another date, so want to get the
    //reverse of that ordering so collections.sort will order the most current dates first
    @Override
    public int compareTo(Movie other) {
        if (other.getClass() != Movie.class) {
            return 0;
        }
        return -(this._releaseDate.compareTo(other.getReleaseDate()));
    }

}
