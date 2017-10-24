package com.cnpinyin.lastchinese.background_tasks;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cnpinyin.lastchinese.extras.VocabularyMainList;
import com.cnpinyin.lastchinese.singleton.MySingleton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by inspiron on 8/24/2017.
 */
public class BackgroundTask {

    Context ctx;
    final String server_url = "http://cnpinyin.com/pinyin/API/CnpinyinApiHandler.php";
    ArrayList<VocabularyMainList> mainVocabularyItems  = new ArrayList<>();

    public BackgroundTask(Context ctx) {
        this.ctx = ctx;
    }



    public int getMainVocabularyItems(){
        JsonObjectRequest jsonOb = new JsonObjectRequest(Request.Method.GET, server_url,(String ) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

/*                Toast.makeText(ctx, "" + response, Toast.LENGTH_SHORT).show();
                Toast.makeText(ctx, ""+ response, Toast.LENGTH_SHORT).show();*/

                Iterator<String> keys = response.keys();
                //BackgroundTask b = new BackgroundTask(ctx);
                while (keys.hasNext()){



                    String key = (String) keys.next();
                    String value = response.optString(key);

                    VocabularyMainList vMainItem = new VocabularyMainList(value);


                    mainVocabularyItems.add(vMainItem);
                    /*Toast.makeText(ctx, ""+ mainVocabularyItems.size(), Toast.LENGTH_SHORT).show();*/
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ctx, "Error occurs....", Toast.LENGTH_SHORT).show();

            }
        });

        MySingleton.getInstance(ctx).addToRequestQueue(jsonOb);
        return mainVocabularyItems.size();
    }
}
