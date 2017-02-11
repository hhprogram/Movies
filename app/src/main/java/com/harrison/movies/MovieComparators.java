package com.harrison.movies;

import java.util.Comparator;

/**
 * An enum class where each element is a comparator, allows me to not have to create different
 * classes for each type of comparator if I want to compare Movie objects by attributes other than
 * release date (their natural ordering). Can be used:
 *  Collections.sort(<iterable of comparable objects>, <one of the comparators below>)
 *  MovieComparators.TitleComparator etc...
 *
 * per advice of : http://stackoverflow.com/questions/27784735/class-to-store-multiple-comparators
 * Created by harrison on 1/4/17.
 */

public enum MovieComparators implements Comparator<Movie>{
    TitleComparator {
        @Override
        public int compare(Movie m1, Movie m2) {
            return m1.getTitle().compareTo(m2.getTitle());
        }
    },

    //element in this enum class compares votes with highest value being first
    VoteComparator {
        @Override
        public int compare(Movie m1, Movie m2) {
            return -(m1.getVoteAvg().compareTo(m2.getVoteAvg()));
        }
    }
}
