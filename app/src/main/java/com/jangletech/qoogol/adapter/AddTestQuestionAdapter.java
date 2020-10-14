package com.jangletech.qoogol.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ItemQuestBinding;
import com.jangletech.qoogol.model.LearningQuestionsNew;

import java.util.ArrayList;
import java.util.List;

public class AddTestQuestionAdapter extends RecyclerView.Adapter<AddTestQuestionAdapter.ViewHolder> {

    private static final String TAG = "AddTestQuestionAdapter";
    private Context mContext;
    private List<LearningQuestionsNew> learningQuestionsNewList;
    private ItemQuestBinding itemQuestBinding;
    private List<LearningQuestionsNew> tempList;
    private AddTestQuestionListener listener;
    private boolean flag;
    private int sectionPos;

    public AddTestQuestionAdapter(Context mContext, List<LearningQuestionsNew> learningQuestionsNewList,
                                  boolean flag, int sectionPos, AddTestQuestionListener listener) {
        this.mContext = mContext;
        this.learningQuestionsNewList = learningQuestionsNewList;
        this.listener = listener;
        this.flag = flag;
        this.sectionPos = sectionPos;
        tempList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemQuestBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_quest, parent, false);
        return new AddTestQuestionAdapter.ViewHolder(itemQuestBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LearningQuestionsNew learningQuestionsNew = learningQuestionsNewList.get(position);
        holder.itemQuestBinding.tvQuest.setText(learningQuestionsNew.getQuestion());

        if (flag) {
            holder.itemQuestBinding.remove.setVisibility(View.GONE);
            holder.itemQuestBinding.checkQuest.setVisibility(View.VISIBLE);
        } else {
            holder.itemQuestBinding.checkQuest.setVisibility(View.GONE);
            holder.itemQuestBinding.remove.setVisibility(View.VISIBLE);
        }

        holder.itemQuestBinding.remove.setOnClickListener(v -> {
            Log.i(TAG, "onBindViewHolder: ");
            listener.onRemoveClick(learningQuestionsNew, sectionPos, holder.getAdapterPosition());
        });

        holder.itemQuestBinding.checkQuest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tempList.add(learningQuestionsNew);
                listener.onQuestSelected(tempList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return learningQuestionsNewList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemQuestBinding itemQuestBinding;

        public ViewHolder(@NonNull ItemQuestBinding itemView) {
            super(itemView.getRoot());
            this.itemQuestBinding = itemView;
        }
    }

    public interface AddTestQuestionListener {
        void onQuestSelected(List<LearningQuestionsNew> learningQuestionsNewList);

        void onRemoveClick(LearningQuestionsNew learningQuestionsNew, int sectionPos, int questPos);
    }
}
