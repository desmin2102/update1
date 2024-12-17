package com.app.myapp.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.myapp.Class.Ad;
import com.app.myapp.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class QuanLyQuangCaoDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_quanlyquangcao_detail);

        // Xử lý nút quay lại
        ImageView imBack = findViewById(R.id.imBack);
        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Quay lại trang trước
            }
        });

        // Liên kết các View trong layout
        EditText edTenQuangCao = findViewById(R.id.editName); // Trường tên quảng cáo
        EditText edAnhQuangCao = findViewById(R.id.editAnhquangcao); // Trường ảnh quảng cáo
        EditText edNote=findViewById(R.id.editNote);
        ImageView imageView = findViewById(R.id.imageView2);
        Button btThemAdmin = findViewById(R.id.btThemAdmin);
        Button btSuaAdmin = findViewById(R.id.btSuaAdmin);
        Button btXoaAdmin = findViewById(R.id.btXoaAdmin);

        // Lấy adId từ Intent
        String adId = getIntent().getStringExtra("adId");

        // Kiểm tra nếu adId không null, truy xuất dữ liệu từ Firebase
        if (adId != null) {
            btThemAdmin.setVisibility(View.GONE);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Ad").child(adId);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Gán dữ liệu từ Firebase vào các View
                        edTenQuangCao.setText(snapshot.child("adName").getValue(String.class)); // Gán tên quảng cáo
                        edAnhQuangCao.setText(snapshot.child("mediaurl").getValue(String.class));
                        edNote.setText(snapshot.child("note").getValue(String.class));

                        // Hiển thị ảnh bằng Glide nếu có mediaurl
                        String mediaUrl = snapshot.child("mediaurl").getValue(String.class);
                        if (mediaUrl != null && !mediaUrl.isEmpty()) {
                            Glide.with(QuanLyQuangCaoDetailActivity.this).load(mediaUrl).into(imageView);
                        } else {
                            Toast.makeText(QuanLyQuangCaoDetailActivity.this, "Ảnh không có sẵn", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(QuanLyQuangCaoDetailActivity.this, "Dữ liệu không tồn tại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(QuanLyQuangCaoDetailActivity.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Thêm quảng cáo", Toast.LENGTH_SHORT).show();
            btSuaAdmin.setVisibility(View.GONE); // Ẩn nút "Sửa" khi thêm
            btXoaAdmin.setVisibility(View.GONE); // Ẩn nút "Xóa" khi thêm
        }

        btThemAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy dữ liệu từ các EditText
                String adName= edTenQuangCao.getText().toString().trim();
                String note = edNote.getText().toString().trim();
                String mediaUrl = edAnhQuangCao.getText().toString().trim();

                // Kiểm tra dữ liệu đầu vào
                if (note.isEmpty() || mediaUrl.isEmpty()) {
                    Toast.makeText(QuanLyQuangCaoDetailActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tạo id cho quảng cáo mới
                String adId = UUID.randomUUID().toString();

                // Tạo đối tượng Ad
                Ad ad = new Ad(adId, note, mediaUrl,adName);

                // Thêm vào Firebase
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Ad");
                if (adId != null) {
                    databaseReference.child(adId).setValue(ad).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(QuanLyQuangCaoDetailActivity.this, "Thêm quảng cáo thành công!", Toast.LENGTH_SHORT).show();
                            finish(); // Đóng Activity sau khi thêm thành công
                        } else {
                            Toast.makeText(QuanLyQuangCaoDetailActivity.this, "Lỗi khi thêm quảng cáo!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        btXoaAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adId == null) {
                    Toast.makeText(QuanLyQuangCaoDetailActivity.this, "Không tìm thấy quảng cáo để xóa", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Ad").child(adId);
                databaseReference.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(QuanLyQuangCaoDetailActivity.this, "Xóa quảng cáo thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(QuanLyQuangCaoDetailActivity.this, "Xóa quảng cáo thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btSuaAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adId == null) {
                    Toast.makeText(QuanLyQuangCaoDetailActivity.this, "Không tìm thấy quảng cáo để sửa", Toast.LENGTH_SHORT).show();
                    return;
                }

                String updatedNote = edTenQuangCao.getText().toString();
                String updatedMediaUrl = edAnhQuangCao.getText().toString();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Ad").child(adId);
                databaseReference.child("note").setValue(updatedNote);
                databaseReference.child("mediaurl").setValue(updatedMediaUrl).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(QuanLyQuangCaoDetailActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(QuanLyQuangCaoDetailActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}