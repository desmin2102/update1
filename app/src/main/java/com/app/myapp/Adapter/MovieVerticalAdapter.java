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
import androidx.viewpager2.widget.ViewPager2;

import com.app.myapp.Activity.MovieDetailsActivity;
import com.app.myapp.Class.Movie;
import com.app.myapp.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class MovieVerticalAdapter extends RecyclerView.Adapter<MovieVerticalAdapter.MovieViewHolder> {

    private List<Movie> movieList;
    private Context context;

    public MovieVerticalAdapter(List<Movie> movieList) {
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieVerticalAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.items_movie_doc,
                viewGroup, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        holder.setImage(movieList.get(position));
        Movie movie = movieList.get(position);
        holder.tvTitle.setText(movie.getTitle());
        holder.tvDuration.setText("Thời lượng: " + movie.getDuration());
        holder.tvShowTime.setText("Khởi chiếu: " + movie.getMovieDateStart());
        //holder.tvGenre.setText("Thể loại: " + movie.getGenre());
        // Bạn có thể thêm các thiết lập khác cho ImageView, Button v.v.
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvDuration, tvShowTime, tvGenre, btnScheduled;
        private ImageView imageViewmv;


        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvShowTime = itemView.findViewById(R.id.tvShowTime);
            //tvGenre = itemView.findViewById(R.id.tvGenre);
            imageViewmv = itemView.findViewById(R.id.imgPoster);
            btnScheduled = itemView.findViewById(R.id.btnScheduled);
        }

        void setImage(final Movie movie) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(60));
            Glide.with(itemView.getContext()) // Sử dụng itemView.getContext() để lấy context
                    .load(movie.getImageUrl())
                    .apply(requestOptions)
                    .into(imageViewmv);

            imageViewmv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), MovieDetailsActivity.class);
                    intent.putExtra("imageMovieUrl", movie.getImageUrl());
                    intent.putExtra("movieId", movie.getId());
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }

}
