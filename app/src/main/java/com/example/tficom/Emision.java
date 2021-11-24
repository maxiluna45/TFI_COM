package com.example.tficom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class Emision extends AppCompatActivity {

    boolean tieneFlash = false;
    boolean flashEncendido = false;
    EditText text;
    String mensaje;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emision);
        Intent intent = getIntent();
        tieneFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (tieneFlash) {
            //flashOn();
        } else
            Toast.makeText(Emision.this, "El dispositivo no cuenta con Flash", Toast.LENGTH_SHORT).show();

    }


    private void flashOn() {
        CameraManager cameraM = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            assert cameraM != null;
            String cameraId = cameraM.getCameraIdList()[0];
            cameraM.setTorchMode(cameraId, true);
            flashEncendido = true;
            //Toast.makeText(Emision.this, "La Linterna esta prendida", Toast.LENGTH_SHORT).show();
        } catch (CameraAccessException ex) {
            Log.e("Error en la camara", "No se pudo encender la linterna");
        }
    }

    private void flashOff() {
        CameraManager cameraM = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            assert cameraM != null;
            String cameraId = cameraM.getCameraIdList()[0];
            cameraM.setTorchMode(cameraId, false);
            flashEncendido = false;
            //Toast.makeText(Emision.this, "La Linterna esta apagada", Toast.LENGTH_SHORT).show();
        } catch (CameraAccessException ex) {
            Log.e("Error en la camara", "No se pudo apagar la linterna");
        }
    }

    public void getMessage(View view) {
        text = findViewById(R.id.edx_mensaje);
        mensaje = text.getText().toString().trim();
        transformMessage(mensaje);
        //Toast.makeText(Emision.this, mensaje, Toast.LENGTH_SHORT).show();
    }

    public void transformMessage(String message) {
        // Transformar mensaje de texto a codigo
        // var codedMsg = codeMessage(message);
        String nuevo = "1000010"; // Añado caracter START
        for (int i = 0; i < message.length(); i++) {
            Character letra = message.charAt(i);
            nuevo += codificarChar(letra);
        }
        nuevo += "1000001"; //Añado caracter END

        emitirMsg(nuevo);
        //Toast.makeText(Emision.this, nuevo, Toast.LENGTH_SHORT).show();
    }

    public void emitirMsgggggg(String mensaje) {
        final Handler handler = new Handler(Looper.getMainLooper());
        for (int i = 0; i < mensaje.length(); i++) {
            Character letra = mensaje.charAt(i);
            if (letra == '1')
                if (!flashEncendido)
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            flashOn();
                        }
                    }, 100);
            if (letra == '0')
                if (flashEncendido)
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            flashOff();
                        }

                    }, 100);

        }
    }


    public String codificarChar(Character letra) {

        String charCodificado;
        switch (Character.toLowerCase(letra)) {

            case 'a':
                charCodificado = "111111";
                break;
            case 'b':
                charCodificado = "111110";
                break;
            case 'c':
                charCodificado = "111100";
                break;
            case 'd':
                charCodificado = "111101";
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
                charCodificado = "010101";
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

    public String agregarParidad(String charCodificado) {
        int contador = 0;
        for (int i = 0; i < charCodificado.length(); i++) {
            if (charCodificado.charAt(i) == '1') {
                contador += 1;
            }
        }
        if (contador % 2 == 1) {
            return "1";
        } else {
            return "0";
        }
    }

    public void emitirAlerta1(String mensaje) {
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

    public void emitirAlerta2(String mensaje) {
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


    public void emitirMsg(String mensaje) {

        emitirAlerta1("101010101"); //Alerta 1
        emitirAlerta2("101010101"); //Alerta 2

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
    }
}






