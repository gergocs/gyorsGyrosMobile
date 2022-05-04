package com.main.gyorsgyros.models;

import java.util.ArrayList;

public class Food {
    private String name;
    private long price;
    private String image;
    private ArrayList<Ingredient> ingredients;

    public Food(String name, Long price, String image, ArrayList<Ingredient> ingredients) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.ingredients = ingredients;
    }

    public void addIngredient(Ingredient ingredient){
        ingredients.add(ingredient);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
