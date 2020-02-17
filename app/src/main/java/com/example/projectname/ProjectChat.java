package com.example.projectname;

import android.app.Activity;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class ProjectChat extends Fragment {

    Activity mcontext;
    FormDetails mFormDetails;
    ProjectChat(Activity context, FormDetails formDetails ) {
        mcontext = context;
        mFormDetails = formDetails;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    RecyclerView chatRecyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_chat, container, false);

        final EditText message = (EditText) view.findViewById(R.id.message);
        Button sendMsg = (Button) view.findViewById(R.id.sendMsg);
        chatRecyclerView = (RecyclerView) view.findViewById(R.id.chatRecyclerView);

        final List<ChatClass> chatClassList = new ArrayList<>();
        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(message.getText().toString().equals("")){
                    Toast.makeText(mcontext, "Enter message", Toast.LENGTH_SHORT).show();
                    return;
                }
                final DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
                dr.child("Chats").child(mFormDetails.getCompany()+"_"+mFormDetails.getProjectName()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        chatClassList.clear();
                        for(DataSnapshot d : dataSnapshot.getChildren()){
                            chatClassList.add(d.getValue(ChatClass.class));
                        }
                        chatClassList.add(new ChatClass(
                                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                "MSG",
                                message.getText().toString(),
                                getCurrentDate()
                        ));
                        dr.child("Chats").child(mFormDetails.getCompany()+"_"+mFormDetails.getProjectName()).setValue(chatClassList);
                        message.setText("");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });

        return view;
    }

    static String  DATE_FORMAT_9 = "h:mm a dd MMMM yyyy";
    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_9, Locale.US);
//        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }

    @Override
    public void onResume() {
        super.onResume();

        final List<ChatClass> list = new ArrayList<>();
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
        dr.child("Chats").child(mFormDetails.getCompany()+"_"+mFormDetails.getProjectName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    list.add(d.getValue(ChatClass.class));
                }

                ChatAdapter adapter = new ChatAdapter(getActivity(), list);
                chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                chatRecyclerView.scrollToPosition(list.size() - 1);
                chatRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+ databaseError.toString());
            }
        });
    }
}
