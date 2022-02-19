package com.appynitty.swachbharatabhiyanlibrary.pojos;


/**
 * Created by Swapnil Lanjewar on 08/01/2022.
 */

public class EmpOfflineCollectionCount {

    String houseCount;
    String dumpYardCount;
    String streetSweepCount;
    String liquidWasteCount;
    String date;

    public EmpOfflineCollectionCount(String houseCount, String dumpYardCount, String streetSweepCount, String liquidWasteCount, String date) {
        this.houseCount = houseCount;
        this.dumpYardCount = dumpYardCount;
        this.streetSweepCount = streetSweepCount;
        this.liquidWasteCount = liquidWasteCount;
        this.date = date;
    }


    public String getHouseCount() {
        return houseCount;
    }

    public void setHouseCount(String houseCount) {
        this.houseCount = houseCount;
    }

    public String getDumpYardCount() {
        return dumpYardCount;
    }

    public void setDumpYardCount(String dumpYardCount) {
        this.dumpYardCount = dumpYardCount;
    }

    public String getStreetSweepCount() {
        return streetSweepCount;
    }

    public void setStreetSweepCount(String streetSweepCount) {
        this.streetSweepCount = streetSweepCount;
    }

    public String getLiquidWasteCount() {
        return liquidWasteCount;
    }

    public void setLiquidWasteCount(String liquidWasteCount) {
        this.liquidWasteCount = liquidWasteCount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
