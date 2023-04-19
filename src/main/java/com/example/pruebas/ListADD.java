package com.example.pruebas;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;

public class ListADD extends AppCompatActivity {

    ListView listView;
    ArrayList<String> list,words;
    ArrayAdapter adapter;
    String textoPlantas = "";


    private String LoadName() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String savedValue = sharedPreferences.getString("key","");
        return savedValue;
    }


    private void Escribir(String Planta) {
        if(textoPlantas == "") {

            textoPlantas = Planta + ",";

        }else {
            textoPlantas = textoPlantas + Planta + ",";

        }

    }

    private void Borrar(String pl) {
        String nuevo = "";
        String[] parts = textoPlantas.split(",");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i]!=""){
                Log.e("Partes", parts[i]);
                if (!pl.equals(parts[i])) {

                    if(nuevo == "") {

                        nuevo = parts[i] + ",";

                    }else {
                        nuevo = nuevo + parts[i] + ",";
                    }
                }
            }
        }
        textoPlantas = nuevo;

    }

    private void writeToFile(String data) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String savedValue = LoadName();
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Grupos/";

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput(rootPath +  savedValue + ".txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static String loadTextFromAssets(Context context, String assetsPath, Charset charset) throws IOException {
        InputStream is = context.getResources().getAssets().open(assetsPath);
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int length = is.read(buffer); length != -1; length = is.read(buffer)) {
            baos.write(buffer, 0, length);
        }
        is.close();
        baos.close();
        return charset == null ? new String(baos.toByteArray()) : new String(baos.toByteArray(), charset);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        // assigning ID of the toolbar to a variable
        Toolbar toolbar = findViewById(R.id.toolbar);

        // using toolbar as ActionBar
        setSupportActionBar(toolbar);

        String st = null;
        try {
            st = loadTextFromAssets(getApplicationContext(),"PLANTAS0.txt", Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }


        listView = findViewById(R.id.list_view);

        list = new ArrayList<>();
        words = new ArrayList<>();

        Scanner scanner = new Scanner(st);

        while (scanner.hasNextLine()) {

            String line = scanner.nextLine();
            words.add(line);


        }
        scanner.close();

        for(int i = 0;i < words.toArray().length;i++){
            String[] corte = words.toArray()[i].toString().split(",");
            list.add(corte[0]);
        }


        //list.add(splited_text[0]);
        ImageView img = ( ImageView) findViewById(R.id.imageView2);

         img.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                //save in the file the string
                startActivity(new Intent(ListADD.this, ListPeople.class));
            }
        });


        listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE);

        adapter = new ArrayAdapter(ListADD.this,android.R.layout.simple_list_item_single_choice,list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                if(listView.isItemChecked(i)){

                    Toast.makeText(ListADD.this, "Selected -> " + list.toArray()[i].toString(), Toast.LENGTH_SHORT).show();
                    Escribir(list.toArray()[i].toString());
                }else{

                    Borrar(list.toArray()[i].toString());
                }


            }
        });
    }
}
