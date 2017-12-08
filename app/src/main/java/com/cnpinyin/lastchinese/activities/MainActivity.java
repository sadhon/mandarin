package com.cnpinyin.lastchinese.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.cnpinyin.lastchinese.R;
import com.cnpinyin.lastchinese.database.Messase;
import com.cnpinyin.lastchinese.database.VocDatabaseAdapter;

public class MainActivity extends AppCompatActivity {

    VocDatabaseAdapter vocHelper;
    EditText url, json, findBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        url = (EditText) findViewById(R.id.urlk);
        json = (EditText) findViewById(R.id.json);
        findBy = (EditText) findViewById(R.id.find_by_url);

        vocHelper = new VocDatabaseAdapter(this);
    }

    public void addData(View view) {
        String urlText = url.getText().toString();
        String jsonText = json.getText().toString();

        Messase.m(this, urlText + "  " + jsonText);
        long id = vocHelper.insertData(urlText, jsonText);
        if(id<0){
            Messase.m(this, "fails..." + id);
        }else {
            Messase.m(this, "success..." + id);
        }
    }

    public void viewDetails(View view) {

        String datas = vocHelper.getAllData();
        Messase.m(this, datas);

    }

    public void getSerchData(View view) {
        String findByText = findBy.getText().toString();
        String jsonData = vocHelper.getData(findByText);

        Messase.m(this, jsonData);

    }
}
