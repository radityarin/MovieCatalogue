package com.radityarin.moviecatalogue.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dbmoviecatalogue";
    private static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE_TABLE_NOTE = String.format("CREATE TABLE %s"
                    + " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL)",
            DatabaseContract.FavoriteColumns.TABLE_FAVORITE,
            DatabaseContract.FavoriteColumns._ID,
            DatabaseContract.FavoriteColumns.TITLE,
            DatabaseContract.FavoriteColumns.YEAR,
            DatabaseContract.FavoriteColumns.DESCRIPTION,
            DatabaseContract.FavoriteColumns.RATING,
            DatabaseContract.FavoriteColumns.PHOTO,
            DatabaseContract.FavoriteColumns.CATEGORY
    );

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_NOTE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.FavoriteColumns.TABLE_FAVORITE);
        onCreate(db);
    }
}