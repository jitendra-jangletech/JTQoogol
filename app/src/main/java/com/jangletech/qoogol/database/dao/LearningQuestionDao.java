package com.jangletech.qoogol.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.jangletech.qoogol.model.LearningQuestions;
import com.jangletech.qoogol.model.LearningQuestionsNew;

import java.util.List;

/**
 * Created by Pritali on 4/23/2020.
 */
@Dao
public interface LearningQuestionDao {

    @Query("SELECT * FROM LearningQuestionsNew")
    LiveData<List<LearningQuestionsNew>> getAllQuestions();


//    @Query("SELECT * FROM LearningQuestionsNew where (:qtrending!=null AND trending=:qtrending) OR (:qpopular!=null AND popular=:qpopular) OR (:qrecent!=null AND recent=:qrecent) " +
//            "OR (:ratings!=null AND rating>=:ratings) OR (:short_ans!=null AND type=:short_ans) OR (:long_ans!=null AND type=:long_ans)" +
//            "OR (:fill_the_blanks!=null AND type=:fill_the_blanks) OR (:scq!=null AND que_option_type=:scq) OR (:mcq!=null AND que_option_type=:mcq) " +
//            "OR (:true_false!=null AND que_option_type=:true_false) OR (:match_pair!=null AND que_option_type=:match_pair)")
//    LiveData<List<LearningQuestionsNew>> getAllQuestions(String qtrending, String qpopular, String qrecent, String ratings,
//                                                         String short_ans, String long_ans,String fill_the_blanks,
//                                                         String scq, String mcq, String true_false, String match_pair);

    @Query("SELECT * FROM LearningQuestionsNew where question_id=:q_id")
    LiveData<List<LearningQuestionsNew>> getQuestion(String q_id);

    @Query("SELECT * FROM LearningQuestionsNew where is_fav=:fav")
    LiveData<List<LearningQuestionsNew>> getFavQuestions(String fav);

    @Query("SELECT * FROM LearningQuestionsNew where fetchCASE=:fetchCase")
    LiveData<List<LearningQuestionsNew>> getSharedQuestions(String fetchCase);

    @Query("SELECT * FROM LearningQuestions")
    LiveData<List<LearningQuestions>> getAllSavedQuestions();

    @Query("SELECT * FROM LearningQuestionsNew")
    List<LearningQuestionsNew> getQuestions();

    @Query("SELECT * FROM LearningQuestions")
    List<LearningQuestions> getSavedQuestions();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQuestions(List<LearningQuestionsNew> learningQuestions);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSavedQuestions(List<LearningQuestions> learningQuestions);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQuestion(LearningQuestions learningQuestions);

    @Query("delete from LearningQuestions where question_id=:questionId")
    void deleteQuestion(int questionId);



    @Query("update LearningQuestions set is_fav=:fav_flag  where question_id=:questionId")
    void updateQuestion(int questionId, String fav_flag);


    @Delete
    void delete(List<LearningQuestionsNew> learningQuestions);

    @Update
    void updateQuestions(List<LearningQuestionsNew> learningQuestions);

    @Transaction
    default void upsertQuestions(List<LearningQuestionsNew> messageList) {
        insertQuestions(messageList);
        updateQuestions(messageList);
    }
}
