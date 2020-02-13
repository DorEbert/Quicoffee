package com.example.quicoffee;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class MyCameraActivity extends AppCompatActivity {
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_camera);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //get the image into bitMap
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
        //intent in order to pass bitmap to previous activity
        Intent returnIntent = new Intent();
        //add selected picture to intent
        ByteArrayOutputStream _bs = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, _bs);
        returnIntent.putExtra(Global_Variable.RESULT_IMAGE, _bs.toByteArray());
        //finish help us to return to the previous activity
        finish();
    }
}
