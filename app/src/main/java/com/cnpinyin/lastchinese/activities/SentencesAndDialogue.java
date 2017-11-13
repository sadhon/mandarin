package com.cnpinyin.lastchinese.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.Resource;
import com.cnpinyin.lastchinese.R;
import com.cnpinyin.lastchinese.adapters.SentenceEXpListViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SentencesAndDialogue extends AppCompatActivity {

    private ExpandableListView expListView;
    private SentenceEXpListViewAdapter sentenceEXpListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentences_and_dialogue);

        expListView = (ExpandableListView) findViewById(R.id.sentence_exp_listview);

        String[] parentItems = getResources().getStringArray(R.array.sntence_parent_items);
        final String[] childItems = getResources().getStringArray(R.array.sentece_child_itema);

        final List<String> parentList  = new ArrayList<>(Arrays.asList(parentItems));
        final HashMap<String, List<String>> relatedChildList = new HashMap<>();
        relatedChildList.put(parentList.get(0), new ArrayList<String>(Arrays.asList(childItems)) );

        sentenceEXpListViewAdapter = new SentenceEXpListViewAdapter(parentList, relatedChildList, this);

        expListView.setAdapter(sentenceEXpListViewAdapter);
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                String child  = relatedChildList.get(parentList.get(groupPosition)).get(childPosition);

                String parentItem  = parentList.get(0);
                Toast.makeText(SentencesAndDialogue.this, "" + child, Toast.LENGTH_SHORT).show();

                startActivity(new Intent(getApplicationContext(), SentencePageSlider.class));

                return false;
            }
        });
    }
}
