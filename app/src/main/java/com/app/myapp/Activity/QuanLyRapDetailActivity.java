package com.app.myapp.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.myapp.Class.Location;
import com.app.myapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class QuanLyRapDetailActivity extends AppCompatActivity {
    private EditText edTenRap, edDiaChi, edSoPhong;
    private Button btThem, btSua, btXoa;
    private DatabaseReference databaseReference;
    private String locationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_quanlyrap_detail);

        ImageView imBack = findViewById(R.id.imBack);
        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Quay lại trang trước
            }
        });

        edTenRap = findViewById(R.id.edSoCot);
        edDiaChi = findViewById(R.id.editDiaChi);
        edSoPhong = findViewById(R.id.editSoPhong);

        btThem = findViewById(R.id.btThemAdmin);
        btSua = findViewById(R.id.btSuaAdmin);
        btXoa = findViewById(R.id.btXoaAdmin);

        databaseReference = FirebaseDatabase.getInstance().getReference("Location");

        locationId = getIntent().getStringExtra("locationId");

        if (locationId != null) {
            loadLocationDetails(locationId);
            btThem.setVisibility(View.GONE);
        } else {
            btSua.setVisibility(View.GONE);
            btXoa.setVisibility(View.GONE);
        }

        btThem.setOnClickListener(v -> addLocation());
        btSua.setOnClickListener(v -> updateLocation());
        btXoa.setOnClickListener(v -> deleteLocation());
    }

    private void loadLocationDetails(String locationId) {
        databaseReference.child(locationId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    Integer numberOfRooms = snapshot.child("numberOfRooms").getValue(Integer.class);

                    edTenRap.setText(name);
                    edDiaChi.setText(address);
                    edSoPhong.setText(numberOfRooms != null ? String.valueOf(numberOfRooms) : "");
                } else {
                    Toast.makeText(QuanLyRapDetailActivity.this, "Không tìm thấy thông tin rạp!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(QuanLyRapDetailActivity.this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addLocation() {
        String name = edTenRap.getText().toString().trim();
        String address = edDiaChi.getText().toString().trim();
        String numberOfRoomsStr = edSoPhong.getText().toString().trim();

        if (validateInputs(name, address, numberOfRoomsStr)) {
            String newLocationId = UUID.randomUUID().toString();
            int numberOfRooms = Integer.parseInt(numberOfRoomsStr);

            // Sử dụng lớp Location đã tạo
            Location newLocation = new Location(newLocationId, numberOfRooms, address, name);
            databaseReference.child(newLocationId).setValue(newLocation)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Thêm rạp thành công!", Toast.LENGTH_SHORT).show();
                        finish(); // Quay về QuanLyRapActivity
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Lỗi thêm rạp: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void updateLocation() {
        if (locationId == null) {
            Toast.makeText(this, "Không có rạp để sửa!", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = edTenRap.getText().toString().trim();
        String address = edDiaChi.getText().toString().trim();
        String numberOfRoomsStr = edSoPhong.getText().toString().trim();

        if (validateInputs(name, address, numberOfRoomsStr)) {
            int numberOfRooms = Integer.parseInt(numberOfRoomsStr);

            // Sử dụng lớp Location đã tạo
            Location updatedLocation = new Location(locationId, numberOfRooms, address, name);
            databaseReference.child(locationId).setValue(updatedLocation)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Sửa rạp thành công!", Toast.LENGTH_SHORT).show();
                        finish(); // Quay về QuanLyRapActivity
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Lỗi sửa rạp: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }


    private void deleteLocation() {
        if (locationId == null) {
            Toast.makeText(this, "Không có rạp để xóa!", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.child(locationId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Xóa rạp thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi xóa rạp: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private boolean validateInputs(String name, String address, String numberOfRoomsStr) {
        if (TextUtils.isEmpty(name)) {
            edTenRap.setError("Tên rạp không được để trống!");
            return false;
        }

        if (TextUtils.isEmpty(address)) {
            edDiaChi.setError("Địa chỉ không được để trống!");
            return false;
        }

        if (TextUtils.isEmpty(numberOfRoomsStr)) {
            edSoPhong.setError("Số phòng không được để trống!");
            return false;
        }

        try {
            int numberOfRooms = Integer.parseInt(numberOfRoomsStr);
            if (numberOfRooms <= 0) {
                edSoPhong.setError("Số phòng phải lớn hơn 0!");
                return false;
            }
        } catch (NumberFormatException e) {
            edSoPhong.setError("Số phòng phải là số hợp lệ!");
            return false;
        }

        return true;
    }

    private void clearInputs() {
        edTenRap.setText("");
        edDiaChi.setText("");
        edSoPhong.setText("");
    }
}