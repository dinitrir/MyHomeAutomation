package com.example.user.myhomeautomation;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

public class MQTTConnectionToActivity {

    AppCompatActivity CurrentActivity;
    MqttAndroidClient ClientActivity;
    String[] topicSub;


    public MQTTConnectionToActivity(AppCompatActivity activity,String[] topics){
        CurrentActivity=activity;
        topicSub=topics;
        /////// For the MQTT connection
        String clientId = MqttClient.generateClientId();
        ClientActivity =
                new MqttAndroidClient(CurrentActivity.getApplicationContext(), Broker.broker,
                        clientId);

        try {
            IMqttToken token = ClientActivity.connect();
            token.setActionCallback(new IMqttActionListener(){
                @Override
                public void onSuccess(IMqttToken asyncActionToken){
                    // connected
                    Toast.makeText(CurrentActivity.getApplicationContext(), "Connected ", Toast.LENGTH_SHORT).show();
                    if(topicSub!=null){
                        for(int i=0;i<topicSub.length;i++){
                            SubscribeToTopic(topicSub[i]);//subscribe to topic
                        }
                    }

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // connection failed
                    Toast.makeText(CurrentActivity.getApplicationContext(), "Failed ! ", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }


    }//end of constructor

    public void PublishToTopic(String topic,String STR_message){

        try {
            MqttMessage message = new MqttMessage(STR_message.getBytes());
            message.setQos(0);
            ClientActivity.publish(topic, message);
            System.out.println("Message published");

        }
        catch(MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }

    }

    public void SubscribeToTopic(String topic){
        try{
            ClientActivity.subscribe(topic,0);
        }
        catch (MqttSecurityException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public MqttAndroidClient getClient(){
        return this.ClientActivity;
    }
}
