package com.example.saurav.login;

import android.*;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private String ChatUser;
    private TextView TitleView;
    private CircleImageView ProfileImage;
    private DatabaseReference ChatUserDatabase;
    private DatabaseReference frienddatabase;
    private FirebaseAuth mAuth;
    private String mCurrentUser_id;
    public String ChatUserName,ChatUserImage;

    private EditText msgbox;
    private TextView image_option;
    private FloatingActionButton sendbtn;
    private RecyclerView messagelist;
    private final List<Messages> messagebody=new ArrayList<>();
    private LinearLayoutManager mlinearlayout;
    private MessageAdapter madapter;
    private ProgressDialog progressDialog;
    private Uri mainImageURI=null;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ChatUser= getIntent().getStringExtra("user_id");
        ChatUserDatabase= FirebaseDatabase.getInstance().getReference();

        mAuth=FirebaseAuth.getInstance();
        mCurrentUser_id=mAuth.getCurrentUser().getUid();
        frienddatabase=FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrentUser_id);


        ActionBar actionBar =getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater =(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view =layoutInflater.inflate(R.layout.chat_custom_bar,null);
        actionBar.setCustomView(action_bar_view);

        TitleView =(TextView)action_bar_view.findViewById(R.id.chat_user_name);
        ProfileImage =(CircleImageView)action_bar_view.findViewById(R.id.cutom_user_image);
        msgbox=(EditText)findViewById(R.id.msgbox);
        image_option=(TextView)findViewById(R.id.image_option) ;
        sendbtn=(FloatingActionButton) findViewById(R.id.sendbtn);

        storageReference= FirebaseStorage.getInstance().getReference();

        messagelist=(RecyclerView)findViewById(R.id.messagelist);
        mlinearlayout=new LinearLayoutManager(this);
        messagelist.setHasFixedSize(true);
        messagelist.setLayoutManager(mlinearlayout);
        madapter=new MessageAdapter(messagebody);
        messagelist.setAdapter(madapter);
        mlinearlayout.setStackFromEnd(true);
        madapter.notifyDataSetChanged();

        loadmessage();

        ChatUserDatabase.child("Users").child(ChatUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatUserName= dataSnapshot.child("name").getValue().toString();
                ChatUserImage=dataSnapshot.child("image_url").getValue().toString();
                char arr;
                arr=ChatUserName.charAt(0);
                arr=Character.toUpperCase(arr);
                TitleView.setText(arr+ChatUserName.substring(1,ChatUserName.length()));
                Glide.with(ProfileImage.getContext()).load(ChatUserImage).into(ProfileImage);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*
        ChatUserDatabase.child("Chat").child(mCurrentUser_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(ChatUser)){
                    Map ChatAddMap =new HashMap();
                    ChatAddMap.put("seen",false);
                    ChatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map ChatUserMap =new HashMap();
                    ChatUserMap.put("Chat/"+mCurrentUser_id+"/"+ChatUser,ChatAddMap);
                    ChatUserMap.put("Chat/"+ChatUser+"/"+mCurrentUser_id,ChatAddMap);
                    ChatUserDatabase.updateChildren(ChatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError!=null){
                                Log.d("CHAT_LOG",databaseError.getMessage().toString());
                            }

                        }
                    });


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        */

        //send msg

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendmessage();
            }
        });

        image_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(ChatActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(ChatActivity.this,"Permission Denied",Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(ChatActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }
                    else {
                        //Toast.makeText(ProfileActivity.this,"You already have permission",Toast.LENGTH_SHORT).show();
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1,1)
                                .start(ChatActivity.this);


                    }
                }
            }
        });





    }




    //load message
    private void loadmessage() {
        ChatUserDatabase.child("messages").child(mCurrentUser_id).child(ChatUser).keepSynced(true);
        ChatUserDatabase.child("messages").child(mCurrentUser_id).child(ChatUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages message= dataSnapshot.getValue(Messages.class);
                messagebody.add(message);
                madapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    //send message
    private void sendmessage() {
        String message= msgbox.getText().toString();
        msgbox.setText("");
        if(!TextUtils.isEmpty(message)){
            String current_user_ref="messages/"+mCurrentUser_id+"/"+ChatUser;
            String chat_user_ref="messages/"+ChatUser+"/"+mCurrentUser_id;

            DatabaseReference user_message_push=ChatUserDatabase.child("messages").child(mCurrentUser_id).child(ChatUser).push();
            String push_id=user_message_push.getKey();

            Map messageMap=new HashMap();
            messageMap.put("message",message);
            messageMap.put("seen",false);
            messageMap.put("type","text");
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("from",mCurrentUser_id);
            messageMap.put("to",ChatUser);
            messageMap.put("msg_key",push_id);

            final Map messageUserMap=new HashMap();
            messageUserMap.put(current_user_ref+"/"+push_id,messageMap);
            messageUserMap.put(chat_user_ref+"/"+push_id,messageMap);
            ChatUserDatabase.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError!=null){
                        Log.d("CHAT_LOG",databaseError.getMessage().toString());
                    }
                }
            });


            //create users in chat location if chatted with


            Map ChatAddMap =new HashMap();
            ChatAddMap.put("seen",false);
            ChatAddMap.put("timestamp", ServerValue.TIMESTAMP);

            Map ChatUserMap =new HashMap();
            ChatUserMap.put("Chat/"+mCurrentUser_id+"/"+ChatUser,ChatAddMap);
            ChatUserMap.put("Chat/"+ChatUser+"/"+mCurrentUser_id,ChatAddMap);
            ChatUserDatabase.updateChildren(ChatUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError!=null){
                        Log.d("CHAT_LOG",databaseError.getMessage().toString());
                    }
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                final String current_user_ref="messages/"+mCurrentUser_id+"/"+ChatUser;
                final String chat_user_ref="messages/"+ChatUser+"/"+mCurrentUser_id;

                DatabaseReference user_message_push=ChatUserDatabase.child("messages").child(mCurrentUser_id).child(ChatUser).push();
                final String push_id=user_message_push.getKey();
                StorageReference image_path= storageReference.child("Message Images").child(push_id+".jpg");
                image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            String downloadUrl = task.getResult().getDownloadUrl().toString();

                            Map messageMap=new HashMap();
                            messageMap.put("message",downloadUrl);
                            messageMap.put("seen",false);
                            messageMap.put("type","image");
                            messageMap.put("time",ServerValue.TIMESTAMP);
                            messageMap.put("from",mCurrentUser_id);

                            final Map messageUserMap=new HashMap();
                            messageUserMap.put(current_user_ref+"/"+push_id,messageMap);
                            messageUserMap.put(chat_user_ref+"/"+push_id,messageMap);
                            ChatUserDatabase.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if(databaseError!=null){
                                        Log.d("CHAT_LOG",databaseError.getMessage().toString());
                                    }
                                }
                            });
                        }
                    }
                });

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
