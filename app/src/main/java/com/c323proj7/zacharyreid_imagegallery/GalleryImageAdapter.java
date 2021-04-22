package com.c323proj7.zacharyreid_imagegallery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GalleryImageAdapter extends RecyclerView.Adapter<GalleryImageAdapter.GalleryImageView> {
    private GalleryArrayList images;
    private Context context;
    private AdapterListener listener;

    public interface AdapterListener {
        void onAdapterSignal(int position);
    }
    public GalleryImageAdapter(GalleryArrayList galleryImages, Context context) {
        images = galleryImages;
        this.context = context;
        if(context instanceof GalleryImageAdapter.AdapterListener) {
            listener = (AdapterListener) context;
        }
    }

    @NonNull
    @Override
    public GalleryImageView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_image_layout, parent, false);
        return new GalleryImageView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryImageView holder, final int position) {
        holder.imageView.setImageBitmap(images.get(position).getBitmap());
        holder.titleTextView.setText(images.get(position).getTitle());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(context, ZoomedActivity.class);
                //intent.putExtra("POSITION", position);
                //context.startActivity(intent);
                listener.onAdapterSignal(position);
            }
        });
        //holder.imageView.setImageResource(R.drawable.default_image);

    }

    @Override
    public int getItemCount() {
        //return images.size();
        return (null != images ? images.size() : 0);
    }

    public void remove(String toRemove) {
        while(!toRemove.equals("")) {
            int tempPos = toRemove.indexOf("/");
            String temp = toRemove.substring(0,tempPos);
            int position = Integer.valueOf(temp);
            toRemove = toRemove.substring(tempPos+1);
            if(position != -1 && position<images.size()) {
                images.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
            }
        }


    }

    public class GalleryImageView extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;

        public GalleryImageView(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.galleryImage);
            titleTextView = itemView.findViewById(R.id.titleTextView);

        }

    }
}
