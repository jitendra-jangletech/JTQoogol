package com.jangletech.qoogol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.FaqItemBinding;
import com.jangletech.qoogol.model.FAQModel;

import java.util.List;


public class FAQTilesAdapter extends RecyclerView.Adapter<FAQTilesAdapter.ViewHolder> {

    private List<FAQModel> faqModelList;
    private Context mContext;
    private TilesClickListener tilesClickListener;
    private FaqItemBinding itemBinding;

    public FAQTilesAdapter(List<FAQModel> faqModelList,Context mContext,TilesClickListener tilesClickListener){
        this.mContext = mContext;
        this.tilesClickListener= tilesClickListener;
        this.faqModelList = faqModelList;
    }

    @NonNull
    @Override
    public FAQTilesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        itemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.faq_item,parent,false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull FAQTilesAdapter.ViewHolder holder, int position) {
        FAQModel faqModel = faqModelList.get(position);
        holder.itemBinding.tvTopicName.setText(faqModel.getFaqt_name());
        holder.itemBinding.cardLayout.setOnClickListener(v->{
            tilesClickListener.onItemClick(faqModel);
        });
    }

    @Override
    public int getItemCount() {
        return faqModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        FaqItemBinding itemBinding;
        public ViewHolder(@NonNull FaqItemBinding itemView) {
            super(itemView.getRoot());
            this.itemBinding = itemView;
        }
    }

    public interface TilesClickListener{
        void onItemClick(FAQModel faqModel);
    }
}
