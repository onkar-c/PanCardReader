package com.example.pancardreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Onkar Chopade
 */

class ScanOperations {
    private static final int TOP_PER = 22;
    private static final int RIGHT_PER = 30;
    private static final int BOTTOM_PER = 22;
    private static final int NAME_PER = 22;
    private static final int FATHER_NAME_PER = 30;
    private static final int DOB_PER = 50;

    private static final String TAG = "Text recognizer";
    private TextRecognizer textRecognizer;
    private Bitmap bitmap;
    private PanCard panCard;


    PanCard extractPanCardDetails(Context context, String filePath) {
        bitmap = BitmapFactory.decodeFile(filePath);
        textRecognizer = new TextRecognizer.Builder(context).build();
        this.panCard = new PanCard();
        convertBitmapToGray();
        cropTop();
        detectName();
        detectFatherName();
        detectDOB();
        detectPanNumber();
        return this.panCard;
    }


    /**
     * Converts color bitmap to gray bitmap
     */
    private void convertBitmapToGray(){
        Bitmap bmpGrayscale = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bitmap, 0, 0, paint);
    }


    /**
     * Removes top , bottom and right portion of the card image which is not required for text recognition
     * Top portion contains header like (Income Tax Department).
     * Bottom portion contains signature.
     * Right portion contains image of the pan card holder.
     */
    private void cropTop() {
        int y = (TOP_PER * bitmap.getHeight()) / 100;
        int width = ((100 - RIGHT_PER) * bitmap.getWidth()) / 100;
        int bottom = (BOTTOM_PER * bitmap.getHeight()) / 100;
        bitmap = Bitmap.createBitmap(bitmap, 0, y, width, (bitmap.getHeight() - y) - bottom);
    }

    /**
     * Crops the name portion from the image and is sent to text recognizer.
     * Eliminates name portion from actual image.
     */
    private void detectName() {
        int height = (NAME_PER * bitmap.getHeight()) / 100;
        Bitmap nameBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), height);
        panCard.setName(getTextFromImage(nameBitmap));
        bitmap = Bitmap.createBitmap(bitmap, 0, height, bitmap.getWidth(), bitmap.getHeight() - height);
    }


    /**
     * Crops the father name portion from the image and is sent to text recognizer.
     * Eliminates father name portion from actual image.
     */
    private void detectFatherName() {
        int height = (FATHER_NAME_PER * bitmap.getHeight()) / 100;
        Bitmap fatherNameBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), height);
        panCard.setFatherName(getTextFromImage(fatherNameBitmap));
        bitmap = Bitmap.createBitmap(bitmap, 0, height, bitmap.getWidth(), bitmap.getHeight() - height);
    }


    /**
     * Crops the date of birth portion from the image and is sent to text recognizer.
     * Using regular expression date of birt is extracted.
     * Eliminates ate of birth name portion from actual image.
     */
    private void detectDOB() {
        int height = (DOB_PER * bitmap.getHeight()) / 100;
        Bitmap dateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), height);
        panCard.setDob(getDate(getTextFromImage(dateBitmap).trim()));
        bitmap = Bitmap.createBitmap(bitmap, 0, height, bitmap.getWidth(), bitmap.getHeight() - height);
    }


    /**
     * Remaining Pan number portion is sent to text recognizer
     * Using regular expression Pan number is extracted from detected string
     */
    private void detectPanNumber() {
        panCard.setPanNumber(getPanNumber(getTextFromImage(bitmap)).trim());
    }


    private String getTextFromImage(Bitmap bitmap) {
        if (bitmap != null) {
            if (!textRecognizer.isOperational())
                Log.w(TAG, "Detector dependencies are not yet available.");

            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> origTextBlocks = textRecognizer.detect(frame);
            List<TextBlock> textBlocks = new ArrayList<>();
            for (int i = 0; i < origTextBlocks.size(); i++) {
                TextBlock textBlock = origTextBlocks.valueAt(i);
                textBlocks.add(textBlock);
            }

            StringBuilder detectedText = new StringBuilder();
            for (TextBlock textBlock : textBlocks) {
                if (textBlock.getValue() != null)
                    detectedText.append(textBlock.getValue()).append("\n");
            }
            return detectedText.toString();
        }
        return "";
    }

    private String getDate(String detectedText) {
        String tempText = detectedText;
        Pattern pattern = Pattern.compile("^[0-3]?[0-9]/[0-3]?[0-9]/(?:[0-9]{2})?[0-9]{2}$");
        tempText = tempText.replaceAll("\n", " ");
        String[] array = tempText.split("\\s+");
        for (String s : array) {
            Matcher matcher = pattern.matcher(s);
            if (matcher.matches()) {
                return s;
            }
        }
        return "";
    }

    private String getPanNumber(String detectedText) {
        Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]");
        String[] array = detectedText.split("\\s+");
        for (String s : array) {
            Matcher matcher = pattern.matcher(s);
            if (matcher.matches()) {
                return s;
            }
        }
        return detectedText;
    }
}
