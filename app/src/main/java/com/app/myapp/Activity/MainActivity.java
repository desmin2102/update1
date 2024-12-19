package com.app.myapp.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.app.myapp.Activity.fragment.ThayDoiThongTin;
import com.app.myapp.Adapter.AdAdapter;
import com.app.myapp.Adapter.MovieAdapter;
import com.app.myapp.Class.Ad;
import com.app.myapp.Class.Customer;
import com.app.myapp.Class.Movie;
import com.app.myapp.Class.Rank;
import com.app.myapp.MonthlyDeductPointsReceiver;
import com.app.myapp.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import me.relex.circleindicator.CircleIndicator3;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final int MY_REQUEST_CODE = 10;

    private static final int Fragment_ChangePassWord = 4;

    private int mCurrentFragment = 0;

    private FirebaseAuth mAuth;
    private ViewPager2 viewPagerqc;
    private ViewPager2 viewPagermv;
    private ViewPager2 viewpagermMenu;

    private CircleIndicator3 circleIndicator;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    private AdAdapter adAdapter;
    private MovieAdapter mvAdapter;

    private List<Ad> listAd = new ArrayList<>();
    private List<Movie> listMovie=new ArrayList<>();

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;

    private ImageView backgroundImageView;
    private TextView txt_name, txt_email;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupMonthlyDeductAlarm();
        drawerLayout = findViewById(R.id.main);
        backgroundImageView = findViewById(R.id.backgroundImageView);
        navigationView = findViewById(R.id.nav_view);
        ImageView imageAccount = findViewById(R.id.imageAccount);
        // Thiết lập biểu tượng tùy chỉnh làm biểu tượng điều hướng

        // Xử lý sự kiện nhấp vào biểu tượng điều hướng
        imageAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    // Mở ngăn kéo điều hướng
                    drawerLayout.openDrawer(GravityCompat.START);

                } else {
                    startActivity(new Intent(MainActivity.this, Login.class));
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId(); if (id == R.id.home) {
                return true;
            }
            else if (id == R.id.session) {
                startActivity(new Intent(getApplicationContext(), SessionActivity.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();
                return true; }
            else if (id == R.id.movie) {
                startActivity(new Intent(getApplicationContext(), MovieActivity.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();
                return true; }
            else if (id == R.id.promotion) {
                startActivity(new Intent(getApplicationContext(), AdActivity.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();
                return true; } else { return false; }
        });
        FirebaseApp.initializeApp(this);

        init();
    }

    // Hàm khởi tạo
    private void init() {
        // Khởi tạo View quang cao
        initializeViewsAd();
        //Khởi tạo View Movie
        initializeViewsMovie();
        // Khởi tạo nút Booking
        initializeViewsMenu();
        initializeBookingButton();
        // Cài đặt ảnh chạy tự động
        setupAutoSlideImages();
        // Thiết lập ViewPager
        setupViewPagerAd();
        // Lấy dữ liệu từ Firebase
        setupViewPagerMovie();
        showUserInfomation();
        fetchAdsFromDatabase();
        fetchMoviesFromDatabase();
        saveData();
        // Cập nhật ảnh nền khi khởi tạo ứng dụng
        if (!listMovie.isEmpty()) {
            String initialImageUrl = listMovie.get(0).getImageUrl();
            updateBackgroundImage(initialImageUrl); }
        // Đăng ký lắng nghe sự kiện thay đổi trang trong ViewPager2
        viewPagermv.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Lấy URL ảnh của bộ phim hiện tại
                String imageUrl = listMovie.get(position).getImageUrl();
                // Cập nhật nền ứng dụng
                updateBackgroundImage(imageUrl); } });
    }

    private void setupMonthlyDeductAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, MonthlyDeductPointsReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0); alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 30, pendingIntent);
    }

    private void initializeViewsMenu() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        //txt_name = findViewById(R.id.txt_name);
        //nếu k chạy đc thi đổi qua
        txt_name = navigationView.getHeaderView(0).findViewById(R.id.txt_name);
        txt_email = navigationView.getHeaderView(0).findViewById(R.id.txt_email);
    }
    private void updateBackgroundImage(String imageUrl) {
        RequestOptions requestOptions = new RequestOptions()
                .transform(new BlurTransformation(25, 3));
        // Điều chỉnh độ mờ
        Glide.with(this) .load(imageUrl) .apply(requestOptions) .into(backgroundImageView); }

    private void initializeViewsAd() {
        viewPagerqc = findViewById(R.id.viewPager_quangcao);
        circleIndicator = findViewById(R.id.circleIndicator);
    }
    private void initializeViewsMovie() {
        viewPagermv = findViewById(R.id.viewPager_movie);
    }

    private void initializeBookingButton() {
        Button buttonBooking = findViewById(R.id.button);
        buttonBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra xem danh sách phim đã được tải hay chưa
                if (listMovie != null && !listMovie.isEmpty()) {
                    // Lấy đối tượng Movie hiện tại từ ViewPager2
                    Movie currentMovie = listMovie.get(viewPagermv.getCurrentItem());

                    // Mở LocationActivity khi nhấn nút Booking
                    Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                    intent.putExtra("movieId", currentMovie.getId()); // Truyền ID của bộ phim
                    startActivity(intent);
                } else {
                    // Hiển thị thông báo lỗi nếu danh sách phim chưa được tải
                    Toast.makeText(MainActivity.this, "Dữ liệu phim chưa được tải", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    private void setupAutoSlideImages() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (viewPagerqc.getCurrentItem() == listAd.size() - 1) {
                    viewPagerqc.setCurrentItem(0);
                } else {
                    viewPagerqc.setCurrentItem(viewPagerqc.getCurrentItem() + 1);
                }
                handler.postDelayed(this, 3000); // 3 seconds delay
            }
        };
    }

    private void setupViewPagerAd() {
        viewPagerqc.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 3000);
            }
        });
    }

    private void setupViewPagerMovie() {
        mvAdapter = new MovieAdapter(listMovie, viewPagermv);
        viewPagermv.setAdapter(mvAdapter);
        viewPagermv.setClipToPadding(false);
        viewPagermv.setClipChildren(false);
        viewPagermv.setOffscreenPageLimit(4);
        viewPagermv.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(60)); // Điều chỉnh khoảng cách
        compositePageTransformer.addTransformer((view, v) -> {
            float r = 1 - Math.abs(v);
            view.setScaleY(0.85f + r * 0.15f);
        });
        viewPagermv.setPageTransformer(compositePageTransformer);
        viewPagermv.setCurrentItem(mvAdapter.getItemCount() / 2, false);
    }

    private void fetchAdsFromDatabase() {
        DatabaseReference databaseReferenceAd = FirebaseDatabase.getInstance().getReference("Ad");
        databaseReferenceAd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listAd.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Ad ad = snapshot.getValue(Ad.class);
                    listAd.add(ad);
                }
                adAdapter = new AdAdapter(listAd, MainActivity.this); // Truyền context
                viewPagerqc.setAdapter(adAdapter);
                circleIndicator.setViewPager(viewPagerqc);
                handler.postDelayed(runnable, 3000); // Bắt đầu auto slide sau khi dữ liệu được tải
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có.
                Log.e("FirebaseError", "Failed to load data: " + databaseError.getMessage());
            }

        });
    }

    private void fetchMoviesFromDatabase() {
        DatabaseReference databaseReferenceMovie = FirebaseDatabase.getInstance().getReference("Movie");
        databaseReferenceMovie.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listMovie.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Movie movie = snapshot.getValue(Movie.class);
                    listMovie.add(movie);
                }
                mvAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có.
                Log.e("FirebaseError", "Failed to load data: " + databaseError.getMessage());
            }

        });
    }

    private void showUserInfomation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();

        // Lấy các TextView từ nav_header
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView txt_name = headerView.findViewById(R.id.txt_name);
        TextView txt_email = headerView.findViewById(R.id.txt_email);
        TextView txtDiemRank = headerView.findViewById(R.id.txtDiemRank);
        TextView rankhientai = headerView.findViewById(R.id.rankhientai);

        // Lấy thông tin người dùng từ Firebase
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userId);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    Customer customer = task.getResult().getValue(Customer.class);
                    if (customer == null) {
                        Toast.makeText(MainActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                    } else {
                        // Hiển thị tên người dùng
                        String name = customer.getName();
                        Log.d("DEBUG", "Tên người dùng: " + name); // Thêm log để kiểm tra tên
                        if (name == null) {
                            txt_name.setVisibility(View.GONE); // ẩn Tên
                        } else {
                            txt_name.setVisibility(View.VISIBLE);
                            txt_name.setText(name);
                        }

                        // Hiển thị email người dùng
                        String email = user.getEmail();
                        txt_email.setText(email);

                        // Hiển thị điểm số người dùng
                        txtDiemRank.setText(String.valueOf(customer.getDiemTV()));

                        // Lấy Rank từ Firebase dựa trên rankId
                        DatabaseReference rankRef = FirebaseDatabase.getInstance().getReference("Rank").child(customer.getRankId());
                        rankRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> rankTask) {
                                if (rankTask.isSuccessful()) {
                                    Rank rank = rankTask.getResult().getValue(Rank.class);
                                    if (rank != null) {
                                        // Hiển thị tên rank
                                        rankhientai.setText(rank.getName());
                                    } else {
                                        rankhientai.setText("Unknown");
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "Lỗi: Không tìm thấy thông tin rank", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 3000);
    }


    private void saveData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Xử lý các mục trong NavigationView tại đây
        int id = item.getItemId();
        if (id == R.id.nav_history) {
            startActivity(new Intent(MainActivity.this,PhimDaXem.class));
        }else
        if (id == R.id.nav_ticket) {
            startActivity(new Intent(MainActivity.this,VeCuaToi.class));
        }else
        if (id == R.id.nav_changeTT) {
            startActivity(new Intent(MainActivity.this, ThayDoiThongTin.class));
        }else
        if (id == R.id.nav_point) {
            startActivity(new Intent(MainActivity.this, ChinhSach.class));
        }else
        if (id == R.id.nav_change) {
            startActivity(new Intent(MainActivity.this,ChangePassword.class));
        } else
        if(id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, Login.class));
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }



}

