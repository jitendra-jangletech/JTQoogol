package com.jangletech.qoogol.ui.create_pdf;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
                createNewPdfPage(result.getUri());
                mAdapter.updateList(images);
            }
        }
    }

    @Override
    public void onRemoveClick(int position) {
        Log.i(TAG, "onRemoveClick : " + position);
    }

    private void createNewPdfPage(Uri uri) {
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
            Bitmap resizedBmp = Bitmap.createScaledBitmap(bmp, 960, 1280, false);
            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(960, 1280, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(myPageInfo);

            page.getCanvas().drawBitmap(resizedBmp, 0, 0, null);
            pdfDocument.finishPage(page);

            String pdfPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/Qoogol/pdf_2.pdf";
            File myPDFFile = new File(pdfPath);

            try {
                pdfDocument.writeTo(new FileOutputStream(myPDFFile));
            } catch (IOException e) {
                e.printStackTrace();
            }

            pdfDocument.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void mergePdf() {
//        try {
//            String pdfPath = Environment.getExternalStorageDirectory()
//                    .getAbsolutePath() + "/Qoogol/merged.pdf";
//
//            String pdfPath1 = Environment.getExternalStorageDirectory()
//                    .getAbsolutePath() + "/Qoogol/pdf_1.pdf";
//            File myPDFFile1 = new File(pdfPath1);
//
//            String pdfPath2 = Environment.getExternalStorageDirectory()
//                    .getAbsolutePath() + "/Qoogol/pdf_2.pdf";
//            File myPDFFile2 = new File(pdfPath2);
//
//            com.itextpdf.kernel.pdf.PdfDocument pdfDocument = new com.itextpdf.kernel.pdf.PdfDocument(new PdfReader(pdfPath1), new PdfWriter(pdfPath));
//            com.itextpdf.kernel.pdf.PdfDocument pdfDocument2 = new com.itextpdf.kernel.pdf.PdfDocument(new PdfReader(pdfPath2));
//
//            Pdf merger = new PdfMerger(pdfDocument);
//            merger.merge(pdfDocument2, 1, pdfDocument2.getNumberOfPages());
//
//            pdfDocument2.close();
//            pdfDocument.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
