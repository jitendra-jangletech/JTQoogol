package com.jangletech.qoogol.ui.learning;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.LearningQuestResponse;
import com.jangletech.qoogol.model.LearningQuestionsNew;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.service.DownloadAsyncTask;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;


public class LearningViewModel extends AndroidViewModel {
    ApiInterface apiService;
    public final AppRepository mAppRepository;
    Call<LearningQuestResponse> call;

    public LearningViewModel(Application application) {
        super(application);
        apiService = ApiClient.getInstance().getApi();
        mAppRepository = new AppRepository(application);
    }

    LiveData<List<LearningQuestionsNew>> getQuestionList() {
        return mAppRepository.getQuestionsFromDb();
    }

    LiveData<List<LearningQuestionsNew>> getQuestion(String q_id) {
        return mAppRepository.getQuestionFromDb(q_id);
    }


    LiveData<List<LearningQuestionsNew>> getSharedQuestionList(String CASE) {
        return mAppRepository.getSharedQuestions(CASE);
    }

    void fetchQuestionData(String q_id) {
        getDataFromApi(q_id, "");
    }

    void fetchQuestionData(String q_id, String CASE) {
        getDataFromApi(q_id, CASE);
    }

    private void getDataFromApi(String q_id, String CASE) {
        if (CASE.equalsIgnoreCase(""))
            call = apiService.fetchQAApi(new PreferenceManager(getApplication()).getUserId(), q_id);
        else
            call = apiService.fetchQAApi(new PreferenceManager(getApplication()).getUserId(), q_id, CASE);
        call.enqueue(new Callback<LearningQuestResponse>() {
            @Override
            public void onResponse(Call<LearningQuestResponse> call, retrofit2.Response<LearningQuestResponse> response) {
                try {
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> mAppRepository.insertQuestions(response.body().getQuestion_list()));
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                downloadImages();
                            }
                        };

                        thread.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LearningQuestResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private String getQuestionImages() {
        try {
            String images = "";
            List<LearningQuestionsNew> list = mAppRepository.getQuestions();
            for (LearningQuestionsNew learningQuestionsNew : list) {
                images = images + learningQuestionsNew.getImageList();
            }

            return images;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void downloadImages() {
        try {
            DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask(UtilHelper.getDirectory(getApplication()));
            downloadAsyncTask.execute(createMediaPathDownloaded((getQuestionImages())), "1");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private String createMediaPathDownloaded(String img) {
        String mediaPaths = "";
        String[] imglist = img.split(",");
        for (String mediaList1 : imglist) {
            String path = Constant.QUESTION_IMAGES_API + mediaList1.trim();
            mediaPaths = mediaPaths + path + ",";
        }
        return mediaPaths;
    }
}
