package com.app.myapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.myapp.Activity.AdDetailsActivity;
import com.app.myapp.Class.Ad;
import com.app.myapp.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.AdViewHolder> {
    private List<Ad> adList;
    private Context context;

    public AdAdapter(List<Ad> adList, Context context) {
        this.adList = adList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdAdapter.AdViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        return new AdViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.item_ad, viewGroup, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull AdAdapter.AdViewHolder adViewHolder, int i) {
        adViewHolder.setImage(adList.get(i));
    }

    @Override
    public int getItemCount() {
        return adList.size();
    }

    public class AdViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewad;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageViewad = itemView.findViewById(R.id.ad_item_image);
        }

        @SuppressWarnings("unchecked")
        void setImage(final Ad ad) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(60));
            Glide.with(context)
                    .load(ad.getMediaurl())
                    .apply(requestOptions)
                    .into(imageViewad);

            imageViewad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AdDetailsActivity.class);
                    intent.putExtra("imageAdUrl", ad.getMediaurl());
                    intent.putExtra("adId", ad.getId()); // Truyền ID quảng cáo dưới dạng chuỗi
                    context.startActivity(intent);
                }
            });
        }
    }

}
