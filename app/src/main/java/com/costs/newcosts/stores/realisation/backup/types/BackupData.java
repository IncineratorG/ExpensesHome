package com.costs.newcosts.stores.realisation.backup.types;

import com.costs.newcosts.activities.backup.DataUnitBackupFolder;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add a class header comment
 */
public class BackupData {
    private String mStatus;
    private String mRootFolderId;
    private List<DataUnitBackupFolder> mDeviceBackupFolders;

    public static final String NotSet = "not_set";
    public static final String Setting = "setting";
    public static final String Set = "set";


    public BackupData() {
        mRootFolderId = "";
        mDeviceBackupFolders = new ArrayList<>();
        mStatus = NotSet;
    }

    public BackupData(String rootFolderId, List<DataUnitBackupFolder> deviceBackupFolders, String status) {
        mRootFolderId = rootFolderId;
        if (mRootFolderId == null) {
            mRootFolderId = "";
        }
        mDeviceBackupFolders = deviceBackupFolders;
        if (mDeviceBackupFolders == null) {
            mDeviceBackupFolders = new ArrayList<>();
        }
        mStatus = status;
    }

    public String getRootFolderId() {
        return mRootFolderId;
    }

    public List<DataUnitBackupFolder> getDeviceBackupFolders() {
        return mDeviceBackupFolders;
    }

    public String getBackupDataStatus() {
        return mStatus;
    }
}
