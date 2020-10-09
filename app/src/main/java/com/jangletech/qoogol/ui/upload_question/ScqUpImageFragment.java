package com.jangletech.qoogol.ui.upload_question;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.FragmenUpScqImageBinding;
import com.jangletech.qoogol.dialog.AddImageDialog;
import com.jangletech.qoogol.model.ImageObject;
import com.jangletech.qoogol.model.UploadQuestion;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.AppUtils;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class ScqUpImageFragment extends BaseFragment implements AddImageDialog.AddImageClickListener {

    private static final String TAG = "ScqUpImageFragment";
    private UploadQuestion uploadQuestion;
    private FragmenUpScqImageBinding mBinding;
    private AddImageDialog addImageDialog;
    private int optionId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragmen_up_scq_image, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.REQUEST_CODE_PICK_FILE:
                if (resultCode == RESULT_OK) {
                    ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                    //StringBuilder builder = new StringBuilder();
                    for (NormalFile file : list) {
                        String path = file.getPath();
                        Log.i(TAG, "onActivityResult File Path : " + file.getPath());
                        extractImages(path);
                    }
                }
                break;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null && getArguments().getSerializable("Question") != null) {
            uploadQuestion = (UploadQuestion) getArguments().getSerializable("Question");
            mBinding.etQuestion.setText(uploadQuestion.getQuestDescription());
            mBinding.subject.setText("Subject : " + uploadQuestion.getSubjectName());

            if (uploadQuestion.getQuestionType().equals(com.jangletech.qoogol.util.Constant.scq_image)) {
                getActionBar().setTitle("Scq Image");
            } else {
                getActionBar().setTitle("Mcq Image");
            }
        }

        mBinding.toggleAddQuestDesc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBinding.etQuestionDesc.setVisibility(View.VISIBLE);
                } else {
                    mBinding.etQuestionDesc.setVisibility(View.GONE);
                }
            }
        });

        mBinding.image1.setOnClickListener(v -> {
            new AddImageDialog(getActivity(), 1, this)
                    .show();
        });

        mBinding.image2.setOnClickListener(v -> {
            new AddImageDialog(getActivity(), 2, this)
                    .show();
        });
        mBinding.image3.setOnClickListener(v -> {
            new AddImageDialog(getActivity(), 3, this)
                    .show();
        });
        mBinding.image4.setOnClickListener(v -> {
            new AddImageDialog(getActivity(), 4, this)
                    .show();
        });
    }

    @Override
    public void onImageClickListener(ImageObject imageObject, int opt) {
        Log.d(TAG, "onImageClickListener Option Id : " + opt);
        String path = imageObject.getName();
        if (opt == 1) {
            setImage(mBinding.image1, path);
        } else if (opt == 2) {
            setImage(mBinding.image2, path);
        } else if (opt == 3) {
            setImage(mBinding.image3, path);
        } else if (opt == 4) {
            setImage(mBinding.image4, path);
        }
    }

    @Override
    public void onScanPdfClick(int optId) {
        optionId = optId;
        Intent intent4 = new Intent(getActivity(), NormalFilePickActivity.class);
        intent4.putExtra(Constant.MAX_NUMBER, 1);
        intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"pdf"});
        startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
    }

    private void setImage(ImageView image, String path) {
        Glide.with(getActivity())
                .load(path)
                .into(image);
    }

    private void extractImages(String filepath) {
        Log.i(TAG, "extractImages Copied File Path : " + filepath);
        PRStream prStream;

        File file;

        PdfObject pdfObject;

        PdfImageObject pdfImageObject;

        FileOutputStream fos;

        try {

            PdfReader reader = new PdfReader(filepath);

            File folder = new File(Environment.getExternalStorageDirectory() + "/Qoogol/Temp");

            if (!folder.exists())
                folder.mkdir();
            else
                AppUtils.deleteDir(folder);

            // Get number of objects in pdf document

            int numOfObject = reader.getXrefSize();
            int imageCount = 0;

            Log.i(TAG, "No. Of Objects Found : " + numOfObject);

            for (int i = 0; i < numOfObject; i++) {

                // Get PdfObject

                pdfObject = reader.getPdfObject(i);

                if (pdfObject != null && pdfObject.isStream()) {

                    prStream = (PRStream) pdfObject; //cast object to stream

                    PdfObject type = prStream.get(PdfName.SUBTYPE); //get the object type

                    // Check if the object is the image type object

                    if (type != null && (type.toString().equals(PdfName.IMAGE.toString()))) {
//                            type.toString().equals(PdfName.IMAGEB.toString()) ||
//                            type.toString().equals(PdfName.IMAGEC.toString()) ||
//                            type.toString().equals(PdfName.IMAGEI.toString()))) {

                        imageCount++;
                        // Get the image from the stream

                        pdfImageObject = new PdfImageObject(prStream);

                        fos = new FileOutputStream(folder.getPath() + "/" + System.currentTimeMillis() + ".jpg");

                        // Read bytes of image in to an array

                        byte[] imgdata = pdfImageObject.getImageAsBytes();

                        // Write the bytes array to file

                        fos.write(imgdata, 0, imgdata.length);

                        fos.flush();

                        fos.close();
                    }
                }
            }

            //mBinding.tvNoOfImages.setText("Images Found : " + imageCount);
            showToast("Images Extracted : " + imageCount);
            new AddImageDialog(getActivity(), optionId, this)
                    .show();
            Log.i(TAG, "extractImages Total Image Count : " + imageCount);

        } catch (IOException ioe) {
            ioe.printStackTrace();
            extractImages(filepath);
        }
    }
}
