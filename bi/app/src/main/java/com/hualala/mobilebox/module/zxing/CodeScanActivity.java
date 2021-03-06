package com.hualala.mobilebox.module.zxing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.android.Intents;
import com.hualala.mobilebox.R;
import com.hualala.ui.widget.CommonHeader;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class CodeScanActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener {

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private CommonHeader commonHeader;

    private boolean isOpen = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_code_scan);

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.setTorchListener(this);

        commonHeader = findViewById(R.id.commonHeader);
        if (FlashCompact.hasFlash(this)) {
            commonHeader.getRightButton().setVisibility(View.VISIBLE);
            commonHeader.getRightButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    v.setEnabled(false);

                    if (!isOpen) {
                        isOpen = true;
                        commonHeader.getRightButton().setText("关灯");
                        barcodeScannerView.setTorchOn();
                    } else {
                        isOpen = false;
                        commonHeader.getRightButton().setText("开灯");
                        barcodeScannerView.setTorchOff();
                    }

                    v.setEnabled(true);
                }
            });
        } else {
            commonHeader.getRightButton().setVisibility(View.INVISIBLE);
        }

        capture = new CaptureManager(this, barcodeScannerView);

        //指定扫猫条形码
        Intent intent = getIntent();
        intent.setAction(Intents.Scan.ACTION);
        intent.putExtra(Intents.Scan.FORMATS, BarcodeFormat.QR_CODE.name());
        intent.putExtra(Intents.Scan.MODE, Intents.Scan.QR_CODE_MODE);

        capture.initializeFromIntent(intent, savedInstanceState);
        capture.decode();

    }


    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onTorchOn() {

    }

    @Override
    public void onTorchOff() {


    }


}
