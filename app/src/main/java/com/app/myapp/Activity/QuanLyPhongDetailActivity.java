package com.app.myapp.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.myapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.UUID;

public class QuanLyPhongDetailActivity extends AppCompatActivity {

    private EditText editTenPhong, editSoGhe, editSoCot, editSoHang;
    private Button btThemAdmin, btSuaAdmin, btXoaAdmin;
    private DatabaseReference roomDatabase;
    private String roomId; // ID của phòng (nếu cần sửa/xóa)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_quanlyphong_detail);

        // Kết nối Firebase
        roomDatabase = FirebaseDatabase.getInstance().getReference("Room");

        // Tham chiếu các View
        editTenPhong = findViewById(R.id.editTenBac);
        editSoGhe = findViewById(R.id.editGiamGia);
        editSoCot = findViewById(R.id.editDiem);
        editSoHang = findViewById(R.id.editSoHang);

        btThemAdmin = findViewById(R.id.btThemAdmin);
        btSuaAdmin = findViewById(R.id.btSuaAdmin);
        btXoaAdmin = findViewById(R.id.btXoaAdmin);

        // Lấy roomId (nếu có) từ Intent để sửa/xóa
        roomId = getIntent().getStringExtra("roomId");
        if (roomId != null) {
            loadRoomData(roomId);
            btThemAdmin.setVisibility(View.GONE);
            // Ẩn nút "Thêm" khi sửa/xóa
        }
        else {
            Toast.makeText(this, "Thêm phòng", Toast.LENGTH_SHORT).show();
            btSuaAdmin.setVisibility(View.GONE); // Ẩn nút "Sửa" khi thêm
            btXoaAdmin.setVisibility(View.GONE); // Ẩn nút "Xóa" khi thêm
        }

        // Xử lý nút quay lại
        ImageView imBack = findViewById(R.id.imBack);
        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Kết thúc Activity và quay lại trang trước
            }
        });
        // Xử lý nút "Thêm"
        btThemAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRoom();
            }
        });

        // Xử lý nút "Sửa"
        btSuaAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRoom();
            }
        });

        // Xử lý nút "Xóa"
        btXoaAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRoom();
            }
        });
    }

    private void loadRoomData(String roomId) {
        roomDatabase.child(roomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                String roomName = task.getResult().child("roomName").getValue(String.class);
                Long totalSeats = task.getResult().child("totalSeats").getValue(Long.class);
                Long columns = task.getResult().child("columns").getValue(Long.class);
                Long rows = task.getResult().child("rows").getValue(Long.class);

                // Gán dữ liệu vào EditText
                editTenPhong.setText(roomName);
                editSoGhe.setText(String.valueOf(totalSeats));
                editSoCot.setText(String.valueOf(columns));
                editSoHang.setText(String.valueOf(rows));
            } else {
                Toast.makeText(this, "Không tìm thấy thông tin phòng!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void addRoom() {
        String tenPhong = editTenPhong.getText().toString().trim();
        String soGhe = editSoGhe.getText().toString().trim();
        String soCot = editSoCot.getText().toString().trim();
        String soHang = editSoHang.getText().toString().trim();

        if (tenPhong.isEmpty() || soGhe.isEmpty() || soCot.isEmpty() || soHang.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int totalSeats = Integer.parseInt(soGhe);
            int columns = Integer.parseInt(soCot);
            int rows = Integer.parseInt(soHang);

            if (totalSeats <= 0 || columns <= 0 || rows <= 0) {
                Toast.makeText(this, "Số ghế, số cột và số hàng phải là số nguyên dương!", Toast.LENGTH_SHORT).show();
                return;
            }

            String newRoomId = UUID.randomUUID().toString();
            HashMap<String, Object> roomData = new HashMap<>();
            roomData.put("roomId", newRoomId);
            roomData.put("roomName", tenPhong);
            roomData.put("totalSeats", totalSeats);
            roomData.put("columns", columns);
            roomData.put("rows", rows);

            roomDatabase.child(newRoomId).setValue(roomData)
                    .addOnSuccessListener(unused -> Toast.makeText(this, "Thêm phòng thành công!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    finish(); // Thoát khỏi Activity
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số hợp lệ!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateRoom() {
        if (roomId == null) {
            Toast.makeText(this, "Không có phòng nào để sửa!", Toast.LENGTH_SHORT).show();
            return;
        }

        String tenPhong = editTenPhong.getText().toString().trim();
        String soGhe = editSoGhe.getText().toString().trim();
        String soCot = editSoCot.getText().toString().trim();
        String soHang = editSoHang.getText().toString().trim();

        if (tenPhong.isEmpty() || soGhe.isEmpty() || soCot.isEmpty() || soHang.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int totalSeats = Integer.parseInt(soGhe);
            int columns = Integer.parseInt(soCot);
            int rows = Integer.parseInt(soHang);

            if (totalSeats <= 0 || columns <= 0 || rows <= 0) {
                Toast.makeText(this, "Số ghế, số cột và số hàng phải là số nguyên dương!", Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, Object> roomData = new HashMap<>();
            roomData.put("roomName", tenPhong);
            roomData.put("totalSeats", totalSeats);
            roomData.put("columns", columns);
            roomData.put("rows", rows);

            roomDatabase.child(roomId).updateChildren(roomData)
                    .addOnSuccessListener(unused -> Toast.makeText(this, "Cập nhật phòng thành công!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    finish(); // Thoát khỏi Activity
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số hợp lệ!", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteRoom() {
        if (roomId == null) {
            Toast.makeText(this, "Không có phòng nào để xóa!", Toast.LENGTH_SHORT).show();
            return;
        }

        roomDatabase.child(roomId).removeValue()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Xóa phòng thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại màn hình trước
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
