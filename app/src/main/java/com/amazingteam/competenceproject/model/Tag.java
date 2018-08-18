package com.amazingteam.competenceproject.model;


public class Tag implements Comparable<Tag> {

    private int id;
    private String name;
    private int weight;
    private String category;

    public Tag() {

    }

    public Tag(String name, int weight, String category) {
        this.name = name;
        this.weight = weight;
        this.category = category;
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

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public int compareTo(Tag o) {
        return o.getWeight() - this.getWeight();
    }

}


