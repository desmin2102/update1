package com.app.myapp.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
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
import com.app.myapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
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
    public   static final  int MY_REQUEST_CODE =10;

    private  static final  int Fragment_PhimDaXem=0;
    private  static final  int Fragment_VecuaTui=1;
    private  static final  int Fragment_ThongTinThanhVien=2;
    private  static final  int Fragment_ChinhSachTichDiem=3;
    private  static final  int Fragment_ChangePassWord=4;

    private int mCurrentFragment = 0;

    private ViewPager2 viewPagerqc;
    private ViewPager2 viewPagermv;

    private CircleIndicator3 circleIndicator;
    private Handler handler = new Handler();
    private Runnable runnable;

    private AdAdapter adAdapter;
    private MovieAdapter mvAdapter;

    private List<Ad> listAd = new ArrayList<>();
    private List<Movie> listMovie=new ArrayList<>();

    private DrawerLayout drawerLayout;
    private FloatingActionButton fab;
    private BottomNavigationView bottomNavigationView;
    private  NavigationView nav_View;



    private Button buttonBooking;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
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
        setupViewPagerMovie();
        fetchAdsFromDatabase();



        fetchMoviesFromDatabase();

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

    //@Override
    private boolean onNavigationItemSelected(@NonNull MenuItem item){
        int id= item.getItemId();
       if(id==R.id.nav_change){
           if(mCurrentFragment!= Fragment_ChangePassWord){
               //replaceFragment(new ChangePassword());
               mCurrentFragment=Fragment_ChangePassWord;
           }
       }
       drawerLayout.closeDrawer(GravityCompat.START);
       return true;
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

    } }

