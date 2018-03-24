package com.example.user.myhomeautomation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.TextView;
import java.util.ArrayList;

public class TheDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=3;
    private static final String DATABASE_NAME="scenes.db";
    private static final String TABLE_SCENES="scenes";
    private static final String COLUMN_SCENENAME="_sceneName";
    private static final String COLUMN_lIGHTLIVINGROOM="_lightLivingRoom";
    private static final String COLUMN_LIGHTKITCHEN="_lightKitchen";
    private static final String COLUMN_LIGHTOUTSIDE="_lightOutside";
    private static final String COLUMN_RGBLIGHT= "_RGBlight";
    private static final String COLUMN_GATE="_gate";
    private static final String COLUMN_SHUTTER="_shutter";
    private static final String COLUMN_TIMETOACTIVATE="_timeToActivate";
    private static final String COLUMN_STATUS="_status";

    public TheDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query= "CREATE TABLE "+ TABLE_SCENES+"("+
                COLUMN_SCENENAME+" TEXT PRIMARYKEY,"+
                COLUMN_lIGHTLIVINGROOM+" TEXT,"+
                COLUMN_LIGHTKITCHEN+" TEXT,"+
                COLUMN_LIGHTOUTSIDE+" TEXT,"+
                COLUMN_RGBLIGHT+" TEXT,"+
                COLUMN_GATE+" TEXT,"+
                COLUMN_SHUTTER+" TEXT,"+
                COLUMN_TIMETOACTIVATE+ " TEXT,"+
                COLUMN_STATUS+ " TEXT"+
                ");";

        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_SCENES);
        onCreate(db);
    }

    //add a row to db
    public void addScenes(String SceneName,String LivingRoom,String Kitchen,String Outside,String RGB,String gate,String Shutter,String time,String status){
        ContentValues values = new ContentValues();

        values.put(COLUMN_SCENENAME,SceneName);
        values.put(COLUMN_lIGHTLIVINGROOM,LivingRoom);
        values.put(COLUMN_LIGHTKITCHEN,Kitchen);
        values.put(COLUMN_LIGHTOUTSIDE,Outside);
        values.put(COLUMN_RGBLIGHT,RGB);
        values.put(COLUMN_GATE,gate);
        values.put(COLUMN_SHUTTER,Shutter);
        values.put(COLUMN_TIMETOACTIVATE,time);
        values.put(COLUMN_STATUS,status);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_SCENES,null,values);
        db.close();
    }

    //delete a row
    public void deleteScenes(String sceneName){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_SCENES +" WHERE " + COLUMN_SCENENAME +"='" + sceneName + "'");
        db.close();
    }

    //fill arraylist with scenename
    public ArrayList<String> getAllSceneNamefromDB(){

        ArrayList<String> arrayname = new ArrayList<String>();

        SQLiteDatabase db = getWritableDatabase();
        String query ="SELECT * FROM "+ TABLE_SCENES;

        //cursor to traverse results
        Cursor c = db.rawQuery(query,null);

        if(c.getCount()!=0){
           //move to first row
           c.moveToFirst();
            do {
               arrayname.add(c.getString(c.getColumnIndex("_sceneName")));
            } while (c.moveToNext());
        }

        c.close();
        db.close();
        return arrayname;
    }

    public Scenes updateSceneStatusInActivity(String name){
        Scenes s= new Scenes();
        SQLiteDatabase db = getWritableDatabase();
        String query ="SELECT * FROM "+ TABLE_SCENES +" WHERE "+COLUMN_SCENENAME+"='"+name+"'";

        //cursor to traverse results
        Cursor c = db.rawQuery(query,null);

        if(c.getCount()!=0) {
            //move to first row
            if (c.moveToFirst()) {
                s.set_sceneName(c.getString(c.getColumnIndex("_sceneName")));
                s.set_lightLivingRoom(c.getString(c.getColumnIndex("_lightLivingRoom")));
                s.set_lightKitchen(c.getString(c.getColumnIndex("_lightKitchen")));
                s.set_lightOutside(c.getString(c.getColumnIndex("_lightOutside")));
                s.set_RGBlight(c.getString(c.getColumnIndex("_RGBlight")));
                s.set_gate(c.getString(c.getColumnIndex("_gate")));
                s.set_shutter(c.getString(c.getColumnIndex("_shutter")));
                s.set_timeToActivate(c.getString(c.getColumnIndex("_timeToActivate")));
                s.set_status(c.getString(c.getColumnIndex("_status")));
            }
        }
        c.close();
        db.close();
        return s;
    }

    public void updateSceneInDB(String SceneName,String LivingRoom,String Kitchen,String Outside,String RGB,String gate,String Shutter,String time,String status){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE "+ TABLE_SCENES +" SET "+
                COLUMN_SCENENAME+"='"+SceneName+"',"+
                COLUMN_lIGHTLIVINGROOM+"='"+LivingRoom+"',"+
                COLUMN_LIGHTKITCHEN+"='"+Kitchen+"',"+
                COLUMN_LIGHTOUTSIDE+"='"+Outside+"',"+
                COLUMN_RGBLIGHT+"='"+RGB+"',"+
                COLUMN_GATE+"='"+gate+"',"+
                COLUMN_SHUTTER+"='"+Shutter+"',"+
                COLUMN_TIMETOACTIVATE+"='"+time+"',"+
                COLUMN_STATUS+"='"+status+"' "+
                "WHERE "+COLUMN_SCENENAME+"='"+SceneName+"'" );
        db.close();
    }

    public Scenes getAlarmDetails(String time){
        Scenes s= new Scenes();
        SQLiteDatabase db = getWritableDatabase();
        String query ="SELECT * FROM "+ TABLE_SCENES +" WHERE "+COLUMN_TIMETOACTIVATE+"='"+time+"'";

        //cursor to traverse results
        Cursor c = db.rawQuery(query,null);

        if(c.getCount()!=0) {
            //move to first row
            if (c.moveToFirst()) {
                s.set_sceneName(c.getString(c.getColumnIndex("_sceneName")));
                s.set_lightLivingRoom(c.getString(c.getColumnIndex("_lightLivingRoom")));
                s.set_lightKitchen(c.getString(c.getColumnIndex("_lightKitchen")));
                s.set_lightOutside(c.getString(c.getColumnIndex("_lightOutside")));
                s.set_RGBlight(c.getString(c.getColumnIndex("_RGBlight")));
                s.set_gate(c.getString(c.getColumnIndex("_gate")));
                s.set_shutter(c.getString(c.getColumnIndex("_shutter")));
                s.set_timeToActivate(c.getString(c.getColumnIndex("_timeToActivate")));
                s.set_status(c.getString(c.getColumnIndex("_status")));
            }
        }
        c.close();
        db.close();
        return s;
    }

    public void display(TextView t){
        Scenes s= new Scenes();
        SQLiteDatabase db = getWritableDatabase();
        String query ="SELECT * FROM scenes";

        String ger=null;

        //cursor to traverse results
        Cursor c = db.rawQuery(query,null);

        if(c.getCount()!=0) {
            //move to first row
            if (c.moveToFirst()) {
                s.set_sceneName(c.getString(c.getColumnIndex("_sceneName")));
                s.set_lightLivingRoom(c.getString(c.getColumnIndex("_lightLivingRoom")));
                s.set_lightKitchen(c.getString(c.getColumnIndex("_lightKitchen")));
                s.set_lightOutside(c.getString(c.getColumnIndex("_lightOutside")));
                s.set_RGBlight(c.getString(c.getColumnIndex("_RGBlight")));
                s.set_gate(c.getString(c.getColumnIndex("_gate")));
                s.set_shutter(c.getString(c.getColumnIndex("_shutter")));
                s.set_timeToActivate(c.getString(c.getColumnIndex("_timeToActivate")));
                s.set_status(c.getString(c.getColumnIndex("_status")));
            }
            ger+=s.get_sceneName()+" "+" \n"+
                    s.get_lightLivingRoom()+" "+" \n"+
                    s.get_lightKitchen()+" "+" \n"+
                    s.get_lightOutside()+" "+" \n"+
                    s.get_RGBlight()+" "+" \n"+
                    s.get_gate()+" "+" \n"+
                    s.get_shutter()+" "+" \n"+
                    s.get_timeToActivate()+" "+" \n"+
                    s.get_status()+" \n";

            while(c.moveToNext()){
                s.set_sceneName(c.getString(c.getColumnIndex("_sceneName")));
                s.set_lightLivingRoom(c.getString(c.getColumnIndex("_lightLivingRoom")));
                s.set_lightKitchen(c.getString(c.getColumnIndex("_lightKitchen")));
                s.set_lightOutside(c.getString(c.getColumnIndex("_lightOutside")));
                s.set_RGBlight(c.getString(c.getColumnIndex("_RGBlight")));
                s.set_gate(c.getString(c.getColumnIndex("_gate")));
                s.set_shutter(c.getString(c.getColumnIndex("_shutter")));
                s.set_timeToActivate(c.getString(c.getColumnIndex("_timeToActivate")));
                s.set_status(c.getString(c.getColumnIndex("_status")));

                ger+=s.get_sceneName()+" "+" \n"+
                        s.get_lightLivingRoom()+" "+" \n"+
                        s.get_lightKitchen()+" "+" \n"+
                        s.get_lightOutside()+" "+" \n"+
                        s.get_RGBlight()+" "+" \n"+
                        s.get_gate()+" "+" \n"+
                        s.get_shutter()+" "+" \n"+
                        s.get_timeToActivate()+" "+" \n"+
                        s.get_status()+" \n";
            }
        }

        t.setText(ger);
        c.close();
        db.close();
    }//for testing purposes
}
