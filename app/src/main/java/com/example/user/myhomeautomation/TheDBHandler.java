package com.example.user.myhomeautomation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class TheDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="scenes.db";
    private static final String TABLE_SCENES="scenes";
    private static final String  COLUMN_SCENENAME="_sceneName";
    private static final String COLUMN_lIGHTLIVINGROOM="_lightLivingRoom";
    private static final String COLUMN_LIGHTKITCHEN="_lightKitchen";
    private static final String COLUMN_LIGHTOUTSIDE="_lightOutside";
    private static final String COLUMN_RGBLIGHT= "_RGBlight";
    private static final String COLUMN_GATE="_gate";
    private static final String COLUMN_SHUTTER="_shutter";
    private static final String COLUMN_TIMETOACTIVATE="_timeToAcivate";

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
                COLUMN_TIMETOACTIVATE+ " TEXT"+
                ");";

        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_SCENES);
        onCreate(db);
    }

    //add a row to db
    public void addScenes(Scenes scenes){
        ContentValues values = new ContentValues();

        values.put(COLUMN_SCENENAME,scenes.get_sceneName());
        values.put(COLUMN_lIGHTLIVINGROOM,scenes.get_lightLivingRoom());
        values.put(COLUMN_LIGHTKITCHEN,scenes.get_lightKitchen());
        values.put(COLUMN_LIGHTOUTSIDE,scenes.get_lightOutside());
        values.put(COLUMN_RGBLIGHT,scenes.get_RGBlight());
        values.put(COLUMN_GATE,scenes.get_gate());
        values.put(COLUMN_SHUTTER,scenes.get_shutter());
        values.put(COLUMN_TIMETOACTIVATE,scenes.get_timeToAcivate());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_SCENES,null,values);
        db.close();
    }

    //delete a row
    public void deleteScenes(String sceneName){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_SCENES +" WHERE " + COLUMN_SCENENAME +"=\"" + sceneName + "\";");
    }

    //fill arraylist with scenename
    public ArrayList<String> getAllSceneNamefromDB(){

        ArrayList<String> arrayname = new ArrayList<String>();

        SQLiteDatabase db = getWritableDatabase();
        String query ="SELECT * FROM "+ TABLE_SCENES +" WHERE 1";

        //cursor to traverse results
        Cursor c = db.rawQuery(query,null);

        //move to first row
        c.moveToFirst();

        while (!c.isAfterLast()){
            arrayname.add(c.getString(c.getColumnIndex("_sceneName")));
        }

        return arrayname;
    }

    public Scenes updateSceneStatus(String name){
        Scenes s= new Scenes();
        SQLiteDatabase db = getWritableDatabase();
        String query ="SELECT * FROM "+ TABLE_SCENES +" WHERE "+name;

        //cursor to traverse results
        Cursor c = db.rawQuery(query,null);

        //move to first row
        if(c.moveToFirst()) {
            s.set_sceneName(c.getString(c.getColumnIndex("_sceneName")));
            s.set_lightLivingRoom(c.getString(c.getColumnIndex("_sceneName")));
            s.set_lightKitchen(c.getString(c.getColumnIndex("_sceneName")));
            s.set_lightOutside(c.getString(c.getColumnIndex("_sceneName")));
            s.set_RGBlight(c.getString(c.getColumnIndex("_sceneName")));
            s.set_gate(c.getString(c.getColumnIndex("_sceneName")));
            s.set_shutter(c.getString(c.getColumnIndex("_sceneName")));
            s.set_timeToAcivate(c.getString(c.getColumnIndex("_sceneName")));
        }
        return s;
    }
}
