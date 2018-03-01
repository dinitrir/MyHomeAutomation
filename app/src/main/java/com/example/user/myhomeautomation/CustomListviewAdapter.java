package com.example.user.myhomeautomation;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListviewAdapter extends ArrayAdapter<String>{

    CustomListviewAdapter(Context context, ArrayList<String> scenes){
        super(context,R.layout.list_item,scenes);
    }

    @Override
    public View getView(int position,  View convertView, ViewGroup parent) {
        LayoutInflater scenceInflater = LayoutInflater.from(getContext());
        View customView = scenceInflater.inflate(R.layout.list_item,parent,false);

        String scene = getItem(position);
        TextView sceneName=(TextView) customView.findViewById(R.id.mode_name);
        ImageView sceneImage= (ImageView) customView.findViewById(R.id.mode_pic);

        sceneName.setText(scene);
        sceneImage.setImageResource(R.drawable.night_mode);
        return customView;
    }
}
