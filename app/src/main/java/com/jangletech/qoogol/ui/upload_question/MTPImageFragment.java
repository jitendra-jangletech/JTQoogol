package com.jangletech.qoogol.ui.upload_question;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.VideoActivity;
import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.adapter.AdapterGallerySelectedImage;
import com.jangletech.qoogol.databinding.FragmentMtpImageBinding;
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
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

import static android.app.Activity.RESULT_OK;
import static com.jangletech.qoogol.util.Constant.A1;
import static com.jangletech.qoogol.util.Constant.A2;
import static com.jangletech.qoogol.util.Constant.A3;
import static com.jangletech.qoogol.util.Constant.A4;
import static com.jangletech.qoogol.util.Constant.ADD;
import static com.jangletech.qoogol.util.Constant.B1;
import static com.jangletech.qoogol.util.Constant.B2;
import static com.jangletech.qoogol.util.Constant.B3;
import static com.jangletech.qoogol.util.Constant.B4;
import static com.jangletech.qoogol.util.Constant.MATCH_PAIR_IMAGE;
import static com.jangletech.qoogol.util.Constant.UPDATE;
import static com.jangletech.qoogol.util.Constant.qoogol;

public class MTPImageFragment extends BaseFragment implements QueMediaListener {

    FragmentMtpImageBinding mBinding;
    private static final int CAMERA_REQUEST = 1, GALLERY_REQUEST = 2, PICKFILE_REQUEST_CODE = 3, VIDEO_REQUEST = 4, AUDIO_REQUEST = 5;
    public ArrayList<Uri> mAllUri = new ArrayList<>();
    private AdapterGallerySelectedImage galleryAdapter;
    private Uri[] mOptionsUri = new Uri[8];
    private static final String TAG = "MTPImageFragment";
    private UploadQuestion uploadQuestion;
    String questionId = "", subjectId = "";
    List<String> tempimgList = new ArrayList<>();
    int call_from;
    int optionId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mtp_image, container, false);
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
                mBinding.etQuestion.setText(uploadQuestion.getQuestDescription());
                mBinding.subject.setText("Subject : " + uploadQuestion.getSubjectName());
                subjectId = uploadQuestion.getSubjectId();
            }
        }else if (getArguments().getInt("call_from")==UPDATE) {
            call_from=UPDATE;
            LearningQuestionsNew learningQuestionsNew = (LearningQuestionsNew) getArguments().getSerializable("data");
            setData(learningQuestionsNew);
        }

        mBinding.image1.setOnClickListener(v -> {
            ((MainActivity) getActivity()).openMediaDialog(A1);
        });

        mBinding.image2.setOnClickListener(v -> {
            ((MainActivity) getActivity()).openMediaDialog(B1);
        });

        mBinding.image3.setOnClickListener(v -> {
            ((MainActivity) getActivity()).openMediaDialog(A2);
        });

        mBinding.image4.setOnClickListener(v -> {
            ((MainActivity) getActivity()).openMediaDialog(B2);
        });

        mBinding.image5.setOnClickListener(v -> {
            ((MainActivity) getActivity()).openMediaDialog(A3);
        });

        mBinding.image6.setOnClickListener(v -> {
            ((MainActivity) getActivity()).openMediaDialog(B3);
        });

        mBinding.image7.setOnClickListener(v -> {
            ((MainActivity) getActivity()).openMediaDialog(A4);
        });

        mBinding.image8.setOnClickListener(v -> {
            ((MainActivity) getActivity()).openMediaDialog(B4);
        });

        mBinding.saveQuestion.setOnClickListener(v -> addQuestion());

        mBinding.addImages.setOnClickListener(v -> ((MainActivity) getActivity()).openMediaDialog(Constant.QUESTION));
    }

    private void setData(LearningQuestionsNew learningQuestionsNew) {
        questionId = String.valueOf(learningQuestionsNew.getQuestion_id());
        subjectId = learningQuestionsNew.getSubject_id();
        mBinding.subject.setText("Subject : " + learningQuestionsNew.getSubject());
        mBinding.etQuestion.setText(learningQuestionsNew.getQuestion());
        mBinding.etQuestionDesc.setText(learningQuestionsNew.getQuestiondesc());

        if (!learningQuestionsNew.getMcq1().isEmpty()) {
            Uri uri1 = Uri.parse(AppUtils.getMedialUrl(getActivity(), learningQuestionsNew.getMcq1().split(":", -1)[1], learningQuestionsNew.getMcq1().split(":", -1)[2]));
            mOptionsUri[0] = uri1;
            setImage(uri1, mBinding.image1);
        }

        if (!learningQuestionsNew.getMcq2().isEmpty()) {
            Uri uri2 = Uri.parse(AppUtils.getMedialUrl(getActivity(), learningQuestionsNew.getMcq2().split(":", -1)[1], learningQuestionsNew.getMcq2().split(":", -1)[2]));
            mOptionsUri[0] = uri2;
            setImage(uri2, mBinding.image2);
        }

        if (!learningQuestionsNew.getMcq3().isEmpty()) {
            Uri uri3 = Uri.parse(AppUtils.getMedialUrl(getActivity(), learningQuestionsNew.getMcq3().split(":", -1)[1], learningQuestionsNew.getMcq3().split(":", -1)[2]));
            mOptionsUri[0] = uri3;
            setImage(uri3, mBinding.image3);
        }

        if (!learningQuestionsNew.getMcq4().isEmpty()) {
            Uri uri4 = Uri.parse(AppUtils.getMedialUrl(getActivity(), learningQuestionsNew.getMcq4().split(":", -1)[1], learningQuestionsNew.getMcq4().split(":", -1)[2]));
            mOptionsUri[0] = uri4;
            setImage(uri4, mBinding.image4);
        }


        mBinding.edtmarks.setText(learningQuestionsNew.getMarks());
        mBinding.edtduration.setText(learningQuestionsNew.getDuration());

        if (learningQuestionsNew.getDifficulty_level().equalsIgnoreCase("E"))
            mBinding.radioEasy.setChecked(true);
        else if (learningQuestionsNew.getDifficulty_level().equalsIgnoreCase("M"))
            mBinding.radioMedium.setChecked(true);
        else if (learningQuestionsNew.getDifficulty_level().equalsIgnoreCase("H"))
            mBinding.radioHard.setChecked(true);


//        if (learningQuestionsNew.getAnswer().equalsIgnoreCase("A"))
//            mBinding.radioA.setChecked(true);
//        else if (learningQuestionsNew.getAnswer().equalsIgnoreCase("B"))
//            mBinding.radioB.setChecked(true);
//        else if (learningQuestionsNew.getAnswer().equalsIgnoreCase("C"))
//            mBinding.radioC.setChecked(true);
//        else if (learningQuestionsNew.getAnswer().equalsIgnoreCase("D"))
//            mBinding.radioD.setChecked(true);

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
        Call<ResponseObj> call = getApiService().deleteMedia(new PreferenceManager(getActivity()).getUserId(), getDeviceId(getActivity()),
                tempimgList.get(position).split(":", -1)[0]);
        call.enqueue(new Callback<ResponseObj>() {
            @Override
            public void onResponse(Call<ResponseObj> call, retrofit2.Response<ResponseObj> response) {
                try {
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        deleteImage(position);
                        tempimgList.remove(position);
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
            String images = "", pair1 = "", pair2 = "", pair3 = "", pair4 = "";
            int size = 0;
            if ( mAllUri != null && mAllUri.size()>0)
                size = size+mAllUri.size();
            if (mOptionsUri !=null && mOptionsUri.length>0)
                size = size+ mOptionsUri.length;
            queImagesParts = new MultipartBody.Part[size];

            if (mAllUri != null && mAllUri.size() > 0) {
                try {
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

            if (mOptionsUri !=null && mOptionsUri.length>0) {
                try {
                    for (int index = 0; index < mOptionsUri.length; index++) {
                        Uri single_image = mOptionsUri[index];
                        if (!single_image.toString().contains("https")) {
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
                                        pair1 = AppUtils.encodedString(imageFile.getName());
                                    else if (index == 1)
                                        pair1 = pair1 + "::" +  AppUtils.encodedString(imageFile.getName());
                                    else if (index == 2)
                                        pair2 = AppUtils.encodedString(imageFile.getName());
                                    else if (index == 3)
                                        pair2 = pair2 + "::" + AppUtils.encodedString(imageFile.getName());
                                    else if (index == 4)
                                        pair3 = AppUtils.encodedString(imageFile.getName());
                                    else if (index == 5)
                                        pair3 = pair3 + "::" + AppUtils.encodedString(imageFile.getName());
                                    else if (index == 6)
                                        pair4 = AppUtils.encodedString(imageFile.getName());
                                    else if (index == 7)
                                        pair4 = pair4 + "::" + AppUtils.encodedString(imageFile.getName());
                                }
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            String user_id = new PreferenceManager(getActivity()).getUserId();

            RequestBody userId = RequestBody.create(MediaType.parse("multipart/form-data"), user_id);
            RequestBody appname = RequestBody.create(MediaType.parse("multipart/form-data"), qoogol);
            RequestBody deviceId = RequestBody.create(MediaType.parse("multipart/form-data"), getDeviceId(getActivity()));
            RequestBody subId = RequestBody.create(MediaType.parse("multipart/form-data"), subjectId);
            RequestBody question = RequestBody.create(MediaType.parse("multipart/form-data"), AppUtils.encodedString(mBinding.etQuestion.getText().toString()));
            RequestBody questiondesc = RequestBody.create(MediaType.parse("multipart/form-data"), AppUtils.encodedString(mBinding.etQuestionDesc.getText().toString()));
            RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), MATCH_PAIR_IMAGE);
            RequestBody selectedpair1 = RequestBody.create(MediaType.parse("multipart/form-data"), pair1);
            RequestBody selectedpair2 = RequestBody.create(MediaType.parse("multipart/form-data"), pair2);
            RequestBody selectedpair3 = RequestBody.create(MediaType.parse("multipart/form-data"), pair3);
            RequestBody selectedpair4 = RequestBody.create(MediaType.parse("multipart/form-data"), pair4);
            RequestBody marks = RequestBody.create(MediaType.parse("multipart/form-data"), mBinding.edtmarks.getText().toString());
            RequestBody duration = RequestBody.create(MediaType.parse("multipart/form-data"), mBinding.edtduration.getText().toString());
            RequestBody difflevel = RequestBody.create(MediaType.parse("multipart/form-data"), getSelectedDiffLevel());
            RequestBody ans = RequestBody.create(MediaType.parse("multipart/form-data"), getSelectedAns());
            RequestBody imgname = RequestBody.create(MediaType.parse("multipart/form-data"), images);
            RequestBody question_id = RequestBody.create(MediaType.parse("multipart/form-data"), questionId);


            Call<ResponseObj> call = getApiService().addSCQQuestionsApi(userId, appname, deviceId,
                    subId, question, questiondesc, type, selectedpair1, selectedpair2, selectedpair3, selectedpair4, marks, duration, difflevel, ans, imgname, queImagesParts,question_id);
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

    private String getSelectedAns() {
        int id1 = mBinding.a1.getCheckedRadioButtonId();
        int id2 = mBinding.a2.getCheckedRadioButtonId();
        int id3 = mBinding.a3.getCheckedRadioButtonId();
        int id4 = mBinding.a4.getCheckedRadioButtonId();
        if (id1 == -1 && id2 == -1 && id3 == -1 && id4 == -1) {
            return "";
        } else {
            String ans = "";
            View radioButton1 = mBinding.a1.findViewById(id1);
            int idx1 = mBinding.a1.indexOfChild(radioButton1);
            RadioButton r1 = (RadioButton) mBinding.a1.getChildAt(idx1);
            ans = "A1::" + r1.getText().toString();

            View radioButton2 = mBinding.a2.findViewById(id2);
            int idx2 = mBinding.a2.indexOfChild(radioButton2);
            RadioButton r2 = (RadioButton) mBinding.a2.getChildAt(idx2);
            ans = ans + "A1::" + r2.getText().toString();

            View radioButton3 = mBinding.a3.findViewById(id3);
            int idx3 = mBinding.a3.indexOfChild(radioButton3);
            RadioButton r3 = (RadioButton) mBinding.a3.getChildAt(idx3);
            ans = ans + "A1::" + r3.getText().toString();

            View radioButton4 = mBinding.a4.findViewById(id4);
            int idx4 = mBinding.a4.indexOfChild(radioButton4);
            RadioButton r4 = (RadioButton) mBinding.a4.getChildAt(idx4);
            ans = ans + "A1::" + r4.getText().toString();

            return ans;

        }
    }

    private boolean isValidate() {
        int id1 = mBinding.a1.getCheckedRadioButtonId();
        int id2 = mBinding.a2.getCheckedRadioButtonId();
        int id3 = mBinding.a3.getCheckedRadioButtonId();
        int id4 = mBinding.a4.getCheckedRadioButtonId();

        if (mBinding.subject.getText().toString().isEmpty()) {
            mBinding.subject.setError("Please select subject.");
            return false;
        } else if (mBinding.etQuestion.getText().toString().isEmpty()) {
            mBinding.etQuestion.setError("Please enter question.");
            return false;
        } else if (mOptionsUri[0]==null) {
            Toast.makeText(getActivity(), "Please select minimum 2 option pairs", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mOptionsUri[1]==null) {
            Toast.makeText(getActivity(), "Please select minimum 2 option pairs", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mOptionsUri[2]==null) {
            Toast.makeText(getActivity(), "Please select minimum 2 option pairs", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mOptionsUri[3]==null) {
            Toast.makeText(getActivity(), "Please select minimum 2 option pairs", Toast.LENGTH_SHORT).show();
            return false;
        } else if (id1 == -1 || id2 == -1 || id3 == -1 || id4 == -1) {
            if (id1 == -1 && id2 == -1 && id3 == -1 && id4 == -1) {
                return true;
            } else {
                Toast.makeText(getActivity(), "Please select all radio options or reset all", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            return true;
        }
    }

    private void setImage(Uri path, ImageView img) {
        Glide.with(getActivity())
                .load(path)
                .dontAnimate()
                .error(R.drawable.ic_broken_image)
                .into(img);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null && resultCode == RESULT_OK) {
                Log.i(TAG, "onActivityResult Uri : " + result.getUri());
                loadImage(result.getUri(),optionId);
            }
        }
    }



    @Override
    public void onMediaReceived(int requestCode, int resultCode, Intent data, Uri photouri, int optionId) {
        this.optionId = optionId;
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
                        CropImage.activity(imageUri)
                                .setInitialCropWindowPaddingRatio(0.0f)
                                .start(getActivity(), MTPImageFragment.this);
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
            CropImage.activity(photouri)
                    .setInitialCropWindowPaddingRatio(0.0f)
                    .start(getActivity(), MTPImageFragment.this);
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
            } else if (opt==A1) {
                mOptionsUri[0] = uri;
                setImage(uri,mBinding.image1);
            }else if (opt==B1) {
                mOptionsUri[1] = uri;
                setImage(uri,mBinding.image2);
            } else if (opt==A2) {
                mOptionsUri[2] = uri;
                setImage(uri,mBinding.image3);
            } else if (opt==B2) {
                mOptionsUri[3] = uri;
                setImage(uri,mBinding.image4);
            }  else if (opt==A3){
                mOptionsUri[4] = uri;
                setImage(uri,mBinding.image5);
            } else if (opt==B3){
                mOptionsUri[5] = uri;
                setImage(uri,mBinding.image6);
            }else if (opt==A4){
                mOptionsUri[6] = uri;
                setImage(uri,mBinding.image7);
            }else if (opt==B4){
                mOptionsUri[7] = uri;
                setImage(uri,mBinding.image8);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}