package com.example.pruebas;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
    private static final int PERMISSION_REQUEST_CODE = 200;



    private boolean checkPermissionRead() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }
    private boolean checkPermissionWrite() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(newGroup.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }

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
                  if (checkPermissionRead() && checkPermissionWrite()) {
                      ExistGroup(name.getText().toString());
                  } else {
                      requestPermission();
                  }
              }
            }
        });

    }


}