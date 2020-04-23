package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.PracticeTestActivity;
import com.jangletech.qoogol.databinding.ImageItemBinding;
import com.jangletech.qoogol.ui.learning.SlideshowDialogFragment;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Pritali on 4/14/2020.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>  {

    ImageItemBinding imageItemBinding;
    ArrayList<String> imgList;
    Activity activity;

    public ImageAdapter(Activity activity,ArrayList<String> imgList) {
        this.activity = activity;
        this.imgList = imgList;
    }

    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        imageItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.image_item, parent, false);

        return new ImageAdapter.ViewHolder(imageItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {

        String img = imgList.get(position);
        try {
            Glide.with(activity).load(new URL(img)).into(imageItemBinding.img);
        } catch (Exception e) {
            e.printStackTrace();
        }

        imageItemBinding.img.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("images", (Serializable) imgList);
            bundle.putInt("position", position);
            FragmentTransaction fragmentTransaction =null;
            if(activity instanceof MainActivity) {
                fragmentTransaction = ((MainActivity) activity).getSupportFragmentManager().beginTransaction();
            }
            if(activity instanceof PracticeTestActivity) {
                fragmentTransaction = ((PracticeTestActivity) activity).getSupportFragmentManager().beginTransaction();
            }
            SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
            newFragment.setArguments(bundle);
            newFragment.show(fragmentTransaction, "slideshow");
        });
    }

    @Override
    public int getItemCount() {
        return imgList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull ImageItemBinding itemView) {
            super(itemView.getRoot());
        }
    }
}
