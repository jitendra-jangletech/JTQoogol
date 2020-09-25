package com.jangletech.qoogol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.FaqItemBinding;
import com.jangletech.qoogol.model.SubjectClass;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {

    private List<SubjectClass> subjects;
    private Context mContext;
    private FaqItemBinding itemBinding;
    private SubjectTileClickListener listener;

    public SubjectAdapter(Context mContext, List<SubjectClass> subjects, SubjectTileClickListener listener) {
        this.subjects = subjects;
        this.mContext = mContext;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SubjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.faq_item, parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectAdapter.ViewHolder holder, int position) {
        SubjectClass subject = subjects.get(position);
        itemBinding.tvTopicName.setText(subject.getSubjectName());
        itemBinding.cardLayout.setOnClickListener(v -> {
            listener.onSubjectSelected(subject);
        });
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private FaqItemBinding itemBinding;

        public ViewHolder(@NonNull FaqItemBinding itemView) {
            super(itemView.getRoot());
            itemBinding = itemView;
        }
    }

    public interface SubjectTileClickListener {
        void onSubjectSelected(SubjectClass subjectClass);
    }
}
