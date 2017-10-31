package com.cnpinyin.lastchinese.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.cnpinyin.lastchinese.R;
import com.cnpinyin.lastchinese.adapters.ExpandableListAdapter;
import com.cnpinyin.lastchinese.constants.AllConstans;
import com.cnpinyin.lastchinese.singleton.MySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class VocabularyList extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ExpandableListView exp_listview;
    ExpandableListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vocabulary_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        exp_listview = (ExpandableListView) findViewById(R.id.expnadable_listview);

        //toolbar setting
        setSupportActionBar(toolbar);


        //Hasmap for dynamically getting url endpoint

        final HashMap<String, String> map = new HashMap<>();

        map.put("By Topics Part 1", "topic");
        map.put("By Topics Part 2", "lesson");
        map.put("By Topics Part3 + Image", "lesson");
        map.put("By Level", "level");
        map.put("By Lesson", "lesson");
        map.put("By HSK", "hsk");
        map.put("By BCT", "bct");
        map.put("Single Character List", "lesson");


        //Vocbulary Main Items example: topic , lesson etc
        String[] heading_items = getResources().getStringArray(R.array.heading_items);

        //Converting array into arrayList
        final List<String> headings = new ArrayList<String>(Arrays.asList(heading_items));

        //Sub Item List under Main Item. example: conversatoin, verb etc under topic
        final HashMap<String, List<String>> childList = new HashMap<String, List<String>>();

        //Firstly set sub itemList empty coz data will load dynamically from server
        for (int i = 0; i < headings.size(); i++) {
            childList.put(headings.get(i), new ArrayList<String>());
        }

        //provided here Main Item List , hashmap of child Item List and applicationContext
        adapter = new ExpandableListAdapter(headings, childList, getApplicationContext());

        //initial setAdapter for ExpandableListView here
        exp_listview.setAdapter(adapter);

        //Here group click listener will be inseted..


        exp_listview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, final int groupPosition, final long id) {
                 /*when enpoint is "bct" then no need to show child directly go to the content page */


                final String parentEndPoint = map.get(headings.get(groupPosition));

                if (parentEndPoint.equals("bct")) {
                    Intent intent = new Intent(getApplicationContext(),
                            ViewPagerSlider.class);

                    intent.putExtra("parentEndPoint", parentEndPoint);
                    intent.putExtra("pageTitle", "BCT");
                    intent.putExtra("contentSize", 1035);

                    startActivity(intent);
                } else {

                    if (parent.isGroupExpanded(groupPosition)) {
                        exp_listview.collapseGroup(groupPosition);
                    } else {

                        String server_url = AllConstans.SERVER_URL + parentEndPoint;

                       // Log.e("parent1", parentEndPoint);


                        JsonArrayRequest jsonArray = new JsonArrayRequest(Request.Method.GET, server_url, (String) null,
                                new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {

                                        List<String> childValueList = new ArrayList<String>();
                                        final List<Integer> childSizeList = new ArrayList<>();
                                        List<String> keysList = new ArrayList<String>();



                                        try {


                                            //Determining dynamically keys from the first object

                                            JSONObject firstJSONObject = response.getJSONObject(0);
                                            Iterator keysIterator = firstJSONObject.keys();

                                            while (keysIterator.hasNext()) {
                                                String key = (String) keysIterator.next();
                                                keysList.add(key);

                                            }

                                            //Determining child values and sizes using the above keys

                                            String childValue;
                                            int childSizeValue;

                                            for (int i = 0; i < response.length(); i++) {
                                                // Get current json object
                                                JSONObject singleObj = response.getJSONObject(i);

                                                // Determining  single child value and size


                                                //as Sometime it doesn't get keys serially so this solution

                                                if(keysList.get(0).equalsIgnoreCase("size")){
                                                    childValue = singleObj.getString(keysList.get(1));
                                                    childSizeValue = singleObj.getInt(keysList.get(0));
                                                }else {
                                                    childValue = singleObj.getString(keysList.get(0));
                                                    childSizeValue = singleObj.getInt(keysList.get(1));
                                                }

                                                //adding single child value and size in respective List
                                                childValueList.add(childValue);
                                                childSizeList.add(childSizeValue);

                                            }

                                            //Adding child value list against each parent
                                            childList.put(headings.get(groupPosition), childValueList);

                                            //Update ExpandableListAdapter..
                                            adapter.update(childList);

                                            //Setting adapter with expandable list view
                                            exp_listview.setAdapter(adapter);
                                            exp_listview.expandGroup(groupPosition);


                                            exp_listview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                                                @Override
                                                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                                                    int childSize = childSizeList.get(childPosition);
                                                    String childValue = childList.get(headings.get(groupPosition))
                                                            .get(childPosition);

                                                    Intent intent = new Intent(getApplicationContext(),
                                                            ViewPagerSlider.class);


                                                    intent.putExtra("parentEndPoint", parentEndPoint);
                                                    intent.putExtra("pageTitle", childValue);
                                                    intent.putExtra("contentSize", childSize);

                                                    startActivity(intent);

                                                    return false;
                                                }
                                            });


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                },

                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        Toast.makeText(VocabularyList.this, error + "", Toast.LENGTH_SHORT).show();
                                    }
                                }

                        );

                        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArray);
                    }


                }

                return false;
            }
        });


        //This is Navigation portion
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vocabulary_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            startActivity(new Intent(this, NewActivity.class));
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
