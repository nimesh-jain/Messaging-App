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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FreindsFragment extends Fragment {
    private RecyclerView friendrecyclerview;
    private View mainview;
    private String current_user_id;
    private FirebaseAuth mauth;
    private DatabaseReference frienddatabase;
    private DatabaseReference usersdatabase;


    public FreindsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mainview=inflater.inflate(R.layout.fragment_freinds, container, false);
        friendrecyclerview=(RecyclerView)mainview.findViewById(R.id.friendlistview);

        mauth=FirebaseAuth.getInstance();
        current_user_id=mauth.getCurrentUser().getUid();
        frienddatabase=FirebaseDatabase.getInstance().getReference().child("Friends").child(current_user_id);
        usersdatabase=FirebaseDatabase.getInstance().getReference().child("Users");




        friendrecyclerview.setHasFixedSize(true);
        friendrecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        return mainview;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(frienddatabase, Friends.class)
                        .build();
        FirebaseRecyclerAdapter<Friends,UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final UsersViewHolder holder, int position, @NonNull Friends model) {
                holder.setDate(model.getDate());
                //holder.setEmail(model.getEmail());
                //holder.setImage(model.getImage_url(),mainview.getContext());

                final String user_id=getRef(position).getKey();
                usersdatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String username= dataSnapshot.child("name").getValue().toString();
                        char arr;
                        arr=username.charAt(0);
                        arr=Character.toUpperCase(arr);
                        final String userimage= dataSnapshot.child("image_url").getValue().toString();
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
            public FreindsFragment.UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false);

                return new FreindsFragment.UsersViewHolder(view);
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
            TextView userNameView =(TextView)mview.findViewById(R.id.user_name_view);
            userNameView.setText(name);
        }
        public  void setDate(String date){
            TextView userDateView =(TextView)mview.findViewById(R.id.user_email_view);
            userDateView.setText(date);
        }
        public  void setImage(String image_url, Context ctx){
            CircleImageView circleImageView=(CircleImageView)mview.findViewById(R.id.user_single_image);
            Glide.with(ctx).load(image_url).into(circleImageView);
        }
    }
}
