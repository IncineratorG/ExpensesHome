package com.costs.newcosts.stores.realisation;

import com.costs.newcosts.stores.abstraction.Store;
import com.costs.newcosts.stores.realisation.backup.BackupStore;

/**
 * TODO: Add a class header comment
 */
public class Stores {
    private static Stores mInstance = null;
    private com.costs.newcosts.stores.realisation.backup.BackupStore mBackupStore;

    public static final int BackupStore = 1;


    public Stores() {
        mBackupStore = new BackupStore();
    }

    public static synchronized Stores getInstance() {
        if (mInstance != null)
            return mInstance;
        else {
            mInstance = new Stores();
            return mInstance;
        }
    }

    public Store getStore(int type) {
        switch (type) {
            case BackupStore: {
                return mBackupStore;
            }
        }

        return null;
    }
}
