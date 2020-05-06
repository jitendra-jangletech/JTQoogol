package com.jangletech.qoogol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ItemBoardBinding;
import com.jangletech.qoogol.model.University;

import java.util.List;

public class BoardAdapterNew extends RecyclerView.Adapter<BoardAdapterNew.ViewHolder> {

    private List<University> universityList;
    private Context mContext;
    BoardItemClickListener boardItemClickListener;
    private ItemBoardBinding itemBoardBinding;

    public BoardAdapterNew(Context mContext,List<University> universities,BoardItemClickListener boardItemClickListener){
        this.universityList = universities;
        this.mContext = mContext;
        this.boardItemClickListener = boardItemClickListener;
    }

    @NonNull
    @Override
    public BoardAdapterNew.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        itemBoardBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_board,parent,false);
        return new ViewHolder(itemBoardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BoardAdapterNew.ViewHolder holder, int position) {
        University university = universityList.get(position);
        holder.itemBoardBinding.tvBoard.setText(university.getName());
        holder.itemBoardBinding.rootLayout.setOnClickListener(v->{
            boardItemClickListener.onBoardSelected(universityList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return universityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemBoardBinding itemBoardBinding;
        public ViewHolder(@NonNull ItemBoardBinding itemView) {
            super(itemView.getRoot());
            this.itemBoardBinding = itemView;
        }
    }

    public interface BoardItemClickListener{
        void onBoardSelected(University university);
    }

    public void setBoardItems(List<University> universities) {
        this.universityList = universities;
        notifyDataSetChanged();
    }
}
