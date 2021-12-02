package com.example.tficom;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class Recepcion extends AppCompatActivity {

    private AV_FrameCapture mFrameCapture = null;
    boolean USE_MEDIA_META_DATA_RETRIEVER = false;
    int requestCode = 1;
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
        Context context = getApplicationContext();

        if (_requestCode == requestCode && resultCode == Activity.RESULT_OK)
        {
            if (data == null)
            {
                return;
            }
            Uri uri = data.getData();
            String path = uri.getPath();
            iterateVideo(uri,context);



                }
        }
        // After camera screen this code will excuted
        /*
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

            Uri uri = data.getData();
            iterateVideo(uri, ActivityContext);
        }
    }*/



    public void OpenFileChooser(View view)
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent,requestCode);
    }



    public void iterateVideo(Uri uri, Context context) {
        // 1000000
        FFmpegMediaMetadataRetriever med = new FFmpegMediaMetadataRetriever();
        //MediaMetadataRetriever mee = new MediaMetadataRetriever();
        //med.setDataSource(context, uri);
        //mee.setDataSource(context, uri);
        //String path = getPath(context, uri);
        med.setDataSource("file:///storage/emulated/0/Pictures/MyCameraVideo/VID_20211201_205201.mp4");
        //med.setDataSource(uri.toString());
        String time = med.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);
        int videoLenght = (Integer.parseInt(time)/1000);
        int frameNumber = videoLenght * 30;
        ArrayList<Float> bmRGB = new ArrayList<>();
        ArrayList<String> bmBits = new ArrayList<>();
        float maxLuminance = 0;
        float minLuminance = 1;

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
        boolean first = true;
        for(long i = 1; i < frameNumber+1; i++){
            int averageColor;
            float luminance = 0;

            Bitmap bmp = med.getFrameAtTime((i*33333), FFmpegMediaMetadataRetriever.OPTION_CLOSEST);

            saveImageToGallery(bmp);
            averageColor = getRGBAverage(bmp);
            //String hexColor = String.format("#%06X", (0xFFFFFF & averageColor));
            //Log.i("Color",hexColor);
            //averageColor = getRGBPixel(bmp);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                luminance = getRelativeLuminance(averageColor);
                bmRGB.add(luminance);
                if (first)
                {
                    maxLuminance = luminance;
                    minLuminance = luminance;
                    first = false;
                }
                if (luminance >= maxLuminance)
                    maxLuminance = luminance;
                else if (luminance <= minLuminance)
                    minLuminance = luminance;
            }

        }
        //imprimirFrames(bmRGB, maxLuminance, minLuminance);
        bmBits = getBits(bmRGB, maxLuminance, minLuminance);
        getMsg(bmBits, maxLuminance, minLuminance);
    }

    private ArrayList<String> getBits(ArrayList<Float> bmRGB, float maxLuminance, float minLuminance) {
        float averageLuminance = ((maxLuminance + minLuminance) / 2);
        ArrayList<String> bmBits = new ArrayList<>();
        for(int i=0; i < bmRGB.size();i++)
        {
            String bit = getWord(averageLuminance, bmRGB.get(i));
            bmBits.add(bit);
        }
        return bmBits;
    }

    private void imprimirFrames(ArrayList<Float> bmRGB, float maxLuminance, float minLuminance){
        float averageLuminance = ((maxLuminance + minLuminance) / 2);
        Log.i("ValorMax: ", String.valueOf(maxLuminance));
        Log.i("ValorMin: ", String.valueOf(minLuminance));
        Log.i("ValorPromedio", String.valueOf(averageLuminance));
        Log.i("Tamaño: ",String.valueOf(bmRGB.size()));
        for (int i = 0; i < bmRGB.size();i++){
            Log.i("Valor: ", getWord(averageLuminance, bmRGB.get(i)));
            Log.i("Luminancia: ", String.valueOf(bmRGB.get(i)));
        }
    }


    private void getMsg(ArrayList<String> bmBits, float maxLuminance, float minLuminance) {
        boolean startFound = false;
        int startPosition = 0;
        String bitSecuence = "";
        float averageLuminance = ((maxLuminance + minLuminance) / 2);
        Log.i("ValorMax: ", String.valueOf(maxLuminance));
        Log.i("ValorMin: ", String.valueOf(minLuminance));
        Log.i("ValorPromedio", String.valueOf(averageLuminance));
        Log.i("Tamaño: ",String.valueOf(bmBits.size()));

        startPosition = searchStart(bmBits);
        if (startPosition!= -1) {
            startFound = true;
            for(int i = startPosition + 4; i < bmBits.size(); i+= 3)
            {
                bitSecuence += bmBits.get(i);
            }
            MsgDecodification((bitSecuence));
        }
        else
            Toast.makeText(this, "No se encontro el bit de start", Toast.LENGTH_SHORT).show();

    }

    private int searchStart(ArrayList<String> bmBits) {
        for(int i = 0; i < bmBits.size() - 18;i++)
        {
            String start = bmBits.get(i) + bmBits.get(i+3) + bmBits.get(i+6) + bmBits.get(i+9) + bmBits.get(i+12) + bmBits.get(i+15) + bmBits.get(i+18);
            if(start.equals("1000010"))
                return i+18;
        }
        return -1;
    }

    private String getWord(float averageLuminance, float luminance) {

        if(luminance < averageLuminance){
            return "0";
        } else{
            return "1";
        }
    }

    private void MsgDecodification(String bitSecuence){
        String bitFrame = "";
        String message = "";
        String symbol = "";
        int count = 0;
        boolean errorFlag = false;
        for (int i = 0; i < bitSecuence.length(); i++){
            if (count < 7){
                bitFrame += bitSecuence.charAt(i);
                count += 1;
            } else {
                symbol = bitFrameDecodification(bitFrame);
                if (symbol == "$"){
                    errorFlag = true;
                    break;
                }
                message += symbol;
                count = 0;
                bitFrame = "";
            }

        }
        if (errorFlag){
            viewMsg(message);
        } else {
            //RepeatAlert();
        }
    }

    private void viewMsg(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private String bitFrameDecodification(String bitFrame){
        boolean parity = ParityControl(bitFrame);
        if (parity){
            return "$";
        }
        else{
            String symbol;
            switch (bitFrame.substring(0,5)) {

                case "111111":
                    symbol = "a";
                    break;
                case "111110":
                    symbol = "b";
                    break;
                case "111100":
                    symbol = "c";
                    break;
                case "111101":
                    symbol = "d";
                    break;
                case "111011":
                    symbol = "e";
                    break;
                case "111010":
                    symbol = "f";
                    break;
                case "111001":
                    symbol = "g";
                    break;
                case "111000":
                    symbol = "h";
                    break;
                case "110111":
                    symbol = "i";
                    break;
                case "110110":
                    symbol = "j";
                    break;
                case "110101":
                    symbol = "k";
                    break;
                case "110100":
                    symbol = "l";
                    break;
                case "110011":
                    symbol = "m";
                    break;
                case "110010":
                    symbol = "n";
                    break;
                case "110001":
                    symbol = "ñ";
                    break;
                case "110000":
                    symbol = "o";
                    break;
                case "101111":
                    symbol = "p";
                    break;
                case "101110":
                    symbol = "q";
                    break;
                case "101101":
                    symbol = "r";
                    break;
                case "101100":
                    symbol = "s";
                    break;
                case "101011":
                    symbol = "t";
                    break;
                case "101010":
                    symbol = "u";
                    break;
                case "101001":
                    symbol = "v";
                    break;
                case "101000":
                    symbol = "w";
                    break;
                case "100111":
                    symbol = "x";
                    break;
                case "100110":
                    symbol = "y";
                    break;
                case "100101":
                    symbol = "z";
                    break;
                case "011110":
                    symbol = "0";
                    break;
                case "011101":
                    symbol = "1";
                    break;
                case "011100":
                    symbol = "2";
                    break;
                case "011011":
                    symbol = "3";
                    break;
                case "011010":
                    symbol = "4";
                    break;
                case "011001":
                    symbol = "5";
                    break;
                case "011000":
                    symbol = "6";
                    break;
                case "010111":
                    symbol = "7";
                    break;
                case "010110":
                    symbol = "8";
                    break;
                case "010101":
                    symbol = "9";
                    break;
                case "100100":
                    symbol = " ";
                    break;
                case "100010":
                    symbol = ",";
                    break;
                case "100011":
                    symbol = ".";
                    break;

                default:
                    throw new IllegalStateException("Error de decodificación");
            }

            return symbol;
        }
    }

    private boolean ParityControl(String bitFrame){
        int sum = 0;
        for (int i = 0; i < bitFrame.length(); i++){
            if (bitFrame.charAt(i) == '1') {
                sum += 1;
            } else {
                continue;
            }
        }
        if (sum % 2 == 1) {
            return false;
        } else {
            return true;
        }
    }


    @ColorInt
    private int getRGBPixel(Bitmap bm){
        long redColor = 0;
        long greenColor = 0;
        long blueColor= 0;
        int average = 0;

        if (bm != null)
        {
            Bitmap bitmap = scaleDown(bm, 1,true);
            int px = bitmap.getPixel(0, 0);
            redColor = Color.red(px);
            greenColor = Color.green(px);
            blueColor = Color.blue(px);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                average = Color.rgb(redColor, greenColor, blueColor);

            }
        }
        return average;
    }
    @ColorInt
    private int getRGBAverage(Bitmap bm){
        int redColor = 0;
        int greenColor = 0;
        int blueColor= 0;
        int pixelCount = 0;
        int average = 0;

        if (bm != null)
        {
            Bitmap bitmap = scaleDown(bm, 10,true);
            for(int i = 0; i < bitmap.getWidth(); i++)
            {
                for(int j = 0; j < bitmap.getHeight();j++)
                {
                    int px = bitmap.getPixel(i, j);
                    pixelCount ++;
                    redColor += Color.red(px);
                    greenColor += Color.green(px);
                    blueColor += Color.blue(px);
                }
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int averageRed = redColor/pixelCount;
                int averageGreen = greenColor/pixelCount;
                int averageBlue = blueColor/pixelCount;
                average = Color.rgb(averageRed, averageGreen, averageBlue);


            }
        }
        return average;
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    @SuppressLint("SupportAnnotationUsage")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @ColorInt
    private float getRelativeLuminance(@ColorInt int color){

        return (float) ColorUtils.calculateLuminance(color);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Color.luminance(color);
        }*/
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