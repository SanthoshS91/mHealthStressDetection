package com.mhealth.heartrate;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mhealth.heartrate.R;

import java.io.IOException;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import java.util.Hashtable;
import java.util.List;

/**
 * Created by sant1 on 2/12/2017.
 */
public class MainActivity extends WearableActivity implements SensorEventListener {

    private TextView mTextView;
    private ImageButton btnStart;
    private ImageButton btnPause;
    private Drawable imgStart;
    private SensorManager mSensorManager;
    private Sensor mHeartRateSensor;
    SensorEventListener sensorEventListener;
    private Hashtable<Integer, String> savedVals = new Hashtable<Integer, String>();
    private HRSData hrsGlobalData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.heartRateText);
                btnStart = (ImageButton) stub.findViewById(R.id.btnStart);
                btnPause = (ImageButton) stub.findViewById(R.id.btnPause);

                btnStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnStart.setVisibility(ImageButton.GONE);
                        btnPause.setVisibility(ImageButton.VISIBLE);
                        mTextView.setText("Please wait...");
                        startMeasure();
                    }
                });

                btnPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnPause.setVisibility(ImageButton.GONE);
                        btnStart.setVisibility(ImageButton.VISIBLE);
                        mTextView.setText("--");
                        stopMeasure();
                    }
                });

            }
        });

        setAmbientEnabled();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

    }

    private void startMeasure() {
        boolean sensorRegistered = mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_FASTEST);
        Log.d("Sensor Status:", " Sensor registered: " + (sensorRegistered ? "yes" : "no"));
    }

    private void stopMeasure() {
        int mean = 0;
        try {
            List<Integer> hrList = SQLiteInterface.getHeartRates(MainActivity.this);
            mean = CalculateAverage(hrList);
        } catch (IOException e) {
            Log.e("HRSActivity", "StressLevels"+e);
        }
        SQLiteInterface.sendDataToBackend(this.getBaseContext());
        if(mean != 0 && mean >= 70){
            Toast t = Toast.makeText(getApplicationContext(), "Stressed, you are! Relax, you must!",Toast.LENGTH_LONG);
            t.show();
        }
        else if(mean != 0 && mean < 70){
            Toast t = Toast.makeText(getApplicationContext(), "You aren't stressed!2",Toast.LENGTH_LONG);
            t.show();
        }
        mSensorManager.unregisterListener(this);
    }

    private int CalculateAverage(List<Integer> Data){
        int sum = 0;
        for( int i = 0; i < Data.size(); i++){
            sum += Data.get(i);
        }
        if(Data.size() != 0)
            return sum / Data.size();
        else
            return -1;
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        float mHeartRateFloat = event.values[0];
        Log.d("MainActivity", "Sensor Name: "+event.sensor.getName());
        int mHeartRate = Math.round(mHeartRateFloat);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

        savedVals.put(mHeartRate,sdf.format(cal.getTime()));
        Log.d("HRvals", "HR: " + mHeartRate +" TimeStamp: "+ sdf.format(cal.getTime()));

        hrsGlobalData = new HRSData();
        hrsGlobalData.setHeartRate(String.valueOf(mHeartRate));
        hrsGlobalData.setTimestamp(sdf.format(cal.getTime()));
        try {
            SQLiteInterface.insertHeartRate(hrsGlobalData, MainActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mTextView.setText(Integer.toString(mHeartRate));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

}
