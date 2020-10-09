package com.jangletech.qoogol.ui.upload_question;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.FragmentScanQuestDescBinding;
import com.jangletech.qoogol.model.UploadQuestion;
import com.jangletech.qoogol.ui.BaseFragment;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ScanQuestionDescFragment extends BaseFragment {

    private static final int REQUEST_GALLERY = 0;
    private static final int REQUEST_CAMERA = 1;
    private Uri imageUri;
    private UploadQuestion uploadQuestion;
    private static final String TAG = "ScanQuestionDescFragmen";
    private FragmentScanQuestDescBinding mBinding;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_scan_quest_desc, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null && getArguments().getSerializable("Question") != null) {
            uploadQuestion = (UploadQuestion) getArguments().getSerializable("Question");
            Log.i(TAG, "onActivityCreated : "+uploadQuestion.getSubjectName());
            getActionBar().setTitle("Scan " + uploadQuestion.getSubjectName() + " Question");
        }

        mBinding.scan.setOnClickListener(v -> {
            Dexter.withActivity(getActivity())
                    .withPermissions(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (report.areAllPermissionsGranted()) {
                        String filename = System.currentTimeMillis() + ".jpg";
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, filename);
                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                        imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                        Intent intent = new Intent();
                        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, REQUEST_CAMERA);
                        //dispatchTakePictureIntent();
                    } else {
                        Log.e(TAG, "onPermissionsChecked Error : ");
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
            }).check();
        });

        mBinding.Gallery.setOnClickListener(v -> {
            Dexter.withActivity(getActivity())
                    .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            Intent i = new Intent(
                                    Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(i, REQUEST_GALLERY);
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK && data != null) {
            try {
                CropImage.activity(data.getData())
                        .start(getContext(), this);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "onFailure onActivityResult: " + e.getMessage());
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
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
                inspect(result.getUri());
            }
        }
    }

    private void inspect(Uri uri) {
        Log.d(TAG, "inspect Uri : " + uri);
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = getActivity().getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inSampleSize = 2;
            options.inScreenDensity = DisplayMetrics.DENSITY_LOW;
            bitmap = BitmapFactory.decodeStream(is, null, options);
            inspectFromBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.w(TAG, "Failed to find the file: " + uri, e);
        } finally {

            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }

            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.w(TAG, "Failed to close InputStream", e);
                }
            }
        }
    }

    private void inspectFromBitmap(Bitmap bitmap) {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getActivity()).build();
        try {
            if (!textRecognizer.isOperational()) {
                new AlertDialog.
                        Builder(getActivity()).
                        setMessage("Text recognizer could not be set up on your device").show();
                return;
            }

            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> origTextBlocks = textRecognizer.detect(frame);
            List<TextBlock> textBlocks = new ArrayList<>();
            for (int i = 0; i < origTextBlocks.size(); i++) {
                TextBlock textBlock = origTextBlocks.valueAt(i);
                textBlocks.add(textBlock);
            }
            Collections.sort(textBlocks, new Comparator<TextBlock>() {
                @Override
                public int compare(TextBlock o1, TextBlock o2) {
                    int diffOfTops = o1.getBoundingBox().top - o2.getBoundingBox().top;
                    int diffOfLefts = o1.getBoundingBox().left - o2.getBoundingBox().left;
                    if (diffOfTops != 0) {
                        return diffOfTops;
                    }
                    return diffOfLefts;
                }
            });

            StringBuilder detectedText = new StringBuilder();
            for (TextBlock textBlock : textBlocks) {
                if (textBlock != null && textBlock.getValue() != null) {
                    detectedText.append(textBlock.getValue());
                    detectedText.append("\n");
                }
            }
            //mBinding.tvDetected.setText(detectedText.toString().trim());
            uploadQuestion.setQuestDescription(detectedText.toString().trim());
            Bundle bundle = new Bundle();
            bundle.putSerializable("Question", uploadQuestion);
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_quest_type, bundle);

        } finally {
            textRecognizer.release();
        }
    }
}
