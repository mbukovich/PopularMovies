package com.example.popularmovies.utilities;

import android.content.Context;

import com.example.popularmovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

public class MovieJsonUtil {

    public static HashMap<String, String[]> getMovieDataFromJson(Context context, String movieJsonString)
            throws JSONException {
        // We create a Json object in order to parse the data
        JSONObject movieJsonObj = new JSONObject(movieJsonString);

        // We check for errors. If there is an error, the json data will not have "results"
        if (!movieJsonObj.has("results")) {
            return null;
        }

        // We create the HashMap with string and string array that we will return
        HashMap<String, String[]> result = new HashMap<String, String[]>();

        // We get a JSONArray of JSONObjects
        JSONArray movieArray = movieJsonObj.getJSONArray("results");
        int arraySize = movieArray.length();

        // We create String arrays to contain the details of all our Movie items
        String[] poster = new String[arraySize];
        String[] id = new String[arraySize];
        String[] releaseDate = new String[arraySize];
        String[] synopsis = new String[arraySize];
        String[] voteAverage = new String[arraySize];
        String[] title = new String[arraySize];

        // Now we loop through the results
        for (int i = 0; i < arraySize; i++) {
            poster[i] = movieArray.getJSONObject(i).getString(context.getResources().getString(R.string.map_poster));
            id[i] = movieArray.getJSONObject(i).getString(context.getResources().getString(R.string.map_id));
            releaseDate[i] = movieArray.getJSONObject(i).getString(context.getResources().getString(R.string.map_release));
            synopsis[i] = movieArray.getJSONObject(i).getString(context.getResources().getString(R.string.map_synopsis));
            voteAverage[i] = movieArray.getJSONObject(i).getString(context.getResources().getString(R.string.map_vote_avg));
            title[i] = movieArray.getJSONObject(i).getString(context.getResources().getString(R.string.map_title));
        }
        result.put(context.getResources().getString(R.string.map_poster), poster);
        result.put(context.getResources().getString(R.string.map_id), id);
        result.put(context.getResources().getString(R.string.map_release), releaseDate);
        result.put(context.getResources().getString(R.string.map_synopsis), synopsis);
        result.put(context.getResources().getString(R.string.map_vote_avg), voteAverage);
        result.put(context.getResources().getString(R.string.map_title), title);

        return result;
    }

    public static String[] getVideoDataFromJson(String jsonString) throws JSONException {
        // We create a Json object in order to parse the data
        JSONObject videoJsonObj = new JSONObject(jsonString);

        // We check for errors. If there is an error, the json data will not have "results"
        if (!videoJsonObj.has("results")) {
            return null;
        }

        // We get a JSON Array of JSON Objects
        JSONArray videoArray = videoJsonObj.getJSONArray("results");
        int arraySize = videoArray.length();

        // Now we create a String array to store our data
        String[] results = new String[arraySize];

        // Now we loop through the results
        for (int i = 0; i < arraySize; i++)
            results[i] = videoArray.getJSONObject(i).getString("key");

        return results;
    }

    public static String[][] getReviewDataFromJson(String jsonString) throws JSONException {
        // We create a Json object in order to parse the data
        JSONObject reviewJsonObj = new JSONObject(jsonString);

        // We check for errors. If there is an error, the json data will not have "results"
        if (!reviewJsonObj.has("results")) {
            return null;
        }

        // We get a JSON Array of JSON Objects
        JSONArray reviewArray = reviewJsonObj.getJSONArray("results");
        int arraySize = reviewArray.length();

        // Now we create a String array to store our data
        String[][] results = new String[2][arraySize];

        // Now we loop through the results
        for (int i = 0; i < arraySize; i++) {
            results[0][i] = reviewArray.getJSONObject(i).getString("author");
            results[1][i] = reviewArray.getJSONObject(i).getString("content");
        }

        return results;
    }
}
