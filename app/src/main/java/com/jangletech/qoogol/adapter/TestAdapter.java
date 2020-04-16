package com.jangletech.qoogol.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.TestItemBinding;
import com.jangletech.qoogol.model.TestModel;

import java.util.List;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {

    Fragment fragment;
    List<TestModel> testModelList;
    TestItemBinding itemBinding;
    TestAdapter.TestClickListener testClickListener;

    public TestAdapter(Fragment fragment, List<TestModel> itemlist, TestAdapter.TestClickListener testClickListener) {
        this.fragment = fragment;
        this.testModelList = itemlist;
        this.testClickListener = testClickListener;
    }

    @NonNull
    @Override
    public TestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.test_item, parent, false);
        return new TestAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TestAdapter.ViewHolder holder, int position) {

        TestModel testModel = testModelList.get(position);
        holder.itemBinding.tvTestNameSubject.setText(testModel.getName() + " (" + testModel.getSubject() + ")");
        holder.itemBinding.tvQuestCount.setText(testModel.getQuestionCount());
        holder.itemBinding.tvDuration.setText(testModel.getDuration() + " Mins.");
        holder.itemBinding.tvTotalMarks.setText(testModel.getTotalMarks());

        holder.itemBinding.tvDifficultyLevel.setText(testModel.getDifficultyLevel());
        holder.itemBinding.tvRanking.setText(testModel.getRanking());
        holder.itemBinding.tvAttendedBy.setText(testModel.getAttendedBy());

        holder.itemBinding.tvPublishedDate.setText(testModel.getPublishedDate());
        holder.itemBinding.tvRatingStartCount.setText(testModel.getRatingStarCount());
        holder.itemBinding.tvRatingCount.setText("("+testModel.getRatingCount()+")");

        if (testModel.isNegativeMarks()) {
            holder.itemBinding.tvNegativeMarks.setText(itemBinding.getRoot().getResources().getString(R.string.negative_marks));
        }

        if(testModel.isFavourite()){
            holder.itemBinding.tvFavourite.setChecked(true);
        }


        //holder.itemBinding.tvAuthorNameEdu.setText(testModel.getAuthor()+" ("+testModel.getAuthorEdu()+")");
        //holder.itemBinding.tvCategory.setText(testModel.getCategory());

        holder.itemBinding.testItemCard.setOnClickListener(v -> {
            testClickListener.onTestItemClick(testModel);
        });

        holder.itemBinding.btnStartTest.setOnClickListener(v -> {
            testClickListener.onStartTestClick(testModel);
        });


        holder.itemBinding.tvShare.setOnClickListener(v -> {
            testClickListener.onShareClick(testModel);
        });

        holder.itemBinding.tvDownload.setOnClickListener(v -> {
            testClickListener.onDownloadClick(testModel);
        });
    }

    public interface TestClickListener {
        void onTestItemClick(TestModel testModel);

        void onStartTestClick(TestModel testModel);

        void onShareClick(TestModel testModel);

        void onDownloadClick(TestModel testModel);

        void onFavouriteClick(TestModel testModel);
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

    public void setSearchResult(List<TestModel> result) {
        testModelList = result;
        notifyDataSetChanged();
    }
}
