package com.jangletech.qoogol.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ItemImageBinding;
import com.jangletech.qoogol.model.ImageObject;
import java.util.List;

public class QuestImageAdapter extends RecyclerView.Adapter<QuestImageAdapter.ViewHolder> {

    private static final String TAG = "QuestImageAdapter";
    private List<ImageObject> imageObjects;
    private Context mContext;
    private ItemImageBinding imageBinding;
    private ImageClickListener listener;

    public QuestImageAdapter(Context mContext, List<ImageObject> imageObjects, ImageClickListener listener) {
        this.mContext = mContext;
        this.imageObjects = imageObjects;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuestImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        imageBinding = DataBindingUtil.inflate(inflater, R.layout.item_image, parent, false);
        return new QuestImageAdapter.ViewHolder(imageBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestImageAdapter.ViewHolder holder, int position) {
        ImageObject imageObject = imageObjects.get(position);
        Log.i(TAG, "onBindViewHolder: " + imageObject);
        Glide.with(mContext)
                .load(imageObject.getName())
                .into(holder.itemImageBinding.img);

        imageBinding.img.setOnClickListener(v -> {
            listener.onImageSelected(imageObject);
        });
    }

    @Override
    public int getItemCount() {
        return imageObjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemImageBinding itemImageBinding;

        public ViewHolder(@NonNull ItemImageBinding itemView) {
            super(itemView.getRoot());
            this.itemImageBinding = itemView;
        }
    }

    public interface ImageClickListener {
        void onImageSelected(ImageObject imageObject);
    }
}
