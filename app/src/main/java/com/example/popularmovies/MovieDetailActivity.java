package com.example.popularmovies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.popularmovies.database.AppDatabase;
import com.example.popularmovies.database.FavoriteMovie;
import com.example.popularmovies.utilities.MovieJsonUtil;
import com.example.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

public class MovieDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<String> {

    ImageView posterView;

    TextView titleView;
    TextView releaseDateView;
    TextView ratingView;
    TextView synopsisView;
    TextView reviewView;

    Button favoriteButton;

    LinearLayout trailerLinearView;

    private Boolean isFavorite;
    private String[] movieData;
    private String[] trailerKeys;

    private AppDatabase mDb;

    private static final int VIDEO_QUERY_LOADER = 42;
    private static final int REVIEW_QUERY_LOADER = 43;
    private static final String QUERY_URL = "query";
    private static final String TRAILER_KEYS_ARRAY = "keys";
    private static final String REVIEW_TEXT = "reviews";
    private static final String MOVIE_DATA_ARRAY = "movieData";
    private static final String IS_IT_A_FAVORITE = "isFavorite";

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
        reviewView = (TextView) findViewById(R.id.tv_reviews);
        trailerLinearView = (LinearLayout) findViewById(R.id.ll_trailers);

        // If we are returning to onCreate after a life cycle change (such as rotating the device),
        // then we use data from savedInstanceState to refill the views
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MOVIE_DATA_ARRAY)) {
                // fill data from MOVIE_DATA_ARRAY
                movieData = savedInstanceState.getStringArray(MOVIE_DATA_ARRAY);
                if (movieData != null && movieData[0] != null) {
                    URL imageURL = NetworkUtils.buildPosterUrl(this, movieData[0]);
                    Picasso.get().load(imageURL.toString()).fit().centerInside().into(posterView);
                }

                if (movieData != null) {
                    releaseDateView.setText(movieData[2]);
                    synopsisView.setText(movieData[3]);
                    ratingView.setText(movieData[4]);
                    titleView.setText(movieData[5]);
                }

                // create a reference to our favorites database for adding or removing entries
                mDb = AppDatabase.getInstance(getApplicationContext());
            }
            if (savedInstanceState.containsKey(TRAILER_KEYS_ARRAY)) {
                // create trailer buttons
                buildTrailerButtons(savedInstanceState.getStringArray(TRAILER_KEYS_ARRAY));
            }
            if (savedInstanceState.containsKey(REVIEW_TEXT)) {
                // fill the review textView with text
                reviewView.setText(savedInstanceState.getString(REVIEW_TEXT));
            }
            if (savedInstanceState.containsKey(IS_IT_A_FAVORITE))  {
                isFavorite = savedInstanceState.getBoolean(IS_IT_A_FAVORITE);

                // Here we set up the add/remove favorite button
                favoriteButton.setVisibility(View.VISIBLE);
                if (isFavorite) {
                    favoriteButton.setText(this.getResources().getString(R.string.unfavorite));
                }
                else {
                    favoriteButton.setText(this.getResources().getString(R.string.addfavorite));
                }
            }
        }
        else {
            // We get the intent that was used to start this activity
            Intent intent = getIntent();

            // Now we make sure that the intent contains data and if so, pass that data to the views in the layout
            if (intent.hasExtra("array") && intent.getStringArrayExtra("array") != null) {
                movieData = intent.getStringArrayExtra("array");
                if (movieData != null && movieData[0] != null) {
                    URL imageURL = NetworkUtils.buildPosterUrl(this, movieData[0]);
                    Picasso.get().load(imageURL.toString()).fit().centerInside().into(posterView);
                }

                if (movieData != null) {
                    if (Objects.equals(movieData[6], "false")) isFavorite = false;
                    else if (Objects.equals(movieData[6], "true")) isFavorite = true;
                }

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

                // start an Async Task Loader to query the API to get video trailers
                Bundle videoBundle = new Bundle();
                videoBundle.putString(QUERY_URL, NetworkUtils.buildVideosUrl(this, movieData[1]).toString());
                LoaderManager.getInstance(this).initLoader(VIDEO_QUERY_LOADER, videoBundle, this).forceLoad();
                // start an Async Task Loader to query the API to get video reviews
                Bundle reviewBundle = new Bundle();
                reviewBundle.putString(QUERY_URL, NetworkUtils.buildReviewsUrl(this, movieData[1]).toString());
                LoaderManager.getInstance(this).initLoader(REVIEW_QUERY_LOADER, reviewBundle, this).forceLoad();

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
            isFavorite = false;
        }
        else {
            final FavoriteMovie favoriteMovie = new FavoriteMovie(Integer.parseInt(movieData[1]),
                    movieData[5], movieData[2], Float.parseFloat(movieData[4]),
                    movieData[3], movieData[0]);
            AppExecuters.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.favoriteMovieDao().insertFavoriteMovie(favoriteMovie);
                }
            });
            favoriteButton.setText(this.getResources().getString(R.string.unfavorite));
            isFavorite = true;
        }
    }

    private String prepareReviews(String[][] reviewStrings) {
        // build a String array into a continuous String to put into a textView
        String result = "";
        if (reviewStrings != null) {
            for (int i = 0; i < reviewStrings[0].length; i++) {
                result = result + "\"" + reviewStrings[1][i] + "\" \n" + "author: " + reviewStrings[0][i] + "\n \n";
            }
        }
        return result;
    }

    private void buildTrailerButtons(String[] trailerStrings) {
        if (trailerStrings != null) {
            // build trailer buttons if the incoming array is not null
            for (int i = 0; i < trailerStrings.length; i++) {
                // builds a youtube URL and creates an intent that opens it
                // also inserts a button into the ListView for each trailer
                final URL trailerURL = NetworkUtils.buildTrailerURL(this, trailerStrings[i]);

                Button trailerButton = new Button(this);
                String btnText = "Trailer " + Integer.toString(i + 1);
                trailerButton.setText(btnText);
                trailerButton.setPadding(5, 20, 5, 20);
                trailerButton.setTextSize(15);
                trailerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(trailerURL.toString());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        if (intent.resolveActivity(getPackageManager()) != null) startActivity(intent);
                    }
                });
                trailerLinearView.addView(trailerButton);
            }
            if (trailerStrings.length == 0) {
                // if there are no trailer keys, then we let the user know
                TextView textView = new TextView(this);
                textView.setText(R.string.no_trailers);
                textView.setPadding(5, 20, 5, 20);
                trailerLinearView.addView(textView);
            }
        }
        else {
            TextView textView = new TextView(this);
            textView.setText(R.string.no_trailers);
            textView.setPadding(5, 20, 5, 20);
            trailerLinearView.addView(textView);
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
                if (args != null && args.containsKey(QUERY_URL)) {
                    if (args.getString(QUERY_URL) != null) {
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
                    else return null;
                }
                else return null;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        int id = loader.getId();
        if (id == VIDEO_QUERY_LOADER) {
            // parse the JSON data and load it into the activity by adding buttons to the listView
            // as long as the string from the API query contains data
            if (data != null) {
                try {
                    trailerKeys = MovieJsonUtil.getVideoDataFromJson(data);
                    buildTrailerButtons(trailerKeys);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                TextView textView = new TextView(this);
                textView.setText(R.string.no_trailers);
                textView.setPadding(5, 20, 5, 20);
                trailerLinearView.addView(textView);
            }
        }
        else if (id == REVIEW_QUERY_LOADER) {
            // parse the JSON data and load it into the reviewView text view
            // as long as the string from the API query contains data
            if (data != null) {
                try {
                    String[][] reviewData = MovieJsonUtil.getReviewDataFromJson(data);
                    String reviews = prepareReviews(reviewData);
                    if (Objects.equals(reviews, "")) reviewView.setText(R.string.no_reviews);
                    else reviewView.setText(reviews);
                } catch (JSONException e) {
                    e.printStackTrace();
                    reviewView.setText(R.string.reviews_error);
                }
            }
            else {
                reviewView.setText(R.string.reviews_error);
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray(TRAILER_KEYS_ARRAY, trailerKeys);
        String text = reviewView.getText().toString();
        outState.putString(REVIEW_TEXT, text);
        outState.putStringArray(MOVIE_DATA_ARRAY, movieData);
        outState.putBoolean(IS_IT_A_FAVORITE, isFavorite);
    }
}
