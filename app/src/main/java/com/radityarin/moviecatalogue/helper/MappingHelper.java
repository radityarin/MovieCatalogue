package com.radityarin.moviecatalogue.helper;

import android.database.Cursor;

import com.radityarin.moviecatalogue.pojo.Movie;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.radityarin.moviecatalogue.db.DatabaseContract.FavoriteColumns.CATEGORY;
import static com.radityarin.moviecatalogue.db.DatabaseContract.FavoriteColumns.DESCRIPTION;
import static com.radityarin.moviecatalogue.db.DatabaseContract.FavoriteColumns.PHOTO;
import static com.radityarin.moviecatalogue.db.DatabaseContract.FavoriteColumns.RATING;
import static com.radityarin.moviecatalogue.db.DatabaseContract.FavoriteColumns.TITLE;
import static com.radityarin.moviecatalogue.db.DatabaseContract.FavoriteColumns.YEAR;

public class MappingHelper {
    public static ArrayList<Movie> mapCursorToArrayList(Cursor moviesCursor, String category) {
        ArrayList<Movie> moviesList = new ArrayList<>();
        while (moviesCursor.moveToNext()) {
            if ((moviesCursor.getString(moviesCursor.getColumnIndexOrThrow(CATEGORY))).equalsIgnoreCase(category)) {
                int id = moviesCursor.getInt(moviesCursor.getColumnIndexOrThrow(_ID));
                String title = moviesCursor.getString(moviesCursor.getColumnIndexOrThrow(TITLE));
                String year = moviesCursor.getString(moviesCursor.getColumnIndexOrThrow(YEAR));
                String description = moviesCursor.getString(moviesCursor.getColumnIndexOrThrow(DESCRIPTION));
                String rating = moviesCursor.getString(moviesCursor.getColumnIndexOrThrow(RATING));
                String photo = moviesCursor.getString(moviesCursor.getColumnIndexOrThrow(PHOTO));
                String type = moviesCursor.getString(moviesCursor.getColumnIndexOrThrow(CATEGORY));
                moviesList.add(new Movie(id, title, year, description, rating, photo, type));
            }
        }
        return moviesList;
    }
}
