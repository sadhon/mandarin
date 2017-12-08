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
        SQLiteDatabase db = vocHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(VocHelper.URL, url);
        contentValues.put(VocHelper.JSON_DATA, jsonData);

        long id = db.insert(VocHelper.TABLE_NAME, null, contentValues);

        return id;

    }

    public String getAllData()
    {
        SQLiteDatabase db = vocHelper.getWritableDatabase();
        String[] columns = {VocHelper.URL, VocHelper.JSON_DATA};
        Cursor cursor = db.query(VocHelper.TABLE_NAME, columns, null, null, null, null, null);

        StringBuffer sb = new StringBuffer();

        while (cursor.moveToNext())
        {
           // int id = cursor.getInt(0);
            String url = cursor.getString(0);
            String jsonData = cursor.getString(1);
            sb.append( url + " " + jsonData + "\n");
        }
        return sb.toString();
    }

    public String getData(String url)
    {
        SQLiteDatabase db = vocHelper.getWritableDatabase();
        String[] columns = {VocHelper.JSON_DATA};
        String[] selections = {url};
        Cursor cursor = db.query(VocHelper.TABLE_NAME, columns, VocHelper.URL + " =?", selections, null, null, null);

        StringBuffer sb = new StringBuffer();

        while (cursor.moveToNext())
        {
            int index1 = cursor.getColumnIndex(VocHelper.JSON_DATA);
            String jsonData = cursor.getString(index1);
            sb.append(jsonData);
        }
        return sb.toString();
    }


    public boolean hasRow(String url)
    {

        SQLiteDatabase db = vocHelper.getWritableDatabase();
        String[] columns = {VocHelper.JSON_DATA};
        String[] selections = {url};
        Cursor cursor = db.query(VocHelper.TABLE_NAME, columns, VocHelper.URL + " =?", selections, null, null, null);

        int totalRows = cursor.getCount();
        if(totalRows >0)
            return true;

        return false;
    }


    static class VocHelper  extends SQLiteOpenHelper {
        private static final String DATABASE_NAME="vocdatabase";
        private static final String TABLE_NAME="VOCTABLE";
        private static final String ID="_id";
        private static final String URL="Url";
        private static final String JSON_DATA="JsonData";
        private static final int DATABASE_VERSION = 3;
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
