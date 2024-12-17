package com.app.myapp.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.myapp.Adapter.AdActivityAdapter;
import com.app.myapp.Class.Ad;
import com.app.myapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AdActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerViewAds;
    private AdActivityAdapter adActivityAdapter;
    private List<Ad> adList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_activity);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.promotion);
        bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
            int id = menuItem.getItemId();
            if (id == R.id.home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (id == R.id.session) {
                startActivity(new Intent(getApplicationContext(), SessionActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (id == R.id.movie) {
                startActivity(new Intent(getApplicationContext(), MovieActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (id == R.id.promotion) {
                return true;
            } else {
                return false;
            }
        });

        // Setup RecyclerView
        recyclerViewAds = findViewById(R.id.recyclerViewAds);
        recyclerViewAds.setLayoutManager(new LinearLayoutManager(this));

        // Initialize ad list and adapter
        adList = new ArrayList<>();
        adActivityAdapter = new AdActivityAdapter(adList);
        recyclerViewAds.setAdapter(adActivityAdapter);

        // Load ads from Firebase
        loadAdsFromFirebase();
    }

    private void loadAdsFromFirebase() {
        DatabaseReference adsRef = FirebaseDatabase.getInstance().getReference("Ad");
        adsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Ad ad = snapshot.getValue(Ad.class);
                    if (ad != null) {
                        adList.add(ad);
                    }
                }
                adActivityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}
