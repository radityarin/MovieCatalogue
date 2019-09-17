package com.radityarin.moviecatalogue;

import android.database.Cursor;

public interface LoadMovieCallbacks {
    void preExecute();
    void postExecute(Cursor movies);
}
