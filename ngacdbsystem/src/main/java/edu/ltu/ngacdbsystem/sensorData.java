package edu.ltu.ngacdbsystem;

import java.io.Serializable;

public class sensorData implements Serializable {

	//=================================================================================================
	// members

	private static final long serialVersionUID = -8371510478751740542L;
	
	private int data;
    private String SensorType;
    private String SensorTag;
	private long timeStamp;

    public sensorData() {    }

    public sensorData(int data, String sensorType, String Tag, long timeStamp) {
        this.data = data;
        SensorType = sensorType;
        this.SensorTag = Tag;
        this.timeStamp = timeStamp;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public String getSensorType() {
        return SensorType;
    }

    public void setSensorType(String sensorType) {
        SensorType = sensorType;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getTag(){
        return SensorTag;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

	
}
