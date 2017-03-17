package com.mhealth.heartrate;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by sant1 on 2/13/2017.
 */

public class HRSSQLiteHelper extends SQLiteOpenHelper {

    /**
     * Inner class for table contents
     */
    public class TABLEDATA implements BaseColumns {
        public static final String TABLE_NAME = "sd_proj";
        public static final String COLUMN_NAME_SENSOR_TYPE = "SensorType";
        public static final String COLUMN_NAME_HEART_RATE = "HeartRateRR";
        public static final String COLUMN_NAME_TIME = "timestamp";
    }

    private static final String TAG = "HRSSQLiteHelper";

    public static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "energy.db";

    // SQL instructions for creation and deletion of the table
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLEDATA.TABLE_NAME + " (" +
                    TABLEDATA.COLUMN_NAME_SENSOR_TYPE + " TEXT, " +
                    TABLEDATA.COLUMN_NAME_HEART_RATE + " int," +
                    TABLEDATA.COLUMN_NAME_TIME + " datetime)";

    /*private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE sd_proj(SensorType TEXT, HeartRateRR int, timestamp text)";
*/
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLEDATA.TABLE_NAME;

    /**
     * Maintains the instance for the singleton model
     */
    private static HRSSQLiteHelper instance = null;

    private HRSSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Returns the current instance of the DBHelper
     *
     * @param context of the app
     * @return the helper
     */
    public static HRSSQLiteHelper getInstance(Context context) {
        if (instance == null) instance = new HRSSQLiteHelper(context);
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Log.d(TAG, "SQL_CREATE_ENTRIES"+SQL_CREATE_ENTRIES)
        //sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static void insertHeartRate(HRSData hrsData, Context context) {
        SQLiteDatabase sqlDb = getInstance(context).getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TABLEDATA.COLUMN_NAME_SENSOR_TYPE, hrsData.getSensorType());
        values.put(TABLEDATA.COLUMN_NAME_HEART_RATE, hrsData.getHeartRate());
        values.put(TABLEDATA.COLUMN_NAME_TIME, hrsData.getTimestamp());

        // Insert the new row, returning the primary key value of the new row
        //String insert = "insert into sd_proj values('Bio-Stamp', '62', '2015-02-11 03:19:12')";
        long newRowId = sqlDb.insert(TABLEDATA.TABLE_NAME, null, values);
        //sqlDb.execSQL(insert);
        Log.d(TAG, "DataInserted Successfully " + newRowId);
    }
}