package com.example.user.myhomeautomation;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.danielnilsson9.colorpickerview.view.ColorPanelView;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by User on 12/15/2017.
 */

public class ColorSelector extends AppCompatActivity implements ColorPickerView.OnColorChangedListener{

    private ColorPickerView mColorPickerView;
    private ColorPanelView mOldColorPanelView;
    private ColorPanelView mNewColorPanelView;
    private Button mOkButton;
    private Button mCancelButton;
    public static int redNew;
    public static int greenNew;
    public static int blueNew;
    public MqttAndroidClient client=null;
    MQTTConnectionToActivity connection;
    final String[] topicSub=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colorselector);

        //MQTT Connection
        connection= new MQTTConnectionToActivity(ColorSelector.this,topicSub);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int initialColor = prefs.getInt("color_3", 0xFF000000);

        mColorPickerView = (ColorPickerView) findViewById(R.id.colorpickerview__color_picker_view);
        mOldColorPanelView = (ColorPanelView) findViewById(R.id.colorpickerview__color_panel_old);
        mNewColorPanelView = (ColorPanelView) findViewById(R.id.colorpickerview__color_panel_new);

        mOkButton = (Button) findViewById(R.id.okButton);
        mCancelButton = (Button) findViewById(R.id.cancelButton);


        ((LinearLayout) mOldColorPanelView.getParent()).setPadding(
                mColorPickerView.getPaddingLeft(), 0,
                mColorPickerView.getPaddingRight(), 0);


        mColorPickerView.setOnColorChangedListener(this);
        mColorPickerView.setColor(initialColor, true);
        mOldColorPanelView.setColor(initialColor);

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(ColorSelector.this).edit();
                edit.putInt("color_3", mColorPickerView.getColor());
                edit.commit();

                //converting color to rgb
                String white = "#ffffff";
                int whiteInt = Color.parseColor(white);
                removeAlpha(mColorPickerView.getColor(),whiteInt);

                //passing color  rgb code as string
                String content= Integer.toString(ColorSelector.redNew)+","+Integer.toString(ColorSelector.greenNew)+","+Integer.toString(ColorSelector.blueNew);
                String topic = "homeautomationledlight/rgb";

                MemoryPersistence persistence = new MemoryPersistence();
                connection.PublishToTopic(topic,content);
                finish();
            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onColorChanged(int newColor) {
        mNewColorPanelView.setColor(mColorPickerView.getColor());


    }

    //convert ARGB to RGB
    public static void removeAlpha(int foreground, int background) {
        int redForeground = Color.red(foreground);
        int redBackground = Color.red(background);
        int greenForeground = Color.green(foreground);
        int greenBackground = Color.green(background);
        int blueForeground = Color.blue(foreground);
        int blueBackground = Color.blue(background);
        int alphaForeground = Color.alpha(foreground);
        redNew = (redForeground * alphaForeground) + (redBackground * (1 - alphaForeground));
        greenNew = (greenForeground * alphaForeground) + (greenBackground * (1 - alphaForeground));
        blueNew = (blueForeground * alphaForeground) + (blueBackground * (1 - alphaForeground));
        redNew=((redNew/255)+255);
        greenNew=((greenNew/255)+255);
        blueNew=((blueNew/255)+255);
        System.out.print("r->"+redNew);
        System.out.print(" , g->"+greenNew);
        System.out.println(" , b->"+blueNew);
    }
}
