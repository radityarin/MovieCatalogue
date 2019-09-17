package com.radityarin.moviecatalogue.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.radityarin.moviecatalogue.fragment.FavoriteFragment;
import com.radityarin.moviecatalogue.fragment.MoviesFragment;
import com.radityarin.moviecatalogue.R;
import com.radityarin.moviecatalogue.fragment.TVShowFragment;

public class MainActivity extends AppCompatActivity {

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_movies:
                    fragment = new MoviesFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment, fragment.getClass().getSimpleName()).commit();
                    return true;
                case R.id.navigation_tvshow:
                    fragment = new TVShowFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment, fragment.getClass().getSimpleName()).commit();
                    return true;
                case R.id.navigation_favorite:
                    fragment = new FavoriteFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment, fragment.getClass().getSimpleName()).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (savedInstanceState == null) {
            navView.setSelectedItemId(R.id.navigation_movies);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_change_settings) {
            Intent mIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
            startActivity(mIntent);
        } else if (item.getItemId() == R.id.action_settings) {
            Intent mIntent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(mIntent);
        }
        return true;
    }

}
