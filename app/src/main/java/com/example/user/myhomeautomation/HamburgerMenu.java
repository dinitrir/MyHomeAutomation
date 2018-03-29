package com.example.user.myhomeautomation;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class HamburgerMenu implements NavigationView.OnNavigationItemSelectedListener{

    ActionBarDrawerToggle toggle;
    AppCompatActivity currentActivity;

    public HamburgerMenu(AppCompatActivity activity){
        currentActivity=activity;
        Toolbar toolbar=(Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        DrawerLayout drawer =(DrawerLayout) activity.findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                activity,drawer,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.NavView);
        navigationView.setNavigationItemSelectedListener(this);
    }

   public ActionBarDrawerToggle getToggle(){
        return toggle;
   }

    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.itemDashboard){
            Intent searchIntent = new Intent(currentActivity.getApplicationContext(), MainActivity.class);
            currentActivity.startActivity(searchIntent);
            currentActivity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }else if(id == R.id.itemLight){
            Intent searchIntent = new Intent(currentActivity.getApplicationContext(), Light.class);
            currentActivity.startActivity(searchIntent);
            currentActivity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }else if(id == R.id.itemDoor){
            Intent searchIntent = new Intent(currentActivity.getApplicationContext(), Door.class);
            currentActivity.startActivity(searchIntent);
            currentActivity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }else if(id == R.id.itemCamera) {
            Intent searchIntent = new Intent(currentActivity.getApplicationContext(), Camera.class);
            currentActivity.startActivity(searchIntent);
            currentActivity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }else if(id == R.id.itemMode) {
            Intent searchIntent = new Intent(currentActivity.getApplicationContext(), Mode.class);
            currentActivity.startActivity(searchIntent);
            currentActivity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }else if(id == R.id.itemVoice) {
            Intent searchIntent = new Intent(currentActivity.getApplicationContext(),VoiceInterpreter.class);
            currentActivity.startActivity(searchIntent);
            currentActivity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        }
        DrawerLayout drawer = (DrawerLayout) currentActivity.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
