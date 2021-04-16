package com.yunitski.fixedassets;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Environment;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ChangeDialog.Communicator {

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private TextView barcodeText, two, justScanned, count;
    private EditText position;
    private String barcodeData;
    Button apply;
    ImageButton reply;
    Switch mSwitch, pathS;
    int clickCount;
    static int counter;
    ImageButton reNew, more, less;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = findViewById(R.id.surface_view);
        barcodeText = findViewById(R.id.barcode_text);
        apply = findViewById(R.id.apply);
        apply.setOnClickListener(this);
        reply = findViewById(R.id.reply);
        reply.setOnClickListener(this);
        position = findViewById(R.id.position);
        mSwitch = findViewById(R.id.switch1);
        mSwitch.setChecked(true);
        pathS = findViewById(R.id.show_path);
        pathS.setChecked(true);
        barcodeText.setOnClickListener(this);
        two = findViewById(R.id.two);
        two.setOnClickListener(this);
        clickCount = 0;
        justScanned = findViewById(R.id.just_scanned);
        more = findViewById(R.id.more_btn);
        less = findViewById(R.id.less_btn);
        reNew = findViewById(R.id.renew_btn);
        more.setOnClickListener(this);
        less.setOnClickListener(this);
        reNew.setOnClickListener(this);
        count = findViewById(R.id.tv_count);
        counter = 0;
        count.setText(""+ counter);
        initialiseDetectorsAndSources();
    }


    private void initialiseDetectorsAndSources() {

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true)
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {


                    barcodeText.post(new Runnable() {

                        @Override
                        public void run() {
                            cameraSource.stop();
                            barcodeData = barcodes.valueAt(0).displayValue;
                            barcodeText.setText(barcodeData);
                            barcodeText.setTextColor(Color.parseColor("#808080"));
                            if (mSwitch.isChecked()) {
                                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 150);
                                toneGen1.startTone(ToneGenerator.TONE_SUP_PIP, 150);
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        getSupportActionBar().hide();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().hide();
        initialiseDetectorsAndSources();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.apply:
                String s = barcodeData + "\n";
                saveFA(s);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                barcodeText.setTextColor(Color.parseColor("#00ff00"));
                justScanned.setText(barcodeData);
                counter += 1;
                count.setText("" + counter);
                break;
            case R.id.reply:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                try {
                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.barcode_text:
                FragmentManager fragmentManager = getSupportFragmentManager();
                ChangeDialog changeDialog = new ChangeDialog();
                changeDialog.show(fragmentManager, "dialog");
                break;
            case R.id.two:
                clickCount++;
                if (clickCount == 5){
                    startActivity(new Intent(this, MainActivity2.class));
                    clickCount = 0;
                }
                break;
            case R.id.more_btn:
                counter += 1;
                count.setText("" + counter);
                break;
            case R.id.less_btn:
                if (counter > 0){
                    counter -= 1;
                    count.setText("" + counter);
                }
                break;
            case R.id.renew_btn:
                counter = 0;
                count.setText(""+counter);
                break;
        }
    }
private void saveFA(String data){
String fileName = "FixedAssets.txt";
    File filesDir = this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
    if (!filesDir.exists()){
        filesDir.mkdirs();
    }
    File file = null;
    if (position.getText().toString().isEmpty()) {
        file = new File(filesDir, fileName);
    } else {
        fileName = position.getText().toString() + ".txt";
        file = new File(filesDir, fileName);
    }
    try {
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException("Cant able to create file");
            }
        }
        OutputStream os = new FileOutputStream(file, true);
        byte[] dataF = data.getBytes();
        os.write(dataF);
        os.close();
        if (pathS.isChecked()) {
            Toast.makeText(this, barcodeData + " Saved in directory: " + filesDir, Toast.LENGTH_LONG).show();
        }
    } catch (IOException e) {
        e.printStackTrace();
        Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
    }
}

    @Override
    public void onDialogMessage(String message) {
        barcodeText.setText(message);
        barcodeData = message;
    }
}