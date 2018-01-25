package com.example.user.myhomeautomation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
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
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.sql.Timestamp;

public class Light extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //widgets on light content page
    ActionBarDrawerToggle toggle;
    public Switch LivingRoomSwitch,KitchenSwitch,OutsideSwitch;
    private SharedPreferences.Editor edit=null;
    //mqtt components
    public MqttAndroidClient client=null;

    /// Oncreate method including
    ///MQTT Connections
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        /////// For the Hamburger Menu \\\\\\\\
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

        LivingRoomSwitch= (Switch) findViewById(R.id.LivingRoomLightSwitch);
        KitchenSwitch= (Switch) findViewById(R.id.KitchenLightSwitch);
        OutsideSwitch= (Switch) findViewById(R.id.OutsideLightSwitch);

        SharedPreferences prefLight = PreferenceManager.getDefaultSharedPreferences(this);
        boolean ls = prefLight.getBoolean("LivingRoom",false );
        boolean ks = prefLight.getBoolean("Kitchen",false );
        boolean os = prefLight.getBoolean("Outside",false );
        LivingRoomSwitch.setChecked(ls);
        KitchenSwitch.setChecked(ks);
        OutsideSwitch.setChecked(os);

        edit = PreferenceManager.getDefaultSharedPreferences(Light.this).edit();

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
                    Toast.makeText(Light.this, "Connected ", Toast.LENGTH_SHORT).show();
                   // MonitorLightStatus();

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // connection failed
                    Toast.makeText(Light.this, "Failed ! ", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }



        //Living Room Light switch
        LivingRoomSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String topic = "homeautomationledlight/livingroom";
                int qos=0;//quality of service
                String content;

                if(isChecked){
                    content   = "on";

                    try {
                        MqttMessage message = new MqttMessage(content.getBytes());
                        message.setQos(qos);
                        client.publish(topic, message);
                        System.out.println("Message published");

                    } catch(MqttException me) {
                        System.out.println("reason "+me.getReasonCode());
                        System.out.println("msg "+me.getMessage());
                        System.out.println("loc "+me.getLocalizedMessage());
                        System.out.println("cause "+me.getCause());
                        System.out.println("excep "+me);
                        me.printStackTrace();
                    }
                }
                else{
                    content = "off";

                    try {
                        MqttMessage message = new MqttMessage(content.getBytes());
                        message.setQos(qos);
                        client.publish(topic, message);
                        System.out.println("Message published");
                        System.out.println("Disconnected");

                    } catch(MqttException me) {
                        System.out.println("reason "+me.getReasonCode());
                        System.out.println("msg "+me.getMessage());
                        System.out.println("loc "+me.getLocalizedMessage());
                        System.out.println("cause "+me.getCause());
                        System.out.println("excep "+me);
                        me.printStackTrace();
                    }

                }
                edit.putBoolean("LivingRoom",isChecked );
                edit.apply();

                }



        });
        //kitchen light Switch
        KitchenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String topic = "homeautomationledlight/kitchen";
                int qos=0;//quality of service
                String content;
                if(isChecked){

                    content   = "on";

                    MemoryPersistence persistence = new MemoryPersistence();

                    try {
                        MqttMessage message = new MqttMessage(content.getBytes());
                        message.setQos(qos);
                        client.publish(topic, message);
                        System.out.println("Message published");

                    } catch(MqttException me) {
                        System.out.println("reason "+me.getReasonCode());
                        System.out.println("msg "+me.getMessage());
                        System.out.println("loc "+me.getLocalizedMessage());
                        System.out.println("cause "+me.getCause());
                        System.out.println("excep "+me);
                        me.printStackTrace();
                    }
                }
                else{
                    content = "off";
                    try {
                        MqttMessage message = new MqttMessage(content.getBytes());
                        message.setQos(qos);
                        client.publish(topic, message);
                        System.out.println("Message published");
                        System.out.println("Disconnected");

                    } catch(MqttException me) {
                        System.out.println("reason "+me.getReasonCode());
                        System.out.println("msg "+me.getMessage());
                        System.out.println("loc "+me.getLocalizedMessage());
                        System.out.println("cause "+me.getCause());
                        System.out.println("excep "+me);
                        me.printStackTrace();
                    }

                }
                edit.putBoolean("Kitchen",isChecked );
                edit.apply();
            }



        });
        //Outside light Switch
        OutsideSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String topic = "homeautomationledlight/outside";
                int qos=0;//quality of service
                String content;

                if(isChecked){

                    content   = "on";

                    MemoryPersistence persistence = new MemoryPersistence();

                    try {
                        MqttMessage message = new MqttMessage(content.getBytes());
                        message.setQos(qos);
                        client.publish(topic, message);
                        System.out.println("Message published");

                    } catch(MqttException me) {
                        System.out.println("reason "+me.getReasonCode());
                        System.out.println("msg "+me.getMessage());
                        System.out.println("loc "+me.getLocalizedMessage());
                        System.out.println("cause "+me.getCause());
                        System.out.println("excep "+me);
                        me.printStackTrace();
                    }
                }
                else{
                    content = "off";

                    MemoryPersistence persistence = new MemoryPersistence();

                    try {
                        MqttMessage message = new MqttMessage(content.getBytes());
                        message.setQos(qos);
                        client.publish(topic, message);
                        System.out.println("Message published");
                        System.out.println("Disconnected");

                    } catch(MqttException me) {
                        System.out.println("reason "+me.getReasonCode());
                        System.out.println("msg "+me.getMessage());
                        System.out.println("loc "+me.getLocalizedMessage());
                        System.out.println("cause "+me.getCause());
                        System.out.println("excep "+me);
                        me.printStackTrace();
                    }

                }
                edit.putBoolean("Outside",isChecked );
                edit.apply();
            }



        });
        //RGB SET button
        Button rgbledsettter= (Button)findViewById(R.id.RGBLightButton);
        rgbledsettter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(Light.this, ColorSelector.class);
                startActivity(searchIntent);
            }
        });

        Button turnOffbtn= (Button)findViewById(R.id.RGBLightButtonOFF);
        turnOffbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    String topic = "homeautomationledlight/rgb";
                    int qos=0;//quality of service
                    String content="0,0,0";
                    MqttMessage message = new MqttMessage(content.getBytes());
                    message.setQos(qos);
                    client.publish(topic, message);
                    System.out.println("Message published");
                    System.out.println("Disconnected");
                }
                catch (MqttException me){
                    System.out.println("reason "+me.getReasonCode());
                    System.out.println("msg "+me.getMessage());
                    System.out.println("loc "+me.getLocalizedMessage());
                    System.out.println("cause "+me.getCause());
                    System.out.println("excep "+me);
                    me.printStackTrace();
                }
            }
        });


    }

    // ///////For Navigation Purpose\\\\\\\\\\\\\\\\\
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
            Intent searchIntent = new Intent(Light.this, MainActivity.class);
            startActivity(searchIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }else if(id == R.id.itemLight){
            Intent searchIntent = new Intent(Light.this, Light.class);
            startActivity(searchIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }else if(id == R.id.itemDoor){
            Intent searchIntent = new Intent(Light.this, Door.class);
            startActivity(searchIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }else if(id == R.id.itemCamera) {
            Intent searchIntent = new Intent(Light.this, Camera.class);
            startActivity(searchIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }else if(id == R.id.itemMode) {
            Intent searchIntent = new Intent(Light.this, Mode.class);
            startActivity(searchIntent);
            overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void MonitorLightStatus(){
        String[] lightTopics={"homeautomationledlight/livingroom","homeautomationledlight/kitchen","homeautomationledlight/outside"};
        int[] lightSwitchIDs={R.id.LivingRoomLightSwitch,R.id.KitchenLightSwitch,R.id.OutsideLightSwitch};

        for(int i=0;i<lightTopics.length;i++){

            try{
                client.subscribe(lightTopics[i],0);
            }
            catch (MqttSecurityException e) {
                e.printStackTrace();
            } catch (MqttException e) {
                e.printStackTrace();
            }
            MqttMessage message= new MqttMessage();
            Switch lightSwitch = (Switch) ((Activity) Light.this).findViewById(lightSwitchIDs[i]);

            String msg= new String(message.getPayload());

                switch (msg){

                    case"on":
                        lightSwitch.setChecked(true);
                        break;

                    case "off":
                        lightSwitch.setChecked(false);
                        break;

                    default:
                        break;
                }

        }


    }







}
