package com.jangletech.qoogol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ItemTestSectionBinding;
import com.jangletech.qoogol.model.TestSection;

import java.util.List;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.ViewHolder> {

    private List<TestSection> testSections;
    private Context mContext;
    private ItemTestSectionBinding itemTestSectionBinding;
    private AddTestQuestionAdapter addTestQuestionAdapter;
    private SectionClickListener listener;

    public SectionAdapter(Context mContext, List<TestSection> testSections, SectionClickListener listener) {
        this.mContext = mContext;
        this.testSections = testSections;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemTestSectionBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_test_section, parent, false);
        return new SectionAdapter.ViewHolder(itemTestSectionBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionAdapter.ViewHolder holder, int position) {
        TestSection testSection = testSections.get(position);
        holder.itemTestSectionBinding.tvNameMarks.setText(testSection.getSectionName() + " (" + testSection.getSectionMarks() + " Marks)");

        if (testSection.getSectionQuestions().size() > 0) {
            addTestQuestionAdapter = new AddTestQuestionAdapter(mContext, testSection.getSectionQuestions());
            holder.itemTestSectionBinding.questRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            holder.itemTestSectionBinding.questRecyclerView.setAdapter(addTestQuestionAdapter);
        }
    }


    @Override
    public int getItemCount() {
        return testSections.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemTestSectionBinding itemTestSectionBinding;

        public ViewHolder(@NonNull ItemTestSectionBinding itemView) {
            super(itemView.getRoot());
            this.itemTestSectionBinding = itemView;
        }
    }

    public void addSectionToList(List<TestSection> testSectionList) {
        this.testSections = testSectionList;
        notifyItemInserted(testSections.size() - 1);
    }

    public interface SectionClickListener {
        void onAddQuestionClick(TestSection testSection);
    }
}
