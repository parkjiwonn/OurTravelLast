package com.example.ourtravel.Map.Model;

public class ListClass {

    private String description;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "{" +

                "description=" + description +


                '}';
    }
}
