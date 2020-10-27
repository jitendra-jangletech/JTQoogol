package com.jangletech.qoogol.ui.create_pdf;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.CreatePdfAdapter;
import com.jangletech.qoogol.databinding.FragmentCreatePdfBinding;
import com.jangletech.qoogol.ui.BaseFragment;
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
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class CreatePdfFragment extends BaseFragment implements CreatePdfAdapter.CreatePdfClickListener {

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
        setPdfSamplePdfAdapter();

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
            if (getAllFilesFromDirectory(tempPdfPath) != null &&
                    getAllFilesFromDirectory(tempPdfPath).length > 0) {
                mergePdf();
                /*for (File file : getAllFilesFromDirectory(tempPdfPath)) {
                    generateImageFromPdf(Uri.fromFile(file));
                }*/
            } else {
                showAlert("No Pdf Files Added to generate document. Please add pdf files then try again!!");
            }
        });
    }

    private void setPdfSamplePdfAdapter() {
        images.clear();
        if (getAllFilesFromDirectory(tempPdfPath) != null) {
            File[] files = getAllFilesFromDirectory(tempPdfPath);
            for (File file : files) {
                Log.i(TAG, "setPdfSamplePdfAdapter: " + Uri.fromFile(new File(file.getAbsolutePath())));
                images.add(Uri.fromFile(new File(file.getAbsolutePath())));
            }

        }
        mAdapter = new CreatePdfAdapter(getActivity(), images, 1, this);
        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mBinding.recyclerView.setLayoutManager(gridLayoutManager);
        mBinding.recyclerView.addItemDecoration(itemDecoration);
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                Log.i(TAG, "onActivityResult Uri : " + result.getUri());
                images.add(result.getUri());
                Log.i(TAG, "onActivityResult Size : " + images.size());
                createNewPdfPage(result.getUri());
                // mAdapter.updateList(images);
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
        showPdf(uri);
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
            String pdfPageName = tempPdfPath + "Page_" + System.currentTimeMillis() + ".pdf";
            pdfDocument.writeTo(new FileOutputStream(new File(pdfPageName)));
            pdfDocument.close();

            setPdfSamplePdfAdapter();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mergePdf() {
        File finalDocs = new File(finalPdfDocs);
        String mergedPdfName = finalPdfDocs + "Final_" + System.currentTimeMillis() + ".pdf";

        if (!finalDocs.exists()) {
            finalDocs.mkdirs();
            Log.i(TAG, "Folder Created : ");
        }

        File[] pdfFiles = getAllFilesFromDirectory(tempPdfPath);

        try {
            Document document = new Document();
            PdfCopy copy = new PdfCopy(document, new FileOutputStream(mergedPdfName));
            document.open();
            for (File file : pdfFiles) {
                Log.i(TAG, "mergePdf Name: " + file.getName());
                Log.i(TAG, "mergePdf Path : " + file.getAbsolutePath());
                PdfReader reader = new PdfReader(file.getAbsolutePath());
                copy.addDocument(reader);
            }
            document.close();
            FileUtils.deleteDirectory(new File(tempPdfPath));
            setPdfSamplePdfAdapter();
            showSuccessAlert(mergedPdfName);
        } catch (Exception e) {
            e.printStackTrace();
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

    void generateImageFromPdf(Uri pdfUri) {
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
            saveImage(bmp);
            pdfiumCore.closeDocument(pdfDocument); // important!
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveImage(Bitmap bmp) {
        String thumbnail = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Qoogol/Thumbnails/";
        FileOutputStream out = null;
        try {
            File folder = new File(thumbnail);
            if (!folder.exists())
                folder.mkdirs();
            File file = new File(folder, System.currentTimeMillis() + ".png");
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
}
