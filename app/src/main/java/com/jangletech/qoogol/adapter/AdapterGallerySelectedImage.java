package com.jangletech.qoogol.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.List;

import static com.jangletech.qoogol.util.Constant.ADD;
import static com.jangletech.qoogol.util.Constant.LEARNING;

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
    boolean isplaying = false;
    MediaPlayer mediaPlayer = new MediaPlayer();
    private RequestManager requestManager;
    List<String> tempimgList;
    int call_from;

    public AdapterGallerySelectedImage(List<Uri> uriList, List<String> tempimgList, int call_from, Context context, GalleryUplodaHandler galleryUplodaHandler) {
        this.uriList = uriList;
        this.mContext = context;
        this.galleryUplodaHandler = galleryUplodaHandler;
        this.tempimgList = tempimgList;
        this.call_from = call_from;
        requestManager = Glide.with(mContext);
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
        try {
            if (call_from==LEARNING) {
                holder.removeItem.setVisibility(View.GONE);
            }
            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
            circularProgressDrawable.setStrokeWidth(5f);
            circularProgressDrawable.setCenterRadius(30f);
            circularProgressDrawable.start();
            Uri uri = uriList.get(position);
            if (uri != null) {
                if (UtilHelper.isAudio(uriList.get(position),mContext)){
                    loadImage(R.drawable.ic_mp3,holder.image);
                    holder.play.setVisibility(View.VISIBLE);
                    holder.play.setOnClickListener(v -> {
                        if (isplaying) {
                            isplaying = false;
                            holder.play.setImageResource(R.drawable.ic_action_play_circle_filled);
                            if (mediaPlayer.isPlaying())
                                mediaPlayer.stop();
                        } else {
                            isplaying = true;
                            holder.play.setImageResource(R.drawable.ic_action_pause_circle_filled);
                            if (uri.getPath() != null && !uri.getPath().isEmpty()) {
                                try {
                                    mediaPlayer.reset();
                                    mediaPlayer.setDataSource(mContext,uri);
                                    mediaPlayer.prepare();
                                    mediaPlayer.start();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(mContext, mContext.getResources().getString(R.string.server_error), Toast.LENGTH_LONG).show();
                            }
                        }

                    });
                } else if (UtilHelper.isDoc(uriList.get(position),mContext)) {
                    loadImage(R.drawable.ic_documents,holder.image);
                }else {
                    Glide.with(mContext)
                            .load(uriList.get(position))
                            .dontAnimate()
                            .placeholder(circularProgressDrawable)
                            .error(R.drawable.ic_broken_image)
                            .into(holder.image);

                }

                holder.image.setOnClickListener(view -> galleryUplodaHandler.imageClick(uriList.get(position), position));
            } else {
                Glide.with(mContext)
                        .load(R.drawable.ic_add_gray)
                        .placeholder(circularProgressDrawable)
                        .error(R.drawable.ic_broken_image)
                        .into(holder.image);
                holder.image.setOnClickListener(view -> galleryUplodaHandler.addClick(uriList.get(position), position));
            }

            holder.removeItem.setOnClickListener(v -> {
                if (UtilHelper.isAudio(uriList.get(position),mContext)) {
                    isplaying = false;
                    if (mediaPlayer.isPlaying())
                        mediaPlayer.stop();
                }
                galleryUplodaHandler.actionRemoved(position);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadImage(int id,ImageView img) {
        try {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.load)
                    .error(R.drawable.load);
            requestManager.load(id).apply(options).dontAnimate().into(img);
        } catch (Exception e) {
            e.printStackTrace();
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
        ImageView removeItem, play;

        GalleryViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.imageView);
            removeItem = view.findViewById(R.id.removeItem);
            play = view.findViewById(R.id.play);
        }
    }
}

