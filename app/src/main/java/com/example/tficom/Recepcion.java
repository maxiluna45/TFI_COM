package com.example.tficom;

import static com.example.tficom.BuildConfig.DEBUG;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class Recepcion extends AppCompatActivity {

    private AV_FrameCapture mFrameCapture = null;
    boolean USE_MEDIA_META_DATA_RETRIEVER = false;
    //int requestCode = 1;
    private Uri fileUri;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    public static Recepcion ActivityContext = null;
    public static TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recepcion);

        ActivityContext = this;
        Button btn_grabar = (Button)findViewById(R.id.btn_grabar);
        output = (TextView)findViewById(R.id.output);
        checkExternalStoragePermission();

    }


    private static Uri getOutputMediaFileUri(int type){

        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){

        // Check that the SDCard is mounted
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraVideo");
        // Create the storage directory(MyCameraVideo) if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                output.setText("Failed to create directory MyCameraVideo.");
                Toast.makeText(ActivityContext, "Failed to create directory MyCameraVideo.",
                        Toast.LENGTH_LONG).show();
                Log.d("MyCameraVideo", "Failed to create directory MyCameraVideo.");
                return null;
            }
        }
        // Create a media file name

        // For unique file name appending current timeStamp with file name
        java.util.Date date= new java.util.Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(date.getTime());
        File mediaFile;
        if(type == MEDIA_TYPE_VIDEO) {
            // For unique video file name appending current timeStamp with file name
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }


    public void onActivityResult(int _requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(_requestCode, resultCode, data);
        /* Context context = getApplicationContext();

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
                    // Recorrer frames
                    iterateVideo(uri, context);

                }
        }*/
        // After camera screen this code will excuted

        if (_requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {

                output.setText("Video File : " +data.getData());

                // Video captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Video saved to: " +
                        data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                output.setText("User cancelled the video capture.");
                // User cancelled the video capture
                Toast.makeText(this, "User cancelled the video capture.",
                        Toast.LENGTH_LONG).show();
            } else {
                output.setText("Video capture failed.");
                // Video capture failed, advise user
                Toast.makeText(this, "Video capture failed.",
                        Toast.LENGTH_LONG).show();
            }

            Uri path = data.getData();
            iterateVideo(path, ActivityContext);
        }
    }



    public void OpenFileChooser(View view)
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        //startActivityForResult(intent,requestCode);
    }



    public void loadVideo(Uri video, VideoView _videoView)
    {
        _videoView.setVideoURI(video);
        MediaController mediaController = new MediaController(this);
        _videoView.setMediaController(mediaController);
        mediaController.setAnchorView(_videoView);
        _videoView.start();
    }

    public void iterateVideo(Uri uri, Context context) {
        // 1000000
        FFmpegMediaMetadataRetriever med = new FFmpegMediaMetadataRetriever();
        //MediaMetadataRetriever mee = new MediaMetadataRetriever();
        //med.setDataSource(context, uri);
        //mee.setDataSource(context, uri);
        //String path = getPath(context, uri);

        med.setDataSource(uri.toString());
        String time = med.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);
        int videoLenght = (Integer.parseInt(time)/1000);
        int frameNumber = videoLenght * 30;
        ArrayList<Integer> bmRGB = new ArrayList<>();
        int maxLuminance = 0;
        int minLuminance = 0;

        // AV_FRAMECAPTURE
        /*for (long i = 1; i < frameNumber+1; i++)
        {
            String path = getPath(context, uri);
            captureFrame(path,(33*i),1920,1080);
        }*/


        // MEDIA METADATA RETRIEVER
        /*for (long i = 1; i < frameNumber+1; i++)
        {
            Bitmap bmp = mee.getFrameAtTime((i*33333), MediaMetadataRetriever.OPTION_CLOSEST);
            saveToInternalStorage(bmp);
        }*/


        // FFMPEG
        for(long i = 1; i < frameNumber+1; i++){
            int averageColor;
            int luminance = 0;

            Bitmap bmp = med.getFrameAtTime((i*33333), FFmpegMediaMetadataRetriever.OPTION_CLOSEST);


            averageColor = getRGBAverage(bmp);
            bmRGB.add(averageColor);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                luminance = getRelativeLuminance(averageColor);
                if (luminance >= maxLuminance)
                    maxLuminance = luminance;
                else if (luminance <= minLuminance)
                    maxLuminance = luminance;
            }

        }

        getMsg(bmRGB, maxLuminance, minLuminance);
    }

    private void getMsg(ArrayList<Integer> bmRGB, int maxLuminance, int minLuminance) {
        boolean startFound = false;
        String[] start = new String[21];
        String startBits = "";
        int averageLuminance = ((maxLuminance + minLuminance) / 2)/2;
        for (int i = 0; i < bmRGB.size();i++)
        {
            if(!startFound)
            {
                if(!start[20].equals(""))
                {
                    start[i] = getWord(averageLuminance, bmRGB.get(i));
                }
                else
                    {
                        startBits += start[0] + start[2] + start[5] + start[8] + start[11] + start[14] + start[17] + start[20];
                        if(startBits.equals("1000010"))
                            startFound = true;
                        else
                            {
                                for(int k = 0; k < start.length; k++)
                                {
                                    if(k == 20)
                                        start[20] = "";
                                    else
                                        start[k] = start[k+1];
                                }
                            }
                    }
            }
            else
                {
                    // OBTENER MENSAJE
                }
        }
    }

    private String getWord(int averageLuminance, Integer integer) {
        if()
    }

    private int getRGBAverage(Bitmap bitmap){
        long redColor = 0;
        long greenColor = 0;
        long blueColor= 0;
        long pixelCount = 0;
        int average = 0;

        for(int i = 0; i < bitmap.getHeight(); i++)
        {
            for(int j = 0; j < bitmap.getWidth();j++)
            {
                int px = bitmap.getPixel(i, j);
                pixelCount ++;
                redColor = Color.red(px);
                greenColor = Color.green(px);
                blueColor = Color.blue(px);
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            average = Color.rgb(redColor/pixelCount, greenColor/pixelCount, blueColor/pixelCount);

        }
        return average;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private int getRelativeLuminance(int number){
        return (int) Color.luminance(number);
    }

    private void captureFrame(String VIDEO_FILE_PATH, long SNAPSHOT_DURATION_IN_MILLIS, int SNAPSHOT_WIDTH, int SNAPSHOT_HEIGHT) {

        // getFrameAtTimeByMMDR & getFrameAtTimeByFrameCapture function uses a micro sec 1millisecond = 1000 microseconds

        Bitmap bmp = USE_MEDIA_META_DATA_RETRIEVER ? getFrameAtTimeByMMDR(VIDEO_FILE_PATH, (SNAPSHOT_DURATION_IN_MILLIS * 1000))
                : getFrameAtTimeByFrameCapture(VIDEO_FILE_PATH, (SNAPSHOT_DURATION_IN_MILLIS * 1000), SNAPSHOT_WIDTH, SNAPSHOT_HEIGHT);

        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(new Date());
        if (null != bmp) {
            AV_BitmapUtil.saveBitmap(bmp, String.format("/sdcard/read_%s.jpg", timeStamp));
        }

        if (mFrameCapture != null) {
            mFrameCapture.release();
        }
    }

    private Bitmap getFrameAtTimeByMMDR(String path, long time) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        Bitmap bmp = mmr.getFrameAtTime(time, MediaMetadataRetriever.OPTION_CLOSEST);
        mmr.release();
        return bmp;
    }

    private Bitmap getFrameAtTimeByFrameCapture(String path, long time, int snapshot_width, int snapshot_height) {
        mFrameCapture = new AV_FrameCapture();
        mFrameCapture.setDataSource(path);
        mFrameCapture.setTargetSize(snapshot_width, snapshot_height);
        mFrameCapture.init();
        return mFrameCapture.getFrameAtTime(time);
    }

    public void grabar(View v) {
        // Crea un nuevo Intent con un IntentAaction que puede ser enviado
        // para hacer que la camara capture un video, y lo returne

        // Estas siguientes lineas son para forzar el uso de la cámara y del almacenamiento interno
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        // Se crea un archivo para guardar el video
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

        // Se incluye el nombre del archivo
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // Se setea la calidad del video
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        // Inicia el Intent para capturar el video
        startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
    }

    private void checkExternalStoragePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para leer.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 225);
        } else {
            Log.i("Mensaje", "Se tiene permiso para leer!");
        }
    }

    // Funcion para guardar un bitmap en la galeria como un jpeg
    // No es necesaria, pero a fines de pruebas se incluye

    private void saveImageToGallery(Bitmap bitmap){

        OutputStream fos;

        try{

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){

                ContentResolver resolver = getContentResolver();
                ContentValues contentValues =  new ContentValues();
                Random generator = new Random();
                int n = 10000;
                n = generator.nextInt(n);
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Image_"+n + ".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "TestFolder");
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                Objects.requireNonNull(fos);

                //Toast.makeText(this, "Image Saved", Toast.LENGTH_SHORT).show();
            }

        }catch(Exception e){

            Toast.makeText(this, "Image not saved \n" + e.toString(), Toast.LENGTH_SHORT).show();
        }


    }


}