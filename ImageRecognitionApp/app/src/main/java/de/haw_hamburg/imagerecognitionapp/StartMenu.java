package de.haw_hamburg.imagerecognitionapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StartMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_view);

        final MediaPlayer mp = MediaPlayer.create(this, R.raw.button_click);
        Button button = findViewById(R.id.start_button);
        button.setSoundEffectsEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                Intent intent = new Intent(StartMenu.this, MainMenu.class);
                startActivity(intent);
            }
        });
    }
}
