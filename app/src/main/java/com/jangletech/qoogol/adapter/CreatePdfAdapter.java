package com.jangletech.qoogol.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ItemCreatePdfBinding;

import java.util.List;

public class CreatePdfAdapter extends RecyclerView.Adapter<CreatePdfAdapter.ViewHolder> {

    private ItemCreatePdfBinding itemCreatePdfBinding;
    private Context mContext;
    private List<Uri> imageUri;
    private CreatePdfClickListener listener;

    public CreatePdfAdapter(Context mContext, List<Uri> imageUri, CreatePdfClickListener listener) {
        this.mContext = mContext;
        this.imageUri = imageUri;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CreatePdfAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        itemCreatePdfBinding = DataBindingUtil.inflate(inflater, R.layout.item_create_pdf, parent, false);
        return new CreatePdfAdapter.ViewHolder(itemCreatePdfBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CreatePdfAdapter.ViewHolder holder, int position) {
        Uri uri = imageUri.get(position);
        Glide.with(mContext)
                .load(uri)
                .into(holder.itemCreatePdfBinding.image);

        holder.itemCreatePdfBinding.remove.setOnClickListener(v -> {
            listener.onRemoveClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return imageUri.size();
    }

    public void updateList(List<Uri> imageList) {
        this.imageUri = imageList;
        notifyItemInserted(imageUri.size() - 1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemCreatePdfBinding itemCreatePdfBinding;

        public ViewHolder(@NonNull ItemCreatePdfBinding itemView) {
            super(itemView.getRoot());
            this.itemCreatePdfBinding = itemView;
        }
    }

    public interface CreatePdfClickListener {
        void onRemoveClick(int position);
    }
}
