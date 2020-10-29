package com.jangletech.qoogol.ui.upload_question;


import android.Manifest;
import android.content.ClipData;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.VideoActivity;
import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.adapter.AdapterGallerySelectedImage;
import com.jangletech.qoogol.databinding.FragmentLongAnsQueBinding;
import com.jangletech.qoogol.dialog.AnsScanDialog;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.listeners.QueMediaListener;
import com.jangletech.qoogol.model.LearningQuestionsNew;
import com.jangletech.qoogol.model.ResponseObj;
import com.jangletech.qoogol.model.UploadQuestion;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.ui.learning.SlideshowDialogFragment;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.ImageOptimization;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;
import com.jangletech.qoogol.videocompressions.DialogProcessFile;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

import static android.app.Activity.RESULT_OK;
import static com.jangletech.qoogol.util.Constant.ADD;
import static com.jangletech.qoogol.util.Constant.LONG_ANSWER;
import static com.jangletech.qoogol.util.Constant.UPDATE;
import static com.jangletech.qoogol.util.Constant.qoogol;

/**
 * A simple {@link Fragment} subclass.
 */
public class LongAns_QueFragment extends BaseFragment implements QueMediaListener {

    private UploadQuestion uploadQuestion;
    FragmentLongAnsQueBinding mBinding;
    private static final int REQUEST_GALLERY = 0;
    private static final int REQUEST_CAMERA = 1;
    private Uri imageUri;
    private int ansId;
    private static final String TAG = "LongAns_QueFragment";
    private static final int CAMERA_REQUEST = 1, GALLERY_REQUEST = 2, PICKFILE_REQUEST_CODE = 3, VIDEO_REQUEST = 4, AUDIO_REQUEST = 5;
    public ArrayList<Uri> mAllUri = new ArrayList<>();
    private AdapterGallerySelectedImage galleryAdapter;
    String questionId = "", subjectId = "";
    List<String> tempimgList = new ArrayList<>();
    int call_from;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_long_ans__que, container, false);
        ((MainActivity) getActivity()).setOnDataListener(this);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initSelectedImageView();

        if (getArguments().getInt("call_from")==ADD) {
            call_from=ADD;
            if (getArguments() != null && getArguments().getSerializable("Question") != null) {
                uploadQuestion = (UploadQuestion) getArguments().getSerializable("Question");
                mBinding.questionEdittext.setText(uploadQuestion.getQuestDescription());
                mBinding.subject.setText("Subject : " + uploadQuestion.getSubjectName());
                subjectId = uploadQuestion.getSubjectId();
            }
        }else if (getArguments().getInt("call_from")==UPDATE) {
            call_from=UPDATE;
            LearningQuestionsNew learningQuestionsNew = (LearningQuestionsNew) getArguments().getSerializable("data");
            setData(learningQuestionsNew);
        }


        mBinding.saveQuestion.setOnClickListener(v -> addQuestion());

        mBinding.addImages.setOnClickListener(v -> ((MainActivity) getActivity()).openMediaDialog(Constant.QUESTION));

        mBinding.ansEdit.setOnClickListener(v -> {
            ((MainActivity) getActivity()).openAnsScanDialog(Constant.SCQ1);
        });
    }

    private void setData(LearningQuestionsNew learningQuestionsNew) {
        questionId = String.valueOf(learningQuestionsNew.getQuestion_id());
        subjectId = learningQuestionsNew.getSubject_id();
        mBinding.subject.setText("Subject : " + learningQuestionsNew.getSubject());
        mBinding.questionEdittext.setText(learningQuestionsNew.getQuestion());
        mBinding.questiondescEdittext.setText(learningQuestionsNew.getQuestiondesc());
        mBinding.edtmarks.setText(learningQuestionsNew.getMarks());
        mBinding.edtduration.setText(learningQuestionsNew.getDuration());
        mBinding.answerEdittext.setText(learningQuestionsNew.getAnswer());

        if (learningQuestionsNew.getDifficulty_level().equalsIgnoreCase("E"))
            mBinding.radioEasy.setChecked(true);
        else if (learningQuestionsNew.getDifficulty_level().equalsIgnoreCase("M"))
            mBinding.radioMedium.setChecked(true);
        else if (learningQuestionsNew.getDifficulty_level().equalsIgnoreCase("H"))
            mBinding.radioHard.setChecked(true);

        if (learningQuestionsNew.getQue_images() != null && !learningQuestionsNew.getQue_images().isEmpty()) {
            String[] stringrray = learningQuestionsNew.getQue_images().split(",");
            tempimgList = Arrays.asList(stringrray);
            for (int i = 0; i < stringrray.length; i++) {
                String s = AppUtils.getMedialUrl(getActivity(), tempimgList.get(i).split(":", -1)[1], tempimgList.get(i).split(":", -1)[2]);
                setupPreview(Uri.parse(s));
            }
        }
    }

    private void initSelectedImageView() {
        setupPreview(null);
        galleryAdapter = new AdapterGallerySelectedImage(mAllUri, tempimgList, call_from, getActivity(), new AdapterGallerySelectedImage.GalleryUplodaHandler() {
            @Override
            public void imageClick(Uri media, int position) {
                try {
                    if (UtilHelper.isImage(media, getActivity())) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("urilist", (Serializable) mAllUri);
                        bundle.putInt("position", 0);
                        bundle.putString("type", "uri");
                        SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                        newFragment.setArguments(bundle);
                        FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
                        newFragment.show(ft, "slideshow");
                    } else if (UtilHelper.isVideo(media, getActivity())) {
                        if (media.getPath() != null && !media.getPath().isEmpty()) {
                            Intent intent = new Intent(getActivity(), VideoActivity.class);
                            intent.putExtra("uri", new ImageOptimization(getActivity()).getPath(getActivity(), media));
                            intent.putExtra("fromUrl", false);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getActivity().startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.server_error), Toast.LENGTH_LONG).show();
                        }
                    } else if (UtilHelper.isDoc(media, getActivity())) {
                        File file = AppUtils.createImageFile(requireActivity(), media);
                        MimeTypeMap mime = MimeTypeMap.getSingleton();
                        String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                        Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                        pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        pdfOpenintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        if (media != null) {
                            pdfOpenintent.setDataAndType(media, AppUtils.getType(file.getName()));
                        } else {
                            pdfOpenintent.setDataAndType(media, "application/octet-stream");
                        }
                        getActivity().startActivity(pdfOpenintent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void addClick(Uri media, int position) {
            }

            @Override
            public void actionRemoved(int position) {
                Uri uri = mAllUri.get(position);
                if (uri.toString().contains("http")) {
                    deleteApiCall(position);
                } else {
                    deleteImage(position);
                }
            }
        });
    }

    private void deleteApiCall(int position) {
        deleteImage(position);
    }

    private void deleteImage(int position) {
        mAllUri.remove(position);
        galleryAdapter.notifyItemRemoved(position);
        galleryAdapter.notifyItemRangeChanged(position, mAllUri.size());
        if (mAllUri.size() == 0)
            mBinding.queimgRecycler.setVisibility(View.GONE);
    }

    private void setupPreview(Uri media) {
        if (media == null) {
            Log.d("#>Empty data", "add image");
            // model.preview.setValue(LocalImages.ADD_UPLOAD.value);
            mBinding.queimgRecycler.setVisibility(View.GONE);
            return;
        }
        try {
            // model.preview.setValue(media.toString());
            if (UtilHelper.isImage(media, getActivity()) || UtilHelper.isVideo(media, getActivity()) || UtilHelper.isAudio(media, getActivity()) || UtilHelper.isDoc(media, getActivity())) {
                Log.d("#>type ", "image");
                mAllUri.add(media);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                mBinding.queimgRecycler.setLayoutManager(mLayoutManager);
                mBinding.queimgRecycler.setItemAnimator(new DefaultItemAnimator());
                mBinding.queimgRecycler.setAdapter(galleryAdapter);
                mBinding.queimgRecycler.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getActivity(), "Upload the proper media", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }


    private void addQuestion() {
        if (isValidate()) {
            ProgressDialog.getInstance().show(getActivity());
            MultipartBody.Part[] queImagesParts = null;
            String images = "";
            if (mAllUri != null && mAllUri.size() > 0) {
                try {
                    queImagesParts = new MultipartBody.Part[mAllUri.size()];
                    for (int index = 0; index < mAllUri.size(); index++) {
                        Uri single_image = mAllUri.get(index);
                        if (!single_image.toString().contains("https")) {
                            File imageFile = AppUtils.createImageFile(requireActivity(), single_image);
                            if (!imageFile.exists()) {
                                imageFile.createNewFile();
                            }

                            final InputStream imageStream = getActivity().getContentResolver().openInputStream(single_image);
                            AppUtils.readFully(imageStream, imageFile);
                            File file = new File(mAllUri.get(index).getPath());
                            long fileSizeInMB = imageFile.length() / 1048576;
                            Log.i(TAG, "File Size: " + fileSizeInMB + " MB");
                            if (fileSizeInMB > new PreferenceManager(getActivity()).getImageSize()) {
                                Toast.makeText(getActivity(), "Please upload the image of size less than 10MB", Toast.LENGTH_LONG).show();
                            } else {
                                RequestBody queBody = null;
                                if (UtilHelper.isImage(single_image, getActivity())) {
                                    queBody = RequestBody.create(MediaType.parse("image/*"),
                                            imageFile);
                                } else if (UtilHelper.isVideo(single_image, getActivity())) {
                                    queBody = RequestBody.create(MediaType.parse("video/*"),
                                            imageFile);
                                } else if (UtilHelper.isAudio(single_image, getActivity())) {
                                    queBody = RequestBody.create(MediaType.parse("audio/*"),
                                            imageFile);
                                } else if (UtilHelper.isDoc(single_image, getActivity())) {
                                    queBody = RequestBody.create(MediaType.parse("application/*"),
                                            imageFile);
                                }

                                queImagesParts[index] = MultipartBody.Part.createFormData("Files",
                                        imageFile.getName(), queBody);
                                if (images.isEmpty())
                                    images = AppUtils.encodedString(imageFile.getName());
                                else
                                    images = images + "," + AppUtils.encodedString(imageFile.getName());
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            String user_id = new PreferenceManager(getActivity()).getUserId();

            RequestBody userId = RequestBody.create(MediaType.parse("multipart/form-data"), user_id);
            RequestBody appname = RequestBody.create(MediaType.parse("multipart/form-data"), qoogol);
            RequestBody deviceId = RequestBody.create(MediaType.parse("multipart/form-data"), getDeviceId(getActivity()));
            RequestBody subId = RequestBody.create(MediaType.parse("multipart/form-data"), subjectId);
            RequestBody question = RequestBody.create(MediaType.parse("multipart/form-data"), AppUtils.encodedString(mBinding.questionEdittext.getText().toString()));
            RequestBody questiondesc = RequestBody.create(MediaType.parse("multipart/form-data"), AppUtils.encodedString(mBinding.questiondescEdittext.getText().toString()));
            RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), LONG_ANSWER);
            RequestBody marks = RequestBody.create(MediaType.parse("multipart/form-data"), mBinding.edtmarks.getText().toString());
            RequestBody duration = RequestBody.create(MediaType.parse("multipart/form-data"), mBinding.edtduration.getText().toString());
            RequestBody difflevel = RequestBody.create(MediaType.parse("multipart/form-data"), getSelectedDiffLevel());
            RequestBody ans = RequestBody.create(MediaType.parse("multipart/form-data"), mBinding.answerEdittext.getText().toString());
            RequestBody imgname = RequestBody.create(MediaType.parse("multipart/form-data"), images);

            Call<ResponseObj> call = getApiService().addSubjectiveQuestionsApi(userId, appname, deviceId,
                    subId, question, questiondesc, type, marks, duration, difflevel, ans, imgname, queImagesParts);
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

        return level.replace("Easy","E").replace("Medium","M").replace("Hard","h");
    }

    private boolean isValidate() {
        if (mBinding.subject.getText().toString().isEmpty()) {
            mBinding.subject.setError("Please select subject.");
            return false;
        } if (mBinding.questionEdittext.getText().toString().isEmpty()) {
            mBinding.questionEdittext.setError("Please enter question.");
            return false;
        }  else {
            return true;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null && resultCode == RESULT_OK) {
                Log.i(TAG, "onActivityResult Uri : " + result.getUri());
                setupPreview(result.getUri());
            }
        }
    }


    @Override
    public void onMediaReceived(int requestCode, int resultCode, Intent data, Uri photouri, int optionId) {
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                ArrayList<Uri> mArrayUri = new ArrayList<>();
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    if (mClipData.getItemCount() > 3) {
                        Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                    } else if (mAllUri.size() > 3) {
                        Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                    } else {
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            if (mAllUri.size() > 3) {
                                Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                            } else {
                                setupPreview(uri);
                            }

                        }
                    }
                } else if (data.getData() != null) {
                    if (mAllUri.size() > 3) {
                        Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            final Uri imageUri = data.getData();
                            CropImage.activity(imageUri)
                                    .setInitialCropWindowPaddingRatio(0.0f)
                                    .start(getActivity(), LongAns_QueFragment.this);
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "onFailure onActivityResult: " + e.getMessage());
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            if (mAllUri.size() > 3) {
                Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
            } else {
                CropImage.activity(photouri)
                        .setInitialCropWindowPaddingRatio(0.0f)
                        .start(getActivity(), LongAns_QueFragment.this);
            }
        } else if (requestCode == VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                ArrayList<Uri> video_uri = new ArrayList<>();
                if (data.getData() != null) {
                    Uri mVideoUri = data.getData();
                    int quality = ImageOptimization.getQualityReductionParams(getActivity(), mVideoUri);
                    Log.i(TAG, "File reduce size: " + quality);
                    if (mAllUri.size() > 3) {
                        Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                    } else if (quality == 3) {
                        if (mAllUri.size() > 3) {
                            Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                        } else {
                            video_uri.add(mVideoUri);
                            setupPreview(mVideoUri);
                        }

                    } else {
                        new DialogProcessFile(requireActivity(), mVideoUri, quality, (processedFile, processedFilePath, success) -> {
                            if (mAllUri.size() > 3) {
                                Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                            } else {
                                if (success) {
                                    setupPreview(processedFile);
                                } else {
                                    setupPreview(mVideoUri);
                                }
                            }
                        });
                    }
                } else if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    if (mClipData.getItemCount() > 3) {
                        Toast.makeText(getActivity(), "A maximum of 4 videos can be uploaded at once.", Toast.LENGTH_LONG).show();
                    } else {
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            video_uri.add(uri);
                            if (mAllUri.size() > 3) {
                                Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                            } else {
                                setupPreview(uri);
                            }
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICKFILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            try {
                ArrayList<Uri> video_uri = new ArrayList<>();
                if (data.getData() != null) {
                    Uri mVideoUri = data.getData();
                    if (mAllUri.size() > 3) {
                        Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                    } else {
                        if (mAllUri.size() > 3) {
                            Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                        } else {
                            video_uri.add(mVideoUri);
                            setupPreview(mVideoUri);
                        }

                    }
                } else if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    if (mClipData.getItemCount() > 3) {
                        Toast.makeText(getActivity(), "A maximum of 4 videos can be uploaded at once.", Toast.LENGTH_LONG).show();
                    } else {
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            video_uri.add(uri);
                            if (mAllUri.size() > 3) {
                                Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                            } else {
                                setupPreview(uri);
                            }
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == AUDIO_REQUEST) {
            if (data.getData() != null) {
                if (mAllUri.size() > 3) {
                    Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                } else {
                    Uri mVideoUri = data.getData();
                    setupPreview(mVideoUri);
                }

            } else {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    if (mClipData.getItemCount() > 3) {
                        Toast.makeText(getActivity(), "A maximum of 4 audios can be uploaded at once.", Toast.LENGTH_LONG).show();
                    } else {
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            if (mAllUri.size() > 3) {
                                Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                            } else {
                                setupPreview(uri);
                            }
                        }

                    }
                }
            }
        }
    }

    @Override
    public void onScanImageClick(Uri uri, int opt) {
        if (opt==Constant.QUESTION)
            setupPreview(uri);
    }

    @Override
    public void onScanText(String text, int ansId) {
        if (ansId == 1)
            mBinding.answerEdittext.setText(text);
    }
}
