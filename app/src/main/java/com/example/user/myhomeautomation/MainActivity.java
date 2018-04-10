package com.example.user.myhomeautomation;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    ActionBarDrawerToggle toggle;
    public static MQTTConnectionToActivity c;
    public MqttAndroidClient client=null;
    public TextView temperature_text,humidity_text,motion_text,motion_icon,smoke_text,smoke_icon,gas_text,gas_icon,door_text,door_icon;
    public Switch ButtonGateSwitch,AwayModeSwitch;
    public static String motionStatus="idle",m_Text="24";
    final String[] topicSub={"homeautomationtemperaturehumiditysensor/dhtsensor","homeautomationmotionsensor/pirsensor","homeautomationmotor/gatestatus","homeautomationsmokesensor/mq2sensor","homeautomationcosensor/mq7sensor"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HamburgerMenu HamMenu= new HamburgerMenu(MainActivity.this);
        toggle= HamMenu.getToggle();

        temperature_text= new TextView(this.getApplicationContext());
        humidity_text= new TextView(this.getApplicationContext());
        motion_text= new TextView(this.getApplicationContext());
        motion_icon= new TextView(this.getApplicationContext());
        smoke_text= new TextView(this.getApplicationContext());
        smoke_icon= new TextView(this.getApplicationContext());
        gas_text= new TextView(this.getApplicationContext());
        gas_icon= new TextView(this.getApplicationContext());
        door_text= new TextView(this.getApplicationContext());
        door_icon= new TextView(this.getApplicationContext());

        temperature_text= (TextView) findViewById(R.id.txt_temperature);
        humidity_text=(TextView) findViewById(R.id.txt_humidity);
        motion_text=(TextView) findViewById(R.id.txt_Motion);
        motion_icon=(TextView) findViewById(R.id.txt_Motion_icon);
        smoke_text=(TextView) findViewById(R.id.txt_Smoke);
        smoke_icon=(TextView) findViewById(R.id.txt_Smoke_icon);
        gas_text=(TextView) findViewById(R.id.txt_Gas);
        gas_icon=(TextView) findViewById(R.id.txt_Gas_icon);
        door_text=(TextView) findViewById(R.id.txt_door_access);
        door_icon=(TextView) findViewById(R.id.txt_DoorAcces_icon);



        AwayModeSwitch= (Switch)findViewById(R.id.away_mode_switch);

        final MQTTConnectionToActivity connection = new MQTTConnectionToActivity(MainActivity.this,topicSub);
        c=connection;//for temp
        client= connection.getClient();

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                HandleMessage(topic,message);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

                }
        });
        //Handling away mode / home mode
        AwayModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TextView home = (TextView)findViewById(R.id.txt_home_mode);
                TextView away = (TextView)findViewById(R.id.txt_away_mode);
                if(isChecked){
                    home.setTextColor(getResources().getColor(android.R.color.background_light));
                    away.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
                    connection.PublishToTopic("HomeMode","AWAY");
                }else{
                    away.setTextColor(getResources().getColor(android.R.color.background_light));
                    home.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
                    connection.PublishToTopic("HomeMode","HOME");
                }
            }
        });

        temperature_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTemperature();
            }
        });



    }


    @Override
    public void onBackPressed(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
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

    @SuppressLint("SetTextI18n")
    public void HandleMessage(String topic, MqttMessage message){
        String msg= new String(message.getPayload());
        switch (topic){
            case "homeautomationtemperaturehumiditysensor/dhtsensor":
                String[] msgs= new String(message.getPayload()).split(" ");
                temperature_text.setText(msgs[1]+" \u2103");
                humidity_text.setText(msgs[0]+" %");
                break;
            case "homeautomationmotionsensor/pirsensor":
                if(msg.equals("motion")){
                    motion_text.setText("Motion Detected");
                    motion_icon.setTextColor(Color.parseColor("#db4343"));
                }
                else{
                    motion_text.setText("No Motion");
                    motion_icon.setTextColor(Color.parseColor("#94c64d"));
                }
                break;
            case "homeautomationmotor/gatestatus":
                if(msg.equals("open")){
                    door_text.setText("Door Open");
                    door_icon.setTextColor(Color.parseColor("#db4343"));
                }
                else{
                    door_text.setText("Door Closed");
                    door_icon.setTextColor(Color.parseColor("#94c64d"));
                }
                break;

            case "homeautomationsmokesensor/mq2sensor":
                if(msg.equals("smoke")){
                    smoke_text.setText("Smoke Detected");
                    smoke_icon.setTextColor(Color.parseColor("#db4343"));
                    NotifyUser("Smoke Dectected");
                }
                else{
                    smoke_text.setText("No Smoke");
                    smoke_icon.setTextColor(Color.parseColor("#94c64d"));

                }
                break;
            case "homeautomationcosensor/mq7sensor":
                if(msg.equals("gas")){
                    gas_text.setText("CO Gas Detected");
                    gas_icon.setTextColor(Color.parseColor("#db4343"));
                    NotifyUser("Carbon Monoxide Detected");
                }
                else{
                    gas_text.setText("No CO Gas");
                    gas_icon.setTextColor(Color.parseColor("#94c64d"));
                }
                break;

        }
    }

    public void setTemperature(){
        final EditText txtTemp = new EditText(this);
        txtTemp.setHint("Temperature in degree celcius");

        new AlertDialog.Builder(this)
                .setTitle("Temperature Limit")
                .setMessage("Set temperature to activate fan")
                .setView(txtTemp)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        m_Text = txtTemp.getText().toString();
                        c.PublishToTopic("homeautomationthresholdtemperature",m_Text);

                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    public void NotifyUser(String subject){
        NotificationCompat.Builder NotifBuilder = new NotificationCompat.Builder(getApplicationContext());
        NotifBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(subject)
                .setContentText("Warning")
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentInfo("Info");

        NotificationManager notificationManager = (NotificationManager)this.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,NotifBuilder.build());
    }
}
