package com.jangletech.qoogol.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

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
        holder.itemBinding.tvRatingCount.setText("(" + testModel.getRatingCount() + ")");
        holder.itemBinding.likeValue.setText(String.valueOf(testModel.getLikeCount()));

        if (testModel.isLiked()) {
            holder.itemBinding.like.setChecked(true);
        } else {
            holder.itemBinding.like.setChecked(false);
        }

        if (testModel.isFavourite()) {
            itemBinding.favorite.setChecked(true);
        } else {
            itemBinding.favorite.setChecked(false);
        }

        //ToggleButton toggle = (ToggleButton) findViewById(R.id.togglebutton);
        itemBinding.favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    testModel.setFavourite(true);
                } else {
                    testModel.setFavourite(false);
                }
            }
        });


        itemBinding.like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    testModel.setLikeCount(testModel.getLikeCount() + 1);
                    testModel.setLiked(true);
                    //notifyItemChanged(position);
                    testClickListener.onLikeClick(testModel, position);
                } else {
                    //int likeCount = Integer.parseInt(itemBinding.likeValue.getText().toString());
                    testModel.setLikeCount(testModel.getLikeCount() - 1);
                    testModel.setLiked(false);
                    //notifyItemChanged(position);
                    testClickListener.onLikeClick(testModel, position);
                }
            }
        });

        if (testModel.isPaused()) {
            holder.itemBinding.btnStartTest.setText(" Resume Test ");
        }

        if (testModel.isNegativeMarks()) {
            holder.itemBinding.tvNegativeMarks.setText(itemBinding.getRoot().getResources().getString(R.string.negative_marks));
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

    public interface TestClickListener {
        void onTestItemClick(TestModel testModel);

        void onStartTestClick(TestModel testModel);

        void onCommentClick(TestModel testModel);

        void onShareClick(TestModel testModel);

        void onLikeClick(TestModel testModel, int pos);

        void onFavouriteClick(TestModel testModel, boolean isChecked);
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
