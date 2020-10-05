package com.jangletech.qoogol.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.QuestImageAdapter;
import com.jangletech.qoogol.databinding.DialogAddImageBinding;
import com.jangletech.qoogol.model.ImageObject;
import com.jangletech.qoogol.util.ItemOffsetDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddImageDialog extends Dialog implements QuestImageAdapter.ImageClickListener {

    private static final String TAG = "AddImageDialog";
    private Context mContext;
    private DialogAddImageBinding mBinding;
    private QuestImageAdapter imageAdapter;
    private List<ImageObject> imageObjects = new ArrayList<>();
    private int optionId;
    private AddImageClickListener listener;

    public AddImageDialog(@NonNull Activity mContext, int optionId, AddImageClickListener listener) {
        super(mContext, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.mContext = mContext;
        this.optionId = optionId;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_add_image, null, false);
        setContentView(mBinding.getRoot());
        getFolderImages();

        mBinding.btnClose.setOnClickListener(v -> {
            dismiss();
        });

        mBinding.tvScanPdf.setOnClickListener(v -> {
            listener.onScanPdfClick(optionId);
            dismiss();
        });

        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(mContext, R.dimen.item_offset);
        imageAdapter = new QuestImageAdapter(mContext, imageObjects, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        mBinding.imageRecycler.setLayoutManager(gridLayoutManager);
        mBinding.imageRecycler.addItemDecoration(itemDecoration);
        mBinding.imageRecycler.setAdapter(imageAdapter);
    }

    private void getFolderImages() {
        File folder = new File(Environment.getExternalStorageDirectory() + "/Qoogol/Temp/");
        File[] allFiles = folder.listFiles();
        if (allFiles != null && allFiles.length > 0) {
            for (File file : allFiles) {
                Log.i(TAG, "getFolderImages File Path : " + file.getAbsolutePath());
                imageObjects.add(new ImageObject(file.getAbsolutePath()));
            }
        } else {
            Log.d(TAG, "Folder is Empty: ");
        }
    }

    @Override
    public void onImageSelected(ImageObject imageObject) {
        Log.d(TAG, "onImageSelected Uri : " + imageObject);
        listener.onImageClickListener(imageObject, optionId);
        dismiss();
    }

    public interface AddImageClickListener {
        void onImageClickListener(ImageObject imageObject, int opt);
        void onScanPdfClick(int option);
    }
}
