package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PeriodAdapter extends BaseAdapter {

    Context context;
    private LayoutInflater inf;
    private int start;
    private String[] periods;
    private int[] flags;


    PeriodAdapter(int sta, int[] fla, String[] per, Context cont) {
        context = cont;//getting activity context
        inf = (LayoutInflater) cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//get service for creating views from xml
        start = sta;
        flags = fla;
        periods = per;


    }

    @Override
    public int getCount() {
        return 48 - start;
    }

    @Override
    public Object getItem(int position) {
        return periods[position];
    }

    @Override
    public long getItemId(int position) {
        return position + start;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inf.inflate(R.layout.period_layout, parent, false);
        }

        TextView period = view.findViewById(R.id.period_info);
        period.setText("Время: ");
        period.append(periods[position + start]);

        TextView state = view.findViewById(R.id.period_state);
        ImageView state_ind = view.findViewById(R.id.period_state_indicator);


        if (flags[position] == 1) {
            state.setText("Занято");
            state_ind.setImageResource(R.drawable.red);
        } else {
            state.setText("Доступно");
            state_ind.setImageResource(R.drawable.green);

        }

        return view;
    }
}
