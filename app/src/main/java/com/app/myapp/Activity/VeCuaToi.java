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

public class VeCuaToi extends AppCompatActivity {
    private Button btnVeDaDat;
    private Button btnVeDaHuy;
    private Button btnLichSuMuaVe;
    private Button btnLichSuMuaHang;
    private Button btnDSQua;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ve_cua_toi);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Nút quay lại
        ImageView imBack = findViewById(R.id.imBack);
        imBack.setOnClickListener(v -> finish());

        btnVeDaDat = findViewById(R.id.btnVeDaDat);
        btnVeDaHuy = findViewById(R.id.btnVeDaHuy);
        btnLichSuMuaVe = findViewById(R.id.btnLichSuMuaVe);
        btnLichSuMuaHang = findViewById(R.id.btnLichSuMuaHang);
        btnDSQua = findViewById(R.id.btnDSQua);
    }

    public void onButtonClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btnVeDaDat) {
            btnVeDaDat.setBackgroundResource(R.drawable.button_chon);
            btnVeDaDat.setTextColor(getResources().getColor(android.R.color.white));
            btnVeDaHuy.setBackgroundResource(R.drawable.button_khong_chon);
            btnVeDaHuy.setTextColor(getResources().getColor(android.R.color.black));
        } else if (viewId == R.id.btnVeDaHuy) {
            btnVeDaHuy.setBackgroundResource(R.drawable.button_chon);
            btnVeDaHuy.setTextColor(getResources().getColor(android.R.color.white));
            btnVeDaDat.setBackgroundResource(R.drawable.button_khong_chon);
            btnVeDaDat.setTextColor(getResources().getColor(android.R.color.black));
        } else if (viewId == R.id.btnLichSuMuaVe) {
            btnLichSuMuaVe.setBackgroundResource(R.drawable.chon_gach_chan);
            btnLichSuMuaHang.setBackgroundResource(R.drawable.khong_chon_k_gach_chan);
            btnDSQua.setBackgroundResource(R.drawable.khong_chon_k_gach_chan);
        } else if (viewId == R.id.btnLichSuMuaHang) {
            btnLichSuMuaHang.setBackgroundResource(R.drawable.chon_gach_chan);
            btnLichSuMuaVe.setBackgroundResource(R.drawable.khong_chon_k_gach_chan);
            btnDSQua.setBackgroundResource(R.drawable.khong_chon_k_gach_chan);
        } else if (viewId == R.id.btnDSQua) {
            btnDSQua.setBackgroundResource(R.drawable.chon_gach_chan);
            btnLichSuMuaHang.setBackgroundResource(R.drawable.khong_chon_k_gach_chan);
            btnLichSuMuaVe.setBackgroundResource(R.drawable.khong_chon_k_gach_chan);
        }
    }
}