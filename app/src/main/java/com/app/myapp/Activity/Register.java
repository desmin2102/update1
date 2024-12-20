package com.app.myapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.myapp.Class.Customer;
import com.app.myapp.Class.User;
import com.app.myapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private TextInputEditText txtSoDienThoai, txtEmail, txtMatKhauDK, txtTen;
    private CheckBox checkBoxDieuKhoan;
    private Button btDangKy2;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Nếu người dùng đã đăng nhập thì không cần vào Register nữa
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

        // Khởi tạo các view
        txtTen = findViewById(R.id.txtTen);
        txtSoDienThoai = findViewById(R.id.txtSoDienThoai);
        txtEmail = findViewById(R.id.txtEmail);
        txtMatKhauDK = findViewById(R.id.txtMatKhauDK);
        checkBoxDieuKhoan = findViewById(R.id.checkBoxDieuKhoan);
        btDangKy2 = findViewById(R.id.btDangKy2);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();

        // Nút quay lại
        ImageView imBack = findViewById(R.id.imBack_register);
        imBack.setOnClickListener(v -> onBackPressed());

        initView();
    }

    private void initView() {
        btDangKy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String ten = txtTen.getText().toString().trim();
                String email = txtEmail.getText().toString().trim();
                String password = txtMatKhauDK.getText().toString().trim();
                String phone = txtSoDienThoai.getText().toString().trim();

                if (ten.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Register.this, "Hãy nhập Tên, Số điện thoại, Email và Mật Khẩu!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!checkBoxDieuKhoan.isChecked()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Register.this, "Bạn cần đồng ý với điều khoản!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        FirebaseUser currentUser = mAuth.getCurrentUser();
                                        if (currentUser != null) {
                                            String userId = currentUser.getUid(); // Sử dụng UID từ Firebase Authentication
                                            saveUserToDatabase(userId, ten, email, phone, password);
                                        }
                                    } else {
                                        Toast.makeText(Register.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        // Kiểm tra mật khẩu dài ít nhất 6 ký tự
        txtMatKhauDK.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 6) {
                    txtMatKhauDK.setError("Mật khẩu phải có ít nhất 6 ký tự");
                } else {
                    txtMatKhauDK.setError(null);
                }
            }
        });
    }

    private void saveUserToDatabase(String userId, String ten, String email, String phone, String password) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("User");

        int rankId = 1; // Mặc định rankId là 1 cho khách hàng
        Customer customer = new Customer(userId, ten, email, phone, password, false,0,"1"); // Tạo đối tượng Customer với rankId

        usersRef.child(userId).setValue(customer).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Hiển thị thông báo thành công
                Toast.makeText(Register.this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();

                // Đăng xuất ngay sau khi tạo tài khoản thành công
                mAuth.signOut();

            } else {
                // Nếu lưu thông tin người dùng thất bại
                Toast.makeText(Register.this, "Lưu thông tin người dùng thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Quay lại màn hình chính khi người dùng nhấn nút quay lại
        finish();
    }
}
