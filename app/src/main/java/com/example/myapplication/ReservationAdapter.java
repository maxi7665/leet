package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ReservationAdapter extends BaseAdapter {

    ArrayList<Reservation_code> list;
    Context context;
    private LayoutInflater inf;

    ReservationAdapter(ArrayList<Reservation_code> codelist, Context cont){
        context=cont;//getting activity context
        inf=(LayoutInflater) cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//get service for creating views from xml
        list=codelist;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Integer.parseInt(list.get(position).getIdReservation());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=convertView;
        if (view == null) {
            view = inf.inflate(R.layout.reservation_item_layout, parent, false);
        }

        TextView name=view.findViewById(R.id.reservation_name);
        TextView datetime=view.findViewById(R.id.res_date_time);

        name.setText("Standard #"+(list.get(position).getIdComputer()+1));
        datetime.setText(list.get(position).getDateReservation()+" | "+list.get(position).getTime()+" минут");




        return view;
    }
}
