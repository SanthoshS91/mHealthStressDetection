package com.mhealth.heartrate;

import android.content.Context;
import android.database.Cursor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sant1 on 2/12/2017.
 */

public class SQLiteInterface {
    private static final String TAG = "SQLiteInterface";

    public static void insertHeartRate(HRSData hrsData, Context context)
            throws IOException {
        HRSSQLiteHelper.insertHeartRate(hrsData, context);
    }
    /**
     * Send data to the back end.
     *
     * @param context of the application
     */
    public static void sendDataToBackend(Context context) {
        new HRSServerHelper(context).execute();
    }

    public static List<Integer> getHeartRates(Context context)
            throws IOException {
        List<Integer> hrList = new ArrayList<Integer>();
        Cursor c = HRSSQLiteHelper.getHREntries(context);
        c.moveToFirst();
        int heartRate = c.getColumnIndex(HRSSQLiteHelper.TABLEDATA.COLUMN_NAME_HEART_RATE);
        while(!c.isAfterLast()) {
            hrList.add(Integer.valueOf(c.getString(heartRate)));
            c.moveToNext();
        }

        return hrList;
    }
}
