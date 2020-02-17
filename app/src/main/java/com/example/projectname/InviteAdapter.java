package com.example.projectname;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class InviteAdapter extends RecyclerView.Adapter<InviteAdapter.InviteViewHolder> {

    Activity context;
    List<InviteClass> list;

    public InviteAdapter(Activity context, List<InviteClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public InviteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.invite_item, parent, false);
        return new InviteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InviteViewHolder holder, final int position) {

        holder.projectName.setText(list.get(position).getProjectName());
        holder.companyName.setText(list.get(position).getCompany());


        holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                final DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
                dr.child("Requests").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(list.get(position).getCompany()+ "_" + list.get(position).getProjectName()).removeValue();
                dr.child("ProjectsUnderUsers").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(list.get(position).getCompany()+ "_" + list.get(position).getProjectName()).setValue("Member");
                dr.child("Projects").child(list.get(position).getCompany()+ "_" + list.get(position).getProjectName()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        FormDetails formDetails = dataSnapshot.getValue(FormDetails.class);
                        List<String> memberList;
                        if(formDetails.getMembers()!=null){
                            memberList = formDetails.getMembers();
                        }else {
                            memberList = new ArrayList<>();
                        }
                        memberList.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        memberList = removeDuplicates(memberList);
                        formDetails.setMembers(memberList);
                        dr.child("Projects").child(list.get(position).getCompany()+ "_" + list.get(position).getProjectName()).setValue(formDetails);

                        mOnButtonClicked.remove(position);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        holder.deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
                dr.child("Requests").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(list.get(position).getCompany()+ "_" + list.get(position).getProjectName()).removeValue();
                mOnButtonClicked.remove(position);
            }
        });

    }

    public static <T> List<T> removeDuplicates(List<T> list)
    {

        // Create a new LinkedHashSet
        Set<T> set = new LinkedHashSet<>();

        // Add the elements to set
        set.addAll(list);

        // Clear the list
        list.clear();

        // add the elements of set
        // with no duplicates to the list
        list.addAll(set);

        // return the list
        return list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class InviteViewHolder extends RecyclerView.ViewHolder{
        TextView projectName, companyName;
        ImageButton accept, deny;

        public InviteViewHolder(@NonNull View itemView) {
            super(itemView);

            projectName = (TextView) itemView.findViewById(R.id.projectName);
            companyName = (TextView) itemView.findViewById(R.id.companyName);
            accept = (ImageButton) itemView.findViewById(R.id.accept);
            deny = (ImageButton) itemView.findViewById(R.id.deny);
        }
    }
    OnButtonClicked mOnButtonClicked;
    public interface OnButtonClicked{
        void remove(int position);
    }
    public void setOnButtonClicked(OnButtonClicked onButtonClicked){
        mOnButtonClicked = onButtonClicked;
    }
}
