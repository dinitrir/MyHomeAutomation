package com.example.user.myhomeautomation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
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
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.internal.ClientComms;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActionBarDrawerToggle toggle;
    public MqttAndroidClient client=null;
    public TextView temperature_text,humidity_text,motion_text,motion_icon,smoke_text,smoke_icon,gas_text,gas_icon;
    public Switch ButtonGateSwitch;
    public static String motionStatus="idle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer =(DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this,drawer,R.string.navigation_drawer_open,R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.NavView);
        navigationView.setNavigationItemSelectedListener(this);

        temperature_text= new TextView(this.getApplicationContext());
        humidity_text= new TextView(this.getApplicationContext());
        motion_text= new TextView(this.getApplicationContext());
        motion_icon= new TextView(this.getApplicationContext());
        smoke_text= new TextView(this.getApplicationContext());
        smoke_icon= new TextView(this.getApplicationContext());
        gas_text= new TextView(this.getApplicationContext());
        gas_icon= new TextView(this.getApplicationContext());

        temperature_text= (TextView) findViewById(R.id.txt_temperature);
        humidity_text=(TextView) findViewById(R.id.txt_humidity);
        motion_text=(TextView) findViewById(R.id.txt_Motion);
        motion_icon=(TextView) findViewById(R.id.txt_Motion_icon);
        smoke_text=(TextView) findViewById(R.id.txt_Smoke);
        smoke_icon=(TextView) findViewById(R.id.txt_Smoke_icon);
        gas_text=(TextView) findViewById(R.id.txt_Gas);
        gas_icon=(TextView) findViewById(R.id.txt_Gas_icon);

        /////// For the MQTT connection
        String clientId = MqttClient.generateClientId();
        client =
                new MqttAndroidClient(this.getApplicationContext(), Broker.broker,
                        clientId);
        try {

                IMqttToken token = client.connect();
                token.setActionCallback(new IMqttActionListener(){
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken){
                        // connected
                        Toast.makeText(MainActivity.this, "Connected ", Toast.LENGTH_SHORT).show();
                        String[] topic={"homeautomationtemperaturehumiditysensor/dhtsensor","homeautomationmotionsensor/pirsensor","homeautomationmotor/gatestatus","homeautomationsmokesensor/mq2sensor","homeautomationcosensor/mq7sensor"};
                        for(int i=0;i<topic.length;i++){
                            SubscribeToTopic(topic[i]);//subscribe to topic
                        }

                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // connection failed
                    Toast.makeText(MainActivity.this, "Failed ! ", Toast.LENGTH_SHORT).show();
                }
                });

            }
            catch (MqttException e) {
                 e.printStackTrace();
            }

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

    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.itemDashboard){
            Intent searchIntent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(searchIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }else if(id == R.id.itemLight){
            Intent searchIntent = new Intent(MainActivity.this, Light.class);
            startActivity(searchIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }else if(id == R.id.itemDoor){
            Intent searchIntent = new Intent(MainActivity.this, Door.class);
            startActivity(searchIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }else if(id == R.id.itemCamera) {
            Intent searchIntent = new Intent(MainActivity.this, Camera.class);
            startActivity(searchIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }else if(id == R.id.itemMode) {
            Intent searchIntent = new Intent(MainActivity.this, Mode.class);
            startActivity(searchIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void SubscribeToTopic(String topic){
        try{
            client.subscribe(topic,0);
        }
        catch (MqttSecurityException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }

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
                    ButtonGateSwitch.setChecked(Boolean.TRUE);
                }
                else{
                    ButtonGateSwitch.setChecked(Boolean.FALSE);
                }
                break;

            case "homeautomationsmokesensor/mq2sensor":
                if(msg.equals("smoke")){
                    smoke_text.setText("Smoke Detected");
                    smoke_icon.setTextColor(Color.parseColor("#db4343"));
                }
                else{
                    smoke_text.setText("No Smoke");
                    smoke_icon.setTextColor(Color.parseColor("#94c64d"));
                }
                break;
            case "homeautomationcosensor/mq7sensor":
                if(msg.equals("gas")){
                    gas_text.setText("Smoke Detected");
                    gas_icon.setTextColor(Color.parseColor("#db4343"));
                }
                else{
                    gas_text.setText("No Smoke");
                    gas_icon.setTextColor(Color.parseColor("#94c64d"));
                }
                break;

        }
    }


}
