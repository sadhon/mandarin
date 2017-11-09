package com.cnpinyin.lastchinese.activities;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.cnpinyin.lastchinese.R;
import com.cnpinyin.lastchinese.constants.AllConstans;
import com.cnpinyin.lastchinese.singleton.MySingleton;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Slc extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView mainSpinnerTitle, subSpinnerTitle;
    private Spinner mainSpinner, subSpiinner;
    private ViewPager viewPager;
    String superVar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slc);

        superVar = "helllllllllooooo";

        mainSpinner = (Spinner) findViewById(R.id.main_spinner);
        subSpiinner = (Spinner) findViewById(R.id.sub_spinner);
        mainSpinnerTitle = (TextView) findViewById(R.id.main_spinner_title);
        subSpinnerTitle = (TextView) findViewById(R.id.sub_spinner_title);



        Intent intent = getIntent();
        String parentEndpoint = intent.getStringExtra("parentEndPoint");
        final String childEndPoint = intent.getStringExtra("childEndPoint");

        mainSpinnerTitle.setText(childEndPoint.toUpperCase());
        String url = AllConstans.SERVER_VOC_URL + parentEndpoint + "/" + childEndPoint;

      //  ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, new ArrayList<CharSequence>());

        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url, (String) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        final ArrayList<String> mainSpinnerValues = new ArrayList<>();
                        ArrayList<String> mainSpinnerSizes = new ArrayList<>();
                        final HashMap<String, Integer> mainMapSub = new HashMap<>();
                        String main = "";
                        int size = 0;

                            try {
                                for(int i = 0;  i<response.length(); i++) {
                                    JSONObject jObj = response.getJSONObject(i);

                                    if (childEndPoint.equalsIgnoreCase("stroke")){
                                        main = jObj.getString("numberOfStroke");
                                        size = jObj.getInt("size");
                                    }
                                    mainSpinnerValues.add(main);
                                    mainMapSub.put(main, size);
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_spinner_layout, mainSpinnerValues);
                                adapter.setDropDownViewResource(R.layout.custom_spiner_dropdown_item);
                                mainSpinner.setAdapter(adapter);

                                mainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                        int size = mainMapSub.get(mainSpinnerValues.get(position));
                                        ArrayList<String> temp = new ArrayList<>();

                                        temp = getRangeArrayList(size);
                                        ArrayAdapter<String> subArrayApater = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_spinner_layout, temp);

                                        subArrayApater.setDropDownViewResource(R.layout.custom_spiner_dropdown_item);

                                        subSpiinner.setAdapter(subArrayApater);

                                        Toast.makeText(Slc.this, "" + superVar, Toast.LENGTH_SHORT).show();

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

                    }
                }
        );

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(arrayRequest);





    }

    private ArrayList<String> getRangeArrayList(int size) {
        int min = 1;
        int high = 0;
        String range = "";
        ArrayList<String> ranges = new ArrayList<>();


        for (int i = 1; i < size; i++) {

            high = min + 49;
            if (size < high) {
                high = size;
            }

            range = "( " + min + "-" + high + " )";
            ranges.add(range);

            if (high == size) {
                break;
            }

            min = high + 1;
        }

        return ranges;
    }
}
