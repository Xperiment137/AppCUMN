package com.example.pruebas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore Db;
    private boolean choose = false;
    MutableLiveData<String> listen;

    private int countChar(String str)
    {
      return str.toCharArray().length;
    }

    private void SaveEmail(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }


    private void CheckUserName(String nom){
            DocumentReference docRef = Db.collection("Usuarios").document(nom);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            Log.e("tag",document.getData().toString());
                            Toast.makeText(MainActivity.this, "Ya existe un usuario con ese nombre, prueba otro", Toast.LENGTH_LONG).show();

                        } else {
                            Log.e("tag","Works!");
                            listen.setValue("nuevo");
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Algo fallo, vuelva a intentarlo", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }

    private void addDataToFirestore(String user, String email) {

        Map<String, Object> User = new HashMap<>();
        User.put("Nombre", user);
        User.put("Email", email);

        // creating a collection reference
        // for our Firebase Firestore database.
        CollectionReference usuarios =   Db.collection("Usuarios");


        // below method is use to add data to Firebase Firestore.
        usuarios.document(user).set(User).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void Avoid) {
                // after the data addition is successful
                // we are displaying a success toast message.
                Toast.makeText(MainActivity.this, "User has been added to DB ", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // this method is called when the data addition process is failed.
                // displaying a toast message when data addition is failed.
                Toast.makeText(MainActivity.this, "Fail to add User \n" + e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void NewUser(String email,String user,String password) {
            SaveEmail("email", email);
            if ((!email.equals("") && !password.equals(""))) {
                if (countChar(password) > 6) {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            Toast.makeText(MainActivity.this, "Añadiendote al sistema....", Toast.LENGTH_SHORT).show();
                            if (task.isSuccessful()) {
                                addDataToFirestore(user, email);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        FirebaseUser user = mAuth.getCurrentUser(); //You Firebase user
                                        // user registered, start profile activity
                                        Toast.makeText(MainActivity.this, "Usuario Creado", Toast.LENGTH_LONG).show();
                                        finish();
                                        startActivity(new Intent(MainActivity.this, MoveMenu.class));
                                    }
                                }, 2000);
                            } else {
                                Toast.makeText(MainActivity.this, "No se pudo crear el usuario", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "La contraseña debe tener 7 caracateres o mas", Toast.LENGTH_LONG).show();
                }
            } else {

                Toast.makeText(MainActivity.this, "Uno de los campos esta vacio", Toast.LENGTH_LONG).show();

            }
        }


    private void LogUser(String email,String password) {
        SaveEmail("email",email);
        if((!email.equals("") && !password.equals(""))) {
            if(countChar(password) > 6){
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Exito al autenticar", Toast.LENGTH_LONG).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        startActivity(new Intent(MainActivity.this, MoveMenu.class));
                    } else {
                        Toast.makeText(MainActivity.this, "Fallo al autenticar", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }else{
            Toast.makeText(MainActivity.this, "La contraseña debe tener 7 caracateres o mas", Toast.LENGTH_LONG).show();
        }
        }else{

            Toast.makeText(MainActivity.this, "Uno de los campos esta vacio", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        Db = FirebaseFirestore.getInstance();
        Button button = (Button) findViewById(R.id.login);
        Button button2 = (Button) findViewById(R.id.signin);
        EditText em = findViewById(R.id.email);
        EditText ps = findViewById(R.id.password);
        EditText user = findViewById(R.id.usuario);
        listen = new MutableLiveData<>();
        listen.setValue("nada");
        em.setVisibility(View.GONE);
        ps.setVisibility(View.GONE);
        user.setVisibility(View.GONE);

       /* String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Grupos/";
        File dir = new File(rootPath);
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(dir, children[i]).delete();
            }
        }*/

        listen.observe(MainActivity.this,new Observer<String>() {
            @Override
            public void onChanged(String changedValue) {
                if (changedValue.equals("nuevo")) {

                    NewUser(em.getText().toString(), user.getText().toString(),ps.getText().toString());

                }
            }
        });

            button.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {


                    if(choose) {
                        LogUser(em.getText().toString(), ps.getText().toString());

                    }else{

                        em.setVisibility(View.VISIBLE);
                        ps.setVisibility(View.VISIBLE);
                        button2.setVisibility(View.GONE);

                        choose = true;

                    }
                }
            });

            button2.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    if(choose) {

                        CheckUserName(user.getText().toString());

                    }else{

                        em.setVisibility(View.VISIBLE);
                        ps.setVisibility(View.VISIBLE);
                        user.setVisibility(View.VISIBLE);
                        button.setVisibility(View.GONE);
                        choose = true;

                    }


                }
            });

       // startActivity(new Intent(MainActivity.this, MoveMenu.class));


    }
}