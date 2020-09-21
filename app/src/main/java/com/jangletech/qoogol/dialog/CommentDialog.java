package com.jangletech.qoogol.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.CommentAdapter;
import com.jangletech.qoogol.databinding.CommentDialogBinding;
import com.jangletech.qoogol.enums.Module;
import com.jangletech.qoogol.model.Comments;
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.model.VerifyResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.AESSecurities;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
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
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentDialog extends Dialog implements
        CommentAdapter.onCommentItemClickListener,
        RepliesDialog.ReplyClickListener {

    private static final String TAG = "CommentDialog";
    private CommentDialogBinding mBinding;
    private Activity mContext;
    private List<Comments> commentList;
    private CommentAdapter commentAdapter;
    private int id;
    private Call<VerifyResponse> sendGifCall;
    private int itemPosition;
    public static int addCommentCount = 0;
    private Call<ProcessQuestion> call;
    private boolean isCallFromTest;
    private int selectedPos = -1;
    private Comments selectedComment;
    private CommentClickListener commentClickListener;
    private ApiInterface apiService = ApiClient.getInstance().getApi();


    public CommentDialog(@NonNull Activity mContext, int id, boolean isCallFromTest, CommentClickListener commentClickListener) {
        super(mContext, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.mContext = mContext;
        this.id = id;
        this.isCallFromTest = isCallFromTest;
        this.commentClickListener = commentClickListener;
        addCommentCount = 0;
        Log.d(TAG, "CommentDialog ID: " + id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.comment_dialog, null, false);
        setContentView(mBinding.getRoot());
        commentList = new ArrayList<>();
        setCommentAdapter();
        fetchCommentsAPI(Integer.parseInt(AppUtils.getUserId()),
                id, "L", "", "", "", "");

        mBinding.btnClose.setOnClickListener(v -> {
            commentClickListener.onBackClick(addCommentCount);
            dismiss();
        });

        mBinding.etComment.setKeyBoardInputCallbackListener((inputContentInfo, flags, opts) -> {

            Dexter.withActivity(mContext)
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
                                File imageFile = AppUtils.createStickerGifyFile(mContext, extension);
                                if (!imageFile.exists()) {
                                    imageFile.createNewFile();
                                }

                                final InputStream imageStream = mContext.getContentResolver().openInputStream(inputContentInfo.getContentUri());
                                //final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                                AppUtils.readFully(imageStream,imageFile);
                                //ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                /*selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                                byte[] byteArray = byteArrayOutputStream.toByteArray();
                                fos = new FileOutputStream(imageFile);
                                fos.write(byteArray);
                                fos.flush();
                                fos.close();*/
                                sendGifComment(imageFile);
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

        setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.d(TAG, "onKey Executed : ");
                    commentClickListener.onBackClick(addCommentCount);
                    dismiss();
                }
                return true;
            }
        });

        mBinding.btnSend.setOnClickListener(v -> {
            if (mBinding.etComment.getText().toString().isEmpty()) {
                Toast.makeText(mContext, "Please enter comment", Toast.LENGTH_SHORT).show();
                return;
            }
            fetchCommentsAPI(Integer.parseInt(AppUtils.getUserId()),
                    id, "I", mBinding.etComment.getText().toString(), "", "", "");
        });
    }

    private void sendGifComment(File imageFile) {
        ProgressDialog.getInstance().show(mContext);
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("image/png"), imageFile);

        RequestBody userId =
                RequestBody.create(MediaType.parse("multipart/form-data"), AppUtils.getUserId());
        RequestBody tmId =
                RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(id));
        RequestBody strCase =
                RequestBody.create(MediaType.parse("multipart/form-data"), "I");

        Log.d(TAG, "Profile Image Size: " + imageFile.getTotalSpace());
        Log.d(TAG, "updateProfileImage Name : " + imageFile.getName());

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("Files", imageFile.getName(), requestFile);

        if (isCallFromTest)
            sendGifCall = apiService.sendGifComment(userId, tmId, strCase, body);
        else
            sendGifCall = apiService.sendQuestGifComment(userId, tmId, strCase, body);

        sendGifCall.enqueue(new Callback<VerifyResponse>() {
            @Override
            public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    Log.d(TAG, "onResponse Success: ");
                    addCommentCount++;
                    fetchCommentsAPI(Integer.parseInt(AppUtils.getUserId()),
                            id, "L", "", "", "", "");
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

    private void fetchCommentsAPI(int user_id, int que_id, String api_case, String comment_text,
                                  String commentFlag, String likeFlag, String replyId) {
        ProgressDialog.getInstance().show(mContext);
        mBinding.emptytv.setText("Fetching Comments...");
        mBinding.emptytv.setVisibility(View.VISIBLE);
        Log.d(TAG, "fetchCommentsAPI userId : " + user_id);
        Log.d(TAG, "fetchCommentsAPI Case : " + api_case);

        try {
            if (isCallFromTest) {
                if (api_case.equalsIgnoreCase("L"))
                    call = apiService.fetchTestComments(user_id, que_id, api_case, 1);
                else
                    call = apiService.addTestCommentApi(user_id, que_id, "I", AppUtils.encodedString(comment_text));
            } else {
                if (api_case.equalsIgnoreCase("L"))
                    call = apiService.fetchComments(user_id, que_id, api_case, 1);
                else
                    call = apiService.addQuestCommentApi(user_id, que_id, "I", AppUtils.encodedString(comment_text), "1");
            }

            call.enqueue(new Callback<ProcessQuestion>() {
                @Override
                public void onResponse(Call<ProcessQuestion> call, retrofit2.Response<ProcessQuestion> response) {
                    try {
                        ProgressDialog.getInstance().dismiss();
                        mBinding.emptytv.setVisibility(View.GONE);
                        commentList.clear();
                        if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                            List<Comments> newCommentList = response.body().getCommentList();
                            for (Comments comments : newCommentList) {
                                comments.setUserFirstName(AESSecurities.getInstance().decrypt(TinyDB.getInstance(mContext).getString(Constant.cf_key1), comments.getUserFirstName()));
                                comments.setUserLastName(AESSecurities.getInstance().decrypt(TinyDB.getInstance(mContext).getString(Constant.cf_key2), comments.getUserLastName()));
                                commentList.add(comments);
                            }
                            if (api_case.equalsIgnoreCase("I")) {
                                addCommentCount++;
                            }
                            emptyView();
                        } else {
                            AppUtils.showToast(mContext, null, response.body().getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mBinding.emptytv.setVisibility(View.GONE);
                        mBinding.shimmerViewContainer.hideShimmer();
                        ProgressDialog.getInstance().dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ProcessQuestion> call, Throwable t) {
                    t.printStackTrace();
                    mBinding.shimmerViewContainer.hideShimmer();
                    ProgressDialog.getInstance().dismiss();
                    AppUtils.showToast(mContext, t, "");
                    dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            mBinding.shimmerViewContainer.hideShimmer();
            ProgressDialog.getInstance().dismiss();
        }
    }

    private void setCommentAdapter() {
        if (isCallFromTest)
            commentAdapter = new CommentAdapter(mContext, commentList, Module.Test.toString(), "", false, this);
        else
            commentAdapter = new CommentAdapter(mContext, commentList, Module.Learning.toString(), "", false, this);

        mBinding.commentRecycler.setHasFixedSize(true);
        mBinding.commentRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.commentRecycler.setAdapter(commentAdapter);
        mBinding.shimmerViewContainer.hideShimmer();
    }

    private void emptyView() {
        setCommentAdapter();
        mBinding.etComment.setText("");
        if (commentList.size() == 0) {
            mBinding.emptytv.setText("No Comments added yet.");
            mBinding.emptytv.setVisibility(View.VISIBLE);
        } else {
            mBinding.emptytv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onReplyBackClick(int count) {
        if (selectedComment != null) {
            if (isCallFromTest) {
                int prevCount = selectedComment.getReplyCommentCount() + count;
                selectedComment.setReplyCommentCount(prevCount);
                commentAdapter.notifyItemChanged(selectedPos, selectedComment);
            } else {
                //Question case
                int prevCount = selectedComment.getQuestCommentCount() + count;
                selectedComment.setQuestCommentCount(prevCount);
                commentAdapter.notifyItemChanged(selectedPos, selectedComment);
            }
        }
    }

    public interface CommentClickListener {
        void onCommentClick(String userId);

        void onBackClick(int count);
    }

    @Override
    public void onItemClick(String userId) {
        commentClickListener.onCommentClick(userId);
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
        itemPosition = pos;
        selectedPos = pos;
        selectedComment = comments;
        new RepliesDialog(mContext, mContext, comments, id, isCallFromTest, this).show();
    }

    @Override
    public void onLikeClick(int pos, Comments comments) {
        itemPosition = pos;
        if (isCallFromTest)
            likeReplyComment("I", comments.getTlc_id());
        else
            likeReplyComment("I", comments.getCommentId());
    }

    @Override
    public void onReplyClick(int pos, Comments comments) {
        itemPosition = pos;
        new RepliesDialog(mContext, mContext, comments, id, isCallFromTest, this).show();
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
                    commentAdapter.deleteComment(pos);
                    addCommentCount--;
                } else {
                    AppUtils.showToast(mContext, null, response.body().getMessage());
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

    private void likeReplyComment(String strCase,
                                  String commentId) {
        ProgressDialog.getInstance().show(mContext);

        Log.d(TAG, "likeReplyComment isCallFromTest : " + isCallFromTest);
        Log.d(TAG, "likeReplyComment UserId : " + AppUtils.getUserId());
        Log.d(TAG, "likeReplyComment TestQuestId : " + id);
        Log.d(TAG, "likeReplyComment strCase : " + strCase);
        Log.d(TAG, "likeReplyComment strCase : " + strCase);
        Log.d(TAG, "likeReplyComment commentId : " + commentId);

        if (isCallFromTest)
            call = apiService.likeTestComment(
                    AppUtils.getUserId(),
                    id,
                    strCase,
                    "1",
                    commentId
            );
        else
            call = apiService.likeReplyQuestComment(
                    AppUtils.getUserId(),
                    id,
                    strCase,
                    "1",
                    commentId
            );

        call.enqueue(new Callback<ProcessQuestion>() {
            @Override
            public void onResponse(Call<ProcessQuestion> call, Response<ProcessQuestion> response) {
                ProgressDialog.getInstance().dismiss();

                if (response.body() != null &&
                        response.body().getResponse().equals("200")) {
                    try {
                        updateCommentList(commentList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    AppUtils.showToast(mContext, response.body().getResponse());
                }
            }

            @Override
            public void onFailure(Call<ProcessQuestion> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                AppUtils.showToast(mContext, t, null);
                t.printStackTrace();
            }
        });
    }


    private void updateCommentList(List<Comments> list) {
        Comments comments = list.get(itemPosition);
        if (isCallFromTest) {
            int preLikeCount = comments.getReplyLikeCount();
            if (comments.isLiked()) {
                comments.setLiked(false);
                comments.setReplyLikeCount(preLikeCount - 1);
            } else {
                comments.setLiked(true);
                comments.setReplyLikeCount(preLikeCount + 1);
            }
        } else {
            int preLikeCount = comments.getQuestLikeCount();
            if (comments.isLiked()) {
                comments.setLiked(false);
                comments.setQuestLikeCount(preLikeCount - 1);
            } else {
                comments.setLiked(true);
                comments.setQuestLikeCount(preLikeCount + 1);
            }
        }
        commentAdapter.notifyItemChanged(itemPosition, comments);
    }


}
