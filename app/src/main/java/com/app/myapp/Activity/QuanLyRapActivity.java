// QuanLyRapActivity.java
package com.app.myapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.myapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class QuanLyRapActivity extends AppCompatActivity {
    private ListView lvRap;
    private EditText searchBar;
    private ArrayList<String> rapList;
    private HashMap<String, String> rapMap; // Map để lưu tên và ID của rạp
    private ArrayAdapter<String> adapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_quanlyrap);

        lvRap = findViewById(R.id.lvRap);
        searchBar = findViewById(R.id.etSearch);
        rapList = new ArrayList<>();
        rapMap = new HashMap<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rapList);
        lvRap.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Location");

        fetchRaps();

        ImageView imAdd = findViewById(R.id.imAdd);
        imAdd.setOnClickListener(v -> {
            Intent intent = new Intent(QuanLyRapActivity.this, QuanLyRapDetailActivity.class);
            startActivity(intent);
        });

        ImageView imBack = findViewById(R.id.imBack);
        imBack.setOnClickListener(v -> finish());

        lvRap.setOnItemClickListener((parent, view, position, id) -> {
            String name = rapList.get(position);
            String rapId = rapMap.get(name);

            if (rapId != null) {
                Intent intent = new Intent(QuanLyRapActivity.this, QuanLyRapDetailActivity.class);
                intent.putExtra("locationId", rapId);
                startActivity(intent);
            } else {
                Toast.makeText(QuanLyRapActivity.this, "Không tìm thấy rạp này!", Toast.LENGTH_SHORT).show();
            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchRaps() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                rapList.clear();
                rapMap.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String rapId = dataSnapshot.getKey();
                    String name = dataSnapshot.child("name").getValue(String.class);

                    if (rapId != null && name != null) {
                        rapList.add(name);
                        rapMap.put(name, rapId);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(QuanLyRapActivity.this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}