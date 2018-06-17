package com.example.saurav.login;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView name,email;
    private ProgressBar editprog;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private DatabaseReference databaseReference;
    private CircleImageView imageView;
    private Button edit_btn;
    private Uri mainImageURI=null;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        getSupportActionBar().setTitle("Profile");
        firebaseAuth=FirebaseAuth.getInstance();
        storageReference=FirebaseStorage.getInstance().getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getUid());
        databaseReference.keepSynced(true);
        user_id=firebaseAuth.getCurrentUser().getUid();

        editprog =(ProgressBar)findViewById(R.id.editprogress);
        edit_btn=(Button)findViewById(R.id.edit_profile_btn);
        imageView=(CircleImageView) findViewById(R.id.img);
        name=(TextView)findViewById(R.id.us_name);
        email=(TextView)findViewById(R.id.us_email);
        edit_btn.setEnabled(false);

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(ProfileActivity.this,NameChangeActivity.class);
                i.putExtra("name",name.getText().toString());
                startActivity(i);
                finish();

            }
        });


        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                editprog.setVisibility(View.VISIBLE);
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        String image=task.getResult().getString("image");

                        Glide.with(ProfileActivity.this).load(image).into(imageView);
                        editprog.setVisibility(View.INVISIBLE);

                        //Toast.makeText(ProfileActivity.this,"Data Exists ",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(ProfileActivity.this,"Data Doesn't Exists ",Toast.LENGTH_SHORT).show();
                        editprog.setVisibility(View.INVISIBLE);
                    }

                }
                else{
                    String errr=task.getException().getMessage();
                    Toast.makeText(ProfileActivity.this,"Firestore Retrieve error: "+errr,Toast.LENGTH_SHORT).show();
                }
            }
        });

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editprog.setVisibility(View.VISIBLE);

                user_id=firebaseAuth.getCurrentUser().getUid();
                final String user_name=name.getText().toString();
                StorageReference image_path= storageReference.child("Profile Image").child(user_id+".jpg");
                image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Uri download_uri=task.getResult().getDownloadUrl();
                            databaseReference.child("image_url").setValue(download_uri.toString());


                            Map<String,String> user_map=new HashMap<>();
                            user_map.put("name",user_name);
                            user_map.put("image",download_uri.toString());
                            firebaseFirestore.collection("Users").document(user_id).set(user_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ProfileActivity.this,"Image has been uploaded to firestore",Toast.LENGTH_SHORT).show();
                                        editprog.setVisibility(View.INVISIBLE);
                                        edit_btn.setEnabled(false);

                                    }
                                    else {
                                        String errr=task.getException().getMessage();
                                        Toast.makeText(ProfileActivity.this,"Firestore eroor: "+errr,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            Toast.makeText(ProfileActivity.this,"Uploaded",Toast.LENGTH_SHORT).show();

                        }
                        else{
                            String error=task.getException().getMessage();
                            Toast.makeText(ProfileActivity.this,"Image Error "+error,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(ProfileActivity.this,"Permission Denied",Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }
                    else {
                        //Toast.makeText(ProfileActivity.this,"You already have permission",Toast.LENGTH_SHORT).show();
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1,1)
                                .start(ProfileActivity.this);


                    }
                }
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase =FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("Users").child(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userprofile userprof = dataSnapshot.getValue(userprofile.class);
                name.setText(userprof.getName());
                email.setText(userprof.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this,databaseError.getCode(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                imageView.setImageURI(mainImageURI);
                edit_btn.setEnabled(true);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
