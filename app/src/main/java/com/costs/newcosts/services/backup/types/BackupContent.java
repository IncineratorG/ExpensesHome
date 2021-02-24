package com.costs.newcosts.services.backup.types;

import java.io.InputStream;

/**
 * TODO: Add a class header comment
 */
public class BackupContent {
    private InputStream mCostValuesInputStream;
    private InputStream mCostNamesInputStream;


    public BackupContent() {
        mCostValuesInputStream = null;
        mCostNamesInputStream = null;
    }

    public void setCostValuesInputStream(InputStream stream) {
        mCostValuesInputStream = stream;
    }

    public void setCostNamesInputStream(InputStream stream) {
        mCostNamesInputStream = stream;
    }

    public InputStream getCostValuesInputStream() {
        return mCostValuesInputStream;
    }

    public InputStream getCostNamesInputStream() {
        return mCostNamesInputStream;
    }
}
