package com.jangletech.qoogol.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.jangletech.qoogol.R;

/***
 * Progress Dialog Singleton
 */
public class ProgressDialog {

    private Dialog dialog;
    private static final ProgressDialog instance = new ProgressDialog();

    public static ProgressDialog getInstance() {
        return instance;
    }

    private ProgressDialog() {

    }

    public void show(Context context) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
