package com.example.projectname;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment {
    private static final String TAG = "Home";

    RecyclerView myProjects;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        myProjects = (RecyclerView) v.findViewById(R.id.myProjects);
        ImageButton addProject = (ImageButton) v.findViewById(R.id.addProject);

        addProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddProject.class));
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        final List<FormDetails> list = new ArrayList<>();

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading...");
        progressDialog.show();

        final DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth fa = FirebaseAuth.getInstance();


        dr.child("ProjectsUnderUsers").child(fa.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    dr.child("Projects").child(d.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            FormDetails f = dataSnapshot.getValue(FormDetails.class);
                            list.add(f);

                            MyProjectsAdapter adapter = new MyProjectsAdapter(getActivity(), list);
                            adapter.setOnProjectClickListener(new MyProjectsAdapter.ProjectClickListener() {
                                @Override
                                public void projectClicked(int position) {
                                    Intent i = new Intent(getContext(), ProjectDashBoard.class);
                                    i.putExtra("FormDetails", list.get(position));
                                    startActivity(i);
                                }
                            });
                            myProjects.setLayoutManager(new LinearLayoutManager(getContext()));
                            myProjects.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d(TAG, "onCancelled: " + databaseError.toString());
                        }
                    });
//
                }
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
