package com.app.myapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.myapp.Adapter.CinemaAdapter;
import com.app.myapp.Class.Cinema;
import com.app.myapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SelectLocationActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private List<Cinema> cinemaList = new ArrayList<>();
    private CinemaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        // Thiết lập RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView_locations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CinemaAdapter(cinemaList, new CinemaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Cinema cinema) {
                openScheduleFragment(cinema);
            }
        });
        recyclerView.setAdapter(adapter);

        // Thiết lập Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("cinemas");

        // Lấy dữ liệu từ Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cinemaList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Cinema cinema = snapshot.getValue(Cinema.class);
                    cinemaList.add(cinema);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi
                Log.e("SelectLocationActivity", "Failed to read value.", databaseError.toException());
            }
        });
    }

    private void openScheduleFragment(Cinema cinema) {
        Intent intent = new Intent(SelectLocationActivity.this, MainActivity.class);
        intent.putExtra("cinema_name", cinema.getName());
        intent.putExtra("cinema_address", cinema.getAddress());
        intent.putExtra("fragment", "schedule");
        startActivity(intent);
    }
}
