package com.example.user.myhomeautomation;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;


public class Mode extends AppCompatActivity{

    ActionBarDrawerToggle toggle;
    Button AddNewScenes;
    public static Switch s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);

        HamburgerMenu HamMenu= new HamburgerMenu(Mode.this);
        toggle= HamMenu.getToggle();

        //Database/////
        //////////////
        TheDBHandler dbh = new TheDBHandler(this,null,null,1);
        /////////////
        ArrayList<String> scenes= dbh.getAllSceneNamefromDB();

        CustomListviewAdapter sceneAdapter = new CustomListviewAdapter(Mode.this,scenes);
        ListView sceneListView =(ListView) findViewById(R.id.mode_listview);
        sceneListView.setAdapter(sceneAdapter);


        sceneListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        long viewID=view.getId();
                        if(viewID==position) {
                            String txt = String.valueOf(parent.getItemAtPosition(position));
                            Toast.makeText(Mode.this, txt, Toast.LENGTH_SHORT).show();
                        }

                       // Toast.makeText(Mode.this, txt, Toast.LENGTH_SHORT).show();

                    }
                }
        );

        AddNewScenes = (Button) findViewById(R.id.add_listview_item_btn);
        AddNewScenes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(Mode.this, NewMode.class);
                startActivity(searchIntent);
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

}