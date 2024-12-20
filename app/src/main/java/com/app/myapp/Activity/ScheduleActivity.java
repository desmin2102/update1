package com.app.myapp.Activity;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.app.myapp.Adapter.DayAdapter;
import com.app.myapp.Class.Room;
import com.app.myapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView movieSessionRecyclerView;
    private DayAdapter dayAdapter;
    private List<Calendar> dayList;
    private Toolbar toolbar;
    private Spinner spinnerLocation;
    private String selectedLocationId;
    private String movieId; // Thêm biến movieId
    private Map<String, Room> roomMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        movieId = getIntent().getStringExtra("movieId"); // Nhận movieId từ Intent

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chọn suất"); // Đặt tiêu đề mới
        }

        recyclerView = findViewById(R.id.recyclerView);
        movieSessionRecyclerView = findViewById(R.id.sessionRecycleview);
        spinnerLocation = findViewById(R.id.spinnerLocation);

        // Thiết lập LayoutManager cho RecyclerView để hiển thị ngày
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Thiết lập LayoutManager cho movieSessionRecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        movieSessionRecyclerView.setLayoutManager(linearLayoutManager);

        // Khởi tạo danh sách ngày
        dayList = generateDays();

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
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ScheduleActivity.this, android.R.layout.simple_spinner_item, locationAddressList);
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
                        // Cập nhật lại DayAdapter với locationId mới
                        updateDayAdapter(selectedLocationId);
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
                Log.e("ScheduleActivity", "Failed to load locations: " + databaseError.getMessage());
            }
        });
    }

    private void updateDayAdapter(String locationId) {
        // Truyền locationId và movieId vào DayAdapter
        dayAdapter = new DayAdapter(dayList, this, movieSessionRecyclerView, locationId, movieId);
        recyclerView.setAdapter(dayAdapter);
        dayAdapter.selectFirstDay(); // Mặc định chọn ngày đầu tiên
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
