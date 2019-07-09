package com.example.pancardreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


/**
 * Created by Onkar Chopade
 * This activity displays the result of scan card
 */

public class PanCardResult extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pan_card_result);
        PanCard panCard = (PanCard) getIntent().getSerializableExtra(PanCard.PAN_CARD_RESUT);
        if(panCard !=null){
            ((TextView) findViewById(R.id.txt_name)).setText(panCard.getName());
            ((TextView) findViewById(R.id.txt_father_name)).setText(panCard.getFatherName());
            ((TextView) findViewById(R.id.txt_dob)).setText(panCard.getDob());
            ((TextView) findViewById(R.id.txt_pan)).setText(panCard.getPanNumber());
        }
    }
}
