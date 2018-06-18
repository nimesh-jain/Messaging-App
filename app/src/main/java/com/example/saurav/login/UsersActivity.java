package com.example.saurav.login;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private ProgressBar editprog;
    private CircleImageView imageView;
    private RecyclerView mUsersList;
    private DatabaseReference mUsersDatabase;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mauth=FirebaseAuth.getInstance();
        getSupportActionBar().setTitle("All Users");
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        firebaseFirestore=FirebaseFirestore.getInstance();
        imageView=(CircleImageView)findViewById(R.id.user_single_image);

        mUsersList=(RecyclerView)findViewById(R.id.users_list);

        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<userprofile> options =
                new FirebaseRecyclerOptions.Builder<userprofile>()
                        .setQuery(mUsersDatabase, userprofile.class)
                        .build();
        FirebaseRecyclerAdapter<userprofile,UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<userprofile, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull userprofile model) {


                final String user_id=getRef(position).getKey();
                if(user_id.equals(mauth.getUid().toString())){

                    //holder.mview.setVisibility(View.GONE);
                    holder.Layout_hide();
                }
                else
                {
                    holder.setName(model.getName());
                    holder.setEmail(model.getEmail());
                    holder.setImage(model.getImage_url(),getApplicationContext());
                }



                holder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent userprofileintent = new Intent(UsersActivity.this,Detailsinfo.class);
                        userprofileintent.putExtra("user_id",user_id);
                        startActivity(userprofileintent);

                    }
                });
            }

            @Override
            public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_single_layout, parent, false);

                return new UsersViewHolder(view);
            }
        };


        mUsersList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }


    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        final GridLayoutManager.LayoutParams params;

        View mview;
        public UsersViewHolder(View itemView) {
            super(itemView);
            mview=itemView;

            params = new GridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        public  void setName(String name){
            TextView userNameView =(TextView)mview.findViewById(R.id.user_name_view);
            userNameView.setText(name);
        }
        public  void setEmail(String email){
            TextView userEmailView =(TextView)mview.findViewById(R.id.user_email_view);
            userEmailView.setText(email);
        }
        public  void setImage(final String image_url, final Context ctx){
            final CircleImageView circleImageView=(CircleImageView)mview.findViewById(R.id.user_single_image);
            Picasso.with(ctx).load(image_url).networkPolicy(NetworkPolicy.OFFLINE).into(circleImageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(image_url).into(circleImageView);
                }
            });
        }

        private void Layout_hide() {
            params.height = 0;
            mview.setLayoutParams(params); //This One

        }
    }
}
