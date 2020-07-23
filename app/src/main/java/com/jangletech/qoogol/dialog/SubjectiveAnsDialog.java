package com.jangletech.qoogol.dialog;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.DialogSubjectiveAnsBinding;
import com.jangletech.qoogol.util.AppUtils;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

public class SubjectiveAnsDialog extends Dialog {
    private static final String TAG = "SubjectiveAnsDialog";
    private DialogSubjectiveAnsBinding mBinding;
    private Context mContext;
    private String strAns;
    private GetAnsListener getAnsListener;
    private int seconds, minutes;

    public SubjectiveAnsDialog(@NonNull Context mContext, String ans, int seconds, int minutes, GetAnsListener listener) {
        super(mContext, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.mContext = mContext;
        this.strAns = ans;
        this.getAnsListener = listener;
        this.seconds = seconds;
        this.minutes = minutes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getAttributes().windowAnimations = R.anim.fragment_open_enter;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_subjective_ans, null, false);
        setContentView(mBinding.getRoot());
        setTimer(mBinding.tvtimer, seconds, minutes);
        mBinding.etAns.requestFocus();
        answerCharCounter(mBinding.etAns, mBinding.tvWordCounter, 200);
        mBinding.etAns.append(strAns);

        KeyboardVisibilityEvent.setEventListener((Activity) mContext,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen) {
                            //showToast("Opened");
                        } else {
                            //showToast("Closed");
                            getAnsListener.onAnswerEntered(AppUtils.encodedString(mBinding.etAns.getText().toString().trim()));
                            dismiss();
                        }
                    }
                });

       /* setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    getAnsListener.onAnswerEntered(mBinding.etAns.getText().toString().trim());
                    dismiss();
                }
                return true;
            }
        });*/
    }

    private void setTimer(TextView tvTimer, int seconds, int minutes) {
        CountDownTimer countDownTimer = new CountDownTimer(60 * 1000 * 60, 1000) {
            int timerCountSeconds = seconds;
            int timerCountMinutes = minutes;

            public void onTick(long millisUntilFinished) {
                // timer.setText(new SimpleDateFormat("mm:ss").format(new Date( millisUntilFinished)));
                if (timerCountSeconds < 59) {
                    timerCountSeconds++;
                } else {
                    timerCountSeconds = 0;
                    timerCountMinutes++;
                }
                if (timerCountMinutes < 10) {
                    if (timerCountSeconds < 10) {
                        tvTimer.setText(String.valueOf("0" + timerCountMinutes + ":0" + timerCountSeconds));
                    } else {
                        tvTimer.setText(String.valueOf("0" + timerCountMinutes + ":" + timerCountSeconds));
                    }
                } else {
                    if (timerCountSeconds < 10) {
                        tvTimer.setText(String.valueOf(timerCountMinutes + ":0" + timerCountSeconds));
                    } else {
                        tvTimer.setText(String.valueOf(timerCountMinutes + ":" + timerCountSeconds));
                    }
                }
            }

            public void onFinish() {
                tvTimer.setText("00:00");
            }
        }.start();
    }

    private void answerCharCounter(EditText etAnswer, TextView tvCounter, int maxWordLength) {

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                return null;
            }
        };
        etAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int wordCount = 0;
                if (s.toString().contains(" ")) {
                    String[] words = s.toString().split(" ", -1);
                    for (int i = 0; i < words.length; i++) {
                        if (!words[i].isEmpty()) {
                            wordCount++;
                            //AppUtils.encodedString(s.toString());
                        }
                    }
                }

                if (wordCount == maxWordLength) {
                    etAnswer.setFilters(new InputFilter[]{new InputFilter.LengthFilter(s.length())});
                }

                tvCounter.setText("Words Remaining : " + (maxWordLength - wordCount + "/" + String.valueOf(maxWordLength)));
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    public interface GetAnsListener {
        void onAnswerEntered(String answer);
    }

    public class MainSearchLayout extends ConstraintLayout {

        public MainSearchLayout(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.dialog_subjective_ans, this);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            Log.d("Search Layout", "Handling Keyboard Window shown");

            final int proposedheight = MeasureSpec.getSize(heightMeasureSpec);
            final int actualHeight = getHeight();

            if (actualHeight > proposedheight) {
                // Keyboard is shown
                showToast("Keyboard is shown");

            } else {
                // Keyboard is hidden
                showToast("Keyboard is hidden");
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
