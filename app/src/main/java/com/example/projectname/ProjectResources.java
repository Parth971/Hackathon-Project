package com.example.projectname;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.FileUtils;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ProjectResources extends Fragment {
    private static final String TAG = "ProjectResources";
    
    private static final int GET_FILE_CODE = 120;

    Activity mcontext;
    FormDetails mFormDetails;
    ProjectResources(Activity context, FormDetails formDetails ) {
        mcontext = context;
        mFormDetails = formDetails;
    }

    EditText fileName;
    ProgressDialog progressDialog;
    RecyclerView resourceRecyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_project_resorces, container, false);

        ImageButton add_resource = (ImageButton) view.findViewById(R.id.add_resource);
        resourceRecyclerView = (RecyclerView) view.findViewById(R.id.resourceRecyclerView);

        add_resource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.add_file_layout, null);

                Button selectFile = (Button) dialogView.findViewById(R.id.selectFile);
                fileName = (EditText) dialogView.findViewById(R.id.fileName);
                Button uploadResource = (Button) dialogView.findViewById(R.id.uploadResource);

                alert.setView(dialogView);
                final AlertDialog dialog = alert.show();

                selectFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(ContextCompat.checkSelfPermission(mcontext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                            ActivityCompat.requestPermissions(mcontext,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                    GET_FILE_CODE);
                            return;
                        }
                        getFiles();
                    }
                });
                uploadResource.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(fileUri!=null){
                            final StorageReference sr = FirebaseStorage.getInstance().getReference();
                            final StorageReference path = sr.child("Project Resources")
                                    .child(mFormDetails.getCompany()+"_"+mFormDetails.getProjectName())
                                    .child(fileName.getText().toString());

                            progressDialog = new ProgressDialog(getContext());
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progressDialog.setProgress(0);
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            path.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                       @Override
                                       public void onSuccess(Uri uri) {
                                           final DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
                                           List<ResourceClass> s;
                                           if(mFormDetails.getResourceUris()!=null){
                                               s = mFormDetails.getResourceUris();
                                           }
                                           else {
                                               s = new ArrayList<>();
                                           }
                                           s.add(new ResourceClass(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                                   fileName.getText().toString()+getExtension(mimeType),
                                                   uri.toString(),
                                                   getCurrentDate(),
                                                   mimeType));
                                           mFormDetails.setResourceUris(s);
                                           dr.child("Projects").child(mFormDetails.getCompany()+"_"+mFormDetails.getProjectName()).setValue(mFormDetails);
                                           final List<ChatClass> chatClassList = new ArrayList<>();
                                           dr.child("Chats").child(mFormDetails.getCompany()+"_"+mFormDetails.getProjectName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                               @Override
                                               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                   chatClassList.clear();
                                                   for(DataSnapshot d : dataSnapshot.getChildren()){
                                                       chatClassList.add(d.getValue(ChatClass.class));
                                                   }
                                                   chatClassList.add(new ChatClass(
                                                           FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                                           "RES",
                                                           "NEW RESOURCES ADDED, PLEASE CHECK",
                                                           getCurrentDate()
                                                   ));
                                                   dr.child("Chats").child(mFormDetails.getCompany()+"_"+mFormDetails.getProjectName()).setValue(chatClassList);

                                               }

                                               @Override
                                               public void onCancelled(@NonNull DatabaseError databaseError) {

                                               }
                                           });
                                           dialog.dismiss();
                                           progressDialog.dismiss();
                                       }
                                   }).addOnFailureListener(new OnFailureListener() {
                                       @Override
                                       public void onFailure(@NonNull Exception e) {
                                           Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                           dialog.dismiss();
                                           progressDialog.dismiss();
                                       }
                                   });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    progressDialog.dismiss();
                                }
                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.setProgress((int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount()));
                                }
                            });
                        }else {
                            Toast.makeText(mcontext, "Please select a file", Toast.LENGTH_SHORT).show();
                        }
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
    String getExtension(String s){
        return "."+s.substring(s.indexOf('/')+1);
    }
    String[] s = new String[]{"image/jpeg",
            "audio/mpeg4-generic",
            "text/html",
            "audio/mpeg",
            "audio/aac",
            "audio/wav",
            "audio/ogg",
            "audio/midi",
            "audio/x-ms-wma",
            "video/mp4",
            "video/x-msvideo",
            "video/x-ms-wmv",
            "image/png",
            "image/jpg",
            "image/gif",
            "text/xml",
            "text/plain",
            "text/html",
            "application/pdf",
            "application/vnd.android.package-archive",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/msword",
            "application/zip"};

    public void getFiles(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
                // Update with additional mime types here using a String[].
        intent.putExtra(Intent.EXTRA_MIME_TYPES, s);

        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        startActivityForResult(intent, GET_FILE_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case GET_FILE_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getFiles();
                } else {
                    Toast.makeText(mcontext, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Log.d(TAG, "onRequestPermissionsResult: default");
        }
    }

    Uri fileUri;
    String mimeType;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GET_FILE_CODE){
            if(resultCode == Activity.RESULT_OK){
                if(data!=null && data.getData()!=null){
                    fileUri = data.getData();

                    mimeType = getContext().getContentResolver().getType(fileUri);
                    fileName.setText(fileUri.getLastPathSegment());
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
        dr.child("Projects").child(mFormDetails.getCompany()+"_"+mFormDetails.getProjectName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue(FormDetails.class).getResourceUris()!=null){
                    ResourcesAdapter adapter = new ResourcesAdapter(getActivity(), dataSnapshot.getValue(FormDetails.class).getResourceUris());
                    resourceRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    resourceRecyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+ databaseError.toString());
            }
        });
    }
}
