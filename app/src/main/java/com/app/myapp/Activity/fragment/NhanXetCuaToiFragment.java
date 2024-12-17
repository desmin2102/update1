package com.app.myapp.Activity.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.myapp.Adapter.MovieVerticalAdapter;
import com.app.myapp.Adapter.ReviewAdapter;
import com.app.myapp.Class.Movie;
import com.app.myapp.Class.MovieSession;
import com.app.myapp.Class.Review;
import com.app.myapp.Class.Ticket;
import com.app.myapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class NhanXetCuaToiFragment extends Fragment {

    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Review> listReview = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nhan_xet_cua_toi, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_NhanXet);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reviewAdapter = new ReviewAdapter(getContext(), listReview);
        recyclerView.setAdapter(reviewAdapter);

        fetchReviewsFromDatabase();
        return view;
    }

    private void fetchReviewsFromDatabase() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference databaseReferenceReviews = FirebaseDatabase.getInstance().getReference("Review");
            Query query = databaseReferenceReviews.orderByChild("userId").equalTo(userId);

            query.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listReview.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Review review = snapshot.getValue(Review.class);
                        if (review != null) {
                            Log.e("Review Data", "Review: " + review.getContent()); // Kiểm tra log dữ liệu review
                            listReview.add(review);
                        }
                    }
                    reviewAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý lỗi nếu cần
                    Log.e("DatabaseError", databaseError.getMessage());
                }
            });
        } else {
            // Người dùng chưa đăng nhập, xử lý logic đăng nhập hoặc hiển thị thông báo
            Log.e("AuthError", "Người dùng chưa đăng nhập.");
        }
    }
}
