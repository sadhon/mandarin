package com.cnpinyin.lastchinese.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.cnpinyin.lastchinese.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by inspiron on 8/24/2017.
 */

public class ExpandabelListAdapter extends BaseExpandableListAdapter {

    private List<String> header_titles;
    private HashMap<String, List<String>> child_titles;
    private Context ctx;

    public ExpandabelListAdapter(List<String> header_titles, HashMap<String, List<String>> child_titles, Context ctx) {
        this.header_titles = header_titles;
        this.child_titles = child_titles;
        this.ctx = ctx;
    }



    @Override
    public int getGroupCount() {
        return header_titles.size();
    }




    @Override
    public int getChildrenCount(int groupPosition) {
        return child_titles.get(header_titles.get(groupPosition)).size();
    }




    @Override
    public Object getGroup(int groupPosition) {
        return header_titles.get(groupPosition);
    }




    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return child_titles.get(header_titles.get(groupPosition)).get(childPosition);
    }




    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }





    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }





    @Override
    public boolean hasStableIds() {
        return false;
    }





    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String title = (String) this.getGroup(groupPosition);

        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.parent_layout, null);
        }

        TextView headerTitle = (TextView) convertView.findViewById(R.id.header_title);
        headerTitle.setText(title);
        headerTitle.setTypeface(null, Typeface.BOLD);

        return convertView;
    }



    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String child_title = (String) getChild(groupPosition, childPosition);
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_layout, null);
        }

        TextView childHeader = (TextView) convertView.findViewById(R.id.child_header);
        childHeader.setText(child_title);
        childHeader.setTypeface(null, Typeface.BOLD);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
