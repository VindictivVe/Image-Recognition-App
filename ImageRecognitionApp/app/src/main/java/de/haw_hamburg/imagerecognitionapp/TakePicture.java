package de.haw_hamburg.imagerecognitionapp;

import android.content.Intent;
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
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

public class TakePicture extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1;
    ImageView imageView;
    Bitmap photo;
    Button takePicture;
    ImageButton back;
    TextView catOrDogTextView1;
    TextView catOrDogTextView2;
    TextView catOrDogTextView3;
    TextView formatInfo;
    Interpreter tflite;
    ArrayList<Bitmap> photoHistory = new ArrayList();
    ArrayList<String> resultHistory = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_picture_view);
        this.imageView = (ImageView) this.findViewById(R.id.camera_view);
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.button_click);
        catOrDogTextView1 = findViewById(R.id.cat_or_dog_text_view1);
        catOrDogTextView1.setVisibility(View.INVISIBLE);
        catOrDogTextView2 = findViewById(R.id.cat_or_dog_text_view2);
        catOrDogTextView2.setVisibility(View.INVISIBLE);
        catOrDogTextView3 = findViewById(R.id.cat_or_dog_text_view3);
        catOrDogTextView3.setVisibility(View.INVISIBLE);
        formatInfo = findViewById(R.id.formatInfo);
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

        Intent intent = getIntent();
        if (intent.hasExtra("bitmapList")){
            photoHistory = intent.getParcelableArrayListExtra("bitmapList");
            Log.i("error", "Size TP: "+photoHistory.size());
        }
        if (intent.hasExtra("resultList")){
            resultHistory = intent.getStringArrayListExtra("resultList");
        }

        back = findViewById(R.id.take_a_picture_back);
        back.setSoundEffectsEnabled(false);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                //TODO if BitMap not Null save for photoHistory
                Intent intent = new Intent(TakePicture.this, MainMenu.class);
//                intent.putExtra("photo", photo);
                if(photo != null){
                    photoHistory.add(0,photo);
                }
                if (photoHistory.size() >3 ){
                    photoHistory.remove(3);
                }
                if (resultHistory.size() > 3){
                    resultHistory.remove(3);
                }
                intent.putExtra("bitmapList", photoHistory);
                intent.putExtra("resultList", resultHistory);
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
            formatInfo.setVisibility(View.INVISIBLE);
            catOrDogTextView1.setVisibility(View.VISIBLE);
            catOrDogTextView2.setVisibility(View.VISIBLE);
            catOrDogTextView3.setVisibility(View.VISIBLE);

            catOrDogTextView1.setText("Katze: " + (roundNumber(result[0]*100)) + "%");
            catOrDogTextView2.setText("Hund: " + (roundNumber(result[1]*100)) + "%");
            catOrDogTextView3.setText("Spinne: " + (roundNumber(result[2]*100)) + "%");
            Log.i("rN", "number1: " + roundNumber(0.221234f*100));
            Log.i("rN", "number2: " + roundNumber(0.883456f*100));
            Log.i("rN", "number3: " + roundNumber(0.665678f*100));
            float max = 0;
            int index = 0;
            for (int i = 0; i < result.length; i++) {
                if (max < roundNumber(result[i]*100)) {
                    max = roundNumber(result[i]*100);
                    index = i;
                }
            }
            switch (index){
                case 0:
                    resultHistory.add(0,"Katze: " + max+ "%");
                    break;
                case 1:
                    resultHistory.add(0,"Hund: " + max + "%");
                    break;
                case 2:
                    resultHistory.add(0,"Spinne: " + max + "%");
                    break;
            }
        }
    }

    public float[] doPrediction(Bitmap bitmap) {
        //Initialize ImageProcessor to crop and resize the image: resize method https://www.tensorflow.org/api_docs/python/tf/image/resize?hl=en
        ImageProcessor imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeOp(128, 128, ResizeOp.ResizeMethod.BILINEAR))
                .build();

        //Create wrapper class for image, datatype float32
        TensorImage tensorImage = new TensorImage(DataType.FLOAT32);

        //Takes in bitmap of taken photo and resizes it
        tensorImage.load(bitmap);
        tensorImage = imageProcessor.process(tensorImage);

        //TensorBuffer for output data
        //fixedSize of [1,1] because output of net is a 2D array
        TensorBuffer probabilityBuffer = TensorBuffer.createFixedSize(new int[]{1, 3}, DataType.FLOAT32);

        //Load model and pass it into interpreter
        try {
            MappedByteBuffer tfliteModel = FileUtil.loadMappedFile(TakePicture.this, "model128_final.tflite");
            tflite = new Interpreter(tfliteModel, new Interpreter.Options());
        } catch (IOException e){
            Log.e("result", "Error reading model", e);
        }

        //Run model
        if(null != tflite){
            tflite.run(tensorImage.getBuffer(), probabilityBuffer.getBuffer());
        }

        Log.i("result", Arrays.toString(probabilityBuffer.getFloatArray()));

        //Close interpreter to avoid memory leak
        tflite.close();

        return probabilityBuffer.getFloatArray();
    }

    public float roundNumber(float number){
        number = number * 100;
        number = (int) number;
        number = (float) number / 100;
        return number;
    }
}

