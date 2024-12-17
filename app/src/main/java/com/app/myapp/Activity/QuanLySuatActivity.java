package com.app.myapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        // Xử lý khi nhấn vào item
        lvShow.setOnItemClickListener((parent, view, position, id) -> {
            // Lấy `SessionId` của session được chọn
            String selectedSessionId = sessionList.get(position).getSessionId();

            // Tạo Intent và truyền `SessionId`
            Intent intent = new Intent(this, QuanLySuatDetailActivity.class);
            intent.putExtra("SessionId", selectedSessionId); // Truyền `SessionId`
            intent.putExtra("isFromListView", true);

            // Bắt đầu activity
            startActivity(intent);
        });
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

                        // Lấy title từ Movie thông qua movieId
                        DatabaseReference movieRef = FirebaseDatabase.getInstance().getReference("Movie").child(session.getMovieId());
                        movieRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot movieSnapshot) {
                                String title = movieSnapshot.child("title").getValue(String.class);
                                if (title != null) {
                                    String displayText = title + " | "
                                            + session.getSessionName() + " | "
                                            + session.getStartDay() + " | "
                                            + session.getStartTime() + " | Price: "
                                            + session.getPrice();
                                    sessionDisplayList.add(displayText);
                                    updateListView(); // Cập nhật danh sách sau khi load dữ liệu
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {}
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
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
}
