package com.app.myapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.myapp.Activity.SeatSelectionActivity;
import com.app.myapp.Class.MovieSession;
import com.app.myapp.Class.Room;
import com.app.myapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Date;

public class MovieSessionAdapter extends RecyclerView.Adapter<MovieSessionAdapter.MovieSessionViewHolder> {

    private List<MovieSession> movieSessions;
    private Map<String, Room> roomMap;
    private Context context;

    public MovieSessionAdapter(List<MovieSession> movieSessions, Map<String, Room> roomMap, Context context) {
        this.movieSessions = movieSessions;
        this.roomMap = roomMap;
        this.context = context;
    }

    @NonNull
    @Override
    public MovieSessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_session, parent, false);
        return new MovieSessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieSessionViewHolder holder, int position) {
        MovieSession session = movieSessions.get(position);
        Room room = roomMap.get(session.getRoomId());

        if (room != null) {
            holder.roomNameTextView.setText(room.getRoomName());
            holder.availableSeatsTextView.setText(String.valueOf(room.getTotalSeats())); // Lấy totalSeats của Room
        } else {
            holder.roomNameTextView.setText("Unknown Room");
            holder.availableSeatsTextView.setText("N/A");
        }

        // Định dạng lại thời gian chỉ hiển thị giờ và phút
        String formattedStartTime = formatTime(session.getStartTime());
        String formattedEndTime = formatTime(session.getEndTime());
        holder.timeTextView.setText(formattedStartTime + " - " + formattedEndTime);

        // Thêm sự kiện click để mở trang đặt ghế
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SeatSelectionActivity.class);
            intent.putExtra("sessionId", session.getSessionId());
            intent.putExtra("movieId", session.getMovieId());
            intent.putExtra("roomId", session.getRoomId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movieSessions.size();
    }

    public void updateSessions(List<MovieSession> newSessions) {
        this.movieSessions = newSessions;
        notifyDataSetChanged();
    }

    private String formatTime(String dateTime) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        SimpleDateFormat targetFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date date;
        try {
            date = originalFormat.parse(dateTime);
            return targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTime;
        }
    }

    static class MovieSessionViewHolder extends RecyclerView.ViewHolder {
        TextView roomNameTextView, timeTextView, availableSeatsTextView;

        MovieSessionViewHolder(@NonNull View itemView) {
            super(itemView);
            roomNameTextView = itemView.findViewById(R.id.roomNameTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView); // TextView để hiển thị thời gian
            availableSeatsTextView = itemView.findViewById(R.id.availableSeatsTextView);
        }
    }
}
