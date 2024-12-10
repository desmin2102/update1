package com.app.myapp.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.myapp.Class.Movie;
import com.app.myapp.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class QuanLyPhimDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_quanlyphim_detail);

        // Xử lý nút quay lại
        ImageView imBack = findViewById(R.id.imBack);
        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Kết thúc Activity và quay lại trang trước
            }
        });

        // Liên kết các View trong layout
        EditText edPhim = findViewById(R.id.edPhim);
        EditText edThoiLuong = findViewById(R.id.edThoiLuong);
        EditText edKhoiChieu = findViewById(R.id.edKhoiChieu);
        EditText edTheLoai = findViewById(R.id.edTheLoai);
        EditText edAnhphim = findViewById(R.id.edAnhphim);
        EditText edTrailer = findViewById(R.id.edTrailer);
        EditText edMoTa = findViewById(R.id.edMoTa);
        ImageView imageView = findViewById(R.id.imageView);
        Button btThemAdmin = findViewById(R.id.btThemAdmin);
        Button btSuaAdmin = findViewById(R.id.btSuaAdmin);
        Button btXoaAdmin = findViewById(R.id.btXoaAdmin);


        // Lấy movieId từ Intent
        String movieId = getIntent().getStringExtra("movieId");

        // Kiểm tra nếu movieId không null, truy xuất dữ liệu từ Firebase
        if (movieId != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Movie").child(movieId);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Gán dữ liệu từ Firebase vào các View
                        edPhim.setText(snapshot.child("title").getValue(String.class));
                        edThoiLuong.setText(snapshot.child("duration").getValue(String.class));
                        edKhoiChieu.setText(snapshot.child("movieDateStart").getValue(String.class));
                        edTheLoai.setText(snapshot.child("genre").getValue(String.class));
                        edAnhphim.setText(snapshot.child("imageUrl").getValue(String.class));
                        edTrailer.setText(snapshot.child("trailerUrl").getValue(String.class));
                        edMoTa.setText(snapshot.child("summary").getValue(String.class));

                        // Hiển thị ảnh bằng Glide nếu có imageUrl
                        String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(QuanLyPhimDetailActivity.this).load(imageUrl).into(imageView);
                        } else {
                            Toast.makeText(QuanLyPhimDetailActivity.this, "Ảnh không có sẵn", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(QuanLyPhimDetailActivity.this, "Dữ liệu không tồn tại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(QuanLyPhimDetailActivity.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Thêm phim", Toast.LENGTH_SHORT).show();
        }

        btThemAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy dữ liệu từ các EditText
                String title = edPhim.getText().toString().trim();
                String duration = edThoiLuong.getText().toString().trim();
                String movieDateStart = edKhoiChieu.getText().toString().trim();
                String genre = edTheLoai.getText().toString().trim();
                String imageUrl = edAnhphim.getText().toString().trim();
                String trailerUrl = edTrailer.getText().toString().trim();
                String summary = edMoTa.getText().toString().trim();

                // Kiểm tra dữ liệu đầu vào
                if (title.isEmpty() || duration.isEmpty() || movieDateStart.isEmpty() || genre.isEmpty() || imageUrl.isEmpty() || trailerUrl.isEmpty() || summary.isEmpty()) {
                    Toast.makeText(QuanLyPhimDetailActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tạo id cho phim mới
                String movieId = FirebaseDatabase.getInstance().getReference("Movie").push().getKey();

                // Tạo đối tượng Movie
                Movie movie = new Movie(movieId, title, duration, movieDateStart, genre, "", summary, trailerUrl, imageUrl);

                // Thêm vào Firebase
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Movie");
                if (movieId != null) {
                    databaseReference.child(movieId).setValue(movie).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(QuanLyPhimDetailActivity.this, "Thêm phim thành công!", Toast.LENGTH_SHORT).show();
                            finish(); // Đóng Activity sau khi thêm thành công
                        } else {
                            Toast.makeText(QuanLyPhimDetailActivity.this, "Lỗi khi thêm phim!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        btXoaAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (movieId == null) {
                    Toast.makeText(QuanLyPhimDetailActivity.this, "Không tìm thấy phim để xóa", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Movie").child(movieId);
                databaseReference.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(QuanLyPhimDetailActivity.this, "Xóa phim thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(QuanLyPhimDetailActivity.this, "Xóa phim thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btSuaAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (movieId == null) {
                    Toast.makeText(QuanLyPhimDetailActivity.this, "Không tìm thấy phim để sửa", Toast.LENGTH_SHORT).show();
                    return;
                }

                String updatedTitle = edPhim.getText().toString();
                String updatedDuration = edThoiLuong.getText().toString();
                String updatedMovieDateStart = edKhoiChieu.getText().toString();
                String updatedGenre = edTheLoai.getText().toString();
                String updatedImageUrl = edAnhphim.getText().toString();
                String updatedTrailerUrl = edTrailer.getText().toString();
                String updatedSummary = edMoTa.getText().toString();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Movie").child(movieId);
                databaseReference.child("title").setValue(updatedTitle);
                databaseReference.child("duration").setValue(updatedDuration);
                databaseReference.child("movieDateStart").setValue(updatedMovieDateStart);
                databaseReference.child("genre").setValue(updatedGenre);
                databaseReference.child("imageUrl").setValue(updatedImageUrl);
                databaseReference.child("trailerUrl").setValue(updatedTrailerUrl);
                databaseReference.child("summary").setValue(updatedSummary).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(QuanLyPhimDetailActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        finish(); // Đóng Activity sau khi cập nhật thành công
                    } else {
                        Toast.makeText(QuanLyPhimDetailActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }
}
