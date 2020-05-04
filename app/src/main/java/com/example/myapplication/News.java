package com.example.myapplication;

import java.sql.Time;
import java.util.Date;

public class News {
    private int idNews;
    private String Header;
    private String Content;
    private Date Date;
    private String Time;
    private int idAdmin;

    public String getHeader(){
        return Header;
    }

    public String getContent(){
        return Content;
    }


    public int getIdNews() {
        return idNews;
    }

    public void setIdNews(int idNews) {
        this.idNews = idNews;
    }
}
