package com.app.myapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuanLyPhongActivity extends AppCompatActivity {

    private ListView lvRoom;
    private ArrayAdapter<String> roomAdapter;
    private List<String> roomTitles;
    private List<String> roomIds;
    private DatabaseReference databaseReference;
    private EditText etSearch;
    private Handler handler; // Để tạo độ trễ khi tìm kiếm
    private Runnable searchRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_quanlyphong);

        // Nút thêm phòng
        ImageView imAdd = findViewById(R.id.imAdd);
        imAdd.setOnClickListener(v -> {
            Intent intent = new Intent(QuanLyPhongActivity.this, QuanLyPhongDetailActivity.class);
            startActivity(intent);
        });

        // Nút quay lại
        ImageView imBack = findViewById(R.id.imBack);
        imBack.setOnClickListener(v -> finish());

        // Khởi tạo ListView và EditText
        lvRoom = findViewById(R.id.lvRoom);
        etSearch = findViewById(R.id.etSearch);

        // Khởi tạo danh sách phòng và adapter
        roomTitles = new ArrayList<>();
        roomIds = new ArrayList<>();
        roomAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, roomTitles);
        lvRoom.setAdapter(roomAdapter);

        // Kết nối Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Room");

        // Lấy dữ liệu từ Firebase
        fetchRoomData();

        // Khởi tạo Handler để tạo độ trễ
        handler = new Handler(Objects.requireNonNull(Looper.myLooper()));

        // Thêm sự kiện TextWatcher cho EditText
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Huỷ tìm kiếm trước đó nếu người dùng vẫn đang gõ
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Tạo Runnable mới để tìm kiếm sau 1 giây
                searchRunnable = () -> searchRooms(s.toString().trim());
                handler.postDelayed(searchRunnable, 1000); // 1 giây
            }
        });

        // Sự kiện click cho ListView
        lvRoom.setOnItemClickListener((adapterView, view, position, id) -> {
            String selectedRoomId = roomIds.get(position); // Lấy roomId từ danh sách

            // Gọi phương thức để lấy locationId tương ứng
            getLocationIdForRoom(selectedRoomId, locationId -> {
                Log.d("QuanLyPhongActivity", "roomId: " + selectedRoomId + ", locationId: " + locationId);

                if (locationId != null) {
                    Log.d("QuanLyPhongActivity", "roomId: " + selectedRoomId + ", locationId: " + locationId);

                    // Chỉ thực hiện nếu locationId được trả về
                    Intent intent = new Intent(QuanLyPhongActivity.this, QuanLyPhongDetailActivity.class);
                    intent.putExtra("roomId", selectedRoomId); // Truyền roomId sang Activity chi tiết
                    intent.putExtra("locationId", locationId); // Truyền locationId sang Activity chi tiết
                    startActivity(intent);
                } else {
                    Toast.makeText(QuanLyPhongActivity.this, "Không tìm thấy locationId", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    private void fetchRoomData() {
        databaseReference.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomTitles.clear();
                roomIds.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String roomName = dataSnapshot.child("roomName").getValue(String.class);
                    String roomId = dataSnapshot.getKey();
                    String locationId = dataSnapshot.child("locationId").getValue(String.class);

                    if (roomName != null && locationId != null) {
                        DatabaseReference locationRef = FirebaseDatabase.getInstance()
                                .getReference("Location")
                                .child(locationId);

                        locationRef.child("address").get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String address = task.getResult().getValue(String.class);
                                String displayText = roomName + " | " + (address != null ? address : "Unknown Address");

                                roomTitles.add(displayText); // Thêm vào danh sách hiển thị
                                roomIds.add(roomId); // Lưu lại roomId tương ứng

                                roomAdapter.notifyDataSetChanged(); // Cập nhật ListView
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuanLyPhongActivity.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void searchRooms(String query) {
        if (query.isEmpty()) {
            fetchRoomData(); // Hiển thị lại toàn bộ danh sách nếu từ khoá rỗng
            return;
        }

        databaseReference.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomTitles.clear();
                roomIds.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String roomName = dataSnapshot.child("roomName").getValue(String.class);
                    String roomId = dataSnapshot.getKey();

                    if (roomName != null && roomName.toLowerCase().contains(query.toLowerCase())) {
                        roomTitles.add(roomName);
                        roomIds.add(roomId);
                    }
                }

                if (roomTitles.isEmpty()) {
                    Toast.makeText(QuanLyPhongActivity.this, "Không tìm thấy phòng nào!", Toast.LENGTH_SHORT).show();
                }

                roomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuanLyPhongActivity.this, "Lỗi khi tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức bất đồng bộ để lấy locationId từ Firebase
    private void getLocationIdForRoom(String roomId, LocationIdCallback callback) {
        DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference("Room").child(roomId);

        roomRef.child("locationId").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String locationId = task.getResult().getValue(String.class);
                callback.onLocationIdRetrieved(locationId);
            } else {
                Toast.makeText(QuanLyPhongActivity.this, "Lỗi khi lấy locationId", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Interface callback để nhận locationId
    interface LocationIdCallback {
        void onLocationIdRetrieved(String locationId);
    }
}
