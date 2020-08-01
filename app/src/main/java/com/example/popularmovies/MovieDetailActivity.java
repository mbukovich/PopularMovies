package com.example.popularmovies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class MovieDetailActivity extends AppCompatActivity {

    ImageView posterView;

    TextView titleView;
    TextView releaseDateView;
    TextView ratingView;
    TextView synopsisView;

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

        // We get the intent that was used to start this activity
        Intent intent = getIntent();

        // Now we make sure that the intent contains data and if so, pass that data to the views in the layout
        if (intent.hasExtra("array")) {
            String[] movieData = intent.getStringArrayExtra("array");
            URL imageURL = NetworkUtils.buildPosterUrl(this, movieData[0]);
            // TODO find out if this movie is a favorite and save to a Boolean variable

            // TODO if the movie is a favorite, then load the image from the database instead of from the web
            Picasso.get().load(imageURL.toString()).fit().centerInside().into(posterView);

            // TODO if the movie is a favorite, then update the text of the favorite button to "unfavorite", else set text to "favorite"

            releaseDateView.setText(movieData[2]);
            synopsisView.setText(movieData[3]);
            ratingView.setText(movieData[4]);
            titleView.setText(movieData[5]);

            // TODO if the device has data connectivity, then start an Async Task to query the API to get video trailers
            // TODO if the device has data connectivity, then start an Async Task to query the API to get video reviews

            // TODO add click functionality to the favorite button to add or remove the movie from the database and change color of the button
        }
        else {
            releaseDateView.setText("No Data Found.");
            synopsisView.setText("No Data Found.");
            ratingView.setText("No Data Found.");
            titleView.setText("No Data Found.");

            // TODO hide the favorite button
        }
    }
}
