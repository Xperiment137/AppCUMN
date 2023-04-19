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

public class Camara  extends AppCompatActivity {

    ActivityResultLauncher activityResultLauncher;
    ImageView imageView,takePhoto;
    Toolbar toolbar;


    public void onBackPressed() {
        startActivity(new Intent(Camara.this, MoveMenu.class));
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

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageView.setVisibility(View.VISIBLE);
                    takePhoto.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.VISIBLE);
                    toolbar.setTitle("Camara");
                    Bundle bundle = result.getData().getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    imageView.setImageBitmap(bitmap);
                }
            }
        });
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            activityResultLauncher.launch(intent);
        } else {
            Toast.makeText(Camara.this, "There is no app that support this action",
                    Toast.LENGTH_SHORT).show();
        }

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Camara.this, "It just works!",
                        Toast.LENGTH_SHORT).show();
            }
        });


    }
}

