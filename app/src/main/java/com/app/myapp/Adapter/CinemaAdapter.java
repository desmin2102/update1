package com.app.myapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.myapp.Class.Cinema;

import java.util.List;

public class CinemaAdapter extends RecyclerView.Adapter<CinemaAdapter.ViewHolder> {
    private List<Cinema> cinemas;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Cinema cinema);
    }

    public CinemaAdapter(List<Cinema> cinemas, OnItemClickListener listener) {
        this.cinemas = cinemas;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Cinema cinema = cinemas.get(position);
        holder.nameTextView.setText(cinema.getName());
        holder.addressTextView.setText(cinema.getAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(cinema);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cinemas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView addressTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(android.R.id.text1);
            addressTextView = itemView.findViewById(android.R.id.text2);
        }
    }
}
