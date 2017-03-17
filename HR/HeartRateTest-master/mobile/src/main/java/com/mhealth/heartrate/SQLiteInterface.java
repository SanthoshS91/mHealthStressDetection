package com.mhealth.heartrate;

import android.content.Context;

import java.io.IOException;

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
}
