package com.jangletech.qoogol.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ItemCreatePdfBinding;
import com.jangletech.qoogol.databinding.ItemMyPdfListBinding;

import java.util.List;

public class CreatePdfAdapter extends RecyclerView.Adapter<CreatePdfAdapter.ViewHolder> {

    private static final String TAG = "CreatePdfAdapter";
    private ItemCreatePdfBinding itemCreatePdfBinding;
    private ItemMyPdfListBinding itemMyPdfListBinding;
    private Context mContext;
    private List<Uri> imageUri;
    private CreatePdfClickListener listener;
    private int flag;

    public CreatePdfAdapter(Context mContext, List<Uri> imageUri, int flag, CreatePdfClickListener listener) {
        this.mContext = mContext;
        this.imageUri = imageUri;
        this.listener = listener;
        this.flag = flag;
    }

    @NonNull
    @Override
    public CreatePdfAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (flag == 1) {
            itemCreatePdfBinding = DataBindingUtil.inflate(inflater, R.layout.item_create_pdf, parent, false);
            return new CreatePdfAdapter.ViewHolder(itemCreatePdfBinding);
        } else {
            itemMyPdfListBinding = DataBindingUtil.inflate(inflater, R.layout.item_my_pdf_list, parent, false);
            return new CreatePdfAdapter.ViewHolder(itemMyPdfListBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CreatePdfAdapter.ViewHolder holder, int position) {
        Uri uri = imageUri.get(position);
        //String path = uri.toString();
        Log.i(TAG, "onBindViewHolder Uri : " + uri.toString());
        if (flag == 1) {
            Log.i(TAG, "onBindViewHolder Size : " + imageUri.size());
            Glide.with(mContext).load(uri).into(holder.itemCreatePdfBinding.imageView);

            holder.itemCreatePdfBinding.removeItem.setOnClickListener(v -> {
                listener.onRemoveClick(uri, position);
            });

            holder.itemCreatePdfBinding.imageView.setOnClickListener(v -> {
                listener.onItemClick(uri, holder.getAdapterPosition());
            });
        } else {

            holder.itemMyPdfListBinding.tvPdfName.setText(uri.toString().substring(uri.toString().lastIndexOf('/') + 1));
            holder.itemMyPdfListBinding.btnShare.setOnClickListener(v -> {
                listener.onShareClick(uri, holder.getAdapterPosition());
            });

            holder.itemMyPdfListBinding.btnView.setOnClickListener(v -> {
                listener.onRemoveClick(uri, holder.getAdapterPosition());
            });

            holder.itemMyPdfListBinding.btnView.setOnClickListener(v -> {
                listener.onItemClick(uri, holder.getAdapterPosition());
            });

            holder.itemMyPdfListBinding.tvDelete.setOnClickListener(v -> {
                showConfirmDialog(uri, position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return imageUri.size();
    }

    public void updateList(List<Uri> imageList) {
        this.imageUri = imageList;
        notifyItemInserted(imageUri.size() - 1);
    }

    public void deleteItem(int pos) {
        imageUri.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, getItemCount());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemCreatePdfBinding itemCreatePdfBinding;
        ItemMyPdfListBinding itemMyPdfListBinding;

        public ViewHolder(@NonNull ItemCreatePdfBinding itemView) {
            super(itemView.getRoot());
            this.itemCreatePdfBinding = itemView;
        }

        public ViewHolder(@NonNull ItemMyPdfListBinding itemView) {
            super(itemView.getRoot());
            this.itemMyPdfListBinding = itemView;
        }
    }

    public interface CreatePdfClickListener {
        void onRemoveClick(Uri uri, int position);

        void onShareClick(Uri uri, int position);

        void onItemClick(Uri uri, int position);
    }

    private void showConfirmDialog(Uri uri, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogStyle);
        builder.setTitle("Confirm")
                .setMessage("Are you sure, you want to delete this document?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onRemoveClick(uri, pos);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }
}
