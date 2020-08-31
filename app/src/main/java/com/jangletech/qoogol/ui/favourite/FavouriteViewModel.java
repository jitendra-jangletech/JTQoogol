package com.jangletech.qoogol.ui.favourite;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.model.LearningQuestResponse;
import com.jangletech.qoogol.model.LearningQuestionsNew;
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

import static com.jangletech.qoogol.ui.BaseFragment.getDeviceId;
import static com.jangletech.qoogol.util.Constant.qoogol;


public class FavouriteViewModel extends AndroidViewModel {
    ApiInterface apiService;
    public final AppRepository mAppRepository;
    private MutableLiveData<List<LearningQuestionsNew>> favQueList;
    public FavouriteViewModel(Application application) {
        super(application);
        apiService = ApiClient.getInstance().getApi();
        mAppRepository = new AppRepository(application);
        favQueList=new MutableLiveData<>();
    }

    LiveData<List<LearningQuestionsNew>> geFavtQuestionList() {
        return favQueList;
    }


   public void fetchFavQuestionData(SwipeRefreshLayout learningSwiperefresh) {
        getDataFromApi(learningSwiperefresh);
    }

    private void getDataFromApi(SwipeRefreshLayout learningSwiperefresh) {
        Call<LearningQuestResponse> call = apiService.fetchFavQAApi(new PreferenceManager(getApplication()).getUserId(), "FV",getDeviceId(getApplication()), qoogol);
        call.enqueue(new Callback<LearningQuestResponse>() {
            @Override
            public void onResponse(Call<LearningQuestResponse> call, retrofit2.Response<LearningQuestResponse> response) {
                try {
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> mAppRepository.insertQuestions(response.body().getQuestion_list()));
                        if (response.body().getQuestion_list()!=null&&response.body().getQuestion_list().size()>0) {
                            favQueList.setValue(response.body().getQuestion_list());
                        } else
                            favQueList.setValue(null);
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                downloadImages();
                            }
                        };

                        thread.start();
                        dismissRefresh(learningSwiperefresh);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    dismissRefresh(learningSwiperefresh);
                }
            }

            @Override
            public void onFailure(Call<LearningQuestResponse> call, Throwable t) {
                t.printStackTrace();
                dismissRefresh(learningSwiperefresh);
            }
        });
    }

    private void dismissRefresh(SwipeRefreshLayout learningSwiperefresh) {
        if (learningSwiperefresh.isRefreshing())
            learningSwiperefresh.setRefreshing(true);
    }

    private String  getQuestionImages() {
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
