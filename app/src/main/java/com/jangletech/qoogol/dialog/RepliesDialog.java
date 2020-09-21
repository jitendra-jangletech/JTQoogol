package com.jangletech.qoogol.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.CommentAdapter;
import com.jangletech.qoogol.databinding.DialogCommentNewBinding;
import com.jangletech.qoogol.enums.Module;
import com.jangletech.qoogol.model.Comments;
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.model.VerifyResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.AESSecurities;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.DateUtils;
import com.jangletech.qoogol.util.TinyDB;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepliesDialog extends Dialog implements CommentAdapter.onCommentItemClickListener {
    private static final String TAG = "RepliesDialog";
    private Context mContext;
    private Activity activity;
    private Call<VerifyResponse> sendGifCall;
    private DialogCommentNewBinding mBinding;
    private Comments comments;
    private boolean isCallFromTest;
    private Call<ProcessQuestion> call;
    private int id;
    private HashMap<String, String> params = new HashMap<>();
    private CommentAdapter mAdapter;
    private String commentId = "";
    private List<Comments> commentList;
    private ReplyClickListener replyClickListener;
    private int replyCount = 0;
    private ApiInterface apiService = ApiClient.getInstance().getApi();

    public RepliesDialog(@NonNull Context mContext, Activity activity,
                         Comments comments, int id, boolean isCallFromTest,
                         ReplyClickListener replyClickListener) {
        super(mContext, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.mContext = mContext;
        this.activity = activity;
        this.comments = comments;
        this.id = id;
        this.isCallFromTest = isCallFromTest;
        this.replyClickListener = replyClickListener;
        replyCount = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_comment_new, null, false);
        setContentView(mBinding.getRoot());
        commentList = new ArrayList<Comments>();

        mBinding.etComment.setKeyBoardInputCallbackListener((inputContentInfo, flags, opts) -> {
            Dexter.withActivity(activity)
                    .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            try {
                                Log.i(TAG, inputContentInfo.getContentUri().toString());
                                FileOutputStream fos = null;
                                String filePath = inputContentInfo.getContentUri().getPath();
                                String extension = filePath.substring(filePath.lastIndexOf(".") + 1);
                                Log.i(TAG, "selected file path: " + filePath);
                                File imageFile = AppUtils.createStickerGifyFile(activity, extension);
                                if (!imageFile.exists()) {
                                    imageFile.createNewFile();
                                }

                                final InputStream imageStream = mContext.getContentResolver().openInputStream(inputContentInfo.getContentUri());
                                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                                byte[] byteArray = byteArrayOutputStream.toByteArray();
                                fos = new FileOutputStream(imageFile);
                                fos.write(byteArray);
                                fos.flush();
                                fos.close();
                                if (isCallFromTest)
                                    sendGifComment(imageFile, comments.getTlc_id());
                                else
                                    sendGifComment(imageFile, comments.getCommentId());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            Toast.makeText(mContext, "Storage permission denied.", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    })
                    .withErrorListener(error ->
                            Toast.makeText(mContext, "Error occurred! ", Toast.LENGTH_SHORT).show())
                    .onSameThread()
                    .check();


        });

        if (isCallFromTest) {
            likeReplyComment("L", comments.getTlc_id(), "");
            mAdapter = new CommentAdapter(activity, commentList, Module.Test.toString(), String.valueOf(id), true, this);
            mBinding.textCommentBody.setText(AppUtils.decodedString(comments.getTlc_comment_text()));
            mBinding.textCommentTime.setText(DateUtils.localeDateFormat(comments.getTlc_cdatetime()));
        } else {
            likeReplyComment("L", comments.getCommentId(), "");
            mAdapter = new CommentAdapter(activity, commentList, Module.Learning.toString(), String.valueOf(id), true, this);
            mBinding.textCommentBody.setText(AppUtils.decodedString(comments.getComment()));
            mBinding.textCommentTime.setText(DateUtils.localeDateFormat(comments.getTime()));
        }
        mBinding.commentRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        mBinding.commentRecycler.setAdapter(mAdapter);

        mBinding.tvSenderName.setText(comments.getUserFirstName() + " " + comments.getUserLastName());

        Glide.with(mContext)
                .load(getProfileImageUrl(comments))
                .apply(RequestOptions.circleCropTransform())
                .into(mBinding.profilePic);

        mBinding.btnClose.setOnClickListener(v -> {
            replyClickListener.onReplyBackClick(replyCount);
            dismiss();
        });

        mBinding.btnSend.setOnClickListener(v -> {
            if (mBinding.etComment.getText().toString().isEmpty()) {
                Toast.makeText(mContext, "Please enter comment", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(TAG, "Replied To Id : " + comments.getCommentId());
            if (isCallFromTest)
                likeReplyComment("I", comments.getTlc_id(), AppUtils.encodedString(mBinding.etComment.getText().toString().trim()));
            else
                likeReplyComment("I", comments.getCommentId(), AppUtils.encodedString(mBinding.etComment.getText().toString().trim()));

        });
    }


    private String getProfileImageUrl(Comments comments) {
        return Constant.PRODUCTION_BASE_FILE_API + comments.getProfile_image();
    }

    @Override
    public void onItemClick(String userId) {

    }

    @Override
    public void onCommentDelete(int pos, Comments comments) {
        if (isCallFromTest)
            deleteComment(comments.getTlc_id(), pos);
        else
            deleteComment(comments.getCommentId(), pos);
    }

    @Override
    public void onCommentsClick(int pos, Comments comments) {

    }

    @Override
    public void onLikeClick(int pos, Comments comments) {

    }

    @Override
    public void onReplyClick(int pos, Comments comments) {

    }

    private void deleteComment(String commentId, int pos) {
        ProgressDialog.getInstance().show(mContext);

        if (isCallFromTest)
            call = apiService.deleteTestComment(
                    AppUtils.getUserId(),
                    id,
                    "D",
                    "1",
                    commentId
            );
        else
            call = apiService.deleteQuestComment(
                    AppUtils.getUserId(),
                    id,
                    "D",
                    "1",
                    commentId
            );

        call.enqueue(new Callback<ProcessQuestion>() {
            @Override
            public void onResponse(Call<ProcessQuestion> call, Response<ProcessQuestion> response) {
                ProgressDialog.getInstance().dismiss();

                if (response.body() != null &&
                        response.body().getResponse().equals("200")) {
                    mAdapter.deleteComment(pos);
                    replyCount--;
                    //CommentDialog.addCommentCount--;
                } else {
                    AppUtils.showToast(mContext, null, response.body().getResponse());
                }
            }

            @Override
            public void onFailure(Call<ProcessQuestion> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                AppUtils.showToast(mContext, t, "");
                t.printStackTrace();
            }
        });
    }


    private void setCommentAdapter() {
        mBinding.etComment.setText("");
        mBinding.btnSend.setClickable(true);
        if (commentList.size() > 0) {
            mBinding.tvNoReplies.setVisibility(View.GONE);
            mAdapter.updateList(commentList);
        } else {
            //no replies added
            mBinding.tvNoReplies.setText("No Replies Added Yet.");
            mBinding.tvNoReplies.setVisibility(View.VISIBLE);
        }
    }

    private void likeReplyComment(String strCase,
                                  String commentId, String text) {
        ProgressDialog.getInstance().show(mContext);
        mBinding.btnSend.setClickable(false);
        mBinding.tvNoReplies.setText("Fetching Replies...");
        mBinding.tvNoReplies.setVisibility(View.VISIBLE);

        Log.d(TAG, "likeReplyComment u_user_id : " + AppUtils.getUserId());
        Log.d(TAG, "likeReplyComment tm_id : " + id);
        Log.d(TAG, "likeReplyComment strCase : " + strCase);
        Log.d(TAG, "likeReplyComment commentId : " + commentId);
        Log.d(TAG, "likeReplyComment text : " + text);

        if (isCallFromTest)
            call = apiService.replyTestComment(
                    AppUtils.getUserId(),
                    id,
                    strCase,
                    "1",
                    commentId,
                    text);
        else
            call = apiService.replyQuestComment(
                    AppUtils.getUserId(),
                    id,
                    strCase,
                    "1",
                    commentId,
                    text);

        call.enqueue(new Callback<ProcessQuestion>() {
            @Override
            public void onResponse(Call<ProcessQuestion> call, Response<ProcessQuestion> response) {
                ProgressDialog.getInstance().dismiss();
                mBinding.tvNoReplies.setVisibility(View.GONE);
                if (response.body() != null &&
                        response.body().getResponse().equals("200")) {
                    List<Comments> newCommentList = response.body().getCommentList();
                    commentList.clear();
                    for (Comments comments : newCommentList) {
                        comments.setUserFirstName(AESSecurities.getInstance().decrypt(TinyDB.getInstance(activity).getString(Constant.cf_key1), comments.getUserFirstName()));
                        comments.setUserLastName(AESSecurities.getInstance().decrypt(TinyDB.getInstance(activity).getString(Constant.cf_key2), comments.getUserLastName()));
                        commentList.add(comments);
                    }

                    if (strCase.equalsIgnoreCase("L")) {
                        setCommentAdapter();
                    } else {
                        replyCount++;
                        //CommentDialog.addCommentCount++;
                        if (isCallFromTest)
                            likeReplyComment("L", comments.getTlc_id(), "");
                        else
                            likeReplyComment("L", comments.getCommentId(), "");
                    }

                } else {
                    mBinding.btnSend.setClickable(true);
                    AppUtils.showToast(mContext, null, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<ProcessQuestion> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                mBinding.etComment.setText("");
                mBinding.btnSend.setClickable(true);
                mBinding.tvNoReplies.setVisibility(View.GONE);
                AppUtils.showToast(mContext, t, "");
                t.printStackTrace();
            }
        });
    }

    private void sendGifComment(File imageFile, String replyId) {
        ProgressDialog.getInstance().show(mContext);
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("image/png"), imageFile);

        RequestBody userId =
                RequestBody.create(MediaType.parse("multipart/form-data"), AppUtils.getUserId());
        RequestBody tmId =
                RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(id));
        RequestBody strCase =
                RequestBody.create(MediaType.parse("multipart/form-data"), "I");

        RequestBody rId =
                RequestBody.create(MediaType.parse("multipart/form-data"), replyId);

        Log.d(TAG, "sendGifComment Reply Id : "+replyId);
        Log.d(TAG, "Profile Image Size: " + imageFile.getTotalSpace());
        Log.d(TAG, "updateProfileImage Name : " + imageFile.getName());

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("Files", imageFile.getName(), requestFile);

        if (isCallFromTest)
            sendGifCall = apiService.sendReplyGifComment(userId, tmId, strCase,rId, body);
        else
            sendGifCall = apiService.sendReplyQuestGifComment(userId, tmId, strCase,rId, body);

        sendGifCall.enqueue(new Callback<VerifyResponse>() {
            @Override
            public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    Log.d(TAG, "onResponse Success: ");
                    replyCount++;
                    if (isCallFromTest)
                        likeReplyComment("L", comments.getTlc_id(), "");
                    else
                        likeReplyComment("L", comments.getCommentId(), "");

                } else {
                    AppUtils.showToast(mContext, null, response.body().getErrorMsg());
                }
            }

            @Override
            public void onFailure(Call<VerifyResponse> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
                AppUtils.showToast(mContext, t, "");
            }
        });
    }

    @Override
    public void onBackPressed() {
        replyClickListener.onReplyBackClick(replyCount);
        dismiss();
    }

    public interface ReplyClickListener {
        void onReplyBackClick(int count);
    }
}
