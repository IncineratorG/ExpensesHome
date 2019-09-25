package com.costs.newcosts.stores.realisation.backup.types;

import java.io.InputStream;

/**
 * TODO: Add a class header comment
 */
public class BackupContentBundle {
    private String mStatus;
    private InputStream mCostValuesInputStream;
    private InputStream mCostNamesInputStream;

    public static final String NotSet = "not_set";
    public static final String Setting = "setting";
    public static final String Set = "set";


    public BackupContentBundle() {
        mCostValuesInputStream = null;
        mCostNamesInputStream = null;
        mStatus = NotSet;
    }

    public BackupContentBundle(InputStream costValuesInputStream, InputStream costNamesInputStream, String status) {
        this.mCostValuesInputStream = costValuesInputStream;
        this.mCostNamesInputStream = costNamesInputStream;
        mStatus = status;
    }

    public InputStream getCostValuesInputStream() {
        return mCostValuesInputStream;
    }

    public InputStream getCostNamesInputStream() {
        return mCostNamesInputStream;
    }

    public String getContentStatus() {
        return mStatus;
    }
}
