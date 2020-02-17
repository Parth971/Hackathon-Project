package com.example.projectname;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class ResourcesAdapter extends RecyclerView.Adapter<ResourcesAdapter.ResourcesViewHolder> {

    private static final String TAG = "ResourcesAdapter";

    Activity context;
    List<ResourceClass> list;

    public ResourcesAdapter(Activity context, List<ResourceClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ResourcesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.resource_item, parent, false);

        return new ResourcesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ResourcesViewHolder holder, final int position) {

        holder.nameOfFile.setText(list.get(position).getFileName());
        holder.time.setText(list.get(position).getTime());
        holder.downloadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadfile(context, list.get(position).getFileName(),DIRECTORY_DOWNLOADS, list.get(position).getFileUrl() );

            }
        });

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
        dr.child("Users").child(list.get(position).getIdOfUploader()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.nameOfSender.setText(dataSnapshot.getValue(UserDetails.class).getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.toString());
            }
        });
    }
    String getExtension(String s){
        return "."+s.substring(s.indexOf('/')+1);
    }

    public void downloadfile(Context context, String fileName, String desDir, String url){

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, desDir, fileName);

        downloadManager.enqueue(request);
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    class ResourcesViewHolder extends RecyclerView.ViewHolder{
        TextView nameOfFile, nameOfSender, time;
        ImageButton downloadFile;

        public ResourcesViewHolder(@NonNull View itemView) {
            super(itemView);

            nameOfFile = (TextView) itemView.findViewById(R.id.nameOfFile);
            nameOfSender = (TextView) itemView.findViewById(R.id.nameOfSender);
            time = (TextView) itemView.findViewById(R.id.time);
            downloadFile = (ImageButton) itemView.findViewById(R.id.downloadFile);
        }
    }
}
