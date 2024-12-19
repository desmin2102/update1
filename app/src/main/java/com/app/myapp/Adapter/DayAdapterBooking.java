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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class DayAdapterBooking extends RecyclerView.Adapter<DayAdapterBooking.DayViewHolder> {

    private List<Calendar> dayList;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private Context context;
    private RecyclerView movieSessionRecyclerView;
    private MovieSessionBookingAdapter movieSessionAdapter;
    private Map<String, Room> roomMap = new HashMap<>();
    private String movieId;
    private String locationId;

    // Constructor
    public DayAdapterBooking(List<Calendar> dayList, Context context, RecyclerView movieSessionRecyclerView, String movieId, String locationId) {
        if (movieId == null || locationId == null) {
            Log.e("DayAdapterBooking", "movieId or locationId is null");
            return; // Không khởi tạo adapter nếu movieId hoặc locationId là null
        }
        this.dayList = dayList;
        this.context = context;
        this.movieSessionRecyclerView = movieSessionRecyclerView;
        this.movieId = movieId;
        this.locationId = locationId;
        loadRooms();
        if (!dayList.isEmpty()) {
            selectFirstDay();
        }
        Log.d("DayAdapterBooking", "Initialized with movieId: " + movieId + ", locationId: " + locationId);
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

    // Kiểm tra RoomId và trả về LocationId
    private String getLocationIdFromRoomId(String roomId) {
        Room room = roomMap.get(roomId);
        return room != null ? room.getLocationId() : null;
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView dayOfWeekTextView, dateTextView;

        DayViewHolder(@NonNull View itemView) {
            super(itemView);
            dayOfWeekTextView = itemView.findViewById(R.id.dayOfWeekTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }

    // Chọn ngày đầu tiên trong danh sách
    @SuppressLint("NotifyDataSetChanged")
    public void selectFirstDay() {
        if (dayList != null && !dayList.isEmpty()) {
            selectedPosition = 0;
            notifyDataSetChanged();
            showMovieDetails(dayList.get(0));
        }
    }

    // Hiển thị chi tiết các buổi chiếu phim cho ngày được chọn
    @SuppressLint("NotifyDataSetChanged")
    private void showMovieDetails(Calendar calendar) {
        Log.d("DayAdapterBooking", "Showing details for movieId: " + movieId + ", locationId: " + locationId);

        DatabaseReference sessionsRef = FirebaseDatabase.getInstance().getReference("MovieSession");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = sdf.format(calendar.getTime());

        sessionsRef.orderByChild("startDay").equalTo(dateString).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<MovieSession> movieSessions = new ArrayList<>();
                for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
                    MovieSession session = sessionSnapshot.getValue(MovieSession.class);
                    Log.d("DayAdapterBooking", "Loaded session: " + session);

                    if (session != null) {
                        String sessionMovieId = session.getMovieId();
                        String sessionRoomId = session.getRoomId();
                        String sessionLocationId = getLocationIdFromRoomId(sessionRoomId);

                        // Kiểm tra null trước khi so sánh
                        if (sessionMovieId != null && movieId != null && sessionMovieId.equals(movieId) &&
                                sessionLocationId != null && locationId != null && sessionLocationId.equals(locationId)) {
                            movieSessions.add(session);
                        }
                    }
                }

                if (movieSessions.isEmpty()) {
                    Log.d("DayAdapterBooking", "No sessions found for movieId: " + movieId + " on date: " + dateString);
                }

                if (movieSessionAdapter == null) {
                    movieSessionAdapter = new MovieSessionBookingAdapter(movieSessions, roomMap, context);
                    movieSessionRecyclerView.setLayoutManager(new GridLayoutManager(context, 3));
                    movieSessionRecyclerView.setAdapter(movieSessionAdapter);
                } else {
                    movieSessionAdapter.updateSessions(movieSessions);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DayAdapterBooking", "Failed to load movie sessions: " + databaseError.getMessage());
            }
        });
    }

    // Tải thông tin phòng từ Firebase
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
                Log.e("DayAdapterBooking", "Failed to load rooms: " + databaseError.getMessage());
            }
        });
    }
}
