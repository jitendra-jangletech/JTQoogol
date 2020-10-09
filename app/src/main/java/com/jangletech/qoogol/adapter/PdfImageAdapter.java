package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.ui.upload_question.SCQ_QueFragment;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Pritali on 10/8/2020.
 */
public class PdfImageAdapter extends RecyclerView.Adapter<PdfImageAdapter.ViewHolder> {

    Activity activity;
    ArrayList<String> mAllImages;
    pdfImageItemClick pdfImageItemClick;

    public PdfImageAdapter(Activity activity, ArrayList<String> mAllImages, pdfImageItemClick pdfImageItemClick) {
        this.activity = activity;
        this.mAllImages = mAllImages;
        this.pdfImageItemClick = pdfImageItemClick;
    }

    @NonNull
    @Override
    public PdfImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.selected_gallery_list_item, parent, false);
        return new PdfImageAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PdfImageAdapter.ViewHolder holder, int position) {
       try {
           CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(activity);
           circularProgressDrawable.setStrokeWidth(5f);
           circularProgressDrawable.setCenterRadius(30f);
           circularProgressDrawable.start();
           if (mAllImages.get(position) != null) {
               holder.removeItem.setVisibility(View.VISIBLE);
               Glide.with(activity)
                       .load(mAllImages.get(position).trim())
                       .transition(DrawableTransitionOptions.withCrossFade())
                       .diskCacheStrategy(DiskCacheStrategy.DATA)
                       .placeholder(circularProgressDrawable)
                       .error(R.drawable.ic_broken_image)
                       .into(holder.image);
               holder.removeItem.setOnClickListener(v -> {
                   pdfImageItemClick.onItemRemove(position);
               });
               holder.image.setOnClickListener(view -> pdfImageItemClick.onimageClick(mAllImages.get(position), position));
           } else {
               holder.removeItem.setVisibility(View.GONE);
               Glide.with(activity)
                       .load(R.drawable.ic_add_gray)
                       .placeholder(circularProgressDrawable)
                       .error(R.drawable.ic_broken_image)
                       .into(holder.image);
               holder.image.setOnClickListener(view -> pdfImageItemClick.onimageClick(mAllImages.get(position), position));
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
    }

    @Override
    public int getItemCount() {
        return mAllImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ImageView removeItem;
        public ViewHolder(@NonNull View view) {
            super(view);
            image = view.findViewById(R.id.imageView);
            removeItem = view.findViewById(R.id.removeItem);
        }
    }

    public interface pdfImageItemClick {
        void onimageClick(String media, int position);

        void onItemRemove(int position);
    }
}
