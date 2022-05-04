package com.main.gyorsgyros.models;

import com.google.type.DateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class Order {
    private String address;
    private String date;
    private String items;
    private String payment;
    private String phone;
    private int price;
    private String uid;

    public Order(String address, String date, String items, String payment, String phone, int price, String uid) {
        this.address = address;
        this.date = date;
        this.items = items;
        this.payment = payment;
        this.phone = phone;
        this.price = price;
        this.uid = uid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
