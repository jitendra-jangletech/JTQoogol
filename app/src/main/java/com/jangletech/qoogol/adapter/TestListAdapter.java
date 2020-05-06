package com.jangletech.qoogol.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.TestItemBinding;
import com.jangletech.qoogol.model.TestModelNew;
import java.util.HashMap;
import java.util.List;

public class TestListAdapter extends RecyclerView.Adapter<TestListAdapter.ViewHolder> {

    Fragment fragment;
    List<TestModelNew> testModelList;
    TestItemBinding itemBinding;
    TestListAdapter.TestClickListener testClickListener;
    private HashMap<String, String> mapDiffLevel = new HashMap<>();

    public TestListAdapter(Fragment fragment, List<TestModelNew> itemlist, TestListAdapter.TestClickListener testClickListener) {
        this.fragment = fragment;
        this.testModelList = itemlist;
        this.testClickListener = testClickListener;
        setMapValues();
    }

    private void setMapValues() {
        mapDiffLevel.put("M", "Medium");
        mapDiffLevel.put("H", "Hard");
        mapDiffLevel.put("E", "Easy");
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

        TestModelNew testModel = testModelList.get(position);

        itemBinding.tvTestNameSubject.setText(testModel.getTm_name()+"("+testModel.getSm_sub_name()+")");
        itemBinding.tvCategory.setText(testModel.getTm_catg());
        itemBinding.tvDuration.setText(testModel.getTm_duration() + " Min.");
        itemBinding.tvTotalMarks.setText(testModel.getTm_tot_marks());
        itemBinding.tvDifficultyLevel.setText(mapDiffLevel.get(testModel.getTm_diff_level()));
        itemBinding.ratingvalue.setText(testModel.getTm_rating_count());
        itemBinding.tvAttendedBy.setText(testModel.getTm_attempted_by());
        itemBinding.tvRanking.setText(testModel.getTm_ranking());
        itemBinding.tvQuestCount.setText(testModel.getQuest_count());
        itemBinding.likeValue.setText(testModel.getLikeCount());
        itemBinding.shareValue.setText(testModel.getShareCount());
        itemBinding.commentValue.setText(testModel.getCommentsCount());

        itemBinding.tvPublishedDate.setText(testModel.getPublishedDate().substring(0, 10));
        itemBinding.tvAuthorName.setText(testModel.getAuthor());

        if (testModel.isFavourite()) {
            itemBinding.favorite.setChecked(true);
        } else {
            itemBinding.favorite.setChecked(false);
        }

        if (testModel.isFavourite()) {
            itemBinding.like.setChecked(true);
        } else {
            itemBinding.like.setChecked(false);
        }


        holder.itemBinding.testItemCard.setOnClickListener(v -> {
            testClickListener.onTestItemClick(testModel);
        });

        holder.itemBinding.btnStartTest.setOnClickListener(v -> {
            testClickListener.onStartTestClick(testModel);
        });

        /*holder.itemBinding.likeLayout.setOnClickListener(v->{
            testClickListener.onLikeClick(testModel);
        });*/

        holder.itemBinding.commentLayout.setOnClickListener(v -> {
            testClickListener.onCommentClick(testModel);
        });

        holder.itemBinding.shareLayout.setOnClickListener(v -> {
            testClickListener.onShareClick(testModel);
        });

        holder.itemBinding.favorite.setOnClickListener(v -> {
            //Toast.makeText(fragment.getContext(), ""+isSelected, Toast.LENGTH_SHORT).show();
            //testClickListener.onFavouriteClick(testModel, isSelected[0]);
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

        void onShareClick(TestModelNew testModel);

        void onLikeClick(TestModelNew testModel, int pos);

        void onFavouriteClick(TestModelNew testModel, boolean isChecked);
    }

    public void setSearchResult(List<TestModelNew> result) {
        testModelList = result;
        notifyDataSetChanged();
    }
}
