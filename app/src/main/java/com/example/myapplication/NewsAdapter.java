package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewsAdapter extends BaseAdapter {

    ArrayList<News> list;
    Context context;
    LayoutInflater inf;


    NewsAdapter(ArrayList<News> a, Context cont){
        list=(ArrayList<News>)a;
        context=cont;//getting activity context
        inf=(LayoutInflater) cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//get service for creating views from xml
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
        return list.get(position).getIdNews();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=convertView;
        if (view == null) {
            view = inf.inflate(R.layout.news_layout, parent, false);
        }

        TextView title=view.findViewById(R.id.titletext);
        TextView content=view.findViewById(R.id.contenttext);

        title.setText(list.get(position).getHeader());
        content.setText(list.get(position).getContent());

        CircleImageView icon=view.findViewById(R.id.newsicon);
        //icon.setBackgroundColor(view.getResources().getColor(R.color.reserved));



        return view;
    }
}
