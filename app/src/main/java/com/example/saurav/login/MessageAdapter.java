package com.example.saurav.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.PublicKey;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Saurav on 24-03-2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List <Messages> mMessageList;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private DatabaseReference chatuserdatabase,messagereference;


    public MessageAdapter(List<Messages> mMessageList) {
        this.mMessageList = mMessageList;
    }
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout,parent,false);
        return new MessageViewHolder(v);
    }
    public class MessageViewHolder extends RecyclerView.ViewHolder{
        private TextView msgtext,msgrighttext;
        private CircleImageView sender_image,sender_right_image;
        private ImageView imageView_right,imageView_left;
        private PhotoViewAttacher left_attacher,right_attacher;


        public MessageViewHolder(View view) {
            super(view);
            msgtext=(TextView)view.findViewById(R.id.mssg_item_txt);
            sender_image=(CircleImageView)view.findViewById(R.id.msg_sender_img);
            msgrighttext=(TextView)view.findViewById(R.id.mssg_item_right_txt);
            sender_right_image=(CircleImageView)view.findViewById(R.id.msg_sender_right_img);
            imageView_right=(ImageView)view.findViewById(R.id.imageView_right);
            //right_attacher=new PhotoViewAttacher(imageView_right);
            imageView_left=(ImageView)view.findViewById(R.id.imageView_left);
            //left_attacher=new PhotoViewAttacher(imageView_left);

        }
    }
    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i){
        final String current_user_id=mAuth.getCurrentUser().getUid();
        final Messages c= mMessageList.get(i);
        final  String from_user= c.getFrom();
        final String to_user=c.getTo();
        final String msg_key=c.getMsg_key();
        final String message_type=c.getType();
        chatuserdatabase =FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);
        chatuserdatabase.keepSynced(true);
        chatuserdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String image = dataSnapshot.child("image_url").getValue().toString();
                if(from_user.equals(current_user_id)){
                    viewHolder.sender_right_image.setVisibility(View.VISIBLE);
                    viewHolder.sender_image.setVisibility(View.INVISIBLE);

                    Glide.with(viewHolder.sender_image.getContext()).load(image).into(viewHolder.sender_right_image);

                    viewHolder.sender_right_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent imageShow= new Intent(viewHolder.sender_image.getContext(),ProfileActivity.class);
                            imageShow.putExtra("user_id",from_user);
                            viewHolder.imageView_right.getContext().startActivity(imageShow);

                        }
                    });
                }
                else{
                    viewHolder.sender_right_image.setVisibility(View.INVISIBLE);
                    viewHolder.sender_image.setVisibility(View.VISIBLE);
                    Glide.with(viewHolder.sender_image.getContext()).load(image).into(viewHolder.sender_image);
                    viewHolder.sender_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent imageShow= new Intent(viewHolder.sender_image.getContext(),Detailsinfo.class);
                            imageShow.putExtra("user_id",from_user);
                            viewHolder.imageView_right.getContext().startActivity(imageShow);

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        if(from_user.equals(current_user_id)){
            if(message_type.equals("text")){
                viewHolder.msgtext.setVisibility(View.INVISIBLE);
                viewHolder.imageView_right.setVisibility(View.INVISIBLE);
                viewHolder.imageView_right.setLayoutParams(new RelativeLayout.LayoutParams(0,0));
                viewHolder.imageView_left.setVisibility(View.INVISIBLE);
                viewHolder.imageView_left.setLayoutParams(new RelativeLayout.LayoutParams(0,0));
                viewHolder.msgrighttext.setVisibility(View.VISIBLE);
                //viewHolder.msgrighttext.setBackgroundResource(R.color.silver);
                viewHolder.msgrighttext.setTextColor(Color.BLACK);
                viewHolder.msgrighttext.setText(c.getMessage());


                viewHolder.msgrighttext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String message=c.getMessage();
                            if(message.substring(message.length()-4,message.length()).equals(".com")) {
                                Intent openintent = new Intent(Intent.ACTION_VIEW);
                                openintent.setData(Uri.parse(message));
                                view.getContext().startActivity(openintent);
                                notifyDataSetChanged();
                            }

                    }
                });


                /*
                viewHolder.msgrighttext.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        AlertDialog.Builder a_builder= new AlertDialog.Builder(viewHolder.sender_image.getRootView().getContext());
                        a_builder.setMessage("Delete the message?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        FirebaseDatabase.getInstance().getReference().child("messages").child(from_user).child(c.getTo())
                                                .child(c.getMsg_key()).removeValue();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });
                        AlertDialog alert= a_builder.create();
                        alert.setTitle("Delete");
                        alert.show();
                        notifyDataSetChanged();
                        return false;
                    }
                });
                */



                viewHolder.msgrighttext.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View view) {
                        PopupMenu popupMenu =new PopupMenu(view.getContext(),viewHolder.msgrighttext);
                        popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {

                                if(menuItem.getTitle().equals("Delete"))
                                {
                                    AlertDialog.Builder a_builder= new AlertDialog.Builder(viewHolder.sender_image.getRootView().getContext());
                                    a_builder.setMessage("Delete the message?")
                                            .setCancelable(false)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    FirebaseDatabase.getInstance().getReference().child("messages").child(from_user).child(c.getTo())
                                                            .child(c.getMsg_key()).removeValue();
                                                    Intent intent=new Intent(view.getContext(),ChatActivity.class);
                                                    intent.putExtra("user_id",c.getTo());
                                                    view.getContext().startActivity(intent);
                                                    ((Activity)viewHolder.msgrighttext.getContext()).finish();
                                                }
                                            })
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.cancel();
                                                }
                                            });
                                    AlertDialog alert= a_builder.create();
                                    alert.setTitle("Delete");
                                    alert.show();
                                    notifyDataSetChanged();
                                }
                                else if(menuItem.getTitle().equals("Copy"))
                                {
                                    String stringYouExtracted = viewHolder.msgrighttext.getText().toString();
                                    int startIndex = viewHolder.msgrighttext.getSelectionStart();
                                    int endIndex = viewHolder.msgrighttext.getSelectionEnd();
                                    stringYouExtracted = stringYouExtracted.substring(startIndex,endIndex);

                                    ClipboardManager clipboard = (ClipboardManager) viewHolder.msgrighttext.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("label", stringYouExtracted);
                                    clipboard.setPrimaryClip(clip);
                                }
                                return false;
                            }
                        });
                        popupMenu.show();
                        return false;
                    }
                });
            }
            else{
                viewHolder.imageView_right.setVisibility(View.VISIBLE);

                viewHolder.imageView_left.setVisibility(View.INVISIBLE);
                viewHolder.imageView_left.setLayoutParams(new RelativeLayout.LayoutParams(0,0));
                viewHolder.msgtext.setVisibility(View.INVISIBLE);
                viewHolder.msgrighttext.setVisibility(View.INVISIBLE);
                //viewHolder.msgrighttext.setBackgroundResource(R.color.silver);
                //viewHolder.msgrighttext.setTextColor(Color.BLACK);
                Glide.with(viewHolder.imageView_right.getContext()).load(c.getMessage()).into(viewHolder.imageView_right);
                viewHolder.imageView_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent imageShow= new Intent(viewHolder.imageView_right.getContext(),ShowImage.class);
                        imageShow.putExtra("image_url",c.getMessage());
                        viewHolder.imageView_right.getContext().startActivity(imageShow);

                    }
                });
            }

        }
        else{
            if(message_type.equals("text")){
                viewHolder.msgtext.setVisibility(View.VISIBLE);
                viewHolder.msgtext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent openintent = new Intent(Intent.ACTION_VIEW);
                        openintent.setData(Uri.parse(c.getMessage()));
                        view.getContext().startActivity(openintent);
                        notifyDataSetChanged();

                    }
                });
                viewHolder.msgrighttext.setVisibility(View.INVISIBLE);
                viewHolder.imageView_right.setVisibility(View.INVISIBLE);
                viewHolder.imageView_right.setLayoutParams(new RelativeLayout.LayoutParams(0,0));
                viewHolder.imageView_left.setVisibility(View.INVISIBLE);
                viewHolder.imageView_left.setLayoutParams(new RelativeLayout.LayoutParams(0,0));
                viewHolder.msgtext.setText(c.getMessage());
            }
            else{
                viewHolder.msgtext.setVisibility(View.INVISIBLE);
                viewHolder.msgrighttext.setVisibility(View.INVISIBLE);
                viewHolder.imageView_right.setVisibility(View.INVISIBLE);
                viewHolder.imageView_right.setLayoutParams(new RelativeLayout.LayoutParams(0,0));
                viewHolder.imageView_left.setVisibility(View.VISIBLE);
                Glide.with(viewHolder.imageView_right.getContext()).load(c.getMessage()).into(viewHolder.imageView_left);
                viewHolder.imageView_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent imageShow= new Intent(viewHolder.imageView_right.getContext(),ShowImage.class);
                        imageShow.putExtra("image_url",c.getMessage());
                        viewHolder.imageView_right.getContext().startActivity(imageShow);

                    }
                });
            }


        }
    }
    @Override
    public int getItemCount(){
        return  mMessageList.size();
    }
}
