package com.jangletech.qoogol.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.model.Question;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class QuestionSection extends Section {

    private final String title;
    private final List<Question> list;


    public QuestionSection(@NonNull final String title, @NonNull final List<Question> list) {

        super(SectionParameters.builder()
                .itemResourceId(R.layout.item_question)
                .headerResourceId(R.layout.item_question_header)
                .build());

        this.title = title;
        this.list = list;
    }

    @Override
    public int getContentItemsTotal() {
        return list.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(final View view) {
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final QuestionViewHolder itemHolder = (QuestionViewHolder) holder;

        final Question question = list.get(position);

        itemHolder.tvQuestNo.setText(""+question.getId());
        itemHolder.tvQuestDesc.setText(question.getStrQuestion());

        /*itemHolder.rootView.setOnClickListener(v ->
                clickListener.onItemRootViewClicked(title, itemHolder.getAdapterPosition())
        );*/
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(final View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(final RecyclerView.ViewHolder holder) {
        final HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

        headerHolder.tvTitle.setText(title);
    }
}
