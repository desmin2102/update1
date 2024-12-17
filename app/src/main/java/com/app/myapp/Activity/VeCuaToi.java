package com.app.myapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.app.myapp.Activity.fragment.LichSuMuaHangFragment;
import com.app.myapp.Activity.fragment.LichSuMuaVeFragment;
import com.app.myapp.Activity.fragment.PhimDaXemFragment;
import com.app.myapp.R;

public class VeCuaToi extends AppCompatActivity {
    private Button btnLichSuMuaVe;
    private Button btnLichSuMuaHang;
    private Button btnDSQua;

    private static final int Fragment_LICHSUMUAVE = 1;
    private static final int Fragment_LICHSUMUAHANG = 2;
    private int currentFragment = Fragment_LICHSUMUAVE;

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

        btnLichSuMuaVe = findViewById(R.id.btnLichSuMuaVe);
        btnLichSuMuaHang = findViewById(R.id.btnLichSuMuaHang);
        btnDSQua = findViewById(R.id.btnDSQua);

        replaceFragment(new LichSuMuaVeFragment());
    }

    public void onButtonClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btnLichSuMuaVe) {
            btnLichSuMuaVe.setBackgroundResource(R.drawable.chon_gach_chan);
            btnLichSuMuaHang.setBackgroundResource(R.drawable.khong_chon_k_gach_chan);
            btnDSQua.setBackgroundResource(R.drawable.khong_chon_k_gach_chan);
            if (currentFragment != Fragment_LICHSUMUAVE) {
                replaceFragment(new LichSuMuaVeFragment());
                currentFragment = Fragment_LICHSUMUAVE;
            }
        } else if (viewId == R.id.btnLichSuMuaHang) {
            btnLichSuMuaHang.setBackgroundResource(R.drawable.chon_gach_chan);
            btnLichSuMuaVe.setBackgroundResource(R.drawable.khong_chon_k_gach_chan);
            btnDSQua.setBackgroundResource(R.drawable.khong_chon_k_gach_chan);
            if (currentFragment != Fragment_LICHSUMUAHANG) {
                replaceFragment(new LichSuMuaHangFragment());
                currentFragment = Fragment_LICHSUMUAHANG;
            }
        } else if (viewId == R.id.btnDSQua) {
            btnDSQua.setBackgroundResource(R.drawable.chon_gach_chan);
            btnLichSuMuaHang.setBackgroundResource(R.drawable.khong_chon_k_gach_chan);
            btnLichSuMuaVe.setBackgroundResource(R.drawable.khong_chon_k_gach_chan);
        }
    }
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_VeCuaToi, fragment);
        fragmentTransaction.commit();
    }
}