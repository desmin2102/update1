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

    public DayAdapter(List<Calendar> dayList, Context context, RecyclerView movieSessionRecyclerView, String locationId) {
        this.dayList = dayList;
        this.context = context;
        this.movieSessionRecyclerView = movieSessionRecyclerView;
        this.locationId = locationId;
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

    // Lớp ViewHolder không cần 'static'
    public class DayViewHolder extends RecyclerView.ViewHolder {
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

    private void showMovieDetails(Calendar calendar) {
        Log.d("DayAdapter", "Showing all movie sessions for locationId: " + locationId);

        // Lấy tất cả các MovieSession
        DatabaseReference sessionsRef = FirebaseDatabase.getInstance().getReference("MovieSession");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = sdf.format(calendar.getTime());

        sessionsRef.orderByChild("startDay").equalTo(dateString).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, List<MovieSession>> movieSessionsMap = new HashMap<>();

                // Kiểm tra nếu dữ liệu trả về là null hoặc rỗng
                if (!dataSnapshot.exists()) {
                    Log.e("DayAdapter", "No data found for date: " + dateString);
                }

                for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
                    MovieSession session = sessionSnapshot.getValue(MovieSession.class);

                    // Kiểm tra giá trị của session và roomId
                    if (session != null) {
                        Log.d("DayAdapter", "Session loaded: " + session.getMovieId() + " RoomId: " + session.getRoomId());

                        String roomId = session.getRoomId();
                        String locationIdFromRoom = getLocationIdFromRoomId(roomId);

                        // Kiểm tra roomId và locationIdFromRoom có phải là null không
                        if (roomId == null) {
                            Log.e("DayAdapter", "RoomId is null for session with MovieId: " + session.getMovieId());
                        } else {
                            Log.d("DayAdapter", "RoomId: " + roomId);
                        }

                        if (locationIdFromRoom == null) {
                            Log.e("DayAdapter", "LocationIdFromRoom is null for RoomId: " + roomId);
                        } else {
                            Log.d("DayAdapter", "LocationIdFromRoom: " + locationIdFromRoom);
                        }

                        // So sánh locationId từ room và locationId mà bạn đã truyền vào
                        if (locationIdFromRoom != null && locationIdFromRoom.equals(locationId)) {
                            if (!movieSessionsMap.containsKey(session.getMovieId())) {
                                movieSessionsMap.put(session.getMovieId(), new ArrayList<>());
                            }
                            movieSessionsMap.get(session.getMovieId()).add(session);
                        } else {
                            Log.e("DayAdapter", "LocationId does not match. Expected: " + locationId + ", Found: " + locationIdFromRoom);
                        }
                    } else {
                        Log.e("DayAdapter", "Session is null");
                    }
                }

                // Lấy tất cả các Movie
                List<Movie> movies = new ArrayList<>();
                DatabaseReference moviesRef = FirebaseDatabase.getInstance().getReference("Movie");
                moviesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot movieSnapshot) {
                        if (!movieSnapshot.exists()) {
                            Log.e("DayAdapter", "No movies found in the database.");
                        }

                        for (DataSnapshot movieData : movieSnapshot.getChildren()) {
                            Movie movie = movieData.getValue(Movie.class);
                            if (movie != null) {
                                movies.add(movie);
                                Log.d("DayAdapter", "Loaded movie: " + movie.getTitle());
                            } else {
                                Log.e("DayAdapter", "Movie data is null.");
                            }
                        }

                        // Sau khi đã lấy xong Movies và MovieSessions
                        sessionAdapter = new SessionAdapter(movies, movieSessionsMap, roomMap, context);
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

        // Kiểm tra roomId có bị null không
        if (room == null) {
            Log.e("DayAdapter", "Room not found for roomId: " + roomId);
            return null; // Trả về null nếu không tìm thấy Room
        }

        Log.d("DayAdapter", "Room found for roomId: " + roomId);
        return room.getLocationId();
    }

    private void loadRooms() {
        DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference("Room");
        roomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Log.e("DayAdapter", "No rooms found in the database.");
                }

                for (DataSnapshot roomSnapshot : dataSnapshot.getChildren()) {
                    Room room = roomSnapshot.getValue(Room.class);
                    if (room != null) {
                        roomMap.put(room.getRoomId(), room);
                        Log.d("DayAdapter", "Room loaded: " + room.getRoomId());
                    } else {
                        Log.e("DayAdapter", "Room data is null.");
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