package com.example.projem;

import java.util.ArrayList;

public class Foods {
    private String name;
    private String id;
    private String serving_unit;
    private String serving_qty;
    private String img;
    private String carb;
    private String gram;

    public Foods(String name, String carb, String serving_unit, String serving_qty, String img,String gram) {
        this.name = name;
        this.carb = carb;
        this.serving_unit = serving_unit;
        this.serving_qty = serving_qty;
        this.img = img;
        this.gram=gram;
    }

    public String getGram() {
        return gram;
    }

    public void setGram(String gram) {
        this.gram = gram;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServing_unit() {
        return serving_unit;
    }

    public void setServing_unit(String serving_unit) {
        this.serving_unit = serving_unit;
    }

    public String getServing_qty() {
        return serving_qty;
    }

    public void setServing_qty(String serving_qty) {
        this.serving_qty = serving_qty;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getCarb() {
        return carb;
    }

    public void setCarb(String carb) {
        this.carb = carb;
    }
}
