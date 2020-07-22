package com.jangletech.qoogol.ui.saved;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.model.LearningQuestions;
import com.jangletech.qoogol.model.SaveQuestResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.service.DownloadAsyncTask;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;


public class SavedViewModel extends AndroidViewModel {
    ApiInterface apiService;
    public final AppRepository mAppRepository;

    public SavedViewModel(Application application) {
        super(application);
        apiService = ApiClient.getInstance().getApi();
        mAppRepository = new AppRepository(application);
    }

    public void fetchQuestionData() {
        getDataFromApi();
    }


    private void getDataFromApi() {
        Call<SaveQuestResponse> call = apiService.fetchSavedQAApi(new PreferenceManager(getApplication()).getUserId(),"SV");
        call.enqueue(new Callback<SaveQuestResponse>() {
            @Override
            public void onResponse(Call<SaveQuestResponse> call, retrofit2.Response<SaveQuestResponse> response) {
                try {
                    if (response.body()!=null && response.body().getResponse().equalsIgnoreCase("200")){
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> mAppRepository.insertSavedQuestions(response.body().getQuestion_list()));
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
            public void onFailure(Call<SaveQuestResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void downloadImages() {
        try {
            DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask(UtilHelper.getDirectory(getApplication()));
            downloadAsyncTask.execute(createMediaPathDownloaded((getQuestionImages())), "1");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private String  getQuestionImages() {
        try {
            String images = "";
            List<LearningQuestions> list = mAppRepository.getSavedQuestions();
            for (LearningQuestions learningQuestionsNew : list) {
                images = images + learningQuestionsNew.getImageList();
            }

            return images;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
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

    public LiveData<List<LearningQuestions>> getQuestionList() {
        return mAppRepository.getSavedQuestionsFromDb();
    }
}
