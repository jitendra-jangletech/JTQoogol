package com.jangletech.qoogol.ui.upload_question;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.FragmentMcqQueBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.ResponseObj;
import com.jangletech.qoogol.model.UploadQuestion;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.util.AppUtils.getDeviceId;
import static com.jangletech.qoogol.util.Constant.MCQ;
import static com.jangletech.qoogol.util.Constant.qoogol;

/**
 * A simple {@link Fragment} subclass.
 */
public class MCQ_QueFragment extends Fragment {

    private FragmentMcqQueBinding mBinding;
    private UploadQuestion uploadQuestion;
    ApiInterface apiService = ApiClient.getInstance().getApi();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mcq__que, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null && getArguments().getSerializable("Question") != null) {
            uploadQuestion = (UploadQuestion) getArguments().getSerializable("Question");
            mBinding.questionEdittext.setText(uploadQuestion.getQuestDescription());
            mBinding.subject.setText("Subject : " + uploadQuestion.getSubjectName());
        }

        mBinding.saveQuestion.setOnClickListener(v -> addQuestion());
    }

    private void addQuestion() {
        if (isValidate()) {
            String user_id = new PreferenceManager(getActivity()).getUserId();

            Call<ResponseObj> call= apiService.addQuestionsApi(user_id, qoogol, getDeviceId(),
                    1, mBinding.questionEdittext.getText().toString(),
                    mBinding.questiondescEdittext.getText().toString(),MCQ,mBinding.mcq1Edittext.getText().toString(),
                    mBinding.mcq2Edittext.getText().toString(),mBinding.mcq3Edittext.getText().toString(),
                    mBinding.mcq4Edittext.getText().toString(),getSelectedAns());

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

    private String getSelectedAns() {
        String ans="";
        if (mBinding.ansA.isChecked())
            ans = "A";

        if (mBinding.ansB.isChecked()) {
            if (ans.isEmpty())
                ans="B";
            else
                ans=ans+","+"B";
        }

        if (mBinding.ansC.isChecked()) {
            if (ans.isEmpty())
                ans="C";
            else
                ans=ans+","+"C";
        }

        if (mBinding.ansD.isChecked()) {
            if (ans.isEmpty())
                ans="D";
            else
                ans=ans+","+"D";
        }
        return ans;
    }

    private boolean isValidate() {
        if (mBinding.subject.getText().toString().isEmpty()) {
            mBinding.subject.setError("Please select subject.");
            return false;
        } if (mBinding.questionEdittext.getText().toString().isEmpty()) {
            mBinding.questionEdittext.setError("Please enter question.");
            return false;
        } if (mBinding.mcq1Edittext.getText().toString().isEmpty()) {
            mBinding.mcq1Edittext.setError("Please enter option 1.");
            return false;
        } if (mBinding.mcq2Edittext.getText().toString().isEmpty()) {
            mBinding.mcq2Edittext.setError("Please enter option 2.");
            return false;
        } if (mBinding.mcq3Edittext.getText().toString().isEmpty()) {
            mBinding.mcq3Edittext.setError("Please enter option 3.");
            return false;
        } else {
            return true;
        }
    }

}
