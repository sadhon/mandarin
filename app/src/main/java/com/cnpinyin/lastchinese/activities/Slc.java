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
import com.cnpinyin.lastchinese.database.VocDatabaseAdapter;
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

public class Slc extends AppCompatActivity implements View.OnClickListener {

    private CustomViewPager mViewPager;
    private CustomSwipeAdapter customSwipeAdapter;
    private Spinner mainSpinner, subSpinner;
    android.support.v7.widget.Toolbar toolbar;
    ArrayList<String> temp = new ArrayList<>();
    String parentEndpoint;
    String childEndPoint;
    String urlForMainSpinnerItems = "";
    VocDatabaseAdapter vocDbAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slc);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        mainSpinner = (Spinner) findViewById(R.id.main_spinner);
        subSpinner = (Spinner) findViewById(R.id.sub_spinner);
        TextView mainSpinnerTitle = (TextView) findViewById(R.id.main_spinner_title);
        mViewPager = (CustomViewPager) findViewById(R.id.container);
        Button btnNext = (Button) findViewById(R.id.btn_prev);
        Button btnPrev = (Button) findViewById(R.id.btn_next);
        vocDbAdapter = new VocDatabaseAdapter(this);

        Intent intent = getIntent();
        parentEndpoint = intent.getStringExtra("parentEndPoint");
        childEndPoint = intent.getStringExtra("childEndPoint");
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        setSupportActionBar(toolbar);
        mainSpinnerTitle.setText(childEndPoint.toUpperCase());

        //generate url for main (first) spinner item
        urlForMainSpinnerItems = AllConstans.SERVER_BASE_URL + "by=" + childEndPoint;

        if (childEndPoint.equalsIgnoreCase("sc-pinyin")) {
            //empty url because need not face data from url, its static
            setMainSpinnerAdapterAndClickListern("", parentEndpoint, childEndPoint);
        } else {
            setMainSpinnerAdapterAndClickListern(urlForMainSpinnerItems, parentEndpoint, childEndPoint);
        }
    }

    private void setMainSpinnerAdapterAndClickListern(final String url, final String parentEndpoint, final String childEndPoint) {
        if (url.isEmpty()) {
            /*
            * This is part is for pinyin
            *
            * */


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

                    setSubSpinnerItemsForPinyin( urlForSupSpinnerItems );
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });



        } else {

           /*
            **check first if the url corresponding response is available in db
            **if not the fetch data from server for the first time
            ** and save the server response in the sqlite database.
            */

            if(vocDbAdapter.hasRow(url))
            {
                String subSpnrItms = vocDbAdapter.getData(url);
                try {
                    JSONArray response = new JSONArray(subSpnrItms);
                    setSubSpnrItmsExcptPinyin(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                //fetch sub spinner items from server for the first time and save to db
                fetchSubSpnrItms(url);
            }
        }
    }

    private void fetchSubSpnrItms(final String url) {
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url, (String) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        setSubSpnrItmsExcptPinyin(response);

                        //save data to sqlite database for the first time
                        vocDbAdapter.insertData(url, response.toString());
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


    private void setSubSpnrItmsExcptPinyin(JSONArray response) {
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


                            String url = "";
                            if(childEndPoint.equalsIgnoreCase("sc-radical") )
                            {
                                url = AllConstans.SERVER_BASE_URL + "by=sc-radical&size=50&filter=" + numOrCharEncoded+"&paze="+currentPageIndex;

                            }else {
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

    private void setSubSpinnerItemsForPinyin(String urlForSupSpinnerItems) {



        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlForSupSpinnerItems, (String) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        showSubSpinnerItemsForPinyin(response);

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


    private void showSubSpinnerItemsForPinyin(JSONArray response) {
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
                                pinyin = contentObj.getString("SC_pytone");
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


    private void setCustomSwipeAdapter(final String url, final String parentEndpoint, final int currentPageIndex) {

        if(vocDbAdapter.hasRow(url))
        {
            String pageData = vocDbAdapter.getData(url);
            try {
                JSONArray response = new JSONArray(pageData);
                showScPageDataExctpPin(response, currentPageIndex);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else
        {
            fetchScPageData(url, currentPageIndex);
        }

    }


    private void fetchScPageData(final String url,final int currentPageIndex) {
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url, (String) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        //show page data except for the first time
                        showScPageDataExctpPin(response, currentPageIndex);

                        //save resopse to database
                        vocDbAdapter.insertData(url, response.toString());

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

    private void showScPageDataExctpPin(JSONArray response, int currentPageIndex) {
        try {
            // JSONArray jsonArray = response.getJSONArray("content");
            String cnchar, pinyin, engword, sound;
            ArrayList<PageContent> pageContents = new ArrayList<PageContent>();
            for (int i = 0; i < response.length(); i++) {
                JSONObject contentObj = response.getJSONObject(i);
                cnchar = contentObj.getString("SC_char");
                pinyin = contentObj.getString("SC_pytone");
                engword = contentObj.getString("SC_eng");
                sound = contentObj.getString("SC_sound");
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
