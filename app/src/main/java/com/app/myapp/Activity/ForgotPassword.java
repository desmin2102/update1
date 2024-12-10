package com.app.myapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class ForgotPassword extends AppCompatActivity {
    private ProgressBar progressBar;
    private Button btnGui;
    private EditText txtEmailXT;
    private FirebaseAuth mAuth;
    private String userEmail;
    private TextView txtDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnGui = findViewById(R.id.btnGui);
        progressBar = findViewById(R.id.progressBar);
        txtEmailXT=findViewById(R.id.txtEmailXT);
        txtDK= findViewById(R.id.txtDK);

        // Nút quay lại
        ImageView imBack = findViewById(R.id.imBack);
        imBack.setOnClickListener(v -> finish());

        btnGui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtEmailXT.getText().toString().isEmpty()) {
                    Toast.makeText(ForgotPassword.this,
                            "Hãy nhập Email Tài Khoản!", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    userEmail = txtEmailXT.getText().toString().trim();
                    mAuth = FirebaseAuth.getInstance();
                    progressBar.setVisibility(View.VISIBLE);
                    // Lấy thông tin người dùng
                    mAuth.sendPasswordResetEmail(userEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ForgotPassword.this,
                                                "Đã gửi đến Email!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ForgotPassword.this,Login.class));
                                        finish();
                                    }else {
                                        Toast.makeText(ForgotPassword.this,
                                                "Email không tồn tại", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        txtDK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPassword.this,Register.class);
                startActivity(intent);
            }
        });
    }
}