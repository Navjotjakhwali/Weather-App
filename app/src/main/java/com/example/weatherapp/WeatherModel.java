package com.example.weatherapp;

public class WeatherModel {
    private String time;
    private String temperatue;
    private String icon;
    private String windspeed;

    public WeatherModel(String time, String temperatue, String icon, String windspeed) {
        this.time = time;
        this.temperatue = temperatue;
        this.icon = icon;
        this.windspeed = windspeed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemperatue() {
        return temperatue;
    }

    public void setTemperatue(String temperatue) {
        this.temperatue = temperatue;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getWindspeed() {
        return windspeed;
    }

    public void setWindspeed(String windspeed) {
        this.windspeed = windspeed;
    }
}
