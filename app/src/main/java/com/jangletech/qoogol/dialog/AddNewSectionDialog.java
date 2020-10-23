package com.jangletech.qoogol.dialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.DialogAddNewSectionBinding;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.TinyDB;

public class AddNewSectionDialog extends DialogFragment implements TextWatcher {

    private static final String TAG = "AddNewSectionDialog";
    private DialogAddNewSectionBinding mBinding;
    private AddNewSectionClickListener listener;
    private String[] sectionsAvailable;
    private String sectionName = "None";
    private int pos;
    private int testTotMarks = 0;
    private int type;
    private float qMarks;
    private String duration = "";

    public AddNewSectionDialog(AddNewSectionClickListener listener, int type, float qMarks, String duration) {
        this.listener = listener;
        this.type = type;
        this.qMarks = qMarks;
        this.duration = duration;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog_Alert);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_add_new_section, container, false);
        setCancelable(false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated Type : " + type);
        try {
            setSections();
            testTotMarks = Integer.parseInt(TinyDB.getInstance(getActivity()).getString(Constant.tm_tot_marks));
            Log.i(TAG, "onActivityCreated Tm Total Marks : " + testTotMarks);
            mBinding.etSectionName.addTextChangedListener(this);
            mBinding.etSectionMarks.addTextChangedListener(this);

            if (type == Constant.mrks) {
                mBinding.tvAddSection.setText("Add Question Marks");
                mBinding.tilSectionMarks.setVisibility(View.VISIBLE);
                mBinding.etSectionMarks.setText(String.valueOf(qMarks));
                mBinding.tilSectionMarks.setHint("Question Marks");
                mBinding.sectionRadioGroup.setVisibility(View.GONE);
                mBinding.tilQuestDuration.setVisibility(View.VISIBLE);
                mBinding.etQuestDuration.setText(duration);
            }

            mBinding.btnSave.setOnClickListener(v -> {
                try {

                    if (type == Constant.section) {
                        if (mBinding.tilSectionMarks.getVisibility() == View.VISIBLE &&
                                mBinding.etSectionMarks.getText().toString().trim().isEmpty()) {
                            mBinding.tilSectionMarks.setError("Enter Section Marks");
                            return;
                        } else if (mBinding.tilSectionMarks.getVisibility() == View.VISIBLE &&
                                qMarks > testTotMarks) {
                            mBinding.tilSectionMarks.setError("Section Marks cant be greater than Test Total Marks.");
                            return;
                        } else {
                            qMarks = Float.parseFloat(mBinding.etSectionMarks.getText().toString().trim());
                            String duration = mBinding.etQuestDuration.getText().toString().trim();
                            if (mBinding.tilSectionMarks.getVisibility() == View.GONE) {
                                listener.onNewSectionSaveClick(sectionName, testTotMarks, duration, pos);
                            } else {
                                listener.onNewSectionSaveClick(sectionName, qMarks, duration, pos);
                            }
                            dismiss();
                        }

                    } else {
                        qMarks = Float.parseFloat(mBinding.etSectionMarks.getText().toString().trim());
                        duration = mBinding.etQuestDuration.getText().toString().trim();
                        if (mBinding.tilSectionMarks.getVisibility() == View.VISIBLE &&
                                mBinding.etSectionMarks.getText().toString().trim().isEmpty()) {
                            mBinding.tilSectionMarks.setError("Enter Question Marks");
                            return;
                        } else if (mBinding.tilSectionMarks.getVisibility() == View.VISIBLE && qMarks <= 0) {
                            mBinding.tilSectionMarks.setError("Enter valid marks");
                            return;
                        } else if (mBinding.tilSectionMarks.getVisibility() == View.VISIBLE &&
                                qMarks > testTotMarks) {
                            mBinding.tilSectionMarks.setError("Question Marks cant be greater than Test Total Marks.");
                            return;
                        } else if (mBinding.tilQuestDuration.getVisibility() == View.VISIBLE &&
                                mBinding.etQuestDuration.getText().toString().trim().isEmpty()) {
                            mBinding.tilQuestDuration.setError("Enter Question Recommended Time.");
                            return;
                        } else {
                            listener.onNewSectionSaveClick("Marks", qMarks, duration, -1);
                            dismiss();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            mBinding.sectionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton radioButton = group.findViewById(checkedId);
                    if (radioButton != null) {
                        pos = Integer.parseInt(radioButton.getTag().toString());
                        sectionName = radioButton.getText().toString();
                        if (sectionName.equalsIgnoreCase("None")) {
                            mBinding.tilSectionMarks.getEditText().setText("");
                            mBinding.tilSectionMarks.setVisibility(View.GONE);
                        } else {
                            mBinding.tilSectionMarks.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });

            mBinding.btnCancel.setOnClickListener(v -> {
                dismiss();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSections() {
        //Gson gson = new Gson();
        //String json = TinyDB.getInstance(getActivity()).getString(Constant.TEST_SUBJECT_CHAP);
        //TestSubjectChapterMaster testSubjectChapterMaster = gson.fromJson(json, TestSubjectChapterMaster.class);
        //Log.i(TAG, "setSections : " + testSubjectChapterMaster.getSections());
        String sections = TinyDB.getInstance(getActivity()).getString(Constant.TEST_SUBJECT_CHAP);
        int sectionCount = 0;
        String[] sectns = new String[5];
        if (sections != null) {
            sectns = sections.split(",", -1);
            sectionCount = sectns.length;
            Log.i(TAG, "setSections : " + sectns);
            Log.i(TAG, "setSections : " + sectionCount);
        }
        if (sectionCount == 1) {
            Log.i(TAG, "setSections First : " + sectns[0]);
            mBinding.section1.setVisibility(View.VISIBLE);
            mBinding.section1.setText(sectns[0].split("=", -1)[1]);
            mBinding.section1.setTag(sectns[0].split("=", -1)[0]);
        } else if (sectionCount == 2) {
            mBinding.section1.setVisibility(View.VISIBLE);
            mBinding.section1.setText(sectns[0].split("=", -1)[1]);
            mBinding.section1.setTag(sectns[0].split("=", -1)[0]);

            mBinding.section2.setVisibility(View.VISIBLE);
            mBinding.section2.setText(sectns[1].split("=", -1)[1]);
            mBinding.section2.setTag(sectns[1].split("=", -1)[0]);
        } else if (sectionCount == 3) {
            mBinding.section1.setVisibility(View.VISIBLE);
            mBinding.section1.setText(sectns[0].split("=", -1)[1]);
            mBinding.section1.setTag(sectns[0].split("=", -1)[0]);

            mBinding.section2.setVisibility(View.VISIBLE);
            mBinding.section2.setText(sectns[1].split("=", -1)[1]);
            mBinding.section2.setTag(sectns[1].split("=", -1)[0]);

            mBinding.section3.setVisibility(View.VISIBLE);
            mBinding.section3.setText(sectns[2].split("=", -1)[1]);
            mBinding.section3.setTag(sectns[2].split("=", -1)[0]);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().length() > 0) {
            mBinding.tilSectionName.setError(null);
            mBinding.tilSectionMarks.setError(null);
        }
    }

    public interface AddNewSectionClickListener {
        void onNewSectionSaveClick(String name, float marks, String duration, int pos);
    }
}
