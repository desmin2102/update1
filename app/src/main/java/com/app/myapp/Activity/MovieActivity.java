package com.app.myapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.DividerItemDecoration;
import com.app.myapp.Adapter.MovieActivityAdapter;
import com.app.myapp.Class.Movie;
import com.app.myapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MovieActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerViewMovies;
    private MovieActivityAdapter movieActivityAdapter;
    private List<Movie> movieList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_activity);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.movie);
        bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
            int id = menuItem.getItemId(); if (id == R.id.home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();
                return true;
            }
            else if (id == R.id.session) {
                startActivity(new Intent(getApplicationContext(), SessionActivity.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();
                return true; }
            else if (id == R.id.movie) {
                return true; }
            else if (id == R.id.promotion) {
                startActivity(new Intent(getApplicationContext(), AdActivity.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();
                return true; }
            else { return false; }
        });

        recyclerViewMovies = findViewById(R.id.recyclerViewMovies);
        recyclerViewMovies.setLayoutManager(new LinearLayoutManager(this));
        movieList = new ArrayList<>();
        movieActivityAdapter = new MovieActivityAdapter(movieList);
        recyclerViewMovies.setAdapter(movieActivityAdapter);

        // Thêm phân cách vào RecyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerViewMovies.addItemDecoration(dividerItemDecoration);

        // Thiết lập tham chiếu đến Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Movie");

        // Đọc dữ liệu từ Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                movieList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Movie movie = snapshot.getValue(Movie.class);
                    if (movie != null) {
                        movieList.add(movie);
                    }
                }
                movieActivityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }
}
