package com.example.saurav.login;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SecondActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Button logout;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private DatabaseReference userdatabase;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        userdatabase=FirebaseDatabase.getInstance().getReference();

        getSupportActionBar().setTitle("Home");

        TabLayout tablayout = findViewById(R.id.tabs);
        ViewPager viewpager =findViewById(R.id.viewpager);

        SectionsPagerAdapter pager = new SectionsPagerAdapter(getSupportFragmentManager());
        viewpager.setAdapter(pager);
        tablayout.setupWithViewPager(viewpager);



        firebaseAuth = FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();
    }



    public void Logout(){

        firebaseAuth.signOut();

        finish();
        startActivity(new Intent(SecondActivity.this,MainActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.logout:
                Logout();
                break;
            case R.id.profile:
                startActivity(new Intent(SecondActivity.this,ProfileActivity.class));
                break;
            case R.id.allusers:
                startActivity(new Intent(SecondActivity.this,UsersActivity.class));

        }
        return super.onOptionsItemSelected(item);
    }
}
