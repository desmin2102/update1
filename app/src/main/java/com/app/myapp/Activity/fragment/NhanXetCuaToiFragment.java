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

import com.app.myapp.Adapter.ReviewAdapter;
import com.app.myapp.Class.Review;
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
 * Use the factory method to create an instance of this fragment.
 */
public class NhanXetCuaToiFragment extends Fragment {

    // Parameters for fragment initialization
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Review> listReview = new ArrayList<>();

    public NhanXetCuaToiFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NhanXetCuaToiFragment.
     */
    public static NhanXetCuaToiFragment newInstance(String param1, String param2) {
        NhanXetCuaToiFragment fragment = new NhanXetCuaToiFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listReview.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Review review = snapshot.getValue(Review.class);
                        if (review != null) {
                            listReview.add(review);
                        }
                    }
                    reviewAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("DatabaseError", "Failed to load review data: " + databaseError.getMessage());
                }
            });
        } else {
            Log.e("AuthError", "User is not logged in.");
        }
    }
}
