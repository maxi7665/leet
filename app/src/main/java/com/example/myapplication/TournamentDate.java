package com.example.myapplication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TournamentDate {

    @SerializedName("idTournament")
    @Expose
    private String idTournament;
    @SerializedName("Date")
    @Expose
    private String date;
    @SerializedName("Time")
    @Expose
    private String time;
    @SerializedName("GameName")
    @Expose
    private String gameName;
    @SerializedName("TournamentName")
    @Expose
    private String tournamentName;
    @SerializedName("Type")
    @Expose
    private String type;
    @SerializedName("max_players")
    @Expose
    private String maxPlayers;
    @SerializedName("accepted")
    @Expose
    private String accepted;
    @SerializedName("accept_flag")
    @Expose
    private String acceptFlag;
    @SerializedName("description")
    @Expose
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public String getHTime(){
        if(time.length() == 8){
            return time.substring(0,5);
        }
        else return time;
    }

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

    public String getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(String maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getAccepted() {
        return accepted;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }

    public String getAcceptFlag() {
        return acceptFlag;
    }

    public void setAcceptFlag(String acceptFlag) {
        this.acceptFlag = acceptFlag;
    }

}
