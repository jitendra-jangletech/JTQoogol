package com.jangletech.qoogol.adapter;

import android.app.Activity;
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

public class AddTestQuestionAdapter extends RecyclerView.Adapter<AddTestQuestionAdapter.ViewHolder> {

    private static final String TAG = "AddTestQuestionAdapter";
    private Activity mContext;
    private List<LearningQuestionsNew> learningQuestionsNewList;
    private ItemQuestBinding itemQuestBinding;
    private List<LearningQuestionsNew> tempList;
    private AddTestQuestionListener listener;
    private boolean flag;
    private int sectionPos;

    public AddTestQuestionAdapter(Activity mContext, List<LearningQuestionsNew> learningQuestionsNewList,
                                  boolean flag, AddTestQuestionListener listener) {
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
                    listener.onQuestSelected(tempList);
                }
            });
        } else {
            holder.itemQuestBinding.checkQuest.setVisibility(View.GONE);
            holder.itemQuestBinding.remove.setVisibility(View.VISIBLE);
        }

        holder.itemQuestBinding.remove.setOnClickListener(v -> {
            Log.i(TAG, "onBindViewHolder: ");
            listener.onRemoveClick(learningQuestionsNew, holder.getAdapterPosition());
        });

        /*holder.itemQuestBinding.checkQuest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tempList.add(learningQuestionsNew);
                } else {
                    tempList.remove(learningQuestionsNew);
                }

                listener.onQuestSelected(tempList);
            }
        });*/

        /*holder.itemQuestBinding.checkQuest.setOnClickListener(v -> {
            if (holder.itemQuestBinding.checkQuest.isChecked()) {
                tempList.add(learningQuestionsNew);
            } else {
                tempList.remove(learningQuestionsNew);
            }

            listener.onQuestSelected(tempList);
        });*/
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

        void onRemoveClick(LearningQuestionsNew learningQuestionsNew, int questPos);
    }

    public void deleteTestQuest(int pos, LearningQuestionsNew learningQuestionsNew) {
        learningQuestionsNewList.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, getItemCount());
    }

//    public void addToList(LearningQuestionsNew learningQuestionsNew) {
//        if (!tempList.contains(learningQuestionsNew)) {
//            Log.i(TAG, "Not Added.");
//
//        } else {
//            Log.i(TAG, "Already Added.");
//        }
//    }

    public void updateQuestMarks(){

    }
}
