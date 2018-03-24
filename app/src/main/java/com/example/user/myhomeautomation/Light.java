package com.example.user.myhomeautomation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

public class Light extends AppCompatActivity {

    //widgets on light content page
    ActionBarDrawerToggle toggle;
    public Switch LivingRoomSwitch,KitchenSwitch,OutsideSwitch;
    final String[] topicSub={"homeautomationstatusesBack"};

    //mqtt components
    public MqttAndroidClient client=null;
    MQTTConnectionToActivity connection;

    /// Oncreate method including MQTT connection
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        HamburgerMenu HamMenu= new HamburgerMenu(Light.this);
        toggle= HamMenu.getToggle();

        LivingRoomSwitch= (Switch) findViewById(R.id.LivingRoomLightSwitch);
        KitchenSwitch= (Switch) findViewById(R.id.KitchenLightSwitch);
        OutsideSwitch= (Switch) findViewById(R.id.OutsideLightSwitch);

        //MQTT Connection
        connection= new MQTTConnectionToActivity(Light.this,topicSub);
        connection.PublishToTopic("homeautomationstatuses","opensactivity");
        client=connection.getClient();

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

        //Living Room Light switch
        LivingRoomSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String topic = "homeautomationledlight/livingroom";
                if(isChecked){
                    connection.PublishToTopic(topic,"on");
                }
                else{
                    connection.PublishToTopic(topic,"off");
                }
            }
        });

        //kitchen light Switch
        KitchenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String topic = "homeautomationledlight/kitchen";
                if(isChecked){
                    connection.PublishToTopic(topic,"on");
                }
                else{
                    connection.PublishToTopic(topic,"off");
                }
            }



        });

        //Outside light Switch
        OutsideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String topic = "homeautomationledlight/outside";
                if(isChecked){
                    connection.PublishToTopic(topic,"on");
                }
                else{
                    connection.PublishToTopic(topic,"off");
                }
            }



        });

        //RGB SET button
        Button rgbledsettter= (Button)findViewById(R.id.RGBLightButton);
        rgbledsettter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorSelector.sourceClass="Light";
                Intent searchIntent = new Intent(Light.this, ColorSelector.class);
                startActivity(searchIntent);
            }
        });

        Button turnOffbtn= (Button)findViewById(R.id.RGBLightButtonOFF);
        turnOffbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topic = "homeautomationledlight/rgb";
                connection.PublishToTopic(topic,"0,0,0");
            }
        });

    }

    /////////For Navigation Purpose\\\\\\\\\\\\\\\\\
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

    public void HandleMessage(String topic, MqttMessage message){
        String msg= new String(message.getPayload());
        String[] buttonStatus=msg.split("|");
        LivingRoomSwitch.setChecked(buttonStatus[0].equals("on")?true:false);
        KitchenSwitch.setChecked(buttonStatus[1].equals("on")?true:false);
        OutsideSwitch.setChecked(buttonStatus[2].equals("on")?true:false);
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
