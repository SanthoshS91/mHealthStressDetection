package com.mhealth.heartrate;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by sant1 on 2/13/2017.
 */

public class HRSServerHelper extends AsyncTask<Void, Void, Object> {

    private Context context;
    private static final String TAG = "HRSServerHelper";

    public HRSServerHelper(Context context){
        this.context = context;
    }

    @Override
    /**
     * When execute() is called, this happens first
     */
    protected void onPreExecute() {

    }
    /**
     * When execute() is called, this happens second
     */
    @Override
    protected Void doInBackground(Void... params) {
        BufferedWriter writer = null;
        BufferedReader reader;
        Cursor c = HRSSQLiteHelper.getHREntries(context);

        int sensorType = c.getColumnIndex(HRSSQLiteHelper.TABLEDATA.COLUMN_NAME_SENSOR_TYPE);
        int heartRate = c.getColumnIndex(HRSSQLiteHelper.TABLEDATA.COLUMN_NAME_HEART_RATE);
        int timeStamp = c.getColumnIndex(HRSSQLiteHelper.TABLEDATA.COLUMN_NAME_TIME);
        c.moveToFirst();
        try {
            while(!c.isAfterLast()) {
                URL url = new URL("http://murphy.wot.eecs.northwestern.edu/~ssl4520/SQLGateway.py");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                //urlConnection.connect();

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("sensorType", c.getString(sensorType))
                        .appendQueryParameter("heartRateRR", c.getString(heartRate))
                        .appendQueryParameter("timestamp", c.getString(timeStamp));
                String query = builder.build().getEncodedQuery();
                Log.d(TAG, "Input Query : " + query);
                OutputStream os = urlConnection.getOutputStream();
                writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                urlConnection.connect();
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String decodedString;
                while ((decodedString = reader.readLine()) != null) {
                    Log.d(TAG, "Cursor : " + decodedString);
                }
                c.moveToNext();
            }
            Log.d(TAG, "Insertion to murphy db is complete ");
        } catch (MalformedURLException ex){
            Log.e(TAG, "MalformedURLException while  making http post url connection");
            ex.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "IO exception while  making http post url connection");
            e.printStackTrace();
        }catch (Exception e) {
            Log.e(TAG, "Exception while  making http post url connection");
            e.printStackTrace();
        } finally{
        } {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    /**
     * When execute is called, this happens third
     */
    protected void onPostExecute(Object result) {
        HRSSQLiteHelper.deleteHREntries(context);
    }
}

