package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TwoStringsAdapter extends BaseAdapter {

    String[] s1;
    String[] s2;
    LayoutInflater inf;
    Context cont;

    TwoStringsAdapter(Context context, String[] str1, String[] str2){
        cont=context;
        inf=(LayoutInflater)  cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        s1=str1;
        s2=str2;
    }

    @Override
    public int getCount() {
        return s1.length;
    }

    @Override
    public Object getItem(int position) {
        return s1[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=convertView;
        if (view == null) {
            view = inf.inflate(R.layout.two_string_list_item, parent, false);
        }

        TextView main=view.findViewById(R.id.main_string);
        TextView desc=view.findViewById(R.id.description);

        if(s1.length>position){
            main.setText(s1[position]);
        }

        if(s2.length>position){
            if(!s2[position].equals("")) {
                desc.setText(s2[position]);
            } else {
                desc.setText("*Не задано*");
            }
        }

        return view;
    }
}
