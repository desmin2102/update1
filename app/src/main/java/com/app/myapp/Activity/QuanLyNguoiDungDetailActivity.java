package com.app.myapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.myapp.Class.User;
import com.app.myapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class QuanLyNguoiDungDetailActivity extends AppCompatActivity {

    private EditText editTenUser, editTaiKhoan, editMatKhau, editPhone,editName;
    private Spinner spinnerUserRole;
    private Button btThemAdmin, btSuaAdmin, btXoaAdmin;

    private FirebaseAuth mAuth;

    private DatabaseReference databaseReference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_quanlynguoidung_detail);
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ View
        ImageView imBack = findViewById(R.id.imBack);
        editTenUser = findViewById(R.id.editTenBac);
        editTaiKhoan = findViewById(R.id.editGiamGia);
        editMatKhau = findViewById(R.id.editDiem);
        editPhone=findViewById(R.id.editphone);
        editName=findViewById(R.id.editName);
        spinnerUserRole = findViewById(R.id.spinnerUserRole);
        btThemAdmin = findViewById(R.id.btThemAdmin);
        btSuaAdmin = findViewById(R.id.btSuaAdmin);
        btXoaAdmin = findViewById(R.id.btXoaAdmin);

        // Xử lý quay lại
        imBack.setOnClickListener(v -> finish());

        // Thiết lập Spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Admin", "User"});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserRole.setAdapter(spinnerAdapter);

        // Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        userId = getIntent().getStringExtra("userId");

        // Kiểm tra và tải dữ liệu người dùng
        if (userId != null) {
            loadUserDetails();
        } else {
            // Nếu không có userId (thêm mới), ẩn các nút Sửa và Xóa
            btSuaAdmin.setVisibility(View.GONE);
            btXoaAdmin.setVisibility(View.GONE);
        }

        // Thêm sự kiện cho các nút
        btThemAdmin.setOnClickListener(v -> addUser());
        btSuaAdmin.setOnClickListener(v -> updateUser());
        btXoaAdmin.setOnClickListener(v -> deleteUser());
    }

    private void loadUserDetails() {
        databaseReference.child(userId).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                // Ẩn nút Thêm khi đang ở chế độ xem/sửa
                btThemAdmin.setVisibility(View.GONE);

                editTenUser.setText(snapshot.child("name").getValue(String.class));
                editTaiKhoan.setText(snapshot.child("email").getValue(String.class));
                editPhone.setText(snapshot.child("phone").getValue(String.class));
                editMatKhau.setText(snapshot.child("password").getValue(String.class));
                spinnerUserRole.setSelection(snapshot.child("role").getValue(Boolean.class) ? 0 : 1);
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi tải dữ liệu!", Toast.LENGTH_SHORT).show());
    }

    private void addUser() {
        // Lấy dữ liệu từ các ô nhập
        String name = editName.getText().toString().trim();
        String taiKhoan = editTaiKhoan.getText().toString().trim();
        String matKhau = editMatKhau.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        boolean role = spinnerUserRole.getSelectedItemPosition() == 0;

        // Kiểm tra dữ liệu nhập
        if (name.isEmpty() || taiKhoan.isEmpty() || matKhau.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ẩn nút Sửa và Xóa
        btSuaAdmin.setVisibility(View.GONE);
        btXoaAdmin.setVisibility(View.GONE);

        // Tạo mới User trong Firebase
        mAuth.createUserWithEmailAndPassword(taiKhoan, matKhau)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                userId = currentUser.getUid(); // Sử dụng UID từ Firebase Authentication
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference usersRef = database.getReference("User");
                                User user = new User(userId, taiKhoan, phone, matKhau,name, role); // Gán vai trò "customer"
                                usersRef.child(userId).setValue(user)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Toast.makeText(QuanLyNguoiDungDetailActivity.this, "Tạo Tài Khoản Thành Công!", Toast.LENGTH_SHORT).show();
                                                // Chuyển hướng đến trang MainActivity
                                                startActivity(new Intent(QuanLyNguoiDungDetailActivity.this, QuanLyNguoiDungDetailActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(QuanLyNguoiDungDetailActivity.this, "Lỗi khi lưu vào Firebase!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(QuanLyNguoiDungDetailActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUser() {
        String tenUser = editName.getText().toString().trim();
        String taiKhoan = editTaiKhoan.getText().toString().trim();
        String matKhau = editMatKhau.getText().toString().trim();
        String phone=editPhone.getText().toString().trim();
        boolean role = spinnerUserRole.getSelectedItemPosition() == 0;

        if (tenUser.isEmpty() || taiKhoan.isEmpty() || matKhau.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("name", tenUser);
        updatedData.put("email", taiKhoan);
        updatedData.put("password", matKhau);
        updatedData.put("role", role);
        updatedData.put("phone", phone);
        databaseReference.child(userId).updateChildren(updatedData)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi cập nhật tài khoản!", Toast.LENGTH_SHORT).show());
                finish(); // Thoát khỏi Activity
    }

    private void deleteUser() {
        databaseReference.child(userId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Xóa tài khoản thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi xóa tài khoản!", Toast.LENGTH_SHORT).show());
    }
}
