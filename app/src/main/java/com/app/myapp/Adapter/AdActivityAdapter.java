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
import com.app.myapp.Activity.AdDetailsActivity;
import com.app.myapp.Class.Ad;
import com.app.myapp.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import java.util.List;

public class AdActivityAdapter extends RecyclerView.Adapter<AdActivityAdapter.AdViewHolder> {
    private List<Ad> adList;
    private Context context;

    public AdActivityAdapter(List<Ad> adList) {
        this.adList = adList;
    }

    @NonNull
    @Override
    public AdActivityAdapter.AdViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        return new AdViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.ad_activity_item, viewGroup, false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull AdActivityAdapter.AdViewHolder adViewHolder, int i) {
        adViewHolder.setDetails(adList.get(i));
    }

    @Override
    public int getItemCount() {
        return adList.size();
    }

    public class AdViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageAd;
        private TextView textViewAdName;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageAd = itemView.findViewById(R.id.imageAd);
            this.textViewAdName = itemView.findViewById(R.id.textViewadName);
        }

        void setDetails(final Ad ad) {
            String imageUrl = ad.getMediaurl();
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(60));
            Glide.with(context)
                    .load(imageUrl)
                    .apply(requestOptions)
                    .into(imageAd);

            textViewAdName.setText(ad.getAdName());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AdDetailsActivity.class);
                    intent.putExtra("imageAdUrl", ad.getMediaurl());
                    intent.putExtra("adId", ad.getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
