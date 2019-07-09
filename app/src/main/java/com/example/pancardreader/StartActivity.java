package com.example.pancardreader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import io.card.payment.CardIOActivity;

import static io.card.payment.CardIOActivity.RESULT_SCAN_SUPPRESSED;


/**
 * Created by Onkar Chopade
 */

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MY_SCAN_REQUEST_CODE = 3;
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        findViewById(R.id.scan).setOnClickListener(this);
        findViewById(R.id.qr).setOnClickListener(this);
        qrScan = new IntentIntegrator(this);
        qrScan.setOrientationLocked(true);
    }

    public void onScanPress() {

        Intent scanIntent = new Intent(this, CardIOActivity.class);
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_SCAN, true); // supmit cuando termine de reconocer el documento
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true); // esconder teclado
        scanIntent.putExtra(CardIOActivity.EXTRA_USE_CARDIO_LOGO, true); // cambiar logo de paypal por el de card.io
        scanIntent.putExtra(CardIOActivity.EXTRA_RETURN_CARD_IMAGE, true); // capture img
        scanIntent.putExtra(CardIOActivity.EXTRA_CAPTURED_CARD_IMAGE, true); // capturar img
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan:
                onScanPress(); // Starts camera for image scanning
                break;

            case R.id.qr:
                qrScan.initiateScan(); // starts camera for QR scan
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        PanCard panCard = null;

        if (requestCode == MY_SCAN_REQUEST_CODE || requestCode == RESULT_SCAN_SUPPRESSED) {
            // Image scan result
            Bitmap capturedCardImage = CardIOActivity.getCapturedCardImage(data);
            if (capturedCardImage != null)
                panCard = new ScanOperations().extractPanCardDetails(this, ImageUtils.bitmapToFile(capturedCardImage, this));
//            startNextActivity(bitmapToFile(capturedCardImage));
        } else {

            //  Qr scan result
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        panCard = QrUtils.parseResult(result.getContents());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        if (panCard != null) {
            Log.i("Card data", panCard.toString());
            startDisplayResultActivity(panCard);

        } else
            Toast.makeText(this, "Nothing was Scanned", Toast.LENGTH_SHORT).show();
    }

    private void startDisplayResultActivity(PanCard panCard) {
        Intent i = new Intent(this, PanCardResult.class);
        i.putExtra(PanCard.PAN_CARD_RESUT, panCard);
        startActivity(i);
    }

   /* public void startNextActivity(String filepath) {
        Intent i = new Intent(this, DetailedResultActivity.class);
        i.putExtra("file", filepath);
        startActivity(i);
    }*/


}
