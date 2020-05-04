package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class FutureTournamentsAdapter extends BaseAdapter {

    Context context;
    ArrayList<Tournament> tours;
    LayoutInflater inf;

    FutureTournamentsAdapter(ArrayList<Tournament> a, Context cont ){
        tours=a;//set main objects
        context=cont;//getting activity context
        inf=(LayoutInflater) cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//get service for creating views from xml
    }


    @Override
    public int getCount() {
        return tours.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
