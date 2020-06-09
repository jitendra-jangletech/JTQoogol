package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.TestItemBinding;
import com.jangletech.qoogol.model.TestModelNew;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.DateUtils;

import java.util.HashMap;
import java.util.List;

public class TestListAdapter extends RecyclerView.Adapter<TestListAdapter.ViewHolder> {

    private static final String TAG = "TestListAdapter";
    Activity activity;
    HashMap<Integer, TextView> map;
    List<TestModelNew> testModelList;
    ApiInterface apiService = ApiClient.getInstance().getApi();
    TestItemBinding itemBinding;
    TestListAdapter.TestClickListener testClickListener;
    private HashMap<String, String> mapDiffLevel = new HashMap<>();

    public TestListAdapter(Activity activity, List<TestModelNew> itemlist, TestListAdapter.TestClickListener testClickListener) {
        this.activity = activity;
        this.testModelList = itemlist;
        this.testClickListener = testClickListener;
        setMapValues();
    }

    private void setMapValues() {
        mapDiffLevel.put("M", "Medium");
        mapDiffLevel.put("H", "Hard");
        mapDiffLevel.put("E", "Easy");
        map = new HashMap<>();
    }

    @NonNull
    @Override
    public TestListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.test_item, parent, false);
        return new TestListAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TestListAdapter.ViewHolder holder, int position) {
        //TestModelNew testModel = testModelList.get(position);
        //map.put(position, itemBinding.likeValue);
        itemBinding.tvTestNameSubject.setText(testModelList.get(holder.getAdapterPosition()).getTm_name() + "(" + testModelList.get(holder.getAdapterPosition()).getSm_sub_name() + ")");
        itemBinding.tvCategory.setText(testModelList.get(holder.getAdapterPosition()).getTm_catg());
        itemBinding.tvDuration.setText(testModelList.get(holder.getAdapterPosition()).getTm_duration() + " Min.");
        itemBinding.tvTotalMarks.setText(testModelList.get(holder.getAdapterPosition()).getTm_tot_marks());
        itemBinding.tvDifficultyLevel.setText(mapDiffLevel.get(testModelList.get(holder.getAdapterPosition()).getTm_diff_level()));
        itemBinding.ratingvalue.setText(testModelList.get(holder.getAdapterPosition()).getTm_rating_count());
        itemBinding.tvAttendedBy.setText(testModelList.get(holder.getAdapterPosition()).getTm_attempted_by());
        itemBinding.tvRanking.setText(testModelList.get(holder.getAdapterPosition()).getTm_ranking());
        itemBinding.tvQuestCount.setText(testModelList.get(holder.getAdapterPosition()).getQuest_count());

        if (testModelList.get(holder.getAdapterPosition()).getAttemptedTests() != null)
            itemBinding.tvNoOfAttempts.setText(Html.fromHtml("<h>Attempts : " + testModelList.get(holder.getAdapterPosition()).getAttemptedTests().size() + "</h>"));

        itemBinding.tvNoOfAttempts.setOnClickListener(v -> {
            testClickListener.onAttemptsClick(testModelList.get(holder.getAdapterPosition()));
        });

        Log.e(TAG, "Like Count : " + testModelList.get(holder.getAdapterPosition()).getLikeCount());

        if (testModelList.get(holder.getAdapterPosition()).getLikeCount() != null) {
            itemBinding.likeValue.setText(testModelList.get(holder.getAdapterPosition()).getLikeCount());
        } else {
            itemBinding.likeValue.setText("0");
        }

        if (testModelList.get(holder.getAdapterPosition()).getShareCount() != null) {
            itemBinding.shareValue.setText(testModelList.get(holder.getAdapterPosition()).getShareCount());
        } else {
            itemBinding.shareValue.setText("0");
        }

        if (testModelList.get(holder.getAdapterPosition()).getCommentsCount() != null) {
            itemBinding.commentValue.setText(testModelList.get(holder.getAdapterPosition()).getCommentsCount());
        } else {
            itemBinding.commentValue.setText("0");
        }

        // Log.d(TAG, "Share Count : "+testModel.getShareCount());
        //Log.d(TAG, "Comment Count : "+testModel.getCommentsCount());

        itemBinding.shareValue.setText(testModelList.get(holder.getAdapterPosition()).getShareCount());
        itemBinding.commentValue.setText(testModelList.get(holder.getAdapterPosition()).getCommentsCount());

        if (testModelList.get(holder.getAdapterPosition()).getPublishedDate() != null)
            itemBinding.tvPublishedDate.setText(DateUtils.getFormattedDate(testModelList.get(holder.getAdapterPosition()).getPublishedDate().substring(0, 10)));

        itemBinding.tvAuthorName.setText(testModelList.get(holder.getAdapterPosition()).getAuthor());

        if (testModelList.get(holder.getAdapterPosition()).isFavourite()) {
            itemBinding.favorite.setChecked(true);
        } else {
            itemBinding.favorite.setChecked(false);
        }

        if (testModelList.get(holder.getAdapterPosition()).isLike()) {
            itemBinding.like.setChecked(true);
        } else {
            itemBinding.like.setChecked(false);
        }

        holder.itemBinding.testItemCard.setOnClickListener(v -> {
            testClickListener.onTestItemClick(testModelList.get(holder.getAdapterPosition()));
        });

        holder.itemBinding.btnStartTest.setOnClickListener(v -> {
            testClickListener.onStartTestClick(testModelList.get(holder.getAdapterPosition()));
        });

        holder.itemBinding.like.setOnClickListener(v->{
            testClickListener.onLikeClick(testModelList.get(holder.getAdapterPosition()),holder.getAdapterPosition(),testModelList.get(holder.getAdapterPosition()).isLike());
        });

        /*holder.itemBinding.like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                testClickListener.onLikeClick(testModel,holder.getAdapterPosition(), isChecked);
            }
        });*/

        /*holder.itemBinding.like.setOnClickListener(v -> {
            boolean isChecked = testModel.isLike();
            if (isChecked) {
                doLikeTest(1, position, testModel);
            } else {
                doLikeTest(0, position, testModel);
            }
            //testClickListener.onLikeClick(testModel, position,isChecked);
        });*/

        holder.itemBinding.commentLayout.setOnClickListener(v -> {
            testClickListener.onCommentClick(testModelList.get(holder.getAdapterPosition()));
        });

        holder.itemBinding.shareLayout.setOnClickListener(v -> {
            testClickListener.onShareClick(testModelList.get(holder.getAdapterPosition()).getTm_id());
        });

       /* holder.itemBinding.favorite.setOnClickListener(v -> {
            //Toast.makeText(fragment.getContext(), ""+isSelected, Toast.LENGTH_SHORT).show();
            //testClickListener.onFavouriteClick(testModel, isSelected[0]);
        });*/

        holder.itemBinding.favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                testClickListener.onFavouriteClick(testModelList.get(holder.getAdapterPosition()), isChecked,holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return testModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TestItemBinding itemBinding;

        public ViewHolder(@NonNull TestItemBinding itemView) {
            super(itemView.getRoot());
            this.itemBinding = itemView;
        }
    }

    public interface TestClickListener {
        void onTestItemClick(TestModelNew testModel);

        void onStartTestClick(TestModelNew testModel);

        void onCommentClick(TestModelNew testModel);

        void onShareClick(int testid);

        void onLikeClick(TestModelNew testModel, int pos, boolean isChecked);

        void onFavouriteClick(TestModelNew testModel, boolean isChecked,int position);

        void onAttemptsClick(TestModelNew testModel);
    }

    public void updateList(List<TestModelNew> list) {
        this.testModelList = list;
        notifyDataSetChanged();
    }

    public void setSearchResult(List<TestModelNew> result) {
        testModelList = result;
        notifyDataSetChanged();
        for (TestModelNew testModelNew : testModelList) {
            Log.d(TAG, "isLike " + testModelNew.isLike());
            Log.d(TAG, "Like Count " + testModelNew.getLikeCount());
        }
    }

    public void deleteFav(int pos){
        testModelList.remove(pos);
        notifyItemRemoved(pos);
    }
}
