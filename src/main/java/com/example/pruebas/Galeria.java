package com.example.pruebas;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Galeria  extends AppCompatActivity {

    ActivityResultLauncher activityResultLauncher;
    ImageView imageView,takePhoto;
    Toolbar toolbar;

    public void onBackPressed() {

        startActivity(new Intent(Galeria.this, MoveMenu.class));
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara);
        imageView = findViewById(R.id.vista);
        takePhoto = findViewById(R.id.imageView2);
        toolbar = findViewById(R.id.toolbar);
        imageView.setVisibility(View.GONE);
        takePhoto.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);

        setSupportActionBar(toolbar);

        Intent intent = new Intent(Intent.ACTION_PICK);
        ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        //here we will handle the result of our intent
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            //image picked
                            //get uri of image
                            Intent data = result.getData();
                            Uri imageUri = data.getData();
                            imageView.setVisibility(View.VISIBLE);
                            takePhoto.setVisibility(View.VISIBLE);
                            toolbar.setVisibility(View.VISIBLE);
                            toolbar.setTitle("Galeria");
                            imageView.setImageURI(imageUri);

                        } else {
                            //cancelled
                            Toast.makeText(Galeria.this, "Cancelled...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Galeria.this, "It just works!",
                        Toast.LENGTH_SHORT).show();
            }
        });



    }
}

