package com.app.myapp.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.myapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ThongKeActivity extends AppCompatActivity {
    private TableLayout tableThongKe;
    private DatabaseReference databaseReference;
    private EditText etSearch;
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_static);

        tableThongKe = findViewById(R.id.tableThongKeUser);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        ImageView imBack = findViewById(R.id.imBack);
        imBack.setOnClickListener(v -> finish());

        // Spinner
        Spinner spThongke = findViewById(R.id.spThongke);
        TableLayout tableThongKeUser = findViewById(R.id.tableThongKeUser);
        LinearLayout layoutThongkeRap = findViewById(R.id.layoutThongkeRap);

        // Adapter cho Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"Thống Kê User", "Thống Kê Rạp"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spThongke.setAdapter(adapter);

        // Xử lý khi chọn Spinner
        spThongke.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Ẩn cả hai layout trước
                tableThongKeUser.setVisibility(View.GONE);
                layoutThongkeRap.setVisibility(View.GONE);
                etSearch.setVisibility(View.GONE); // Ẩn EditText theo mặc định

                // Hiển thị layout tương ứng
                if (position == 0) { // Thống Kê User
                    tableThongKeUser.setVisibility(View.VISIBLE);
                    etSearch.setVisibility(View.VISIBLE); // Hiển thị EditText cho tìm kiếm
                } else if (position == 1) { // Thống Kê Rạp
                    layoutThongkeRap.setVisibility(View.VISIBLE);
                    loadThongKeRap(); // Gọi hàm load dữ liệu cho Thống Kê Rạp
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không cần xử lý
            }
        });

        // Khởi tạo EditText và thêm TextWatcher
        etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Xóa runnable hiện tại nếu có
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Xóa bất kỳ runnable nào trước đó
                searchHandler.removeCallbacks(searchRunnable);
                // Tạo runnable mới với độ trễ 1000ms
                searchRunnable = () -> filterData(s.toString());
                searchHandler.postDelayed(searchRunnable, 1000);
            }
        });

        loadThongKe();
    }

    private void loadThongKe() {
        tableThongKe.removeAllViews(); // Xóa dữ liệu cũ
        addHeaderRow(); // Thêm tiêu đề cố định

        databaseReference.child("Invoice").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot invoiceSnapshot) {
                Map<String, String> userNames = new HashMap<>();
                databaseReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                        for (DataSnapshot user : userSnapshot.getChildren()) {
                            String userId = user.getKey();
                            String name = user.child("name").getValue(String.class);
                            userNames.put(userId, name);
                        }

                        // Thống kê dữ liệu
                        int totalUsers = 0;
                        int totalTickets = 0;
                        int totalRevenue = 0;

                        for (DataSnapshot invoice : invoiceSnapshot.getChildren()) {
                            String userId = invoice.child("userId").getValue(String.class);
                            String name = userNames.get(userId);
                            int tickets = invoice.child("totalTickets").getValue(Integer.class);
                            int price = invoice.child("totalPrice").getValue(Integer.class);
                            String purchaseDate = invoice.child("purchaseDate").getValue(String.class);

                            addTableRow(name, purchaseDate, String.valueOf(tickets), String.valueOf(price));

                            totalUsers++;
                            totalTickets += tickets;
                            totalRevenue += price;
                        }

                        // Thêm dòng thống kê bên dưới
                        addStatisticsRow(String.valueOf(totalUsers), String.valueOf(totalTickets), String.valueOf(totalRevenue));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý lỗi khi tải dữ liệu người dùng
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi khi tải dữ liệu hóa đơn
            }
        });
    }

    private void loadThongKeRap() {
        LinearLayout layoutThongkeRap = findViewById(R.id.layoutThongkeRap);
        layoutThongkeRap.removeAllViews(); // Xóa dữ liệu cũ

        TableLayout tableThongKeRap = new TableLayout(this);
        layoutThongkeRap.addView(tableThongKeRap);

        // Thêm tiêu đề
        TableRow headerRow = new TableRow(this);
        String[] headers = {"Địa chỉ", "Số vé đã bán"};
        for (String header : headers) {
            TextView tvHeader = new TextView(this);
            tvHeader.setText(header);
            tvHeader.setPadding(8, 8, 8, 8);
            tvHeader.setGravity(Gravity.CENTER);
            tvHeader.setTypeface(null, android.graphics.Typeface.BOLD);
            headerRow.addView(tvHeader);
        }
        tableThongKeRap.addView(headerRow);

        // Tải dữ liệu Firebase
        databaseReference.child("Ticket").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ticketSnapshot) {
                Map<String, Integer> locationTicketCount = new HashMap<>();

                for (DataSnapshot ticket : ticketSnapshot.getChildren()) {
                    String sessionId = ticket.child("sessionId").getValue(String.class);
                    databaseReference.child("MovieSession").child(sessionId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot sessionSnapshot) {
                            String roomId = sessionSnapshot.child("roomId").getValue(String.class);
                            databaseReference.child("Room").child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot roomSnapshot) {
                                    String locationId = roomSnapshot.child("locationId").getValue(String.class);
                                    databaseReference.child("Location").child(locationId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot locationSnapshot) {
                                            String address = locationSnapshot.child("address").getValue(String.class);
                                            locationTicketCount.put(address, locationTicketCount.getOrDefault(address, 0) + 1);

                                            // Xóa hàng và vẽ lại để cập nhật dữ liệu
                                            tableThongKeRap.removeAllViews();
                                            tableThongKeRap.addView(headerRow);

                                            for (Map.Entry<String, Integer> entry : locationTicketCount.entrySet()) {
                                                TableRow row = new TableRow(ThongKeActivity.this);

                                                TextView tvAddress = new TextView(ThongKeActivity.this);
                                                tvAddress.setText(entry.getKey());
                                                tvAddress.setPadding(8, 8, 8, 8);
                                                tvAddress.setGravity(Gravity.CENTER);
                                                row.addView(tvAddress);

                                                TextView tvTickets = new TextView(ThongKeActivity.this);
                                                tvTickets.setText(String.valueOf(entry.getValue()));
                                                tvTickets.setPadding(8, 8, 8, 8);
                                                tvTickets.setGravity(Gravity.CENTER);
                                                row.addView(tvTickets);

                                                tableThongKeRap.addView(row);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {}
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {}
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void addHeaderRow() {
        TableRow headerRow = new TableRow(this);

        String[] headers = {"Tên", "Ngày mua", "Số lượng vé", "Giá vé"};
        for (String header : headers) {
            TextView tvHeader = new TextView(this);
            tvHeader.setText(header);
            tvHeader.setPadding(8, 8, 8, 8);
            tvHeader.setGravity(Gravity.CENTER);
            tvHeader.setTypeface(null, android.graphics.Typeface.BOLD);
            headerRow.addView(tvHeader);
        }

        tableThongKe.addView(headerRow);
    }

    private void addTableRow(String name, String date, String tickets, String price) {
        TableRow row = new TableRow(this);

        // Cột tên
        TextView tvName = new TextView(this);
        tvName.setText(name);
        tvName.setPadding(8, 8, 8, 8);
        tvName.setGravity(Gravity.CENTER);
        row.addView(tvName);

        // Cột ngày mua (chuyển đổi định dạng)
        TextView tvDate = new TextView(this);
        tvDate.setPadding(8, 8, 8, 8);
        tvDate.setGravity(Gravity.CENTER);
        tvDate.setText(convertDateFormat(date));
        row.addView(tvDate);

        // Cột số lượng vé
        TextView tvTickets = new TextView(this);
        tvTickets.setText(tickets);
        tvTickets.setPadding(8, 8, 8, 8);
        tvTickets.setGravity(Gravity.CENTER);
        row.addView(tvTickets);

        // Cột giá vé
        TextView tvPrice = new TextView(this);
        tvPrice.setText(price);
        tvPrice.setPadding(8, 8, 8, 8);
        tvPrice.setGravity(Gravity.CENTER);
        row.addView(tvPrice);

        // Thêm hàng vào TableLayout
        tableThongKe.addView(row);
    }

    private void addStatisticsRow(String totalUsers, String totalTickets, String totalRevenue) {
        TableRow row = new TableRow(this);

        // Cột tổng số người
        TextView tvTotalUsers = new TextView(this);
        tvTotalUsers.setText(totalUsers);
        tvTotalUsers.setPadding(8, 8, 8, 8);
        tvTotalUsers.setGravity(Gravity.CENTER);
        row.addView(tvTotalUsers);

        // Thêm TextView rỗng để trống cột thứ hai
        TextView emptyView = new TextView(this);
        emptyView.setPadding(8, 8, 8, 8);
        row.addView(emptyView);

        // Cột tổng số vé
        TextView tvTotalTickets = new TextView(this);
        tvTotalTickets.setText(totalTickets);
        tvTotalTickets.setPadding(8, 8, 8, 8);
        tvTotalTickets.setGravity(Gravity.CENTER);
        row.addView(tvTotalTickets);

        // Cột tổng doanh thu
        TextView tvTotalRevenue = new TextView(this);
        tvTotalRevenue.setText(totalRevenue);
        tvTotalRevenue.setPadding(8, 8, 8, 8);
        tvTotalRevenue.setGravity(Gravity.CENTER);
        row.addView(tvTotalRevenue);

        // Thêm hàng thống kê vào TableLayout
        tableThongKe.addView(row);
    }

    private void filterData(String query) {
        tableThongKe.removeAllViews(); // Xóa dữ liệu cũ
        addHeaderRow(); // Giữ tiêu đề bảng

        databaseReference.child("Invoice").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot invoiceSnapshot) {
                Map<String, String> userNames = new HashMap<>();
                databaseReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                        for (DataSnapshot user : userSnapshot.getChildren()) {
                            String userId = user.getKey();
                            String name = user.child("name").getValue(String.class);
                            userNames.put(userId, name);
                        }

                        int totalUsers = 0;
                        int totalTickets = 0;
                        int totalRevenue = 0;

                        for (DataSnapshot invoice : invoiceSnapshot.getChildren()) {
                            String userId = invoice.child("userId").getValue(String.class);
                            String name = userNames.get(userId);
                            int tickets = invoice.child("totalTickets").getValue(Integer.class);
                            int price = invoice.child("totalPrice").getValue(Integer.class);
                            String purchaseDate = invoice.child("purchaseDate").getValue(String.class);

                            String formattedDate = convertDateFormat(purchaseDate);
                            if (name != null && (name.toLowerCase().contains(query.toLowerCase()) ||
                                    formattedDate.contains(query))) {
                                addTableRow(name, formattedDate, String.valueOf(tickets), String.valueOf(price));
                                totalUsers++;
                                totalTickets += tickets;
                                totalRevenue += price;
                            }
                        }

                        addStatisticsRow(String.valueOf(totalUsers), String.valueOf(totalTickets), String.valueOf(totalRevenue));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý lỗi khi tải dữ liệu người dùng
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi khi tải dữ liệu hóa đơn
            }
        });
    }

    private String convertDateFormat(String dateStr) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat desiredFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date = originalFormat.parse(dateStr);
            return desiredFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr; // Nếu lỗi, trả về định dạng gốc
        }
    }
}

