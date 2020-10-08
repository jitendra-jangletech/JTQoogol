package com.jangletech.qoogol.ui.upload_question;


import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.FragmentMcqQueBinding;
import com.jangletech.qoogol.dialog.AnsScanDialog;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.ResponseObj;
import com.jangletech.qoogol.model.UploadQuestion;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;
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

import retrofit2.Call;
import retrofit2.Callback;

import static android.app.Activity.RESULT_OK;
import static com.jangletech.qoogol.util.AppUtils.getDeviceId;
import static com.jangletech.qoogol.util.Constant.MCQ;
import static com.jangletech.qoogol.util.Constant.qoogol;

/**
 * A simple {@link Fragment} subclass.
 */
public class MCQ_QueFragment extends Fragment implements AnsScanDialog.AnsScannerListener {

    private FragmentMcqQueBinding mBinding;
    private UploadQuestion uploadQuestion;
    ApiInterface apiService = ApiClient.getInstance().getApi();
    private static final int REQUEST_GALLERY = 0;
    private static final int REQUEST_CAMERA = 1;
    private Uri imageUri;
    private int ansId;
    private static final String TAG = "SCQ_QueFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mcq__que, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null && getArguments().getSerializable("Question") != null) {
            uploadQuestion = (UploadQuestion) getArguments().getSerializable("Question");
            mBinding.questionEdittext.setText(uploadQuestion.getQuestDescription());
            mBinding.subject.setText("Subject : " + uploadQuestion.getSubjectName());
        }

        mBinding.saveQuestion.setOnClickListener(v -> addQuestion());

        mBinding.mc1Edit.setOnClickListener(v -> {
            new AnsScanDialog(getActivity(), 1, this)
                    .show();
        });

        mBinding.mcq2Edit.setOnClickListener(v -> {
            new AnsScanDialog(getActivity(), 2, this)
                    .show();
        });

        mBinding.mcq3Edit.setOnClickListener(v -> {
            new AnsScanDialog(getActivity(), 3, this)
                    .show();
        });
        mBinding.mcq4Edit.setOnClickListener(v -> {
            new AnsScanDialog(getActivity(), 4, this)
                    .show();
        });
    }

    private void addQuestion() {
        if (isValidate()) {
            UploadQuestion  uploadQuestion = (UploadQuestion) getArguments().getSerializable("Question");
            String user_id = new PreferenceManager(getActivity()).getUserId();

            Call<ResponseObj> call= apiService.addQuestionsApi(user_id, qoogol, getDeviceId(),
                    uploadQuestion.getSubjectId(), mBinding.questionEdittext.getText().toString(),
                    mBinding.questiondescEdittext.getText().toString(),MCQ,mBinding.mcq1Edittext.getText().toString(),
                    mBinding.mcq2Edittext.getText().toString(),mBinding.mcq3Edittext.getText().toString(),
                    mBinding.mcq4Edittext.getText().toString(),mBinding.edtmarks.getText().toString(),
                    mBinding.edtduration.getText().toString(),getSelectedDiffLevel(),getSelectedAns());

            call.enqueue(new Callback<ResponseObj>() {
                @Override
                public void onResponse(Call<ResponseObj> call, retrofit2.Response<ResponseObj> response) {
                    try {
                        if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                            Toast.makeText(getActivity(), "Question added successfully", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_upload_question);
                        } else {
                            Toast.makeText(getActivity(), UtilHelper.getAPIError(String.valueOf(response.body())), Toast.LENGTH_SHORT).show();
                        }
                        ProgressDialog.getInstance().dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                        ProgressDialog.getInstance().dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ResponseObj> call, Throwable t) {
                    t.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            });
        }
    }

    private String getSelectedDiffLevel() {
        String level = "";
        int id = mBinding.radioDifflevel.getCheckedRadioButtonId();

        View radioButton = mBinding.radioDifflevel.findViewById(id);
        if (radioButton != null) {
            int idx = mBinding.radioDifflevel.indexOfChild(radioButton);
            RadioButton r = (RadioButton) mBinding.radioDifflevel.getChildAt(idx);
            level = r.getText() != null ? r.getText().toString() : "";
        }

        return level;
    }

    private String getSelectedAns() {
        String ans="";
        if (mBinding.ansA.isChecked())
            ans = "A";

        if (mBinding.ansB.isChecked()) {
            if (ans.isEmpty())
                ans="B";
            else
                ans=ans+","+"B";
        }

        if (mBinding.ansC.isChecked()) {
            if (ans.isEmpty())
                ans="C";
            else
                ans=ans+","+"C";
        }

        if (mBinding.ansD.isChecked()) {
            if (ans.isEmpty())
                ans="D";
            else
                ans=ans+","+"D";
        }
        return ans;
    }

    private boolean isValidate() {
        if (mBinding.subject.getText().toString().isEmpty()) {
            mBinding.subject.setError("Please select subject.");
            return false;
        } if (mBinding.questionEdittext.getText().toString().isEmpty()) {
            mBinding.questionEdittext.setError("Please enter question.");
            return false;
        } if (mBinding.mcq1Edittext.getText().toString().isEmpty()) {
            mBinding.mcq1Edittext.setError("Please enter option 1.");
            return false;
        } if (mBinding.mcq2Edittext.getText().toString().isEmpty()) {
            mBinding.mcq2Edittext.setError("Please enter option 2.");
            return false;
        } if (mBinding.mcq3Edittext.getText().toString().isEmpty()) {
            mBinding.mcq3Edittext.setError("Please enter option 3.");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onCamScannerClick(int id) {
        ansId = id;
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
                    Log.e("", "onPermissionsChecked Error : ");
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();

    }

    @Override
    public void onGalleryClick(int id) {
        ansId = id;
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

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
            setScanAns(detectedText.toString().trim());

        } finally {
            textRecognizer.release();
        }
    }

    private void setScanAns(String text) {
        if (ansId == 1)
            mBinding.mcq1Edittext.setText(text);
        if (ansId == 2)
            mBinding.mcq2Edittext.setText(text);
        if (ansId == 3)
            mBinding.mcq3Edittext.setText(text);
        if (ansId == 4)
            mBinding.mcq4Edittext.setText(text);
    }
}
