
package com.costs.newcosts;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * TODO: Add a class header comment
 */

public class DataUnitExpenses implements Parcelable {
    private int expenseId_N = -1;
    private int day = -1;
    private int month = -1;
    private int year = -1;
    private long milliseconds = -1;
    private double expenseValueDouble = -1.0d;
    private String expenseValueString = "";
    private String expenseName = "";
    private String expenseNoteString = "";
    public boolean HAS_NOTE = false;

    public DataUnitExpenses() {}
    public DataUnitExpenses(DataUnitExpenses dataUnit) {
        expenseId_N = dataUnit.getExpenseId_N();
        day = dataUnit.getDay();
        month = dataUnit.getMonth();
        year = dataUnit.getYear();
        milliseconds = dataUnit.getMilliseconds();
        expenseValueDouble = dataUnit.getExpenseValueDouble();
        expenseValueString = dataUnit.getExpenseValueString();
        expenseName = dataUnit.getExpenseName();
        expenseNoteString = dataUnit.getExpenseNoteString();
        HAS_NOTE = dataUnit.HAS_NOTE;
    }

    public void setExpenseId_N(int id) { expenseId_N = id; }
    public void setDay(int day) { this.day = day; }
    public void setMonth(int month) { this.month = month; }
    public void setYear(int year) { this.year = year; }
    public void setMilliseconds(long milliseconds) { this.milliseconds = milliseconds; }
    public void setExpenseValueDouble(double value) { expenseValueDouble = value; }
    public void setExpenseValueString(String valueString) {
        if (valueString != null)
            expenseValueString = valueString;
    }
    public void setExpenseNoteString(String note) {
        if (note != null && !"".equals(note)) {
            expenseNoteString = note;
            HAS_NOTE = true;
        } else
            HAS_NOTE = false;
    }
    public void setExpenseName(String name) {
        if (name != null)
            expenseName = name;
    }

    public int getDay() { return day; }
    public int getExpenseId_N() { return expenseId_N; }
    public String getExpenseNoteString() { return expenseNoteString; }
    public double getExpenseValueDouble() { return expenseValueDouble; }
    public String getExpenseValueString() { return expenseValueString; }
    public String getExpenseName() { return expenseName; }
    public long getMilliseconds() { return milliseconds; }
    public int getMonth() { return month; }
    public int getYear() { return year; }





    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.expenseId_N);
        dest.writeInt(this.day);
        dest.writeInt(this.month);
        dest.writeInt(this.year);
        dest.writeLong(this.milliseconds);
        dest.writeDouble(this.expenseValueDouble);
        dest.writeString(this.expenseValueString);
        dest.writeString(this.expenseName);
        dest.writeString(this.expenseNoteString);
        dest.writeByte(this.HAS_NOTE ? (byte) 1 : (byte) 0);
    }

    protected DataUnitExpenses(Parcel in) {
        this.expenseId_N = in.readInt();
        this.day = in.readInt();
        this.month = in.readInt();
        this.year = in.readInt();
        this.milliseconds = in.readLong();
        this.expenseValueDouble = in.readDouble();
        this.expenseValueString = in.readString();
        this.expenseName = in.readString();
        this.expenseNoteString = in.readString();
        this.HAS_NOTE = in.readByte() != 0;
    }

    public static final Creator<DataUnitExpenses> CREATOR = new Creator<DataUnitExpenses>() {
        @Override
        public DataUnitExpenses createFromParcel(Parcel source) {
            return new DataUnitExpenses(source);
        }

        @Override
        public DataUnitExpenses[] newArray(int size) {
            return new DataUnitExpenses[size];
        }
    };
}
