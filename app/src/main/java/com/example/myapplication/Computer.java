package com.example.myapplication;

public class Computer {

    private int idComputer;

    private String monitor;

    private String graphicsCard;

    private String mouse;

    private String keyboard;

    private String headphones;

    private String ram;

    private String processor;

    private String price;


    public void Computer(String monitor){
        this.monitor=monitor;
        idComputer=-1;
        graphicsCard="";
        mouse="";
        keyboard="";
        headphones="";
        ram="";
        processor="";
        price="";


    }

    public void Computer(){

    }


    public int getIdComputer() {
        return idComputer;
    }


    public void setIdComputer(int idComputer) {
        this.idComputer = idComputer;
    }


    public String getMonitor() {
        return monitor;
    }


    public void setMonitor(String monitor) {
        this.monitor = monitor;
    }


    public String getGraphicsCard() {
        return graphicsCard;
    }


    public void setGraphicsCard(String graphicsCard) {
        this.graphicsCard = graphicsCard;
    }


    public String getMouse() {
        return mouse;
    }


    public void setMouse(String mouse) {
        this.mouse = mouse;
    }


    public String getKeyboard() {
        return keyboard;
    }


    public void setKeyboard(String keyboard) {
        this.keyboard = keyboard;
    }


    public String getHeadphones() {
        return headphones;
    }


    public void setHeadphones(String headphones) {
        this.headphones = headphones;
    }


    public String getram() {
        return ram;
    }


    public void setRAM(String ram) {
        this.ram = ram;
    }


    public String getProcessor() {
        return processor;
    }


    public void setProcessor(String processor) {
        this.processor = processor;
    }


    public String getPrice() {
        return price;
    }


    public void setPrice(String price) {
        this.price = price;
    }
}
