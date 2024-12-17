package com.app.myapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.myapp.Class.Movie;
import com.app.myapp.Class.MovieSession;
import com.app.myapp.Class.Room;
import com.app.myapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {

    private List<Calendar> dayList;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private Context context;
    private RecyclerView movieSessionRecyclerView;
    private SessionAdapter sessionAdapter;
    private Map<String, Room> roomMap = new HashMap<>();
    private String locationId;
    private String movieId; // Thêm biến movieId

    public DayAdapter(List<Calendar> dayList, Context context, RecyclerView movieSessionRecyclerView, String locationId, String movieId) {
        this.dayList = dayList;
        this.context = context;
        this.movieSessionRecyclerView = movieSessionRecyclerView;
        this.locationId = locationId;
        this.movieId = movieId; // Nhận movieId từ constructor
        loadRooms();
        if (!dayList.isEmpty()) {
            selectFirstDay();
        }
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        Calendar calendar = dayList.get(position);

        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());

        String dayOfWeek = dayOfWeekFormat.format(calendar.getTime());
        String date = dateFormat.format(calendar.getTime());

        holder.dayOfWeekTextView.setText(dayOfWeek);
        holder.dateTextView.setText(date);

        holder.itemView.setSelected(selectedPosition == position);
        holder.dayOfWeekTextView.setTypeface(null, holder.itemView.isSelected() ? Typeface.BOLD : Typeface.NORMAL);
        holder.dateTextView.setTypeface(null, holder.itemView.isSelected() ? Typeface.BOLD : Typeface.NORMAL);

        holder.itemView.setOnClickListener(v -> {
            notifyItemChanged(selectedPosition);
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(selectedPosition);
            showMovieDetails(calendar);
        });
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView dayOfWeekTextView, dateTextView;

        DayViewHolder(@NonNull View itemView) {
            super(itemView);
            dayOfWeekTextView = itemView.findViewById(R.id.dayOfWeekTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void selectFirstDay() {
        selectedPosition = 0;
        notifyDataSetChanged();
        showMovieDetails(dayList.get(0));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showMovieDetails(Calendar calendar) {
        Log.d("DayAdapter", "Showing details for locationId: " + locationId);

        DatabaseReference sessionsRef = FirebaseDatabase.getInstance().getReference("MovieSession");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = sdf.format(calendar.getTime());

        sessionsRef.orderByChild("startDay").equalTo(dateString).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, List<MovieSession>> movieSessionsMap = new HashMap<>();

                for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
                    MovieSession session = sessionSnapshot.getValue(MovieSession.class);
                    Log.d("DayAdapter", "Loaded session: " + session);
                    if (session != null && getLocationIdFromRoomId(session.getRoomId()).equals(locationId)) {
                        if (movieId == null || session.getMovieId().equals(movieId)) { // Kiểm tra movieId
                            if (!movieSessionsMap.containsKey(session.getMovieId())) {
                                movieSessionsMap.put(session.getMovieId(), new ArrayList<>());
                            }
                            movieSessionsMap.get(session.getMovieId()).add(session);
                        }
                    }
                }

                List<Movie> movies = new ArrayList<>();
                DatabaseReference moviesRef = FirebaseDatabase.getInstance().getReference("Movie");
                moviesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot movieSnapshot) {
                        for (DataSnapshot movieData : movieSnapshot.getChildren()) {
                            Movie movie = movieData.getValue(Movie.class);
                            if (movie != null && (movieId == null || movie.getId().equals(movieId))) { // Kiểm tra movieId
                                movies.add(movie);
                                Log.d("DayAdapter", "Loaded movie: " + movie.getTitle());
                            }
                        }
                        sessionAdapter = new SessionAdapter(movies, movieSessionsMap, roomMap, context); // Cập nhật constructor của SessionAdapter
                        movieSessionRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                        movieSessionRecyclerView.setAdapter(sessionAdapter);
                        sessionAdapter.notifyDataSetChanged(); // Cập nhật dữ liệu cho RecyclerView
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("DayAdapter", "Failed to load movies: " + databaseError.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DayAdapter", "Failed to load movie sessions: " + databaseError.getMessage());
            }
        });
    }

    private String getLocationIdFromRoomId(String roomId) {
        Room room = roomMap.get(roomId);
        return room != null ? room.getLocationId() : null;
    }

    private void loadRooms() {
        DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference("Room");
        roomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot roomSnapshot : dataSnapshot.getChildren()) {
                    Room room = roomSnapshot.getValue(Room.class);
                    if (room != null) {
                        roomMap.put(room.getRoomId(), room);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DayAdapter", "Failed to load rooms: " + databaseError.getMessage());
            }
        });
    }
}
