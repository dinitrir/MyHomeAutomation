package com.example.user.myhomeautomation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

public class NewMode extends AppCompatActivity {
    private Switch livingRoom,kitchen,Outside,gate,shutter;
    private Button setcolor,off;

    private Button mOkButton;
    private Button mCancelButton;


    protected void onCreate(@Nullable Bundle savedInstanceState,boolean NewMode,@Nullable String name) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmode);

        /*if not new update status of switch

         */
        Scenes s;
        TheDBHandler mdh = new TheDBHandler(this,null,null,1);

        if(!NewMode){
          s=mdh.updateSceneStatus(name);
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
