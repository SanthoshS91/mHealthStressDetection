package com.mhealth.heartrate;

/**
 * Created by sant1 on 2/12/2017.
 */

public class HRSData {

    public String timestamp;
    public String heartRate;
    public String sensorType = "Smart Watch";

    public String getTimestamp() {
        return timestamp;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }
}
