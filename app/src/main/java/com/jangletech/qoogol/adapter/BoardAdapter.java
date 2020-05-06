package com.jangletech.qoogol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.model.University;
import java.util.List;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardViewHolder> {

    private Context context;
    private List<University> boardItems;
    private int checkedPosition = 0;
    BoardItemClickListener boardItemClickListener;


    public BoardAdapter(Context context, List<University> boardItems,BoardItemClickListener boardItemClickListener) {
        this.context = context;
        this.boardItems = boardItems;
        this.boardItemClickListener = boardItemClickListener;
    }

    public void setBoardItems(List<University> boardItems) {
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
        private RelativeLayout rootLayout;

        BoardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBoardName = itemView.findViewById(R.id.tvBoardName);
            tvBoard = itemView.findViewById(R.id.tvBoard);
            imageView = itemView.findViewById(R.id.imageView);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }

        void bind(final University boardItem) {
            if (checkedPosition == -1) {
                imageView.setVisibility(View.GONE);
            } else {
                if (checkedPosition == getAdapterPosition()) {
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    imageView.setVisibility(View.GONE);
                }
            }
            //tvBoardName.setText(boardItem.getName());
            tvBoard.setText(boardItem.getName());

            rootLayout.setOnClickListener(v->{
                boardItemClickListener.onBoardSelected(boardItems.get(getAdapterPosition()));
            });

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

    public interface BoardItemClickListener{
        void onBoardSelected(University university);
    }

    public University getSelected() {
        if (checkedPosition != -1) {
            return boardItems.get(checkedPosition);
        }
        return null;
    }
}

