package com.example.saurav.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Registration extends AppCompatActivity {

    private EditText username,password,email;
    private Button register;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    String user_email,user_password,user_name;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        AlertDialog.Builder builder=new AlertDialog.Builder(Registration.this);
        final View mview= getLayoutInflater().inflate(R.layout.activity_registration,null);
        builder.setView(mview);
        AlertDialog dialog = builder.create();
        dialog.show();

        username =(EditText)mview.findViewById(R.id.email_login);
        password =(EditText)mview.findViewById(R.id.passwordlogin);
        email =(EditText)mview.findViewById(R.id.email);
        register =(Button)mview.findViewById(R.id.registercreate);
        progressBar=(ProgressBar)mview.findViewById(R.id.progressBarReg);

        */
        setContentView(R.layout.activity_registration);

        getSupportActionBar().setTitle("Registration");
        setupuiviews();
        //getSupportActionBar().hide();

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
    }

    private void setupuiviews(){
        username =(EditText)findViewById(R.id.email_login);
        password =(EditText)findViewById(R.id.passwordlogin);
        email =(EditText)findViewById(R.id.email);
        register =(Button)findViewById(R.id.registercreate);
        progressBar=(ProgressBar)findViewById(R.id.progressBarReg);
    }
    public void regclick(View view){
        if(validate()){
            //data to database
            user_name=username.getText().toString().trim();
            user_email = email.getText().toString().trim();
            user_password = password.getText().toString().trim();

            progressBar.setVisibility(View.VISIBLE);

            progressDialog.setMessage("Registering");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(user_email,user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        senduserdata();

                        sendemailverification();
                        firebaseAuth.signOut();
                        progressDialog.dismiss();
                        Toast.makeText(Registration.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Registration.this,MainActivity.class));
                        finish();
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(Registration.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void sendemailverification() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(Registration.this,"Verification email has been sent",Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                    else{
                        Toast.makeText(Registration.this,"Verification email not sent",Toast.LENGTH_SHORT).show();
                        overridePendingTransition(0, 0);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                    }

                }
            });
        }
    }

    private boolean validate(){
        Boolean result=false;
        String name=username.getText().toString();
        String pass=password.getText().toString();
        String emailid=email.getText().toString();
        if(name.isEmpty()|| pass.isEmpty()||emailid.isEmpty()){
            Toast.makeText(this,"Fill all details",Toast.LENGTH_SHORT).show();
        }
        else {
            result=true;
        }
        return result;
    }

    private void senduserdata(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference myref = firebaseDatabase.getReference().child("Users").child(firebaseAuth.getUid());
        userprofile userprof = new userprofile(user_name,user_email,"default");
        myref.setValue(userprof);

    }

    public void alreadymember(View view){
        startActivity(new Intent(Registration.this,MainActivity.class));
    }
}
