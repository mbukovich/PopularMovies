package com.example.popularmovies.database;

// This is the entity that manages the local database containing information for all movies
// the user has favorited.

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "favoriteMovies")
public class FavoriteMovie {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int movieId;
    private String title;
    private String releaseDate;
    private float rating;
    private String synopsis;
    private String imagePath;

    @Ignore
    public FavoriteMovie(int movieAPIid, String movieTitle, String movieDate, float movieRating, String movieDesc, String movieImage) {
        movieId = movieAPIid;
        title = movieTitle;
        releaseDate = movieDate;
        rating = movieRating;
        synopsis = movieDesc;
        imagePath = movieImage;
    }

    public FavoriteMovie(int dataId, int movieAPIid, String movieTitle, String movieDate, float movieRating, String movieDesc, String movieImage) {
        id = dataId;
        movieId = movieAPIid;
        title = movieTitle;
        releaseDate = movieDate;
        rating = movieRating;
        synopsis = movieDesc;
        imagePath = movieImage;
    }

    public int getId() {return id;}

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) { this.title = title;}

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }
}
