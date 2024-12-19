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

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public LichSuMuaVeFragment() {
        // Required empty public constructor
    }

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lich_su_mua_ve, container, false);

        recyclerView_LichSuMuaVe = view.findViewById(R.id.recyclerView_Lich_Su_Ve);
        recyclerView_LichSuMuaVe.setLayoutManager(new LinearLayoutManager(getContext()));
        mvAdapter = new MovieVerticalAdapter(listMovie_lichSuVe);
        recyclerView_LichSuMuaVe.setAdapter(mvAdapter);

        fetchMoviesFromDatabase();
        return view;
    }

    private void fetchMoviesFromDatabase() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Fetch Ticket Data
            DatabaseReference databaseReferenceTicket = FirebaseDatabase.getInstance().getReference("Ticket");
            databaseReferenceTicket.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listTicket.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Ticket ticket = snapshot.getValue(Ticket.class);
                        listTicket.add(ticket);
                    }
                    fetchMovieSessionsAndMovies();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("FirebaseError", "Failed to load ticket data: " + databaseError.getMessage());
                }
            });
        } else {
            // Handle unauthenticated user
            Log.e("AuthError", "User is not logged in.");
        }
    }

    private void fetchMovieSessionsAndMovies() {
        // Fetch MovieSession Data
        DatabaseReference databaseReferenceMovieSession = FirebaseDatabase.getInstance().getReference("MovieSession");
        databaseReferenceMovieSession.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listMovieSession.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MovieSession movieSession = snapshot.getValue(MovieSession.class);
                    for (Ticket ticket : listTicket) {
                        if (ticket.getSessionId().equals(movieSession.getSessionId())) {
                            listMovieSession.add(movieSession);
                        }
                    }
                }
                fetchMoviesFromDatabaseAgain();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Failed to load movie session data: " + databaseError.getMessage());
            }
        });
    }

    private void fetchMoviesFromDatabaseAgain() {
        // Fetch Movie Data
        DatabaseReference databaseReferenceMovie = FirebaseDatabase.getInstance().getReference("Movie");
        databaseReferenceMovie.addValueEventListener(new ValueEventListener() {
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
                Log.e("FirebaseError", "Failed to load movie data: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listMovie_lichSuVe.clear();
        listTicket.clear();
        listMovieSession.clear();
        mvAdapter.notifyDataSetChanged();
    }
    public void clearData() {
        listMovie_lichSuVe.clear();
        listTicket.clear();
        listMovieSession.clear();
        mvAdapter.notifyDataSetChanged();
    }

}