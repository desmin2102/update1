package com.app.myapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Typeface;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.app.myapp.Class.Location;
import com.app.myapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends AppCompatActivity {

    private ListView listView;
    private Button btnConfirm;
    private int selectedPosition = -1;
    private int movieId;
    private List<Location> locations;
    private ArrayAdapter<Location> adapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        listView = findViewById(R.id.listViewLocations);
        btnConfirm = findViewById(R.id.buttonConfirm);

        movieId = getIntent().getIntExtra("movieId", -1);

        // Khởi tạo danh sách địa chỉ
        locations = new ArrayList<>();
        adapter = new ArrayAdapter<Location>(this, android.R.layout.simple_list_item_1, locations) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setText(locations.get(position).getAddress()); // Hiển thị địa chỉ

                // Thay đổi màu nền và tô đậm chữ khi item được chọn
                if (position == selectedPosition) {
                    view.setBackgroundColor(ContextCompat.getColor(LocationActivity.this, android.R.color.holo_blue_light));
                    textView.setTypeface(null, Typeface.BOLD);
                } else {
                    view.setBackgroundColor(ContextCompat.getColor(LocationActivity.this, android.R.color.transparent));
                    textView.setTypeface(null, Typeface.NORMAL);
                }
                return view;
            }
        };
        listView.setAdapter(adapter);

        // Kết nối đến Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Location");

        // Lấy dữ liệu từ Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                locations.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Location location = postSnapshot.getValue(Location.class);
                    locations.add(location);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LocationActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                adapter.notifyDataSetChanged(); // Cập nhật giao diện của ListView
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition != -1) {
                    Location selectedLocation = locations.get(selectedPosition);

                    Intent intent = new Intent(LocationActivity.this, ScheduleActivity.class);
                    intent.putExtra("movieId", movieId); // Truyền ID của bộ phim
                    intent.putExtra("locationAddress", selectedLocation.getAddress()); // Truyền địa chỉ của khu vực
                    startActivity(intent);
                } else {
                    Toast.makeText(LocationActivity.this, "Vui lòng chọn một địa điểm", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
