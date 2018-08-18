package com.amazingteam.competenceproject.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.amazingteam.competenceproject.util.Constants.LINE_SEPARATOR;


public class Cloth {

    private int id;
    private String name;
    private String type;
    private String imagePath;
    private List<Tag> tagList;

    public Cloth() {

    }

    public Cloth(String name, String type, String imagePath, List<Tag> tags) {
        this.type = type;
        this.name = name;
        this.imagePath = imagePath;
        tagList = new ArrayList<>();
        Collections.sort(tags);
        tagList = tags;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public List<Tag> getTagList() {
        return tagList;
    }

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }



    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append(name);
        output.append(System.getProperty(LINE_SEPARATOR));
        output.append(type);
        output.append(System.getProperty(LINE_SEPARATOR));
        output.append(imagePath);
        output.append(System.getProperty(LINE_SEPARATOR));
        output.append(this.id);
        output.append(System.getProperty(LINE_SEPARATOR));
        for (Tag tag : tagList) {
            output.append(tag.getName()).append(" ");
        }
        return output.toString();
    }
}


