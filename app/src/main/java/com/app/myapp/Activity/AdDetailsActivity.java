package com.app.myapp.Activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.myapp.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AdDetailsActivity extends AppCompatActivity {

    private TextView qcNoteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ImageView adImageView = findViewById(R.id.ad_detail_image);
        qcNoteTextView = findViewById(R.id.qc_note);

        String imageAdUrl = getIntent().getStringExtra("imageAdUrl");
        Glide.with(this).load(imageAdUrl).into(adImageView);

        // Lấy ID quảng cáo được truyền qua Intent
        String adId = getIntent().getStringExtra("adId");

        if (adId != null) {
            fetchAdNoteFromDatabase(adId);
        } else {
            qcNoteTextView.setText("Không tìm thấy ID quảng cáo.");
        }

        // Sử dụng OnBackPressedDispatcher để xử lý nút back
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void fetchAdNoteFromDatabase(String adId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Ad").child(adId).child("note");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String note = dataSnapshot.getValue(String.class);
                if (note != null) {
                    qcNoteTextView.setText(note);
                } else {
                    qcNoteTextView.setText("Không tìm thấy nội dung quảng cáo.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                qcNoteTextView.setText("Lỗi: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
