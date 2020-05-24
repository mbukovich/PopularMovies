package com.example.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    String[] posters;
    Context context;
    final private MovieOnClickHandler movieOnClickHandler;

    public interface MovieOnClickHandler {
        void onClick(int itemNumber);
    }

    public MovieAdapter(MovieOnClickHandler handler) {
        movieOnClickHandler = handler;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_layout_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        URL imageUrl = NetworkUtils.buildPosterUrl(context, posters[position]);
        Picasso.get().load(imageUrl.toString()).fit().centerInside().into(holder.poster);
    }

    public void setPosters(String[] posterData) {
        posters = posterData;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (posters == null)
            return 0;
        return posters.length;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView poster;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.grid_poster_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            movieOnClickHandler.onClick(getAdapterPosition());
        }
    }
}
