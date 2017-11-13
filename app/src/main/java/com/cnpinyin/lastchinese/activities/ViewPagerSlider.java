package com.cnpinyin.lastchinese.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

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
    private Button prev, next;
    private int size;
    private ArrayList<String> ranges = new ArrayList<>();

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
        //adding a new fornt
        toolBarTitle.setTypeface(TypeFaceProvider.getTypeFace(ViewPagerSlider.this, "orangejuice"));
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        final String childEndPoint = intent.getStringExtra("pageTitle");
        final String parentEndPoint = intent.getStringExtra("parentEndPoint");
        size = intent.getIntExtra("contentSize", 0);
        toolBarTitle.setText(childEndPoint.toUpperCase());
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
                int min = getRangeMinimumValue();
                //determining page index
                int index = (min - 1) / 20;
                if (childEndPoint.equalsIgnoreCase("bct") || parentEndPoint.equalsIgnoreCase("hsk") || parentEndPoint.equalsIgnoreCase("sc")) {
                    index = (min - 1) / 50;
                }
                final int currentPageIndex = index;
                String cleanChildEndPoint = childEndPoint.replaceAll(" ", "%20");
                //When item number of page is 20
                String server_url = AllConstans.SERVER_VOC_URL + parentEndPoint + "/" + cleanChildEndPoint + "?page=" + index;
                //When item number of page is 20
                if (parentEndPoint.equalsIgnoreCase("hsk")) {
                    server_url += "&size=50";
                } else if (parentEndPoint.equalsIgnoreCase("bct") || parentEndPoint.equalsIgnoreCase("sc")) {
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
                                    String cnchar, pinyin, engword, sound;
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject singleContentObj = jsonArray.getJSONObject(i);

                                        //checking objects belong to which parent
                                        if (parentEndPoint.equals("topic3")) {
                                            cnchar = singleContentObj.getString("md_cnchar");
                                            pinyin = singleContentObj.getString("md_pinyin");
                                            engword = singleContentObj.getString("md_engword");
                                            sound = singleContentObj.getString("md_sound");

                                        } else if (parentEndPoint.equalsIgnoreCase("hsk")) {
                                            cnchar = singleContentObj.getString("hskw_char");
                                            pinyin = singleContentObj.getString("hskw_pinyin");
                                            engword = singleContentObj.getString("hskw_eng");
                                            sound = singleContentObj.getString("hsk_sound");

                                        } else if (parentEndPoint.equalsIgnoreCase("bct")) {
                                            cnchar = singleContentObj.getString("bct_char");
                                            pinyin = singleContentObj.getString("bct_pinyin");
                                            engword = singleContentObj.getString("bct_eng");
                                            sound = singleContentObj.getString("bct_sound");

                                        } else if (parentEndPoint.equalsIgnoreCase("topic2")) {
                                            cnchar = singleContentObj.getString("wp2_char");
                                            pinyin = singleContentObj.getString("wp2_pinyin");
                                            engword = singleContentObj.getString("wp2_eng");
                                            sound = singleContentObj.getString("wp2_sound");

                                        } else if (parentEndPoint.equalsIgnoreCase("sc")) {
                                            cnchar = singleContentObj.getString("sc_char");
                                            pinyin = singleContentObj.getString("sc_pinyin");
                                            engword = singleContentObj.getString("sc_eng");
                                            sound = singleContentObj.getString("sc_sound");

                                        } else {
                                            cnchar = singleContentObj.getString("cnchar");
                                            pinyin = singleContentObj.getString("pinyin");
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
            public void onNothingSelected(AdapterView<?> parent) {}
        });
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