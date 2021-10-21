package com.example.tficom;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.Serializable;

public class Reproductor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);
        cargar();

    }
    VideoView videoView = (VideoView) findViewById(R.id.reproductor);

    public void cargar(){
        String parameter = getIntent().getStringExtra("VideoUri");
        Uri video = Uri.parse(parameter);
       //this.loadVideo(video);
    }

   /* public void loadVideo(Uri video)
    {
        videoView.setVideoURI(video);
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
    }*/
}