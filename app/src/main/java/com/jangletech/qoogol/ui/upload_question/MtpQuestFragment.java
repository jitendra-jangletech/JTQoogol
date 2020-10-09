package com.jangletech.qoogol.ui.upload_question;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.FragmentUpMtpQueBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.dialog.SubjectiveAnsDialog;
import com.jangletech.qoogol.model.ResponseObj;
import com.jangletech.qoogol.model.UploadQuestion;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.util.Constant.MATCH_PAIR;
import static com.jangletech.qoogol.util.Constant.qoogol;

public class MtpQuestFragment extends BaseFragment implements SubjectiveAnsDialog.GetAnsListener {

    private static final String TAG = "MtpQuestFragment";
    private FragmentUpMtpQueBinding mBinding;
    private SubjectiveAnsDialog subjectiveAnsDialog;
    private String A1 = "", A2 = "", A3 = "", A4 = "";
    private String selectedOptions = "";
    private UploadQuestion uploadQuestion;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_up_mtp_que, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null && getArguments().getSerializable("Question") != null) {
            uploadQuestion = (UploadQuestion) getArguments().getSerializable("Question");
            mBinding.etQuestion.setText(uploadQuestion.getQuestDescription());
            mBinding.subject.setText("Subject : " + uploadQuestion.getSubjectName());
        }

        mBinding.toggleAddQuestDesc.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mBinding.etQuestionDesc.setVisibility(View.VISIBLE);
            } else {
                mBinding.etQuestionDesc.setVisibility(View.GONE);
            }
        });


        mBinding.saveQuestion.setOnClickListener(v -> addQuestion());

        mBinding.reset.setOnClickListener(v -> {
            mBinding.a1.clearCheck();
            mBinding.a2.clearCheck();
            mBinding.a3.clearCheck();
            mBinding.a4.clearCheck();
            resetRadioGroup(mBinding.a1);
            resetRadioGroup(mBinding.a2);
            resetRadioGroup(mBinding.a3);
            resetRadioGroup(mBinding.a4);
        });

        mBinding.a1.setOnCheckedChangeListener((group, checkedId) -> {
            if (!selectedOptions.contains(getCheckedRadioButton(mBinding.a1)))
                selectedOptions = selectedOptions + "," + getCheckedRadioButton(mBinding.a1);
            resetRadioGroup(mBinding.a2);
            setRadioDisable(mBinding.a2, selectedOptions);
            setRadioDisable(mBinding.a3, selectedOptions);
            setRadioDisable(mBinding.a4, selectedOptions);
        });

        mBinding.a2.setOnCheckedChangeListener((group, checkedId) -> {
            if (!selectedOptions.contains(getCheckedRadioButton(mBinding.a2)))
                selectedOptions = selectedOptions + "," + getCheckedRadioButton(mBinding.a2);
            resetRadioGroup(mBinding.a3);
            setRadioDisable(mBinding.a3, selectedOptions);
            setRadioDisable(mBinding.a4, selectedOptions);

        });

        mBinding.a3.setOnCheckedChangeListener((group, checkedId) -> {
            if (!selectedOptions.contains(getCheckedRadioButton(mBinding.a3)))
                selectedOptions = selectedOptions + "," + getCheckedRadioButton(mBinding.a3);
            resetRadioGroup(mBinding.a4);
            setRadioDisable(mBinding.a4, selectedOptions);
        });

        mBinding.a4.setOnCheckedChangeListener((group, checkedId) -> getCheckedRadioButton(mBinding.a4));
    }

    @Override
    public void onAnswerEntered(String answer) {

    }

    private void addQuestion() {
        if (isValidate()) {
            String user_id = new PreferenceManager(getActivity()).getUserId();
            UploadQuestion uploadQuestion = (UploadQuestion) getArguments().getSerializable("Question");
            Call<ResponseObj> call = getApiService().addTFQuestionsApi(user_id, qoogol, getDeviceId(getActivity()),
                    uploadQuestion.getSubjectId(), mBinding.etQuestion.getText().toString(),
                    mBinding.etQuestionDesc.getText().toString(), MATCH_PAIR,  mBinding.edtmarks.getText().toString(),
                    mBinding.edtduration.getText().toString(),getSelectedDiffLevel(),getSelectedAns());

            call.enqueue(new Callback<ResponseObj>() {
                @Override
                public void onResponse(Call<ResponseObj> call, retrofit2.Response<ResponseObj> response) {
                    try {
                        if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                            Toast.makeText(getActivity(), "Question added successfully", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_upload_question);
                        } else {
                            Toast.makeText(getActivity(), UtilHelper.getAPIError(String.valueOf(response.body())), Toast.LENGTH_SHORT).show();
                        }
                        ProgressDialog.getInstance().dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                        ProgressDialog.getInstance().dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ResponseObj> call, Throwable t) {
                    t.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            });
        }
    }

    private String getSelectedDiffLevel() {
        String level = "";
        int id = mBinding.radioDifflevel.getCheckedRadioButtonId();

        View radioButton = mBinding.radioDifflevel.findViewById(id);
        if (radioButton != null) {
            int idx = mBinding.radioDifflevel.indexOfChild(radioButton);
            RadioButton r = (RadioButton) mBinding.radioDifflevel.getChildAt(idx);
            level = r.getText() != null ? r.getText().toString() : "";
        }

        return level.replace("Easy","E").replace("Medium","M").replace("Hard","h");
    }

    private String getSelectedAns() {
        int id1 = mBinding.a1.getCheckedRadioButtonId();
        int id2 = mBinding.a2.getCheckedRadioButtonId();
        int id3 = mBinding.a3.getCheckedRadioButtonId();
        int id4 = mBinding.a4.getCheckedRadioButtonId();
        if (id1 == -1 && id2 == -1 && id3 == -1 && id4 == -1) {
            return "";
        } else {
            String ans = "";
            View radioButton1 = mBinding.a1.findViewById(id1);
            int idx1 = mBinding.a1.indexOfChild(radioButton1);
            RadioButton r1 = (RadioButton) mBinding.a1.getChildAt(idx1);
            ans = "A1::" + r1.getText().toString();

            View radioButton2 = mBinding.a2.findViewById(id2);
            int idx2 = mBinding.a2.indexOfChild(radioButton2);
            RadioButton r2 = (RadioButton) mBinding.a2.getChildAt(idx2);
            ans = ans + "A1::" + r2.getText().toString();

            View radioButton3 = mBinding.a3.findViewById(id3);
            int idx3 = mBinding.a3.indexOfChild(radioButton3);
            RadioButton r3 = (RadioButton) mBinding.a3.getChildAt(idx3);
            ans = ans + "A1::" + r3.getText().toString();

            View radioButton4 = mBinding.a4.findViewById(id4);
            int idx4 = mBinding.a4.indexOfChild(radioButton4);
            RadioButton r4 = (RadioButton) mBinding.a4.getChildAt(idx4);
            ans = ans + "A1::" + r4.getText().toString();

            return ans;

        }
    }

    private boolean isValidate() {
        int id1 = mBinding.a1.getCheckedRadioButtonId();
        int id2 = mBinding.a2.getCheckedRadioButtonId();
        int id3 = mBinding.a3.getCheckedRadioButtonId();
        int id4 = mBinding.a4.getCheckedRadioButtonId();

        if (mBinding.subject.getText().toString().isEmpty()) {
            mBinding.subject.setError("Please select subject.");
            return false;
        } else if (mBinding.etQuestion.getText().toString().isEmpty()) {
            mBinding.etQuestion.setError("Please enter question.");
            return false;
        } else if (mBinding.opa1.getText().toString().isEmpty()) {
            mBinding.opa1.setError("Please enter option 1.");
            return false;
        } else if (mBinding.opb1.getText().toString().isEmpty()) {
            mBinding.opb1.setError("Please enter option 1.");
            return false;
        } else if (mBinding.opa2.getText().toString().isEmpty()) {
            mBinding.opa2.setError("Please enter option 2.");
            return false;
        } else if (mBinding.opb2.getText().toString().isEmpty()) {
            mBinding.opb2.setError("Please enter option 1.");
            return false;
        } else if (mBinding.opa3.getText().toString().isEmpty()) {
            mBinding.opa3.setError("Please enter option 1.");
            return false;
        } else if (mBinding.opb3.getText().toString().isEmpty()) {
            mBinding.opb3.setError("Please enter option 1.");
            return false;
        } else if (mBinding.opa4.getText().toString().isEmpty()) {
            mBinding.opa4.setError("Please enter option 1.");
            return false;
        } else if (mBinding.opb4.getText().toString().isEmpty()) {
            mBinding.opb4.setError("Please enter option 1.");
            return false;
        } else if (id1 == -1 || id2 == -1 || id3 == -1 || id4 == -1) {
            if (id1 == -1 && id2 == -1 && id3 == -1 && id4 == -1) {
                return true;
            } else {
                Toast.makeText(getActivity(), "Please select all radio options or reset all", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            return true;
        }
    }

    private String getCheckedRadioButton(RadioGroup radioGroup) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            if (radioButton.isChecked()) {
                return radioButton.getText().toString();
            }
        }
        return "";
    }

    private void setRadioDisable(RadioGroup radioGroup, String strSelected) {
        Log.i(TAG, "setRadioDisable : " + strSelected);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            String[] opts = strSelected.split(",", -1);
            for (String string : opts) {
                if (string != null && !string.isEmpty()) {
                    if (radioButton.getText().toString().contains(string)) {
                        radioButton.setChecked(false);
                        radioButton.setEnabled(false);
                    } else {
                        radioButton.setEnabled(true);
                    }
                }
            }
        }
    }

    private void resetRadioGroup(RadioGroup radioGroup) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            radioButton.setEnabled(true);
            radioButton.setChecked(false);
        }
    }
}
