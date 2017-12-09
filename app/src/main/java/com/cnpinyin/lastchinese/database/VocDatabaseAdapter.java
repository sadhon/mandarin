package com.cnpinyin.lastchinese.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by User on 12/8/2017.
 */

public class VocDatabaseAdapter {

    private VocHelper vocHelper;
    public VocDatabaseAdapter(Context context)
    {
        vocHelper = new VocHelper(context);
    }
    public long insertData(String url, String jsonData)
    {
        long id;
        if(!hasRow(url))
        {
            SQLiteDatabase db = vocHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(VocHelper.URL, url);
            contentValues.put(VocHelper.JSON_DATA, jsonData);
            id = db.insert(VocHelper.TABLE_NAME, null, contentValues);

            db.close();
        }else
        {
            id = getRowId(url);
        }

        return id;
    }

    public String getAllData()
    {
        SQLiteDatabase db = vocHelper.getWritableDatabase();
        String[] columns = {VocHelper.URL, VocHelper.JSON_DATA};
        Cursor cursor = db.query(VocHelper.TABLE_NAME, columns, null, null, null, null, null);

        String allData = "";
        while (cursor.moveToNext())
        {
           // int id = cursor.getInt(0);
            String url = cursor.getString(0);
            String jsonData = cursor.getString(1);
            allData += url + " " + jsonData + "\n";
        }

        cursor.close();
        db.close();
        return allData;
    }

    public String getData(String url)
    {
        SQLiteDatabase db = vocHelper.getWritableDatabase();
        String[] columns = {VocHelper.JSON_DATA};
        String[] selections = {url};
        Cursor cursor = db.query(VocHelper.TABLE_NAME, columns, VocHelper.URL + " =?", selections, null, null, null);

        String jsnDataAsTxt="";
        if (cursor.moveToNext())
        {
            int index1 = cursor.getColumnIndex(VocHelper.JSON_DATA);
            jsnDataAsTxt =  cursor.getString(index1);
        }

        cursor.close();
        db.close();
        return jsnDataAsTxt;
    }

    private long getRowId(String url)
    {
        SQLiteDatabase db = vocHelper.getWritableDatabase();
        String[] columns = {VocHelper.ID};
        String[] selections = {url};
        Cursor cursor = db.query(VocHelper.TABLE_NAME, columns, VocHelper.URL + " =?", selections, null, null, null);

        long id = -1;
        if (cursor.moveToNext())
        {
            int index1 = cursor.getColumnIndex(VocHelper.ID);
            id =  cursor.getInt(index1);
        }

        cursor.close();
        db.close();
        return id;
    }

    public boolean hasRow(String url)
    {
        SQLiteDatabase db = vocHelper.getWritableDatabase();
        String[] columns = {VocHelper.JSON_DATA};
        String[] selections = {url};
        Cursor cursor = db.query(VocHelper.TABLE_NAME, columns, VocHelper.URL + " =?", selections, null, null, null);
        int totalRows = cursor.getCount();

        cursor.close();
        db.close();

        if(totalRows >0)
        {
            return true;
        }else {
            return false;
        }
    }


    static class VocHelper  extends SQLiteOpenHelper {
        private static final String DATABASE_NAME="vocdatabase";
        private static final String TABLE_NAME="VOCTABLE";
        private static final String ID="_id";
        private static final String URL="Url";
        private static final String JSON_DATA="JsonData";
        private static final int DATABASE_VERSION = 4;
        private static final String CREATE_TABLE = "CREATE TABLE "+ VocHelper.TABLE_NAME+
                " ("+ VocHelper.ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+ VocHelper.URL+
                " VARCHAR(255), "+ VocHelper.JSON_DATA+" TEXT);";
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + VocHelper.TABLE_NAME;

        private Context context;
        VocHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
           // String q ="CREATE TABLE VOCTABLE (_id INTEGER PRIMARY KEY AUTOINCREMENT, Url VARCHAR(255), JsonData TEXT);";
            try{
                sqLiteDatabase.execSQL(CREATE_TABLE);
            }catch (SQLException e){
                Messase.m(context, "" + e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            //String q = "DROP TABLE IF EXISTS VOCTABLE";
            try{
                sqLiteDatabase.execSQL(DROP_TABLE);
                onCreate(sqLiteDatabase);
            }catch (SQLException e){
                Messase.m(context, ""+e);
            }
        }
    }
}
