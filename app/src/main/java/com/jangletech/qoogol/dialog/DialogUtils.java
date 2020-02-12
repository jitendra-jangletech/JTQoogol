package com.jangletech.qoogol.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.jangletech.qoogol.R;


public class DialogUtils {

    private static AlertDialog mDialog;
    private static DialogInterface.OnClickListener mDefaultListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            mDialog = null;
        }
    };

    public static void dismissDialog() {
        try {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isDialogShowing() {
        try {
            if (mDialog != null) {
                return mDialog.isShowing();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void showOKAlert(Context context, String title, String msg, String btnLabel, DialogInterface.OnClickListener listener) {
        try {
            if (isDialogShowing()) {
                dismissDialog();
            }

            if (listener == null) {
                listener = mDefaultListener;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = setCustomView(context, title);
            builder.setCustomTitle(view);

            builder.setTitle(title)
                    .setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton(btnLabel, listener);

            mDialog = builder.create();
            mDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void showOKAlert(Context context, String title, String msg, DialogInterface.OnClickListener listener) {
        showOKAlert(context, title, msg, "Ok", listener);
    }

    public static void showOKAlert(Context context, String msg) {
        showOKAlert(context, null, msg, null);
    }


    public static void showYesNoAlert(Context context, String title, String msg,
                                      String yesLabel, String noLabel, DialogInterface.OnClickListener yesListener,
                                      DialogInterface.OnClickListener noListener) {
        try {
            if (isDialogShowing())
                return;

            if (yesListener == null)
                yesListener = mDefaultListener;
            if (noListener == null)
                noListener = mDefaultListener;

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = setCustomView(context, title);
            builder.setCustomTitle(view);
            builder.setTitle(title)
                    .setMessage(msg)
                    .setCancelable(false)
                    .setNegativeButton(noLabel, noListener)
                    .setPositiveButton(yesLabel, yesListener);

            mDialog = builder.create();
            mDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showYesNoAlert(Context context, String title, String msg, DialogInterface.OnClickListener yesListener, DialogInterface.OnClickListener noListener) {
        showYesNoAlert(context, title, msg, null, null, yesListener, noListener);
    }

    public static void showSubmitCancelAlert(Context context, String msg, DialogInterface.OnClickListener submitListener, DialogInterface.OnClickListener cancelListener) {
        showYesNoAlert(context, null, msg, "SUBMIT", "CANCEL", submitListener, cancelListener);
    }

    public static void showYesNoAlert(Context context, String msg, DialogInterface.OnClickListener yesListener, DialogInterface.OnClickListener noListener) {
        showYesNoAlert(context, null, msg, yesListener, noListener);
    }

    public static void showYesNoAlert(Context context, String msg, DialogInterface.OnClickListener yesListener) {
        showYesNoAlert(context, null, msg, yesListener, null);
    }

    public static void showYesNoAlert(Context context, int resId, DialogInterface.OnClickListener yesListener) {
        showYesNoAlert(context, context.getResources().getString(resId), yesListener);
    }

    public static void showDialog(Context context, String title, String message, boolean cancelable, String buttonLabel, DialogInterface.OnClickListener buttonListener) {
        try {
            if (isDialogShowing())
                return;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = setCustomView(context, title);
            builder.setCustomTitle(view);
            builder.setTitle(title)
                    .setMessage(message)
                    .setCancelable(cancelable)
                    .setNeutralButton(buttonLabel, buttonListener);

            mDialog = builder.create();
            mDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static View setCustomView(Context context, String title) {
        View view = null;
        try {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.activity_dialog_title, null);
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);

            ImageView ivError = (ImageView) view.findViewById(R.id.iv_error);
            ImageView ivWarning = (ImageView) view.findViewById(R.id.iv_warning);
            ImageView ivSucess = (ImageView) view.findViewById(R.id.iv_sucess);
            ImageView ivConfirm = (ImageView) view.findViewById(R.id.iv_confirm);

            ivSucess.setVisibility(View.GONE);
            ivWarning.setVisibility(View.GONE);
            ivError.setVisibility(View.GONE);
            ivConfirm.setVisibility(View.GONE);

            if (title != null && title.indexOf("Success") != -1) {
                ivSucess.setVisibility(View.VISIBLE);
            } else if (title != null && title.indexOf("Alert") != -1) {
                ivWarning.setVisibility(View.VISIBLE);
            } else if (title != null && (title.indexOf("Error") != -1
                    || title.indexOf("Failed") != -1)) {
                ivError.setVisibility(View.VISIBLE);
            }
            if (title == null || title.equals("")) {
                title = "Confirm";
            }
            if (title != null && title.indexOf("Confirm") != -1) {
                ivConfirm.setVisibility(View.VISIBLE);
            }
            tvTitle.setText(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

}
