package com.app.myapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
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

public class QuanLyQuangCaoActivity extends AppCompatActivity {
    private ListView lvAd;
    private ArrayList<String> adList;
    private ArrayList<String> filteredAdList;
    private HashMap<String, String> adMap; // Map để lưu trữ note và adId
    private ArrayAdapter<String> adapter;
    private DatabaseReference databaseReference;
    private EditText searchAd;
    private Handler searchHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_quanlyquangcao);

        lvAd = findViewById(R.id.lvAd);
        searchAd = findViewById(R.id.etSearch);
        adList = new ArrayList<>();
        filteredAdList = new ArrayList<>();
        adMap = new HashMap<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredAdList);
        lvAd.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Ad");
        searchHandler = new Handler();

        fetchAds();

        ImageView imAdd = findViewById(R.id.imAdd);
        imAdd.setOnClickListener(v -> {
            Intent intent = new Intent(QuanLyQuangCaoActivity.this, QuanLyQuangCaoDetailActivity.class);
            startActivity(intent);
        });

        ImageView imBack = findViewById(R.id.imBack);
        imBack.setOnClickListener(v -> finish());

        // Xử lý sự kiện click vào item trong ListView
        lvAd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String note = filteredAdList.get(position);
                String adId = adMap.get(note); // Lấy adId từ map

                if (adId != null) {
                    Intent intent = new Intent(QuanLyQuangCaoActivity.this, QuanLyQuangCaoDetailActivity.class);
                    intent.putExtra("adId", adId);
                    startActivity(intent);
                } else {
                    Toast.makeText(QuanLyQuangCaoActivity.this, "Không tìm thấy quảng cáo này!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Xử lý tìm kiếm với độ trễ
        searchAd.addTextChangedListener(new TextWatcher() {
            private Runnable searchRunnable = new Runnable() {
                @Override
                public void run() {
                    filterAds(searchAd.getText().toString());
                }
            };

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHandler.removeCallbacks(searchRunnable);
                searchHandler.postDelayed(searchRunnable, 500); // Độ trễ 500ms
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void fetchAds() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adList.clear();
                adMap.clear(); // Xóa dữ liệu cũ
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String adId = dataSnapshot.getKey();
                    String note = dataSnapshot.child("note").getValue(String.class);

                    if (adId != null && note != null) {
                        adList.add(note);
                        adMap.put(note, adId); // Lưu note và adId vào map
                    }
                }
                filterAds(searchAd.getText().toString()); // Áp dụng bộ lọc sau khi tải dữ liệu
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuanLyQuangCaoActivity.this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterAds(String query) {
        filteredAdList.clear();
        for (String ad : adList) {
            if (ad.toLowerCase().contains(query.toLowerCase())) {
                filteredAdList.add(ad);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
