package com.example.popularmovies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.popularmovies.database.AppDatabase;
import com.example.popularmovies.database.FavoriteMovie;
import com.example.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;

public class MovieDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<String> {

    ImageView posterView;

    TextView titleView;
    TextView releaseDateView;
    TextView ratingView;
    TextView synopsisView;

    Button favoriteButton;

    private Boolean isFavorite;
    private String[] movieData;

    private AppDatabase mDb;

    private static final int VIDEO_QUERY_LOADER = 42;
    private static final int REVIEW_QUERY_LOADER = 43;
    private static final String QUERY_URL = "query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // We find all of the views in our layout
        posterView = (ImageView) findViewById(R.id.detail_poster_image);
        titleView = (TextView) findViewById(R.id.tv_detail_title);
        releaseDateView = (TextView) findViewById(R.id.tv_detail_release_date);
        ratingView = (TextView) findViewById(R.id.tv_detail_voter_rating);
        synopsisView = (TextView) findViewById(R.id.tv_detail_synopsis);
        favoriteButton = (Button) findViewById(R.id.btn_favorite);

        // We get the intent that was used to start this activity
        Intent intent = getIntent();

        // Now we make sure that the intent contains data and if so, pass that data to the views in the layout
        if (intent.hasExtra("array")) {
            movieData = intent.getStringArrayExtra("array");
            URL imageURL = NetworkUtils.buildPosterUrl(this, movieData[0]);

            if (movieData[6] == "false") isFavorite = false;
            else if (movieData[6] == "true") isFavorite = true;

            Picasso.get().load(imageURL.toString()).fit().centerInside().into(posterView);

            // Here we set up the add/remove favorite button
            favoriteButton.setVisibility(View.VISIBLE);
            if (isFavorite) {
                favoriteButton.setText(this.getResources().getString(R.string.unfavorite));
            }
            else {
                favoriteButton.setText(this.getResources().getString(R.string.addfavorite));
            }

            releaseDateView.setText(movieData[2]);
            synopsisView.setText(movieData[3]);
            ratingView.setText(movieData[4]);
            titleView.setText(movieData[5]);

            // TODO if the device has data connectivity, then start an Async Task to query the API to get video trailers
            Bundle videoBundle = new Bundle();
            // TODO if the device has data connectivity, then start an Async Task to query the API to get video reviews

            mDb = AppDatabase.getInstance(getApplicationContext());
        }
        else {
            releaseDateView.setText(this.getResources().getString(R.string.error_no_data));
            synopsisView.setText(this.getResources().getString(R.string.error_no_data));
            ratingView.setText(this.getResources().getString(R.string.error_no_data));
            titleView.setText(this.getResources().getString(R.string.error_no_data));

            favoriteButton.setVisibility(View.INVISIBLE);
        }
    }

    public void clickFavorite(View view) {
        if (isFavorite) {
            AppExecuters.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    FavoriteMovie favoriteMovie = mDb.favoriteMovieDao().loadFavMovieByMovieId(Integer.parseInt(movieData[1]));
                    mDb.favoriteMovieDao().deleteFavoriteMovie(favoriteMovie);
                }
            });
            favoriteButton.setText(this.getResources().getString(R.string.addfavorite));
        }
        else {
            final FavoriteMovie favoriteMovie = new FavoriteMovie(Integer.parseInt(movieData[1]),
                    movieData[5], movieData[2], Float.parseFloat(movieData[4]),
                    movieData[3], movieData[0]);
            AppExecuters.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.favoriteMovieDao().insertFavoriteMovie(favoriteMovie);
                    finish();
                }
            });
            favoriteButton.setText(this.getResources().getString(R.string.unfavorite));
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (args == null) return;
            }

            @Nullable
            @Override
            public String loadInBackground() {
                String queryUrl = args.getString(QUERY_URL);
                if (queryUrl == null || TextUtils.isEmpty(queryUrl)) return null;
                try {
                    URL url = new URL(queryUrl);
                    return NetworkUtils.getResponseFromHttpUrl(url);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        int id = loader.getId();
        if (id == VIDEO_QUERY_LOADER) {
            // do stuff with the video JSON data
        }
        else if (id == REVIEW_QUERY_LOADER) {
            // do stuff with the review JSON data
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}
