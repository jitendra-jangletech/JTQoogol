package com.jangletech.qoogol.ui.create_pdf;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.CreatePdfAdapter;
import com.jangletech.qoogol.databinding.FragmentMyPdfBinding;
import com.jangletech.qoogol.ui.BaseFragment;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyPdfsFragment extends BaseFragment implements CreatePdfAdapter.CreatePdfClickListener {

    private static final String TAG = "MyPdfsFragment";
    private FragmentMyPdfBinding mBinding;
    private CreatePdfAdapter mAdapter;
    private List<Uri> images = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_pdf, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                setPdfSamplePdfAdapter();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();

    }

    private void setPdfSamplePdfAdapter() {
        images.clear();
        if (getAllFilesFromDirectory(finalPdfDocs) != null) {
            File[] files = getAllFilesFromDirectory(finalPdfDocs);
            for (File file : files) {
                Log.i(TAG, "setPdfSamplePdfAdapter: " + Uri.fromFile(new File(file.getAbsolutePath())));
                images.add(Uri.fromFile(new File(file.getAbsolutePath())));
            }
        }

        mAdapter = new CreatePdfAdapter(getActivity(), images, 0, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mBinding.myPdfRecyclerView.setHasFixedSize(true);
        mBinding.myPdfRecyclerView.setLayoutManager(linearLayoutManager);
        mBinding.myPdfRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onRemoveClick(Uri uri, int position) {
        Log.i(TAG, "onRemoveClick Position : " + position);
        mAdapter.deleteItem(position);
        deleteFile(uri.toString());
    }

    @Override
    public void onShareClick(Uri uri, int position) {
        Log.i(TAG, "onShareClick Uri : " + uri);
        Log.i(TAG, "onShareClick Position : " + position);
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.setType("pdf/*");
        startActivity(Intent.createChooser(sendIntent, "Share Pdf via..."));
    }

    @Override
    public void onItemClick(Uri uri, int position) {
        Log.i(TAG, "onShareClick Uri :` " + uri);
        Log.i(TAG, "onShareClick Position : " + position);
        showPdf(uri);
    }
}
