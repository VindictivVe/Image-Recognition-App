package de.haw_hamburg.imagerecognitionapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class History extends AppCompatActivity {

    ArrayList<Bitmap> photoHistory = new ArrayList();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_view);

        ImageView imgage1 = findViewById(R.id.history_ImageView1);
        ImageView imgage2 = findViewById(R.id.history_ImageView2);
        ImageView imgage3 = findViewById(R.id.history_ImageView3);

        ImageButton back = findViewById(R.id.history_back_to_menu);
        back.setSoundEffectsEnabled(false);

        Intent intent = getIntent();
        if (intent.hasExtra("bitmapList")){
            photoHistory = intent.getParcelableArrayListExtra("bitmapList");
        }

        Log.i("error", "Size: "+photoHistory.size());
        if (photoHistory.size()>0){
            imgage1.setImageBitmap(photoHistory.get(0));
        }
        if (photoHistory.size()>1){
            imgage2.setImageBitmap(photoHistory.get(1));
        }
        if (photoHistory.size()>2){
            imgage3.setImageBitmap(photoHistory.get(2));
        }

        final MediaPlayer mp = MediaPlayer.create(this, R.raw.button_click);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                Intent intent = new Intent(History.this, MainMenu.class);
                intent.putExtra("bitmapList", photoHistory);
                startActivity(intent);
            }
        });
    }
}
