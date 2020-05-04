package com.example.myapplication;

import java.util.List;

public class ReservationList {
    List<Reservation> json;

    List<Reservation> getList(){
        return json;
    }

    void setList(List<Reservation> a){
        json=a;
    }
}
