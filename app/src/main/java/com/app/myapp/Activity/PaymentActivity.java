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
import com.app.myapp.Class.Rank;
import com.app.myapp.Class.Room;
import com.app.myapp.Class.Seat;
import com.app.myapp.Class.Ticket;
import com.app.myapp.Model.CreateOrder;
import com.app.myapp.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.Locale;
import java.util.UUID;

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
    private Button btnThanhToanB;
    private TextView soLuongVeTextView;
    private TextView viTriGheTextView;
    private String sessionId;
    private String totalPrice;
    private ArrayList<String> selectedSeats;
    private TextView giamgiaTextView;
    private String priceDiscount;
    private String ticketPriceAfterDiscount;
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
        Log.d("PaymentActivity", "Received sessionId: " + sessionId); Log.d("PaymentActivity", "Received totalPrice: " + totalPrice); Log.d("PaymentActivity", "Received selectedSeats: " + selectedSeats.size());

        // Ánh xạ các view
        movieImageView = findViewById(R.id.movieImageView);
        movieNameTextView = findViewById(R.id.movieNameTextView);
        showTimeTextView = findViewById(R.id.showTimeTextView);
        locationTextView = findViewById(R.id.locationTextView);
        priceTextView = findViewById(R.id.priceTextView);
        btnThanhToan = findViewById(R.id.btnPayZalo);
        btnThanhToanB=findViewById(R.id.btnthanhtoanbth);
        soLuongVeTextView = findViewById(R.id.soluongveTextView);
        viTriGheTextView = findViewById(R.id.vitrigheTextView);
        giamgiaTextView=findViewById(R.id.giamgiaTextView);

        applyDiscountBasedOnRank();
        // Hiển thị giá vé
        soLuongVeTextView.setText("Số lượng vé: " + selectedSeats.size());
        viTriGheTextView.setText("Vị trí ghế: " + String.join(", ", selectedSeats));
        // Khởi tạo dữ liệu
        initData(sessionId);

        btnThanhToanB.setOnClickListener(v -> {
            saveInvoiceAndTickets();
            startActivity(new Intent(PaymentActivity.this, MainActivity.class));
        });
        // Xử lý sự kiện click nút thanh toán
        btnThanhToan.setOnClickListener(v -> {
            Log.d("PaymentActivity", "Button clicked");
            CreateOrder orderApi = new CreateOrder();
            try {
                // Đảm bảo totalPrice là số nguyên
                int totalPriceInt = (int) Double.parseDouble(totalPrice);
                Log.d("PaymentActivity", "Total price (int): " + totalPriceInt);

                JSONObject data = orderApi.createOrder(String.valueOf(totalPriceInt));
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
                    String subReturnCode = data.getString("sub_return_code");
                    String subReturnMessage = data.getString("sub_return_message");
                    Log.d("PaymentActivity", "Sub return code: " + subReturnCode);
                    Log.d("PaymentActivity", "Sub return message: " + subReturnMessage);
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
                        String startDay = session.getStartDay();
                        String startTime = session.getStartTime();
                        String endTime = session.getEndTime();
                        String formattedShowTime = formatShowTime(startDay, startTime, endTime);

                        // Hiển thị khung giờ chiếu
                        showTimeTextView.setText(formattedShowTime);

                        loadMovieData(movieId);
                        loadRoomData(roomId);
                        Log.e("initData","thanh cong");
                    } else {
                        Log.e("initData", "MovieSession data is null");
                    }
                } else {
                    Log.e("initData", "Session data does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("initData", "Firebase query cancelled: " + databaseError.getMessage());
            }
        });
    }


    private String formatShowTime(String startDay, String startTime, String endTime) {
        // Định dạng để chỉ hiển thị thời gian
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            // Xử lý thời gian bắt đầu và kết thúc
            Date start = timeFormat.parse(startTime);
            Date end = timeFormat.parse(endTime);
            if (start != null && end != null) {
                String formattedStartTime = timeFormat.format(start);
                String formattedEndTime = timeFormat.format(end);
                return startDay + " " + formattedStartTime + " - " + formattedEndTime;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return startDay + " " + startTime + " - " + endTime; // Trường hợp lỗi định dạng thời gian
    }

    public void applyDiscountBasedOnRank() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            Log.d("applyDiscountBasedOnRank", "User ID: " + userId);

            // Truy vấn thông tin user từ Firebase
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Customer customer = dataSnapshot.getValue(Customer.class);
                    if (customer != null) {
                        String rankId = customer.getRankId();
                        if (rankId != null) {
                            Log.d("applyDiscountBasedOnRank", "Rank ID: " + rankId);

                            // Truy vấn thông tin rank dựa trên rankId
                            DatabaseReference rankRef = FirebaseDatabase.getInstance().getReference("Rank").child(rankId);
                            rankRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Rank rank = dataSnapshot.getValue(Rank.class);
                                    if (rank != null) {
                                        double discount = rank.getDiscount();
                                        if (discount >= 0 && discount <= 1) {
                                            try {
                                                double totalPriceDouble = Double.parseDouble(totalPrice);  // Chuyển totalPrice từ String sang double

                                                double discountPrice = totalPriceDouble * discount;
                                                double priceAfterDiscount = totalPriceDouble - discountPrice;

                                                totalPrice = String.valueOf(priceAfterDiscount); // Chuyển giá trị sau giảm lại thành String
                                                priceDiscount = String.valueOf(discountPrice); // Cập nhật giá trị giảm giá

                                                giamgiaTextView.setText("Giá đã giảm: " + String.format("%.0f", discountPrice) + " đồng");
                                                priceTextView.setText("Tổng tiền: " + String.format("%.0f", priceAfterDiscount) + " đồng");

                                                if (selectedSeats != null && selectedSeats.size() > 0) {
                                                    double ticketPriceAfterDiscountDouble = priceAfterDiscount / selectedSeats.size();  // Tính toán giá vé sau giảm
                                                    ticketPriceAfterDiscount = String.valueOf((int) ticketPriceAfterDiscountDouble); // Ép kiểu về int để loại bỏ phần thập phân
                                                } else {
                                                    Log.e("applyDiscountBasedOnRank", "No seats selected");
                                                }
                                            } catch (NumberFormatException e) {
                                                Log.e("applyDiscountBasedOnRank", "Invalid totalPrice format: " + totalPrice);
                                            }
                                        } else {
                                            Log.e("applyDiscountBasedOnRank", "Invalid discount value: " + discount);
                                        }
                                    } else {
                                        Log.e("applyDiscountBasedOnRank", "Rank data is null");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e("applyDiscountBasedOnRank", "Rank Data Retrieval Cancelled: " + databaseError.getMessage());
                                }
                            });
                        } else {
                            Log.e("applyDiscountBasedOnRank", "Rank ID is null for customer: " + userId);
                        }
                    } else {
                        Log.e("applyDiscountBasedOnRank", "Customer is null");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("applyDiscountBasedOnRank", "User Data Retrieval Cancelled: " + databaseError.getMessage());
                }
            });
        } else {
            Log.e("applyDiscountBasedOnRank", "User is null");
        }
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

    private String getCurrentDate() { SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); return sdf.format(new Date()); }
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

        // Tạo hóa đơn
        DatabaseReference invoicesRef = FirebaseDatabase.getInstance().getReference("Invoice");
        String invoiceId = UUID.randomUUID().toString();
        if (invoiceId != null) {
            Invoice invoice = new Invoice(invoiceId, selectedSeats.size(), Double.parseDouble(priceDiscount), Double.parseDouble(totalPrice), userId, getCurrentDate());
            invoicesRef.child(invoiceId).setValue(invoice).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                        Toast.makeText(PaymentActivity.this, "Hóa đơn đã được lưu", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PaymentActivity.this, "Lỗi khi lưu hóa đơn", Toast.LENGTH_SHORT).show();
                }
            });

            // Lưu vé cho từng ghế và liên kết với hóa đơn
            DatabaseReference ticketsRef = FirebaseDatabase.getInstance().getReference("Ticket");
            DatabaseReference seatsRef = FirebaseDatabase.getInstance().getReference("Seat");
            for (String seatName : selectedSeats) {
                String ticketId = ticketsRef.push().getKey();
                if (ticketId != null) {
                    // Lấy thông tin từ sessionId
                    DatabaseReference sessionRef = FirebaseDatabase.getInstance().getReference("MovieSession").child(sessionId);
                    sessionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String movieId = dataSnapshot.child("movieId").getValue(String.class);
                                String roomId = dataSnapshot.child("roomId").getValue(String.class); // Lấy roomId

                                // Truy vấn thông tin từ Room để lấy locationId
                                DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference("Room").child(roomId);
                                roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String locationId = dataSnapshot.child("locationId").getValue(String.class); // Lấy locationId từ Room
                                        String startTime = dataSnapshot.child("startTime").getValue(String.class); // Lấy startTime
                                        String price = dataSnapshot.child("price").getValue(String.class); // Lấy price

                                        // Lấy tên phim, tên phòng và tên địa điểm
                                        fetchDetails(movieId, roomId, locationId, seatName, ticketId, startTime, ticketPriceAfterDiscount, userId, invoiceId);

                                        // Lưu ghế đã mua
                                        String purchasedSeatId = seatsRef.push().getKey();
                                        if (purchasedSeatId != null) {
                                            Seat purchasedSeat = new Seat(purchasedSeatId, String.valueOf(seatName.charAt(0)), Integer.parseInt(seatName.substring(1)), sessionId, seatName);
                                            seatsRef.child(purchasedSeatId).setValue(purchasedSeat).addOnCompleteListener(task -> {
                                                if (!task.isSuccessful()) {
                                                    Toast.makeText(PaymentActivity.this, "Lỗi khi lưu thông tin ghế đã mua", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // Cập nhật số lượng ghế còn lại trong MovieSession
                                                    updateAvailableSeats(sessionId, selectedSeats.size());
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(PaymentActivity.this, "Lỗi khi lấy thông tin phòng", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(PaymentActivity.this, "Lỗi khi lấy thông tin phiên chiếu", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }



    private void updateAvailableSeats(String sessionId, int seatsDelta) {
        DatabaseReference sessionRef = FirebaseDatabase.getInstance().getReference("MovieSession").child(sessionId);
        sessionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Lấy thông tin phiên chiếu hiện tại
                    MovieSession session = dataSnapshot.getValue(MovieSession.class);
                    if (session != null) {
                        // Cập nhật số lượng ghế còn lại
                        int updatedAvailableSeats = session.getAvailableSeats() - seatsDelta;
                        if (updatedAvailableSeats < 0) {
                            updatedAvailableSeats = 0; // Đảm bảo số ghế không bị âm
                        }                        sessionRef.child("availableSeats").setValue(updatedAvailableSeats)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.d("PaymentActivity", "Available seats updated successfully.");
                                    } else {
                                        Log.e("PaymentActivity", "Failed to update available seats: " + task.getException().getMessage());
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PaymentActivity", "Failed to read session: " + databaseError.getMessage());
            }
        });
    }




    private void fetchDetails(String movieId, String roomId, String locationId, String seatName, String ticketId, String startTime, String price, String userId, String invoiceId) {
        // Truy xuất tên phim từ movieId
        DatabaseReference movieRef = FirebaseDatabase.getInstance().getReference("Movie").child(movieId);
        movieRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String movieName = dataSnapshot.child("title").getValue(String.class);

                // Truy xuất tên phòng từ roomId
                DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference("Room").child(roomId);
                roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String roomName = dataSnapshot.child("name").getValue(String.class);
                        Log.d("LocationID", "Location ID: " + locationId);
                        if (locationId == null || locationId.isEmpty()) {
                            Log.e("LocationID", "Location ID is null or empty!");
                        }
                        // Truy xuất tên địa điểm từ locationId
                        DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference("Location").child(locationId);
                        locationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String locationName = dataSnapshot.child("name").getValue(String.class);

                                // Lưu vé vào Firebase
                                Ticket ticket = new Ticket(ticketId, seatName, sessionId, price, userId,invoiceId);
                                DatabaseReference ticketsRef = FirebaseDatabase.getInstance().getReference("Ticket");
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

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(PaymentActivity.this, "Lỗi khi lấy thông tin địa điểm", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(PaymentActivity.this, "Lỗi khi lấy thông tin phòng", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PaymentActivity.this, "Lỗi khi lấy thông tin phim", Toast.LENGTH_SHORT).show();
            }
        });
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
