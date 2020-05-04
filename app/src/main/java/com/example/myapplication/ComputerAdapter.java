package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ComputerAdapter extends BaseAdapter {

    ArrayList<Computer> computers;
    int[] free;
    Context cont;
    LayoutInflater inf;


    ComputerAdapter(ArrayList<Computer> a, int[] b, Context c){
        computers=a;
        free=b;
        cont=c;
        inf=(LayoutInflater) cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//get service for creating views from xml
    }


    @Override
    public int getCount() {
        return computers.size();
    }

    @Override
    public Object getItem(int position) {
        return computers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return computers.get(position).getIdComputer();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=convertView;
        if (view == null) {
            view = inf.inflate(R.layout.computer_layout, parent, false);
        }

        TextView comp_name=view.findViewById(R.id.comp_class);
        TextView comp_number=view.findViewById(R.id.comp_number);
        TextView comp_price=view.findViewById(R.id.comp_price);
        TextView comp_status=view.findViewById(R.id.comp_state);
        ImageView comp_indicator=view.findViewById(R.id.comp_state_indicator);

        comp_name.setText("Standard");
        comp_number.setText("№ "+(position+1));
        comp_price.setText(computers.get(position).getPrice()+" руб/час");
        if(free[position]==1){
            comp_status.setText("Занят");
            comp_indicator.setImageResource(R.drawable.red);
        } else {
            comp_status.setText("Свободен");
            comp_indicator.setImageResource(R.drawable.green);
        }

        return view;
    }
}
