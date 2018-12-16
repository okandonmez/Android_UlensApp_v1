package com.ulens.ulensapp.SupportClasses;

public class Weather {
    public int icon;
    public String title;
    public String amount="0";
    public String date;
    public Weather(){
        super();
    }

    public Weather(int icon, String title,String amount, String date) {
        super();
        this.icon = icon;
        this.title = title;
        this.amount = amount;
        this.date = date;
    }
}