package com.example.saurav.login;

import android.app.ProgressDialog;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NameChangeActivity extends AppCompatActivity {

    private EditText name_change;
    private Button save;
    private ProgressDialog progressDialog;

    private DatabaseReference mdatabase;
    private FirebaseUser muser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_change);

        name_change=(EditText)findViewById(R.id.change_name);
        save=(Button)findViewById(R.id.save);
        String name_value =getIntent().getStringExtra("name");
        name_change.setText(name_value);

        progressDialog =new ProgressDialog(this);

        muser= FirebaseAuth.getInstance().getCurrentUser();
        String user_id=muser.getUid();

        mdatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setTitle("Saving Changes");
                progressDialog.setMessage("Loading");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                String name=name_change.getText().toString();
                mdatabase.child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            finish();
                            startActivity(new Intent(NameChangeActivity.this,ProfileActivity.class));
                        }
                        else {
                            Toast.makeText(NameChangeActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}
