package com.amazingteam.competenceproject.model;

public class Weather {
    private int id;
    private String description;
    private String icon;
    private int temperature;
    private double windSpeed;
    private int rainfall;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getRainfall() {
        return rainfall;
    }

    public void setRainfall(int rainfall) {
        this.rainfall = rainfall;
    }
}
