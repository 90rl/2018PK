package com.amazingteam.competenceproject.model;


import java.util.ArrayList;
import java.util.List;

public class Meeting {
    private int id;
    private String name;
    private List<Tag> tagList;
    private Boolean weatherDependant;
    private List<String> clothesSet;

    public Meeting() {
    }

    public Meeting(String name, List<Tag> tags, Boolean weatherDependant, List<String> clothesSet) {
        this.name = name;
        tagList = new ArrayList<>();
        tagList = tags;
        this.clothesSet = new ArrayList<>();
        this.clothesSet = clothesSet;
        this.weatherDependant = weatherDependant;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Tag> getTagList() {
        return tagList;
    }

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }

    public void setWeatherDependant(Boolean weatherDependant) {
        this.weatherDependant = weatherDependant;
    }

    public void setClothesSet(List<String> clothesSet) {
        this.clothesSet = clothesSet;
    }

    public Boolean getWeatherDependant() {
        return weatherDependant;
    }

    public List<String> getClothesSet() {
        return clothesSet;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(name);
        return output.toString();
    }
}


