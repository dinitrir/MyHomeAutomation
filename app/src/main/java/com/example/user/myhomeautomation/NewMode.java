package com.example.user.myhomeautomation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;
import org.eclipse.paho.android.service.MqttAndroidClient;
import java.util.Calendar;
import java.util.Objects;

public class NewMode extends AppCompatActivity {

    public static boolean NewMode;
    public static String Scene_Name;
    static Calendar alarmCalender;
    static String ScheduleTime;

    //for color selector activity
    public static String RGBValue="0,0,0";

    //widgets on activity
    private Switch livingRoom,kitchen,Outside,gate,shutter,status;
    private Button setcolor,off,setTIME;
    private EditText txt_modeName;
    private Button mOkButton;
    private Button mCancelButton;

    public static String livingLightState, kitchenLightState,OutsideLightState;
    public static String gateState,shutterState,StatusState;

    final String[] topicSub=null;//for mqtt

    MQTTConnectionToActivity connection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmode);

        connection= new MQTTConnectionToActivity(NewMode.this,topicSub);
        livingRoom= (Switch) findViewById(R.id.LivingRoomLightSwitchMode);
        kitchen= (Switch) findViewById(R.id.KitchenLightSwitchMode);
        Outside= (Switch) findViewById(R.id.OutsideLightSwitchMode);

        gate= (Switch) findViewById(R.id.GateSwitchMode);
        shutter=(Switch) findViewById(R.id.ShutterSwitchMode);

        status=(Switch) findViewById(R.id.status_mode_switch);
        status.setClickable(false);

        setcolor= (Button)findViewById(R.id.RGBLightButtonMode);
        off= (Button)findViewById(R.id.RGBLightButtonOFFMode);

        txt_modeName=(EditText)findViewById(R.id.ModeName);

        setTIME=(Button)findViewById(R.id.btn_time_mode);
        setTIME.setText("SET TIME");

        Scenes s;//Creating Database instance
        final TheDBHandler mdh = new TheDBHandler(this,null,null,3);

        if(NewMode!=true){//if activity is accessed from listview

            status.setClickable(true);
            s=mdh.updateSceneStatusInActivity(Scene_Name);//get scene/mode from DB

            txt_modeName.setText(s.get_sceneName());
            RGBValue=s.get_RGBlight();
            setTIME.setText(s.get_timeToActivate());

            if (s.get_lightLivingRoom().equals("on")) {livingRoom.setChecked(true);}
            if (s.get_lightKitchen().equals("on")) {kitchen.setChecked(true);}
            if (s.get_lightOutside().equals("on")) {Outside.setChecked(true);}
            if (s.get_gate().equals("open")) {gate.setChecked(true);}
            if (s.get_shutter().equals("open")) {shutter.setChecked(true);}
            if (s.get_status().equals("Activated")) {status.setChecked(true);}

        }

        setcolor = (Button) findViewById(R.id.RGBLightButtonMode);
        off = (Button) findViewById(R.id.RGBLightButtonOFFMode);

        setcolor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorSelector.sourceClass="NewMode";//access from NewMode activity
                Intent searchIntent = new Intent(NewMode.this,ColorSelector.class);
                startActivity(searchIntent);
            }
        });

        off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RGBValue="0,0,0";
            }
        });

        mOkButton = (Button) findViewById(R.id.okButtonMode);
        mCancelButton = (Button) findViewById(R.id.cancelButtonMode);

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                livingLightState=(livingRoom.isChecked()?"on":"off");
                kitchenLightState=(kitchen.isChecked()?"on":"off");
                OutsideLightState=(Outside.isChecked()?"on":"off");
                gateState=(gate.isChecked()?"open":"close");
                shutterState=(shutter.isChecked()?"open":"close");

                status.setClickable(true);

                //Custom dialog handler
                ActivatingModeAlertHandler(mdh);
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setTIME.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           final Calendar calendar = Calendar.getInstance();

           int hour=calendar.get(Calendar.HOUR);
           int minute=calendar.get(Calendar.MINUTE);
           TimePickerDialog activationTime =
               new TimePickerDialog(NewMode.this, new TimePickerDialog.OnTimeSetListener() {
                   @Override
                   public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                      setTIME.setText(hourOfDay+":"+minute);//set text of button to time chosen
                      alarmCalender=calendar;
                      alarmCalender.set(
                              calendar.get(Calendar.YEAR),
                              calendar.get(Calendar.MONTH),
                              calendar.get(Calendar.DAY_OF_MONTH),
                      hourOfDay, minute, 0);
                    }
               },hour,minute,true);

                activationTime.show();
            }
        });

        status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
           if(isChecked==true){
              ActivatingModeAlertHandler(mdh);
           }
           else{
               Toast.makeText(NewMode.this,txt_modeName.getText()
               +" mode off ",Toast.LENGTH_SHORT).show();

               connection.PublishToTopic("homeautomationledlight/livingroom","off");
               connection.PublishToTopic("homeautomationledlight/kitchen","off");
               connection.PublishToTopic("homeautomationledlight/outside","off");
               connection.PublishToTopic("homeautomationledlight/rgb","0,0,0");
               connection.PublishToTopic("homeautomationmotor/gate","close");
               connection.PublishToTopic("homeautomationmotor/shutter","close");
               finish();
                }
            }
        });
    }

    public void ActivatingModeAlertHandler(final TheDBHandler mdh){
        AlertDialog.Builder AlertBuilder= new AlertDialog.Builder(NewMode.this);
        AlertBuilder.setMessage("Do you want to activate mode before exit ?")
           .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
             public void onClick(DialogInterface dialog, int which) {
               AlertDialog.Builder AlertActivateMode= new AlertDialog.Builder(NewMode.this);
               AlertActivateMode.setMessage("Activating this mode will override the other settings ?")
                    .setPositiveButton("ACTIVATE", new DialogInterface.OnClickListener() {
                     @Override //Activate mode immediately
                     public void onClick(DialogInterface dialog, int which) {
                        //MQTT Connection
                        MQTTConnectionToActivity connection;
                        connection= new MQTTConnectionToActivity(NewMode.this,topicSub);
                        if(connection.isConnectionGood()==true){
                             connection.PublishToTopic("homeautomationledlight/livingroom",livingLightState);
                             connection.PublishToTopic("homeautomationledlight/kitchen",kitchenLightState);
                             connection.PublishToTopic("homeautomationledlight/outside",OutsideLightState);
                             connection.PublishToTopic("homeautomationledlight/rgb",RGBValue);
                             connection.PublishToTopic("homeautomationmotor/gate",gateState);
                             connection.PublishToTopic("homeautomationmotor/shutter",shutterState);

                             Toast.makeText(NewMode.this,txt_modeName.getText()
                                            +" mode Activated !",Toast.LENGTH_SHORT).show();

                             DatabaseExecutionHandler(true,"Activated",mdh);
                        }else{
                              Toast.makeText(NewMode.this,"No Connection. Not Activated"
                                      ,Toast.LENGTH_LONG).show();

                              DatabaseExecutionHandler(false,"off",mdh);
                              }
                     }
                                }).setNegativeButton("CANCEL",null);//no activation
                        AlertActivateMode.show();
             }
             //decide not to activate mode now
           }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //No immediate activation
                StatusState="off";
                execTransactionToDB(mdh);
                //if time is se
                if((!Objects.equals(setTIME.getText().toString(), "SET TIME")) ) {
                    //schedule activation of mode for later
                    if(alarmCalender!=null) {
                        setScheduledPublishModeAlarm(alarmCalender.getTimeInMillis());
                    }
                }
                finish();
                Intent searchIntent = new Intent(NewMode.this, Mode.class);
                startActivity(searchIntent);
            }
        });
        //force user to select an option to preserve date integrity in DB
        AlertBuilder.setCancelable(false);

        AlertBuilder.show();
    }

    public void execTransactionToDB(TheDBHandler mdh){
        if(NewMode==true){ //INSERT IN DB
            mdh.addScenes(
                    txt_modeName.getText().toString(),
                    livingLightState,
                    kitchenLightState,
                    OutsideLightState,
                    RGBValue,
                    gateState,
                    shutterState,
                    setTIME.getText().toString(),
                    StatusState
            );
        }
        else{//UPDATE DB
            mdh.updateSceneInDB(txt_modeName.getText().toString(),livingLightState,kitchenLightState
                    ,OutsideLightState,RGBValue,gateState,shutterState,setTIME.getText().toString(),StatusState);
        }

        mdh.getWritableDatabase().execSQL(
                "UPDATE scenes SET _status='off' WHERE NOT _sceneName='"+txt_modeName.getText()+"'"
        );
    }

    public void DatabaseExecutionHandler(boolean bool_status,String MyStatusState,TheDBHandler mydbh){
        NewMode.this.finish();
        status.setChecked(bool_status);
        StatusState=MyStatusState;
        setTIME.setText("SET TIME");
        execTransactionToDB(mydbh);
        finish();
        Intent searchIntent = new Intent(NewMode.this, Mode.class);
        startActivity(searchIntent);
    }

    private void setScheduledPublishModeAlarm(long time) {
        //getting the alarm manager
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //creating a new intent specifying the broadcast receiver
        Intent i = new Intent(this, AlarmNotificationForModes.class);

        //creating a pending intent using the intent
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);

        //setting the repeating alarm that will be fired every day
        am.setRepeating(AlarmManager.RTC, time, AlarmManager.INTERVAL_DAY, pi);
        Toast.makeText(this, txt_modeName.getText()+" Mode scheduled", Toast.LENGTH_SHORT).show();
    }

}
