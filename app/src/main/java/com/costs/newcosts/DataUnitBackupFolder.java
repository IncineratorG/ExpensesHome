package com.costs.newcosts;

/**
 * TODO: Add a class header comment
 */

public class DataUnitBackupFolder {

    private String driveId;
    private String title;
    public boolean STATUS_OK = false;

    private String deviceManufacturer = "";
    private String deviceModel = "";
    private String deviceID = "";
    private String comment = "";
    private int day = -1;
    private int month = -1;
    private int year = -1;
    private long milliseconds;
    public boolean HAS_COMMENT = false;


    DataUnitBackupFolder() {
    }


    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }
    public void setTitle(String title) {
        this.title = title;

        String[] titleContentArray = title.split(Constants.BACKUP_FOLDER_NAME_DELIMITER);
        if (titleContentArray.length > 0)
            setDeviceManufacturer(titleContentArray[0]);
        if (titleContentArray.length > 1)
            setDeviceModel(titleContentArray[1]);
        if (titleContentArray.length > 2)
            setDeviceID(titleContentArray[2]);
        if (titleContentArray.length > 3) {
            String dateString = titleContentArray[3];
            String[] dateArray = dateString.split("\\.");
            setDay(Integer.valueOf(dateArray[0]));
            setMonth(Integer.valueOf(dateArray[1]));
            setYear(Integer.valueOf(dateArray[2]));
        }
        if (titleContentArray.length > 4)
            setMilliseconds(Long.parseLong(titleContentArray[4]));
        if (titleContentArray.length > 5) {
            if (titleContentArray[5] != null && titleContentArray[5] != "") {
                setComment(titleContentArray[5]);
                HAS_COMMENT = true;
            } else
                HAS_COMMENT = false;
        } else
            HAS_COMMENT = false;
    }

    private void setComment(String comment) {
        if (comment != null && !comment.equals("")) {
            this.comment = comment;
            HAS_COMMENT = true;
        } else
            HAS_COMMENT = false;
    }
    private void setDeviceID(String deviceID) {
        if (deviceID != null)
            this.deviceID = deviceID;
    }
    private void setDeviceManufacturer(String deviceManufacturer) {
        if (deviceManufacturer != null)
            this.deviceManufacturer = deviceManufacturer;
    }
    private void setDeviceModel(String deviceModel) {
        if (deviceModel != null)
            this.deviceModel = deviceModel;
    }
    public void setMilliseconds(long milliseconds) {
        this.milliseconds = milliseconds;
    }
    private void setDay(int day) {
        this.day = day;
    }
    private void setMonth(int month) {
        this.month = month;
    }
    private void setYear(int year) {
        this.year = year;
    }


    public String getDriveId() {
        return driveId;
    }
    public String getTitle() {
        return title;
    }

    public String getComment() {
        return comment;
    }
    public String getDeviceID() {
        return deviceID;
    }
    public String getDeviceManufacturer() {
        return deviceManufacturer;
    }
    public String getDeviceModel() {
        return deviceModel;
    }
    public long getMilliseconds() {
        return milliseconds;
    }
    public int getDay() {
        return day;
    }
    public int getMonth() {
        return month;
    }
    public int getYear() {
        return year;
    }
}
