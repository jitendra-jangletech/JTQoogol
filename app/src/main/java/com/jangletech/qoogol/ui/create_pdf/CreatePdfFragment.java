package com.jangletech.qoogol.ui.create_pdf;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.CreatePdfAdapter;
import com.jangletech.qoogol.databinding.FragmentCreatePdfBinding;
import com.jangletech.qoogol.dialog.SaveFileDialog;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.ItemOffsetDecoration;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.shockwave.pdfium.PdfiumCore;
import com.theartofdev.edmodo.cropper.CropImage;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class CreatePdfFragment extends BaseFragment implements CreatePdfAdapter.CreatePdfClickListener, SaveFileDialog.SaveFileClickListener {

    private static final String TAG = "CreatePdfFragment";
    private FragmentCreatePdfBinding mBinding;
    private CreatePdfAdapter mAdapter;
    private GridLayoutManager gridLayoutManager;
    private ItemOffsetDecoration itemDecoration;
    private Uri imageUri;
    private List<Uri> images = new ArrayList<>();
    private static final int REQUEST_CAMERA = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_pdf, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        itemDecoration = new ItemOffsetDecoration(requireActivity(), R.dimen.item_offset);
        setPdfSamplePdfAdapter(1);

        mBinding.tvMyPdfs.setOnClickListener(v -> {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_my_pdf, Bundle.EMPTY);
        });

        mBinding.btnAddImage.setOnClickListener(v -> {
            Dexter.withActivity(getActivity())
                    .withPermissions(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    String filename = System.currentTimeMillis() + ".jpg";
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, filename);
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    Intent intent = new Intent();
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, REQUEST_CAMERA);
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
            }).check();
        });

        mBinding.btnGeneratePdf.setOnClickListener(v -> {
            List<File> fileList = new ArrayList<>();
            fileList = getAllFilesFromDirectory(tempPdfPath, FILE_TYPE_PDF);
            Log.i(TAG, "onActivityCreated Size : " + fileList.size());
            if (fileList != null &&
                    fileList.size() > 0) {
                DialogFragment saveFileDialog = new SaveFileDialog(this);
                saveFileDialog.show(getParentFragmentManager(), "dialog_save_file");
            } else {
                showAlert("No Pdf Files Added to generate document. Please add pdf files then try again!!");
            }
        });
    }

//    private void showSaveFileDialog() {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
//        alertDialog.setTitle("Save File");
//        final EditText input = new EditText(getActivity());
//        input.setHint("Enter File Name");
//        input.setKeyListener(DigitsKeyListener.getInstance("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_"));
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//        input.setLayoutParams(lp);
//        alertDialog.setView(input);
//        alertDialog.setPositiveButton("Save",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        String fileName = input.getText().toString().trim();
//                        if (fileName.length() > 0) {
//                            mergePdf(fileName);
//                        } else {
//                            showToast("Please enter file name.");
//                        }
//                    }
//                });
//        alertDialog.show();
//    }

    private void setPdfSamplePdfAdapter(int flag) {
        images.clear();
        List<File> tempList = new ArrayList<>();
        tempList = getAllFilesFromDirectory(tempPdfPath, FILE_TYPE_PNG);
        if (tempList != null && tempList.size() > 0) {
            for (File file : tempList) {
                Log.i(TAG, "setPdfSamplePdfAdapter: " + Uri.fromFile(new File(file.getAbsolutePath())));
                images.add(Uri.fromFile(new File(file.getAbsolutePath())));
            }
        }
        if (flag == 1) {
            mAdapter = new CreatePdfAdapter(getActivity(), images, 1, this);
            gridLayoutManager = new GridLayoutManager(getActivity(), 3);
            mBinding.recyclerView.setLayoutManager(gridLayoutManager);
            mBinding.recyclerView.addItemDecoration(itemDecoration);
            mBinding.recyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.updateList(images);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA) {
            Log.d(TAG, "onActivityResult REQUEST_CAMERA: " + imageUri);
            try {
                CropImage.activity(imageUri)
                        .setInitialCropWindowPaddingRatio(0.0f)
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
                Log.i(TAG, "onActivityResult Uri : " + result.getUri());
                //images.add(result.getUri());
                createNewPdfPage(result.getUri());
            }
        }
    }

    @Override
    public void onRemoveClick(Uri uri, int position) {
        Log.i(TAG, "onRemoveClick : " + position);
        mAdapter.deleteItem(position);
        deleteFile(uri.toString());
    }

    @Override
    public void onShareClick(Uri uri, int position) {
        Log.i(TAG, "onShareClick: ");
    }

    @Override
    public void onItemClick(Uri uri, int position) {
        Log.i(TAG, "onItemClick: " + uri);
        Log.i(TAG, "getFileName  View Click : " + uri.toString().substring(uri.toString().lastIndexOf('/')));
        if (uri.toString().contains("png")) {
            //String[] path = uri.toString().split("\\.", -1);
            //Uri myUri = Uri.parse(path[1]);
            showPdf(getPdfFileName(uri, "pdf"));
        } else {
            showPdf(uri);
        }
    }


    private void createNewPdfPage(Uri uri) {
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
            Bitmap resizedBmp = Bitmap.createScaledBitmap(bmp, 960, 1280, false);
            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(960, 1280, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(myPageInfo);

            page.getCanvas().drawBitmap(resizedBmp, 0, 25, null);
            pdfDocument.finishPage(page);

            File pdfFile = new File(tempPdfPath);
            if (!pdfFile.exists()) {
                pdfFile.mkdirs();
            }
            String pdfPageName = tempPdfPath + "Page_" + System.currentTimeMillis();
            String finalFileName = pdfPageName + ".pdf";
            String pngFileName = pdfPageName + ".png";
            pdfDocument.writeTo(new FileOutputStream(new File(finalFileName)));
            pdfDocument.close();
            writePngFile(uri, pngFileName);
            setPdfSamplePdfAdapter(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writePngFile(Uri uri, String path) {
        try {
            final InputStream imageStream = getActivity().getContentResolver().openInputStream(uri);
            AppUtils.readFully(imageStream, new File(path));
            //mAdapter.insertItem(Uri.parse(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mergePdf(String fileName) {
        File finalDocs = new File(finalPdfDocs);
        String mergedPdfName = finalPdfDocs + fileName + ".pdf";

        if (!finalDocs.exists()) {
            finalDocs.mkdirs();
            Log.i(TAG, "Folder Created : ");
        }

        List<File> temget = new ArrayList<>();
        temget = getAllFilesFromDirectory(tempPdfPath, FILE_TYPE_PDF);
        Log.i(TAG, "mergePdf Size : " + temget.size());

        if (!new File(mergedPdfName).exists()) {
            try {
                Document document = new Document();
                PdfCopy copy = new PdfCopy(document, new FileOutputStream(mergedPdfName));
                document.open();
                for (File file : temget) {
                    Log.i(TAG, "mergePdf Name: " + file.getName());
                    Log.i(TAG, "mergePdf Path : " + file.getAbsolutePath());
                    PdfReader reader = new PdfReader(file.getAbsolutePath());
                    copy.addDocument(reader);
                }
                document.close();
                FileUtils.deleteDirectory(new File(tempPdfPath));
                setPdfSamplePdfAdapter(0);
                showSuccessAlert(mergedPdfName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showToast("File Name Already Exists. Please try with different file name.");
        }
    }

    private void showSuccessAlert(String fileName) {
        Uri uri = Uri.fromFile(new File(fileName));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        builder.setTitle("Success")
                .setMessage("Pdf document generated successfully. please check my pdf link to see all generated documents.")
                .setPositiveButton("View", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showPdf(uri);
                    }
                })
                .show();
    }

    void generateImageFromPdf(Uri pdfUri, String fileName) {
        int pageNumber = 0;
        PdfiumCore pdfiumCore = new PdfiumCore(getActivity());
        try {
            ParcelFileDescriptor fd = getActivity().getContentResolver().openFileDescriptor(pdfUri, "r");
            com.shockwave.pdfium.PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            pdfiumCore.openPage(pdfDocument, pageNumber);
            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);
            saveImage(bmp, fileName);
            pdfiumCore.closeDocument(pdfDocument); // important!
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveImage(Bitmap bmp, String fileName) {
        //String thumbnail = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Qoogol/Thumbnails/";
        FileOutputStream out = null;
        try {
            File folder = new File(fileName);
            if (!folder.exists())
                folder.mkdirs();
            File file = new File(folder, fileName + ".png");
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSaveClick(String name) {
        Log.i(TAG, "onSaveClick File Name : " + name);
        mergePdf(name);
    }
}