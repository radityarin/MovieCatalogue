package com.radityarin.moviecatalogue.activity;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.radityarin.moviecatalogue.R;
import com.radityarin.moviecatalogue.db.MovieHelper;
import com.radityarin.moviecatalogue.pojo.Movie;

import static com.radityarin.moviecatalogue.db.DatabaseContract.FavoriteColumns.TITLE;
import static com.radityarin.moviecatalogue.db.DatabaseContract.FavoriteColumns.YEAR;
import static com.radityarin.moviecatalogue.db.DatabaseContract.FavoriteColumns.DESCRIPTION;
import static com.radityarin.moviecatalogue.db.DatabaseContract.FavoriteColumns.RATING;
import static com.radityarin.moviecatalogue.db.DatabaseContract.FavoriteColumns.PHOTO;
import static com.radityarin.moviecatalogue.db.DatabaseContract.FavoriteColumns.CATEGORY;
import static com.radityarin.moviecatalogue.db.DatabaseContract.FavoriteColumns.CONTENT_URI;


public class DetailActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayout;
    private ImageView movie_photo;
    private TextView movie_title;
    private TextView movie_year;
    private TextView movie_description;
    private MovieHelper movieHelper;
    private TextView movie_rating;
    private Menu menu;
    private Movie movie;
    private final static String URL_IMAGE = "http://image.tmdb.org/t/p/w185";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        constraintLayout = findViewById(R.id.constraintlayout);
        movie = getIntent().getParcelableExtra("movie");
        prepare();
        show();

        movieHelper = MovieHelper.getInstance(getApplicationContext());
        movieHelper.open();
    }

    private void prepare() {
        movie_photo = findViewById(R.id.movie_photo);
        movie_title = findViewById(R.id.movie_title);
        movie_year = findViewById(R.id.movie_year);
        movie_description = findViewById(R.id.movie_description);
        movie_rating = findViewById(R.id.movie_rating);
    }

    private void show() {
        Glide.with(this).load(URL_IMAGE + movie.getPhoto()).into(movie_photo);
        movie_title.setText(movie.getTitle());
        movie_year.setText(movie.getYear());
        movie_description.setText(movie.getDescription());
        movie_rating.setText(movie.getRating());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_menu, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (movieHelper.checkFavourite(movie)) {
            MenuItem settingsItem = menu.findItem(R.id.action_favorite);
            settingsItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_favorite) {
            showAlertDialog();
        }
        return super.onOptionsItemSelected(item);
    }


    private void showAlertDialog() {
        final boolean isFavourite = movieHelper.checkFavourite(movie);
        String dialogTitle, dialogMessage;
        if (isFavourite) {
            dialogMessage = String.format(getResources().getString(R.string.delete_question), movie.getTitle());
            dialogTitle = getResources().getString(R.string.delete);
        } else {
            dialogMessage = String.format(getResources().getString(R.string.favorite_question), movie.getTitle());
            dialogTitle = getResources().getString(R.string.favorite);
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(dialogTitle);
        alertDialogBuilder
                .setMessage(dialogMessage)
                .setCancelable(true)
                .setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (!isFavourite) {
                            ContentValues values = new ContentValues();
                            values.put(TITLE, movie.getTitle());
                            values.put(YEAR, movie.getYear());
                            values.put(DESCRIPTION, movie.getDescription());
                            values.put(RATING, movie.getRating());
                            values.put(PHOTO, movie.getPhoto());
                            values.put(CATEGORY, movie.getType());
                            getContentResolver().insert(CONTENT_URI, values);
                            showSnackbarMessage(String.format(getResources().getString(R.string.favorite_successful), movie.getTitle()));
                            MenuItem settingsItem = menu.findItem(R.id.action_favorite);
                            settingsItem.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite));
                        } else {
                            Uri uri = Uri.parse(CONTENT_URI + "/" + movie.getId());
                            getContentResolver().delete(uri, null, null);
                            showSnackbarMessage(String.format(getResources().getString(R.string.delete_successful), movie.getTitle()));
                            MenuItem settingsItem = menu.findItem(R.id.action_favorite);
                            settingsItem.setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_off));
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        movieHelper.close();
    }

    private void showSnackbarMessage(String message) {
        Snackbar.make(constraintLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DetailActivity.this, MainActivity.class);
        startActivity(intent);
    }
}

