package com.jangletech.qoogol.ui.learning;

import android.app.Application;
import android.text.TextUtils;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;


public class LearningViewModel extends AndroidViewModel {
    ApiInterface apiService;
    public final AppRepository mAppRepository;
    Call<LearningQuestResponse> call;
    String ratings="", diff_level="", q_type="", q_category="", trending="",popular="",recent="",main_catg,sub_catg;
    PreferenceManager mSettings;
    List<LearningQuestionsNew> questionsFilteredList;
    MutableLiveData<List<LearningQuestionsNew>> filterQueList;
    String pageCount;

    public LearningViewModel(Application application) {
        super(application);
        apiService = ApiClient.getInstance().getApi();
        mAppRepository = new AppRepository(application);
        mSettings = new PreferenceManager(application);
        questionsFilteredList=new ArrayList<>();
        filterQueList=new MutableLiveData<>();
        pageCount="0";
    }

    LiveData<List<LearningQuestionsNew>> getQuestionList() {
        return mAppRepository.getQuestionsFromDb();
    }

    LiveData<List<LearningQuestionsNew>> getQuestion(String q_id) {
        return mAppRepository.getQuestionFromDb(q_id);
    }

    LiveData<List<LearningQuestionsNew>> getFilterQuestionList() {
        return filterQueList;
    }

//    public List<LearningQuestionsNew> getFilterQuestionList() {
//        return questionsFilteredList;
//    }

    LiveData<List<LearningQuestionsNew>> getSharedQuestionList(String CASE) {
        return mAppRepository.getSharedQuestions(CASE);
    }

    void fetchQuestionData(String q_id, HashMap<String, String> params) {
        getDataFromApi(q_id, "",params);
    }

    void fetchQuestionData(String q_id, String CASE,HashMap<String, String> params) {
        getDataFromApi(q_id, CASE,params);
    }

//    public void getFilters() {
//        trending="";
//        popular="";
//        recent="";
//        main_catg="";
//        sub_catg="";
//        ratings = mSettings.getRatingsFilter();
//        diff_level = TextUtils.join(", ", mSettings.getQueDiffLevelFilter()).replace("Easy","E").replace("Medium","M").replace("Hard","H");
//        q_type = TextUtils.join(", ", mSettings.getTypeFilter());
//        q_category = TextUtils.join(", ", mSettings.getQueCategoryFilter());
//        if (q_type.contains(Constant.trending))
//            trending="1";
//        if (q_type.contains(Constant.popular))
//            popular="1";
//        if (q_type.contains(Constant.recent))
//            recent="1";
//        if (q_category.contains(Constant.short_ans))
//            addMainCatg(Constant.SHORT_ANSWER);
//        if (q_category.contains(Constant.long_ans))
//            addMainCatg(Constant.LONG_ANSWER);
//        if (q_category.contains(Constant.fill_the_blanks))
//            addMainCatg(Constant.FILL_THE_BLANKS);
//
//        if (q_category.contains(Constant.scq))
//            addSubCatg(Constant.SCQ);
//
//        if (q_category.contains(Constant.mcq))
//            addSubCatg(Constant.MCQ);
//
//        if (q_category.contains(Constant.true_false))
//            addSubCatg(Constant.TRUE_FALSE);
//
//        if (q_category.contains(Constant.match_pair))
//            addSubCatg(Constant.MATCH_PAIR);
//    }

    private void addMainCatg(String catg) {
        if (main_catg.isEmpty())
            main_catg=catg;
        else
            main_catg = ","+catg;
    }

    private void addSubCatg(String catg) {
        if (sub_catg.isEmpty())
            sub_catg=catg;
        else
            sub_catg = ","+catg;
    }


    private void getDataFromApi(String q_id, String CASE,HashMap<String, String> params) {
        if (CASE.equalsIgnoreCase(""))
            call = apiService.fetchQAApi(new PreferenceManager(getApplication()).getUserId(), q_id, params.get(Constant.q_avg_ratings), params.get(Constant.q_diff_level),params.get(Constant.q_trending),params.get(Constant.q_popular)
                    ,params.get(Constant.q_recent),params.get(Constant.q_type),params.get(Constant.q_option_type),new PreferenceManager(getApplication()).getString(Constant.ue_id),String.valueOf(pageCount));
        else
            call = apiService.fetchQAByCaseApi(new PreferenceManager(getApplication()).getUserId(), q_id, CASE,params.get(Constant.q_avg_ratings), params.get(Constant.q_diff_level),params.get(Constant.q_trending),params.get(Constant.q_popular)
                    ,params.get(Constant.q_recent),params.get(Constant.q_type),params.get(Constant.q_option_type));
        call.enqueue(new Callback<LearningQuestResponse>() {
            @Override
            public void onResponse(Call<LearningQuestResponse> call, retrofit2.Response<LearningQuestResponse> response) {
                try {
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {

                        if (response.body().getQuestion_list()!=null && response.body().getQuestion_list().size()>0) {
                            pageCount=response.body().getRow_count();
                            ExecutorService executor = Executors.newSingleThreadExecutor();
                            executor.execute(() -> mAppRepository.insertQuestions(response.body().getQuestion_list()));
                            filterQueList.setValue(response.body().getQuestion_list());
                            Thread thread = new Thread() {
                                @Override
                                public void run() {
                                    downloadImages();
                                }
                            };

                            thread.start();
                        } else {
                            filterQueList.setValue(null);
                        }

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
