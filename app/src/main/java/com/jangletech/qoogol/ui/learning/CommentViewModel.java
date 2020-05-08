package com.jangletech.qoogol.ui.learning;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.jangletech.qoogol.model.Comments;
import java.util.List;

public class CommentViewModel extends AndroidViewModel {

    private MutableLiveData<List<Comments>> commentList;

    public LiveData<List<Comments>> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comments> commentList) {
        this.commentList.setValue(commentList);
    }

    public CommentViewModel(@NonNull Application application) {
        super(application);
        commentList = new MutableLiveData<>();
    }
}
