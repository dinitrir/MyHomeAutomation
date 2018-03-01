package com.example.user.myhomeautomation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class NewMode extends AppCompatActivity {

    private Button mOkButton;
    private Button mCancelButton;


    protected void onCreate(@Nullable Bundle savedInstanceState, boolean NewMode,@Nullable String name) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmode);

        /*if not new update status of switch

         */
        if(!NewMode){

        }

        mOkButton = (Button) findViewById(R.id.okButtonMode);
        mCancelButton = (Button) findViewById(R.id.cancelButtonMode);

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
}
