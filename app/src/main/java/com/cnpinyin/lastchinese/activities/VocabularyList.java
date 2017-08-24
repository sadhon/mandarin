package com.cnpinyin.lastchinese.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cnpinyin.lastchinese.R;
import com.cnpinyin.lastchinese.adapters.ExpandabelListAdapter;
import com.cnpinyin.lastchinese.singleton.MySingleton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class VocabularyList extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ExpandableListView exp_listview;
    ExpandabelListAdapter adapter;
    ArrayList<String> mainVocabularyItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vocabulary_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        /*FOR DATABASE DATA*/



        /*END DATABASE DATA*/





        /*Expandable List View*/

    exp_listview = (ExpandableListView) findViewById(R.id.expnadable_listview);





        /*End Expandable List Code*/




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




        getMainVocabularyItems();



    }



    public void getMainVocabularyItems(){
        String server_url = "http://cnpinyin.com/pinyin/API/CnpinyinApiHandler.php";

        JsonObjectRequest jsonOb = new JsonObjectRequest(Request.Method.GET, server_url,(String ) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //  mainVocabularyItems = parseJson(response);


                Iterator<String> keys = response.keys();
                //BackgroundTask b = new BackgroundTask(ctx);
                while (keys.hasNext()){



                    String key = (String) keys.next();
                    String value = response.optString(key);



                    mainVocabularyItems.add(value);
                    Toast.makeText(getApplicationContext(), ""+ mainVocabularyItems.size(), Toast.LENGTH_SHORT).show();
                }


                //-----------------------------------------


                String[] l1 = getResources().getStringArray(R.array.h1_items);
                String[] l2 = getResources().getStringArray(R.array.h2_items);
                String[] l3 = getResources().getStringArray(R.array.h3_items);

                List<String> headings = mainVocabularyItems;
                List L1 = new ArrayList<String>(Arrays.asList(l1));
                List L2 = new ArrayList<String>(Arrays.asList(l2));
                List L3 = new ArrayList<String>(Arrays.asList(l3));


                HashMap<String, List<String>> childList  = new HashMap<String, List<String>>();

                childList.put(headings.get(0), L1);
                childList.put(headings.get(1), L2);
                childList.put(headings.get(2), L3);


                adapter = new ExpandabelListAdapter(headings, childList, getApplicationContext());
                exp_listview.setAdapter(adapter);


                //Item Event Click Control here:

                controlItemClickEvent();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error occurs....", Toast.LENGTH_SHORT).show();

            }
        });

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonOb);
    }



    public void controlItemClickEvent(){
        exp_listview.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                //Toast.makeText(Vocabulary.this, headings.get(groupPosition) + "is expanded..", Toast.LENGTH_SHORT).show();

            }
        });

        exp_listview.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {

                //Toast.makeText(Vocabulary.this, headings.get(groupPosition) + " is collasped..", Toast.LENGTH_SHORT).show();

            }
        });


        exp_listview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                String itemName = (String) adapter.getChild(groupPosition, childPosition);
                //   Toast.makeText(Vocabulary.this, itemName +  " is clicked...", Toast.LENGTH_SHORT).show();


                startActivity(new Intent(VocabularyList.this, NewActivity.class));


                return true;
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
