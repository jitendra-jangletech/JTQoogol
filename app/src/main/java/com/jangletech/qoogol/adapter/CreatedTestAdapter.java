package com.jangletech.qoogol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ItemCreatedTestBinding;
import com.jangletech.qoogol.model.TestModelNew;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.DateUtils;

import java.util.List;

public class CreatedTestAdapter extends RecyclerView.Adapter<CreatedTestAdapter.ViewHolder> {

    private Context mContext;
    private ItemCreatedTestBinding itemCreatedTestBinding;
    private List<TestModelNew> createdTestList;
    private CreatedTestListener listener;

    public CreatedTestAdapter(Context mContext, List<TestModelNew> createdTestList, CreatedTestListener listener) {
        this.mContext = mContext;
        this.createdTestList = createdTestList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemCreatedTestBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_created_test, parent, false);
        return new ViewHolder(itemCreatedTestBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TestModelNew testModelNew = createdTestList.get(position);

        holder.itemCreatedTestBinding.tvTestNameSubject.setText(testModelNew.getTm_name() + "(" + testModelNew.getSm_sub_name() + ")");
        holder.itemCreatedTestBinding.tvCategory.setText(BaseFragment.getDefinedTestCategory(testModelNew.getTm_catg()));
        holder.itemCreatedTestBinding.tvDuration.setText(testModelNew.getTm_duration());
        holder.itemCreatedTestBinding.tvTotalMarks.setText(testModelNew.getTm_tot_marks());
        holder.itemCreatedTestBinding.tvDifficultyLevel.setText(testModelNew.getTm_diff_level());
        holder.itemCreatedTestBinding.tvAttendedBy.setText(testModelNew.getTm_attempted_by());
        holder.itemCreatedTestBinding.tvRanking.setText(testModelNew.getTm_ranking());
        holder.itemCreatedTestBinding.tvQuestCount.setText(testModelNew.getQuest_count());
        holder.itemCreatedTestBinding.tvAuthorName.setText(testModelNew.getAuthor());
        holder.itemCreatedTestBinding.tvRanking.setText(testModelNew.getTm_ranking());
        holder.itemCreatedTestBinding.tvAttendedBy.setText(testModelNew.getTm_attempted_by());
        if (testModelNew.getPublishedDate() != null)
            holder.itemCreatedTestBinding.tvPublishedDate.setText(DateUtils.getFormattedDate(testModelNew.getPublishedDate().substring(0, 10)));

        holder.itemCreatedTestBinding.buttonEdit.setOnClickListener(v -> {
            listener.onEditClick(testModelNew);
        });

        holder.itemCreatedTestBinding.btnValidate.setOnClickListener(v -> {
            listener.onValidateClick(testModelNew);
        });
    }

    @Override
    public int getItemCount() {
        return createdTestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemCreatedTestBinding itemCreatedTestBinding;

        public ViewHolder(@NonNull ItemCreatedTestBinding itemView) {
            super(itemView.getRoot());
            this.itemCreatedTestBinding = itemView;
        }
    }

    public interface CreatedTestListener {
        void onValidateClick(TestModelNew testModelNew);

        void onEditClick(TestModelNew testModelNew);
    }
}
