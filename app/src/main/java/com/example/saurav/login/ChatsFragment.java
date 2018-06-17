package com.example.saurav.login;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {
    private RecyclerView friendrecyclerview;
    private View mainview;
    private String current_user_id;
    private static FirebaseAuth mauth;
    private DatabaseReference chatdatabase,messageDatabase;
    private DatabaseReference usersdatabase;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainview=inflater.inflate(R.layout.fragment_chats, container, false);
        friendrecyclerview=(RecyclerView)mainview.findViewById(R.id.chatlistview);


        mauth= FirebaseAuth.getInstance();
        current_user_id=mauth.getCurrentUser().getUid();
        chatdatabase= FirebaseDatabase.getInstance().getReference().child("Chat").child(current_user_id);
        messageDatabase=FirebaseDatabase.getInstance().getReference().child("messages").child(current_user_id);
        usersdatabase=FirebaseDatabase.getInstance().getReference().child("Users");




        friendrecyclerview.setHasFixedSize(true);
        friendrecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));


        return  mainview;
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Conv> options =
                new FirebaseRecyclerOptions.Builder<Conv>()
                        .setQuery(chatdatabase, Conv.class)
                        .build();
        FirebaseRecyclerAdapter<Conv,ChatsFragment.UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Conv, ChatsFragment.UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatsFragment.UsersViewHolder holder, int position, @NonNull Conv model) {
                //holder.setEmail(model.getEmail());
                //holder.setImage(model.getImage_url(),mainview.getContext());

                final String user_id=getRef(position).getKey();
                final Query messagequery = messageDatabase.child(user_id).limitToLast(1);
                messagequery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        final String message_popup= dataSnapshot.child("message").getValue().toString();
                        final String type=dataSnapshot.child("type").getValue().toString();
                        final String from =dataSnapshot.child("from").getValue().toString();
                        usersdatabase.child(user_id).child("name").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String name=dataSnapshot.getValue().toString();
                                holder.setMessagePopup(message_popup,type,from,name);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
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
                usersdatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String username= dataSnapshot.child("name").getValue().toString();
                        final String userimage= dataSnapshot.child("image_url").getValue().toString();

                        char arr;
                        arr=username.charAt(0);
                        arr=Character.toUpperCase(arr);

                        holder.setName(arr+username.substring(1,username.length()));
                        holder.setImage(userimage,getContext());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




                holder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent userprofileintent = new Intent(getContext(),ChatActivity.class);
                        userprofileintent.putExtra("user_id",user_id);
                        startActivity(userprofileintent);

                    }
                });
            }

            @Override
            public ChatsFragment.UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_chat_single_layout, parent, false);

                return new ChatsFragment.UsersViewHolder(view);
            }
        };


        friendrecyclerview.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }
    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        View mview;
        public UsersViewHolder(View itemView) {
            super(itemView);
            mview=itemView;
        }
        public  void setName(String name){
            TextView userNameView =(TextView)mview.findViewById(R.id.user_chat_name_view);
            userNameView.setText(name);
        }
        public  void setImage(String image_url, Context ctx){
            CircleImageView circleImageView=(CircleImageView)mview.findViewById(R.id.user_chat_single_image);
            Glide.with(ctx).load(image_url).into(circleImageView);
        }
        public void setMessagePopup(String message,String type,String from,String name){
            if(type.equals("text")) {
                if(from.equals(mauth.getCurrentUser().getUid())) {
                    TextView message_pop = (TextView) mview.findViewById(R.id.message_pop);
                    if(message.length()>27) {
                        String text = message.substring(0, 27);
                        message_pop.setText("You: " + text + "...");
                    }
                    else{
                        message_pop.setText("You: " + message);
                    }
                }
                else{
                    TextView message_pop = (TextView) mview.findViewById(R.id.message_pop);

                    if(message.length()>27) {
                        String text = message.substring(0, 27);
                        message_pop.setText(text + "...");
                    }
                    else {
                        message_pop.setText(message);
                    }
                }
            }
            else{
                if(from.equals(mauth.getCurrentUser().getUid())) {
                    TextView message_pop = (TextView) mview.findViewById(R.id.message_pop);
                    message_pop.setText("You sent a photo");
                }
                else{
                    TextView message_pop = (TextView) mview.findViewById(R.id.message_pop);
                    message_pop.setText(name +" sent a photo");
                }
            }
        }
    }

}
