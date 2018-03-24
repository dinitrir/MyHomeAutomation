package com.example.user.myhomeautomation;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Door extends AppCompatActivity {

    ActionBarDrawerToggle toggle;
    public Switch ButtonGateSwitch,ButtonShutterSwitch;
    final String[] topicSub=null;
    //mqtt components
    public MqttAndroidClient client=null;
    MQTTConnectionToActivity connection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door);

        HamburgerMenu HamMenu= new HamburgerMenu(Door.this);
        toggle= HamMenu.getToggle();

        //MQTT Connection
        connection= new MQTTConnectionToActivity(Door.this,topicSub);
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

        //Gate switch
        ButtonGateSwitch= (Switch) findViewById(R.id.GateSwitch);
        ButtonGateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String topic = "homeautomationmotor/gate";
                if(isChecked){
                   connection.PublishToTopic(topic,"open");
                }
                else{
                    connection.PublishToTopic(topic,"close");
                }

            }



        });

        //Shutter switch
        ButtonShutterSwitch= (Switch) findViewById(R.id.ShutterSwitch);
        ButtonShutterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String topic = "homeautomationmotor/shutter";
                if(isChecked){
                    connection.PublishToTopic(topic,"open");
                }
                else{
                    connection.PublishToTopic(topic,"close");
                }

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

    public void HandleMessage(String topic, MqttMessage message){
        String msg= new String(message.getPayload());
        String[] buttonStatus=msg.split("|");
        ButtonGateSwitch.setChecked(buttonStatus[3].equals("on")?true:false);
        ButtonShutterSwitch.setChecked(buttonStatus[4].equals("on")?true:false);

    }

}
