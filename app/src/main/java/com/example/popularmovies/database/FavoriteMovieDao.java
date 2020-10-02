package com.example.popularmovies.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FavoriteMovieDao {
    @Query("SELECT * FROM favoriteMovies ORDER BY movieId")
    LiveData<List<FavoriteMovie>> loadAllFavoriteMovies();

    @Insert
    void insertFavoriteMovie(FavoriteMovie favoriteMovie);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFavoriteMovie(FavoriteMovie favoriteMovie);

    @Delete
    void deleteFavoriteMovie(FavoriteMovie favoriteMovie);

    @Query("SELECT * FROM favoriteMovies WHERE movieId = :mId")
    FavoriteMovie loadFavMovieByMovieId(int mId);
}
