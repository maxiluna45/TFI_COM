package com.example.tficom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class MessageView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_view);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String message = extras.getString("Msg");
            TextView text = findViewById(R.id.msg);
            text.setText("' "+message+" '");

            }

        }
    }
