package com.example.myapplication;

import java.util.List;

public class Reservation_codeList {
    List<Reservation_code> json;

    List<Reservation_code> getList(){
        return json;
    }

    void setList(List<Reservation_code> a){
        json=a;
    }
}
