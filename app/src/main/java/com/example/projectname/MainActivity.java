package com.example.projectname;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Button btnSignIn, btnRegister;
    RelativeLayout rootLayout;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            setContentView(R.layout.activity_main);
        }
        else {
            goToDashBoard();
        }

        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);

        firebaseAuth = FirebaseAuth.getInstance();

        //Event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterDialog();
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginDialog();
            }
        });


        progressDialog = new ProgressDialog(this);

    }
    private void showRegisterDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("Register");
        dialog.setMessage("Please use email to Register");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.layout_register,null);

        final MaterialEditText edtEmail = register_layout.findViewById(R.id.edtEmail);
        final MaterialEditText edtPassword = register_layout.findViewById(R.id.edtPassword);
        final MaterialEditText edtName = register_layout.findViewById(R.id.edtName);
        final MaterialEditText edtPhone = register_layout.findViewById(R.id.edtPhone);

        dialog.setView(register_layout);
        dialog.setPositiveButton("Register", null);
        dialog.setNegativeButton("Cancel", null);

        final AlertDialog mAlertDialog = dialog.create();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button pos = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                pos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressDialog.setMessage("Signing Up..");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        int flag = 0;

                        //Check Validaation
                        if(TextUtils.isEmpty(edtEmail.getText().toString()) || !edtEmail.getText().toString().contains("@")){
                            edtEmail.setText("");
                            edtEmail.setHint( "Please enter Proper Email");
                            flag = 1;
                        }

                        if(TextUtils.isEmpty(edtName.getText().toString())){
                            edtName.setText("");
                            edtName.setHint("Please enter Proper Name");
                            flag = 1;
                        }
                        if(TextUtils.isEmpty(edtPhone.getText().toString())){
                            edtPhone.setText("");
                            edtPhone.setHint("Please enter Proper Phone Number");
                            flag = 1;
                        }
                        if(edtPhone.getText().toString().length() != 10){
                            edtPhone.setText("");
                            edtPhone.setHint("Please enter Phone Number of 10 digits Only");
                            flag = 1;
                        }
                        if(TextUtils.isEmpty(edtPassword.getText().toString())){
                            edtPassword.setText("");
                            edtPassword.setHint("Please enter Password");
                            flag = 1;
                        }
                        if(( edtPassword.getText().toString().length() ) < 6){
                            edtPassword.setText("");
                            edtPassword.setHint("Passsword too short!");
                            flag = 1;
                        }
                        if(flag == 0){
                            mAlertDialog.dismiss();
                            firebaseAuth.createUserWithEmailAndPassword(edtEmail.getText().toString().trim(),edtPassword.getText().toString().trim())
                                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()){
                                                UserDetails details = new UserDetails(edtName.getText().toString(),
                                                        edtEmail.getText().toString(),
                                                        edtPassword.getText().toString(),
                                                        edtPhone.getText().toString());

                                                databaseReference = FirebaseDatabase.getInstance().getReference();
                                                databaseReference.child("Users").child(firebaseAuth.getCurrentUser().getUid()).setValue(details);
                                                Toast.makeText(MainActivity.this, "Successfull", Toast.LENGTH_LONG).show();

                                                progressDialog.dismiss();
                                                goToDashBoard();

                                            }
                                        }
                                    })
                                    .addOnFailureListener(MainActivity.this, new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(MainActivity.this, "Unsuccessfull: "+e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });

                        }

                    }
                });
                Button neg = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                neg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAlertDialog.dismiss();
                    }
                });
            }
        });
        mAlertDialog.show();
    }

    private void showLoginDialog() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("SIGN IN");
        dialog.setMessage("Please use email to Sign in");

        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.layout_login,null);

        final MaterialEditText edtEmail = login_layout.findViewById(R.id.edtEmail);
        final MaterialEditText edtPassword = login_layout.findViewById(R.id.edtPassword);


        dialog.setView(login_layout);
        dialog.setPositiveButton("Sign In", null);
        dialog.setNegativeButton("Cancel", null);

        final AlertDialog mAlertDialog = dialog.create();

        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button pos = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                pos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressDialog.setMessage("Signing In..");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        int flag = 0;

                        if(TextUtils.isEmpty(edtEmail.getText().toString())){
                            edtEmail.setText("");
                            edtEmail.setHint("Fill Email Correctly");
                            flag = 1;
                        }
                        if(TextUtils.isEmpty(edtPassword.getText().toString())){
                            edtPassword.setText("");
                            edtPassword.setHint("Password is not valid");
                            flag = 1;
                        };
                        if(flag == 0){
                            mAlertDialog.dismiss();
                            firebaseAuth.signInWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString())
                                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()){
                                                progressDialog.dismiss();
                                                goToDashBoard();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(MainActivity.this, new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }

                    }
                });
                Button neg = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                neg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAlertDialog.dismiss();
                    }
                });
            }
        });

        mAlertDialog.show();

    }

    public void goToDashBoard(){
        startActivity(new Intent(MainActivity.this, DashBoard.class));
        finish();
    }
}
