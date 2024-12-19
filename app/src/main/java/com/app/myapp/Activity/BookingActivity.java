package com.app.myapp.Activity;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.MenuItem;

import com.app.myapp.Adapter.DayAdapterBooking;  // Đổi tên import từ DayAdapter thành DayAdapterBooking
import com.app.myapp.Class.Movie;
import com.app.myapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BookingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView movieSessionRecyclerView;
    private DayAdapterBooking dayAdapterBooking;  // Thay đổi từ DayAdapter thành DayAdapterBooking
    private List<Calendar> dayList;
    private TextView movieTitleTextView;
    private Toolbar toolbar;
    private Spinner spinnerLocation;
    private String selectedLocationId;
    private String movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerView);
        movieSessionRecyclerView = findViewById(R.id.movieSessionRecyclerView);
        movieTitleTextView = findViewById(R.id.movieTitleTextView);
        spinnerLocation = findViewById(R.id.spinnerLocation);

        // Thiết lập LayoutManager cho RecyclerView để hiển thị ngày
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Thiết lập GridLayoutManager cho movieSessionRecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        movieSessionRecyclerView.setLayoutManager(gridLayoutManager);

        // Khởi tạo danh sách ngày
        dayList = generateDays();

        // Nhận dữ liệu từ Intent
        movieId = getIntent().getStringExtra("movieId");

        // Truy xuất tên phim từ movieId
        if (movieId != null && !movieId.isEmpty()) {
            DatabaseReference movieRef = FirebaseDatabase.getInstance().getReference("Movie").child(movieId);
            movieRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Movie movie = dataSnapshot.getValue(Movie.class);
                    if (movie != null) {
                        movieTitleTextView.setText(movie.getTitle());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Xử lý lỗi nếu cần
                }
            });
        } else {
            Log.e("BookingActivity", "movieId is null or empty");
        }

        // Truy xuất danh sách Location từ Firebase và thiết lập Spinner
        DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("Location");
        locationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> locationAddressList = new ArrayList<>();
                final List<String> locationIds = new ArrayList<>();
                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                    String locationAddress = locationSnapshot.child("address").getValue(String.class);
                    String locationId = locationSnapshot.getKey();
                    if (locationAddress != null && locationId != null) {
                        locationAddressList.add(locationAddress);
                        locationIds.add(locationId);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(BookingActivity.this, android.R.layout.simple_spinner_item, locationAddressList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerLocation.setAdapter(adapter);

                // Đặt Location mặc định là Location đầu tiên
                if (!locationIds.isEmpty()) {
                    spinnerLocation.setSelection(0);
                    selectedLocationId = locationIds.get(0);
                }

                // Lắng nghe sự kiện thay đổi lựa chọn
                spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedLocationId = locationIds.get(position);
                        // Cập nhật lại DayAdapterBooking với locationId mới
                        updateDayAdapterBooking(movieId, selectedLocationId);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Xử lý nếu không có lựa chọn nào được chọn
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    private void updateDayAdapterBooking(String movieId, String locationId) {
        // Truyền movieId và locationId vào DayAdapterBooking và mặc định chọn ngày đầu tiên
        dayAdapterBooking = new DayAdapterBooking(dayList, this, movieSessionRecyclerView, movieId, locationId);
        recyclerView.setAdapter(dayAdapterBooking);
        dayAdapterBooking.selectFirstDay(); // Thêm dòng này để mặc định chọn ngày đầu tiên
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Quay lại trang trước đó
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<Calendar> generateDays() {
        List<Calendar> days = new ArrayList<>();
        Calendar today = Calendar.getInstance();
        int currentMonth = today.get(Calendar.MONTH);

        while (today.get(Calendar.MONTH) == currentMonth) {
            days.add((Calendar) today.clone());
            today.add(Calendar.DAY_OF_MONTH, 1);
        }

        return days;
    }
}
