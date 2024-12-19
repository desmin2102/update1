package com.app.myapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
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

        imBack.setOnClickListener(v -> {
            // Xóa dữ liệu cũ ở đây (nếu có)

            // Quay lại MainActivity
            Intent intent = new Intent(VeCuaToi.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Xóa tất cả các activity phía trước và chỉ giữ lại MainActivity
            startActivity(intent);
            finish();  // Đảm bảo activity này bị đóng nếu không cần thiết phải quay lại nó

        });



        btnLichSuMuaVe = findViewById(R.id.btnLichSuMuaVe);
        btnLichSuMuaHang = findViewById(R.id.btnLichSuMuaHang);
        replaceFragment(new LichSuMuaVeFragment());
    }

    @Override
    public void onResume() {
        super.onResume();

        // Lắng nghe sự kiện back
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Xử lý sự kiện back
        findViewById(android.R.id.content).setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                clearFragmentsData();
                // Gọi dispatcher để xử lý back
                getOnBackPressedDispatcher().onBackPressed();
                return true;
            }
            return false;
        });
    }



    public void onButtonClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btnLichSuMuaVe) {
            btnLichSuMuaVe.setBackgroundResource(R.drawable.chon_gach_chan);
            btnLichSuMuaHang.setBackgroundResource(R.drawable.khong_chon_k_gach_chan);
            if (currentFragment != Fragment_LICHSUMUAVE) {
                replaceFragment(new LichSuMuaVeFragment());
                currentFragment = Fragment_LICHSUMUAVE;
            }
        } else if (viewId == R.id.btnLichSuMuaHang) {
            btnLichSuMuaHang.setBackgroundResource(R.drawable.chon_gach_chan);
            btnLichSuMuaVe.setBackgroundResource(R.drawable.khong_chon_k_gach_chan);
            if (currentFragment != Fragment_LICHSUMUAHANG) {
                replaceFragment(new LichSuMuaHangFragment());
                currentFragment = Fragment_LICHSUMUAHANG;
            }
        }
    }
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (fragment instanceof LichSuMuaVeFragment) {
            fragmentTransaction.replace(R.id.fragment_container_VeCuaToi, fragment, "LichSuMuaVeFragment");
        } else if (fragment instanceof LichSuMuaHangFragment) {
            fragmentTransaction.replace(R.id.fragment_container_VeCuaToi, fragment, "LichSuMuaHangFragment");
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void clearFragmentsData() {
        // Xóa dữ liệu trong LichSuMuaVeFragment
        LichSuMuaVeFragment lichSuMuaVeFragment = (LichSuMuaVeFragment) getSupportFragmentManager().findFragmentByTag("LichSuMuaVeFragment");
        if (lichSuMuaVeFragment != null) {
            lichSuMuaVeFragment.clearData();
        }

        // Xóa dữ liệu trong LichSuMuaHangFragment
        LichSuMuaHangFragment lichSuMuaHangFragment = (LichSuMuaHangFragment) getSupportFragmentManager().findFragmentByTag("LichSuMuaHangFragment");
        if (lichSuMuaHangFragment != null) {
            lichSuMuaHangFragment.clearData();
        }
    }

    @Override
    public void onBackPressed() {
        // Xóa toàn bộ dữ liệu của các fragment khi quay lại
        clearFragmentsData();

        // Gọi super để thực hiện hành vi quay lại mặc định của hệ thống (hoặc quay lại Activity trước đó)
        super.onBackPressed();
    }


}