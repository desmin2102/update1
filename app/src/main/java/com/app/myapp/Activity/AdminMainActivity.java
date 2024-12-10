package com.app.myapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.app.myapp.R;

public class AdminMainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        // Kết nối các ImageView
        ImageView imQuanLyRap = findViewById(R.id.imQuanLyRap);
        ImageView imQuanLyPhim = findViewById(R.id.imQuanLyPhim);
        ImageView imQuanLyPhong = findViewById(R.id.imQuanLyPhong);
        ImageView imQuanLyQuangCao = findViewById(R.id.imQuanLyQuangCao);
        ImageView imQuanLyBacThanhVien = findViewById(R.id.imQuanLyBacThanhVien);
        ImageView imQuanLyNguoiDung = findViewById(R.id.imQuanLyNguoiDung);
        ImageView imQuanLySuat = findViewById(R.id.imQuanLySuat);
        ImageView imThongKe = findViewById(R.id.imThongKe);
        ImageView imDangXuat = findViewById(R.id.imDangXuat); // Kết nối ImageView


        // Thêm sự kiện click
        imQuanLyRap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, QuanLyRapActivity.class);
                startActivity(intent);
            }
        });

        imQuanLyPhim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, QuanLyPhimActivity.class);
                startActivity(intent);
            }
        });

        imQuanLyPhong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, QuanLyPhongActivity.class);
                startActivity(intent);
            }
        });

        imQuanLyQuangCao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, QuanLyQuangCaoActivity.class);
                startActivity(intent);
            }
        });


        imQuanLyBacThanhVien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, QuanLyBacThanhVienActivity.class);
                startActivity(intent);
            }
        });

        imQuanLyNguoiDung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, QuanLyNguoiDungActivity.class);
                startActivity(intent);
            }
        });

        imQuanLySuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, QuanLySuatActivity.class);
                startActivity(intent);
            }
        });

        imThongKe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, ThongKeActivity.class);
                startActivity(intent);
            }
        });

        imDangXuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay về màn hình chính (MainActivity)
                Intent intent = new Intent(AdminMainActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Đảm bảo thoát hoàn toàn khỏi AdminMainActivity
                startActivity(intent);
                finish(); // Kết thúc AdminMainActivity để tránh quay lại bằng nút Back
            }
        });
    }
}
