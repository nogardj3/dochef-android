package com.yhjoo.dochef.model;

import com.google.gson.annotations.SerializedName;

public class Ingredient {
    @SerializedName("name")
    private String name;
    @SerializedName("amount")
    private String amount;

    public Ingredient(String name, String amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public String getAmount() {
        return amount;
    }
}
