package com.example.projectname;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MembersViewHolder> {

    Activity context;
    List<String> list;

    public MembersAdapter(Activity context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MembersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.project_member_item, parent, false);
        return new MembersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MembersViewHolder holder, int position) {

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
        dr.child("Users").child(list.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.member.setText(dataSnapshot.getValue(UserDetails.class).getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MembersViewHolder extends RecyclerView.ViewHolder{
        TextView member;

        public MembersViewHolder(@NonNull View itemView) {
            super(itemView);
            member = (TextView) itemView.findViewById(R.id.member);
        }
    }

}
