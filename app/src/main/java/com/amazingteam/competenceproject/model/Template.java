package com.amazingteam.competenceproject.model;


public class Template {

    private int id;
    private String name;
    private int photoTemplateID;
    private int templateIconID;

    public Template() {

    }
    public Template(String name, int photoTemplateID,int templateIconID) {
        this.name = name;
        this.photoTemplateID = photoTemplateID;
        this.templateIconID=templateIconID;
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

    public int getPhotoTemplateID() {
        return photoTemplateID;
    }

    public void setPhotoTemplateID(int photoTemplateID) {
        this.photoTemplateID = photoTemplateID;
    }

    public int getTemplateIconID() {
        return templateIconID;
    }

    public void setTemplateIconID(int templateIconID) {
        this.templateIconID = templateIconID;
    }


}
