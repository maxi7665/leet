package com.example.myapplication;

import java.util.List;

public class NewsList {
    List<News> json;

    public List<News> getList(){
        return json;
    }

    public void setList(List<News> a){
        json=a;
    }
}
