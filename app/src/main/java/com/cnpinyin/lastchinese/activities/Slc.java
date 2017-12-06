package com.cnpinyin.lastchinese.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Slc extends AppCompatActivity implements View.OnClickListener {

    private CustomViewPager mViewPager;
    private CustomSwipeAdapter customSwipeAdapter;
    private TextView mainSpinnerTitle, subSpinnerTitle;
    private Spinner mainSpinner, subSpinner;
    android.support.v7.widget.Toolbar toolbar;
    private Button btnNext, btnPrev;
    ArrayList<String> temp = new ArrayList<>();
    String parentEndpoint;
    String childEndPoint;
    String urlForMainSpinnerItems = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slc);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        mainSpinner = (Spinner) findViewById(R.id.main_spinner);
        subSpinner = (Spinner) findViewById(R.id.sub_spinner);
        mainSpinnerTitle = (TextView) findViewById(R.id.main_spinner_title);
        subSpinnerTitle = (TextView) findViewById(R.id.sub_spinner_title);
        mViewPager = (CustomViewPager) findViewById(R.id.container);
        btnNext = (Button) findViewById(R.id.btn_prev);
        btnPrev = (Button) findViewById(R.id.btn_next);

        Intent intent = getIntent();
        parentEndpoint = intent.getStringExtra("parentEndPoint");
        childEndPoint = intent.getStringExtra("childEndPoint");
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        setSupportActionBar(toolbar);
        mainSpinnerTitle.setText(childEndPoint.toUpperCase());
       // urlForMainSpinnerItems = AllConstans.SERVER_VOC_URL + parentEndpoint + "/" + childEndPoint;
        urlForMainSpinnerItems = AllConstans.SERVER_BASE_URL + "by=" + childEndPoint;

        if (childEndPoint.equalsIgnoreCase("sc-pinyin")) {
            setMainSpinnerAdapterAndClickListern("", parentEndpoint, childEndPoint);
        } else {
            setMainSpinnerAdapterAndClickListern(urlForMainSpinnerItems, parentEndpoint, childEndPoint);
        }
    }

    private void setMainSpinnerAdapterAndClickListern(String url, final String parentEndpoint, final String childEndPoint) {
        if (url.isEmpty()) {
            String stringOfMainSpinnerItems = "abcdefghjklmnopqrstvwxyz";
            final ArrayList<Character> listOfMainSpinnerItems = new ArrayList<>();
            for (int i = 0; i < stringOfMainSpinnerItems.length(); i++) {
                listOfMainSpinnerItems.add(stringOfMainSpinnerItems.charAt(i));
            }

            ArrayAdapter<Character> adapterForMainSpinner = new ArrayAdapter<Character>(getApplicationContext(), R.layout.custom_spinner_layout, listOfMainSpinnerItems);
            adapterForMainSpinner.setDropDownViewResource(R.layout.custom_spiner_dropdown_item);
            mainSpinner.setAdapter(adapterForMainSpinner);

            mainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //String urlForSupSpinnerItems = urlForMainSpinnerItems + "/" + listOfMainSpinnerItems.get(position);
                    String urlForSupSpinnerItems = AllConstans.SERVER_BASE_URL+"by=sc-pinyin&char=" + listOfMainSpinnerItems.get(position);
                    Log.e("pin", urlForSupSpinnerItems);

                    setSubSpinnerItems(urlForSupSpinnerItems);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

        } else {
            JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url, (String) null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {

                            final ArrayList<String> mainSpinnerValues = new ArrayList<>();
                            final HashMap<String, Integer> mainToSubSize = new HashMap<>(); //Size against radical or stroke
                            String main = "";
                            int size = 0;
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jObj = response.getJSONObject(i);
                                    if (childEndPoint.equalsIgnoreCase("sc-stroke")) {
                                        main = jObj.getString("SC_stroke_no");
                                        size = jObj.getInt("size");
                                    } else if (childEndPoint.equalsIgnoreCase("sc-radical")) {
                                        main = jObj.getString("SC_radical");
                                        size = jObj.getInt("size");
                                    }
                                    mainSpinnerValues.add(main);
                                    mainToSubSize.put(main, size);
                                }
                                //Main Spinner Adapter setting and selected item control
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_spinner_layout, mainSpinnerValues);
                                adapter.setDropDownViewResource(R.layout.custom_spiner_dropdown_item);
                                mainSpinner.setAdapter(adapter);

                                provideParamsForMainSpinnerItemSelection();

                                mainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        final String mainSpinerText = mainSpinner.getSelectedItem().toString();
                                        int size = mainToSubSize.get(mainSpinnerValues.get(position));
                                        temp = getRangeArrayList(size);
                                        ArrayAdapter<String> subArrayApater = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_spinner_layout, temp);
                                        subArrayApater.setDropDownViewResource(R.layout.custom_spiner_dropdown_item);
                                        subSpinner.setAdapter(subArrayApater);
                                        subSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                int rangeMin;
                                                int itemsPerPage = 50;
                                                int spinnerItemIndex = 0;
                                                String numOrCharEncoded = "";
                                                rangeMin = getRangeMinimumNum();
                                                spinnerItemIndex = (rangeMin - 1) / itemsPerPage;
                                                final int currentPageIndex = spinnerItemIndex;
                                                try {
                                                    numOrCharEncoded = URLEncoder.encode(mainSpinerText, "UTF-8");
                                                } catch (UnsupportedEncodingException e) {
                                                    e.printStackTrace();
                                                }
                                                //String url = AllConstans.SERVER_VOC_URL + parentEndpoint + "/" + childEndPoint + "/" + numOrCharEncoded + "?page=" + spinnerItemIndex + "&size=50";

                                                String url = "";
                                                if(childEndPoint.equalsIgnoreCase("sc-radical") )
                                                {
                                                    url = AllConstans.SERVER_BASE_URL + "by=sc-radical&size=50&filter=" + numOrCharEncoded+"&paze="+currentPageIndex;
                                                }else {
                                                   // url = AllConstans.SERVER_BASE_URL + "by=sc-stroke&filter=" + numOrCharEncoded;
                                                    url = AllConstans.SERVER_BASE_URL + "by=sc-stroke&filter=" + numOrCharEncoded + "&size=50&paze=" + currentPageIndex;
                                                }

                                                //Take data from server and set the required params for CustomSwipeAdapter
                                                setCustomSwipeAdapter(url, parentEndpoint, currentPageIndex);
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
                            if (error instanceof NoConnectionError)
                                Toast.makeText(Slc.this, "Unable to connect to the server! Please ensure your internet is working!", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(arrayRequest);
        }
    }

    private void setSubSpinnerItems(String urlForSupSpinnerItems) {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlForSupSpinnerItems, (String) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        final ArrayList<String> listForSubSpinnerItems = new ArrayList<>();
                        String singleItemForSubSpinner;

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject singlObj  = response.getJSONObject(i);
                                singleItemForSubSpinner = singlObj.getString("SC_pinyin_wd");
                                listForSubSpinnerItems.add(singleItemForSubSpinner);
                            }

                            ArrayAdapter<String> adapterForSubSpinner = new ArrayAdapter<>(getApplicationContext(), R.layout.custom_spinner_layout, listForSubSpinnerItems);
                            adapterForSubSpinner.setDropDownViewResource(R.layout.custom_spiner_dropdown_item);
                            subSpinner.setAdapter(adapterForSubSpinner);

                            subSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    //String urlForPageItems = urlForMainSpinnerItems + "/" +"word/" + listForSubSpinnerItems.get(position) ;
                                    String urlForPageItems = AllConstans.SERVER_BASE_URL+"by=sc-pinyin&filter=" + listForSubSpinnerItems.get(position) ;

                                    setPinyinPageItems(urlForPageItems);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {}
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError)
                            Toast.makeText(Slc.this, "Unable to connect to the server! Please ensure your internet is working!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }


    private void setPinyinPageItems(String urlForPageItems) {
        JsonArrayRequest jObjReq = new JsonArrayRequest(Request.Method.GET, urlForPageItems, (String) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            //JSONArray jsonArray = response.getJSONArray("content");
                            String cnchar, pinyin, engword, sound;
                            ArrayList<PageContent> pageContents = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject contentObj = response.getJSONObject(i);
                                cnchar = contentObj.getString("SC_char");
                                pinyin = contentObj.getString("SC_pinyin");
                                engword = contentObj.getString("SC_eng");
                                sound = contentObj.getString("SC_sound");
                                PageContent pageContent = new PageContent(pinyin, engword, cnchar, sound);
                                pageContents.add(pageContent);
                            }

                            customSwipeAdapter = new CustomSwipeAdapter(Slc.this, 1, pageContents, parentEndpoint);
                            mViewPager.setAdapter(customSwipeAdapter);
                            mViewPager.setCurrentItem(0, true);//set Current page
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError)
                            Toast.makeText(Slc.this, "Unable to connect to the server! Please ensure your internet is working!", Toast.LENGTH_SHORT).show();
                    }
                });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jObjReq);
    }


    private void setCustomSwipeAdapter(String url, final String parentEndpoint, final int currentPageIndex) {
        JsonArrayRequest objectRequest = new JsonArrayRequest(Request.Method.GET, url, (String) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                           // JSONArray jsonArray = response.getJSONArray("content");
                            String cnchar, pinyin, engword, sound;
                            ArrayList<PageContent> pageContents = new ArrayList<PageContent>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject contentObj = response.getJSONObject(i);
                                cnchar = contentObj.getString("SC_char");
                                pinyin = contentObj.getString("SC_pinyin");
                                engword = contentObj.getString("SC_eng");
                                sound = contentObj.getString("SC_sound");
                                PageContent pageContent = new PageContent(pinyin, engword, cnchar, sound);
                                pageContents.add(pageContent);
                            }
                            customSwipeAdapter = new CustomSwipeAdapter(Slc.this, temp.size(), pageContents, parentEndpoint);
                            mViewPager.setAdapter(customSwipeAdapter);
                            mViewPager.setCurrentItem(currentPageIndex, true);//set Current page
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError)
                            Toast.makeText(Slc.this, "Unable to connect to the server! Please ensure your internet is working!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(objectRequest);
    }

    private void provideParamsForMainSpinnerItemSelection() {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.page_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.back) {
            finish();//back to previous page/activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


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
        int index = 0;

        index = subSpinner.getSelectedItemPosition();
        if (s.equalsIgnoreCase("prev")) {
            if (index > 0) {
                subSpinner.setSelection(index - 1);
                mViewPager.setCurrentItem(currentPage - 1, true);
            }
        } else {
            if (index < temp.size() - 1) {
                subSpinner.setSelection(index + 1);
                mViewPager.setCurrentItem(currentPage + 1, true);
            }
        }
    }

    public int getRangeMinimumNum() {
        int rangeMin;
        String selectedSpinnerText = subSpinner.getSelectedItem().toString();
        Matcher matcher = Pattern.compile("\\d+").matcher(selectedSpinnerText);
        matcher.find();
        rangeMin = Integer.valueOf(matcher.group());
        return rangeMin;
    }
}
