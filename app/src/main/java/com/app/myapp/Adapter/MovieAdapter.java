package com.app.myapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.app.myapp.Activity.MovieDetailsActivity;
import com.app.myapp.Class.Movie;
import com.app.myapp.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private List<Movie> movieList;
    private Context context;
    private ViewPager2 viewPager2;

    public MovieAdapter(List<Movie> movieList, ViewPager2 viewPager2) {
        this.movieList = movieList;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        return new MovieViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.item_movie, viewGroup, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.MovieViewHolder movieViewHolder, int i) {
        movieViewHolder.setImage(movieList.get(i));
        if (i == movieList.size() - 3) {
            viewPager2.post(movieViewHolder.runnable);
        }
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewmv;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageViewmv = itemView.findViewById(R.id.movie_item_image);
        }

        void setImage(final Movie movie) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(60));
            Glide.with(context)
                    .load(movie.getImageUrl())
                    .apply(requestOptions)
                    .into(imageViewmv);

            imageViewmv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MovieDetailsActivity.class);
                    intent.putExtra("imageMovieUrl", movie.getImageUrl());
                    intent.putExtra("movieId", movie.getId());
                    context.startActivity(intent);
                }
            });
        }

        private Runnable runnable = new Runnable() {
            @Override
            public void run() {
                movieList.addAll(movieList);
                notifyDataSetChanged();
            }
        };
    }
}
