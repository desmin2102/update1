package com.app.myapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.myapp.Class.User;
import com.app.myapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.content.SharedPreferences;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText txtLogin, txtMatKhau;
    private TextView txtQuenMK;
    private Button btLogin, btDangKy1;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        boolean userLoggedOut = sharedPreferences.getBoolean("userLoggedOut", true);
        if (currentUser != null && !userLoggedOut) {
            checkUserRoleAndRedirect(currentUser.getUid());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtLogin = findViewById(R.id.txtLogin);
        txtMatKhau = findViewById(R.id.txtMatKhau);
        btLogin = findViewById(R.id.btLogin);
        btDangKy1 = findViewById(R.id.btDangKy1);
        txtQuenMK = findViewById(R.id.txtQuenMK);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

        // Nếu người dùng đăng xuất, lưu trạng thái đăng xuất là true
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("userLoggedOut", true);
        editor.apply();

        initView();
    }

    private void initView() {
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String login, password;
                login = String.valueOf(txtLogin.getText().toString().trim());
                password = String.valueOf(txtMatKhau.getText().toString().trim());
                if (txtLogin.getText().toString().isEmpty() || txtMatKhau.getText().toString().isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Login.this, "Hãy nhập Tên Tài Khoản và Mật Khẩu!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    mAuth.signInWithEmailAndPassword(login, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        // Cập nhật trạng thái đăng nhập trong SharedPreferences
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean("userLoggedOut", false);
                                        editor.apply();
                                        Toast.makeText(Login.this, "Đăng Nhập Thành công", Toast.LENGTH_SHORT).show();
                                        String userId = mAuth.getCurrentUser().getUid();
                                        checkUserRoleAndRedirect(userId);
                                    } else {
                                        Toast.makeText(Login.this, "Đăng Nhập Thất Bại", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        btDangKy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
                finish();
            }
        });

        txtQuenMK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, ForgotPassword.class));
                finish();
            }
        });
    }

    private void checkUserRoleAndRedirect(String userId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("User").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    if (user.isRole()) {
                        // Chuyển sang AdminMainActivity nếu người dùng là admin
                        startActivity(new Intent(Login.this, AdminMainActivity.class));
                    } else {
                        // Chuyển sang MainActivity nếu người dùng là customer
                        startActivity(new Intent(Login.this, MainActivity.class));
                    }
                    finish();
                } else {
                    Toast.makeText(Login.this, "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Login.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

