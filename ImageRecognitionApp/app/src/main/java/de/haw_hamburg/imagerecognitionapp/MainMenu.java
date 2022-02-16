package de.haw_hamburg.imagerecognitionapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainMenu extends AppCompatActivity {

    ArrayList<Bitmap> photoHistory = new ArrayList();
    ArrayList<String> resultHistory = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_view);

        Button takePicture = findViewById(R.id.take_picture_button);
        takePicture.setSoundEffectsEnabled(false);
        Button history = findViewById(R.id.history_button);
        history.setSoundEffectsEnabled(false);
        Button exit = findViewById(R.id.exit_button);
        exit.setSoundEffectsEnabled(false);

        Intent intent = getIntent();
        if (intent.hasExtra("bitmapList")){
            photoHistory = intent.getParcelableArrayListExtra("bitmapList");
        }
        if (intent.hasExtra("resultList")){
            resultHistory = intent.getStringArrayListExtra("resultList");
        }

        final MediaPlayer mp = MediaPlayer.create(this, R.raw.button_click);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();;
                Intent intent = new Intent(MainMenu.this, TakePicture.class);
                intent.putExtra("bitmapList", photoHistory);
                intent.putExtra("resultList", resultHistory);
                startActivity(intent);
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                Intent intent = new Intent(MainMenu.this, History.class);
                intent.putExtra("bitmapList", photoHistory);
                intent.putExtra("resultList", resultHistory);

                startActivity(intent);
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                finishAffinity();
            }
        });
    }
}
