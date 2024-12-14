package com.app.myapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.myapp.Class.Customer;
import com.app.myapp.Class.Invoice;
import com.app.myapp.Class.MovieSession;
import com.app.myapp.Class.Movie;
import com.app.myapp.Class.Room;
import com.app.myapp.Class.Seat;
import com.app.myapp.Class.Ticket;
import com.app.myapp.Model.CreateOrder;
import com.app.myapp.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;

public class PaymentActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView movieImageView;
    private TextView movieNameTextView;
    private TextView showTimeTextView;
    private TextView locationTextView;
    private TextView priceTextView;
    private Button btnThanhToan;
    private TextView soLuongVeTextView;
    private TextView viTriGheTextView;
    private String sessionId;
    private String totalPrice;
    private ArrayList<String> selectedSeats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity);

        // Thanh toán ZaloPay
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ZaloPaySDK.init(2554, Environment.SANDBOX);

        // Thiết lập Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Nhận dữ liệu từ Intent
        sessionId = getIntent().getStringExtra("sessionId");
        totalPrice = getIntent().getStringExtra("totalPrice"); // Đảm bảo nhận đúng giá trị
        selectedSeats = getIntent().getStringArrayListExtra("selectedSeats");

        // Ánh xạ các view
        movieImageView = findViewById(R.id.movieImageView);
        movieNameTextView = findViewById(R.id.movieNameTextView);
        showTimeTextView = findViewById(R.id.showTimeTextView);
        locationTextView = findViewById(R.id.locationTextView);
        priceTextView = findViewById(R.id.priceTextView);
        btnThanhToan = findViewById(R.id.btnPayZalo);
        soLuongVeTextView = findViewById(R.id.soluongveTextView);
        viTriGheTextView = findViewById(R.id.vitrigheTextView);

        // Hiển thị giá vé
        priceTextView.setText("Tổng tiền: " + totalPrice + "đồng");
        soLuongVeTextView.setText("Số lượng vé: " + selectedSeats.size());
        viTriGheTextView.setText("Vị trí ghế: " + String.join(", ", selectedSeats));
        // Khởi tạo dữ liệu
        initData(sessionId);

        // Xử lý sự kiện click nút thanh toán
        btnThanhToan.setOnClickListener(v -> {
            Log.d("PaymentActivity", "Button clicked");
            CreateOrder orderApi = new CreateOrder();
            try {
                Log.d("PaymentActivity", "Total price: " + totalPrice);
                JSONObject data = orderApi.createOrder(String.valueOf(totalPrice));
                Log.d("PaymentActivity", "Order created: " + data.toString());
                String code = data.getString("return_code");
                Log.d("PaymentActivity", "Return code: " + code);
                if (code.equals("1")) {
                    String token = data.getString("zp_trans_token");
                    Log.d("PaymentActivity", "Transaction token: " + token);
                    ZaloPaySDK.getInstance().payOrder(PaymentActivity.this, token, "demozpdk://app", new PayOrderListener() {
                        @Override
                        public void onPaymentSucceeded(String s, String s1, String s2) {
                            Log.d("PaymentActivity", "Payment succeeded");
                        }

                        @Override
                        public void onPaymentCanceled(String s, String s1) {
                            Log.d("PaymentActivity", "Payment canceled");
                            Toast.makeText(PaymentActivity.this, "Thanh toán bị hủy", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                            Log.d("PaymentActivity", "Payment error: " + zaloPayError.toString());
                            Toast.makeText(PaymentActivity.this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.d("PaymentActivity", "Return code is not 1");
                }
            } catch (Exception e) {
                Log.e("PaymentActivity", "Exception: ", e);
                e.printStackTrace();
            }
        });
    }

        private void initData(String sessionId) {
        DatabaseReference sessionRef = FirebaseDatabase.getInstance().getReference("MovieSession").child(sessionId);
        sessionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    MovieSession session = dataSnapshot.getValue(MovieSession.class);
                    if (session != null) {
                        String movieId = session.getMovieId();
                        String roomId = session.getRoomId();
                        String startTime = session.getStartTime();
                        String endTime = session.getEndTime();
                        String formattedShowTime = formatShowTime(startTime, endTime);

                        // Hiển thị khung giờ chiếu
                        showTimeTextView.setText(formattedShowTime);

                        loadMovieData(movieId);
                        loadRoomData(roomId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    private String formatShowTime(String startTime, String endTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        try {
            Date startDate = inputFormat.parse(startTime);
            Date endDate = inputFormat.parse(endTime);
            if (startDate != null && endDate != null) {
                String formattedDate = dateFormat.format(startDate);
                String formattedStartTime = timeFormat.format(startDate);
                String formattedEndTime = timeFormat.format(endDate);
                return formattedDate + " " + formattedStartTime + " - " + formattedEndTime;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return startTime + " - " + endTime; // Trường hợp lỗi định dạng thời gian
    }


    private void loadMovieData(String movieId) {
        DatabaseReference movieRef = FirebaseDatabase.getInstance().getReference("Movie").child(movieId);
        movieRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Movie movie = dataSnapshot.getValue(Movie.class);
                    if (movie != null) {
                        String movieName = movie.getTitle();
                        String imageUrl = movie.getImageUrl();

                        // Hiển thị tên phim và hình ảnh
                        movieNameTextView.setText(movieName);
                        Glide.with(PaymentActivity.this).load(imageUrl).into(movieImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    private void loadRoomData(String roomId) {
        DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference("Room").child(roomId);
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Room room = dataSnapshot.getValue(Room.class);
                    if (room != null) {
                        String locationId = room.getLocationId();

                        loadLocationData(locationId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    private void loadLocationData(String locationId) {
        DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("Location").child(locationId);
        locationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String locationAddress = dataSnapshot.child("address").getValue(String.class);

                    // Hiển thị cơ sở
                    locationTextView.setText("Địa điểm: "+locationAddress);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    private void saveInvoiceAndTickets() {
        // Lấy thông tin người dùng
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Lấy thông tin từ giao diện
        String movieName = movieNameTextView.getText().toString();
        String location = locationTextView.getText().toString(); // Giả sử locationTextView hiển thị tên phòng
        String startTime = showTimeTextView.getText().toString();

        // Tạo hóa đơn
        DatabaseReference invoicesRef = FirebaseDatabase.getInstance().getReference("Invoice");
        String invoiceId = invoicesRef.push().getKey();
        if (invoiceId != null) {
            Invoice invoice = new Invoice(invoiceId, movieName, location, selectedSeats.size(), Integer.parseInt(totalPrice), userId);
            invoicesRef.child(invoiceId).setValue(invoice).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(PaymentActivity.this, "Hóa đơn đã được lưu", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PaymentActivity.this, "Lỗi khi lưu hóa đơn", Toast.LENGTH_SHORT).show();
                }
            });

            // Lưu vé cho từng ghế và liên kết với hóa đơn
            DatabaseReference ticketsRef = FirebaseDatabase.getInstance().getReference("Ticket");
            DatabaseReference purchasedSeatsRef = FirebaseDatabase.getInstance().getReference("Seat");
            for (String seatName : selectedSeats) {
                String ticketId = ticketsRef.push().getKey();
                if (ticketId != null) {
                    Ticket ticket = new Ticket(ticketId, movieName, location, seatName, startTime, Integer.parseInt(totalPrice), userId, invoiceId);
                    ticketsRef.child(ticketId).setValue(ticket).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Cộng thêm 300 điểm cho mỗi vé được mua
                            updateCustomerPoints(userId, 300);
                            Toast.makeText(PaymentActivity.this, "Vé đã được lưu", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PaymentActivity.this, "Lỗi khi lưu vé", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                // Lưu thông tin ghế đã mua
                String seatId = purchasedSeatsRef.push().getKey();
                if (seatId != null) {
                    Seat purchasedSeat = new Seat(seatId, null, 0, sessionId, seatName);
                    purchasedSeatsRef.child(seatId).setValue(purchasedSeat).addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Toast.makeText(PaymentActivity.this, "Lỗi khi lưu thông tin ghế đã mua", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

    // Phương thức cộng điểm cho người dùng
    private void updateCustomerPoints(String userId, int points) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Customer customer = snapshot.getValue(Customer.class);
                if (customer != null) {
                    int updatedPoints = customer.getDiemTV() + points;
                    userRef.child("diemTV").setValue(updatedPoints);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}
