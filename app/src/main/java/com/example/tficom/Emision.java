package com.example.tficom;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class Emision extends AppCompatActivity {

    boolean hasFlash = false;
    boolean flashEncendido = false;
    EditText text;
    String mensaje;
    int bitTime = 194;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emision);

        // Llama al administrador de paquetes para preguntar si el celular tiene flash
        // Si no tiene, envía un aviso

        EditText time = (EditText)findViewById(R.id.bitTime);
        time.setText(String.valueOf(bitTime));


        hasFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (hasFlash) {
            //flashOn();
        } else
            Toast.makeText(Emision.this, "El dispositivo no cuenta con Flash", Toast.LENGTH_SHORT).show();

    }

    InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                int type = Character.getType(source.charAt(i));
                //System.out.println("Type : " + type);
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    return "";
                }
            }
            return null;
        }
    };



    @RequiresApi(api = Build.VERSION_CODES.M)
    private void flashOn() {
        // Funcion que prende el flash
        CameraManager cameraM = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            assert cameraM != null;
            String cameraId = cameraM.getCameraIdList()[0];
            cameraM.setTorchMode(cameraId, true);
            flashEncendido = true;
        } catch (CameraAccessException ex) {
            Log.e("Error en la camara", "No se pudo encender la linterna");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void flashOff() {
        // Funcion que apaga el flash
        CameraManager cameraM = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            assert cameraM != null;
            String cameraId = cameraM.getCameraIdList()[0];
            cameraM.setTorchMode(cameraId, false);
            flashEncendido = false;
        } catch (CameraAccessException ex) {
            Log.e("Error en la camara", "No se pudo apagar la linterna");
        }
    }

    public void getMessage(View view) {
        // Funcion que toma el mensaje escrito, luego de colocar enviar
        text = findViewById(R.id.edx_mensaje);
        text.setFilters(new InputFilter[]{filter});
        mensaje = text.getText().toString().trim();
        mensaje = Normalizer.normalize(mensaje, Normalizer.Form.NFD);
        mensaje = mensaje.replaceAll("[^\\p{ASCII}]", "");

        String validateMsg = mensaje.replace(" ", "");

        if (!validateMsg.matches("^[a-z0-9,.?]+$")) {
            Toast.makeText(Emision.this, "Se ingreso un caracter invalido", Toast.LENGTH_SHORT).show();
        }
        else{
            transformMessage(mensaje);}
    }

    public void transformMessage(String message) {
        // Transformar mensaje de texto a codigo
        int counter = 0;
        String nuevo = "1000010"; // Añado caracter START

        for (int i = 0; i < message.length(); i++) {
            Character letra = message.charAt(i);

            counter ++;
            nuevo += codificarChar(letra);
            if(((counter % 2) == 0) && counter != 0){
                nuevo += "1000010";

            }

        }

        nuevo += "1000001"; //Añado caracter END

        String finalNuevo = nuevo;
        Runnable objRunnable = new Runnable() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                Button btnSend = findViewById(R.id.sendMsg);
                btnSend.setClickable(false);
                emitirMsg(finalNuevo);

                }
            };

        Thread objBgThread = new Thread(objRunnable);
        objBgThread.start();
    }


    public String agregarParidad(String charCodificado) {
        // Funcion que chequea la cantidad de 1's y agrega o no otro, segun si es impar o par
        // dicha cantidad

        int contador = 0;

        for (int i = 0; i < charCodificado.length(); i++) {
            if (charCodificado.charAt(i) == '1')
                contador += 1;
        }
        if (contador % 2 == 1) {
            return "1";
        } else {
            return "0";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void emitirAlerta1(String mensaje) {

        // Emite la alarta inicial

        for (int i = 0; i < mensaje.length(); i++) {
            Character letra = mensaje.charAt(i);
            if (letra == '1')
                if (!flashEncendido)
                    flashOn();
            if (letra == '0')
                if (flashEncendido)
                    flashOff();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (flashEncendido)
            flashOff();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void emitirAlerta2(String mensaje) {

        // Emite la alerta secundaria

        for (int i = 0; i < mensaje.length(); i++) {
            Character letra = mensaje.charAt(i);
            if (letra == '1')
                if (!flashEncendido)
                    flashOn();
            if (letra == '0')
                if (flashEncendido)
                    flashOff();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (flashEncendido)
            flashOff();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void emitirMsg(String mensaje) {

        // Se llaman a las dos alertas

        emitirAlerta1("101010101"); //Alerta 1
        emitirAlerta2("101010101"); //Alerta 2

        // Se envia el mensaje

        for (int i = 0; i < mensaje.length(); i++) {
            Character letra = mensaje.charAt(i);
            if (letra == '1')
                if (!flashEncendido)
                    flashOn();
            if (letra == '0')
                if (flashEncendido)
                    flashOff();
            try {
                Thread.sleep(bitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Button btnSend = findViewById(R.id.sendMsg);
        btnSend.setClickable(true);
        if (flashEncendido)
            flashOff();
;
    }

    public void changeBitTime(View view){
        EditText time = (EditText)findViewById(R.id.bitTime);
        bitTime = Integer.parseInt(time.getText().toString());
        Toast.makeText(Emision.this, "Tiempo del bit cambiado a: "+ String.valueOf(bitTime),Toast.LENGTH_SHORT).show();
    }

    public String codificarChar(Character letra) {
        // Funcion que, dado un caracter, devuelve su equivalente en el codigo

        String charCodificado;
        switch (Character.toLowerCase(letra)) {

            case 'a':
                charCodificado = "111111";
                break;
            case 'b':
                charCodificado = "111110";
                break;
            case 'c':
                charCodificado = "111101";
                break;
            case 'd':
                charCodificado = "111100";
                break;
            case 'e':
                charCodificado = "111011";
                break;
            case 'f':
                charCodificado = "111010";
                break;
            case 'g':
                charCodificado = "111001";
                break;
            case 'h':
                charCodificado = "111000";
                break;
            case 'i':
                charCodificado = "110111";
                break;
            case 'j':
                charCodificado = "110110";
                break;
            case 'k':
                charCodificado = "110101";
                break;
            case 'l':
                charCodificado = "110100";
                break;
            case 'm':
                charCodificado = "110011";
                break;
            case 'n':
                charCodificado = "110010";
                break;
            case 'ñ':
                charCodificado = "110001";
                break;
            case 'o':
                charCodificado = "110000";
                break;
            case 'p':
                charCodificado = "101111";
                break;
            case 'q':
                charCodificado = "101110";
                break;
            case 'r':
                charCodificado = "101101";
                break;
            case 's':
                charCodificado = "101100";
                break;
            case 't':
                charCodificado = "101011";
                break;
            case 'u':
                charCodificado = "101010";
                break;
            case 'v':
                charCodificado = "101001";
                break;
            case 'w':
                charCodificado = "101000";
                break;
            case 'x':
                charCodificado = "100111";
                break;
            case 'y':
                charCodificado = "100110";
                break;
            case 'z':
                charCodificado = "100101";
                break;
            case '0':
                charCodificado = "011110";
                break;
            case '1':
                charCodificado = "011101";
                break;
            case '2':
                charCodificado = "011100";
                break;
            case '3':
                charCodificado = "011011";
                break;
            case '4':
                charCodificado = "011010";
                break;
            case '5':
                charCodificado = "011001";
                break;
            case '6':
                charCodificado = "011000";
                break;
            case '7':
                charCodificado = "010111";
                break;
            case '8':
                charCodificado = "010110";
                break;
            case '9':
                charCodificado = "011111";
                break;
            case ' ':
                charCodificado = "100100";
                break;
            case ',':
                charCodificado = "100010";
                break;
            case '.':
                charCodificado = "100011";
                break;

            default:
                throw new IllegalStateException("Carácter inválido: " + letra);
        }

        charCodificado += agregarParidad(charCodificado);

        return charCodificado;
    }
}






