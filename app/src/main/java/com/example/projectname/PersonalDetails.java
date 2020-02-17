package com.example.projectname;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class PersonalDetails extends Fragment {
    private static final String TAG = "PersonalDetails";
    Activity mContext;
    

    FirebaseAuth fa;
    DatabaseReference dr;

    public PersonalDetails(Activity context) {
        mContext = context;
    }

    RecyclerView inviteRecyclerView;
    LinearLayout inviteLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        fa = FirebaseAuth.getInstance();
        dr = FirebaseDatabase.getInstance().getReference();
        final TextView userName = (TextView) view.findViewById(R.id.userName);
        final TextView email = (TextView) view.findViewById(R.id.email);
        final TextView phone = (TextView) view.findViewById(R.id.phone);
        Button signOut = (Button) view.findViewById(R.id.signOut);

        inviteLayout = (LinearLayout) view.findViewById(R.id.inviteLayout);
        inviteRecyclerView = (RecyclerView) view.findViewById(R.id.inviteRecyclerView);
        
        dr.child("Users").child(fa.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserDetails mUserDetails = dataSnapshot.getValue(UserDetails.class);
                userName.setText(mUserDetails.getUserName());
                email.setText(mUserDetails.getUserEmail());
                phone.setText(mUserDetails.getUserPhone());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.toString());
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setCancelable(true);
                alertDialog.setTitle("Are You Sure? Sign-out");

                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fa.signOut();
                        dialog.dismiss();
                        startActivity(new Intent(getContext(), MainActivity.class));
                        mContext.finish();
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                Dialog dialog = alertDialog.create();
                dialog.show();
            }
        });

        

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();

        final List<InviteClass> list = new ArrayList<>();
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
        dr.child("Requests").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()!=0){
                    inviteLayout.setVisibility(View.VISIBLE);
                    list.clear();
                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        list.add(d.getValue(InviteClass.class));
                    }
                    final InviteAdapter adapter = new InviteAdapter(getActivity(), list);
                    adapter.setOnButtonClicked(new InviteAdapter.OnButtonClicked() {
                        @Override
                        public void remove(int position) {
                            list.remove(position);
                            if(list.isEmpty()){
                                inviteLayout.setVisibility(View.GONE);
                            }else {
                                inviteRecyclerView.setAdapter(adapter);
                            }

                        }
                    });
                    inviteRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    inviteRecyclerView.setAdapter(adapter);
                }
                else {
                    inviteLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
