package com.example.pruebas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

public class MoveGroup extends AppCompatActivity {



    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Button button = (Button) findViewById(R.id.button6);
        Button button2 = (Button) findViewById(R.id.button7);

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startActivity(new Intent(MoveGroup.this, newGroup.class));
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {


                startActivity(new Intent(MoveGroup.this, newGroup.class));
            }
        });
    }
}
