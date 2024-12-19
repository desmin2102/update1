package com.app.myapp.Activity.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.myapp.Activity.PhimDaXem;
import com.app.myapp.Adapter.MovieAdapter;
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
 * Use the {@link PhimDaXemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhimDaXemFragment extends Fragment {

    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private MovieVerticalAdapter mvAdapter;
    private List<Movie> listMovie = new ArrayList<>();
    private List<Ticket> listTicket = new ArrayList<>();
    private List<MovieSession> listMovieSession = new ArrayList<>();

    private String mParam1;
    private String mParam2;

    // Empty constructor is required for Fragment
    public PhimDaXemFragment() {
        // Required empty public constructor
    }

    // Factory method to create a new instance of PhimDaXemFragment
    public static PhimDaXemFragment newInstance(String param1, String param2) {
        PhimDaXemFragment fragment = new PhimDaXemFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString("param1");
            mParam2 = getArguments().getString("param2");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phim_da_xem, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_phim_Da_Xem);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mvAdapter = new MovieVerticalAdapter(listMovie);
        recyclerView.setAdapter(mvAdapter);

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
            Log.e("AuthError", "User is not logged in.");
        }
    }

    private void fetchMovieSessionsAndMovies() {
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
                listMovie.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Movie movie = snapshot.getValue(Movie.class);
                    for (MovieSession movieSession : listMovieSession) {
                        if (movie.getId().equals(movieSession.getMovieId())) {
                            listMovie.add(movie);
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
        listMovie.clear();
        listTicket.clear();
        listMovieSession.clear();
        mvAdapter.notifyDataSetChanged();
    }
}
