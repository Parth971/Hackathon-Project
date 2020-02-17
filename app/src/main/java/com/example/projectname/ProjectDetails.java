package com.example.projectname;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ProjectDetails extends Fragment {

    Activity mcontext;
    FormDetails mFormDetails;
    ProjectDetails(Activity context, FormDetails formDetails) {
        mcontext = context;
        mFormDetails = formDetails;
    }



    RecyclerView membersRecyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_project_details, container, false);

        ImageView projectImage = view.findViewById(R.id.projectImage);
        TextView projectName = view.findViewById(R.id.projectName);
        TextView company = view.findViewById(R.id.company);
        TextView projectDescription = view.findViewById(R.id.projectDescription);
        TextView date = view.findViewById(R.id.date);

        membersRecyclerView = (RecyclerView) view.findViewById(R.id.membersRecyclerView);

        final ImageButton add = (ImageButton) view.findViewById(R.id.add);

        if(mFormDetails.getProjectImage()!=null){
            projectImage.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(mFormDetails.getProjectImage())
                    .into(projectImage);
        }
        projectName.setText(mFormDetails.getProjectName());
        company.setText(mFormDetails.getCompany());
        projectDescription.setText(mFormDetails.getProjectDescription());
        date.setText(mFormDetails.getDate());

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
        dr.child("ProjectsUnderUsers").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mFormDetails.getCompany() + "_" + mFormDetails.getProjectName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class).equals("Admin")){
                    add.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                View view1 = getLayoutInflater().inflate(R.layout.all_members_search, null);

                final RecyclerView allMembersRecyclerView = (RecyclerView) view1.findViewById(R.id.allMembersRecyclerView);

                final List<UserDetails> list = new ArrayList<>();
                final List<String> keys = new ArrayList<>();
                DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
                dr.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        list.clear();
                        keys.clear();
                        for(DataSnapshot d : dataSnapshot.getChildren()){
                            if(d.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                continue;
                            }
                            list.add(d.getValue(UserDetails.class));
                            keys.add(d.getKey());
                        }
                        AddMembersAdapter adapter = new AddMembersAdapter(getActivity(), list);
                        adapter.setOnAddItemClickListener(new AddMembersAdapter.OnAddItemClickListener() {
                            @Override
                            public void requestInvite(int position) {
                                DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
                                dr.child("Requests").child(keys.get(position)).child(mFormDetails.getCompany()+ "_" + mFormDetails.getProjectName())
                                        .setValue(new InviteClass(mFormDetails.getProjectName(),
                                                mFormDetails.getCompany()));
                                Toast.makeText(getContext(), "Invite Sent", Toast.LENGTH_SHORT).show();
                            }
                        });
                        allMembersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        allMembersRecyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                alert.setView(view1);
                alert.setCancelable(true);
                alert.show();
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


        DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
        dr.child("Projects").child(mFormDetails.getCompany() + "_" + mFormDetails.getProjectName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> list = new ArrayList<>();
                if(dataSnapshot.getValue(FormDetails.class).getMembers()!=null){
                    list = dataSnapshot.getValue(FormDetails.class).getMembers();
                }
                MembersAdapter adapter = new MembersAdapter(getActivity(), list);
                membersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                membersRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
