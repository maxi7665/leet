package com.example.myapplication;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

class Pager extends PagerAdapter {

    private List<View> views;
    Context context;
    private String[] titles;

    Pager(List<View> views, Context context, String[] tit) {
        this.views = views;
        this.context = context;
        titles = tit;
    }

    public View getView(int position) {
        return views.get(position);
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView(views.get(position));
    }

    @Override
    public @NonNull
    Object instantiateItem(ViewGroup container, int position) {
        View view = views.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        for (int index = 0; index < getCount(); index++) {
            if (object == views.get(index)) {
                return index;
            }
        }
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position < titles.length)
            return titles[position];
        else return "page";
    }


}