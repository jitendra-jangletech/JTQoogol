package com.jangletech.qoogol.ui.upload_question;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.jangletech.qoogol.BuildConfig;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.AdapterGallerySelectedImage;
import com.jangletech.qoogol.adapter.PdfImageAdapter;
import com.jangletech.qoogol.databinding.AlertDialogBinding;
import com.jangletech.qoogol.databinding.FragmentScqQueBinding;
import com.jangletech.qoogol.databinding.MediaUploadLayoutBinding;
import com.jangletech.qoogol.dialog.AddImageDialog;
import com.jangletech.qoogol.dialog.AnsScanDialog;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.ImageObject;
import com.jangletech.qoogol.model.ResponseObj;
import com.jangletech.qoogol.model.UploadQuestion;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.ui.learning.SlideshowDialogFragment;
import com.jangletech.qoogol.util.AppUtils;
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
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

import static android.app.Activity.RESULT_OK;
import static com.jangletech.qoogol.util.Constant.SCQ;
import static com.jangletech.qoogol.util.Constant.qoogol;

/**
 * A simple {@link Fragment}5 subclass.
 */
public class SCQ_QueFragment extends BaseFragment implements AnsScanDialog.AnsScannerListener,
        AddImageDialog.AddImageClickListener, PdfImageAdapter.pdfImageItemClick {

    private static final String TAG = "SCQ_QueFragment";
    private FragmentScqQueBinding mBinding;
    private UploadQuestion uploadQuestion;
    private static final int REQUEST_GALLERY = 0;
    private static final int REQUEST_CAMERA = 1;
    private Uri imageUri;
    private int ansId;
    private AlertDialog mediaDialog;
    private static final int CAMERA_REQUEST = 1, GALLERY_REQUEST = 2, VIDEO_REQUEST = 4, EDIT_IMAGE_REQUEST = 5;
    private Uri mphotouri;
    public ArrayList<Uri> mAllUri = new ArrayList<>();
    public ArrayList<String> mAllImages = new ArrayList<>();
    private AdapterGallerySelectedImage galleryAdapter;
    private PdfImageAdapter pdfImageAdapter;
    private int optionId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_scq__que, container, false);
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

        initView();
        initSelectedImageView();
        mBinding.sc1Edit.setOnClickListener(v -> {
            new AnsScanDialog(getActivity(), 1, this)
                    .show();
        });

        mBinding.scq2Edit.setOnClickListener(v -> {
            new AnsScanDialog(getActivity(), 2, this)
                    .show();
        });

        mBinding.scq3Edit.setOnClickListener(v -> {
            new AnsScanDialog(getActivity(), 3, this)
                    .show();
        });
        mBinding.scq4Edit.setOnClickListener(v -> {
            new AnsScanDialog(getActivity(), 4, this)
                    .show();
        });

        mBinding.saveQuestion.setOnClickListener(v -> addQuestion());

        mBinding.addImages.setOnClickListener(v -> openMediaDialog());
    }

    private void initView() {

    }

    private void initSelectedImageView() {
        setupPreview(null);
        galleryAdapter = new AdapterGallerySelectedImage(mAllUri, getActivity(), new AdapterGallerySelectedImage.GalleryUplodaHandler() {
            @Override
            public void imageClick(Uri media, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("urilist", (Serializable) mAllUri);
                bundle.putInt("position", 0);
                bundle.putString("type", "uri");
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void addClick(Uri media, int position) {
            }

            @Override
            public void actionRemoved(int position) {
                mAllUri.remove(position);
                galleryAdapter.notifyItemRemoved(position);
                galleryAdapter.notifyItemRangeChanged(position, mAllUri.size());
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mBinding.queimgRecycler.setLayoutManager(mLayoutManager);
        mBinding.queimgRecycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.queimgRecycler.setAdapter(galleryAdapter);
    }

    private void openMediaDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireActivity());
        MediaUploadLayoutBinding mediaUploadLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(requireActivity()),
                R.layout.media_upload_layout, null, false);
        dialogBuilder.setView(mediaUploadLayoutBinding.getRoot());
        mediaUploadLayoutBinding.camera.setOnClickListener(view -> {
            mediaDialog.dismiss();
            requestStoragePermission(true, false, false);
        });


        mediaUploadLayoutBinding.gallery.setOnClickListener(view -> {
            mediaDialog.dismiss();
            requestStoragePermission(false, true, false);
        });


        mediaUploadLayoutBinding.videos.setOnClickListener(view -> {
            requestStoragePermission(false, false, false);
            mediaDialog.dismiss();
        });


        mediaUploadLayoutBinding.scanPdf.setOnClickListener(v -> {
            mediaDialog.dismiss();
            new AddImageDialog(getActivity(), 1, this)
                    .show();
        });

        mediaDialog = dialogBuilder.create();
        Objects.requireNonNull(mediaDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        mediaDialog.show();
    }

    private void requestStoragePermission(final boolean isCamera, final boolean isPictures, final boolean isAudio) {
        Dexter.withActivity(requireActivity())
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            if (isCamera) {
                                dispatchTakePictureIntent();
                            } else if (isPictures) {
                                getImages();
                            } else if (isAudio) {

                            } else {
                                getVideo();
                            }
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions,
                                                                   PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .withErrorListener(error -> {
                    // Toast.makeText(mContext, "Error occurred! ", Toast.LENGTH_SHORT).show();
                })
                .onSameThread()
                .check();
    }

    private void getVideo() {
        Intent pickPhoto = new Intent();
        pickPhoto.setType("video/*");
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(pickPhoto, VIDEO_REQUEST);
    }

    private void getImages() {
        Intent pickPhoto = new Intent();
        pickPhoto.setType("image/*");
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(pickPhoto, "Select Picture"), GALLERY_REQUEST);
    }

    private void showSettingsDialog() {
        Dialog builder = new Dialog(requireActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        AlertDialogBinding alertDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(requireActivity()), R.layout.alert_dialog, null, false);
        builder.setContentView(alertDialogBinding.getRoot());
        alertDialogBinding.tvTitle.setText("Need Permissions");
        alertDialogBinding.tvDesc.setText("This app needs permission to use this feature. You can grant them in app settings.");
        alertDialogBinding.btnPositive.setText("GOTO SETTINGS");
        alertDialogBinding.btnPositive.setOnClickListener(view -> {
            builder.dismiss();
            openSettings();
        });
        alertDialogBinding.btnNeutral.setText("Cancel");
        alertDialogBinding.btnNeutral.setOnClickListener(view -> builder.dismiss());
        Objects.requireNonNull(builder.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = AppUtils.createImageFile(requireActivity());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(requireActivity(),
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);

                mphotouri = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
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
        if (mBinding.questionEdittext.getText().toString().isEmpty()) {
            mBinding.questionEdittext.setError("Please enter question.");
            return false;
        }
        if (mBinding.scq1Edittext.getText().toString().isEmpty()) {
            mBinding.scq1Edittext.setError("Please enter option 1.");
            return false;
        }
        if (mBinding.scq2Edittext.getText().toString().isEmpty()) {
            mBinding.scq2Edittext.setError("Please enter option 2.");
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
                    Log.e(TAG, "onPermissionsChecked Error : ");
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK && data != null || requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                ArrayList<Uri> mArrayUri = new ArrayList<>();
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    if (mClipData.getItemCount() > 10) {
                        Toast.makeText(getActivity(), "A maximum of 10 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                    } else if (mAllUri.size() > 10) {
                        Toast.makeText(getActivity(), "A maximum of 10 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                    } else {
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            setupPreview(uri);
                        }
                    }
                } else if (data.getData() != null) {
                    if (mAllUri.size() > 10) {
                        Toast.makeText(getActivity(), "A maximum of 10 media can be uploaded at once.", Toast.LENGTH_LONG).show();
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
        } else if (requestCode == CAMERA_REQUEST) {
            if (mAllUri.size() > 10) {
                Toast.makeText(getActivity(), "A maximum of 10 media can be uploaded at once.", Toast.LENGTH_LONG).show();
            } else {
                setupPreview(mphotouri);
            }
        } else if (requestCode == Constant.REQUEST_CODE_PICK_FILE) {
            if (resultCode == RESULT_OK) {
                ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                //StringBuilder builder = new StringBuilder();
                for (NormalFile file : list) {
                    String path = file.getPath();
                    Log.i(TAG, "onActivityResult File Path : " + file.getPath());
                    extractImages(path);
                }
            }
        }
    }

    private void extractImages(String filepath) {
        Log.i(TAG, "extractImages Copied File Path : " + filepath);
        PRStream prStream;

        File file;

        PdfObject pdfObject;

        PdfImageObject pdfImageObject;

        FileOutputStream fos;

        try {

            // Create pdf reader
            //file = new File(filepath);

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
            if (UtilHelper.isImage(media, getActivity()) || UtilHelper.isVideo(media, getActivity())) {
                Log.d("#>type ", "image");
                mAllUri.add(media);
                galleryAdapter.notifyDataSetChanged();
                mBinding.queimgRecycler.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getActivity(), "Upload the proper media", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
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
            mBinding.scq1Edittext.setText(text);
        if (ansId == 2)
            mBinding.scq2Edittext.setText(text);
        if (ansId == 3)
            mBinding.scq3Edittext.setText(text);
        if (ansId == 4)
            mBinding.scq4Edittext.setText(text);
    }

    private void addQuestion() {
        if (isValidate()) {
            UploadQuestion uploadQuestion = (UploadQuestion) getArguments().getSerializable("Question");
            String user_id = new PreferenceManager(getActivity()).getUserId();
            Call<ResponseObj> call = getApiService().addQuestionsApi(user_id, qoogol, getDeviceId(getActivity()),
                    uploadQuestion.getSubjectId(), mBinding.questionEdittext.getText().toString(),
                    mBinding.questiondescEdittext.getText().toString(), SCQ, mBinding.scq1Edittext.getText().toString(),
                    mBinding.scq2Edittext.getText().toString(), mBinding.scq3Edittext.getText().toString(),
                    mBinding.scq4Edittext.getText().toString(), mBinding.edtmarks.getText().toString(),
                    mBinding.edtduration.getText().toString(), getSelectedDiffLevel(), getSelectedAns());
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
    public void onImageClickListener(ImageObject imageObject, int opt) {
        mAllUri.clear();
        String path = imageObject.getName();
        mAllImages.add(path);
        pdfImageAdapter = new PdfImageAdapter(getActivity(), mAllImages, this);
        mBinding.queimgRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mBinding.queimgRecycler.setLayoutManager(linearLayoutManager);
        mBinding.queimgRecycler.setAdapter(pdfImageAdapter);

    }

    @Override
    public void onScanPdfClick(int optId) {
        optionId = optId;
        Intent intent4 = new Intent(getActivity(), NormalFilePickActivity.class);
        intent4.putExtra(com.vincent.filepicker.Constant.MAX_NUMBER, 1);
        intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"pdf"});
        startActivityForResult(intent4, com.vincent.filepicker.Constant.REQUEST_CODE_PICK_FILE);
    }

    @Override
    public void onimageClick(String media, int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("images", (Serializable) mAllImages);
        bundle.putInt("position", 0);
        SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
        newFragment.setArguments(bundle);
        FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
        newFragment.show(ft, "slideshow");
    }

    @Override
    public void onItemRemove(int position) {
        mAllImages.remove(position);
        pdfImageAdapter.notifyItemRemoved(position);
        pdfImageAdapter.notifyItemRangeChanged(position, mAllImages.size());
    }
}
