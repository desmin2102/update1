package com.app.myapp.Activity.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.app.myapp.Adapter.MovieVerticalAdapter;
import com.app.myapp.Class.Movie;
import com.app.myapp.Class.MovieSession;
import com.app.myapp.Class.Ticket;
import com.app.myapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LichSuMuaVeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LichSuMuaVeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LichSuMuaVeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LichSuMuaVeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LichSuMuaVeFragment newInstance(String param1, String param2) {
        LichSuMuaVeFragment fragment = new LichSuMuaVeFragment();
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
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView_LichSuMuaVe;
    private MovieVerticalAdapter mvAdapter;
    private List<Movie> listMovie_lichSuVe = new ArrayList<>();
    private List<Ticket> listTicket = new ArrayList<>();
    private List<MovieSession> listMovieSession = new ArrayList<>();
    private Button btnVeDaDat, btnVeDaHuy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lich_su_mua_ve, container, false);

//        btnVeDaDat = view.findViewById(R.id.btnVeDaDat);
//        btnVeDaHuy = view.findViewById(R.id.btnVeDaHuy);

        // Tìm RecyclerView bằng View đã được inflate
        recyclerView_LichSuMuaVe = view.findViewById(R.id.recyclerView_Lich_Su_Ve);
        recyclerView_LichSuMuaVe.setLayoutManager(new LinearLayoutManager(getContext()));
        mvAdapter = new MovieVerticalAdapter(listMovie_lichSuVe);
        recyclerView_LichSuMuaVe.setAdapter(mvAdapter);
        fetchMoviesFromDatabase();
        return view;
    }

    private void fetchMoviesFromDatabase() {
        DatabaseReference databaseReferenceMovie1 = FirebaseDatabase.getInstance().getReference("Ticket");
        databaseReferenceMovie1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String userId = currentUser.getUid();
                listTicket.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Ticket ticket = snapshot.getValue(Ticket.class);
                    if (userId.equals(ticket.getUserId())) {
                        listTicket.add(ticket);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Failed to load data: " + databaseError.getMessage());
            }
        });

        DatabaseReference databaseReferenceMovie = FirebaseDatabase.getInstance().getReference("MovieSession");
        databaseReferenceMovie.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listMovieSession.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MovieSession movieSession = snapshot.getValue(MovieSession.class);
                    for(Ticket ticket : listTicket) {
                        if(ticket.getSessionId().equals(movieSession.getSessionId()))
                            listMovieSession.add(movieSession);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Failed to load data: " + databaseError.getMessage());
            }
        });


        DatabaseReference databaseReferenceMovie2 = FirebaseDatabase.getInstance().getReference("Movie");
        databaseReferenceMovie2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listMovie_lichSuVe.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Movie movie = snapshot.getValue(Movie.class);
                    for (MovieSession movieSession : listMovieSession) {
                        if (movie.getId().equals(movieSession.getMovieId())) {
                            listMovie_lichSuVe.add(movie);
                        }
                    }
                }
                mvAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Failed to load data: " + databaseError.getMessage());
            }
        });
    }

//    public void onButtonClick(View view) {
//        int viewId = view.getId();
//        if (viewId == R.id.btnVeDaDat) {
//            btnVeDaDat.setBackgroundResource(R.drawable.button_chon);
//            btnVeDaDat.setTextColor(getResources().getColor(android.R.color.white));
//            btnVeDaHuy.setBackgroundResource(R.drawable.button_khong_chon);
//            btnVeDaHuy.setTextColor(getResources().getColor(android.R.color.black));
//        } else if (viewId == R.id.btnVeDaHuy) {
//            btnVeDaHuy.setBackgroundResource(R.drawable.button_chon);
//            btnVeDaHuy.setTextColor(getResources().getColor(android.R.color.white));
//            btnVeDaDat.setBackgroundResource(R.drawable.button_khong_chon);
//            btnVeDaDat.setTextColor(getResources().getColor(android.R.color.black));
//        }
//    }
}