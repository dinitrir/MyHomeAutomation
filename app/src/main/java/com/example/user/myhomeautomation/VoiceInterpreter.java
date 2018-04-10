package com.example.user.myhomeautomation;

import android.content.ActivityNotFoundException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VoiceInterpreter extends AppCompatActivity{

    private  static  final int VOICE_RECOGNITION_REQUEST_CODE = 1001,REQ_CODE_SPEECH_INPUT = 100;
    private ImageButton mbtSpeak;
    private TextView mVoiceInputTv;
    ActionBarDrawerToggle toggle;
    String[] topicSub=null;
    MQTTConnectionToActivity con;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_interpreter);

        HamburgerMenu HamMenu= new HamburgerMenu(VoiceInterpreter.this);
        toggle= HamMenu.getToggle();

        mVoiceInputTv=(TextView)findViewById(R.id.voice_text);
        mbtSpeak = (ImageButton) findViewById(R.id.btSpeak);

        con = new MQTTConnectionToActivity(VoiceInterpreter.this,topicSub);
    }

    public  void  CheckVoiceRecognition(){
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH),0);
        if (activities.size()==0){
            mbtSpeak.setEnabled(false);
            Toast.makeText(this,"Voice recognizer not present",Toast.LENGTH_LONG).show();
        }
    }

    public void speak(View view){
        CheckVoiceRecognition();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, Tell me what to do ?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if (resultCode == RESULT_OK && null != data){
            {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                mVoiceInputTv.setText(result.get(0));
                interpreteVoiceMessage(mVoiceInputTv.getText().toString());
            }
        }
        else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
            showToastMessage("Audio Error");

        }
        else if ((resultCode == RecognizerIntent.RESULT_CLIENT_ERROR)){
            showToastMessage("Client Error");

        }
        else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
            showToastMessage("Network Error");
        }
        else if (resultCode == RecognizerIntent.RESULT_NO_MATCH){
            showToastMessage("No Match");
        }
        else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
            showToastMessage("Server Error");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void  showToastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    public void interpreteVoiceMessage(String message){
        String returnMessage="";
        switch (message){
            case "light living room on":
                con.PublishToTopic("homeautomationledlight/livingroom","on");
                break;
            case "light living room off":
                con.PublishToTopic("homeautomationledlight/livingroom","off");
                break;
            case "light kitchen on":
                con.PublishToTopic("homeautomationledlight/kitchen","on");
                break;
            case "light kitchen off":
                con.PublishToTopic("homeautomationledlight/kitchen","off");
                break;
            case "light outside on":
                con.PublishToTopic("homeautomationledlight/outside","on");
                break;
            case "light outside off":
                con.PublishToTopic("homeautomationledlight/outside","off");
                break;
            case "all lights on":
                con.PublishToTopic("homeautomationledlight/livingroom","on");
                con.PublishToTopic("homeautomationledlight/kitchen","on");
                con.PublishToTopic("homeautomationledlight/outside","on");
                break;
            case "all lights off":
                con.PublishToTopic("homeautomationledlight/livingroom","off");
                con.PublishToTopic("homeautomationledlight/kitchen","off");
                con.PublishToTopic("homeautomationledlight/outside","off");
                break;
            case "open gate":
                con.PublishToTopic("homeautomationmotor/gate","open");
                break;
            case "close gate":
                con.PublishToTopic("homeautomationmotor/gate","close");
                break;
            case "open shutter":
                con.PublishToTopic("homeautomationmotor/shutter","open");
                break;
            case "close shutter":
                con.PublishToTopic("homeautomationmotor/shutter","close");
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.action_settings){
            return true;
        }
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
