package de.haw_hamburg.imagerecognitionapp;

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

        Button spielen = findViewById(R.id.spielen_button);
        spielen.setSoundEffectsEnabled(false);
        Button highscores = findViewById(R.id.highscores_button);
        highscores.setSoundEffectsEnabled(false);
        Button optionen = findViewById(R.id.optionen_button);
        optionen.setSoundEffectsEnabled(false);
        Button beenden = findViewById(R.id.beenden_button);
        beenden.setSoundEffectsEnabled(false);

        final MediaPlayer mp = MediaPlayer.create(this, R.raw.button_click);
        spielen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();;
            }
        });

        highscores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
            }
        });

        optionen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
            }
        });

        beenden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                finishAffinity();
            }
        });
    }
}
