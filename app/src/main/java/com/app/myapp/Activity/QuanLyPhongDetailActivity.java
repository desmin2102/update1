package com.app.myapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.myapp.Class.Location;
import com.app.myapp.Class.Room;
import com.app.myapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QuanLyPhongDetailActivity extends AppCompatActivity {

    private EditText editTenPhong, editSoGhe, editSoCot, editSoHang;
    private Spinner spChonRap; // Spinner to choose location
    private Button btThemAdmin, btSuaAdmin, btXoaAdmin;
    private DatabaseReference roomDatabase, locationDatabase;
    private String roomId,locationId; // Room ID for update or delete
    private Map<String, String> locationMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_quanlyphong_detail);

        // Initialize Firebase references
        roomDatabase = FirebaseDatabase.getInstance().getReference("Room");
        locationDatabase = FirebaseDatabase.getInstance().getReference("Location");

        // Initialize views
        editTenPhong = findViewById(R.id.editTenBac);
        editSoGhe = findViewById(R.id.editGiamGia);
        editSoCot = findViewById(R.id.editDiem);
        editSoHang = findViewById(R.id.editSoHang);
        spChonRap = findViewById(R.id.spChonRap);

        btThemAdmin = findViewById(R.id.btThemAdmin);
        btSuaAdmin = findViewById(R.id.btSuaAdmin);
        btXoaAdmin = findViewById(R.id.btXoaAdmin);

        Intent intent = getIntent();

        // Get roomId from intent (for update or delete)
        roomId = getIntent().getStringExtra("roomId");
        locationId = intent.getStringExtra("locationId");

        if (roomId != null) {
            loadRoomData(roomId);
            btThemAdmin.setVisibility(View.GONE); // Hide "Add" button when updating or deleting
        } else {
            btSuaAdmin.setVisibility(View.GONE); // Hide "Edit" button when adding
            btXoaAdmin.setVisibility(View.GONE); // Hide "Delete" button when adding
        }

        // Handle back button
        ImageView imBack = findViewById(R.id.imBack);
        imBack.setOnClickListener(v -> finish());

        // Handle "Add" button click
        btThemAdmin.setOnClickListener(v -> addRoom());

        // Handle "Edit" button click
        btSuaAdmin.setOnClickListener(v -> updateRoom());

        // Handle "Delete" button click
        btXoaAdmin.setOnClickListener(v -> deleteRoom());

        // Load locations into spinner
        loadSpinners(() -> {
            if (roomId != null) {
                roomDatabase.child(roomId).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        Room currentRoom = task.getResult().getValue(Room.class);
                        if (currentRoom != null) {
                            displayRoomDetails(currentRoom);
                        }
                    }
                });
            }
        });
    }

    private void loadSpinners(Runnable callback) {
        locationDatabase.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<String> addressList = new ArrayList<>();
                locationMap.clear(); // Xóa map trước khi thêm mới

                for (DataSnapshot data : snapshot.getChildren()) {
                    Location location = data.getValue(Location.class);
                    if (location != null) {
                        locationMap.put(location.getId(), location.getAddress()); // Key là locationId
                        addressList.add(location.getAddress());
                    }
                }


                setSpinnerAdapter(spChonRap, addressList);
                Log.d("SpinnerCheck", "Location Map: " + locationMap.toString()); // Log kiểm tra
                spChonRap.setSelection(0); // Đặt mặc định là mục đầu tiên (tùy chọn)

                callback.run();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(QuanLyPhongDetailActivity.this, "Error loading locations", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void displayRoomDetails(Room room) {
        editTenPhong.setText(room.getRoomName());
        editSoGhe.setText(String.valueOf(room.getTotalSeats()));
        editSoCot.setText(String.valueOf(room.getColumns()));
        editSoHang.setText(String.valueOf(room.getRows()));

        // Ensure correct spinner selection based on locationId
        String locationId = room.getLocationId();
        String locationAddress = locationMap.get(locationId); // Tìm địa chỉ dựa trên locationId
        if (locationAddress != null) {
            int position = ((ArrayAdapter) spChonRap.getAdapter()).getPosition(locationAddress);
            spChonRap.setSelection(position); // Đặt spinner đúng giá trị
        }
        else {
            Log.e("SpinnerCheck", "Invalid locationId: " + locationId);
        }

    }


    private void loadLocationFromRoom(String locationId, Spinner spinner) {
        if (locationId == null || locationId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy locationId!", Toast.LENGTH_SHORT).show();
            return;
        }

        locationDatabase.child(locationId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                String locationName = task.getResult().child("address").getValue(String.class);
                if (locationName != null) {
                    int position = ((ArrayAdapter) spinner.getAdapter()).getPosition(locationName);
                    spinner.setSelection(position);
                } else {
                    Toast.makeText(this, "Không tìm thấy tên khu vực!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Không thể lấy thông tin khu vực!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }



    private void setSpinnerAdapter(Spinner spinner, List<String> addressList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, addressList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    private void loadRoomData(String roomId) {
        roomDatabase.child(roomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Room room = task.getResult().getValue(Room.class);
                if (room != null) {
                    displayRoomDetails(room);
                }
            } else {
                Toast.makeText(this, "Room data not found!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void addRoom() {
        String roomName = editTenPhong.getText().toString().trim();
        String totalSeatsStr = editSoGhe.getText().toString().trim();
        String columnsStr = editSoCot.getText().toString().trim();
        String rowsStr = editSoHang.getText().toString().trim();

        if (roomName.isEmpty() || totalSeatsStr.isEmpty() || columnsStr.isEmpty() || rowsStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int totalSeats = Integer.parseInt(totalSeatsStr);
            int columns = Integer.parseInt(columnsStr);
            int rows = Integer.parseInt(rowsStr);

            if (totalSeats <= 0 || columns <= 0 || rows <= 0) {
                Toast.makeText(this, "Số ghế, số cột và số hàng phải là số nguyên dương", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lấy locationId từ spinner
            String locationId = getLocationIdFromSpinner();

            if (locationId == null || locationId.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn khu vực hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            String newRoomId = UUID.randomUUID().toString();
            Room newRoom = new Room(newRoomId, roomName, totalSeats, columns, rows, locationId);

            roomDatabase.child(newRoomId).setValue(newRoom)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Thêm phòng thành công", Toast.LENGTH_SHORT).show();
                        finish(); // Đóng activity
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số hợp lệ cho ghế, cột và hàng", Toast.LENGTH_SHORT).show();
        }
    }

    private void getLocationIdForRoom(String roomId, OnSuccessListener<String> onSuccessListener) {
        roomDatabase.child(roomId).child("locationId").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                String locationId = task.getResult().getValue(String.class);
                onSuccessListener.onSuccess(locationId);
            } else {
                Toast.makeText(this, "Không thể lấy locationId cho phòng đã chọn!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private String getLocationIdFromSpinner() {
        int selectedPosition = spChonRap.getSelectedItemPosition();
        String selectedAddress = (String) spChonRap.getItemAtPosition(selectedPosition); // Lấy địa chỉ đã chọn
        for (Map.Entry<String, String> entry : locationMap.entrySet()) {
            if (entry.getValue().equals(selectedAddress)) {
                return entry.getKey(); // Trả về locationId
            }
        }
        Toast.makeText(this, "Selected location is invalid", Toast.LENGTH_SHORT).show();
        return null;

    }



    private void updateRoom() {
        if (roomId == null) {
            Toast.makeText(this, "No room to update", Toast.LENGTH_SHORT).show();
            return;
        }

        String roomName = editTenPhong.getText().toString().trim();
        String totalSeatsStr = editSoGhe.getText().toString().trim();
        String columnsStr = editSoCot.getText().toString().trim();
        String rowsStr = editSoHang.getText().toString().trim();

        if (roomName.isEmpty() || totalSeatsStr.isEmpty() || columnsStr.isEmpty() || rowsStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int totalSeats = Integer.parseInt(totalSeatsStr);
            int columns = Integer.parseInt(columnsStr);
            int rows = Integer.parseInt(rowsStr);

            if (totalSeats <= 0 || columns <= 0 || rows <= 0) {
                Toast.makeText(this, "Seats, columns, and rows must be positive integers", Toast.LENGTH_SHORT).show();
                return;
            }

            String locationId = locationMap.get(spChonRap.getSelectedItem().toString()); // Use the spinner item to fetch locationId
            if (locationId == null) {
                Toast.makeText(this, "Please select a valid location", Toast.LENGTH_SHORT).show();
                return;
            }

            Room updatedRoom = new Room(roomId, roomName, totalSeats, columns, rows, locationId);

            roomDatabase.child(roomId).setValue(updatedRoom)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Room updated successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for seats, columns, and rows", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteRoom() {
        if (roomId == null) {
            Toast.makeText(this, "No room to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        roomDatabase.child(roomId).removeValue()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Room deleted successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
