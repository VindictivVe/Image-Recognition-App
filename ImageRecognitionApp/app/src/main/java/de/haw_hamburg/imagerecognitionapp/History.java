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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class History extends AppCompatActivity {

    ArrayList<Bitmap> photoHistory = new ArrayList();
    ArrayList<String> resultHistory = new ArrayList();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_view);

        ImageView imgage1 = findViewById(R.id.history_ImageView1);
        ImageView imgage2 = findViewById(R.id.history_ImageView2);
        ImageView imgage3 = findViewById(R.id.history_ImageView3);

        TextView result1 = findViewById(R.id.history_TextView1);
        TextView result2 = findViewById(R.id.history_TextView2);
        TextView result3 = findViewById(R.id.history_TextView3);

        TextView noHistory = findViewById(R.id.noHistory_TextView);

        ImageButton back = findViewById(R.id.history_back_to_menu);
        back.setSoundEffectsEnabled(false);

        Intent intent = getIntent();
        if (intent.hasExtra("bitmapList")){
            photoHistory = intent.getParcelableArrayListExtra("bitmapList");
        }
        if (intent.hasExtra("resultList")){
            resultHistory = intent.getStringArrayListExtra("resultList");
        }

        Log.i("error", "Size: "+photoHistory.size());
        if (photoHistory.size()>0){
            imgage1.setImageBitmap(photoHistory.get(0));
            result1.setText(resultHistory.get(0));
            noHistory.setVisibility(View.INVISIBLE);
        } else {
            result1.setText("");
            noHistory.setText("Du hast noch keine Fotos geschossen!");
        }
        if (photoHistory.size()>1){
            imgage2.setImageBitmap(photoHistory.get(1));
            result2.setText(resultHistory.get(1));
        } else {
            result2.setText("");
        }
        if (photoHistory.size()>2){
            imgage3.setImageBitmap(photoHistory.get(2));
            result3.setText(resultHistory.get(2));
        } else {
            result3.setText("");
        }

        final MediaPlayer mp = MediaPlayer.create(this, R.raw.button_click);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                Intent intent = new Intent(History.this, MainMenu.class);
                intent.putExtra("bitmapList", photoHistory);
                intent.putExtra("resultList", resultHistory);

                startActivity(intent);
            }
        });
    }
}
