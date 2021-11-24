package de.haw_hamburg.imagerecognitionapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TakePicture extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1;
    ImageView imageView;
    Bitmap photo;
    Button takePicture;
    ImageButton back;
    TextView catOrDogTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_picture_view);

        this.imageView = (ImageView) this.findViewById(R.id.camera_view);
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.button_click);
        catOrDogTextView = findViewById(R.id.cat_or_dog_text_view);
        catOrDogTextView.setVisibility(View.INVISIBLE);
        takePicture = findViewById(R.id.camera_button);
        takePicture.setSoundEffectsEnabled(false);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        });
        back = findViewById(R.id.take_a_picture_back);
        back.setSoundEffectsEnabled(false);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                //TODO if BitMap not Null safe for photoHistory
                Intent intent = new Intent(TakePicture.this, MainMenu.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
            takePicture.setVisibility(View.INVISIBLE);
            catOrDogTextView.setVisibility(View.VISIBLE);

            //TODO net checks if Cat or Dog
            catOrDogTextView.setText("Dog");

        }
    }
}
