package com.example.user.myhomeautomation;

import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

public class Recognition extends AppCompatActivity {
    ActionBarDrawerToggle toggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);

        HamburgerMenu HamMenu = new HamburgerMenu(Recognition.this);
        toggle = HamMenu.getToggle();
    }
}
