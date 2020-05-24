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
}
