package com.app.myapp.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.MediaController;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.myapp.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MovieDetailsActivity extends AppCompatActivity {

    private TextView movieTitleTextView;
    private TextView movieDurationTextView;
    private TextView movieDateStartTextView;
    private TextView movieGenreTextView;
    private TextView movieRatingTextView;
    private TextView movieSummaryTextView;
    private ImageView movieImageView;
    private VideoView movieTrailerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        movieImageView = findViewById(R.id.movie_image);
        movieTitleTextView = findViewById(R.id.movie_title);
        movieDurationTextView = findViewById(R.id.movie_duration);
        movieDateStartTextView = findViewById(R.id.movie_starting);
        movieGenreTextView = findViewById(R.id.movie_genre);
        movieRatingTextView = findViewById(R.id.movie_rating);
        movieSummaryTextView = findViewById(R.id.movie_summary);
        movieTrailerView = findViewById(R.id.movie_trailer);

        String imageMovieUrl = getIntent().getStringExtra("imageMovieUrl");
        Glide.with(this).load(imageMovieUrl).into(movieImageView);

        String trailerUrl = getIntent().getStringExtra("trailerUrl");
        if (trailerUrl != null && !trailerUrl.isEmpty()) {
            movieTrailerView.setVideoURI(Uri.parse(trailerUrl));
            movieTrailerView.setMediaController(new MediaController(this));
            movieTrailerView.requestFocus();
            movieTrailerView.start();
        } else {
            movieTitleTextView.setText("URL video không hợp lệ.");
        }

        // Lấy ID phim được truyền qua Intent
        String movieId = getIntent().getStringExtra("movieId");

        if (movieId != null) {
            fetchMovieDetailsFromDatabase(movieId);
        } else {
            movieTitleTextView.setText("Không tìm thấy ID phim.");
        }

        // Sử dụng OnBackPressedDispatcher để xử lý nút back
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void fetchMovieDetailsFromDatabase(String movieId) {
        DatabaseReference databaseReferenceMovie = FirebaseDatabase.getInstance().getReference("Movie").child(movieId);
        databaseReferenceMovie.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String title = dataSnapshot.child("title").getValue(String.class);
                String duration = dataSnapshot.child("duration").getValue(String.class);
                String dateStart = dataSnapshot.child("movieDateStart").getValue(String.class);
                String genre = dataSnapshot.child("genre").getValue(String.class);
                String rating=dataSnapshot.child("rating").getValue(String.class);
                String summary = dataSnapshot.child("summary").getValue(String.class);
                String trailerUrl = dataSnapshot.child("trailerUrl").getValue(String.class);
                String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);

                if (title != null) {
                    movieTitleTextView.setText(title);
                }
                if (duration != null) {
                    movieDurationTextView.setText(duration);
                }
                if (dateStart != null) {
                    movieDateStartTextView.setText(dateStart);
                }

                if (genre != null) {
                    movieGenreTextView.setText(genre);
                }
                if (rating != null) {
                    movieRatingTextView.setText(rating);
                }
                if (summary != null) {
                    movieSummaryTextView.setText(summary);
                }
                if (trailerUrl != null) {
                    movieTrailerView.setVideoURI(Uri.parse(trailerUrl));
                    movieTrailerView.setMediaController(new MediaController(MovieDetailsActivity.this));
                    movieTrailerView.requestFocus();
                    movieTrailerView.start();
                }
                if (imageUrl != null) {
                    Glide.with(MovieDetailsActivity.this).load(imageUrl).into(movieImageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                movieTitleTextView.setText("Lỗi: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
