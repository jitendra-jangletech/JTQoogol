package com.jangletech.qoogol.ui.faq;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jangletech.qoogol.model.FAQModel;

import java.util.List;

public class FaqViewModel extends AndroidViewModel {

    private MutableLiveData<List<FAQModel>> faqTopicList;
    private MutableLiveData<List<FAQModel>> faqQuestionList;

    public LiveData<List<FAQModel>> getFaqTopics(){
        return faqTopicList;
    }

    public void setFaqTopics(List<FAQModel> faqs){
        this.faqTopicList.setValue(faqs);
    }


    public LiveData<List<FAQModel>> getFaqQuestions(){
        return faqQuestionList;
    }

    public void setFaqQuestions(List<FAQModel> faqs){
        this.faqQuestionList.setValue(faqs);
    }

    public FaqViewModel(@NonNull Application application) {
        super(application);
        faqTopicList = new MutableLiveData<>();
        faqQuestionList = new MutableLiveData<>();
    }
}
