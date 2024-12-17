package com.app.myapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.myapp.Class.MovieSession;
import com.app.myapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuanLySuatDetailActivity extends AppCompatActivity {

    private Spinner spPhim, spPhong, spRap;
    private EditText edGiaVe, edSessionName;
    private Button btChonNgay, btChonGio, btChonEndTime;
    private Button btThemAdmin, btSuaAdmin, btXoaAdmin;

    private DatabaseReference sessionRef;
    private MovieSession currentSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_quanlysuat_detail);
        ImageView imBack = findViewById(R.id.imBack);
        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Quay lại trang trước
            }
        });
        // Tham chiếu view
        spPhim = findViewById(R.id.spPhim);
        spPhong = findViewById(R.id.spPhong);
        spRap = findViewById(R.id.spRap);
        edGiaVe = findViewById(R.id.edGiaVe);
        edSessionName = findViewById(R.id.edSessionName);
        btChonNgay = findViewById(R.id.btChonNgay);
        btChonGio = findViewById(R.id.btChonGio);
        btChonEndTime = findViewById(R.id.btChonGioHet);
        btThemAdmin = findViewById(R.id.btThemAdmin);
        btSuaAdmin = findViewById(R.id.btSuaAdmin);
        btXoaAdmin = findViewById(R.id.btXoaAdmin);

        // Firebase reference
        sessionRef = FirebaseDatabase.getInstance().getReference("MovieSessions");

        // Nhận SessionId từ Intent
        String sessionId = getIntent().getStringExtra("SessionId");

        if (sessionId != null) {
            // Nếu có SessionId, tìm kiếm thông tin của phiên chiếu trong Firebase
            sessionRef.child(sessionId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        currentSession = snapshot.getValue(MovieSession.class);
                        if (currentSession != null) {
                            displaySessionDetails(currentSession);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(QuanLySuatDetailActivity.this, "Lỗi khi tải dữ liệu!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Thêm sự kiện cho các nút
        btSuaAdmin.setOnClickListener(v -> updateSession());
        btXoaAdmin.setOnClickListener(v -> deleteSession());
        btThemAdmin.setOnClickListener(v -> addSession());
    }

    private void displaySessionDetails(MovieSession session) {
        edGiaVe.setText(session.getPrice());
        edSessionName.setText(session.getSessionName());
        // Hiển thị các thông tin khác nếu cần
    }

    private void updateSession() {
        if (currentSession != null) {
            String price = edGiaVe.getText().toString();
            String sessionName = edSessionName.getText().toString();

            Map<String, Object> updates = new HashMap<>();
            updates.put("price", price);
            updates.put("sessionName", sessionName);

            sessionRef.child(currentSession.getSessionId()).updateChildren(updates)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi cập nhật!", Toast.LENGTH_SHORT).show());
        }
    }

    private void deleteSession() {
        if (currentSession != null) {
            sessionRef.child(currentSession.getSessionId()).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi xóa!", Toast.LENGTH_SHORT).show());
        }
    }

    private void addSession() {
        String price = edGiaVe.getText().toString();
        String sessionName = edSessionName.getText().toString();

        String newSessionId = sessionRef.push().getKey();
        if (newSessionId != null) {
            MovieSession newSession = new MovieSession();
            newSession.setSessionId(newSessionId);
            newSession.setPrice(price);
            newSession.setSessionName(sessionName);

            sessionRef.child(newSessionId).setValue(newSession)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Thêm thành công!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi thêm!", Toast.LENGTH_SHORT).show());
        }
    }
}
