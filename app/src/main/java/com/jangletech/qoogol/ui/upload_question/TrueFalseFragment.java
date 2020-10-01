package com.jangletech.qoogol.ui.upload_question;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.FragmentUpTrueFalseBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.ResponseObj;
import com.jangletech.qoogol.model.UploadQuestion;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.util.AppUtils.getDeviceId;
import static com.jangletech.qoogol.util.Constant.TRUE_FALSE;
import static com.jangletech.qoogol.util.Constant.qoogol;

public class TrueFalseFragment extends BaseFragment{

    private static final String TAG = "TrueFalseFragment";
    private FragmentUpTrueFalseBinding mBinding;
    private UploadQuestion uploadQuestion;
    ApiInterface apiService = ApiClient.getInstance().getApi();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_up_true_false, container,false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBinding.toggleAddQuestDesc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBinding.etQuestionDesc.setVisibility(View.VISIBLE);
                } else {
                    mBinding.etQuestionDesc.setVisibility(View.GONE);
                }
            }
        });

        if (getArguments() != null && getArguments().getSerializable("Question") != null) {
            uploadQuestion = (UploadQuestion) getArguments().getSerializable("Question");
            mBinding.etQuestionDesc.setText(uploadQuestion.getQuestDescription());
            mBinding.subject.setText("Subject : " + uploadQuestion.getSubjectName());
        }

        mBinding.saveQuestion.setOnClickListener(v -> addQuestion());
    }

    private void addQuestion() {
        if (isValidate()) {
            String user_id = new PreferenceManager(getActivity()).getUserId();
            UploadQuestion  uploadQuestion = (UploadQuestion) getArguments().getSerializable("Question");
            Call<ResponseObj> call= apiService.addTFQuestionsApi(user_id, qoogol, getDeviceId(getActivity()),
                    uploadQuestion.getSubjectId(), mBinding.etQuestion.getText().toString(),
                    mBinding.etQuestionDesc.getText().toString(),TRUE_FALSE,getSelectedAns());

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
        int id = mBinding.radiobtn.getCheckedRadioButtonId();

        View radioButton =  mBinding.radiobtn.findViewById(id);
        if (radioButton!=null) {
            int idx =  mBinding.radiobtn.indexOfChild(radioButton);
            RadioButton r = (RadioButton) mBinding.radiobtn.getChildAt(idx);
            ans =r.getText()!=null?r.getText().toString():"";
        }

        return ans;
    }

    private boolean isValidate() {
        if (mBinding.subject.getText().toString().isEmpty()) {
            mBinding.subject.setError("Please select subject.");
            return false;
        } if (mBinding.etQuestion.getText().toString().isEmpty()) {
            mBinding.etQuestion.setError("Please enter question.");
            return false;
        }  else {
            return true;
        }
    }
}
