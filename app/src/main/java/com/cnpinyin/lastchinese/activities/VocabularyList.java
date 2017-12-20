package com.cnpinyin.lastchinese.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.cnpinyin.lastchinese.database.VocDatabaseAdapter;
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
    VocDatabaseAdapter vocDbAdapter = null;
    HashMap<String, List<String>> childListUnderVocItem;
    List<String> vocabularyList;
    String parentEndPoint;
    String server_url;
    int latestGroupPosition = -1;

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return super.onRetainCustomNonConfigurationInstance();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("groupPosition", latestGroupPosition);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vocabulary_list);

        if(isNetworkAvailable())
        {
           // Toast.makeText(this, "available....", Toast.LENGTH_SHORT).show();
        }else {
           // Toast.makeText(this, "not available....", Toast.LENGTH_SHORT).show();
        }


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

        //for working with sqlite
        vocDbAdapter = new VocDatabaseAdapter(VocabularyList.this);

        String[] vocabularyItems = getResources().getStringArray(R.array.heading_items);
        vocabularyList = new ArrayList<String>(Arrays.asList(vocabularyItems));

        final String[] sclChildItems = {"By Range", "By Radical", "By Stroke No", "By Pinyin"};
        ArrayList<String> sclChildItemList = new ArrayList<>(Arrays.asList(sclChildItems));

        childListUnderVocItem = new HashMap<>();

        //FirstLy set all childList empty except the Last
        for (int i = 0; i < vocabularyList.size() - 1; i++) {
            childListUnderVocItem.put(vocabularyList.get(i), new ArrayList<String>());
        }

        //setting Single Character List by default as it is static
        childListUnderVocItem.put(vocabularyList.get(vocabularyList.size() - 1), sclChildItemList);
        adapter = new ExpandableListAdapter(vocabularyList, childListUnderVocItem, getApplicationContext());
        exp_listview.setAdapter(adapter);


        if(savedInstanceState != null )
        {
            latestGroupPosition = savedInstanceState.getInt("groupPosition");
            if(exp_listview.isGroupExpanded(latestGroupPosition))
            {

                Toast.makeText(this, "" + latestGroupPosition, Toast.LENGTH_SHORT).show();
            }else {

                exp_listview.collapseGroup(latestGroupPosition);
                Toast.makeText(this, "not expanded" + latestGroupPosition, Toast.LENGTH_SHORT).show();
            }

        }else {
            Toast.makeText(this, "fails" + latestGroupPosition, Toast.LENGTH_SHORT).show();
        }

        //Allow only one parent(Group) to show its children at a time
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

        //control parent(Group) item click
        exp_listview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, final int groupPosition, final long id) {

                parentEndPoint = parentItemToParentEndPoint.get(vocabularyList.get(groupPosition));
                server_url = AllConstans.SERVER_BASE_URL + "by=" + parentEndPoint;

                latestGroupPosition = groupPosition;

                Toast.makeText(VocabularyList.this, "" + latestGroupPosition, Toast.LENGTH_SHORT).show();

                ///bct has no child
                //so if parent is bct then then fetch size from server and go for next page
                if (parentEndPoint.equalsIgnoreCase("bct")) {
                    if (vocDbAdapter.hasRow(server_url)) {
                        String responseText = vocDbAdapter.getData(server_url);
                        try {
                            JSONArray response = new JSONArray(responseText);
                            startNewPageWithResponse(response, "BCT");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //fetch data from real server and show for the first time
                        getSizeAndStartNewPage("BCT");
                    }
                } else {
                    //Except bct all the parent(heading or group) items have children
                    int childItemsNumber = childListUnderVocItem.get(vocabularyList.get(groupPosition)).size();
                    if (parentEndPoint.equals("sc") && childItemsNumber > 0) {
                        provideParamsAtChildClick(vocabularyList, childListUnderVocItem, new ArrayList<Integer>());
                    } else {
                        if (!vocDbAdapter.hasRow(server_url)) {
                            fetchFrmSrvrAndShowChilds(groupPosition);
                        } else {
                            String responseTxt = vocDbAdapter.getData(server_url);
                            try {
                                JSONArray response = new JSONArray(responseTxt);
                                showChildValues(response, groupPosition);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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


    private void fetchFrmSrvrAndShowChilds(final int groupPosition) {
        JsonArrayRequest jsonArray = new JsonArrayRequest(Request.Method.GET, server_url, (String) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //shows the respective children against certain groupPosition
                        showChildValues(response, groupPosition);
                        //save json response as string against respective url
                        vocDbAdapter.insertData(server_url, response.toString());
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


    private void getSizeAndStartNewPage(final String pageTitle) {
        //get size from size request and start new activity
        JsonArrayRequest sizeReq = new JsonArrayRequest(Request.Method.GET, server_url, (String) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        startNewPageWithResponse(response, pageTitle);

                        //save bct data to lovsl database
                        vocDbAdapter.insertData(server_url, response.toString());
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

    }

    private void startNewPageWithResponse(JSONArray response, String pageTitle) {
        try {
            JSONObject sizeContainingObj = response.getJSONObject(0);
            String stringSize = sizeContainingObj.getString("size");
            int intSize = Integer.parseInt(stringSize);

            Intent intent = new Intent(getApplicationContext(), ViewPagerSlider.class);
            intent.putExtra("parentEndPoint", parentEndPoint);
            intent.putExtra("pageTitle", pageTitle);
            intent.putExtra("contentSize", intSize);
            startActivity(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void showChildValues(JSONArray response, int groupPosition) {

        List<String> childValueList = new ArrayList<>();
        final List<Integer> childSizeList = new ArrayList<>();
        List<String> keysList = new ArrayList<>();
        String vocListItem = vocabularyList.get(groupPosition);
        childListUnderVocItem.put(vocListItem, childValueList);

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

            childListUnderVocItem.put(vocListItem, childValueList);
            adapter.update(childListUnderVocItem);
            adapter.notifyDataSetChanged();

            provideParamsAtChildClick(vocabularyList, childListUnderVocItem, childSizeList);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(VocabularyList.this, "" + e, Toast.LENGTH_SHORT).show();
        }
    }

    private void provideParamsAtChildClick(final List<String> vocabularyList, final HashMap<String, List<String>> childListUnderVocItem, final List<Integer> childSizeList) {

        exp_listview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, final View v, int groupPosition, int childPosition, long id) {
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
                        server_url = AllConstans.SERVER_BASE_URL + "by=sc-range";
                        if (vocDbAdapter.hasRow(server_url)) {
                            String responseText = vocDbAdapter.getData(server_url);
                            try {
                                JSONArray response = new JSONArray(responseText);
                                startNewPageWithResponse(response, "By Range");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            //fetch data from read server and show for the first time
                            getSizeAndStartNewPage("By Range");
                        }
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
