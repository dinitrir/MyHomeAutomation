package com.example.user.myhomeautomation;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class CCTV extends AppCompatActivity {

    MQTTConnectionToActivity connection;
    String[] topicSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cctv);

        //MQTT connection
        connection= new MQTTConnectionToActivity(this,topicSub);
    }

    public void quit(View view){
        finish();
    }

    public void capture(View v){
        Date name = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", name);

        try {
            // path plus name plus extension type of image
            String ImagePath = this.getFilesDir().toString() + "/" + name + ".jpg";


            // create bitmap screen capture
            View view = getWindow().getDecorView().getRootView();
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

            // create image file
            File imageFile = new File(ImagePath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();



        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

}

//https://stackoverflow.com/questions/2661536/how-to-programmatically-take-a-screenshot-in-android