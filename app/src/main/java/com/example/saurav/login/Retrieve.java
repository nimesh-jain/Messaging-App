package com.example.saurav.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Retrieve extends AppCompatActivity {
    private ListView listView;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    userprofile user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve);
        listView=(ListView)findViewById(R.id.listView);
        user= new userprofile();
        list=new ArrayList<>();
        adapter =new ArrayAdapter<String>(this,R.layout.user_info,R.id.userInfo,list);
        database=FirebaseDatabase.getInstance();
        ref=database.getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    user = ds.getValue(userprofile.class);
                    list.add("Name: "+user.getName().toString()+"\n"+"Email: "+user.getEmail().toString());

                }
                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                if(position==1){
                    Intent i= new Intent(Retrieve.this,Detailsinfo.class);
                    i.putExtra("Users",listView.getItemAtPosition(position).toString());
                    startActivity(i);
                }
            }
        });
    }
}
