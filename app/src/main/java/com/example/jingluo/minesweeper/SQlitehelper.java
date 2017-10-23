package com.example.jingluo.minesweeper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by jingluo on 2017-10-12.
 */

public class SQlitehelper extends SQLiteOpenHelper {
    public String databasename = "minesweeoer";
    public String tablename = "gamerecords";


    public SQlitehelper(Context context) {
        super(context, "minesweeoer", null, 1);

    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+tablename+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, TIME INT, LEVEL INT)");
        Log.d("DatabaseHandler", "Table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+tablename);
        onCreate(sqLiteDatabase);
    }
    //insert new records to local database
    public Boolean insertrecord(int time, int level){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("TIME", time);
        contentValues.put("LEVEL", level);
        long result = db.insert(tablename,null,contentValues);
        db.close();
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }
    //get all records for a specified level
    public List<record> getAllDatasforlevel(int level)
    {
        List<record> dataList = new ArrayList<record>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tablename + " WHERE LEVEL="+level+" ORDER BY TIME ASC LIMIT 10", null);

        if (cursor.moveToFirst())
        {
            do
            {
                record data = new record(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(2)));
                dataList.add(data);
            }

            while (cursor.moveToNext());
        }
        return dataList;
    }
    //get the number of records in local database
    public int getDataCount()
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tablename, null);
        int count = cursor.getCount();
        db.close();
        cursor.close();

        return count;
    }

}
