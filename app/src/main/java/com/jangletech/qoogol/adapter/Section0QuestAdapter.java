package com.jangletech.qoogol.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ItemQuestBinding;
import com.jangletech.qoogol.model.LearningQuestionsNew;
import com.jangletech.qoogol.util.AppUtils;

import java.util.ArrayList;
import java.util.List;

public class Section0QuestAdapter extends RecyclerView.Adapter<Section0QuestAdapter.ViewHolder> {

    private static final String TAG = "AddTestQuestionAdapter";
    private Context mContext;
    private List<LearningQuestionsNew> learningQuestionsNewList;
    private ItemQuestBinding itemQuestBinding;
    private List<LearningQuestionsNew> tempList;
    private Section0TestQuestAdapterListener listener;
    private boolean flag;

    public Section0QuestAdapter(Context mContext, List<LearningQuestionsNew> learningQuestionsNewList,
                                boolean flag, Section0TestQuestAdapterListener listener) {
        this.mContext = mContext;
        this.learningQuestionsNewList = learningQuestionsNewList;
        this.listener = listener;
        this.flag = flag;
        tempList = new ArrayList<>();
    }

    @NonNull
    @Override
    public Section0QuestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemQuestBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_quest, parent, false);
        return new Section0QuestAdapter.ViewHolder(itemQuestBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull Section0QuestAdapter.ViewHolder holder, int position) {
        LearningQuestionsNew learningQuestionsNew = learningQuestionsNewList.get(position);
        Log.i(TAG, "onBindViewHolder : " + learningQuestionsNew.getQuestion_id() + "," + learningQuestionsNew.getQuestion() + "," + learningQuestionsNew.getMarks());

        holder.itemQuestBinding.tvQuestSerial.setText(String.valueOf(learningQuestionsNew.getQuestion_id()));
        holder.itemQuestBinding.tvQuest.setText(AppUtils.decodedString(learningQuestionsNew.getQuestion()));
        holder.itemQuestBinding.tvQuestMarks.setText("Marks(" + learningQuestionsNew.getMarks() + ")");

        if (flag) {
            holder.itemQuestBinding.remove.setVisibility(View.GONE);
            //holder.itemQuestBinding.checkQuest.setVisibility(View.VISIBLE);
            holder.itemQuestBinding.rootLayout.setBackgroundColor(learningQuestionsNew.isSelected() ? Color.CYAN : Color.WHITE);
            holder.itemQuestBinding.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    learningQuestionsNew.setSelected(!learningQuestionsNew.isSelected());
                    holder.itemQuestBinding.rootLayout.setBackgroundColor(learningQuestionsNew.isSelected() ? Color.CYAN : Color.WHITE);
                    tempList.add(learningQuestionsNew);
                    listener.onSection0QuestSelected(tempList);
                }
            });
        } else {
            holder.itemQuestBinding.checkQuest.setVisibility(View.GONE);
            holder.itemQuestBinding.remove.setVisibility(View.VISIBLE);
            holder.itemQuestBinding.rootLayout.setOnClickListener(v -> {
                listener.onSection0MarksClick(learningQuestionsNew, holder.getAdapterPosition());
            });
        }

        holder.itemQuestBinding.remove.setOnClickListener(v -> {
            Log.i(TAG, "onBindViewHolder: ");
            listener.onSection0RemoveClick(learningQuestionsNew, holder.getAdapterPosition());
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

    public interface Section0TestQuestAdapterListener {
        void onSection0QuestSelected(List<LearningQuestionsNew> learningQuestionsNewList);

        void onSection0RemoveClick(LearningQuestionsNew learningQuestionsNew, int questPos);

        void onSection0MarksClick(LearningQuestionsNew learningQuestionsNew, int questPos);
    }

    public void deleteTestQuest(int pos, LearningQuestionsNew learningQuestionsNew) {
        learningQuestionsNewList.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, getItemCount());
    }

    public void updateMarks(LearningQuestionsNew item, int pos) {
        learningQuestionsNewList.set(pos, item);
        notifyItemChanged(pos);
    }
}
