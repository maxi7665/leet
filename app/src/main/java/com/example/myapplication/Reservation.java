package com.example.myapplication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;

public class Reservation {
    @SerializedName("idReservation")
    @Expose
    private String idReservation;
    @SerializedName("idComputer")
    @Expose
    private int idComputer;
    @SerializedName("DateReservation")
    @Expose
    private String dateReservation;
    @SerializedName("idUser")
    @Expose
    private String idUser;
    @SerializedName("Time")
    @Expose
    private String time;
    @SerializedName("WhenRes")
    @Expose
    private String whenRes;





    public String getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(String idReservation) {
        this.idReservation = idReservation;
    }

    public int getIdComputer() {
        return idComputer;
    }

    public void setIdComputer(int idComputer) {
        this.idComputer = idComputer;
    }

    public String getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(String dateReservation) {
        this.dateReservation = dateReservation;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWhenRes() {
        return whenRes;
    }

    public void setWhenRes(String whenRes) {
        this.whenRes = whenRes;
    }

    public void setNull(){
        idReservation="0";
        idComputer=0;
        dateReservation="2000-01-01 00:00";
        idUser="-5";
        time="0";
        whenRes="2000-01-01 00:00";

    }
}
