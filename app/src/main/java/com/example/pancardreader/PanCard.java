package com.example.pancardreader;

import android.support.annotation.NonNull;


import java.io.Serializable;

/**
 * Created by Onkar Chopade
 * This class holds the details of scanned pan card
 */

public class PanCard implements Serializable {

    static final String PAN_CARD_RESUT = "pan card result";

    private String name;
    private String fatherName;
    private String dob;
    private String panNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String getFatherName() {
        return fatherName;
    }

    void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    String getDob() {
        return dob;
    }

    void setDob(String dob) {
        this.dob = dob;
    }

    String getPanNumber() {
        return panNumber;
    }

    void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    @NonNull
    @Override
    public String toString() {
        return "Name : " + name + "\n Father name: " + fatherName + "\n Date Of Birth: " + dob + "\n Pan Number: " + panNumber ;
    }
}
