package com.example.saurav.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordReset extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private EditText emailreset;
    private Button resetpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        getSupportActionBar().setTitle("Reset Password");

        firebaseAuth = FirebaseAuth.getInstance();

        emailreset=(EditText)findViewById(R.id.emailreset);
        resetpassword=(Button)findViewById(R.id.resetbutton);

        resetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String useremail= emailreset.getText().toString().trim();
                if(useremail.equals("")){
                    Toast.makeText(PasswordReset.this, "Please enter your registered email id",Toast.LENGTH_SHORT).show();
                }
                else{
                    firebaseAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(PasswordReset.this,"Reset email sent!!",Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(PasswordReset.this,MainActivity.class));
                            }
                            else{
                                Toast.makeText(PasswordReset.this,"Error in sending password reset email!!",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });
    }
}
