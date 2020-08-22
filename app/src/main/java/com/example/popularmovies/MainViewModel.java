package com.example.popularmovies;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.popularmovies.database.AppDatabase;
import com.example.popularmovies.database.FavoriteMovie;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<FavoriteMovie>> favorites;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        favorites = database.favoriteMovieDao().loadAllFavoriteMovies();
    }

    public LiveData<List<FavoriteMovie>> getFavorites() {
        return favorites;
    }
}
