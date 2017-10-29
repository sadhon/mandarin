package com.cnpinyin.lastchinese.activities;

import android.content.Intent;
import android.support.constraint.solver.ArrayLinkedVariables;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cnpinyin.lastchinese.R;
import com.cnpinyin.lastchinese.adapters.CustomSwipeAdapter;
import com.cnpinyin.lastchinese.extras.PageContent;

import java.util.ArrayList;


public class ViewPagerSlider extends AppCompatActivity implements View.OnClickListener{


    private android.support.v4.view.ViewPager mViewPager;
    private CustomSwipeAdapter customSwipeAdapter;
    Toolbar toolbar;
    private Spinner spinner;
    Button prev, next;
    ArrayList<PageContent> pageContents = new ArrayList<PageContent>();



    ArrayList<String> ranges = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabed);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        spinner = (Spinner) findViewById(R.id.spinner);
        mViewPager = (android.support.v4.view.ViewPager) findViewById(R.id.container);
        prev = (Button) findViewById(R.id.btn_next);
        next = (Button) findViewById(R.id.btn_prev);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String topic = intent.getStringExtra("topic");
        int size = intent.getIntExtra("size", 0);


        String[] names = getResources().getStringArray(R.array.names);
        for(String name: names){
            PageContent pageContent = new PageContent(name);
            pageContents.add(pageContent);
        }





        topic = topic.substring(0, 1).toUpperCase() + topic.substring(1);
        getSupportActionBar().setTitle(topic);



        next.setOnClickListener(this);
        prev.setOnClickListener(this);

        // Creatin range here...

        int min = 1;
        int high = 0;
        String range = "";

        for (int i = 1; i < size; i++) {

            high = min + 19;
            if (size < high) {
                high = size;
            }
            Log.e("range", min + " - " + high);

            range = "Range ( " + min + "-" + high + " )";
            ranges.add(range);

            if (high == size) {
                break;
            }

            min = high + 1;


        }


        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(ViewPagerSlider.this, R.layout.custom_spinner_layout, ranges);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(ViewPagerSlider.this, spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        customSwipeAdapter = new CustomSwipeAdapter(this, size, pageContents);
        mViewPager.setAdapter(customSwipeAdapter);


    }

    @Override
    public void onClick(View v) {

        Button b = (Button) v;
        String s = b.getText().toString();
        int currentPage = mViewPager.getCurrentItem();

        if(s.equals("prev")){
            mViewPager.setCurrentItem(currentPage - 1, true);
        }else {
            mViewPager.setCurrentItem(currentPage + 1, true);
        }


    }
}