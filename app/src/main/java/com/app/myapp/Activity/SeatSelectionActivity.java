package com.app.myapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.myapp.Adapter.SeatAdapter;
import com.app.myapp.Class.MovieSession;
import com.app.myapp.Class.Room;
import com.app.myapp.Class.Seat;
import com.app.myapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SeatSelectionActivity extends AppCompatActivity {

    private RecyclerView seatRecyclerView;
    private SeatAdapter seatAdapter;
    private List<Seat> seatList;
    private Set<String> purchasedSeats;
    private Set<String> selectedSeats;
    private String sessionId;
    private String roomId;
    private Toolbar toolbar;
    private TextView priceTextView;
    private Button buyTicketButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        // Thiết lập Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chọn ghế"); // Đặt tiêu đề mới
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Nhận sessionId từ Intent
        sessionId = getIntent().getStringExtra("sessionId");

        seatRecyclerView = findViewById(R.id.seatRecyclerView);
        priceTextView = findViewById(R.id.priceTextView);
        buyTicketButton = findViewById(R.id.buyTicketButton);

        // Truy xuất roomId và price từ sessionId
        DatabaseReference sessionRef = FirebaseDatabase.getInstance().getReference("MovieSession").child(sessionId);
        sessionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    MovieSession session = dataSnapshot.getValue(MovieSession.class);
                    if (session != null) {
                        roomId = session.getRoomId();
                        String price = session.getPrice();
                        priceTextView.setText("Giá vé: " + price + " đồng");

                        if (roomId != null) {
                            // Truy xuất thông tin Room dựa trên roomId
                            loadRoomInfo(roomId);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });

        // Tạo danh sách các ghế đã được mua
        purchasedSeats = new HashSet<>();
        selectedSeats = new HashSet<>();

        // Truy xuất danh sách các ghế đã được mua từ Firebase
        refreshSeatsData();

        // Xử lý sự kiện khi người dùng nhấn vào nút "Mua vé"
        buyTicketButton.setOnClickListener(v -> {
            if (selectedSeats.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SeatSelectionActivity.this);
                builder.setTitle("Thông báo")
                        .setMessage("Vui lòng chọn ghế trước khi mua vé.")
                        .setPositiveButton("OK", null)
                        .show();
            } else {
                // Tính toán tổng tiền
                int totalTickets = selectedSeats.size();
                String price = priceTextView.getText().toString().replace("Giá vé: ", "").replace(" đồng", "");
                int totalPrice = Integer.parseInt(price) * totalTickets;

                // Tạo thông tin vé
                StringBuilder ticketInfo = new StringBuilder();
                for (String seatName : selectedSeats) {
                    ticketInfo.append(seatName).append(", ");
                }
                if (ticketInfo.length() > 0) {
                    ticketInfo.setLength(ticketInfo.length() - 2); // Xóa dấu phẩy cuối cùng
                }

                // Hiển thị AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(SeatSelectionActivity.this);
                builder.setTitle("Thông tin vé")
                        .setMessage("Số lượng vé: " + totalTickets + "\nVị trí ghế: " + ticketInfo + "\nTổng tiền: " + totalPrice + " đồng")
                        .setPositiveButton("Xác nhận", (dialog, which) -> {
                            // Cập nhật trạng thái ghế đã mua lên Firebase trước khi chuyển qua trang hóa đơn
                            DatabaseReference seatsRef = FirebaseDatabase.getInstance().getReference("Seat");
                            for (String seat : selectedSeats) {
                                seatsRef.child(seat).child("status").setValue("purchased");
                            }

                            // Chuyển sang trang hóa đơn
                            Intent intent = new Intent(SeatSelectionActivity.this, PaymentActivity.class);
                            intent.putExtra("sessionId", sessionId);
                            intent.putExtra("totalPrice", String.valueOf(totalPrice));
                            intent.putStringArrayListExtra("selectedSeats", new ArrayList<>(selectedSeats));
                            startActivity(intent);
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshSeatsData();
    }

    private void refreshSeatsData() {
        // Làm mới danh sách ghế từ Firebase
        DatabaseReference seatsRef = FirebaseDatabase.getInstance().getReference("Seat");
        seatsRef.orderByChild("sessionId").equalTo(sessionId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                purchasedSeats.clear();
                for (DataSnapshot seatSnapshot : dataSnapshot.getChildren()) {
                    Seat seat = seatSnapshot.getValue(Seat.class);
                    if (seat != null) {
                        purchasedSeats.add(seat.getSeatName());
                    }
                }
                // Cập nhật lại giao diện
                if (seatAdapter != null) {
                    seatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("SeatSelectionActivity", "Failed to load seats: " + databaseError.getMessage());
            }
        });
    }

    private void loadRoomInfo(String roomId) {
        DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference("Room").child(roomId);
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Room room = dataSnapshot.getValue(Room.class);
                    if (room != null) {
                        int rows = room.getRows();
                        int columns = room.getColumns();

                        // Tạo danh sách ghế dựa trên rows và columns
                        seatList = new ArrayList<>();
                        for (char row = 'A'; row < 'A' + rows; row++) {
                            for (int column = 1; column <= columns; column++) {
                                String seatName = row + String.valueOf(column); // Tạo tên ghế từ hàng và cột
                                seatList.add(new Seat(UUID.randomUUID().toString(), String.valueOf(row), column, sessionId, seatName)); // Gọi hàm khởi tạo với seatName
                            }
                        }

                        // Thiết lập LayoutManager và Adapter cho RecyclerView
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(SeatSelectionActivity.this, columns);
                        seatRecyclerView.setLayoutManager(gridLayoutManager);
                        seatAdapter = new SeatAdapter(seatList, purchasedSeats, selectedSeats, SeatSelectionActivity.this);
                        seatRecyclerView.setAdapter(seatAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }
}
