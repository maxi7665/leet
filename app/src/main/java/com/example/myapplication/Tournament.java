package com.example.myapplication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tournament {
    @SerializedName("idTournament")
    @Expose
    private String idTournament="-1";
    @SerializedName("Date")
    @Expose
    private String date="";
    @SerializedName("Time")
    @Expose
    private String time="";
    @SerializedName("GameName")
    @Expose
    private String gameName="";
    @SerializedName("TournamentName")
    @Expose
    private String tournamentName="";
    @SerializedName("Type")
    @Expose
    private String type="";

    Tournament(String name){
        tournamentName=name;
    }

    Tournament(String name, String game){
        tournamentName=name;
        gameName=game;
    }

    Tournament(){}

    public String getIdTournament() {
        return idTournament;
    }

    public void setIdTournament(String idTournament) {
        this.idTournament = idTournament;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        if(time.length()==8)
        return time.substring(0,5);
        else
            return time;
    }

    public String getFullTime(){
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
