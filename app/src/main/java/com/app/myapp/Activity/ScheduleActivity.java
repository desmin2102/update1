package com.app.myapp.Activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.view.View;
import android.widget.ImageView;
import com.app.myapp.Adapter.DayAdapter;
import com.app.myapp.R;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private DayAdapter dayAdapter;
    private List<Calendar> dayList;
    private ImageView swipeHintImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        viewPager = findViewById(R.id.viewPager);
        swipeHintImageView = findViewById(R.id.swipeHintImageView);

        // Khởi tạo danh sách ngày
        dayList = generateDays();

        dayAdapter = new DayAdapter(dayList);
        viewPager.setAdapter(dayAdapter);

        // Thêm hiệu ứng PageTransformer
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(20));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.8f + r * 0.2f);
        });
        viewPager.setPageTransformer(compositePageTransformer);

        // Ẩn biểu tượng mũi tên sau khi người dùng vuốt qua
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position > 0) {
                    swipeHintImageView.setVisibility(View.GONE);
                }
            }
        });
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
