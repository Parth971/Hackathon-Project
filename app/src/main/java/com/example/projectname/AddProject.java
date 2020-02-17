package com.example.projectname;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddProject extends AppCompatActivity {

    private static final String TAG = "AddProject";

    FirebaseAuth fa;
    DatabaseReference dr;
    StorageReference sr;
    List<UserDetails> userDetailsList;

    EditText projectName, company, projectDescription;
    TextView date;

    ImageView projectImage;

    Calendar myCalendar;

    private static final int GALLARY_REQUEST_CODE = 2;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        date = (TextView) findViewById(R.id.date);
        projectName = (EditText) findViewById(R.id.projectName);
        company = (EditText) findViewById(R.id.company);
        projectDescription = (EditText) findViewById(R.id.projectDescription);
        projectImage = (ImageView) findViewById(R.id.projectImage);

        userDetailsList = new ArrayList<>();
        myCalendar = Calendar.getInstance();

        progressDialog = new ProgressDialog(AddProject.this);


        projectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(AddProject.this,
                            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            GALLARY_REQUEST_CODE);
                    return;
                }
                getProjectPic();
            }
        });

    }
    public void getProjectPic(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLARY_REQUEST_CODE);
    }

    Uri uri;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLARY_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK) {
                if (data != null && data.getData() != null) {

                    uri = data.getData();

                    Picasso.get()
                            .load(data.getData())
                            .into(projectImage);

                }
            }else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "onActivityResult: result cancelled");
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case GALLARY_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getProjectPic();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Log.d(TAG, "onRequestPermissionsResult: default");
        }
    }



    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };
    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        date.setText(sdf.format(myCalendar.getTime()));
    }

    public void SelectDate(View v){

        new DatePickerDialog(AddProject.this, dateSetListener, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();

    }
    public void submitForm(View v){
        dr = FirebaseDatabase.getInstance().getReference();
        fa = FirebaseAuth.getInstance();
        sr = FirebaseStorage.getInstance().getReference();

        final StorageReference paths = sr.child(fa.getCurrentUser().getUid() + projectName.getText().toString()).child("image.jpg");

        if(uri==null){
            FormDetails f = new FormDetails(projectName.getText().toString(),
                    company.getText().toString(),
                    projectDescription.getText().toString(),
                    date.getText().toString(),
                    null,
                    null,
                    null);
            dr.child("Projects").child(company.getText().toString()+"_"+projectName.getText().toString()).setValue(f);
            dr.child("ProjectsUnderUsers").child(fa.getCurrentUser().getUid()).
                    child(company.getText().toString()+"_"+projectName.getText().toString()).setValue("Admin");
            finish();
            return;
        }

        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        paths.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                paths.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        FormDetails f = new FormDetails(projectName.getText().toString(),
                                company.getText().toString(),
                                projectDescription.getText().toString(),
                                date.getText().toString(),
                                uri.toString(),
                                null,
                                null);
                        dr.child("Projects").child(company.getText().toString()+"_"+projectName.getText().toString()).setValue(f);
                        dr.child("ProjectsUnderUsers").child(fa.getCurrentUser().getUid()).
                                child(company.getText().toString()+"_"+projectName.getText().toString()).setValue("Admin");
                        progressDialog.show();
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getMessage() );
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }
        });




    }


}
