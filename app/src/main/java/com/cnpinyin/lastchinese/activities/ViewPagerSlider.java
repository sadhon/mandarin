package com.cnpinyin.lastchinese.activities;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cnpinyin.lastchinese.R;
import com.cnpinyin.lastchinese.adapters.CustomSwipeAdapter;
import com.cnpinyin.lastchinese.constants.AllConstans;
import com.cnpinyin.lastchinese.extras.PageContent;
import com.cnpinyin.lastchinese.singleton.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ViewPagerSlider extends AppCompatActivity implements View.OnClickListener {


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

        final String child = intent.getStringExtra("pageTitle");
        final String parentEndPoint = intent.getStringExtra("parentEndPoint");


        size = intent.getIntExtra("contentSize", 0);

        // Log.e("endpoint", parentEndPoint);


        // child = child.toUpperCase();
        getSupportActionBar().setTitle(child);


        next.setOnClickListener(this);
        prev.setOnClickListener(this);

        // Creatin range here...

        int min = 1;
        int high = 0;
        String range = "";


        if (child.equalsIgnoreCase("bct") || parentEndPoint.equalsIgnoreCase("hsk") ) {



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


        } else {
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
                int index = (min - 1) / 20;

                if (child.equalsIgnoreCase("bct") || parentEndPoint.equalsIgnoreCase("hsk") ){
                    index = (min - 1) / 50;
                }


                final int currentPageIndex = index;


                //URL space is replaced by "%20
                String cleanChild = child.replaceAll(" ", "%20");

                //normal range difference 20
                String server_url = AllConstans.SERVER_URL + parentEndPoint + "/" + cleanChild + "?page=" + index;


                //for range difference 50
                if ( parentEndPoint.equalsIgnoreCase("hsk") ){
                    server_url += "&size=50";
                }else if(parentEndPoint.equalsIgnoreCase("bct")){
                    server_url = AllConstans.SERVER_URL + parentEndPoint + "?page=" + index + "&size=50";
                }




                //Server data request

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, server_url, (String) null,

                        new Response.Listener<JSONObject>() {

                            ArrayList<PageContent> pageContents = new ArrayList<PageContent>();

                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                    JSONArray jsonArray = response.getJSONArray("content");

                                    String cnchar;
                                    String pinyin;
                                    String engword;
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject contentObj = jsonArray.getJSONObject(i);


                                        if (parentEndPoint.equalsIgnoreCase("hsk")) {
                                            cnchar = contentObj.getString("hskw_char");
                                            pinyin = contentObj.getString("hskw_pinyin");
                                            engword = contentObj.getString("hskw_eng");
                                        }else if(parentEndPoint.equalsIgnoreCase("bct")){
                                            cnchar = contentObj.getString("bct_char");
                                            pinyin = contentObj.getString("bct_pinyin");
                                            engword = contentObj.getString("bct_eng");
                                        } else {
                                            cnchar = contentObj.getString("cnchar");
                                            pinyin = contentObj.getString("pinyin");
                                            engword = contentObj.getString("engword");

                                        }

                                        PageContent pageContent = new PageContent(pinyin, engword, cnchar);
                                        pageContents.add(pageContent);

                                        customSwipeAdapter = new CustomSwipeAdapter(ViewPagerSlider.this, ranges.size(), pageContents);
                                        mViewPager.setAdapter(customSwipeAdapter);

                                        //set Current page
                                        mViewPager.setCurrentItem(currentPageIndex, true);
                                        //mViewPager.disableScroll(true);


                                        pageChangeListener();


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

    }


    public void  pageChangeListener(){

    }



    @Override
    public void onClick(View v) {

        Button b = (Button) v;
        String s = b.getText().toString();
        int currentPage = mViewPager.getCurrentItem();

        if (s.equals("prev")) {

            int prevIndex = spinner.getSelectedItemPosition()-1;
            if (prevIndex >= 0){
                spinner.setSelection(prevIndex);
                mViewPager.setCurrentItem(currentPage - 1, true);
              //  b.setVisibility(view.Visi);
            }


        } else {


            int nextIndex = spinner.getSelectedItemPosition()+1;
            if (nextIndex < ranges.size()){
                spinner.setSelection(nextIndex);
                mViewPager.setCurrentItem(currentPage + 1, true);
            }

        }


    }
}