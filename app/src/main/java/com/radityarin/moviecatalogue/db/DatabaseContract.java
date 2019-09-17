package com.radityarin.moviecatalogue.db;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseContract {
    public static final String AUTHORITY = "com.radityarin.moviecatalogue";
    private static final String SCHEME = "content";

    public static final class FavoriteColumns implements BaseColumns {
        public static final String TABLE_FAVORITE = "favorite";
        public static final String TITLE = "title";
        public static final String YEAR = "year";
        public static final String DESCRIPTION = "description";
        public static final String RATING = "rating";
        public static final String PHOTO = "photo";
        public static final String CATEGORY = "category";

        public static final Uri CONTENT_URI = new Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_FAVORITE)
                .build();
    }

    public static String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }
    public static int getColumnInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndex(columnName));
    }
}