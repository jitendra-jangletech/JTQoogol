package com.jangletech.qoogol.ui.upload_question;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
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
import android.webkit.MimeTypeMap;
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
import com.jangletech.qoogol.VideoActivity;
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
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.Util;
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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
    private static final int CAMERA_REQUEST = 1, GALLERY_REQUEST = 2, PICKFILE_REQUEST_CODE=3, VIDEO_REQUEST = 4, AUDIO_REQUEST = 5;
    private Uri mphotouri;
    public ArrayList<Uri> mAllUri = new ArrayList<>();
    public ArrayList<Uri> mAllVideoUri = new ArrayList<>();
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
              try {
                  if ( UtilHelper.isImage(media, getActivity())) {
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
                  } else if (UtilHelper.isDoc(media,getActivity())) {
                      File file = AppUtils.createImageFile(requireActivity(), media);
                      MimeTypeMap mime = MimeTypeMap.getSingleton();
                      String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                      Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                      pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                      pdfOpenintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                      if (media != null) {
                          pdfOpenintent.setDataAndType(media,AppUtils.getType(file.getName()));
                      } else {
                          pdfOpenintent.setDataAndType(media, "application/octet-stream");
                      }
                      getActivity().startActivity(pdfOpenintent);
                  }
              } catch (Exception e){
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

    private void openMediaDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireActivity());
        MediaUploadLayoutBinding mediaUploadLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(requireActivity()),
                R.layout.media_upload_layout, null, false);
        dialogBuilder.setView(mediaUploadLayoutBinding.getRoot());
        mediaUploadLayoutBinding.camera.setOnClickListener(view -> {
            mediaDialog.dismiss();
            requestStoragePermission(true, false, false,false);
        });


        mediaUploadLayoutBinding.gallery.setOnClickListener(view -> {
            mediaDialog.dismiss();
            requestStoragePermission(false, true, false,false);
        });


        mediaUploadLayoutBinding.videos.setOnClickListener(view -> {
            requestStoragePermission(false, false, false,true);
            mediaDialog.dismiss();
        });

        mediaUploadLayoutBinding.audios.setOnClickListener(view -> {
            requestStoragePermission(false, false, true,false);
            mediaDialog.dismiss();
        });

        mediaUploadLayoutBinding.scanPdf.setOnClickListener(v -> {
            mediaDialog.dismiss();
            new AddImageDialog(getActivity(), 1, this)
                    .show();
        });

        mediaUploadLayoutBinding.documents.setOnClickListener(v -> {
            mediaDialog.dismiss();
            requestStoragePermission(false, false, false,false);
        });

        mediaDialog = dialogBuilder.create();
        Objects.requireNonNull(mediaDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        mediaDialog.show();
    }

    private void requestStoragePermission(final boolean isCamera, final boolean isPictures, final boolean isAudio,  final boolean isVideo) {
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
                                getAudio();
                            } else  if (isVideo){
                                getVideo();
                            } else {
                                getDocument();
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

    protected void getDocument() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        String[] mimeTypes =
                {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain","application/rtf","application/pdf","application/zip", "application/vnd.android.package-archive"};

        intent.setType("application/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICKFILE_REQUEST_CODE);
    }

    private void getVideo() {
        Intent pickPhoto = new Intent();
        pickPhoto.setType("video/*");
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(pickPhoto, VIDEO_REQUEST);
    }

    private void getAudio() {
        Intent pickAudio = new Intent();
        pickAudio.setType("audio/*");
        pickAudio.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickAudio.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickAudio.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(pickAudio, AUDIO_REQUEST);
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
                    if (mClipData.getItemCount() > 4) {
                        Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                    } else if (mAllUri.size() > 4) {
                        Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                    } else {
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            setupPreview(uri);
                        }
                    }
                } else if (data.getData() != null) {
                    if (mAllUri.size() > 4) {
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
            if (mAllUri.size() > 4) {
                Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
            } else {
                setupPreview(mphotouri);
            }
        } else if (requestCode == Constant.REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK && data != null) {
            if (resultCode == RESULT_OK) {
                ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                //StringBuilder builder = new StringBuilder();
                for (NormalFile file : list) {
                    String path = file.getPath();
                    Log.i(TAG, "onActivityResult File Path : " + file.getPath());
                    extractImages(path);
                }
            }
        } else if (requestCode == VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                ArrayList<Uri> video_uri = new ArrayList<>();
                if (data.getData() != null) {
                    Uri mVideoUri = data.getData();
                    int quality = ImageOptimization.getQualityReductionParams(getActivity(), mVideoUri);
                    Log.i(TAG, "File reduce size: " + quality);
                    if (mAllUri.size() > 4) {
                        Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                    } else if (quality == 3) {
                        video_uri.add(mVideoUri);
                        setupPreview(mVideoUri);
                    } else {
                        new DialogProcessFile(requireActivity(), mVideoUri, quality, (processedFile, processedFilePath, success) -> {
                            if (success) {
                                video_uri.add(processedFile);
                                setupPreview(processedFile);
                            } else {
                                video_uri.add(mVideoUri);
                                setupPreview(mVideoUri);
                            }
                        });
                    }
                } else if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    if (mClipData.getItemCount() > 4) {
                        Toast.makeText(getActivity(), "A maximum of 4 videos can be uploaded at once.", Toast.LENGTH_LONG).show();
                    } else {
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            video_uri.add(uri);
                            if (mAllUri.size() > 4) {
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
        } else if (requestCode == PICKFILE_REQUEST_CODE&& resultCode == RESULT_OK && data != null) {
            try {
                ArrayList<Uri> video_uri = new ArrayList<>();
                if (data.getData() != null) {
                    Uri mVideoUri = data.getData();
                    if (mAllUri.size() > 4) {
                        Toast.makeText(getActivity(), "A maximum of 4 media can be uploaded at once.", Toast.LENGTH_LONG).show();
                    } else  {
                        video_uri.add(mVideoUri);
                        setupPreview(mVideoUri);
                    }
                } else if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    if (mClipData.getItemCount() > 4) {
                        Toast.makeText(getActivity(), "A maximum of 4 videos can be uploaded at once.", Toast.LENGTH_LONG).show();
                    } else {
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            video_uri.add(uri);
                            if (mAllUri.size() > 4) {
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
        }else if (requestCode == AUDIO_REQUEST) {
            if (data.getData() != null) {
                Uri mVideoUri = data.getData();
                setupPreview(mVideoUri);
            } else {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    if (mClipData.getItemCount() > 4) {
                        Toast.makeText(getActivity(), "A maximum of 4 audios can be uploaded at once.", Toast.LENGTH_LONG).show();
                    } else {
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            if (mAllUri.size() > 4) {
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
            if (UtilHelper.isImage(media, getActivity()) || UtilHelper.isVideo(media, getActivity()) || UtilHelper.isAudio(media, getActivity()) ||  UtilHelper.isDoc(media, getActivity())) {
                Log.d("#>type ", "image");
                mAllUri.add(media);
                mAllImages.clear();
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
            ProgressDialog.getInstance().show(getActivity());
            MultipartBody.Part[] queImagesParts=null;
            String images = "";
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
                        AppUtils.readFully(imageStream,imageFile);
                        File file = new File(mAllUri.get(index).getPath());
                        long fileSizeInMB = imageFile.length() / 1048576;
                        Log.i(TAG, "File Size: " + fileSizeInMB + " MB");
                        if (fileSizeInMB > new PreferenceManager(getActivity()).getImageSize()) {
                            Toast.makeText(getActivity(), "Please upload the image of size less than 10MB", Toast.LENGTH_LONG).show();
                        } else {
                            RequestBody queBody = null;
                            if (UtilHelper.isImage(single_image,getActivity())) {
                                queBody = RequestBody.create(MediaType.parse("image/*"),
                                        imageFile);
                            } else if (UtilHelper.isVideo(single_image,getActivity())) {
                                queBody = RequestBody.create(MediaType.parse("video/*"),
                                        imageFile);
                            } else if (UtilHelper.isAudio(single_image,getActivity())) {
                                queBody = RequestBody.create(MediaType.parse("audio/*"),
                                        imageFile);
                            }else if (UtilHelper.isDoc(single_image,getActivity())) {
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
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            if (mAllImages!=null && mAllImages.size()>0) {
                try {
                    queImagesParts = new MultipartBody.Part[mAllImages.size()];
                    for (int index = 0; index < mAllImages.size(); index++) {
                        String single_image = mAllImages.get(index);
                        File imageFile = AppUtils.createImageFile(requireActivity(), single_image);
                        if (!imageFile.exists()) {
                            imageFile.createNewFile();
                        }
                        final InputStream imageStream = getActivity().getContentResolver().openInputStream(Uri.fromFile(imageFile));
                        AppUtils.readFully(imageStream,imageFile);
                        long fileSizeInMB = imageFile.length() / 1048576;
                        Log.i(TAG, "File Size: " + fileSizeInMB + " MB");
                        if (fileSizeInMB > new PreferenceManager(getActivity()).getImageSize()) {
                            Toast.makeText(getActivity(), "Please upload the image of size less than 10MB", Toast.LENGTH_LONG).show();
                        } else {
                            RequestBody queBody = RequestBody.create(MediaType.parse("image/*"),
                                    imageFile);
                            queImagesParts[index] = MultipartBody.Part.createFormData("Files",
                                    imageFile.getName(), queBody);
                            if (images.isEmpty())
                                images = AppUtils.encodedString(imageFile.getName());
                            else
                                images = images + "," + AppUtils.encodedString(imageFile.getName());
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            UploadQuestion uploadQuestion = (UploadQuestion) getArguments().getSerializable("Question");
            RequestBody userId = RequestBody.create(MediaType.parse("multipart/form-data"), new PreferenceManager(getActivity()).getUserId());
            RequestBody appname = RequestBody.create(MediaType.parse("multipart/form-data"),qoogol);
            RequestBody deviceId = RequestBody.create(MediaType.parse("multipart/form-data"),getDeviceId(getActivity()));
            RequestBody subId = RequestBody.create(MediaType.parse("multipart/form-data"), uploadQuestion.getSubjectId());
            RequestBody question = RequestBody.create(MediaType.parse("multipart/form-data"),  AppUtils.encodedString(mBinding.questionEdittext.getText().toString()));
            RequestBody questiondesc = RequestBody.create(MediaType.parse("multipart/form-data"),  AppUtils.encodedString(mBinding.questiondescEdittext.getText().toString()));
            RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), SCQ);
            RequestBody scq1 = RequestBody.create(MediaType.parse("multipart/form-data"),AppUtils.encodedString( mBinding.scq1Edittext.getText().toString()));
            RequestBody scq2 = RequestBody.create(MediaType.parse("multipart/form-data"), AppUtils.encodedString(mBinding.scq2Edittext.getText().toString()));
            RequestBody scq3 = RequestBody.create(MediaType.parse("multipart/form-data"), AppUtils.encodedString(mBinding.scq3Edittext.getText().toString()));
            RequestBody scq4 = RequestBody.create(MediaType.parse("multipart/form-data"), AppUtils.encodedString(mBinding.scq4Edittext.getText().toString()));
            RequestBody marks = RequestBody.create(MediaType.parse("multipart/form-data"), mBinding.edtmarks.getText().toString());
            RequestBody duration = RequestBody.create(MediaType.parse("multipart/form-data"), mBinding.edtduration.getText().toString());
            RequestBody difflevel = RequestBody.create(MediaType.parse("multipart/form-data"), getSelectedDiffLevel());
            RequestBody ans = RequestBody.create(MediaType.parse("multipart/form-data"), getSelectedAns());
            RequestBody imgname = RequestBody.create(MediaType.parse("multipart/form-data"), images);

            Call<ResponseObj> call = getApiService().addSCQQuestionsApi(userId, appname, deviceId,
                    subId, question,questiondesc, type,scq1,scq2, scq3,scq4, marks,duration,difflevel, ans,imgname,queImagesParts);
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
        setupPreview(imageObject.getUri());
//        mAllUri.clear();
//        mBinding.queimgRecycler.setVisibility(View.VISIBLE);
//        String path = imageObject.getName();
//
//        mAllImages.add(path);
//        pdfImageAdapter = new PdfImageAdapter(getActivity(), mAllImages, this);
//        mBinding.queimgRecycler.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//        mBinding.queimgRecycler.setLayoutManager(linearLayoutManager);
//        mBinding.queimgRecycler.setAdapter(pdfImageAdapter);
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
        bundle.putString("img", "que_img");
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
        if (mAllImages.size() == 0)
            mBinding.queimgRecycler.setVisibility(View.GONE);
    }
}
