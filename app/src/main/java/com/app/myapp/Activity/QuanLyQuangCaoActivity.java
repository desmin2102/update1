package com.app.myapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.myapp.R;

public class QuanLyQuangCaoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_quanlyquangcao); // Liên kết layout tương ứng
        ImageView imAdd = findViewById(R.id.imAdd);

        // Thiết lập OnClickListener cho nút Cộng
        imAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang Activity quản lý quảng cáo chi tiết
                Intent intent = new Intent(QuanLyQuangCaoActivity.this, QuanLyQuangCaoDetailActivity.class);
                startActivity(intent);
            }
        });

        ImageView imBack = findViewById(R.id.imBack);
        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Gọi hành vi mặc định quay lại trang trước
            }
        });
    }
}
