package com.app.myapp.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.app.myapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {
    private EditText txtMatKhauMoi, txtNhapLaiMK;
    private Button btnDongY;
    private ProgressBar progressBar;
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
                if(mk.equals(mk1)) {
                    onClickChangePassword();
                }else{
                    Toast.makeText(ChangePassword.this,"Mật khẩu không khớp",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void initUI(){
        txtMatKhauMoi=findViewById(R.id.txtMatKhauMoi);
        txtNhapLaiMK=findViewById(R.id.txtNhapLaiMK);
        btnDongY=findViewById(R.id.btnDongY);
        progressBar = findViewById(R.id.progressBar);
    }
    private void onClickChangePassword(){
        String strNewPassWord= txtMatKhauMoi.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.updatePassword(strNewPassWord)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(ChangePassword.this,"Đổi mật khẩu thành công",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}