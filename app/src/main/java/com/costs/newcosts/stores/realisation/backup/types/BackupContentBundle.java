package com.costs.newcosts.stores.realisation.backup.types;

import java.io.InputStream;

/**
 * TODO: Add a class header comment
 */
public class BackupContentBundle {
    private InputStream mCostValuesInputStream;
    private InputStream mCostNamesInputStream;


    public BackupContentBundle() {
        mCostValuesInputStream = null;
        mCostNamesInputStream = null;
    }

    public BackupContentBundle(InputStream costValuesInputStream, InputStream costNamesInputStream) {
        this.mCostValuesInputStream = costValuesInputStream;
        this.mCostNamesInputStream = costNamesInputStream;
    }

    public InputStream getCostValuesInputStream() {
        return mCostValuesInputStream;
    }

    public InputStream getCostNamesInputStream() {
        return mCostNamesInputStream;
    }
}
