package com.jangletech.qoogol.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.FriendsAdapter;
import com.jangletech.qoogol.databinding.ActivityTestingBinding;
import com.jangletech.qoogol.model.Friends;
import com.jangletech.qoogol.model.ShareModel;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.connections.FriendsViewModel;
import com.jangletech.qoogol.util.AppUtils;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class TestingActivity extends AppCompatActivity {

    private static final String TAG = "TestingActivity";
    private ActivityTestingBinding mBinding;
    private String encoded = "";
    private LinearLayoutManager layoutManager;
    private FriendsAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<Friends> connectionsList = new ArrayList<>();
    private List<Friends> filteredList = new ArrayList<>();
    private List<String> strings;
    private List<ShareModel> shareModelList;
    private Boolean isScrolling = false;
    private int currentItems, scrolledOutItems, totalItems;
    private int pageCount = 0;
    private List<String> paths = new ArrayList<>();
    private boolean isSearching = false;
    private List<ShareModel> filteredModelList;
    private static final int PICK_FILE_PDF = 234;
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private FriendsViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_testing);
        mViewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);

       /* FilePickerBuilder.getInstance().setMaxCount(1)
                .pickFile(this);*/

        /*Intent intentPDF = new Intent(Intent.ACTION_GET_CONTENT);
        intentPDF.setType("application/pdf");
        intentPDF.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intentPDF, PICK_FILE_PDF);*/

        mBinding.btnGetPdf.setOnClickListener(v -> {
            Intent intent4 = new Intent(this, NormalFilePickActivity.class);
            intent4.putExtra(Constant.MAX_NUMBER, 9);
            intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"pdf"});
            startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
        });

        mBinding.btnShowImages.setOnClickListener(v -> {
//            new AddImageDialog(this)
//                    .show();
        });


    }

    private void readPdf(String path) {
        try {
            File file = new File(path);
            PDDocument document = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(document);

            //Rendering an image from the PDF document
            Bitmap image = renderer.renderImage(0);

            //Closing the document
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
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

                    if (type != null && (type.toString().equals(PdfName.IMAGE.toString()) ||
                            type.toString().equals(PdfName.IMAGEB.toString()) ||
                            type.toString().equals(PdfName.IMAGEC.toString()) ||
                            type.toString().equals(PdfName.IMAGEI.toString()))) {

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

            mBinding.tvNoOfImages.setText("Images Found : " + imageCount);
            Log.i(TAG, "extractImages Total Image Count : " + imageCount);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
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
                        //builder.append(path + "\n");
                        extractImages(path);
                    }
                }
                break;

            case PICK_FILE_PDF:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Log.i(TAG, "onActivityResult : " + data.getData().getPath());
                    try {
                        String filePath = data.getData().getPath();
                        String extension = filePath.substring(filePath.lastIndexOf(".") + 1);
                        Log.i(TAG, "selected file path: " + filePath);
                        File imageFile = AppUtils.createStickerGifyFile(this, extension);
                        if (!imageFile.exists()) {
                            imageFile.createNewFile();
                        }
                        final InputStream imageStream = getContentResolver().openInputStream(data.getData());
                        AppUtils.readFully(imageStream, imageFile);
                        extractImages(imageFile.getPath());
                    } catch (Exception e) {
                        Log.e(TAG, "onActivityResult Error : ");
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}

        /*linearLayoutManager = new LinearLayoutManager(this);
        mAdapter = new FriendsAdapter(this, connectionsList, friends, this);
        mBinding.connectionRecycler.setHasFixedSize(true);
        mBinding.connectionRecycler.setLayoutManager(linearLayoutManager);
        mBinding.connectionRecycler.setAdapter(mAdapter);*/
       /* mViewModel.fetchFriendsData(false);

        mViewModel.getFriendList().observe(this, friendsList -> {
            if (friendsList != null) {
                mAdapter.updateList(friendsList);
                //setFriendsList(friendsList);
            }
        });
    }*/

    /*private void setFriendsList(List<Friends> friendsList) {
        if (friendsList.size() > 0) {
            mBinding.emptyview.setVisibility(View.GONE);
            mAdapter.updateList(friendsList);
        } else {
            mBinding.emptyview.setText("No Friends Added.");
            mBinding.emptyview.setVisibility(View.VISIBLE);
        }
    }*/

    /*@Override
    public void onUpdateConnection(String user) {

    }

    @Override
    public void showProfileClick(Bundle bundle) {

    }*/