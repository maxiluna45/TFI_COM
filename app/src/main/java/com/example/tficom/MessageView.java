package com.example.tficom;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MessageView extends AppCompatActivity {

    static boolean flashEncendido = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_view);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String message = extras.getString("Msg");
            Boolean errorFlag = extras.getBoolean("ErrorFlag");
            if (errorFlag){
                Toast.makeText(MessageView.this,"Existen errores de paridad en uno o " +
                        "más caracteres, se mostrarán con '*'",Toast.LENGTH_SHORT).show();
            }
            TextView text = findViewById(R.id.msg);
            text.setText("' " + message + " '");

        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void retransmissionAlert(View view) {
        // Emite la alerta de retransmisión

        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 3; i++) {
                flashOn();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                flashOff();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void correctMessage(View view) {
        // Emite la alerta para indicar que el mensaje llegó correctamente,
        // o con errores, pero es legible por el usuario

        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 2; i++) {
                flashOn();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                flashOff();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

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
}
