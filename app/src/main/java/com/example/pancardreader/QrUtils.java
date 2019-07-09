package com.example.pancardreader;


/**
 * Created by Onkar Chopade
 */
class QrUtils {

    static PanCard parseResult(String res) {

        PanCard panCard = new PanCard();

        res = res.replaceAll("[^\\x00-\\x7F]", "");
        res = res.replace(" / ", "");
        res = res.trim();
        String[] values = res.split("\n");
        for (String value : values) {
            String[] keyValue = value.split(":");
            if (keyValue.length > 0)
                setResult(keyValue[0].trim(), keyValue[1], panCard);
        }
        return panCard;
    }

    private static void setResult(String type, String result, PanCard panCard) {
        switch (type) {
            case "Name":
                panCard.setName(result.trim());
                break;
            case "Father's Name":
            case "Fathers Name":
                panCard.setFatherName(result.trim());
                break;
            case "Date of Birth":
                panCard.setDob(result.trim());
                break;
            case "PAN":
                panCard.setPanNumber(result.trim());
                break;
        }
    }
}
