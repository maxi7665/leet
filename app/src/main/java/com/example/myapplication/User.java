package com.example.myapplication;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("Login")
    @Expose
    private String login;
    @SerializedName("Surname")
    @Expose
    private String surname;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("SecondName")
    @Expose
    private String secondName;
    @SerializedName("Number")
    @Expose
    private String number;
    @SerializedName("team")
    @Expose
    private String team;
    @SerializedName("e-mail")
    @Expose
    private String eMail;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getEMail() {
        return eMail;
    }

    public void setEMail(String eMail) {
        this.eMail = eMail;
    }

}