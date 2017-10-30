package com.cnpinyin.lastchinese.activities;

import android.content.Intent;
import android.support.constraint.solver.ArrayLinkedVariables;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cnpinyin.lastchinese.R;
import com.cnpinyin.lastchinese.adapters.CustomSwipeAdapter;
import com.cnpinyin.lastchinese.extras.PageContent;
import com.cnpinyin.lastchinese.singleton.MySingleton;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ViewPagerSlider extends AppCompatActivity implements View.OnClickListener{


    private android.support.v4.view.ViewPager mViewPager;
    private CustomSwipeAdapter customSwipeAdapter;
    Toolbar toolbar;
    private Spinner spinner;
    Button prev, next;
    private int size;




    ArrayList<String> ranges = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabed);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        spinner = (Spinner) findViewById(R.id.spinner);
        mViewPager = (android.support.v4.view.ViewPager) findViewById(R.id.container);
        prev = (Button) findViewById(R.id.btn_next);
        next = (Button) findViewById(R.id.btn_prev);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String topic = intent.getStringExtra("pageTitle");
        size = intent.getIntExtra("contentSize", 0);


/*
        String[] names = getResources().getStringArray(R.array.names);
        for(String name: names){
            PageContent pageContent = new PageContent(name);
            pageContents.add(pageContent);
        }
*/





        topic = topic.toUpperCase();
        getSupportActionBar().setTitle(topic);



        next.setOnClickListener(this);
        prev.setOnClickListener(this);

        // Creatin range here...

        int min = 1;
        int high = 0;
        String range = "";

        if(topic.equalsIgnoreCase("bct")){

            for (int i = 1; i < size; i++) {

                high = min + 49;
                if (size < high) {
                    high = size;
                }

                range = "Range ( " + min + "-" + high + " )";
                ranges.add(range);

                if (high == size) {
                    break;
                }

                min = high + 1;


            }


        }else{
            for (int i = 1; i < size; i++) {

                high = min + 19;
                if (size < high) {
                    high = size;
                }

                range = "Range ( " + min + "-" + high + " )";
                ranges.add(range);

                if (high == size) {
                    break;
                }

                min = high + 1;


            }

        }




        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(ViewPagerSlider.this, R.layout.custom_spinner_layout, ranges);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                //finding min value of selected range..
                String s = spinner.getSelectedItem().toString();
                Matcher matcher = Pattern.compile("\\d+").matcher(s);
                matcher.find();
                int min = Integer.valueOf(matcher.group());

                //determining page index

                int index = (min-1) / 20;

              //  Log.e("index", index+"");

                String server_url = "http://192.168.43.167:8080/voc/topic/conversation?page=0";

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, server_url, (String) null,

                        new Response.Listener<JSONObject>() {

                            ArrayList<PageContent> pageContents = new ArrayList<PageContent>();
                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                    JSONArray jsonArray = response.getJSONArray("content");

                                    for(int i = 0; i<jsonArray.length(); i++){
                                        JSONObject contentObj = jsonArray.getJSONObject(i);

                                        //String character = contentObj.getString("char");


                                        String pinyin = contentObj.getString("pinyin");
                                        String engword = contentObj.getString("engword");


                                        PageContent pageContent = new PageContent(pinyin, engword);
                                        pageContents.add(pageContent);


                                        customSwipeAdapter = new CustomSwipeAdapter(ViewPagerSlider.this, size, pageContents);
                                        mViewPager.setAdapter(customSwipeAdapter);


                                    }



                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },


                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ViewPagerSlider.this, error + "", Toast.LENGTH_SHORT).show();
                            }
                        }


                );

                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);






            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


      /*  customSwipeAdapter = new CustomSwipeAdapter(this, size, pageContents);
        mViewPager.setAdapter(customSwipeAdapter);
*/

    }

    @Override
    public void onClick(View v) {

        Button b = (Button) v;
        String s = b.getText().toString();
        int currentPage = mViewPager.getCurrentItem();

        if(s.equals("prev")){
            mViewPager.setCurrentItem(currentPage - 1, true);
        }else {
            mViewPager.setCurrentItem(currentPage + 1, true);
        }


    }
}