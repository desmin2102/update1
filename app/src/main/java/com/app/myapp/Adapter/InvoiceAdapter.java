package com.app.myapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.myapp.Activity.MovieDetailsActivity;
import com.app.myapp.Class.Invoice;
import com.app.myapp.Class.Movie;
import com.app.myapp.Class.MovieSession;
import com.app.myapp.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import java.text.NumberFormat;
import java.util.Locale;

import java.util.List;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.MovieViewHolder> {

    private List<Movie> movieList;
    private List<Invoice> movieInvoicesList;
    private Context context;

    public InvoiceAdapter(List<Movie> movieList,List<Invoice>movieInvoicesList) {
        this.movieInvoicesList=movieInvoicesList;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public InvoiceAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.items_bill,
                viewGroup, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        Invoice invoice= movieInvoicesList.get(position);
        holder.txtMaVe.setText(invoice.getInvoiceId());
        holder.setImage(movieList.get(position));
        holder.tvTitle.setText(movie.getTitle());

        holder.tvDuration.setText("Thời lượng: " + movie.getDuration());
        holder.tvShowTime.setText("Khởi chiếu: " + movie.getMovieDateStart());
        //tùy chỉnh hiển thị giá
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        String formattedPrice = numberFormat.format(invoice.getTotalPrice());
        holder.txtPrice.setText(formattedPrice + " đ");
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        private TextView txtMaVe,tvTitle, tvDuration, tvShowTime, txtPrice;
        private ImageView imageViewmv;


        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMaVe=itemView.findViewById(R.id.txtMaVe);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvShowTime = itemView.findViewById(R.id.tvShowTime);
            imageViewmv = itemView.findViewById(R.id.imgPoster);
            txtPrice = itemView.findViewById(R.id.txtPrice);
        }

        void setImage(final Movie movie) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(60));
            Glide.with(itemView.getContext()) // Sử dụng itemView.getContext() để lấy context
                    .load(movie.getImageUrl())
                    .apply(requestOptions)
                    .into(imageViewmv);

            imageViewmv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), MovieDetailsActivity.class);
                    intent.putExtra("imageMovieUrl", movie.getImageUrl());
                    intent.putExtra("movieId", movie.getId());
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }

}
