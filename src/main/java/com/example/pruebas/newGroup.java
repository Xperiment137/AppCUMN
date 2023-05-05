package com.example.pruebas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class newGroup extends AppCompatActivity {

    private FirebaseFirestore Db;


    private void addGroupToFirestore(String name,String lid) {

        Map<String, Object> group = new HashMap<>();
        group.put("Lider", lid);

        // creating a collection reference
        // for our Firebase Firestore database.
        CollectionReference grupos = Db.collection("Grupos");


        // below method is use to add data to Firebase Firestore.
        grupos.document(name).set(group).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void Avoid) {
                // after the data addition is successful
                // we are displaying a success toast message.
                Toast.makeText(newGroup.this, "Your Group has been added to DB", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(newGroup.this, ListADD.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // this method is called when the data addition process is failed.
                // displaying a toast message when data addition is failed.
                Toast.makeText(newGroup.this, "Fail to add Group \n" + e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SaveName(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }


    private String LoadEmail( String email) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String savedValue = sharedPreferences.getString(email,"");
        return savedValue;
    }


    private void ExistGroup(String name){
        DocumentReference docRef = Db.collection("Grupos").document(name);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Toast.makeText(newGroup.this, "Ya hay un grupo con ese nombre", Toast.LENGTH_SHORT).show();

                    } else {


                        CreateFile(name);
                    }
                } else {
                    Toast.makeText(newGroup.this, "Algo fallo, vuelva a intentarlo", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void CreateFile(String name) {

        if(name != "") {
            try {
                String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Grupos/";

                File root = new File(rootPath);
                if (!root.exists()) {
                    root.mkdirs();
                }
                File f = new File(rootPath + name + ".txt");
                if (f.exists()) {
                    Toast.makeText(newGroup.this, "Ya hay un grupo con ese nombre", Toast.LENGTH_SHORT).show();

                }else {
                    f.createNewFile();
                    FileOutputStream out = new FileOutputStream(f);
                    out.flush();
                    out.close();
                    SaveName("key",name);
                    addGroupToFirestore(name,LoadEmail("email"));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(newGroup.this, "El nombre del grupo no puede ser vacio", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_newgroup);
        EditText name = findViewById(R.id.nombre);
        Button button = (Button) findViewById(R.id.botonnext);

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
              if(!name.getText().toString().isEmpty()) {
                  ExistGroup(name.getText().toString());
              }
            }
        });

    }


}