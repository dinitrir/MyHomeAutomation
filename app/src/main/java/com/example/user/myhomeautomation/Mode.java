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
import android.widget.PopupMenu;
import android.widget.Toast;
import java.util.ArrayList;

public class Mode extends AppCompatActivity{

    ActionBarDrawerToggle toggle;
    Button AddNewScenes;
    static ListView sceneListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);

        HamburgerMenu HamMenu= new HamburgerMenu(Mode.this);
        toggle= HamMenu.getToggle();

        //Database/////
        final TheDBHandler dbh = new TheDBHandler(this,null,null,3);
        /////////////
        ArrayList<String> scenes= dbh.getAllSceneNamefromDB();
        //create listview
        sceneListView=refreshLisView(scenes);

        //select a mode in listview
        sceneListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String txt = String.valueOf(parent.getItemAtPosition(position));
                        NewMode.Scene_Name=txt;//To retrieve data of this mode on the Activity
                        NewMode.NewMode=false;
                        Intent searchIntent = new Intent(Mode.this, NewMode.class);
                        startActivity(searchIntent);
                    }
                }
        );

        //Long press to trigger delete option
        sceneListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PopupMenu item_scene_pop = new PopupMenu(Mode.this,sceneListView);
                item_scene_pop.getMenuInflater().inflate(R.menu.popup_menu,item_scene_pop.getMenu());

                final AdapterView<?> parenter=parent;
                final int positioner =position;
                item_scene_pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String scene_name = String.valueOf(parenter.getItemAtPosition(positioner));
                        //delete query method in TheDBhandler class
                        dbh.deleteScenes(scene_name);
                        Toast.makeText(Mode.this,scene_name
                                +" mode deleted successfully !",Toast.LENGTH_SHORT).show();
                        sceneListView=refreshLisView(dbh.getAllSceneNamefromDB());
                        return true;
                    }
                });
                item_scene_pop.show();
                return true;
            }
        });

        AddNewScenes = (Button) findViewById(R.id.add_listview_item_btn);
        AddNewScenes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewMode.NewMode=true;//preparing activity to embrace new mode
                finish();
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

    public ListView refreshLisView( ArrayList<String> scenes){
        //Create List View
        CustomListviewAdapter sceneAdapter = new CustomListviewAdapter(Mode.this,scenes);
        final ListView sceneListView =(ListView) findViewById(R.id.mode_listview);
        sceneListView.setAdapter(sceneAdapter);
        return sceneListView;
    }

}