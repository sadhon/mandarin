package com.cnpinyin.lastchinese.database;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by User on 12/8/2017.
 */

public class Messase {
    public static void m(Context context, String message)
    {
        Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
    }
}
