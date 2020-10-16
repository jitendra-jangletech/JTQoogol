package com.jangletech.qoogol.ui.upload_question;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.VideoActivity;
import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.adapter.AdapterGallerySelectedImage;
import com.jangletech.qoogol.databinding.FragmenUpScqImageBinding;
import com.jangletech.qoogol.dialog.AddImageDialog;
import com.jangletech.qoogol.listeners.QueMediaListener;
import com.jangletech.qoogol.model.ImageObject;
import com.jangletech.qoogol.model.UploadQuestion;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.ui.learning.SlideshowDialogFragment;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.ImageOptimization;
import com.jangletech.qoogol.util.UtilHelper;
import com.jangletech.qoogol.videocompressions.DialogProcessFile;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class ScqUpImageFragment extends BaseFragment implements AddImageDialog.AddImageClickListener, QueMediaListener {

    private static final String TAG = "ScqUpImageFragment";
    private UploadQuestion uploadQuestion;
    private FragmenUpScqImageBinding mBinding;
    private AddImageDialog addImageDialog;
    private int optionId;
    private static final int CAMERA_REQUEST = 1, GALLERY_REQUEST = 2, PICKFILE_REQUEST_CODE = 3, VIDEO_REQUEST = 4, AUDIO_REQUEST = 5;
    public ArrayList<Uri> mAllUri = new ArrayList<>();
    public ArrayList<String> mAllImages = new ArrayList<>();
    private AdapterGallerySelectedImage galleryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragmen_up_scq_image, container, false);
        ((MainActivity) getActivity()).setOnDataListener(this);
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
            getActionBar().setTitle("Scq Image");
        }

        initSelectedImageView();



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

        mBinding.saveQuestion.setOnClickListener(v -> addQuestion());

        mBinding.addImages.setOnClickListener(v -> ((MainActivity) getActivity()).openMediaDialog());
    }

    private void addQuestion() {
    }

    private void initSelectedImageView() {
        setupPreview(null);
        galleryAdapter = new AdapterGallerySelectedImage(mAllUri, getActivity(), new AdapterGallerySelectedImage.GalleryUplodaHandler() {
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

    @Override
    public void onMediaReceived(int requestCode, int resultCode, Intent data, Uri photouri) {
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
                            setupPreview(imageUri);
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
                setupPreview(photouri);
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
    public void onScanImageClick(Uri uri) {
        setupPreview(uri);
    }
}
