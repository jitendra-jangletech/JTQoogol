package com.jangletech.qoogol.videocompressions;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.DialogProcessFileBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

/*
 *
 *
 *  * /////////////////////////////////////////////////////////////////////////////////////////////////
 *  * //
 *  * //            Copyright (c) 2020. JangleTech Systems Private Limited, Thane, India
 *  * //
 *  * /////////////////////////////////////////////////////////////////////////////////////////////////
 *
 */

public class DialogProcessFile extends Dialog {

    private static final String TAG = "DialogProcessFile";
    private DialogProcessFileHandler dialogProcessFileHandler;
    private Context c;
    private Uri uri;
    private Uri originalPath;
    private DialogProcessFileBinding binding;
    private int quality;

    public DialogProcessFile(@NonNull Context context, Uri uri, int quality, DialogProcessFileHandler dialogProcessFileHandler) {
        super(context, R.style.DialogTheme);
        this.dialogProcessFileHandler = dialogProcessFileHandler;
        this.c = context;
        this.uri = uri;
        originalPath = uri;
        this.quality = quality;
        this.show();
    }

    public DialogProcessFile(@NonNull Context context, Bitmap originalBitmap, DialogProcessFileHandler dialogProcessFileHandler) {
        super(context, R.style.DialogTheme);
        this.dialogProcessFileHandler = dialogProcessFileHandler;
        this.c = context;
//        this.uri = createFileFromBitmap(originalBitmap);
        this.show();
    }

    public DialogProcessFile(@NonNull Context context, File file, DialogProcessFileHandler dialogProcessFileHandler) {
        super(context, R.style.DialogTheme);
        this.dialogProcessFileHandler = dialogProcessFileHandler;
        this.c = context;
        this.uri = Uri.fromFile(file);
        this.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_process_file, null, false);
        setContentView(binding.getRoot());
        this.setCancelable(false);
        // if (isImage(uri)) reduceBitmap();
//        if (quality == 1) {
//            reduceVideo();
//        } else if (quality == 2) {
//            reduceVideoMedium();
//        } else if (quality == 3) {
//            reduceVideoHigh();
//        }

        binding.btnCancel.setOnClickListener(view -> {

        });
    }

//    private Uri createFileFromBitmap(Bitmap originalBitmap) {
//        try {
//            File temp = newScrapFile("jpeg", c);
//            originalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, new FileOutputStream(temp));
//            return Uri.fromFile(temp);
//        } catch (Exception e) {
//            // throwCrashFirebase(e);
//            return null;
//        }
//    }

//    //Video Handling
//    private void reduceVideo() {
//        File temp = newScrapFile("mp4", c);
//        if (temp != null) {
//            try {
//                VideoCompress.compressVideoLow(getFilePath(), temp.getAbsolutePath(), new VideoCompress.CompressListener() {
//                    @Override
//                    public void onStart() {
//                        //started compressing
//                    }
//
//                    @Override
//                    public void onSuccess() {
//                        LOG.i(TAG, "onSuccess()");
//                        uri = Uri.fromFile(temp);
//                        dialogProcessFileHandler.status(uri, originalPath, true);
//                        DialogProcessFile.this.dismiss();
//                        //compress success
//                    }
//
//                    @Override
//                    public void onFail() {
//                        LOG.i(TAG, "onFail()");
//                        dialogProcessFileHandler.status(uri, originalPath, false);
//                        DialogProcessFile.this.dismiss();
//                        //Compress Failed!
//                    }
//
//                    @Override
//                    public void onProgress(float percent) {
//                        binding.progress.setProgress((int) percent);
//                        //percent
//                    }
//                });
//            } catch (URISyntaxException e) {
//                // throwCrashFirebase(e);
//                dialogProcessFileHandler.status(uri, originalPath, false);
//                DialogProcessFile.this.dismiss();
//            }
//        }
//    }
//
//    //Video Handling compressVideoMedium
//    private void reduceVideoMedium() {
//        File temp = newScrapFile("mp4", c);
//        if (temp != null) {
//            try {
//                VideoCompress.compressVideoMedium(getFilePath(), temp.getAbsolutePath(), new VideoCompress.CompressListener() {
//                    @Override
//                    public void onStart() {
//                        //started compressing
//                    }
//
//                    @Override
//                    public void onSuccess() {
//                        LOG.i(TAG, "onSuccess()");
//                        uri = Uri.fromFile(temp);
//                        dialogProcessFileHandler.status(uri, originalPath, true);
//                        DialogProcessFile.this.dismiss();
//                        //compress success
//                    }
//
//                    @Override
//                    public void onFail() {
//                        LOG.i(TAG, "onFail()");
//                        dialogProcessFileHandler.status(uri, originalPath, false);
//                        DialogProcessFile.this.dismiss();
//                        //Compress Failed!
//                    }
//
//                    @Override
//                    public void onProgress(float percent) {
//                        binding.progress.setProgress((int) percent);
//                        //percent
//                    }
//                });
//            } catch (URISyntaxException e) {
//                // throwCrashFirebase(e);
//                dialogProcessFileHandler.status(uri, originalPath, false);
//                DialogProcessFile.this.dismiss();
//            }
//        }
//    }
//
//    //Video Handling compressVideoMedium
//    private void reduceVideoHigh() {
//        File temp = newScrapFile("mp4", c);
//        if (temp != null) {
//            try {
//                VideoCompress.compressVideoHigh(getFilePath(), temp.getAbsolutePath(), new VideoCompress.CompressListener() {
//                    @Override
//                    public void onStart() {
//                        //started compressing
//                    }
//
//                    @Override
//                    public void onSuccess() {
//                        LOG.i(TAG, "onSuccess()");
//                        uri = Uri.fromFile(temp);
//                        dialogProcessFileHandler.status(uri, originalPath, true);
//                        DialogProcessFile.this.dismiss();
//                        //compress success
//                    }
//
//                    @Override
//                    public void onFail() {
//                        LOG.i(TAG, "onFail()");
//                        dialogProcessFileHandler.status(uri, originalPath, false);
//                        DialogProcessFile.this.dismiss();
//                        //Compress Failed!
//                    }
//
//                    @Override
//                    public void onProgress(float percent) {
//                        binding.progress.setProgress((int) percent);
//                        //percent
//                    }
//                });
//            } catch (URISyntaxException e) {
//                // throwCrashFirebase(e);
//                dialogProcessFileHandler.status(uri, originalPath, false);
//                DialogProcessFile.this.dismiss();
//            }
//        }
//    }
//
//    private static File newScrapFile(String extension, Context c) {
//        File tempDir = UtilHelper.getDirectory(c);
//        tempDir = new File(tempDir.getAbsolutePath() + "/.temp/");
//        if (!tempDir.exists()) {
//            tempDir.mkdirs();
//        }
//        try {
//            return File.createTempFile("file", "." + extension, tempDir);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    private String getFilePath() throws URISyntaxException {
        try {
            String selection = null;
            String[] selectionArgs = null;
            // Uri is different in versions after KITKAT (Android 4.4), we need to
            if (DocumentsContract.isDocumentUri(c.getApplicationContext(), uri)) {
                if (isExternalStorageDocument()) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                } else if (isDownloadsDocument()) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    uri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                } else if (isMediaDocument()) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("image".equals(type)) {
                        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    selection = "_id=?";
                    selectionArgs = new String[]{
                            split[1]
                    };
                }
            }
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                String[] projection = {
                        MediaStore.Images.Media.DATA
                };
                Cursor cursor = null;
                try {
                    cursor = c.getContentResolver()
                            .query(uri, projection, selection, selectionArgs, null);
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        } catch (Exception ex) {
            dialogProcessFileHandler.status(uri, originalPath, false);
            DialogProcessFile.this.dismiss();
        }
        return null;
    }

    private boolean isExternalStorageDocument() {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private boolean isDownloadsDocument() {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private boolean isMediaDocument() {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

   /* //Image Reduction
    private void reduceBitmap() {
        try {
            Bitmap originalBitmap = new Compressor(c)
                    .setMaxWidth(640)
                    .setMaxHeight(480)
                    .setQuality(60)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .compressToBitmap(new File(uri.getPath()));
            File temp = newScrapFile("jpeg", c);
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, new FileOutputStream(temp));
            uri = Uri.fromFile(temp);
            dialogProcessFileHandler.status(uri, PathUtil.getPath(c, uri), true);
            new Handler().postDelayed(DialogProcessFile.this::dismiss, 100);
        } catch (Exception e) {
            // throwCrashFirebase(e);
            dialogProcessFileHandler.status(uri, PathUtil.getPath(c, uri), false);
            new Handler().postDelayed(DialogProcessFile.this::dismiss, 100);
        }
    }*/

    public interface DialogProcessFileHandler {
        void status(Uri processedFile, Uri processedFilePath, Boolean success);
    }
}
