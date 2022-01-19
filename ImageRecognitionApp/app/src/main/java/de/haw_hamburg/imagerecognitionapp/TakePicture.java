package de.haw_hamburg.imagerecognitionapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.InterpreterApi;
import org.tensorflow.lite.InterpreterFactory;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;

import org.jetbrains.annotations.NotNull;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.TensorFlowLite;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

public class TakePicture extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1;
    ImageView imageView;
    Bitmap photo;
    Button takePicture;
    ImageButton back;
    TextView catOrDogTextView;
    Interpreter tflite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_picture_view);
        Log.i("test123", "TestOnCreate");
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

        /*// Create Interpreter
        //****************************************************
        try {
            tflite = new Interpreter(loadModelFile());
        } catch (Exception e){
            e.printStackTrace();
        }
        //*****************************************************/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

            // Convert photo to Bitmap
            //****************************************************
            photo = (Bitmap) data.getExtras().get("data");

            // Log result
            //****************************************************
            float[] result = doPrediction(photo);
            //****************************************************

            imageView.setImageBitmap(photo);
            takePicture.setVisibility(View.INVISIBLE);
            catOrDogTextView.setVisibility(View.VISIBLE);

            if(result[0] > 0.9f){
                catOrDogTextView.setText("Dog");
            } else if(result[0] < 0.1f) {
                catOrDogTextView.setText("Cat");
            } else {
                catOrDogTextView.setText("Can't tell");
            }
        }
    }

    public float[] doPrediction(Bitmap bitmap) {
        //Initialize ImageProcessor to crop and resize the image: resize method https://www.tensorflow.org/api_docs/python/tf/image/resize?hl=en
        ImageProcessor imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeOp(64, 64, ResizeOp.ResizeMethod.BILINEAR))
                .build();

        //Create wrapper class for image
        TensorImage tensorImage = new TensorImage(DataType.FLOAT32);

        //Takes in bitmap of taken photo and resizes it
        tensorImage.load(bitmap);
        tensorImage = imageProcessor.process(tensorImage);

        //TensorBuffer for output data
        TensorBuffer probabilityBuffer = TensorBuffer.createFixedSize(new int[]{1, 1}, DataType.FLOAT32);

        //Load model and pass it into interpreter
        try {
            MappedByteBuffer tfliteModel = FileUtil.loadMappedFile(TakePicture.this, "model.tflite");
            tflite = new Interpreter(tfliteModel, new Interpreter.Options());
        } catch (IOException e){
            Log.e("tfliteSupport", "Error reading model", e);
        }

        //Run model
        if(null != tflite){
            tflite.run(tensorImage.getBuffer(), probabilityBuffer.getBuffer());
        }

        Log.i("result", "Success: " + tensorImage.getBuffer() + " " + probabilityBuffer.getFloatArray());

        tflite.close();

        return probabilityBuffer.getFloatArray();
    }
}
