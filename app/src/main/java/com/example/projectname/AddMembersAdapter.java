package com.example.projectname;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AddMembersAdapter extends RecyclerView.Adapter<AddMembersAdapter.AddMembersViewHolder> {

    Context context;
    List<UserDetails> userDetailsList;
    OnAddItemClickListener mOnAddItemClickListener;

    public AddMembersAdapter(Context context, List<UserDetails> userDetailsList) {
        this.context = context;
        this.userDetailsList = userDetailsList;
    }

    @NonNull
    @Override
    public AddMembersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.member_item, parent, false);

        return new AddMembersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddMembersViewHolder holder, final int position) {

        holder.name.setText(userDetailsList.get(position).getUserName());
        holder.email.setText(userDetailsList.get(position).getUserEmail());

        holder.requestAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnAddItemClickListener.requestInvite(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userDetailsList.size();
    }

    class AddMembersViewHolder extends RecyclerView.ViewHolder{
        TextView name, email;
        ImageButton requestAdd;

        public AddMembersViewHolder(@NonNull View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            email = (TextView) itemView.findViewById(R.id.email);
            requestAdd = (ImageButton) itemView.findViewById(R.id.requestAdd);

        }
    }

    public interface OnAddItemClickListener {
        void requestInvite(int position);
    }

    public void setOnAddItemClickListener(OnAddItemClickListener onAddItemClickListener){
        mOnAddItemClickListener = onAddItemClickListener;
    }


}
