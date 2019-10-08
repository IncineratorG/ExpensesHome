package com.costs.newcosts.stores.realisation;

import com.costs.newcosts.stores.abstraction.Store;
import com.costs.newcosts.stores.realisation.backup_v2.BackupStore_v2;

/**
 * TODO: Add a class header comment
 */
public class Stores {
    private static Stores mInstance = null;
    private com.costs.newcosts.stores.realisation.backup_v2.BackupStore_v2 mBackupStore_v2;

    public static final int BackupStore_v2 = 2;


    public Stores() {
        mBackupStore_v2 = new BackupStore_v2();
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
            case BackupStore_v2: {
                return mBackupStore_v2;
            }
        }

        return null;
    }
}
