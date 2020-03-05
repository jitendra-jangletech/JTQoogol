package com.jangletech.qoogol.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;

public class QuestionViewHolder extends RecyclerView.ViewHolder {

    final View rootView;
    final TextView tvQuestNo;
    final TextView tvQuestDesc;

    QuestionViewHolder(@NonNull View view) {
        super(view);
        rootView = view;
        tvQuestNo = view.findViewById(R.id.tv_quest_no);
        tvQuestDesc = view.findViewById(R.id.tv_quest_desc);
    }
}
