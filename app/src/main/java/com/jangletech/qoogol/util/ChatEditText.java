package com.jangletech.qoogol.util;

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

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;

/**
 * Created by Jitendra Yadav on 3/9/2020.
 */
public class ChatEditText extends AppCompatEditText {

    private String[] mimeTypes;
    private KeyBoardInputCallbackListener keyBoardInputCallbackListener;
    final InputConnectionCompat.OnCommitContentListener callback =
            new InputConnectionCompat.OnCommitContentListener() {
                @Override
                public boolean onCommitContent(InputContentInfoCompat inputContentInfo,
                                               int flags, Bundle opts) {

                    // read and display inputContentInfo asynchronously
                    if (Build.VERSION.SDK_INT >= 25 && (flags &
                            InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0) {
                        try {
                            inputContentInfo.requestPermission();
                        } catch (Exception e) {
                            return false; // return false if failed
                        }
                    }
                    boolean supported = false;
                    for (final String mimeType : mimeTypes) {
                        if (inputContentInfo.getDescription().hasMimeType(mimeType)) {
                            supported = true;
                            break;
                        }
                    }
                    if (!supported) {
                        return false;
                    }

                    if (keyBoardInputCallbackListener != null) {
                        keyBoardInputCallbackListener.onCommitContent(inputContentInfo, flags, opts);
                    }
                    return true;  // return true if succeeded
                }
            };

    public ChatEditText(Context context) {
        super(context);
        initView();
    }

    public ChatEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mimeTypes = new String[]{"image/png", "image/gif", "image/jpeg", "image/webp"};
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        final InputConnection ic = super.onCreateInputConnection(editorInfo);
        EditorInfoCompat.setContentMimeTypes(editorInfo,
                mimeTypes);
        return InputConnectionCompat.createWrapper(ic, editorInfo, callback);
    }

    public void setKeyBoardInputCallbackListener(KeyBoardInputCallbackListener keyBoardInputCallbackListener) {
        this.keyBoardInputCallbackListener = keyBoardInputCallbackListener;
    }

    public String[] getImgTypeString() {
        return mimeTypes;
    }

    public void setImgTypeString(String[] imgTypeString) {
        this.mimeTypes = imgTypeString;
    }

    public interface KeyBoardInputCallbackListener {
        void onCommitContent(InputContentInfoCompat inputContentInfo,
                             int flags, Bundle opts);
    }
}
