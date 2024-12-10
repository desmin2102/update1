package com.app.myapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.myapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuanLyPhimActivity extends AppCompatActivity {

    private ListView lvMovieList;
    private ArrayAdapter<String> movieAdapter;
    private List<String> movieTitles;
    private List<String> movieIds;
    private DatabaseReference databaseReference;
    private EditText etSearch;
    private Handler handler; // Để tạo độ trễ khi tìm kiếm
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_quanlyphim);

        // Nút thêm phim
        ImageView imAdd = findViewById(R.id.imAdd);
        imAdd.setOnClickListener(v -> {
            Intent intent = new Intent(QuanLyPhimActivity.this, QuanLyPhimDetailActivity.class);
            startActivity(intent);
        });

        // Nút quay lại
        ImageView imBack = findViewById(R.id.imBack);
        imBack.setOnClickListener(v -> finish());

        // Khởi tạo ListView và EditText
        lvMovieList = findViewById(R.id.lvMovieList);
        etSearch = findViewById(R.id.etSearch);

        // Khởi tạo danh sách phim và adapter
        movieTitles = new ArrayList<>();
        movieIds = new ArrayList<>();
        movieAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, movieTitles);
        lvMovieList.setAdapter(movieAdapter);

        // Kết nối Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Movie");

        // Lấy dữ liệu từ Firebase
        fetchMovieData();

        // Khởi tạo Handler để tạo độ trễ
        handler = new Handler(Objects.requireNonNull(Looper.myLooper()));

        // Thêm sự kiện TextWatcher cho EditText
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Huỷ tìm kiếm trước đó nếu người dùng vẫn đang gõ
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Tạo Runnable mới để tìm kiếm sau 1 giây
                searchRunnable = () -> searchMovies(s.toString().trim());
                handler.postDelayed(searchRunnable, 1000); // 1 giây
            }
        });

        // Sự kiện click cho ListView
        lvMovieList.setOnItemClickListener((adapterView, view, position, id) -> {
            String selectedMovieId = movieIds.get(position);
            Intent intent = new Intent(QuanLyPhimActivity.this, QuanLyPhimDetailActivity.class);
            intent.putExtra("movieId", selectedMovieId);
            startActivity(intent);
        });
    }

    private void fetchMovieData() {
        databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                movieTitles.clear();
                movieIds.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String title = dataSnapshot.child("title").getValue(String.class);
                    String movieId = dataSnapshot.getKey();

                    movieTitles.add(title);
                    movieIds.add(movieId);
                }
                movieAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuanLyPhimActivity.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchMovies(String query) {
        if (query.isEmpty()) {
            fetchMovieData(); // Hiển thị lại toàn bộ danh sách nếu từ khoá rỗng
            return;
        }

        databaseReference.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                movieTitles.clear();
                movieIds.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String title = dataSnapshot.child("title").getValue(String.class);
                    String movieId = dataSnapshot.getKey();

                    if (title != null && title.toLowerCase().contains(query.toLowerCase())) {
                        movieTitles.add(title);
                        movieIds.add(movieId);
                    }
                }

                if (movieTitles.isEmpty()) {
                    Toast.makeText(QuanLyPhimActivity.this, "Không tìm thấy phim nào!", Toast.LENGTH_SHORT).show();
                }

                movieAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuanLyPhimActivity.this, "Lỗi khi tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
