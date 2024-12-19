package com.app.myapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.myapp.Class.MovieSession;
import com.app.myapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class QuanLySuatActivity extends AppCompatActivity {

    private ListView lvShow;
    private EditText etSearch;
    private ImageView imAdd;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> sessionDisplayList = new ArrayList<>();
    private ArrayList<MovieSession> sessionList = new ArrayList<>();

    private DatabaseReference sessionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_quanlysuat);

        // Tham chiếu view
        lvShow = findViewById(R.id.lvShow);
        etSearch = findViewById(R.id.etSearch);
        imAdd = findViewById(R.id.imAdd);
        findViewById(R.id.imBack).setOnClickListener(v -> finish());

        sessionRef = FirebaseDatabase.getInstance().getReference("MovieSession");

        // Load dữ liệu
        loadSessions();

        // Nút thêm mới
        imAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, QuanLySuatDetailActivity.class);
            startActivity(intent);
        });

        // Tìm kiếm
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        // Xử lý khi nhấn vào item trong ListView
        lvShow.setOnItemClickListener((parent, view, position, id) -> {
            // Lấy đối tượng MovieSession tương ứng với item được click
            MovieSession selectedSession = sessionList.get(position);

            // Tạo Intent để chuyển sang QuanLySuatDetailActivity
            Intent intent = new Intent(this, QuanLySuatDetailActivity.class);

            // Truyền các thông tin của suất chiếu vào Intent
            intent.putExtra("SessionId", selectedSession.getSessionId()); // Truyền SessionId
            intent.putExtra("MovieName", selectedSession.getSessionName()); // Truyền tên suất chiếu
            intent.putExtra("MoviePrice", selectedSession.getPrice()); // Truyền giá vé
            intent.putExtra("StartDay", selectedSession.getStartDay()); // Truyền ngày bắt đầu
            intent.putExtra("StartTime", selectedSession.getStartTime()); // Truyền giờ bắt đầu
            intent.putExtra("EndTime", selectedSession.getEndTime()); // Truyền giờ kết thúc
            intent.putExtra("MovieId", selectedSession.getMovieId()); // Truyền movieId
            intent.putExtra("RoomId", selectedSession.getRoomId()); // Truyền roomId

            // Bắt đầu activity
            startActivity(intent);
        });
}

        private void updateListView() {
        if (adapter == null) {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sessionDisplayList);
            lvShow.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void filter(String keyword) {
        ArrayList<String> filteredList = new ArrayList<>();
        for (int i = 0; i < sessionList.size(); i++) {
            String displayText = sessionDisplayList.get(i);
            if (displayText.toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(displayText);
            }
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredList);
        lvShow.setAdapter(adapter);
    }

    private void loadSessions() {
        sessionRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                sessionList.clear();
                sessionDisplayList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    MovieSession session = data.getValue(MovieSession.class);
                    if (session != null) {
                        sessionList.add(session);
                        Log.d("QuanLySuatActivity", "Loaded session: " + session.getSessionName());

                        // Lấy title từ Movie thông qua movieId
                        DatabaseReference movieRef = FirebaseDatabase.getInstance().getReference("Movie").child(session.getMovieId());
                        movieRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot movieSnapshot) {
                                String title = movieSnapshot.child("title").getValue(String.class);
                                Log.d("QuanLySuatActivity", "Loaded movie title: " + title);

                                // Lấy RoomId từ session để tiếp tục lấy locationId từ Room
                                DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference("Room").child(session.getRoomId());
                                roomRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot roomSnapshot) {
                                        String locationId = roomSnapshot.child("locationId").getValue(String.class);
                                        Log.d("QuanLySuatActivity", "Loaded room locationId: " + locationId);

                                        // Sau khi có locationId, truy vấn thêm từ Location để lấy địa chỉ
                                        if (locationId != null) {
                                            DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("Location").child(locationId);
                                            locationRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot locationSnapshot) {
                                                    String address = locationSnapshot.child("address").getValue(String.class);
                                                    Log.d("QuanLySuatActivity", "Loaded location address: " + address);

                                                    String roomName = roomSnapshot.child("roomName").getValue(String.class);
                                                    Log.d("QuanLySuatActivity", "Loaded room name: " + roomName);

                                                    if (title != null && address != null && roomName != null) {
                                                        String displayText =   title + " | "
                                                                +   session.getSessionName() + " | "
                                                                + address + " | "
                                                                 + roomName + " | "
                                                               + session.getStartDay() + " | "
                                                                + session.getStartTime() + " | "
                                                                + session.getEndTime() + " | "
                                                                + session.getPrice();
                                                        Log.d("QuanLySuatActivity", "Display text: " + displayText);
                                                        sessionDisplayList.add(displayText);
                                                        updateListView(); // Cập nhật danh sách sau khi load dữ liệu
                                                    } else {
                                                        Log.e("QuanLySuatActivity", "Null values detected for title/address/roomName");
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError error) {
                                                    Log.e("QuanLySuatActivity", "Error loading location data: " + error.getMessage());
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        Log.e("QuanLySuatActivity", "Error loading room data: " + error.getMessage());
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                Log.e("QuanLySuatActivity", "Error loading movie data: " + error.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("QuanLySuatActivity", "Error loading session data: " + error.getMessage());
            }
        });
    }
}