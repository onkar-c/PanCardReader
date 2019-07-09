package com.example.pancardreader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Onkar Chopade
 * This Activity is used to see actual crop that takes place in whole process.
 * It displays all cropped image
 */


public class DetailedResultActivity extends AppCompatActivity {

    public static final int TOP_PER = 22;
    public static final int RIGHT_PER = 30;
    public static final int BOTTOM_PER = 22;
    public static final int NAME_PER = 22;
    public static final int FATHER_NAME_PER = 30;
    public static final int DOB_PER = 50;

    private static final String TAG = "Text recognizer";
    private TextRecognizer textRecognizer;
    TextView mTextView;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_result);
        textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        mTextView = findViewById(R.id.name);
        bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("file"));
        convertBitmapToGray();
        cropTop();
        ((ImageView) findViewById(R.id.image)).setImageBitmap(bitmap);
        detectName();
        detectFatherName();
        detectDOB();
        detectPanNumber();
    }

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

    private void cropTop() {
        int y = (TOP_PER * bitmap.getHeight()) / 100;
        int width = ((100 - RIGHT_PER) * bitmap.getWidth()) / 100;
        int bottom = (BOTTOM_PER * bitmap.getHeight()) / 100;
        bitmap = Bitmap.createBitmap(bitmap, 0, y, width, (bitmap.getHeight() - y) - bottom);
    }

    private  void detectName(){
        int height = (NAME_PER * bitmap.getHeight()) / 100;
        Bitmap nameBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), height);
        ((ImageView) findViewById(R.id.image1)).setImageBitmap(nameBitmap);
        String s = getTextFromImage(nameBitmap);
        ((TextView) findViewById(R.id.name)).setText(s.trim());
        bitmap = Bitmap.createBitmap(bitmap, 0, height, bitmap.getWidth(), bitmap.getHeight() - height);
    }

    private  void detectFatherName(){
        int height = (FATHER_NAME_PER * bitmap.getHeight()) / 100;
        Bitmap nameBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), height);
        ((ImageView) findViewById(R.id.image2)).setImageBitmap(nameBitmap);
        String s = getTextFromImage(nameBitmap);
        ((TextView) findViewById(R.id.father_name)).setText(s.trim());
        bitmap = Bitmap.createBitmap(bitmap, 0, height, bitmap.getWidth(), bitmap.getHeight() - height);
    }

    private  void detectDOB(){
        int height = (DOB_PER * bitmap.getHeight()) / 100;
        Bitmap nameBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), height);
        ((ImageView) findViewById(R.id.image3)).setImageBitmap(nameBitmap);
        String s = getTextFromImage(nameBitmap);
        ((TextView) findViewById(R.id.dob)).setText(getDate(s).trim());
        bitmap = Bitmap.createBitmap(bitmap, 0, height, bitmap.getWidth(), bitmap.getHeight() - height);
    }

    private  void detectPanNumber(){
        String s = getTextFromImage(bitmap);
        ((ImageView) findViewById(R.id.image4)).setImageBitmap(bitmap);
        ((TextView) findViewById(R.id.pan_number)).setText(getPanNumber(s).trim());
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

    public String getDate(String detectedText) {
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

    public String getPanNumber(String detectedText) {
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
