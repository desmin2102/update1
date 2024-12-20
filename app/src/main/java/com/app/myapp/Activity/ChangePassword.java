package com.app.myapp.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.myapp.Class.Customer;
import com.app.myapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangePassword extends AppCompatActivity {
    private EditText txtMatKhau1,txtMatKhauMoi, txtNhapLaiMK;
    private Button btnDongY;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Nút quay lại
        ImageView imBack = findViewById(R.id.imBack_change);
        imBack.setOnClickListener(v -> finish());

        initUI();
        txtMatKhauMoi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 6) {
                    txtMatKhauMoi.setError("Mật khẩu phải có ít nhất 6 ký tự");
                } else {
                    txtMatKhauMoi.setError(null);
                }
            }
        });
        txtNhapLaiMK.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 6) {
                    txtNhapLaiMK.setError("Mật khẩu phải có ít nhất 6 ký tự");
                } else {
                    txtNhapLaiMK.setError(null);
                }
            }
        });

        btnDongY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mk = txtMatKhauMoi.getText().toString().trim();
                String mk1 = txtNhapLaiMK.getText().toString().trim();
                if (mk.equals(mk1)) {
                    onClickChangePassword();
                } else {
                    Toast.makeText(ChangePassword.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initUI() {
        txtMatKhau1=findViewById(R.id.txtMatKhau1);
        txtMatKhauMoi = findViewById(R.id.txtMatKhauMoi);
        txtNhapLaiMK = findViewById(R.id.txtNhapLaiMK);
        btnDongY = findViewById(R.id.btnDongY);
        progressBar = findViewById(R.id.progressBar);
    }

    private void onClickChangePassword() {
        // Lấy người dùng hiện tại
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            // Kiểm tra xem người dùng đã đăng nhập chưa
        if (user != null) {
            // Yêu cầu người dùng nhập lại mật khẩu hiện tại
            String currentPassword = txtMatKhau1.getText().toString().trim();
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

            // Đăng nhập lại
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Đăng nhập lại thành công, tiếp tục thay đổi mật khẩu
                        String strNewPassWord = txtMatKhauMoi.getText().toString().trim();
                        String nhapLai=txtNhapLaiMK.getText().toString().trim();
                        if (strNewPassWord.isEmpty() || nhapLai.isEmpty()) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ChangePassword.this, "Hãy nhập Mật Khẩu!", Toast.LENGTH_SHORT).show();
                            return;
                        }else {
                            user.updatePassword(strNewPassWord)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressBar.setVisibility(View.GONE);
                                            if (task.isSuccessful()) {
                                                updatePassWord();
                                                Toast.makeText(ChangePassword.this, "Đổi mật khẩu thành công",
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(ChangePassword.this, "Đổi mật khẩu thất bại: " +
                                                        task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    } else {
                        // Đăng nhập lại thất bại
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ChangePassword.this, "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // Người dùng chưa đăng nhập
            progressBar.setVisibility(View.GONE);
            Toast.makeText(ChangePassword.this,
                    "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }

    }
    private void updatePassWord(){
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            String userId = currentUser.getUid();

            String matKhauMoi= txtMatKhauMoi.getText().toString().trim();
            DatabaseReference databaseReferenceReviews = FirebaseDatabase.getInstance().getReference("User");
            databaseReferenceReviews.child(userId).child("password").setValue(matKhauMoi);
        }

}