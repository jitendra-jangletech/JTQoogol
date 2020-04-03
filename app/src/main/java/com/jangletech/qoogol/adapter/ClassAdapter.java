package com.jangletech.qoogol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.model.Employee;
import com.jangletech.qoogol.ui.syllabus.ClassFragment;

import java.util.ArrayList;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    private Context context;
    private ArrayList<Employee> employees;
    private int checkedPosition = 0;
    Fragment fragment;

    public ClassAdapter(Context context, ArrayList<Employee> employees, Fragment fragment) {
        this.context = context;
        this.employees = employees;
        this.fragment = fragment;
    }

    public void setEmployees(ArrayList<Employee> employees) {
        this.employees = new ArrayList<>();
        this.employees = employees;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (fragment instanceof ClassFragment) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_class, viewGroup, false);
            return new ClassViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_stream, viewGroup, false);
            return new ClassViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder singleViewHolder, int position) {
        singleViewHolder.bind(employees.get(position));
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    class ClassViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private ImageView imageView;

        ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
        }

        void bind(final Employee employee) {
            if (checkedPosition == -1) {
                imageView.setVisibility(View.GONE);
            } else {
                if (checkedPosition == getAdapterPosition()) {
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    imageView.setVisibility(View.GONE);
                }
            }
            textView.setText(employee.getName());

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

    public Employee getSelected() {
        if (checkedPosition != -1) {
            return employees.get(checkedPosition);
        }
        return null;
    }
}
