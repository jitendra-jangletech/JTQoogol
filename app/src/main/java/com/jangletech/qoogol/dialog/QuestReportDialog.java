package com.jangletech.qoogol.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.DialogReportQuestionBinding;

public class QuestReportDialog extends Dialog {

    private DialogReportQuestionBinding mBinding;
    private QuestReportDialogListener questReportDialogListener;
    private int pos;

    public QuestReportDialog(@NonNull Context context, QuestReportDialogListener questReportDialogListener,int pos) {
        super(context);
        this.questReportDialogListener = questReportDialogListener;
        this.pos = pos;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_report_question, null, false);
        setContentView(mBinding.getRoot());

        mBinding.btnSubmit.setOnClickListener(v -> {
            questReportDialogListener.onReportQuestSubmitClick(pos);
        });

        mBinding.tvWrongQuest.setOnClickListener(v -> {
            setCheck(mBinding.checkWrongQuest);
        });
        mBinding.tvOther.setOnClickListener(v -> {
            setCheck(mBinding.checkOther);
        });

        mBinding.tvFormattingIssue.setOnClickListener(v -> {
            setCheck(mBinding.checkFormattingIssue);
        });

    }

    private void setCheck(CheckBox checkBox) {
        if (checkBox.isChecked()) {
            checkBox.setChecked(false);
        } else {
            checkBox.setChecked(true);
        }
    }

    public interface QuestReportDialogListener {
        void onReportQuestSubmitClick(int pos);
    }
}
