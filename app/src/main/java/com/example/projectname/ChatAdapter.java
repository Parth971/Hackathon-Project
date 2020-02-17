package com.example.projectname;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatAdapterViewHolder> {

    Activity context;
    List<ChatClass> list;

    public ChatAdapter(Activity context, List<ChatClass> list) {
        this.context = context;
        this.list = list;
        }

    @NonNull
    @Override
    public ChatAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);
        return new ChatAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatAdapterViewHolder holder, int position) {

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
        dr.child("Users").child(list.get(position).getSenderId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.name.setText(dataSnapshot.getValue(UserDetails.class).getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.msg.setText(list.get(position).getMessage());
        holder.time.setText(list.get(position).getTime());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class ChatAdapterViewHolder extends RecyclerView.ViewHolder{
        TextView name, msg, time;
        LinearLayout chat_layout;

        public ChatAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            msg = (TextView) itemView.findViewById(R.id.msg);
            time = (TextView) itemView.findViewById(R.id.time);
            chat_layout = (LinearLayout) itemView.findViewById(R.id.chat_layout);

        }
    }
}
