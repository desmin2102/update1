package com.app.myapp.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.MediaController;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.myapp.Adapter.ReviewAdapter;
import com.app.myapp.Class.Review;
import com.app.myapp.R;
import com.app.myapp.Activity.ReviewDialogFragment;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
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
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;
    private Button rateReviewButton; // Nút Rate and Review
    private FirebaseUser currentUser;

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
        recyclerViewReviews = findViewById(R.id.recyclerView_comments);
        rateReviewButton = findViewById(R.id.rateReviewButton); // Nút Rate and Review

        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(this, reviewList);
        recyclerViewReviews.setAdapter(reviewAdapter);

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

        // Lấy thông tin người dùng hiện tại
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (movieId != null) {
            fetchMovieDetailsFromDatabase(movieId);
            fetchReviewsFromDatabase(movieId);
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

        // Xử lý sự kiện khi nhấn nút Rate and Review
        rateReviewButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            ReviewDialogFragment reviewDialog = new ReviewDialogFragment(movieId, currentUser.getUid());
            reviewDialog.show(fragmentManager, "reviewDialog");
        });
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
                Double rating = dataSnapshot.child("rating").getValue(double.class);
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
                    String ratingText = Double.toString(rating);
                    movieRatingTextView.setText(ratingText);
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

    private void fetchReviewsFromDatabase(String movieId) {
        DatabaseReference databaseReferenceReviews = FirebaseDatabase.getInstance().getReference("Review");
        Query query = databaseReferenceReviews.orderByChild("movieId").equalTo(movieId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reviewList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Review review = snapshot.getValue(Review.class);
                    reviewList.add(review);
                }
                reviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
