package com.app.myapp.Activity.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.myapp.Adapter.InvoiceAdapter;
import com.app.myapp.Class.Invoice;
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
 * Use the {@link LichSuMuaHangFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LichSuMuaHangFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public LichSuMuaHangFragment() {
        // Required empty public constructor
    }

    public static LichSuMuaHangFragment newInstance(String param1, String param2) {
        LichSuMuaHangFragment fragment = new LichSuMuaHangFragment();
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

            if (mParam1 == null || mParam2 == null) {
                Log.e("LichSuMuaHangFragment", "Arguments are missing or invalid.");
            }
        }
    }

    private FirebaseAuth mAuth;
    private RecyclerView recyclerView_LichSuMuaHang;
    private InvoiceAdapter mvAdapter;
    private List<Movie> listMovie_lichSuMua = new ArrayList<>();
    private List<Ticket> listTicket = new ArrayList<>();
    private List<Invoice> listInvoice = new ArrayList<>();
    private List<MovieSession> listMovieSession = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lich_su_mua_hang, container, false);

        recyclerView_LichSuMuaHang = view.findViewById(R.id.recyclerView_Lich_Su_Mua);
        recyclerView_LichSuMuaHang.setLayoutManager(new LinearLayoutManager(getContext()));
        mvAdapter = new InvoiceAdapter(listMovie_lichSuMua, listInvoice);
        recyclerView_LichSuMuaHang.setAdapter(mvAdapter);

        fetchMoviesFromDatabase();
        return view;
    }

    private void fetchMoviesFromDatabase() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // 1. Lấy danh sách hóa đơn
            DatabaseReference databaseReferenceInvoice = FirebaseDatabase.getInstance().getReference("Invoice");
            databaseReferenceInvoice.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listInvoice.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Invoice invoice = snapshot.getValue(Invoice.class);
                        if (invoice != null) {
                            listInvoice.add(invoice);
                        }
                    }
                    fetchTickets();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("FirebaseError", "Failed to load invoices: " + databaseError.getMessage());
                }
            });
        } else {
            Log.e("AuthError", "User is not logged in.");
        }
    }

    private void fetchTickets() {
        // 2. Lấy danh sách vé
        DatabaseReference databaseReferenceTicket = FirebaseDatabase.getInstance().getReference("Ticket");
        databaseReferenceTicket.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listTicket.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Ticket ticket = snapshot.getValue(Ticket.class);
                    if (ticket != null) {
                        for (Invoice invoice : listInvoice) {
                            if (invoice.getInvoiceId() != null && invoice.getInvoiceId().equals(ticket.getInvoiceId())) {
                                listTicket.add(ticket);
                            }
                        }
                    }
                }
                fetchMovieSessions();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Failed to load tickets: " + databaseError.getMessage());
            }
        });
    }

    private void fetchMovieSessions() {
        // 3. Lấy danh sách phiên chiếu phim
        DatabaseReference databaseReferenceMovieSession = FirebaseDatabase.getInstance().getReference("MovieSession");
        databaseReferenceMovieSession.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listMovieSession.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MovieSession movieSession = snapshot.getValue(MovieSession.class);
                    if (movieSession != null) {
                        for (Ticket ticket : listTicket) {
                            if (ticket.getSessionId() != null && ticket.getSessionId().equals(movieSession.getSessionId())) {
                                listMovieSession.add(movieSession);
                            }
                        }
                    }
                }
                fetchMovies();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Failed to load movie sessions: " + databaseError.getMessage());
            }
        });
    }

    private void fetchMovies() {
        // 4. Lấy danh sách phim
        DatabaseReference databaseReferenceMovie = FirebaseDatabase.getInstance().getReference("Movie");
        databaseReferenceMovie.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listMovie_lichSuMua.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Movie movie = snapshot.getValue(Movie.class);
                    if (movie != null) {
                        for (MovieSession movieSession : listMovieSession) {
                            if (movie.getId() != null && movie.getId().equals(movieSession.getMovieId())) {
                                listMovie_lichSuMua.add(movie);
                            }
                        }
                    }
                }
                mvAdapter.notifyDataSetChanged(); // Cập nhật giao diện
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Failed to load movies: " + databaseError.getMessage());
            }
        });
    }
    public void clearData() {
        listMovie_lichSuMua.clear();
        listTicket.clear();
        listInvoice.clear();
        listMovieSession.clear();
        mvAdapter.notifyDataSetChanged();
    }

}
