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

public class Section3QuestAdapter extends RecyclerView.Adapter<Section3QuestAdapter.ViewHolder> {

    private static final String TAG = "AddTestQuestionAdapter";
    private Context mContext;
    private List<LearningQuestionsNew> learningQuestionsNewList;
    private ItemQuestBinding itemQuestBinding;
    private List<LearningQuestionsNew> tempList;
    private Section3TestQuestAdapterListener listener;
    private boolean flag;
    private int sectionPos;

    public Section3QuestAdapter(Context mContext, List<LearningQuestionsNew> learningQuestionsNewList,
                                boolean flag, Section3TestQuestAdapterListener listener) {
        this.mContext = mContext;
        this.learningQuestionsNewList = learningQuestionsNewList;
        this.listener = listener;
        this.flag = flag;
        tempList = new ArrayList<>();
    }

    @NonNull
    @Override
    public Section3QuestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemQuestBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_quest, parent, false);
        return new Section3QuestAdapter.ViewHolder(itemQuestBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull Section3QuestAdapter.ViewHolder holder, int position) {
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
                    listener.onSection3QuestSelected(tempList);
                }
            });
        } else {
            holder.itemQuestBinding.checkQuest.setVisibility(View.GONE);
            holder.itemQuestBinding.remove.setVisibility(View.VISIBLE);
            holder.itemQuestBinding.rootLayout.setOnClickListener(v -> {
                listener.onSection3Marks(learningQuestionsNew, holder.getAdapterPosition());
            });
        }

        holder.itemQuestBinding.remove.setOnClickListener(v -> {
            Log.i(TAG, "onBindViewHolder: ");
            listener.onSection3RemoveClick(learningQuestionsNew, holder.getAdapterPosition());
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

    public interface Section3TestQuestAdapterListener {
        void onSection3QuestSelected(List<LearningQuestionsNew> learningQuestionsNewList);

        void onSection3RemoveClick(LearningQuestionsNew learningQuestionsNew, int questPos);
        void onSection3Marks(LearningQuestionsNew learningQuestionsNew, int quesPos);
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
