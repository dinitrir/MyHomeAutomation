package com.example.user.myhomeautomation;

import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;

public class Recognition extends AppCompatActivity {
    ActionBarDrawerToggle toggle;
    MQTTConnectionToActivity connection;
    RadioGroup radioButton;
    String[] topicSub;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);

        //MQTT Connection
        connection= new MQTTConnectionToActivity(this,topicSub);

        //radio button publish
        radioButton=(RadioGroup)findViewById(R.id.radioGroupRecognition);
        radioButton.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioFace:
                        connection.PublishToTopic("homeautomationcamera/face","activityFace");
                        break;
                    case R.id.radioGesture:
                        connection.PublishToTopic("homeautomationcamera/gesture","activityGesture");
                        break;
                    case R.id.radioNone:
                        break;
                    default:
                        break;
                }
            }
        });

    }

    public void quit(View v){
        finish();
    }
}
