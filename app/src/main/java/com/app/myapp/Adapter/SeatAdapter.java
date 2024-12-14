package com.app.myapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.myapp.Class.Seat;
import com.app.myapp.R;

import java.util.List;
import java.util.Set;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatViewHolder> {

    private List<Seat> seatList;
    private Set<String> purchasedSeats;
    private Set<String> selectedSeats; // Thêm biến để theo dõi ghế đang được chọn
    private Context context;

    public SeatAdapter(List<Seat> seatList, Set<String> purchasedSeats, Set<String> selectedSeats, Context context) {
        this.seatList = seatList;
        this.purchasedSeats = purchasedSeats;
        this.selectedSeats = selectedSeats; // Khởi tạo selectedSeats
        this.context = context;
    }

    @NonNull
    @Override
    public SeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_seat, parent, false);
        return new SeatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatViewHolder holder, int position) {
        Seat seat = seatList.get(position);
        holder.seatTextView.setText(seat.getRow() + String.valueOf(seat.getColumn()));

        holder.itemView.setOnClickListener(v -> {
            if (purchasedSeats.contains(seat.getSeatName())) {
                // Ghế đã được mua, không thể chọn
                return;
            }
            if (selectedSeats.contains(seat.getSeatName())) {
                selectedSeats.remove(seat.getSeatName()); // Bỏ chọn ghế
            } else {
                selectedSeats.add(seat.getSeatName()); // Chọn ghế
            }
            notifyItemChanged(position);
        });

        if (purchasedSeats.contains(seat.getSeatName())) {
            holder.seatTextView.setBackgroundResource(R.drawable.seat_unavailable);
        } else if (selectedSeats.contains(seat.getSeatName())) {
            holder.seatTextView.setBackgroundResource(R.drawable.seat_selected);
        } else {
            holder.seatTextView.setBackgroundResource(R.drawable.seat_background);
        }
    }

    @Override
    public int getItemCount() {
        return seatList.size();
    }

    static class SeatViewHolder extends RecyclerView.ViewHolder {
        TextView seatTextView;

        SeatViewHolder(@NonNull View itemView) {
            super(itemView);
            seatTextView = itemView.findViewById(R.id.seatTextView);
        }
    }
}
