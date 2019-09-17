package com.radityarin.moviecatalogue;

import com.radityarin.moviecatalogue.pojo.Movie;

import java.util.ArrayList;

public interface ReleaseMovieCallbacks {
    void onSuccess(ArrayList<Movie> movies);
    void onFailure(boolean failure);
}
