package com.alibaba.excel.model;

import java.util.List;

/**
 * create pengtao
 **/
public class VinReadModel {

    private String mileage;

    private String vin;

    private String fileTime;

    private List<ChargingTime> chargingTime;

    public List<ChargingTime> getChargingTime() {
        return chargingTime;
    }

    public void setChargingTime(List<ChargingTime> chargingTime) {
        this.chargingTime = chargingTime;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getFileTime() {
        return fileTime;
    }

    public void setFileTime(String fileTime) {
        this.fileTime = fileTime;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }
}
