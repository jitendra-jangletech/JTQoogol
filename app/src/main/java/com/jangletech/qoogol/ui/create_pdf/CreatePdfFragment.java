package com.jangletech.qoogol.ui.create_pdf;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.CreatePdfAdapter;
import com.jangletech.qoogol.databinding.FragmentCreatePdfBinding;
import com.jangletech.qoogol.ui.BaseFragment;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class CreatePdfFragment extends BaseFragment implements CreatePdfAdapter.CreatePdfClickListener {

    private static final String TAG = "CreatePdfFragment";
    private FragmentCreatePdfBinding mBinding;
    private CreatePdfAdapter mAdapter;
    private Uri imageUri;
    private List<Uri> images = new ArrayList<>();
    private static final int REQUEST_CAMERA = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_pdf, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new CreatePdfAdapter(getActivity(), images, this);
        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));
        mBinding.recyclerView.setAdapter(mAdapter);

        mBinding.btnAddImage.setOnClickListener(v -> {

            Dexter.withActivity(getActivity())
                    .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            String filename = System.currentTimeMillis() + ".jpg";
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.TITLE, filename);
                            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                            imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                            Intent intent = new Intent();
                            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(intent, REQUEST_CAMERA);
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            Toast.makeText(getActivity(), "Storage permission denied.", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    })
                    .withErrorListener(error ->
                            Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT).show())
                    .onSameThread()
                    .check();


        });

        mBinding.btnGeneratePdf.setOnClickListener(v -> {

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA) {
            Log.d(TAG, "onActivityResult REQUEST_CAMERA: " + imageUri);
            try {
                CropImage.activity(imageUri)
                        .start(getContext(), this);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "onFailure onActivityResult: " + e.getMessage());
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE &&
                resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null && resultCode == RESULT_OK) {
                Log.i(TAG, "onActivityResult Uri : " + result.getUri());
                Log.i(TAG, "onActivityResult Size : " + images.size());
                images.add(result.getUri());
                mAdapter.updateList(images);
            }
        }
    }

    @Override
    public void onRemoveClick(int position) {
        Log.i(TAG, "onRemoveClick : " + position);
    }
}
