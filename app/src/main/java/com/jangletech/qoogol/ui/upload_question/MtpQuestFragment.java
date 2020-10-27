package com.jangletech.qoogol.ui.upload_question;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.VideoActivity;
import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.adapter.AdapterGallerySelectedImage;
import com.jangletech.qoogol.databinding.FragmentUpMtpQueBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.dialog.SubjectiveAnsDialog;
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
import static com.jangletech.qoogol.util.Constant.MATCH_PAIR;
import static com.jangletech.qoogol.util.Constant.UPDATE;
import static com.jangletech.qoogol.util.Constant.qoogol;

public class MtpQuestFragment extends BaseFragment implements SubjectiveAnsDialog.GetAnsListener, QueMediaListener, View.OnTouchListener {

    private static final String TAG = "MtpQuestFragment";
    private FragmentUpMtpQueBinding mBinding;
    private SubjectiveAnsDialog subjectiveAnsDialog;
    private String selectedOptions = "";
    private UploadQuestion uploadQuestion;
    private static final int CAMERA_REQUEST = 1, GALLERY_REQUEST = 2, PICKFILE_REQUEST_CODE = 3, VIDEO_REQUEST = 4, AUDIO_REQUEST = 5;
    public ArrayList<Uri> mAllUri = new ArrayList<>();
    private AdapterGallerySelectedImage galleryAdapter;
    String questionId = "", subjectId = "";
    List<String> tempimgList = new ArrayList<>();
    int call_from;
    int optionId;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_up_mtp_que, container, false);
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

        mBinding.opa1.setOnTouchListener(this);
        mBinding.opb1.setOnTouchListener(this);
        mBinding.opa2.setOnTouchListener(this);
        mBinding.opb2.setOnTouchListener(this);
        mBinding.opa3.setOnTouchListener(this);
        mBinding.opb3.setOnTouchListener(this);
        mBinding.opa4.setOnTouchListener(this);
        mBinding.opb4.setOnTouchListener(this);

        mBinding.saveQuestion.setOnClickListener(v -> addQuestion());

        mBinding.addImages.setOnClickListener(v -> ((MainActivity) getActivity()).openMediaDialog(Constant.QUESTION));

        mBinding.reset.setOnClickListener(v -> {
            mBinding.a1.clearCheck();
            mBinding.a2.clearCheck();
            mBinding.a3.clearCheck();
            mBinding.a4.clearCheck();
            resetRadioGroup(mBinding.a1);
            resetRadioGroup(mBinding.a2);
            resetRadioGroup(mBinding.a3);
            resetRadioGroup(mBinding.a4);
        });

        mBinding.a1.setOnCheckedChangeListener((group, checkedId) -> {
            if (!selectedOptions.contains(getCheckedRadioButton(mBinding.a1)))
                selectedOptions = selectedOptions + "," + getCheckedRadioButton(mBinding.a1);
            resetRadioGroup(mBinding.a2);
            setRadioDisable(mBinding.a2, selectedOptions);
            setRadioDisable(mBinding.a3, selectedOptions);
            setRadioDisable(mBinding.a4, selectedOptions);
        });

        mBinding.a2.setOnCheckedChangeListener((group, checkedId) -> {
            if (!selectedOptions.contains(getCheckedRadioButton(mBinding.a2)))
                selectedOptions = selectedOptions + "," + getCheckedRadioButton(mBinding.a2);
            resetRadioGroup(mBinding.a3);
            setRadioDisable(mBinding.a3, selectedOptions);
            setRadioDisable(mBinding.a4, selectedOptions);

        });

        mBinding.a3.setOnCheckedChangeListener((group, checkedId) -> {
            if (!selectedOptions.contains(getCheckedRadioButton(mBinding.a3)))
                selectedOptions = selectedOptions + "," + getCheckedRadioButton(mBinding.a3);
            resetRadioGroup(mBinding.a4);
            setRadioDisable(mBinding.a4, selectedOptions);
        });

        mBinding.a4.setOnCheckedChangeListener((group, checkedId) -> getCheckedRadioButton(mBinding.a4));
    }

    private void setData(LearningQuestionsNew learningQuestionsNew) {
        questionId = String.valueOf(learningQuestionsNew.getQuestion_id());
        subjectId = learningQuestionsNew.getSubject_id();
        mBinding.subject.setText("Subject : " + learningQuestionsNew.getSubject());
        mBinding.etQuestion.setText(learningQuestionsNew.getQuestion());
        mBinding.etQuestionDesc.setText(learningQuestionsNew.getQuestiondesc());
        mBinding.opa1.setText(learningQuestionsNew.getMcq1().split("::", -1)[0]);
        mBinding.opb1.setText(learningQuestionsNew.getMcq1().split("::", -1)[1]);
        mBinding.opa2.setText(learningQuestionsNew.getMcq2().split("::", -1)[0]);
        mBinding.opa2.setText(learningQuestionsNew.getMcq2().split("::", -1)[1]);
        mBinding.opa3.setText(learningQuestionsNew.getMcq3().split("::", -1)[0]);
        mBinding.opb3.setText(learningQuestionsNew.getMcq3().split("::", -1)[1]);
        mBinding.opa4.setText(learningQuestionsNew.getMcq4().split("::", -1)[0]);
        mBinding.opb4.setText(learningQuestionsNew.getMcq4().split("::", -1)[1]);
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

    @Override
    public void onAnswerEntered(String answer) {

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
        } else if (mBinding.opa1.getText().toString().isEmpty()) {
            mBinding.opa1.setError("Please enter option 1.");
            return false;
        } else if (mBinding.opb1.getText().toString().isEmpty()) {
            mBinding.opb1.setError("Please enter option 1.");
            return false;
        } else if (mBinding.opa2.getText().toString().isEmpty()) {
            mBinding.opa2.setError("Please enter option 2.");
            return false;
        } else if (mBinding.opb2.getText().toString().isEmpty()) {
            mBinding.opb2.setError("Please enter option 2.");
            return false;
        }  else if (id1 == -1 || id2 == -1 || id3 == -1 || id4 == -1) {
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

    private String getCheckedRadioButton(RadioGroup radioGroup) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            if (radioButton.isChecked()) {
                return radioButton.getText().toString();
            }
        }
        return "";
    }

    private void setRadioDisable(RadioGroup radioGroup, String strSelected) {
        Log.i(TAG, "setRadioDisable : " + strSelected);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            String[] opts = strSelected.split(",", -1);
            for (String string : opts) {
                if (string != null && !string.isEmpty()) {
                    if (radioButton.getText().toString().contains(string)) {
                        radioButton.setChecked(false);
                        radioButton.setEnabled(false);
                    } else {
                        radioButton.setEnabled(true);
                    }
                }
            }
        }
    }

    private void resetRadioGroup(RadioGroup radioGroup) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            radioButton.setEnabled(true);
            radioButton.setChecked(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                    if (mClipData.getItemCount() >3) {
                        Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                    } else if (mAllUri.size() >3) {
                        Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                    } else {
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            if (mAllUri.size() >3) {
                                Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                            } else {
                                setupPreview(uri);
                            }

                        }
                    }
                } else if (data.getData() != null) {
                    if (mAllUri.size() >3) {
                        Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            final Uri imageUri = data.getData();
                            CropImage.activity(imageUri)
                                    .setInitialCropWindowPaddingRatio(0.0f)
                                    .start(getActivity(), MtpQuestFragment.this);
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
            if (mAllUri.size() >3) {
                Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
            } else {
                CropImage.activity(photouri)
                        .setInitialCropWindowPaddingRatio(0.0f)
                        .start(getActivity(), MtpQuestFragment.this);
            }
        } else if (requestCode == VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                ArrayList<Uri> video_uri = new ArrayList<>();
                if (data.getData() != null) {
                    Uri mVideoUri = data.getData();
                    int quality = ImageOptimization.getQualityReductionParams(getActivity(), mVideoUri);
                    Log.i(TAG, "File reduce size: " + quality);
                    if (mAllUri.size() >3) {
                        Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                    } else if (quality == 3) {
                        if (mAllUri.size() >3) {
                            Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                        } else {
                            video_uri.add(mVideoUri);
                            setupPreview(mVideoUri);
                        }

                    } else {
                        new DialogProcessFile(requireActivity(), mVideoUri, quality, (processedFile, processedFilePath, success) -> {
                            if (mAllUri.size() >3) {
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
                    if (mClipData.getItemCount() >3) {
                        Toast.makeText(getActivity(), "A maximum of 4 videos can be uploaded at once.", Toast.LENGTH_LONG).show();
                    } else {
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            video_uri.add(uri);
                            if (mAllUri.size() >3) {
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
                    if (mAllUri.size() >3) {
                        Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                    } else {
                        if (mAllUri.size() >3) {
                            Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                        } else {
                            video_uri.add(mVideoUri);
                            setupPreview(mVideoUri);
                        }

                    }
                } else if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    if (mClipData.getItemCount() >3) {
                        Toast.makeText(getActivity(), "A maximum of 4 videos can be uploaded at once.", Toast.LENGTH_LONG).show();
                    } else {
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            video_uri.add(uri);
                            if (mAllUri.size() >3) {
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
                if (mAllUri.size() >3) {
                    Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                } else {
                    Uri mVideoUri = data.getData();
                    setupPreview(mVideoUri);
                }

            } else {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    if (mClipData.getItemCount() >3) {
                        Toast.makeText(getActivity(), "A maximum of 4 audios can be uploaded at once.", Toast.LENGTH_LONG).show();
                    } else {
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            if (mAllUri.size() >3) {
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
        if (opt==Constant.QUESTION)
            setupPreview(uri);
    }

    @Override
    public void onScanText(String text, int ansId) {
        if (ansId==A1)
            mBinding.opa1.setText(text);
         else  if (ansId==B1)
            mBinding.opb1.setText(text);
        else  if (ansId==A2)
            mBinding.opa2.setText(text);
        else  if (ansId==B2)
            mBinding.opb2.setText(text);
        else  if (ansId==A3)
            mBinding.opa3.setText(text);
        else  if (ansId==B3)
            mBinding.opb3.setText(text);
        else  if (ansId==A4)
            mBinding.opa4.setText(text);
        else  if (ansId==B4)
            mBinding.opb4.setText(text);

    }


    private void addQuestion() {
        if (isValidate()) {

            ProgressDialog.getInstance().show(getActivity());
            MultipartBody.Part[] queImagesParts = null;
            String images = "", pair1 = "", pair2 = "", pair3 = "", pair4 = "";
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

            pair1 = mBinding.opa1.getText().toString() + "::" + mBinding.opb1.getText().toString();
            pair2 = mBinding.opa2.getText().toString() + "::" + mBinding.opb2.getText().toString();
            pair3 = mBinding.opa3.getText().toString() + "::" + mBinding.opb3.getText().toString();
            pair4 = mBinding.opa4.getText().toString() + "::" + mBinding.opb4.getText().toString();

            RequestBody userId = RequestBody.create(MediaType.parse("multipart/form-data"), user_id);
            RequestBody appname = RequestBody.create(MediaType.parse("multipart/form-data"), qoogol);
            RequestBody deviceId = RequestBody.create(MediaType.parse("multipart/form-data"), getDeviceId(getActivity()));
            RequestBody subId = RequestBody.create(MediaType.parse("multipart/form-data"), subjectId);
            RequestBody question = RequestBody.create(MediaType.parse("multipart/form-data"), AppUtils.encodedString(mBinding.etQuestion.getText().toString()));
            RequestBody questiondesc = RequestBody.create(MediaType.parse("multipart/form-data"), AppUtils.encodedString(mBinding.etQuestionDesc.getText().toString()));
            RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), MATCH_PAIR);
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


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int DRAWABLE_LEFT = 0;
        final int DRAWABLE_TOP = 1;
        final int DRAWABLE_RIGHT = 2;
        final int DRAWABLE_BOTTOM = 3;

        if(event.getAction() == MotionEvent.ACTION_UP) {
            if(event.getRawX() >= (mBinding.opa1.getRight() - mBinding.opa1.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                ((MainActivity) getActivity()).openAnsScanDialog(A1);
                return true;
            } else if(event.getRawX() >= (mBinding.opb1.getRight() - mBinding.opb1.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                ((MainActivity) getActivity()).openAnsScanDialog(B1);
                return true;
            } else if(event.getRawX() >= (mBinding.opa2.getRight() - mBinding.opa2.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                ((MainActivity) getActivity()).openAnsScanDialog(A2);
                return true;
            } else if(event.getRawX() >= (mBinding.opb2.getRight() - mBinding.opb2.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                ((MainActivity) getActivity()).openAnsScanDialog(B2);
                return true;
            } else if(event.getRawX() >= (mBinding.opa3.getRight() - mBinding.opa3.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                ((MainActivity) getActivity()).openAnsScanDialog(A3);
                return true;
            } else if(event.getRawX() >= (mBinding.opb3.getRight() - mBinding.opb3.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                ((MainActivity) getActivity()).openAnsScanDialog(B3);
                return true;
            } else if(event.getRawX() >= (mBinding.opa4.getRight() - mBinding.opa4.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                ((MainActivity) getActivity()).openAnsScanDialog(A4);
                return true;
            } else if(event.getRawX() >= (mBinding.opb4.getRight() - mBinding.opb4.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                ((MainActivity) getActivity()).openAnsScanDialog(B4);
                return true;
            }
        }
        return false;
    }

}
