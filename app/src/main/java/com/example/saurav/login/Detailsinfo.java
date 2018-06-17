package com.example.saurav.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Date;
import java.text.DateFormat;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Detailsinfo extends AppCompatActivity {
    private CircleImageView user_personal_image;
    private TextView user_personal_name;
    private TextView user_personal_email;
    private Button send_req, decline_req;
    private ProgressBar load_profile_progress;
    private DatabaseReference databaseReference;

    private DatabaseReference friendsReqDatabase;

    private DatabaseReference friendDatabase;

    private DatabaseReference notificationDatabase;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser currentUser;
    private String current_state;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailsinfo);
        user_personal_name = (TextView) findViewById(R.id.user_personal_name);
        user_personal_email = (TextView) findViewById(R.id.user_personal_email);
        user_personal_image = (CircleImageView) findViewById(R.id.user_personal_image);
        load_profile_progress = (ProgressBar) findViewById(R.id.load_profile_progress);
        send_req = (Button) findViewById(R.id.send_request_btn);
        decline_req = (Button) findViewById(R.id.request_decline_btn);
        progressDialog=new ProgressDialog(Detailsinfo.this);

        firebaseFirestore = FirebaseFirestore.getInstance();

        final String passed_id = getIntent().getStringExtra("user_id");

        current_state = "not_friends";

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(passed_id);

        friendsReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");

        friendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        notificationDatabase = FirebaseDatabase.getInstance().getReference().child("Notifications");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                user_personal_name.setText(name);
                user_personal_email.setText(email);

                friendsReqDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(passed_id)) {
                            String req_type = dataSnapshot.child(passed_id).child("Request_Type").getValue().toString();
                            if (req_type.equals("Received")) {
                                current_state = "req_received";
                                send_req.setText("Accept Friend Request");
                                decline_req.setVisibility(View.VISIBLE);
                                decline_req.setEnabled(true);

                            } else if (req_type.equals("Sent")) {
                                current_state = "req_sent";
                                send_req.setText("Cancel Friend Request");
                                decline_req.setVisibility(View.INVISIBLE);
                                decline_req.setEnabled(false);

                            }

                        } else {
                            friendDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(passed_id)) {
                                        current_state = "friends";
                                        send_req.setText("UnFriend Request");
                                        decline_req.setVisibility(View.INVISIBLE);
                                        decline_req.setEnabled(false);

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        firebaseFirestore.collection("Users").document(passed_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                load_profile_progress.setVisibility(View.VISIBLE);
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        final String image = task.getResult().getString("image");
                        Glide.with(Detailsinfo.this).load(image).into(user_personal_image);
                        user_personal_image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent imageShow= new Intent(user_personal_image.getContext(),ShowImage.class);
                                imageShow.putExtra("image_url",image);
                                startActivity(imageShow);
                            }
                        });
                        load_profile_progress.setVisibility(View.INVISIBLE);

                        //Toast.makeText(ProfileActivity.this,"Data Exists ",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Detailsinfo.this, "Data Doesn't Exists ", Toast.LENGTH_SHORT).show();
                        load_profile_progress.setVisibility(View.INVISIBLE);
                    }

                } else {
                    String errr = task.getException().getMessage();
                    Toast.makeText(Detailsinfo.this, "Firestore Retrieve error: " + errr, Toast.LENGTH_SHORT).show();
                    load_profile_progress.setVisibility(View.INVISIBLE);
                }
            }
        });

        send_req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_req.setEnabled(false);

                //Not Friend State

                if (current_state.equals("not_friends")) {
                    friendsReqDatabase.child(currentUser.getUid()).child(passed_id).child("Request_Type").setValue("Sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                friendsReqDatabase.child(passed_id).child(currentUser.getUid()).child("Request_Type").setValue("Received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        HashMap<String, String> notificationdata = new HashMap<>();
                                        notificationdata.put("from", currentUser.getUid());
                                        notificationdata.put("type", "request");
                                        notificationDatabase.child(passed_id).push().setValue(notificationdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                send_req.setEnabled(true);
                                                current_state = "req_sent";
                                                send_req.setText("Cancel Friend Request");
                                                decline_req.setVisibility(View.INVISIBLE);
                                                decline_req.setEnabled(false);

                                            }
                                        });

                                        Toast.makeText(Detailsinfo.this, "Request sent successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                Toast.makeText(Detailsinfo.this, "Failed to send request", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }

                //Cancel Request State
                if (current_state.equals("req_sent")) {
                    friendsReqDatabase.child(currentUser.getUid()).child(passed_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                friendsReqDatabase.child(passed_id).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        send_req.setEnabled(true);
                                        current_state = "not_friends";
                                        send_req.setText("Send Friend Request");
                                        decline_req.setVisibility(View.INVISIBLE);
                                        decline_req.setEnabled(false);

                                    }
                                });
                            }

                        }
                    });
                }

                //Req received State

                if (current_state.equals("req_received")) {
                    final String currentDate = DateFormat.getDateTimeInstance().format(new java.util.Date());

                    friendDatabase.child(currentUser.getUid()).child(passed_id).child("date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendDatabase.child(passed_id).child(currentUser.getUid()).child("date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    friendsReqDatabase.child(currentUser.getUid()).child(passed_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                friendsReqDatabase.child(passed_id).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        send_req.setEnabled(true);
                                                        current_state = "friends";
                                                        send_req.setText("UnFriend Request");
                                                        decline_req.setVisibility(View.INVISIBLE);
                                                        decline_req.setEnabled(false);

                                                    }
                                                });
                                            }

                                        }
                                    });

                                }
                            });
                        }
                    });

                }

                //Friend State
                if (current_state.equals("friends")) {
                    friendDatabase.child(currentUser.getUid()).child(passed_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                friendDatabase.child(passed_id).child(currentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            send_req.setEnabled(true);
                                            current_state = "not_friends";
                                            send_req.setText("Send Friend Request");
                                            decline_req.setVisibility(View.INVISIBLE);
                                            decline_req.setEnabled(false);

                                        }
                                        else{
                                            progressDialog = new ProgressDialog(Detailsinfo.this);
                                            progressDialog.setTitle("Alert!");
                                            progressDialog.setMessage("Can not unfriend from other user.");
                                            progressDialog.show();
                                            progressDialog.setCanceledOnTouchOutside(true);
                                        }
                                    }
                                });
                            }

                        }
                    });

                }

            }
        });

        decline_req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_req.setEnabled(false);

                //Request Received State

                if (current_state.equals("req_received")) {
                    friendsReqDatabase.child(currentUser.getUid()).child(passed_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                friendsReqDatabase.child(passed_id).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        send_req.setEnabled(true);
                                        current_state = "not_friends";
                                        send_req.setText("Send Friend Request");
                                        decline_req.setVisibility(View.INVISIBLE);
                                        decline_req.setEnabled(false);
                                    }
                                });

                            } else {
                                Toast.makeText(Detailsinfo.this, "Failed to cancel request", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
