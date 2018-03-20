package com.example.user.myhomeautomation;

import android.app.TimePickerDialog;
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
import java.util.Calendar;

public class NewMode extends AppCompatActivity {

    public static boolean NewMode;
    public static String Scene_Name;

    //for color selector
    public static String RGBValue="0,0,0";

    //widgets on activity
    private Switch livingRoom,kitchen,Outside,gate,shutter,status;
    private Button setcolor,off,setTIME;
    private EditText txt_modeName;
    private Button mOkButton;
    private Button mCancelButton;

    public static String livingLightState, kitchenLightState,OutsideLightState;
    public static String gateState,shutterState,StatusState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmode);

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

        Scenes s;
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
            if (s.get_gate().equals("on")) {gate.setChecked(true);}
            if (s.get_shutter().equals("on")) {shutter.setChecked(true);}
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
                gateState=(gate.isChecked()?"on":"off");
                shutterState=(shutter.isChecked()?"on":"off");

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
                Calendar calendar = Calendar.getInstance();
                int hour=calendar.get(Calendar.HOUR);
                int minute=calendar.get(Calendar.MINUTE);
                TimePickerDialog activationTime =
                        new TimePickerDialog(NewMode.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                setTIME.setText(hourOfDay+":"+minute);
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
                    //mqtt publish
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
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //mqtt publish
                                        Toast.makeText(NewMode.this,txt_modeName.getText()
                                                +" mode Activated !",Toast.LENGTH_SHORT).show();
                                        NewMode.this.finish();
                                        status.setChecked(true);
                                        StatusState="Activated";
                                        setTIME.setText("SET TiME");
                                        exec(mdh);
                                        finish();
                                        Intent searchIntent = new Intent(NewMode.this, Mode.class);
                                        startActivity(searchIntent);
                                    }
                                }).setNegativeButton("CANCEL",null);
                        AlertActivateMode.show();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StatusState="off";
                exec(mdh);
                finish();
                Intent searchIntent = new Intent(NewMode.this, Mode.class);
                startActivity(searchIntent);
            }
        });

        AlertBuilder.setCancelable(false);

        AlertBuilder.show();
    }

    public void exec(TheDBHandler mdh){
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

}
