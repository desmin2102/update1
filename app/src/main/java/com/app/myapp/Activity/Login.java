package com.app.myapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.myapp.Class.Customer;
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
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
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

        initView();
    }

    private void initView() {
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String login = txtLogin.getText().toString().trim();
                String password = txtMatKhau.getText().toString().trim();
                if (login.isEmpty() || password.isEmpty()) {
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
                                        Toast.makeText(Login.this, "Đăng Nhập Thành công", Toast.LENGTH_SHORT).show();
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
                if (isUserLoggedIn()) {
                    Toast.makeText(Login.this, "Bạn cần đăng xuất trước khi đăng ký tài khoản mới.", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(Login.this, Register.class));
                }
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

    private boolean isUserLoggedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser != null;
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
                        startActivity(new Intent(Login.this, AdminMainActivity.class));
                    } else {
                        if (user instanceof Customer) {
                            Customer customer = (Customer) user;
                            saveCustomerInfo(customer);
                        }
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

    private void saveCustomerInfo(Customer customer) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userEmail", customer.getEmail());
        editor.putString("userMobile", customer.getPhone());
        editor.putString("userName", customer.getName());
        editor.putString("userId", customer.getId());
        editor.putInt("diemTV", customer.getDiemTV());
        editor.putString("rankTV", customer.getRankId());
        editor.apply();
    }
}
