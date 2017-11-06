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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cnpinyin.lastchinese.R;
import com.cnpinyin.lastchinese.adapters.CustomSwipeAdapter;
import com.cnpinyin.lastchinese.constants.AllConstans;
import com.cnpinyin.lastchinese.extras.PageContent;
import com.cnpinyin.lastchinese.extras.TypeFaceProvider;
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
    private Toolbar toolbar;
    private TextView toolBarTitle;
    private Spinner spinner;

    Button prev, next;
    private int size;


    ArrayList<String> ranges = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabed);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolBarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        spinner = (Spinner) findViewById(R.id.spinner);
        mViewPager = (android.support.v4.view.ViewPager) findViewById(R.id.container);
        prev = (Button) findViewById(R.id.btn_next);
        next = (Button) findViewById(R.id.btn_prev);


        toolBarTitle.setTypeface(TypeFaceProvider.getTypeFace(ViewPagerSlider.this, "orangejuice"));
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        final String child = intent.getStringExtra("pageTitle");
        final String parentEndPoint = intent.getStringExtra("parentEndPoint");


        size = intent.getIntExtra("contentSize", 0);

        // Log.e("endpoint", parentEndPoint);


        // child = child.toUpperCase();
        toolBarTitle.setText(child.toUpperCase());


        next.setOnClickListener(this);
        prev.setOnClickListener(this);

        // Creatin range here...

        int min = 1;
        int high = 0;
        String range = "";


        if (child.equalsIgnoreCase("bct") || parentEndPoint.equalsIgnoreCase("hsk")) {


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

        spinnerAdapter.setDropDownViewResource(R.layout.custom_spiner_dropdown_item);

        spinner.setDropDownWidth(300);
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

                if (child.equalsIgnoreCase("bct") || parentEndPoint.equalsIgnoreCase("hsk")) {
                    index = (min - 1) / 50;
                }

                final int currentPageIndex = index;

                //URL space is replaced by "%20
                String cleanChild = child.replaceAll(" ", "%20");

                //normal range difference 20
                String server_url = AllConstans.SERVER_VOC_URL + parentEndPoint + "/" + cleanChild + "?page=" + index;

                //for range difference 50
                if (parentEndPoint.equalsIgnoreCase("hsk")) {
                    server_url += "&size=50";
                } else if (parentEndPoint.equalsIgnoreCase("bct")) {
                    server_url = AllConstans.SERVER_VOC_URL + parentEndPoint + "?page=" + index + "&size=50";
                }

                //Server data request
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, server_url, (String) null,

                        new Response.Listener<JSONObject>() {
                            ArrayList<PageContent> pageContents = new ArrayList<PageContent>();
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray jsonArray = response.getJSONArray("content");

                                   /* declaring some temporary String variables for catching temporary values*/
                                    String cnchar, pinyin, engword, sound;
                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        //get content object one by one
                                        JSONObject contentObj = jsonArray.getJSONObject(i);

                                        //checking objects belong to which parent
                                        if (parentEndPoint.equals("topic3")) {
                                            cnchar = contentObj.getString("md_cnchar");
                                            pinyin = contentObj.getString("md_pinyin");
                                            engword = contentObj.getString("md_engword");
                                            sound = contentObj.getString("md_sound");

                                        } else if (parentEndPoint.equalsIgnoreCase("hsk")) {
                                            cnchar = contentObj.getString("hskw_char");
                                            pinyin = contentObj.getString("hskw_pinyin");
                                            engword = contentObj.getString("hskw_eng");
                                            sound = contentObj.getString("hsk_sound");

                                        } else if (parentEndPoint.equalsIgnoreCase("bct")) {
                                            cnchar = contentObj.getString("bct_char");
                                            pinyin = contentObj.getString("bct_pinyin");
                                            engword = contentObj.getString("bct_eng");
                                            sound = contentObj.getString("bct_sound");

                                        } else if (parentEndPoint.equalsIgnoreCase("topic2")) {
                                            cnchar = contentObj.getString("wp2_char");
                                            pinyin = contentObj.getString("wp2_pinyin");
                                            engword = contentObj.getString("wp2_eng");
                                            sound = contentObj.getString("wp2_sound");

                                        } else {
                                            cnchar = contentObj.getString("cnchar");
                                            pinyin = contentObj.getString("pinyin");
                                            engword = contentObj.getString("engword");
                                            sound = contentObj.getString("wp_sound");
                                        }

                                        PageContent pageContent = new PageContent(pinyin, engword, cnchar, sound);
                                        pageContents.add(pageContent);

                                    }


                                    customSwipeAdapter = new CustomSwipeAdapter(ViewPagerSlider.this, ranges.size(), pageContents, parentEndPoint);
                                    mViewPager.setAdapter(customSwipeAdapter);

                                    //set Current page
                                    mViewPager.setCurrentItem(currentPageIndex, true);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(ViewPagerSlider.this, "" + e, Toast.LENGTH_SHORT).show();
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


    //controlling previous next page
    @Override
    public void onClick(View v) {
        Button b = (Button) v;
        String s = b.getText().toString();
        int currentPage = mViewPager.getCurrentItem();

        if (s.equals("prev")) {

            int prevIndex = spinner.getSelectedItemPosition() - 1;
            if (prevIndex >= 0) {
                spinner.setSelection(prevIndex);
                mViewPager.setCurrentItem(currentPage - 1, true);
            }
        } else {
            int nextIndex = spinner.getSelectedItemPosition() + 1;
            if (nextIndex < ranges.size()) {
                spinner.setSelection(nextIndex);
                mViewPager.setCurrentItem(currentPage + 1, true);
            }
        }
    }
}