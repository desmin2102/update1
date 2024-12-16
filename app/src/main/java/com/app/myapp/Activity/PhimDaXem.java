package com.app.myapp.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.myapp.R;

public class PhimDaXem extends AppCompatActivity {
    private Button btnphimDaXem;
    private Button btnnhanXet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_phim_da_xem);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Nút quay lại
        ImageView imBack = findViewById(R.id.imBack);
        imBack.setOnClickListener(v -> finish());

        btnphimDaXem = findViewById(R.id.btnPhimdaxem);
        btnnhanXet = findViewById(R.id.btnNhanxet);
    }
    //đổi màu button
    public void onButtonClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btnPhimdaxem) {
            btnphimDaXem.setBackgroundResource(R.drawable.button_chon);
            btnphimDaXem.setTextColor(getResources().getColor(android.R.color.white));
            btnnhanXet.setBackgroundResource(R.drawable.button_khong_chon);
            btnnhanXet.setTextColor(getResources().getColor(android.R.color.black));
        } else
            if (viewId == R.id.btnNhanxet) {
                btnnhanXet.setBackgroundResource(R.drawable.button_chon);
                btnnhanXet.setTextColor(getResources().getColor(android.R.color.white));
                btnphimDaXem.setBackgroundResource(R.drawable.button_khong_chon);
                btnphimDaXem.setTextColor(getResources().getColor(android.R.color.black));
            }
    }
}