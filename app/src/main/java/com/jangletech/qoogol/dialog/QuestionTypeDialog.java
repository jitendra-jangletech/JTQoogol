package com.jangletech.qoogol.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.DialogQuestionTypeBinding;
import com.jangletech.qoogol.model.UploadQuestion;
import com.jangletech.qoogol.util.Constant;

import java.util.ArrayList;
import java.util.List;

public class QuestionTypeDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private Activity mContext;
    private UploadQuestion uploadQuestion;
    private DialogQuestionTypeBinding mBinding;

    public QuestionTypeDialog(@NonNull Activity mContext, UploadQuestion uploadQuestion) {
        this.mContext = mContext;
        this.uploadQuestion = uploadQuestion;
    }

    public QuestionTypeDialog() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                setupFullHeight(bottomSheetDialog);
            }
        });
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_question_type, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prepareQueType();
        mBinding.tvSubject.setText(uploadQuestion.getSubjectName());
        mBinding.questionText.setText(uploadQuestion.getQuestDescription());
    }

    private void prepareQueType() {
        List que_categoryList = new ArrayList();
        que_categoryList.add(Constant.short_ans);
        que_categoryList.add(Constant.long_ans);
        que_categoryList.add(Constant.scq);
        que_categoryList.add(Constant.mcq);
        que_categoryList.add(Constant.fill_the_blanks);
        que_categoryList.add(Constant.true_false);
        que_categoryList.add(Constant.match_pair);

        mBinding.chipGrpQuestType.removeAllViews();
        for (int i = 0; i < que_categoryList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.chipGrpQuestType.getContext()).inflate(R.layout.chip_new, mBinding.chipGrpQuestType, false);
            chip.setText(que_categoryList.get(i).toString());
            chip.setTag("Question_Type");
            chip.setId(i);
            chip.setClickable(true);
            chip.setCheckable(true);
            chip.setOnClickListener(this);
            mBinding.chipGrpQuestType.addView(chip);
        }
    }

    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        //MaterialCardView bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(mBinding.rootLayout);
        ViewGroup.LayoutParams layoutParams = mBinding.rootLayout.getLayoutParams();

        int windowHeight = getWindowHeight();
        if (layoutParams != null) {
            layoutParams.height = windowHeight;
        }
        mBinding.rootLayout.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    @Override
    public void onClick(View v) {

    }
}
