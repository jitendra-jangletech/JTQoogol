package com.jangletech.qoogol.ui.Saved;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.model.LearningQuestions;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;

import java.util.List;

public class SavedViewModel extends AndroidViewModel {
    ApiInterface apiService;
    public final AppRepository mAppRepository;

    public SavedViewModel(Application application) {
        super(application);
        apiService = ApiClient.getInstance().getApi();
        mAppRepository = new AppRepository(application);
    }



    public LiveData<List<LearningQuestions>> getQuestionList() {
        return mAppRepository.getSavedQuestionsFromDb();
    }
}
