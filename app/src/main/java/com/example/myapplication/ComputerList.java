package com.example.myapplication;

import java.util.List;

public class ComputerList {

    List<Computer> json;

    public List<Computer> getList(){
        return json;
    }

    public void setList(List<Computer> list){
        this.json=list;
    }
}
