package com.example.projectname;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyProjectsAdapter extends RecyclerView.Adapter<MyProjectsAdapter.MyProjectsViewHolder> {

    Activity context;
    List<FormDetails> list;

    public MyProjectsAdapter(Activity context, List<FormDetails> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyProjectsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_project_item, parent, false);
        return new MyProjectsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyProjectsViewHolder holder, final int position) {
        holder.projectName.setText(list.get(position).getProjectName());
        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProjectClickListener.projectClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyProjectsViewHolder extends RecyclerView.ViewHolder {
        TextView projectName;
        View mItemView;

        public MyProjectsViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView;
            projectName = (TextView) itemView.findViewById(R.id.projectName);
        }
    }

    public interface ProjectClickListener{
        void projectClicked(int position);
    }
    ProjectClickListener mProjectClickListener;
    public void setOnProjectClickListener(ProjectClickListener projectClickListener){
        mProjectClickListener = projectClickListener;
    }

}
