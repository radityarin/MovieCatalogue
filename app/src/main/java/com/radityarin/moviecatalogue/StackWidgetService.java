package com.radityarin.moviecatalogue;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.radityarin.moviecatalogue.pojo.Movie;

import java.util.ArrayList;
import java.util.Objects;

import static android.provider.BaseColumns._ID;
import static com.radityarin.moviecatalogue.db.DatabaseContract.FavoriteColumns.CATEGORY;
import static com.radityarin.moviecatalogue.db.DatabaseContract.FavoriteColumns.CONTENT_URI;
import static com.radityarin.moviecatalogue.db.DatabaseContract.FavoriteColumns.DESCRIPTION;
import static com.radityarin.moviecatalogue.db.DatabaseContract.FavoriteColumns.PHOTO;
import static com.radityarin.moviecatalogue.db.DatabaseContract.FavoriteColumns.RATING;
import static com.radityarin.moviecatalogue.db.DatabaseContract.FavoriteColumns.TITLE;
import static com.radityarin.moviecatalogue.db.DatabaseContract.FavoriteColumns.YEAR;

public class StackWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext());
    }
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final ArrayList<Movie> mWidgetItems = new ArrayList<>();
    private final Context mContext;
    private Cursor cursor;

    StackRemoteViewsFactory(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        cursor = mContext.getContentResolver().query(CONTENT_URI, null, null, null, null);
        for (int i = 0; i < Objects.requireNonNull(cursor).getCount(); i++) {
            cursor.moveToNext();
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(TITLE));
            String year = cursor.getString(cursor.getColumnIndexOrThrow(YEAR));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION));
            String rating = cursor.getString(cursor.getColumnIndexOrThrow(RATING));
            String photo = cursor.getString(cursor.getColumnIndexOrThrow(PHOTO));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(CATEGORY));
            mWidgetItems.add(new Movie(id, title, year, description, rating, photo, type));
            Log.d("cek", title);
        }
    }

    @Override
    public void onDataSetChanged() {
        final long identityToken = Binder.clearCallingIdentity();
        cursor = mContext.getContentResolver().query(CONTENT_URI, null, null,
                null, null);
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        mWidgetItems.clear();
    }

    @Override
    public int getCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {


        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        rv.setTextViewText(R.id.tv_titlewidget, mWidgetItems.get(position).getTitle());

        try {
            String URL_IMAGE = "http://image.tmdb.org/t/p/w185";
            Bitmap bitmap = Glide.with(mContext)
                    .asBitmap()
                    .load(URL_IMAGE + mWidgetItems.get(position).getPhoto())
                    .submit(512, 512)
                    .get();

            rv.setImageViewBitmap(R.id.iv_photowidget, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bundle extras = new Bundle();
        extras.putInt(FavoriteMovieWidget.EXTRA_ITEM, position);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);

        rv.setOnClickFillInIntent(R.id.iv_photowidget, fillInIntent);
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}

