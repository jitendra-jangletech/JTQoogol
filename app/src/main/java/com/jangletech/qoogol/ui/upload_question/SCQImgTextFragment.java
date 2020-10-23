package com.jangletech.qoogol.ui.upload_question;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.VideoActivity;
import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.adapter.AdapterGallerySelectedImage;
import com.jangletech.qoogol.databinding.FragmentScqImgTextBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.listeners.QueMediaListener;
import com.jangletech.qoogol.model.LearningQuestionsNew;
import com.jangletech.qoogol.model.ResponseObj;
import com.jangletech.qoogol.model.UploadQuestion;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.ui.learning.SlideshowDialogFragment;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.ImageOptimization;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;
import com.jangletech.qoogol.videocompressions.DialogProcessFile;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

import static android.app.Activity.RESULT_OK;
import static com.jangletech.qoogol.util.Constant.ADD;
import static com.jangletech.qoogol.util.Constant.SCQ1;
import static com.jangletech.qoogol.util.Constant.SCQ2;
import static com.jangletech.qoogol.util.Constant.SCQ3;
import static com.jangletech.qoogol.util.Constant.SCQ4;
import static com.jangletech.qoogol.util.Constant.SCQ_IMAGE_WITH_TEXT;
import static com.jangletech.qoogol.util.Constant.UPDATE;
import static com.jangletech.qoogol.util.Constant.qoogol;

public class SCQImgTextFragment extends BaseFragment implements QueMediaListener {

    private static final String TAG = "ScqUpImageFragment";
    private UploadQuestion uploadQuestion;
    private FragmentScqImgTextBinding mBinding;
    private static final int CAMERA_REQUEST = 1, GALLERY_REQUEST = 2, PICKFILE_REQUEST_CODE = 3, VIDEO_REQUEST = 4, AUDIO_REQUEST = 5;
    public ArrayList<Uri> mAllUri = new ArrayList<>();
    private Uri[] mOptionsUri = new Uri[4];
    private AdapterGallerySelectedImage galleryAdapter;
    String questionId = "";
    List<String> tempimgList = new ArrayList<>();
    int call_from;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_scq_img_text, container, false);
        ((MainActivity) getActivity()).setOnDataListener(this);
        return mBinding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments().getInt("call_from")==ADD) {
            call_from=ADD;
            if (getArguments() != null && getArguments().getSerializable("Question") != null) {
                uploadQuestion = (UploadQuestion) getArguments().getSerializable("Question");
                mBinding.etQuestion.setText(uploadQuestion.getQuestDescription());
                mBinding.subject.setText("Subject : " + uploadQuestion.getSubjectName());
            }
        }else if (getArguments().getInt("call_from")==UPDATE) {
            call_from=UPDATE;
            LearningQuestionsNew learningQuestionsNew = (LearningQuestionsNew) getArguments().getSerializable("data");
            setData(learningQuestionsNew);
        }

        initSelectedImageView();


        mBinding.image1.setOnClickListener(v -> {
            ((MainActivity) getActivity()).openMediaDialog(SCQ1);
        });

        mBinding.image2.setOnClickListener(v -> {
            ((MainActivity) getActivity()).openMediaDialog(SCQ2);
        });

        mBinding.image3.setOnClickListener(v -> {
            ((MainActivity) getActivity()).openMediaDialog(SCQ3);
        });

        mBinding.image4.setOnClickListener(v -> {
            ((MainActivity) getActivity()).openMediaDialog(SCQ4);
        });

        mBinding.saveQuestion.setOnClickListener(v -> addQuestion());

        mBinding.addImages.setOnClickListener(v -> ((MainActivity) getActivity()).openMediaDialog(Constant.QUESTION));
    }

    private void setData(LearningQuestionsNew learningQuestionsNew) {
    }

    private void addQuestion() {
        if (isValidate()) {
            ProgressDialog.getInstance().show(getActivity());
            MultipartBody.Part[] queImagesParts = null;
            String images = "", SCQ1 = "", SCQ2 = "", SCQ3 = "", SCQ4 = "";
            if (mAllUri != null && mAllUri.size() > 0) {
                try {
                    queImagesParts = new MultipartBody.Part[mAllUri.size()];
                    for (int index = 0; index < mAllUri.size(); index++) {
                        Uri single_image = mAllUri.get(index);
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

                    for (int index = 0; index < mOptionsUri.length; index++) {
                        Uri single_image = mOptionsUri[index];
                        File imageFile = AppUtils.createImageFile(requireActivity(), single_image);
                        if (!imageFile.exists()) {
                            imageFile.createNewFile();
                        }

                        final InputStream imageStream = getActivity().getContentResolver().openInputStream(single_image);
                        AppUtils.readFully(imageStream, imageFile);
                        File file = new File(mOptionsUri[index].getPath());
                        long fileSizeInMB = imageFile.length() / 1048576;
                        Log.i(TAG, "File Size: " + fileSizeInMB + " MB");
                        if (fileSizeInMB > new PreferenceManager(getActivity()).getImageSize()) {
                            Toast.makeText(getActivity(), "Please upload the image of size less than 10MB", Toast.LENGTH_LONG).show();
                        } else {
                            RequestBody queBody = null;
                            if (UtilHelper.isImage(single_image, getActivity())) {
                                queBody = RequestBody.create(MediaType.parse("image/*"),
                                        imageFile);
                            }

                            queImagesParts[index] = MultipartBody.Part.createFormData("Files",
                                    imageFile.getName(), queBody);
                            if (single_image != null) {
                                if (index == 0)
                                    SCQ1 = AppUtils.encodedString(imageFile.getName()) + ":" + mBinding.text1.getText().toString();
                                else if (index == 1)
                                    SCQ2 = AppUtils.encodedString(imageFile.getName()) + ":" + mBinding.text2.getText().toString();
                                else if (index == 2)
                                    SCQ3 = AppUtils.encodedString(imageFile.getName()) + ":" + mBinding.text3.getText().toString();
                                else if (index == 3)
                                    SCQ4 = AppUtils.encodedString(imageFile.getName()) + ":" + mBinding.text4.getText().toString();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }


            UploadQuestion uploadQuestion = (UploadQuestion) getArguments().getSerializable("Question");
            RequestBody userId = RequestBody.create(MediaType.parse("multipart/form-data"), new PreferenceManager(getActivity()).getUserId());
            RequestBody appname = RequestBody.create(MediaType.parse("multipart/form-data"), qoogol);
            RequestBody deviceId = RequestBody.create(MediaType.parse("multipart/form-data"), getDeviceId(getActivity()));
            RequestBody subId = RequestBody.create(MediaType.parse("multipart/form-data"), uploadQuestion.getSubjectId());
            RequestBody question = RequestBody.create(MediaType.parse("multipart/form-data"), AppUtils.encodedString(mBinding.etQuestion.getText().toString()));
            RequestBody questiondesc = RequestBody.create(MediaType.parse("multipart/form-data"), AppUtils.encodedString(mBinding.etQuestionDesc.getText().toString()));
            RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), SCQ_IMAGE_WITH_TEXT);
            RequestBody scq1 = RequestBody.create(MediaType.parse("multipart/form-data"), SCQ1);
            RequestBody scq2 = RequestBody.create(MediaType.parse("multipart/form-data"), SCQ2);
            RequestBody scq3 = RequestBody.create(MediaType.parse("multipart/form-data"), SCQ3);
            RequestBody scq4 = RequestBody.create(MediaType.parse("multipart/form-data"), SCQ4);
            RequestBody marks = RequestBody.create(MediaType.parse("multipart/form-data"), mBinding.edtmarks.getText().toString());
            RequestBody duration = RequestBody.create(MediaType.parse("multipart/form-data"), mBinding.edtduration.getText().toString());
            RequestBody difflevel = RequestBody.create(MediaType.parse("multipart/form-data"), getSelectedDiffLevel());
            RequestBody ans = RequestBody.create(MediaType.parse("multipart/form-data"), getSelectedAns());
            RequestBody imgname = RequestBody.create(MediaType.parse("multipart/form-data"), images);
            RequestBody question_id = RequestBody.create(MediaType.parse("multipart/form-data"), questionId);
            Call<ResponseObj> call = getApiService().addSCQQuestionsApi(userId, appname, deviceId,
                    subId, question, questiondesc, type, scq1, scq2, scq3, scq4, marks, duration, difflevel, ans, imgname, queImagesParts,question_id);
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

        return level.replace("Easy", "E").replace("Medium", "M").replace("Hard", "h");
    }

    private boolean isValidate() {
        if (mBinding.subject.getText().toString().isEmpty()) {
            mBinding.subject.setError("Please select subject.");
            return false;
        }
        if (mBinding.etQuestion.getText().toString().isEmpty()) {
            mBinding.etQuestion.setError("Please enter question.");
            return false;
        }
        if (mOptionsUri == null || mOptionsUri.length < 2) {
            Toast.makeText(getActivity(), "Please add minimum 2 options", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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
                mAllUri.remove(position);
                galleryAdapter.notifyItemRemoved(position);
                galleryAdapter.notifyItemRangeChanged(position, mAllUri.size());
                if (mAllUri.size() == 0)
                    mBinding.queimgRecycler.setVisibility(View.GONE);
            }
        });

    }


    private String getSelectedAns() {
        String ans = "";
        int id = mBinding.radioGrpAnswer.getCheckedRadioButtonId();

        View radioButton = mBinding.radioGrpAnswer.findViewById(id);
        if (radioButton != null) {
            int idx = mBinding.radioGrpAnswer.indexOfChild(radioButton);
            RadioButton r = (RadioButton) mBinding.radioGrpAnswer.getChildAt(idx);
            ans = r.getText() != null ? r.getText().toString() : "";
        }

        return ans;
    }


    private void setImage(Uri path, ImageView img) {
        Glide.with(getActivity())
                .load(path)
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .error(R.drawable.ic_broken_image)
                .into(img);
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
                            loadImage(uri, optionId);
                        }
                    }
                } else if (data.getData() != null) {
                    try {
                        final Uri imageUri = data.getData();
                        loadImage(imageUri, optionId);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "onFailure onActivityResult: " + e.getMessage());
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            loadImage(photouri, optionId);
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

    @Override
    public void onScanImageClick(Uri uri, int opt) {
        loadImage(uri, opt);
    }

    @Override
    public void onScanText(String text, int ansId) {

    }

    private void loadImage(Uri uri, int opt) {
        try {
            if (opt == Constant.QUESTION) {
                if (mAllUri.size() > 3)
                    Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                else
                    setupPreview(uri);
            } else if (opt == SCQ1) {
                mOptionsUri[0] = uri;
                setImage(uri, mBinding.image1);
            } else if (opt == SCQ2) {
                mOptionsUri[1] = uri;
                setImage(uri, mBinding.image2);
            } else if (opt == SCQ3) {
                mOptionsUri[2] = uri;
                setImage(uri, mBinding.image3);
            } else if (opt == SCQ4) {
                mOptionsUri[3] = uri;
                setImage(uri, mBinding.image4);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
