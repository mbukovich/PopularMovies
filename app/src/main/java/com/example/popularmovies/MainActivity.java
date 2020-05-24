package com.example.popularmovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.popularmovies.utilities.MovieJsonUtil;
import com.example.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieOnClickHandler {

    RecyclerView movieRecycler; // our recycler

    MovieAdapter movieAdapter;

    ProgressBar progressBar;

    TextView errorTextView;

    Spinner sortSpinner;

    MovieQueryTask backgroundQueryTask;

    HashMap<String, String[]> dataMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        errorTextView = (TextView) findViewById(R.id.tv_error_message_display);

        // We initialize our spinner that will be used to sort the results and connect it to
        // an Adapter. Also, it will set the listener to the SpinnerActivity class we created
        // at the bottom of this file.
        sortSpinner = (Spinner) findViewById(R.id.spinner_sort);
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_array, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);
        sortSpinner.setOnItemSelectedListener(new SpinnerActivity());

        movieRecycler = (RecyclerView) findViewById(R.id.rv_movieGrid); // finding our recycler view

        movieAdapter = new MovieAdapter(this);

        movieRecycler.setAdapter(movieAdapter);
        movieRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        movieRecycler.setHasFixedSize(true);

        String sortSelection = getString(R.string.popular_path);
        backgroundQueryTask = new MovieQueryTask();
        backgroundQueryTask.execute(sortSelection);
    }

    @Override
    public void onClick(int itemNumber) {
        // We get the data to send to the Movie Detail Activity from our HashMap and store it into
        // a String array
        String[] details = new String[6];
        details[0] = dataMap.get(this.getResources().getString(R.string.map_poster))[itemNumber];
        details[1] = dataMap.get(this.getResources().getString(R.string.map_id))[itemNumber];
        details[2] = dataMap.get(this.getResources().getString(R.string.map_release))[itemNumber];
        details[3] = dataMap.get(this.getResources().getString(R.string.map_synopsis))[itemNumber];
        details[4] = dataMap.get(this.getResources().getString(R.string.map_vote_avg))[itemNumber];
        details[5] = dataMap.get(this.getResources().getString(R.string.map_title))[itemNumber];

        // Now we create the intent and pass the String array to the Movie Detail Activity
        Intent detailIntent = new Intent(MainActivity.this, MovieDetailActivity.class);
        detailIntent.putExtra("array",details);
        startActivity(detailIntent);
    }

    public class MovieQueryTask extends AsyncTask<String, Void, HashMap<String, String[]>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            errorTextView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected HashMap<String, String[]> doInBackground(String... params) {
            // We build our URL first
            URL url = NetworkUtils.buildSearchUrl(MainActivity.this, params[0]);

            // Now we make the connection and parse the Json data in a try block
            try {
                String movieData = NetworkUtils.getResponseFromHttpUrl(url);

                // We add get a HashMap containing all the movie details and return it
                return MovieJsonUtil.getMovieDataFromJson(MainActivity.this, movieData);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(HashMap<String, String[]> map) {
            progressBar.setVisibility(View.INVISIBLE);
            if (map.isEmpty()) {
                // This block executes to display an error message if no data was retrieved
                errorTextView.setText(MainActivity.this.getResources().getString(R.string.error_no_data));
                errorTextView.setVisibility(View.VISIBLE);
            }
            movieAdapter.setPosters(map.get(MainActivity.this.getResources().getString(R.string.map_poster)));
            dataMap = map;
            super.onPostExecute(map);
        }
    }

    public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
            backgroundQueryTask.cancel(true); // to prevent many AsyncTasks from being spammed in
            if (pos == 0) {
                backgroundQueryTask = new MovieQueryTask();
                backgroundQueryTask.execute(MainActivity.this.getResources().getString(R.string.popular_path));
            }
            else {
                backgroundQueryTask = new MovieQueryTask();
                backgroundQueryTask.execute(MainActivity.this.getResources().getString(R.string.rating_path));
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            errorTextView.setText(R.string.error_nothing_selected);
            errorTextView.setVisibility(View.VISIBLE);
        }
    }
}
