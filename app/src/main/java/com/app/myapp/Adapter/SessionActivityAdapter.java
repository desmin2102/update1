package com.app.myapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.myapp.Class.Movie;
import com.app.myapp.Class.MovieSession;
import com.app.myapp.Class.Room;
import com.app.myapp.R;
import java.util.List;
import java.util.Map;

public class SessionActivityAdapter extends RecyclerView.Adapter<SessionActivityAdapter.MovieViewHolder> {

    private List<Movie> movies;
    private Map<String, List<MovieSession>> movieSessionsMap;
    private Map<String, Room> roomMap;
    private Context context;

    public SessionActivityAdapter(List<Movie> movies, Map<String, List<MovieSession>> movieSessionsMap, Map<String, Room> roomMap, Context context) {
        this.movies = movies;
        this.movieSessionsMap = movieSessionsMap;
        this.roomMap = roomMap;
        this.context = context;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_session, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        Log.d("SessionAdapter", "Movie title: " + movie.getTitle());
        holder.movieNameTextView.setText(movie.getTitle());

        List<MovieSession> movieSessions = movieSessionsMap.get(movie.getId());
        if (movieSessions != null) {
            Log.d("SessionAdapter", "Movie sessions for " + movie.getTitle() + ": " + movieSessions.size());
            MovieSessionAdapter movieSessionAdapter = new MovieSessionAdapter(movieSessions, roomMap, context);
            holder.movieSessionRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.movieSessionRecyclerView.setAdapter(movieSessionAdapter);
        } else {
            Log.d("SessionAdapter", "No movie sessions found for " + movie.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView movieNameTextView;
        RecyclerView movieSessionRecyclerView;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            movieNameTextView = itemView.findViewById(R.id.movieName);
            movieSessionRecyclerView = itemView.findViewById(R.id.movieSessionRecyclerView);
        }
    }
}
