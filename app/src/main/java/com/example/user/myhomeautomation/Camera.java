package com.example.user.myhomeautomation;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Camera extends AppCompatActivity {

    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        HamburgerMenu HamMenu= new HamburgerMenu(Camera.this);
        toggle= HamMenu.getToggle();

        ListView camList = (ListView)findViewById(R.id.camera_listview);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("CCTV Surveillance");
        arrayList.add("Face Recognition");
        arrayList.add("Gesture Recognition");

        CustomListviewAdapter camAdapter = new CustomListviewAdapter(Camera.this,arrayList);
        camList.setAdapter(camAdapter);

        camList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String txt = String.valueOf(parent.getItemAtPosition(position));
                        switch (txt){
                            case "CCTV Surveillance":
                                Intent cctv_intent = new Intent(Camera.this, CCTV.class);
                                startActivity(cctv_intent);
                                break;
                            case "Face Recognition":
                                Intent facial_intent = new Intent(Camera.this, Recognition.class);
                                startActivity(facial_intent);
                                break;
                            case "Gesture Recognition":
                                Intent gesture_intent = new Intent(Camera.this, Recognition.class);
                                startActivity(gesture_intent);
                                break;
                            default:
                                break;

                        }

                    }
                }
        );

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

}
