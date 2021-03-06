package com.cnpinyin.lastchinese.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.cnpinyin.lastchinese.extras.PageContent;
import com.cnpinyin.lastchinese.extras.TypeFaceProvider;
import com.cnpinyin.lastchinese.singleton.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ViewPagerSlider extends AppCompatActivity implements View.OnClickListener {
    private android.support.v4.view.ViewPager mViewPager;
    private CustomSwipeAdapter customSwipeAdapter;
    private Spinner spinner;
    private int size;
    TextView contentLoadingtxt;
    private ArrayList<String> ranges = new ArrayList<>();
    private VocDatabaseAdapter voDbHelper = null;
    String parentEndPoint;
    String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Toolbar headingContainer = (Toolbar) findViewById(R.id.heading_container_toolbar);
        TextView toolBarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        spinner = (Spinner) findViewById(R.id.spinner);
        mViewPager = (android.support.v4.view.ViewPager) findViewById(R.id.container);
        Button prev = (Button) findViewById(R.id.btn_next);
        Button next = (Button) findViewById(R.id.btn_prev);
        contentLoadingtxt = (TextView) findViewById(R.id.loading_txt);


        voDbHelper = new VocDatabaseAdapter(this);

        //adding a new fornt
        toolBarTitle.setTypeface(TypeFaceProvider.getTypeFace(ViewPagerSlider.this, "orangejuice"));
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        final String childEndPoint = intent.getStringExtra("pageTitle");
        parentEndPoint = intent.getStringExtra("parentEndPoint");
        size = intent.getIntExtra("contentSize", 0);
        toolBarTitle.setText(childEndPoint.toUpperCase());

        //fixing page header

        LayoutInflater inflater = LayoutInflater.from(this);
        View inflatedLayout= null;

        if(!parentEndPoint.equals("topic3"))
        {
            inflatedLayout= inflater.inflate(R.layout.all_page_heading, null, false);

            //set height and width for inflated layout..
            inflatedLayout.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));

            headingContainer.addView(inflatedLayout);
        }else {


            inflatedLayout= inflater.inflate(R.layout.topic3_heading, null, false);

            //set height and width for inflated layout..
            inflatedLayout.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));

            headingContainer.addView(inflatedLayout);

        }




        next.setOnClickListener(this);
        prev.setOnClickListener(this);
        if (childEndPoint.equalsIgnoreCase("bct") || parentEndPoint.equalsIgnoreCase("hsk") || parentEndPoint.equalsIgnoreCase("sc")) {
            int numberOfRangeItems = 50;
            ranges = getSpinnerRanges(size, numberOfRangeItems);
        } else {
            int numberOfItems = 20;
            ranges = getSpinnerRanges(size, numberOfItems);
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(ViewPagerSlider.this, R.layout.custom_spinner_layout, ranges);
        spinnerAdapter.setDropDownViewResource(R.layout.custom_spiner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Initially hide the content view.
                mViewPager.setVisibility(View.GONE);
                contentLoadingtxt.setVisibility(View.VISIBLE);

                int min = getRangeMinimumValue();

                //determining page index
                int index = (min - 1) / 20;
                if (childEndPoint.equalsIgnoreCase("bct") || parentEndPoint.equalsIgnoreCase("hsk") || parentEndPoint.equalsIgnoreCase("sc")) {
                    index = (min - 1) / 50;
                }
                final int currentPageIndex = index;
                String cleanChildEndPoint = null;
                try {
                    cleanChildEndPoint = URLEncoder.encode(childEndPoint, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                //When item number of page is 20
                String server_url = AllConstans.SERVER_BASE_URL + "size=20&by="+ parentEndPoint + "&filter=" + cleanChildEndPoint+"&paze=" + index;

                //When item number of page is 20
                if (parentEndPoint.equalsIgnoreCase("hsk")) {
                    server_url += "&size=50";
                } else if (parentEndPoint.equalsIgnoreCase("bct") ) {
                    server_url = AllConstans.SERVER_BASE_URL+"size=50&by="+parentEndPoint+"&filter=id&paze=" + index;
                }else if( parentEndPoint.equalsIgnoreCase("sc"))
                {
                    server_url = AllConstans.SERVER_BASE_URL+"size=50&by=sc-range&filter=id&paze=" + index;
                }

                url = server_url;

                /*
                **check first if the url corresponding response is available in db
                **if not the fetch data from server for the first time
                ** and save the server response in the sqlite database.
                */

                if(voDbHelper.hasRow(url))
                {
                    String pageData = voDbHelper.getData(url);
                    try {
                        JSONArray response = new JSONArray(pageData);
                        showPageData(response, parentEndPoint, currentPageIndex);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    fetchDataFromServer(url, parentEndPoint, currentPageIndex);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void fetchDataFromServer(final String url, final String parentEndPoint, final int currentPageIndex) {
        //Server data request
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, (String) null,
                new Response.Listener<JSONArray>() {
                    ArrayList<PageContent> pageContents = new ArrayList<PageContent>();
                    @Override
                    public void onResponse(JSONArray response) {

                        //showing page data from here...
                        showPageData(response, parentEndPoint, currentPageIndex);

                        //save page data to sqlite database
                        voDbHelper.insertData(url, response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof NoConnectionError)
                            Toast.makeText(ViewPagerSlider.this, "Unable to connect to the server! Please ensure your internet is working!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }


    private void showPageData(JSONArray response, String parentEndPoint,  int currentPageIndex) {


        mViewPager.setVisibility(View.VISIBLE);
        contentLoadingtxt.setVisibility(View.GONE);

        ArrayList<PageContent> pageContents = new ArrayList<>();
        try {
            String cnchar, pinyin, engword, sound;
            for(int i = 0 ; i< response.length(); i++){
                JSONObject singleContentObj = response.getJSONObject(i);

                if (parentEndPoint.equals("topic3")) {
                    cnchar = singleContentObj.getString("md_cnchar");
                    pinyin = singleContentObj.getString("md_pytone");
                    engword = singleContentObj.getString("md_engword");
                    sound = singleContentObj.getString("md_sound");

                } else if (parentEndPoint.equalsIgnoreCase("hsk")) {
                    cnchar = singleContentObj.getString("hskw_char");
                    pinyin = singleContentObj.getString("hskw_pytone_m_ws");
                    engword = singleContentObj.getString("hskw_eng");
                    sound = singleContentObj.getString("hsk_sound");

                } else if (parentEndPoint.equalsIgnoreCase("bct")) {
                    cnchar = singleContentObj.getString("bct_char");
                    pinyin = singleContentObj.getString("bct_pytone");
                    engword = singleContentObj.getString("bct_eng");
                    sound = singleContentObj.getString("bct_sound");

                } else if (parentEndPoint.equalsIgnoreCase("topic2")) {
                    cnchar = singleContentObj.getString("wp2_char");
                    pinyin = singleContentObj.getString("wp2_pytone");
                    engword = singleContentObj.getString("wp2_eng");
                    sound = singleContentObj.getString("wp2_sound");

                } else if (parentEndPoint.equalsIgnoreCase("sc")) {
                    cnchar = singleContentObj.getString("SC_char");
                    pinyin = singleContentObj.getString("SC_pytone");
                    engword = singleContentObj.getString("SC_eng");
                    sound = singleContentObj.getString("SC_sound");

                } else {
                    cnchar = singleContentObj.getString("cnchar");
                    pinyin = singleContentObj.getString("pytone_ws");
                    engword = singleContentObj.getString("engword");
                    sound = singleContentObj.getString("wp_sound");
                }

                PageContent singlePageContent = new PageContent(pinyin, engword, cnchar, sound);
                pageContents.add(singlePageContent);
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

    private ArrayList<String> getSpinnerRanges(int size, int numberOfRangeItems) {
        ArrayList<String> ranges = new ArrayList<>();
        int min = 1;
        int high = 0;
        String range = "";
        int diferrence = numberOfRangeItems -1;

        for (int i = 1; i <= size; i++) {
            high = min + diferrence;
            if (size < high) {
                high = size;
            }
            range = "Range ( " + min + "-" + high + " )";
            ranges.add(range);
            if (high == size) {
                return  ranges;
            }
            min = high + 1;
        }
        return ranges;
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

    public int getRangeMinimumValue() {
        int min;
        String s = spinner.getSelectedItem().toString();
        Matcher matcher = Pattern.compile("\\d+").matcher(s);
        matcher.find();
        min = Integer.valueOf(matcher.group());
        return min;
    }


}