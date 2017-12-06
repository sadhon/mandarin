package com.cnpinyin.lastchinese.activities;

import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.cnpinyin.lastchinese.R;
import com.cnpinyin.lastchinese.adapters.ExpandableListAdapter;
import com.cnpinyin.lastchinese.constants.AllConstans;
import com.cnpinyin.lastchinese.singleton.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class VocabularyList extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ExpandableListView exp_listview;
    private ExpandableListAdapter adapter;
    private HashMap<String, String> parentItemToParentEndPoint = new HashMap<>();
    private HashMap<String, String> slcItemToChildEndPoint = new HashMap<>();
    private int lastExpandedPosition = -1;


    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return super.onRetainCustomNonConfigurationInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vocabulary_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        exp_listview = (ExpandableListView) findViewById(R.id.expnadable_listview);
        setSupportActionBar(toolbar);
        parentItemToParentEndPoint.put("By Topics Part 1", "topic");
        parentItemToParentEndPoint.put("By Topics Part 2", "topic2");
        parentItemToParentEndPoint.put("By Topics Part3 + Image", "topic3");
        parentItemToParentEndPoint.put("By Level", "level");
        parentItemToParentEndPoint.put("By Lesson", "lesson");
        parentItemToParentEndPoint.put("By HSK", "hsk");
        parentItemToParentEndPoint.put("By BCT", "bct");
        parentItemToParentEndPoint.put("Single Character List", "sc");

        slcItemToChildEndPoint.put("By Range", "sc-range");
        slcItemToChildEndPoint.put("By Stroke No", "sc-stroke");
        slcItemToChildEndPoint.put("By Radical", "sc-radical");
        slcItemToChildEndPoint.put("By Pinyin", "sc-pinyin");

        final String[] sclChildItems = {"By Range", "By Radical", "By Stroke No", "By Pinyin"};
        String[] vocabularyItems = getResources().getStringArray(R.array.heading_items);
        ArrayList<String> sclChildItemList = new ArrayList<>(Arrays.asList(sclChildItems));
        final List<String> vocabularyList = new ArrayList<String>(Arrays.asList(vocabularyItems));
        final HashMap<String, List<String>> childListUnderVocItem = new HashMap<>();
        //FirstLy set all childList empty except the Last
        for (int i = 0; i < vocabularyList.size() - 1; i++) {
            childListUnderVocItem.put(vocabularyList.get(i), new ArrayList<String>());
        }
        //setting Single Character List default as it is static
        childListUnderVocItem.put(vocabularyList.get(vocabularyList.size() - 1), sclChildItemList);
        adapter = new ExpandableListAdapter(vocabularyList, childListUnderVocItem, getApplicationContext());
        exp_listview.setAdapter(adapter);
        exp_listview.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    exp_listview.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });

        exp_listview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, final int groupPosition, final long id) {
                final String parentEndPoint = parentItemToParentEndPoint.get(vocabularyList.get(groupPosition));

                String server_url = AllConstans.SERVER_BASE_URL +  "by=" + parentEndPoint;

                if (parentEndPoint.equals("bct")) {

                    //get size from size request and start new activity
                    JsonArrayRequest sizeReq = new JsonArrayRequest(Request.Method.GET, server_url, (String) null,
                            new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    try {
                                        JSONObject sizeContainingObj = response.getJSONObject(0);
                                        String stringSize = sizeContainingObj.getString("size");
                                        int intSize = Integer.parseInt(stringSize);

                                        Intent intent = new Intent(getApplicationContext(), ViewPagerSlider.class);
                                        intent.putExtra("parentEndPoint", parentEndPoint);
                                        intent.putExtra("pageTitle", "BCT");
                                        intent.putExtra("contentSize", intSize);
                                        startActivity(intent);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },

                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    if (error instanceof NoConnectionError)
                                    Toast.makeText(VocabularyList.this, "Unable to connect to the server! Please ensure your internet is working!", Toast.LENGTH_SHORT).show();
                                }
                            });
                    MySingleton.getInstance(getApplicationContext()).addToRequestQueue(sizeReq);

                } else {

                    if (parent.isGroupExpanded(groupPosition)) {
                        exp_listview.collapseGroup(groupPosition);
                    } else {
                        int childItemsNumber = childListUnderVocItem.get(vocabularyList.get(groupPosition)).size();
                        if (parentEndPoint.equals("sc") && childItemsNumber > 0) {
                            provideParamsAtChildClick(parentEndPoint, vocabularyList, childListUnderVocItem, new ArrayList<Integer>());
                        } else {

                            JsonArrayRequest jsonArray = new JsonArrayRequest(Request.Method.GET, server_url, (String) null,
                                    new Response.Listener<JSONArray>() {
                                        @Override
                                        public void onResponse(JSONArray response) {
                                            List<String> childValueList = new ArrayList<String>();
                                            final List<Integer> childSizeList = new ArrayList<>();
                                            List<String> keysList = new ArrayList<String>();
                                            try {
                                                JSONObject firstJSONObject = response.getJSONObject(0);
                                                Iterator keysIterator = firstJSONObject.keys();
                                                while (keysIterator.hasNext()) {
                                                    String key = (String) keysIterator.next();
                                                    keysList.add(key);
                                                }
                                                String childValue;
                                                int childSizeValue;
                                                for (int i = 0; i < response.length(); i++) {
                                                    JSONObject singleObj = response.getJSONObject(i);
                                                    /* Determining  single childEndPoint value and size
                                                    as Sometime it doesn't get keys serially so this solution
                                                    obviously only two keys are there.. and one is size*/
                                                    if (keysList.get(0).equalsIgnoreCase("size")) {
                                                        childValue = singleObj.getString(keysList.get(1));
                                                        childSizeValue = singleObj.getInt(keysList.get(0));
                                                    } else {
                                                        childValue = singleObj.getString(keysList.get(0));
                                                        childSizeValue = singleObj.getInt(keysList.get(1));
                                                    }
                                                    childValueList.add(childValue);
                                                    childSizeList.add(childSizeValue);
                                                }
                                                String vocListItem = vocabularyList.get(groupPosition);
                                                childListUnderVocItem.put(vocListItem, childValueList);
                                                adapter.update(childListUnderVocItem);
                                                adapter.notifyDataSetChanged();
                                                exp_listview.expandGroup(groupPosition);
                                                provideParamsAtChildClick(parentEndPoint, vocabularyList, childListUnderVocItem, childSizeList);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                Toast.makeText(VocabularyList.this, "" + e, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            if (error instanceof NoConnectionError)
                                                Toast.makeText(VocabularyList.this, "Unable to connect to the server! Please ensure your internet is working!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            );
                            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArray);
                        }
                    }
                }
                return false;
            }
        });

        //Navigation portion starts here
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void provideParamsAtChildClick(final String parentEndPoint, final List<String> vocabularyList, final HashMap<String, List<String>> childListUnderVocItem, final List<Integer> childSizeList) {

        exp_listview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String childValue = childListUnderVocItem.get(vocabularyList.get(groupPosition))
                        .get(childPosition);
                if (!parentEndPoint.equalsIgnoreCase("sc")) {
                    int childSize = childSizeList.get(childPosition);
                    Intent intent = new Intent(getApplicationContext(), ViewPagerSlider.class);
                    intent.putExtra("parentEndPoint", parentEndPoint);
                    intent.putExtra("pageTitle", childValue);
                    intent.putExtra("contentSize", childSize);
                    startActivity(intent);
                } else {
                    String slcItem = childValue;
                    if (slcItem.equalsIgnoreCase("By Range")) {
                        //Fetching size for Range And go to next activity
                        //String url = AllConstans.SERVER_VOC_URL + "sc";
                        String url = AllConstans.SERVER_BASE_URL + "by=sc-range";
                        JsonArrayRequest objectRequest = new JsonArrayRequest(Request.Method.GET, url, (String) null,
                                new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {

                                        try {

                                            JSONObject rangeContainingObj =  response.getJSONObject(0);

                                            int size = rangeContainingObj.getInt("size");
                                            Intent intent = new Intent(getApplicationContext(), ViewPagerSlider.class);
                                            intent.putExtra("parentEndPoint", parentEndPoint);
                                            intent.putExtra("pageTitle", "By Range");
                                            intent.putExtra("contentSize", size);

                                            startActivity(intent);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        if (error instanceof NoConnectionError)
                                            Toast.makeText(VocabularyList.this, "Unable to connect to the server! Please ensure your internet is working!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(objectRequest);
                    } else {
                        //change childvalue to childEndPoint
                        String childEndPoint = slcItemToChildEndPoint.get(slcItem);

                        Intent intent = new Intent(getApplicationContext(), Slc.class);
                        intent.putExtra("parentEndPoint", parentEndPoint);
                        intent.putExtra("childEndPoint", childEndPoint);
                        startActivity(intent);
                    }
                }
                return false;
            }
        });
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
        getMenuInflater().inflate(R.menu.vocabulary_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
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
