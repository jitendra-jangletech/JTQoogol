package com.jangletech.qoogol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ItemAttemptHistoryBinding;
import com.jangletech.qoogol.model.AttemptedTest;
import com.jangletech.qoogol.util.DateUtils;
import java.util.List;

public class AttemptHistoryAdapter extends RecyclerView.Adapter<AttemptHistoryAdapter.ViewHolder> {

    private Context mContext;
    private List<AttemptedTest> attemptedTests;
    private AttemptedTestClickListener attemptedTestClickListener;
    private ItemAttemptHistoryBinding itemBinding;

    public AttemptHistoryAdapter(Context mContext, List<AttemptedTest> attemptedTests, AttemptedTestClickListener attemptedTestClickListener) {
        this.mContext = mContext;
        this.attemptedTests = attemptedTests;
        this.attemptedTestClickListener = attemptedTestClickListener;
    }

    @NonNull
    @Override
    public AttemptHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_attempt_history, parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AttemptHistoryAdapter.ViewHolder holder, int position) {
        AttemptedTest attemptedTest = attemptedTests.get(position);

        holder.itemAttemptHistoryBinding.tvTtDate.setText(DateUtils.localeDateFormat(attemptedTest.getTt_cdatetime()));
        holder.itemAttemptHistoryBinding.tvPauseDate.setText(DateUtils.localeDateFormat(attemptedTest.getTt_pause_datetime()));
        holder.itemAttemptHistoryBinding.tvObtainMarks.setText(attemptedTest.getTt_obtain_marks());
        holder.itemAttemptHistoryBinding.tvStatus.setText(attemptedTest.getTt_status());
        holder.itemAttemptHistoryBinding.tvPauseDuration.setText(attemptedTest.getTt_duration_taken());

        holder.itemAttemptHistoryBinding.rootLayout.setOnClickListener(v->{
            attemptedTestClickListener.onItemClick(attemptedTest);
        });

    }

    @Override
    public int getItemCount() {
        return attemptedTests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemAttemptHistoryBinding itemAttemptHistoryBinding;

        public ViewHolder(@NonNull ItemAttemptHistoryBinding itemView) {
            super(itemView.getRoot());
            this.itemAttemptHistoryBinding = itemView;
        }
    }

    public interface AttemptedTestClickListener {
        void onItemClick(AttemptedTest attemptedTest);
    }
}
