package com.app.myapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.app.myapp.Adapter.AdAdapter;
import com.app.myapp.Adapter.MovieAdapter;
import com.app.myapp.Class.Ad;
import com.app.myapp.Class.Cinema;
import com.app.myapp.Class.CinemaHall;
import com.app.myapp.Class.Movie;
import com.app.myapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPagerqc;
    private ViewPager2 viewPagermv;

    private CircleIndicator3 circleIndicator;
    private Handler handler = new Handler();
    private Runnable runnable;

    private AdAdapter adAdapter;
    private MovieAdapter mvAdapter;

    private List<Ad> listAd = new ArrayList<>();
    private List<Movie> listMovie=new ArrayList<>();


    private Button buttonBooking;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    // Hàm khởi tạo
    private void init() {
        // Khởi tạo View quang cao
        initializeViewsAd();
        //Khởi tạo View Movie
        initializeViewsMovie();
        // Cài đặt ảnh chạy tự động
        setupAutoSlideImages();
        // Thiết lập ViewPager
        setupViewPagerAd();
        // Lấy dữ liệu từ Firebase
        fetchAdsFromDatabase();

        fetchMoviesFromDatabase();

        setupBookingButton();

        saveData();
    }

    private void initializeViewsAd() {
        viewPagerqc = findViewById(R.id.viewPager_quangcao);
        circleIndicator = findViewById(R.id.circleIndicator);
    }
    private void initializeViewsMovie() {
        viewPagermv = findViewById(R.id.viewPager_movie);
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

    private void setupViewPagerMovie()
    {
        viewPagermv.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(position==listMovie.size()-1)
                {
                    viewPagermv.post(new Runnable() {
                        @Override
                        public void run() {
                                viewPagermv.setCurrentItem(0,false);
                        }
                    });
                }
            }
        });
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
                mvAdapter= new MovieAdapter(listMovie, MainActivity.this); // Truyền context
                viewPagermv.setAdapter(mvAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có.
                Log.e("FirebaseError", "Failed to load data: " + databaseError.getMessage());
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

    //Button Boking
    private void setupBookingButton() {
        Button bookingButton = findViewById(R.id.button);
        bookingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectLocationActivity();
            }
        });
    }

    private void openSelectLocationActivity() {
        Intent intent = new Intent(MainActivity.this, SelectLocationActivity.class);
        startActivity(intent);
    }

    private void saveData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
    } }

