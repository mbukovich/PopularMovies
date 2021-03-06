package com.example.popularmovies.utilities;

import android.content.Context;
import android.net.Uri;

import com.example.popularmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class NetworkUtils {
    public static URL buildSearchUrl(Context c, String popularOrRated){
        Uri builtUri = Uri.parse(c.getResources().getString(R.string.base_url)).buildUpon()
                .appendPath(c.getResources().getString(R.string.movie_path))
                .appendPath(popularOrRated)
                .appendQueryParameter(c.getResources().getString(R.string.key_param), c.getResources().getString(R.string.tmdb_key))
                .appendQueryParameter(c.getResources().getString(R.string.language_param), c.getResources().getString(R.string.language))
                .appendQueryParameter(c.getResources().getString(R.string.page_param), c.getResources().getString(R.string.page))
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildPosterUrl(Context c, String image) {
        Uri buildUri = Uri.parse(c.getResources().getString(R.string.image_base_url)).buildUpon()
                .appendPath(c.getResources().getString(R.string.image_size))
                .appendPath(image.substring(1))
                .build();
        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildVideosUrl(Context c, String movieId){
        Uri builtUri = Uri.parse(c.getResources().getString(R.string.base_url)).buildUpon()
                .appendPath(c.getResources().getString(R.string.movie_path))
                .appendPath(movieId)
                .appendPath(c.getResources().getString(R.string.video_path))
                .appendQueryParameter(c.getResources().getString(R.string.key_param), c.getResources().getString(R.string.tmdb_key))
                .appendQueryParameter(c.getResources().getString(R.string.language_param), c.getResources().getString(R.string.language))
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildReviewsUrl(Context c, String movieId){
        Uri builtUri = Uri.parse(c.getResources().getString(R.string.base_url)).buildUpon()
                .appendPath(c.getResources().getString(R.string.movie_path))
                .appendPath(movieId)
                .appendPath(c.getResources().getString(R.string.review_path))
                .appendQueryParameter(c.getResources().getString(R.string.key_param), c.getResources().getString(R.string.tmdb_key))
                .appendQueryParameter(c.getResources().getString(R.string.language_param), c.getResources().getString(R.string.language))
                .appendQueryParameter(c.getResources().getString(R.string.page_param), c.getResources().getString(R.string.page))
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildTrailerURL(Context c, String trailerKey) {
        Uri builtUri = Uri.parse(c.getResources().getString(R.string.youtube_base_path)).buildUpon()
                .appendPath(c.getResources().getString(R.string.youtube_watch_path))
                .appendQueryParameter(c.getResources().getString(R.string.trailer_query_v), trailerKey)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.connect();

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            if (scanner.hasNext()) {
                String result = scanner.next();
                return result;
            }
            else
                return null;
        } finally {
            urlConnection.disconnect();
        }
    }
}
