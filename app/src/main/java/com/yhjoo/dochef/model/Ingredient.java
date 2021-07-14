package com.yhjoo.dochef.model;

import com.google.gson.annotations.SerializedName;

public class Ingredient {
    @SerializedName("name")
    private String name;
    @SerializedName("amount")
    private int amount;
    @SerializedName("units")
    private String units;

    public Ingredient(String name, int amount, String units) {
        this.name = name;
        this.amount = amount;
        this.units = units;
    }

    public Ingredient() {
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public String getUnits() {
        return units;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "name='" + name + '\'' +
                ", amount=" + amount +
                ", units='" + units + '\'' +
                '}';
    }
}
