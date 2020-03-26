package com.jangletech.qoogol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.model.BoardItem;

import java.util.ArrayList;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardViewHolder> {

    private Context context;
    private ArrayList<BoardItem> boardItems;
    private int checkedPosition = 0;


    public BoardAdapter(Context context, ArrayList<BoardItem> boardItems) {
        this.context = context;
        this.boardItems = boardItems;
    }

    public void setBoardItems(ArrayList<BoardItem> boardItems) {
        this.boardItems = new ArrayList<>();
        this.boardItems = boardItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BoardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_board, viewGroup, false);
        return new BoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BoardViewHolder singleViewHolder, int position) {
        singleViewHolder.bind(boardItems.get(position));
    }

    @Override
    public int getItemCount() {
        return boardItems.size();
    }

    class BoardViewHolder extends RecyclerView.ViewHolder {

        private TextView tvBoardName;
        private TextView tvBoard;
        private ImageView imageView;

        BoardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBoardName = itemView.findViewById(R.id.tvBoardName);
            tvBoard = itemView.findViewById(R.id.tvBoard);
            imageView = itemView.findViewById(R.id.imageView);
        }

        void bind(final BoardItem boardItem) {
            if (checkedPosition == -1) {
                imageView.setVisibility(View.GONE);
            } else {
                if (checkedPosition == getAdapterPosition()) {
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    imageView.setVisibility(View.GONE);
                }
            }
            tvBoardName.setText(boardItem.getBoardName());
            tvBoard.setText(boardItem.getBoard());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imageView.setVisibility(View.VISIBLE);
                    if (checkedPosition != getAdapterPosition()) {
                        notifyItemChanged(checkedPosition);
                        checkedPosition = getAdapterPosition();
                    }
                }
            });
        }
    }

    public BoardItem getSelected() {
        if (checkedPosition != -1) {
            return boardItems.get(checkedPosition);
        }
        return null;
    }
}

