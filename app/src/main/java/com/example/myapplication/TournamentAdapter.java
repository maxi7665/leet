package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TournamentAdapter extends BaseAdapter {


    Context context;
    ArrayList<Tournament> tours;
    LayoutInflater inf;

    TournamentAdapter(ArrayList<Tournament> a, Context cont) {
        tours = a;//set main objects
        context = cont;//getting activity context
        inf = (LayoutInflater) cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//get service for creating views from xml
    }

    @Override
    public int getCount() {
        return tours.size();
    }

    @Override
    public Object getItem(int position) {
        return tours.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Integer.parseInt(tours.get(position).getIdTournament());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inf.inflate(R.layout.tour_list_item, parent, false);
        }
        ((TextView) view.findViewById(R.id.tour_name)).setText(tours.get(position).getTournamentName());

        ((TextView) view.findViewById(R.id.tour_date_game)).setText(tours.get(position).getDate() + " " + tours.get(position).getTime() + " | " + tours.get(position).getGameName());
        if (!tours.get(position).getIdTournament().equals(""))
            view.setTag(Integer.parseInt(tours.get(position).getIdTournament()));
        else
            view.setTag("-1");

        return view;


    }


}
