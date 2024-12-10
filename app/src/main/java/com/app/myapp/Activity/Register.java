package com.app.myapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    private EditText txtSoDienThoai, txtEmail, txtMatKhauDK;
    private CheckBox checkBoxDieuKhoan;
    Button btDangKy2;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(Register.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtSoDienThoai = findViewById(R.id.txtSoDienThoai);
        txtEmail = findViewById(R.id.txtEmail);
        txtMatKhauDK = findViewById(R.id.txtMatKhauDK);
        checkBoxDieuKhoan = findViewById(R.id.checkBoxDieuKhoan);
        btDangKy2 = findViewById(R.id.btDangKy2);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        initView();
    }

    private void initView() {

        btDangKy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(txtEmail.getText().toString().trim());
                password = String.valueOf(txtMatKhauDK.getText().toString().trim());
                if (txtSoDienThoai.getText().toString().isEmpty() ||
                        txtEmail.getText().toString().isEmpty() ||
                        txtMatKhauDK.getText().toString().isEmpty()) {
                    Toast.makeText(Register.this,
                            "Hãy nhập Số điện thoại,Email và Mật Khẩu!",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else if (!checkBoxDieuKhoan.isChecked()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Register.this, "Không hợp lệ ", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    //Vượt qua tiến trình để làm
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        //FirebaseUser user = mAuth.getCurrentUser();; //phương thức getCurrentUser để lấy dữ liệu tài khoản của người dùng.
                                        Toast.makeText(Register.this, "Tạo Tài Khoản Thành Công!",
                                                Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Register.this, Login.class));
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(Register.this, "Thất Bại",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}