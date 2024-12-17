package com.app.myapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.myapp.Activity.MovieDetailsActivity;
import com.app.myapp.Class.Movie;
import com.app.myapp.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import java.util.List;

public class MovieActivityAdapter extends RecyclerView.Adapter<MovieActivityAdapter.MovieViewHolder> {
    private List<Movie> movieList;
    private Context context;

    public MovieActivityAdapter(List<Movie> movieList) {
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieActivityAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        return new MovieViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.movie_activity_item, viewGroup, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieActivityAdapter.MovieViewHolder movieViewHolder, int i) {
        movieViewHolder.setDetails(movieList.get(i));
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewmv;
        private TextView textViewTitle;
        private TextView textViewDuration;
        private TextView textViewStartDate;
        private TextView textViewGenre;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageViewmv = itemView.findViewById(R.id.imageViewPoster);
            this.textViewTitle = itemView.findViewById(R.id.textViewTitle);
            this.textViewDuration = itemView.findViewById(R.id.textViewDuration);
            this.textViewStartDate = itemView.findViewById(R.id.textViewStartDate);
            this.textViewGenre = itemView.findViewById(R.id.textViewGenre);
        }

        void setDetails(final Movie movie) {
            String imageUrl = movie.getImageUrl();
                RequestOptions requestOptions = new RequestOptions();
                requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(60));
                Glide.with(context)
                        .load(imageUrl)
                        .apply(requestOptions)
                        .into(imageViewmv);


            textViewTitle.setText(movie.getTitle());
            textViewDuration.setText(movie.getDuration());
            textViewStartDate.setText(movie.getMovieDateStart());
            textViewGenre.setText(movie.getGenre());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MovieDetailsActivity.class);
                    intent.putExtra("imageMovieUrl", movie.getImageUrl());
                    intent.putExtra("movieId", movie.getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
