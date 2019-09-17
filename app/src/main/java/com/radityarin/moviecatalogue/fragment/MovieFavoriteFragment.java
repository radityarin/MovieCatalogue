package com.radityarin.moviecatalogue.fragment;


import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.radityarin.moviecatalogue.LoadMovieCallbacks;
import com.radityarin.moviecatalogue.R;
import com.radityarin.moviecatalogue.adapter.MovieAdapter;
import com.radityarin.moviecatalogue.db.MovieHelper;
import com.radityarin.moviecatalogue.pojo.Movie;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

import static com.radityarin.moviecatalogue.db.DatabaseContract.FavoriteColumns.CONTENT_URI;
import static com.radityarin.moviecatalogue.helper.MappingHelper.mapCursorToArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFavoriteFragment extends Fragment implements LoadMovieCallbacks {

    private MovieAdapter adapter;
    private MovieHelper movieHelper;
    private ProgressBar progressBar;
    private TextView tvInfo;
    private static final String EXTRA_STATE = "EXTRA_STATE";
    private static final String TYPE = "movie";

    public MovieFavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_movie_favorite, container, false);

        tvInfo = view.findViewById(R.id.tv_info);
        progressBar = view.findViewById(R.id.progressBar);
        showLoading(true);
        movieHelper = MovieHelper.getInstance(getActivity());
        movieHelper.open();

        HandlerThread handlerThread = new HandlerThread("DataObserver");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        DataObserver myObserver = new DataObserver(handler, Objects.requireNonNull(getActivity()).getApplicationContext());
        getActivity().getApplicationContext().getContentResolver().registerContentObserver(CONTENT_URI, true, myObserver);

        adapter = new MovieAdapter();
        adapter.notifyDataSetChanged();
        RecyclerView rvMovies = view.findViewById(R.id.rv_movies);
        rvMovies.setNestedScrollingEnabled(true);
        rvMovies.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvMovies.setAdapter(adapter);
        if (savedInstanceState == null) {
            new LoadMoviesAsync(getContext(), this).execute();
        } else {
            ArrayList<Movie> list = savedInstanceState.getParcelableArrayList(EXTRA_STATE);
            if (list != null) {
                adapter.setListMovie(list);
            }
        }
        return view;
    }

    private void showLoading(Boolean state) {
        if (state) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void preExecute() {
        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
        @Override
        public void run() {
            progressBar.setVisibility(View.VISIBLE);
        }
    });
    }

    @Override
    public void postExecute(Cursor movies) {
        showLoading(false);
        ArrayList<Movie> listMovies = mapCursorToArrayList(movies,TYPE);
        if (listMovies.size() > 0) {
            adapter.setListMovie(listMovies);
        } else {
            tvInfo.setVisibility(View.VISIBLE);
            adapter.setListMovie(new ArrayList<Movie>());
        }
    }

    private static class LoadMoviesAsync extends AsyncTask<Void, Void, Cursor> {
        private final WeakReference<Context> weakContext;
        private final WeakReference<LoadMovieCallbacks> weakCallback;

        private LoadMoviesAsync(Context context, LoadMovieCallbacks loadMovieCallbacks) {
            weakContext = new WeakReference<>(context);
            weakCallback = new WeakReference<>(loadMovieCallbacks);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakCallback.get().preExecute();
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            Context context = weakContext.get();
            return context.getContentResolver().query(CONTENT_URI, null, null, null, null);
        }

        @Override
        protected void onPostExecute(Cursor movies) {
            super.onPostExecute(movies);
            weakCallback.get().postExecute(movies);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_STATE, adapter.getListMovie());
    }

    @Override
    public void onDestroyView() {
        movieHelper.close();
        super.onDestroyView();
    }

    static class DataObserver extends ContentObserver {

        final Context context;

        DataObserver(Handler handler, Context context) {
            super(handler);
            this.context = context;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            new LoadMoviesAsync(context, (LoadMovieCallbacks) context).execute();
        }
    }
}
