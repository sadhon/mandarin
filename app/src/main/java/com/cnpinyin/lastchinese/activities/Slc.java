package com.cnpinyin.lastchinese.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cnpinyin.lastchinese.R;
import com.cnpinyin.lastchinese.adapters.CustomSwipeAdapter;
import com.cnpinyin.lastchinese.constants.AllConstans;
import com.cnpinyin.lastchinese.extras.CustomViewPager;
import com.cnpinyin.lastchinese.extras.PageContent;
import com.cnpinyin.lastchinese.singleton.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Slc extends AppCompatActivity implements View.OnClickListener{

    private CustomViewPager mViewPager;
    private CustomSwipeAdapter customSwipeAdapter;
    private TextView mainSpinnerTitle, subSpinnerTitle;
    private Spinner mainSpinner, subSpiinner;
    android.support.v7.widget.Toolbar toolbar;
    private Button btnNext, btnPrev;
    ArrayList<String> temp = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slc);



        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);

        mainSpinner = (Spinner) findViewById(R.id.main_spinner);
        subSpiinner = (Spinner) findViewById(R.id.sub_spinner);
        mainSpinnerTitle = (TextView) findViewById(R.id.main_spinner_title);
        subSpinnerTitle = (TextView) findViewById(R.id.sub_spinner_title);
        mViewPager = (CustomViewPager) findViewById(R.id.container);
        btnNext = (Button) findViewById(R.id.btn_prev);
        btnPrev = (Button) findViewById(R.id.btn_next);


        btnPrev.setOnClickListener(this);

        btnNext.setOnClickListener(this) ;


        setSupportActionBar(toolbar);


        Intent intent = getIntent();
        final String parentEndpoint = intent.getStringExtra("parentEndPoint");
        final String childEndPoint = intent.getStringExtra("childEndPoint");

        mainSpinnerTitle.setText(childEndPoint.toUpperCase());
        String url = AllConstans.SERVER_VOC_URL + parentEndpoint + "/" + childEndPoint;
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url, (String) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        final ArrayList<String> mainSpinnerValues = new ArrayList<>();
                        final HashMap<String, Integer> mainMapSub = new HashMap<>(); //Size against radical or stroke
                        String main = "";
                        int size = 0;

                            try {
                                for(int i = 0;  i<response.length(); i++) {
                                    JSONObject jObj = response.getJSONObject(i);

                                    if (childEndPoint.equalsIgnoreCase("stroke")){
                                        main = jObj.getString("numberOfStroke");
                                        size = jObj.getInt("size");
                                    }else if(childEndPoint.equalsIgnoreCase("radical")){
                                        main = jObj.getString("radical");
                                        size = jObj.getInt("size");
                                    }

                                    mainSpinnerValues.add(main);
                                    mainMapSub.put(main, size);
                                }


                                //Main Spinner Adapter setting and selected item control
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_spinner_layout, mainSpinnerValues);
                                adapter.setDropDownViewResource(R.layout.custom_spiner_dropdown_item);
                                mainSpinner.setAdapter(adapter);

                                mainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                        final String mainSpinerText = mainSpinner.getSelectedItem().toString();
                                        int size = mainMapSub.get(mainSpinnerValues.get(position));

                                        temp = getRangeArrayList(size);

                                        ArrayAdapter<String> subArrayApater = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_spinner_layout, temp);

                                        subArrayApater.setDropDownViewResource(R.layout.custom_spiner_dropdown_item);

                                        subSpiinner.setAdapter(subArrayApater);

                                        subSpiinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                                String encodedPart = "";


                                                //finding min value of selected range..
                                                String selectedSpinnerText = subSpiinner.getSelectedItem().toString();
                                                Matcher matcher = Pattern.compile("\\d+").matcher(selectedSpinnerText);
                                                matcher.find();

                                                int min = Integer.valueOf(matcher.group());

                                                //determining page index
                                                int index  = index = (min - 1) / 50;
                                                final int currentPageIndex = index;


                                                try {
                                                    encodedPart = URLEncoder.encode(mainSpinerText, "UTF-8");
                                                } catch (UnsupportedEncodingException e) {
                                                    e.printStackTrace();
                                                }

                                                String url = AllConstans.SERVER_VOC_URL + parentEndpoint + "/"+childEndPoint+"/"+encodedPart + "?page=" + index +"&size=50";


                                                JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, (String) null,
                                                        new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {

                                                                try {
                                                                    JSONArray jsonArray = response.getJSONArray("content");
                                                                    String cnchar, pinyin, engword, sound;
                                                                    ArrayList<PageContent> pageContents = new ArrayList<PageContent>();


                                                                    for(int i = 0 ; i < jsonArray.length(); i++){
                                                                        JSONObject contentObj = jsonArray.getJSONObject(i);

                                                                        cnchar = contentObj.getString("sc_char");
                                                                        pinyin = contentObj.getString("sc_pinyin");
                                                                        engword = contentObj.getString("sc_eng");
                                                                        sound = contentObj.getString("sc_sound");

                                                                        PageContent pageContent = new PageContent(pinyin, engword, cnchar, sound);
                                                                        pageContents.add(pageContent);
                                                                    }

                                                                    customSwipeAdapter = new CustomSwipeAdapter(Slc.this, temp.size(), pageContents, parentEndpoint);
                                                                    mViewPager.setAdapter(customSwipeAdapter);

                                                                    //set Current page
                                                                    mViewPager.setCurrentItem(currentPageIndex, true);

                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        },

                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                error.printStackTrace();
                                                                Toast.makeText(Slc.this, "" + error, Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                );

                                                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(objectRequest);
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });


                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(Slc.this, "" + e, Toast.LENGTH_SHORT).show();
                            }


                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(arrayRequest);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.page_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.back){

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //getting sub spinner itemList
    private ArrayList<String> getRangeArrayList(int size) {
        int min = 1;
        int high = 0;
        String range = "";
        ArrayList<String> ranges = new ArrayList<>();


        for (int i = 1; i <= size; i++) {

            high = min + 49;
            if (size < high) {
                high = size;
            }

            range = "( " + min + "-" + high + " )";
            ranges.add(range);

            if (high == size) {
                return ranges;
            }

            min = high + 1;
        }

        return ranges;
    }



    public void onClick(View v) {
       Button b = (Button) v;
        String s = b.getText().toString();
       int currentPage = mViewPager.getCurrentItem();
        int index =0;

        index = subSpiinner.getSelectedItemPosition();
        b.setVisibility(View.VISIBLE);

        if (s.equalsIgnoreCase("prev")) {
            if (index > 0) {
                subSpiinner.setSelection(index-1);
                mViewPager.setCurrentItem(currentPage - 1, true);
            }

        } else {
            if (index < temp.size() - 1) {
                subSpiinner.setSelection(index+1);
                mViewPager.setCurrentItem(currentPage + 1, true);
            }
        }



    }
}
