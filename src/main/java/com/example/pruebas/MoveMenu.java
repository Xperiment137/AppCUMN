package com.example.pruebas;

        import android.content.Intent;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;

        import androidx.appcompat.app.AppCompatActivity;

public class MoveMenu  extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Button button = (Button) findViewById(R.id.bot3);
        Button button2 = (Button) findViewById(R.id.bot4);
        Button button3 = (Button) findViewById(R.id.bot5);

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startActivity(new Intent(MoveMenu.this, PhotoUpload.class));
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startActivity(new Intent(MoveMenu.this, listStat.class));
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startActivity(new Intent(MoveMenu.this, MoveGroup.class));
            }
        });
    }
}
