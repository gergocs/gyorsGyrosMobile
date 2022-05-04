package com.main.gyorsgyros.models;

import java.util.ArrayList;

public class Cart {
    private ArrayList<String> items;
    private String uid;

    public Cart() {
        this.items = new ArrayList<>();
        this.uid = "";
    }

    public Cart(ArrayList<String> items) {
        this.items = items;
        this.uid = "";
    }

    public Cart(ArrayList<String> items, String uid) {
        this.items = items;
        this.uid = uid;
    }

    public ArrayList<String> getItems() {
        return items;
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void addItem(String item){
        this.items.add(item);
    }
}
