package com.costs.newcosts;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * TODO: Add a class header comment
 */

public class DataUnitSms {

    private String smsAddress = "";
    private String smsBody = "";
    private String smsDateInMillisString = "";
    private Long smsDateLong = -1L;
    private int day = -1;
    private int month = -1;
    private int year = -1;



    public String getSmsAddress() {
        return smsAddress;
    }

    public String getSmsBody() {
        return smsBody;
    }

    public String getSmsDateInMillisString() {
        return smsDateInMillisString;
    }

    public long getSmsDateInMillisLong() { return smsDateLong; }

    public int getSmsDay() { return day; }

    public int getSmsMonth() { return month; }

    public int getSmsYear() { return year; }



    public void setSmsDateInMillis(String smsDateInMillisString) {
        this.smsDateInMillisString = smsDateInMillisString;
        try {
            smsDateLong = Long.parseLong(smsDateInMillisString);
        } catch (NumberFormatException e) {
            System.out.println("ERROR_PARSING_LONG_IN: DataUnitSMS");
            e.printStackTrace();
        }

        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(smsDateLong);

        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
    }

    public void setSmsAddress(String smsAddress) {
        this.smsAddress = smsAddress;
    }

    public void setSmsBody(String smsBody) {
        this.smsBody = smsBody;
    }
}
