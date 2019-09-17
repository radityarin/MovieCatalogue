package com.radityarin.moviecatalogue.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.radityarin.moviecatalogue.db.MovieHelper;
import static com.radityarin.moviecatalogue.db.DatabaseContract.AUTHORITY;
import static com.radityarin.moviecatalogue.db.DatabaseContract.FavoriteColumns.CONTENT_URI;
import static com.radityarin.moviecatalogue.db.DatabaseContract.FavoriteColumns.TABLE_FAVORITE;

public class MovieProvider extends ContentProvider {

    private static final int MOVIE = 1;
    private static final int MOVIE_ID = 2;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private MovieHelper movieHelper;

    static {
        sUriMatcher.addURI(AUTHORITY, TABLE_FAVORITE, MOVIE);
        sUriMatcher.addURI(AUTHORITY, TABLE_FAVORITE + "/#", MOVIE_ID);
    }

    @Override
    public boolean onCreate() {
        movieHelper = MovieHelper.getInstance(getContext());
        return true;    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        movieHelper.open();
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                cursor = movieHelper.queryProvider();
                break;
            case MOVIE_ID:
                cursor = movieHelper.queryByIdProvider(uri.getLastPathSegment());
                break;
            default:
                cursor = null;
                break;
        }
        return cursor;    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        movieHelper.open();
        long added;
        if (sUriMatcher.match(uri) == MOVIE) {
            added = movieHelper.insertProvider(contentValues);
        } else {
            added = 0;
        }
        return Uri.parse(CONTENT_URI + "/" + added);
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        movieHelper.open();
        int updated;
        if (sUriMatcher.match(uri) == MOVIE_ID) {
            updated = movieHelper.updateProvider(uri.getLastPathSegment(), contentValues);
        } else {
            updated = 0;
        }
        return updated;
    }

    @Override
    public int delete(@NonNull Uri uri, String s, String[] strings) {
        movieHelper.open();
        int deleted;
        if (sUriMatcher.match(uri) == MOVIE_ID) {
            deleted = movieHelper.deleteProvider(uri.getLastPathSegment());
        } else {
            deleted = 0;
        }
        return deleted;
    }
}
