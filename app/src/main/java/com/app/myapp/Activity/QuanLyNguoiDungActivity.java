package com.app.myapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.myapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class QuanLyNguoiDungActivity extends AppCompatActivity {

    private ListView lvAccount;
    private ArrayList<String> userList; // Danh sách chứa tên và quyền của người dùng
    private ArrayList<String> filteredUserList; // Danh sách lọc sau tìm kiếm
    private HashMap<String, String> userMap; // Lưu trữ tên và userId
    private ArrayAdapter<String> adapter;
    private DatabaseReference databaseReference;
    private EditText searchUser;
    private Handler searchHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_quanlynguoidung);

        // Khởi tạo các view
        ImageView imAdd = findViewById(R.id.imAdd);
        imAdd.setOnClickListener(v -> {
            // Chuyển sang màn hình thêm người dùng
            Intent intent = new Intent(QuanLyNguoiDungActivity.this, QuanLyNguoiDungDetailActivity.class);
            startActivity(intent);
        });

        // Nút quay lại
        ImageView imBack = findViewById(R.id.imBack);
        imBack.setOnClickListener(v -> finish());

        lvAccount = findViewById(R.id.lvAccount); // ListView để hiển thị tên người dùng và quyền
        searchUser = findViewById(R.id.etSearch); // EditText để tìm kiếm người dùng

        // Khởi tạo danh sách và adapter
        userList = new ArrayList<>();
        filteredUserList = new ArrayList<>();
        userMap = new HashMap<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredUserList);
        lvAccount.setAdapter(adapter);

        // Thiết lập Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        searchHandler = new Handler();

        // Lấy dữ liệu người dùng từ Firebase
        fetchUsers();

        // Xử lý tìm kiếm với độ trễ
        searchUser.addTextChangedListener(new TextWatcher() {
            private final Runnable searchRunnable = new Runnable() {
                @Override
                public void run() {
                    filterUsers(searchUser.getText().toString());
                }
            };

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHandler.removeCallbacks(searchRunnable);
                searchHandler.postDelayed(searchRunnable, 500); // Độ trễ 500ms
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Xử lý sự kiện khi click vào item trong ListView
        lvAccount.setOnItemClickListener((parent, view, position, id) -> {
            String selectedUser = filteredUserList.get(position);
            String userId = userMap.get(selectedUser); // Lấy userId từ map

            if (userId != null) {
                // Chuyển sang activity chi tiết với userId
                Intent intent = new Intent(QuanLyNguoiDungActivity.this, QuanLyNguoiDungDetailActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            } else {
                Toast.makeText(QuanLyNguoiDungActivity.this, "Không tìm thấy người dùng này!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Lấy dữ liệu người dùng từ Firebase
    private void fetchUsers() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                userMap.clear(); // Xóa dữ liệu cũ
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userId = dataSnapshot.getKey();
                    String name = dataSnapshot.child("name").getValue(String.class);

                    // Xử lý dữ liệu role
                    Object roleObj = dataSnapshot.child("role").getValue();
                    String role;
                    if (roleObj instanceof Boolean) {
                        role = (Boolean) roleObj ? "Admin" : "User"; // Chuyển true/false thành Admin/User
                    } else if (roleObj instanceof String) {
                        role = ((String) roleObj).equalsIgnoreCase("true") ? "Admin" : "User"; // Ép kiểu Object thành String
                    } else {
                        role = "Unknown"; // Mặc định nếu không tìm thấy
                    }

                    if (name != null && role != null) {
                        // Kết hợp tên và quyền thành một chuỗi
                        String userInfo = name + " - " + role;
                        userList.add(userInfo);
                        userMap.put(userInfo, userId); // Lưu tên và userId vào map
                    }
                }
                filterUsers(searchUser.getText().toString()); // Áp dụng bộ lọc sau khi tải dữ liệu
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuanLyNguoiDungActivity.this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", "Error: " + error.getMessage());
            }
        });
    }

    // Bộ lọc tìm kiếm người dùng
    private void filterUsers(String query) {
        filteredUserList.clear();
        for (String userInfo : userList) {
            if (userInfo.toLowerCase().contains(query.toLowerCase())) {
                filteredUserList.add(userInfo);
            }
        }
        adapter.notifyDataSetChanged(); // Cập nhật dữ liệu cho adapter
    }
}