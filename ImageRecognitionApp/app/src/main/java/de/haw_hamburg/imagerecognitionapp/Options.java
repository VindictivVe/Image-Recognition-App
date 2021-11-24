package de.haw_hamburg.imagerecognitionapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Options extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_view);

        ImageButton back = findViewById(R.id.options_back_to_menu);
        back.setSoundEffectsEnabled(false);

        final MediaPlayer mp = MediaPlayer.create(this, R.raw.button_click);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                Intent intent = new Intent(Options.this, MainMenu.class);
                startActivity(intent);
            }
        });
    }
}
