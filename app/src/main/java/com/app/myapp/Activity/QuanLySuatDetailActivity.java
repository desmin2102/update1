package com.app.myapp.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.myapp.Class.Movie;
import com.app.myapp.Class.MovieSession;
import com.app.myapp.Class.Room;
import com.app.myapp.Class.Location;
import com.app.myapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QuanLySuatDetailActivity extends AppCompatActivity {

    private Button btChonNgay, btChonGio, btChonGioHet, btThemAdmin, btSuaAdmin, btXoaAdmin;
    private Spinner spPhim, spPhong, spRap;
    private EditText edGiaVe, edSessionName;

    private DatabaseReference movieSessionRef;
    private DatabaseReference movieRef;
    private DatabaseReference roomRef;
    private DatabaseReference locationRef;

    private String sessionId;

    private Map<String, String> movieMap = new HashMap<>();
    private Map<String, String> roomMap = new HashMap<>();
    private Map<String, String> locationMap = new HashMap<>();

    private String selectedStartDay = "";
    private String selectedStartHour = "";
    private String selectedEndHour = "";


    private MovieSession currentSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_quanlysuat_detail);


        // Ánh xạ các view
        btChonNgay = findViewById(R.id.btChonNgay);
        btChonGio = findViewById(R.id.btChonGio);
        btChonGioHet = findViewById(R.id.btChonGioHet);
        btThemAdmin = findViewById(R.id.btThemAdmin);
        btSuaAdmin = findViewById(R.id.btSuaAdmin);
        btXoaAdmin = findViewById(R.id.btXoaAdmin);

        spPhim = findViewById(R.id.spPhim);
        spPhong = findViewById(R.id.spPhong);
        spRap = findViewById(R.id.spRap);

        edGiaVe = findViewById(R.id.edGiaVe);
        edSessionName = findViewById(R.id.edSessionName);

        sessionId = getIntent().getStringExtra("SessionId");
        if(sessionId==null)
        {
            btSuaAdmin.setVisibility(View.GONE); // Ẩn nút "Sửa" khi thêm
            btXoaAdmin.setVisibility(View.GONE); // Ẩn nút "Xóa" khi thêm
        }
        else {
            btThemAdmin.setVisibility(View.GONE); // Ẩn nút "Thêm"
        }
        // Tham chiếu Firebase
        movieSessionRef = FirebaseDatabase.getInstance().getReference("MovieSession");
        movieRef = FirebaseDatabase.getInstance().getReference("Movie");
        roomRef = FirebaseDatabase.getInstance().getReference("Room");
        locationRef = FirebaseDatabase.getInstance().getReference("Location");


        // Tải dữ liệu và thiết lập các Spinner
        loadSpinners(() -> {
            if (sessionId != null) {
                movieSessionRef.child(sessionId).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        currentSession = snapshot.getValue(MovieSession.class);
                        if (currentSession != null) {
                            displaySessionDetails(getIntent());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
            }
        });

        // Các sự kiện click
        btChonNgay.setOnClickListener(v -> selectStartDay());
        btChonGio.setOnClickListener(v -> selectStartHour());
        btChonGioHet.setOnClickListener(v -> selectEndHour());
        btThemAdmin.setOnClickListener(v -> addNewSession());
        btSuaAdmin.setOnClickListener(v -> updateSessionDetails());
        btXoaAdmin.setOnClickListener(v -> deleteSessionDetails());
        findViewById(R.id.imBack).setOnClickListener(v -> finish());
    }

    private void displaySessionDetails(Intent intent) {
        // Lấy dữ liệu từ Intent
        String sessionName = intent.getStringExtra("MovieName");
        String movieId = intent.getStringExtra("MovieId");
        String roomId = intent.getStringExtra("RoomId");
        String price = intent.getStringExtra("MoviePrice");
        String startDay = intent.getStringExtra("StartDay");
        String startHour = intent.getStringExtra("StartTime");
        String endHour = intent.getStringExtra("EndTime");

        // Thiết lập dữ liệu lên giao diện người dùng
        edSessionName.setText(sessionName);
        edGiaVe.setText(price);
        btChonNgay.setText(startDay);
        btChonGio.setText(startHour);
        btChonGioHet.setText(endHour);

        // Đặt giá trị mặc định cho các Spinner
        setSpinnerSelection(spPhim, movieMap, movieId);
        setSpinnerSelection(spPhong, roomMap, roomId);
        loadLocationFromRoom(roomId, spRap);
    }


    private void setSpinnerSelection(Spinner spinner, Map<String, String> map, String key) {
        String value = map.get(key);
        if (value != null) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
            int position = adapter.getPosition(value);
            spinner.setSelection(position);
        }
    }

    private void loadSpinners(Runnable callback) {
        // Load Movies
        movieRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<String> movieTitles = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Movie movie = data.getValue(Movie.class);
                    if (movie != null) {
                        movieMap.put(movie.getId(), movie.getTitle());
                        movieTitles.add(movie.getTitle());
                    }
                }
                setSpinnerAdapter(spPhim, movieTitles);
                callback.run();
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });

        // Load Locations
        locationRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<String> locationAddresses = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Location location = data.getValue(Location.class);
                    if (location != null) {
                        locationMap.put(location.getId(), location.getAddress()); // Đảm bảo bạn lưu địa chỉ thay vì tên
                        locationAddresses.add(location.getAddress()); // Thêm địa chỉ vào danh sách
                    }
                }
                setSpinnerAdapter(spRap, locationAddresses);

                // Thiết lập sự kiện chọn item cho Spinner địa điểm
                spRap.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedLocationAddress = parent.getItemAtPosition(position).toString();
                        String selectedLocationId = getKeyByValue(locationMap, selectedLocationAddress);

                        // Load danh sách phòng chiếu thuộc địa điểm đã chọn
                        loadRoomsByLocation(selectedLocationId);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });

                // Đặt giá trị mặc định cho Spinner địa điểm nếu có sessionId
                if (sessionId != null && currentSession != null) {
                    roomRef.child(currentSession.getRoomId()).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot roomSnapshot) {
                            String locationId = roomSnapshot.child("locationId").getValue(String.class);
                            if (locationId != null) {
                                String locationAddress = locationMap.get(locationId);
                                if (locationAddress != null) {
                                    int position = locationAddresses.indexOf(locationAddress);
                                    spRap.setSelection(position);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {}
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });

    }
    private String getKeyByValue(Map<String, String> map, String value) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }


    private void loadRoomsByLocation(String locationId) {
        roomRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<String> roomNames = new ArrayList<>();
                Map<String, Room> tempRoomMap = new HashMap<>(); // Tạo một bản đồ tạm thời để lưu trữ dữ liệu

                for (DataSnapshot data : snapshot.getChildren()) {
                    Room room = data.getValue(Room.class);
                    if (room != null && room.getLocationId().equals(locationId)) {
                        roomMap.put(room.getRoomId(), room.getRoomName());
                        roomNames.add(room.getRoomName());
                        tempRoomMap.put(room.getRoomId(), room); // Thêm phòng vào bản đồ tạm thời
                    }
                }
                MovieSession.setRoomMap(tempRoomMap); // Thiết lập roomMap với dữ liệu từ Firebase
                setSpinnerAdapter(spPhong, roomNames);

                // Đặt giá trị mặc định cho Spinner phòng nếu có sessionId và RoomId của phiên hiện tại khớp với LocationId
                if (sessionId != null && currentSession != null) {
                    roomRef.child(currentSession.getRoomId()).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot roomSnapshot) {
                            String locationId = roomSnapshot.child("locationId").getValue(String.class);
                            if (locationId != null && locationId.equals(currentSession.getRoomId())) {
                                String roomName = roomSnapshot.child("roomName").getValue(String.class);
                                if (roomName != null) {
                                    int position = roomNames.indexOf(roomName);
                                    spPhong.setSelection(position);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {}
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }



    private void loadLocationFromRoom(String roomId, Spinner spinner) {
        roomRef.child(roomId).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String locationId = snapshot.child("locationId").getValue(String.class);
                if (locationId != null) {
                    // Lấy địa chỉ thay vì tên
                    String locationAddress = locationMap.get(locationId);
                    if (locationAddress != null) {
                        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
                        int position = adapter.getPosition(locationAddress);
                        spinner.setSelection(position);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }



    private void setSpinnerAdapter(Spinner spinner, List<String> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void selectStartDay() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            // Lấy ngày chọn
            String selectedStartDay = year + "-" + (month + 1) + "-" + day;

            // Kiểm tra xem ngày chọn có phải là ngày trong quá khứ không
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, day);
            if (selectedDate.before(Calendar.getInstance())) {
                Toast.makeText(this, "Ngày không thể là quá khứ!", Toast.LENGTH_SHORT).show();
            } else {
                btChonNgay.setText(selectedStartDay);
                Toast.makeText(this, "Ngày chọn: " + selectedStartDay, Toast.LENGTH_SHORT).show();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void selectStartHour() {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, hour, minute) -> {
            // Lấy giờ chọn
            String selectedStartHour = hour + ":" + minute;

            // Kiểm tra xem giờ chọn có hợp lệ so với thời gian hiện tại không
            Calendar selectedTime = Calendar.getInstance();
            selectedTime.set(Calendar.HOUR_OF_DAY, hour);
            selectedTime.set(Calendar.MINUTE, minute);
            if (selectedTime.before(Calendar.getInstance())) {
                Toast.makeText(this, "Giờ không thể trong quá khứ!", Toast.LENGTH_SHORT).show();
            } else {
                btChonGio.setText(selectedStartHour);
                Toast.makeText(this, "Giờ chọn: " + selectedStartHour, Toast.LENGTH_SHORT).show();
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void selectEndHour() {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, hour, minute) -> {
            // Lấy giờ kết thúc
            String selectedEndHour = hour + ":" + minute;

            // Kiểm tra giờ kết thúc phải sau giờ bắt đầu
            Calendar selectedEndTime = Calendar.getInstance();
            selectedEndTime.set(Calendar.HOUR_OF_DAY, hour);
            selectedEndTime.set(Calendar.MINUTE, minute);

            Calendar selectedStartTime = Calendar.getInstance();
            String startHour = btChonGio.getText().toString();
            String[] startTimeParts = startHour.split(":");
            int startStartHour = Integer.parseInt(startTimeParts[0]);
            int startStartMinute = Integer.parseInt(startTimeParts[1]);
            selectedStartTime.set(Calendar.HOUR_OF_DAY, startStartHour);
            selectedStartTime.set(Calendar.MINUTE, startStartMinute);

            if (selectedEndTime.before(selectedStartTime)) {
                Toast.makeText(this, "Giờ kết thúc phải sau giờ bắt đầu!", Toast.LENGTH_SHORT).show();
            } else {
                btChonGioHet.setText(selectedEndHour);
                Toast.makeText(this, "Giờ hết chọn: " + selectedEndHour, Toast.LENGTH_SHORT).show();
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void addNewSession() {
        // Cập nhật dữ liệu từ giao diện
        String sessionName = edSessionName.getText().toString().trim();
        String movieId = getSelectedKey(spPhim, movieMap);
        String roomId = getSelectedKey(spPhong, roomMap);
        String locationId = getSelectedKey(spRap, locationMap);
        String price = edGiaVe.getText().toString().trim();
        String startDay = btChonNgay.getText().toString().trim();
        String startHour = btChonGio.getText().toString().trim();
        String endHour = btChonGioHet.getText().toString().trim();

        // Thêm log để kiểm tra giá trị của từng trường
        Log.d("QuanLySuatDetailActivity", "Session Name: " + sessionName);
        Log.d("QuanLySuatDetailActivity", "Movie ID: " + movieId);
        Log.d("QuanLySuatDetailActivity", "Room ID: " + roomId);
        Log.d("QuanLySuatDetailActivity", "Location ID: " + locationId);
        Log.d("QuanLySuatDetailActivity", "Price: " + price);
        Log.d("QuanLySuatDetailActivity", "Start Day: " + startDay);
        Log.d("QuanLySuatDetailActivity", "Start Hour: " + startHour);
        Log.d("QuanLySuatDetailActivity", "End Hour: " + endHour);

        // Kiểm tra dữ liệu đầu vào
        if (sessionName.isEmpty()) {
            Log.e("QuanLySuatDetailActivity", "Session Name is empty!");
        }
        if (movieId == null || movieId.isEmpty()) {
            Log.e("QuanLySuatDetailActivity", "Movie ID is empty or null!");
        }
        if (roomId == null || roomId.isEmpty()) {
            Log.e("QuanLySuatDetailActivity", "Room ID is empty or null!");
        }
        if (locationId == null || locationId.isEmpty()) {
            Log.e("QuanLySuatDetailActivity", "Location ID is empty or null!");
        }
        if (price.isEmpty()) {
            Log.e("QuanLySuatDetailActivity", "Price is empty!");
        }
        if (startDay.isEmpty()) {
            Log.e("QuanLySuatDetailActivity", "Start Day is empty!");
        }
        if (startHour.isEmpty()) {
            Log.e("QuanLySuatDetailActivity", "Start Hour is empty!");
        }
        if (endHour.isEmpty()) {
            Log.e("QuanLySuatDetailActivity", "End Hour is empty!");
        }

        // Kiểm tra nếu có bất kỳ trường nào trống
        if (sessionName.isEmpty() || movieId == null || movieId.isEmpty() || roomId == null || roomId.isEmpty() || locationId == null || locationId.isEmpty() || price.isEmpty() || startDay.isEmpty() || startHour.isEmpty() || endHour.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo ID mới cho phiên chiếu
        String newSessionId = UUID.randomUUID().toString();
        if (newSessionId == null) {
            Toast.makeText(this, "Không thể tạo ID cho phiên làm việc!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng MovieSession mới
        MovieSession newSession = new MovieSession(newSessionId, sessionName, movieId, roomId, startDay, startHour, endHour, price);

        // Lưu dữ liệu vào Firebase
        movieSessionRef.child(newSessionId).setValue(newSession)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Thêm thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi thêm: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateSessionDetails() {
        // Kiểm tra nếu đang trong chế độ chỉnh sửa
        if (currentSession == null || currentSession.getSessionId() == null) {
            Toast.makeText(this, "Không tìm thấy phiên làm việc để sửa!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật dữ liệu từ giao diện
        String sessionName = edSessionName.getText().toString().trim();
        String movieId = getSelectedKey(spPhim, movieMap);
        String roomId = getSelectedKey(spPhong, roomMap);
        String locationId = getSelectedKey(spRap, locationMap);
        String price = edGiaVe.getText().toString().trim();
        String startDay = btChonNgay.getText().toString().trim();
        String startHour = btChonGio.getText().toString().trim();
        String endHour = btChonGioHet.getText().toString().trim();

        // Thêm log để kiểm tra giá trị của từng trường
        Log.d("QuanLySuatDetailActivity", "Session Name: " + sessionName);
        Log.d("QuanLySuatDetailActivity", "Movie ID: " + movieId);
        Log.d("QuanLySuatDetailActivity", "Room ID: " + roomId);
        Log.d("QuanLySuatDetailActivity", "Location ID: " + locationId);
        Log.d("QuanLySuatDetailActivity", "Price: " + price);
        Log.d("QuanLySuatDetailActivity", "Start Day: " + startDay);
        Log.d("QuanLySuatDetailActivity", "Start Hour: " + startHour);
        Log.d("QuanLySuatDetailActivity", "End Hour: " + endHour);

        // Kiểm tra dữ liệu đầu vào
        if (sessionName.isEmpty()) {
            Log.e("QuanLySuatDetailActivity", "Session Name is empty!");
        }
        if (movieId == null || movieId.isEmpty()) {
            Log.e("QuanLySuatDetailActivity", "Movie ID is empty or null!");
        }
        if (roomId == null || roomId.isEmpty()) {
            Log.e("QuanLySuatDetailActivity", "Room ID is empty or null!");
        }
        if (locationId == null || locationId.isEmpty()) {
            Log.e("QuanLySuatDetailActivity", "Location ID is empty or null!");
        }
        if (price.isEmpty()) {
            Log.e("QuanLySuatDetailActivity", "Price is empty!");
        }
        if (startDay.isEmpty()) {
            Log.e("QuanLySuatDetailActivity", "Start Day is empty!");
        }
        if (startHour.isEmpty()) {
            Log.e("QuanLySuatDetailActivity", "Start Hour is empty!");
        }
        if (endHour.isEmpty()) {
            Log.e("QuanLySuatDetailActivity", "End Hour is empty!");
        }

        // Kiểm tra nếu có bất kỳ trường nào trống
        if (sessionName.isEmpty() || movieId == null || movieId.isEmpty() || roomId == null || roomId.isEmpty() || locationId == null || locationId.isEmpty() || price.isEmpty() || startDay.isEmpty() || startHour.isEmpty() || endHour.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật đối tượng hiện tại
        currentSession.setSessionName(sessionName);
        currentSession.setMovieId(movieId);
        currentSession.setRoomId(roomId);
        currentSession.setPrice(price);
        currentSession.setStartDay(startDay);
        currentSession.setStartTime(startHour);
        currentSession.setEndTime(endHour);

        // Lưu thay đổi vào Firebase
        movieSessionRef.child(currentSession.getSessionId()).setValue(currentSession)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Sửa thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi sửa: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void deleteSessionDetails() {
        // Kiểm tra nếu đang trong chế độ chỉnh sửa
        if (currentSession == null || currentSession.getSessionId() == null) {
            Toast.makeText(this, "Không tìm thấy phiên làm việc để xóa!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Xóa phiên làm việc khỏi Firebase
        movieSessionRef.child(currentSession.getSessionId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private String getSelectedKey(Spinner spinner, Map<String, String> map) {
        // Lấy giá trị được chọn từ Spinner
        String selectedValue = spinner.getSelectedItem().toString();

        // Tìm key dựa trên giá trị đã chọn
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(selectedValue)) {
                return entry.getKey(); // Trả về key nếu tìm thấy
            }
        }

        return null; // Trả về null nếu không tìm thấy
    }
}

