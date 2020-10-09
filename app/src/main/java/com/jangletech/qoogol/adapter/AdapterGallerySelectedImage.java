package com.jangletech.qoogol.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.jangletech.qoogol.R;

import java.util.List;

/*
 *
 *
 *  * /////////////////////////////////////////////////////////////////////////////////////////////////
 *  * //
 *  * //            Copyright (c) 2020. JangleTech Systems Private Limited, Thane, India
 *  * //
 *  * /////////////////////////////////////////////////////////////////////////////////////////////////
 *
 */

public class AdapterGallerySelectedImage extends RecyclerView.Adapter<AdapterGallerySelectedImage.GalleryViewHolder> {

    private List<Uri> uriList;
    private Context mContext;
    private GalleryUplodaHandler galleryUplodaHandler;

    public AdapterGallerySelectedImage(List<Uri> uriList, Context context, GalleryUplodaHandler galleryUplodaHandler) {
        this.uriList = uriList;
        this.mContext = context;
        this.galleryUplodaHandler = galleryUplodaHandler;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.selected_gallery_list_item, parent, false);
        return new GalleryViewHolder(itemView);
    }

    public void addToList(List<Uri> uriList) {
        this.uriList.addAll(uriList);
        notifyDataSetChanged();
    }

    public void clearList() {
        this.uriList.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();
        if (uriList.get(position) != null) {
            holder.removeItem.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(uriList.get(position))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .placeholder(circularProgressDrawable)
                    .error(R.drawable.ic_broken_image)
                    .into(holder.image);
            holder.removeItem.setOnClickListener(v -> {
                galleryUplodaHandler.actionRemoved(position);
            });
            holder.image.setOnClickListener(view -> galleryUplodaHandler.imageClick(uriList.get(position), position));
        } else {
            holder.removeItem.setVisibility(View.GONE);
            Glide.with(mContext)
                    .load(R.drawable.ic_add_gray)
                    .placeholder(circularProgressDrawable)
                    .error(R.drawable.ic_broken_image)
                    .into(holder.image);
            holder.image.setOnClickListener(view -> galleryUplodaHandler.addClick(uriList.get(position), position));
        }
    }

    @Override
    public int getItemCount() {
        return uriList.size();
    }

    public interface GalleryUplodaHandler {
        void imageClick(Uri media, int position);

        void addClick(Uri media, int position);

        void actionRemoved(int position);
    }


    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ImageView removeItem;

        GalleryViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.imageView);
            removeItem = view.findViewById(R.id.removeItem);
        }
    }
}

