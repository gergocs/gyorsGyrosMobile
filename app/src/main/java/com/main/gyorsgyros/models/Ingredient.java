package com.main.gyorsgyros.models;

public class Ingredient {
    private String name;
    private boolean spicy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSpicy() {
        return spicy;
    }

    public void setSpicy(boolean spicy) {
        this.spicy = spicy;
    }

    public Ingredient(String name, boolean spicy) {
        this.name = name;
        this.spicy = spicy;
    }
}
