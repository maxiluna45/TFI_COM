package com.example.tficom;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class Recepcion extends AppCompatActivity {

    int requestCode = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recepcion);
    }

    public void onActivityResult(int _requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(_requestCode, resultCode, data);
        Context context = getApplicationContext();
        if (_requestCode == requestCode && resultCode == Activity.RESULT_OK)
        {
            if (data == null)
            {
                return;
            }
            Uri uri = data.getData();
            String path = uri.getPath();
            if(!path.contains("video"))
            {
                Toast.makeText(context,"Error, solo se pueden seleccionar videos",Toast.LENGTH_SHORT).show();
            }
            else{
                    VideoView videoView = findViewById(R.id.videoView);
                    Toast.makeText(context,uri.getPath(),Toast.LENGTH_SHORT).show();
                    loadVideo(uri, videoView);

                }
        }
    }

    public void OpenFileChooser(View view)
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent,requestCode);
    }



    public void loadVideo(Uri video, VideoView _videoView)
    {
        _videoView.setVideoURI(video);
        MediaController mediaController = new MediaController(this);
        _videoView.setMediaController(mediaController);
        mediaController.setAnchorView(_videoView);
        _videoView.start();
    }

}