package com.mahmoudjoe3.tracker.pojo;

public class Expense {
    private Integer id;
    private int amount;
    private float price;
    private String description;
    private String date;
    private String time;
    private String cat;

    public Expense(Integer id, int amount, float price, String description, String date, String time, String cat) {
        this.id = id;
        this.amount = amount;
        this.price = price;
        this.description = description;
        this.date = date;
        this.time = time;
        this.cat = cat;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }
}
