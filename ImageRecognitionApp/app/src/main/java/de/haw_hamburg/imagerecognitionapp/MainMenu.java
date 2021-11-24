package de.haw_hamburg.imagerecognitionapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_view);

        Button takePicture = findViewById(R.id.take_picture_button);
        takePicture.setSoundEffectsEnabled(false);
        Button history = findViewById(R.id.history_button);
        history.setSoundEffectsEnabled(false);
        Button options = findViewById(R.id.options_button);
        options.setSoundEffectsEnabled(false);
        Button exit = findViewById(R.id.exit_button);
        exit.setSoundEffectsEnabled(false);

        final MediaPlayer mp = MediaPlayer.create(this, R.raw.button_click);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();;
                Intent intent = new Intent(MainMenu.this, TakePicture.class);
                startActivity(intent);
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                Intent intent = new Intent(MainMenu.this, History.class);
                startActivity(intent);
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                Intent intent = new Intent(MainMenu.this, Options.class);
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
