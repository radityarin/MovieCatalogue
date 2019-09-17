package com.radityarin.moviecatalogue.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.os.ConfigurationCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.radityarin.moviecatalogue.MainViewModel;
import com.radityarin.moviecatalogue.R;
import com.radityarin.moviecatalogue.adapter.MovieAdapter;
import com.radityarin.moviecatalogue.pojo.Movie;

import java.util.ArrayList;
import java.util.Locale;

public class TVShowFragment extends Fragment implements SearchView.OnQueryTextListener, View.OnClickListener {

    private MovieAdapter adapter;
    private ProgressBar progressBar;
    private MainViewModel mainViewModel;
    private final String type = "tv";
    private Button btn_popular, btn_toprated;

    public TVShowFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_tvshow, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        SearchView searchView = view.findViewById(R.id.sv_tvshow);
        searchView.setOnQueryTextListener(this);

        btn_popular = view.findViewById(R.id.btn_popular);
        btn_toprated = view.findViewById(R.id.btn_toprated);
        btn_popular.setOnClickListener(this);
        btn_toprated.setOnClickListener(this);
        btn_popular.setBackgroundResource(R.drawable.bg_button_on);

        showLoading(true);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.setMovies(type, getCurrentLanguage(),"popular");
        mainViewModel.getMovies().observe(this, getMovies);

        adapter = new MovieAdapter();
        adapter.notifyDataSetChanged();

        RecyclerView recyclerView = view.findViewById(R.id.rv_tvshow);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
        return view;
    }

    private final Observer<ArrayList<Movie>> getMovies = new Observer<ArrayList<Movie>>() {
        @Override
        public void onChanged(ArrayList<Movie> movies) {
            if (movies != null) {
                adapter.setData(movies);
                showLoading(false);
            }
        }
    };

    private void showLoading(Boolean state) {
        if (state) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private String getCurrentLanguage() {
        Locale current = ConfigurationCompat.getLocales(getResources().getConfiguration()).get(0);
        if (current.getLanguage().equals("in")) {
            return "id";
        }
        return current.getLanguage();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mainViewModel.searchMovies(newText,type,getCurrentLanguage());
        mainViewModel.getMovies().observe(this, getMovies);
        return false;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_popular:
                adapter.clearData();
                showLoading(true);
                btn_popular.setBackgroundResource(R.drawable.bg_button_on);
                btn_toprated.setBackgroundResource(R.drawable.bg_button);
                mainViewModel.setMovies(type, getCurrentLanguage(),"popular");
                mainViewModel.getMovies().observe(this, getMovies);
                break;
            case R.id.btn_toprated:
                adapter.clearData();
                showLoading(true);
                btn_toprated.setBackgroundResource(R.drawable.bg_button_on);
                btn_popular.setBackgroundResource(R.drawable.bg_button);
                mainViewModel.setMovies(type, getCurrentLanguage(),"top_rated");
                mainViewModel.getMovies().observe(this, getMovies);
                break;
        }
    }
}
