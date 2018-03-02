package com.costs.newcosts;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * TODO: Add a class header comment
 */

public class DataUnitTableCostNames implements Parcelable {
    private int ID_N = -1;
    private String COST_NAME = "";
    private int IS_ACTIVE = -1;

    public String get_COST_NAME() {
        return COST_NAME;
    }

    public void set_COST_NAME(String COST_NAME) {
        if (COST_NAME != null)
            this.COST_NAME = COST_NAME;
        else
            this.COST_NAME = "";
    }

    public int get_ID_N() {
        return ID_N;
    }

    public void set_ID_N(int ID_N) {
        this.ID_N = ID_N;
    }

    public int get_IS_ACTIVE() {
        return IS_ACTIVE;
    }

    public void set_IS_ACTIVE(int IS_ACTIVE) {
        this.IS_ACTIVE = IS_ACTIVE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.ID_N);
        dest.writeString(this.COST_NAME);
        dest.writeInt(this.IS_ACTIVE);
    }

    public DataUnitTableCostNames() {
    }

    protected DataUnitTableCostNames(Parcel in) {
        this.ID_N = in.readInt();
        this.COST_NAME = in.readString();
        this.IS_ACTIVE = in.readInt();
    }

    public static final Creator<DataUnitTableCostNames> CREATOR = new Creator<DataUnitTableCostNames>() {
        @Override
        public DataUnitTableCostNames createFromParcel(Parcel source) {
            return new DataUnitTableCostNames(source);
        }

        @Override
        public DataUnitTableCostNames[] newArray(int size) {
            return new DataUnitTableCostNames[size];
        }
    };
}
