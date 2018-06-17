package com.example.saurav.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ShowImage extends AppCompatActivity {
    private ImageView fullimageview;
    private PhotoViewAttacher zoomimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        getSupportActionBar().hide();
        String image_url=getIntent().getStringExtra("image_url");
        fullimageview=(ImageView)findViewById(R.id.fullimageview);
        Glide.with(fullimageview.getContext()).load(image_url).into(fullimageview);
        zoomimage = new PhotoViewAttacher(fullimageview);

    }
}
