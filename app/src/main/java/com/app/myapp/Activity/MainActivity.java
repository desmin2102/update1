package com.app.myapp.Activity;

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

import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;


import com.app.myapp.Adapter.AdAdapter;
import com.app.myapp.Adapter.MovieAdapter;
import com.app.myapp.Class.Ad;
import com.app.myapp.Class.Movie;
import com.app.myapp.Class.User;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import jp.wasabeef.glide.transformations.BlurTransformation;
import me.relex.circleindicator.CircleIndicator3;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final int MY_REQUEST_CODE = 10;

    private int mCurrentFragment = 0;

    private FirebaseAuth mAuth;
    private ViewPager2 viewPagerqc;
    private ViewPager2 viewPagermv;

    private CircleIndicator3 circleIndicator;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    private AdAdapter adAdapter;
    private MovieAdapter mvAdapter;

    private List<Ad> listAd = new ArrayList<>();
    private List<Movie> listMovie = new ArrayList<>();

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;

    private Button buttonBooking;
    private ImageView backgroundImageView;
    private TextView txt_name, txt_email;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.main);
        ImageView imageAccount = findViewById(R.id.imageAccount);
        // Thiết lập biểu tượng tùy chỉnh làm biểu tượng điều hướng
        // Xử lý sự kiện nhấp vào biểu tượng điều hướng
        imageAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở ngăn kéo điều hướng
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        backgroundImageView = findViewById(R.id.backgroundImageView);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FirebaseApp.initializeApp(this);

        init();
        showUserInfomation();
    }

    // Hàm khởi tạo
    private void init() {
        // Khởi tạo View quang cao
        initializeViewsAd();
        //Khởi tạo View Movie
        initializeViewsMovie();
        // Khởi tạo nút Booking
        initializeBookingButton();
        //khởi tạo nav_view,menu
        initializeViewsMenu();
        // Cài đặt ảnh chạy tự động
        setupAutoSlideImages();
        // Thiết lập ViewPager
        setupViewPagerAd();
        // Lấy dữ liệu từ Firebase
        setupViewPagerMovie();
        fetchAdsFromDatabase();
        fetchMoviesFromDatabase();
        saveData();
        // Cập nhật ảnh nền khi khởi tạo ứng dụng
        if (!listMovie.isEmpty()) {
            String initialImageUrl = listMovie.get(0).getImageUrl();
            updateBackgroundImage(initialImageUrl);
        }
        // Đăng ký lắng nghe sự kiện thay đổi trang trong ViewPager2
        viewPagermv.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Lấy URL ảnh của bộ phim hiện tại
                String imageUrl = listMovie.get(position).getImageUrl();
                // Cập nhật nền ứng dụng
                updateBackgroundImage(imageUrl);
            }
        });
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
        Glide.with(this).load(imageUrl).apply(requestOptions).into(backgroundImageView);
    }

    private void initializeViewsAd() {
        viewPagerqc = findViewById(R.id.viewPager_quangcao);
        circleIndicator = findViewById(R.id.circleIndicator);
    }

    private void initializeViewsMovie() {
        viewPagermv = findViewById(R.id.viewPager_movie);
    }

    private void initializeBookingButton() {
        buttonBooking = findViewById(R.id.button);
        buttonBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra xem danh sách phim đã được tải hay chưa
                if (listMovie != null && !listMovie.isEmpty()) {
                    // Lấy đối tượng Movie hiện tại từ ViewPager2
                    Movie currentMovie = listMovie.get(viewPagermv.getCurrentItem());

                    // Mở LocationActivity khi nhấn nút Booking
                    Intent intent = new Intent(MainActivity.this, LocationActivity.class);
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
       //Toast.makeText(this, "User ID: " + userId, Toast.LENGTH_LONG).show();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userId);



        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    User user1 = task.getResult().getValue(User.class);
                    if(user1==null){
                        Toast.makeText(MainActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                    }else {
                        String name = user1.getTen();
                        //Toast.makeText(MainActivity.this, "User ID: " + name, Toast.LENGTH_LONG).show();
                        if (name == null) {
                            txt_name.setVisibility(View.GONE);//ẩn Tên

                        } else {
                            txt_name.setVisibility(View.VISIBLE);
                            txt_name.setText(name);
                        }
                        String email = user.getEmail();
                        txt_email.setText(email);
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Lỗi: Không tìm thấy thông tin người dùng",
                            Toast.LENGTH_SHORT).show();
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
            startActivity(new Intent(MainActivity.this,ChangePassword.class));
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


//    private void addMovieToDatabase(String title, String duration, String movieDateStart, String genre, String rating, String summary, String trailerUrl, String imageUrl) {
//        // Tạo UUID cho bộ phim mới
//        String movieId = UUID.randomUUID().toString();
//
//        // Tạo đối tượng Movie mới
//        Movie movie = new Movie(movieId, "VENOM: THE LAST DANCE", "60min", "movieDateStart", "genre", "rating", "summary", "trailerUrl", "imageUrl");
//
//        // Lưu Movie mới vào Firebase
//        DatabaseReference databaseReferenceMovie = FirebaseDatabase.getInstance().getReference("Movie");
//        databaseReferenceMovie.child(movieId).setValue(movie)
//                .addOnSuccessListener(aVoid -> {
//                    // Thành công
//                    Toast.makeText(getApplicationContext(), "Movie added successfully!", Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(e -> {
//                    // Thất bại
//                    Toast.makeText(getApplicationContext(), "Failed to add movie.", Toast.LENGTH_SHORT).show();
//                });
//    }


}

