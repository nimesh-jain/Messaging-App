package com.example.saurav.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;

    public EditText email,password;
    private Button log_in;
    private TextView forgotpassword;
    private ProgressBar progressBar;

    public String emailfield,passwordfield;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference userdatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dl =(DrawerLayout) findViewById(R.id.dl);
        abdt = new ActionBarDrawerToggle(this,dl,R.string.Open,R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);

        dl.setDrawerListener(abdt);
        abdt.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My App");

        final NavigationView nav_view =(NavigationView)findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        int id= item.getItemId();

                        if(id == R.id.myprofile)
                        {
                            Toast.makeText(MainActivity.this,"My Profile",Toast.LENGTH_SHORT).show();

                        }
                        else if(id == R.id.settings)
                        {
                            Toast.makeText(MainActivity.this,"Settings",Toast.LENGTH_SHORT).show();

                        }
                        else if(id == R.id.editprofile)
                        {
                            Toast.makeText(MainActivity.this,"Edit Profile",Toast.LENGTH_SHORT).show();

                        }
                        return true;
                    }
                });



        email=(EditText)findViewById(R.id.email_login);
        password=(EditText)findViewById(R.id.passwordlogin);
        log_in=(Button)findViewById(R.id.log);
        forgotpassword=(TextView) findViewById(R.id.passreset);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);

        emailfield = email.getText().toString();
        passwordfield = password.getText().toString();

        userdatabase=FirebaseDatabase.getInstance().getReference().child("Users");

        log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(logvalidate()) {


                    if(!isConnected(MainActivity.this))
                        buildDialog1(MainActivity.this).show();
                    else {
                        validate(email.getText().toString(), password.getText().toString());
                    }



                }
            }
        });

        firebaseAuth =FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null) {
            finish();
            startActivity(new Intent(this, SecondActivity.class));
        }


        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,PasswordReset.class));
            }
        });
    }

    /*public void loginclick(View v){

        validate(email.getText().toString(),password.getText().toString());

    }*/

    public void registerclick(View v){
        startActivity(new Intent(MainActivity.this,Registration.class));
    }
    private void validate(String email,String password){
        progressBar.setVisibility(View.VISIBLE);

        //progressDialog.setMessage("Logging In");
        //progressDialog.show();


        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //progressDialog.dismiss();
                    checkIfEmailVerified();
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Log in Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //startActivity(new Intent(MainActivity.this,SecondActivity.class));

    }

    private void checkIfEmailVerified()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified())
        {

            /*
            String deviceToken= FirebaseInstanceId.getInstance().getToken();
            final String current_user_id=firebaseAuth.getCurrentUser().getUid();
            userdatabase.child(current_user_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userdatabase.child(current_user_id).child("online").setValue(true);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            userdatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    // user is verified, so you can finish this activity or send user to activity which you want.
                    finish();
                    Toast.makeText(MainActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this,SecondActivity.class));

                }
            });
            */

            finish();
            Toast.makeText(MainActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this,SecondActivity.class));


        }
        else
        {
            // email is not verified, so just prompt the message to the user and restart this activity.
            // NOTE: don't forget to log out the user.
            Toast.makeText(MainActivity.this,"Verify your email first",Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this,MainActivity.class));

            //restart this activity

        }
    }

    private boolean logvalidate(){
        Boolean result=false;
        String pass=password.getText().toString();
        String emailid=email.getText().toString();
        if(pass.isEmpty()||emailid.isEmpty()){
            Toast.makeText(this,"Fill all details",Toast.LENGTH_SHORT).show();
        }
        else {
            result=true;
        }
        return result;
    }





    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
        else
            return false;
        }
        else
        return false;
    }

    public AlertDialog.Builder buildDialog1(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Need Internet Access To Log In!!");
        builder.setCancelable(true);

        /*
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });
        */

        return builder;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }


}
