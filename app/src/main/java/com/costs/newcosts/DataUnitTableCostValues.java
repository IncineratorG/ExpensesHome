package com.costs.newcosts;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * TODO: Add a class header comment
 */

public class DataUnitTableCostValues implements Parcelable {
    private int ID_C = -1;
    private int ID_N_FK = -1;
    private int DAY = -1;
    private int MONTH = -1;
    private int YEAR = -1;
    private long DATE_IN_MILLISECONDS = -1L;
    private double COST_VALUE = -1D;
    private String TEXT = "";


    public double get_COST_VALUE() {
        return COST_VALUE;
    }

    public long get_DATE_IN_MILLISECONDS() {
        return DATE_IN_MILLISECONDS;
    }

    public int get_DAY() {
        return DAY;
    }

    public int get_ID_C() {
        return ID_C;
    }

    public int get_ID_N_FK() {
        return ID_N_FK;
    }

    public int get_MONTH() {
        return MONTH;
    }

    public String get_TEXT() {
        return TEXT;
    }

    public int get_YEAR() {
        return YEAR;
    }


    public void set_COST_VALUE(double COST_VALUE) {
        this.COST_VALUE = COST_VALUE;
    }

    public void set_DATE_IN_MILLISECONDS(long DATE_IN_MILLISECONDS) {
        this.DATE_IN_MILLISECONDS = DATE_IN_MILLISECONDS;
    }

    public void set_DAY(int DAY) {
        this.DAY = DAY;
    }

    public void set_ID_C(int ID_C) {
        this.ID_C = ID_C;
    }

    public void set_ID_N_FK(int ID_N_FK) {
        this.ID_N_FK = ID_N_FK;
    }

    public void set_MONTH(int MONTH) {
        this.MONTH = MONTH;
    }

    public void set_TEXT(String TEXT) {
        if (TEXT != null) {
            this.TEXT = TEXT;
        } else {
            this.TEXT = "";
        }
    }

    public void set_YEAR(int YEAR) {
        this.YEAR = YEAR;
    }





    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.ID_C);
        dest.writeInt(this.ID_N_FK);
        dest.writeInt(this.DAY);
        dest.writeInt(this.MONTH);
        dest.writeInt(this.YEAR);
        dest.writeLong(this.DATE_IN_MILLISECONDS);
        dest.writeDouble(this.COST_VALUE);
        dest.writeString(this.TEXT);
    }

    public DataUnitTableCostValues() {
    }

    protected DataUnitTableCostValues(Parcel in) {
        this.ID_C = in.readInt();
        this.ID_N_FK = in.readInt();
        this.DAY = in.readInt();
        this.MONTH = in.readInt();
        this.YEAR = in.readInt();
        this.DATE_IN_MILLISECONDS = in.readLong();
        this.COST_VALUE = in.readDouble();
        this.TEXT = in.readString();
    }

    public static final Creator<DataUnitTableCostValues> CREATOR = new Creator<DataUnitTableCostValues>() {
        @Override
        public DataUnitTableCostValues createFromParcel(Parcel source) {
            return new DataUnitTableCostValues(source);
        }

        @Override
        public DataUnitTableCostValues[] newArray(int size) {
            return new DataUnitTableCostValues[size];
        }
    };
}
