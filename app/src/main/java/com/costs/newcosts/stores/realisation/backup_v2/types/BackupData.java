package com.costs.newcosts.stores.realisation.backup_v2.types;

import com.costs.newcosts.activities.backup.DataUnitBackupFolder;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add a class header comment
 */
public class BackupData {
    private String mRootFolderId;
    private List<DataUnitBackupFolder> mDeviceBackupFolders;


    public BackupData() {
        mRootFolderId = "";
        mDeviceBackupFolders = new ArrayList<>();
    }

    public BackupData(String rootFolderId, List<DataUnitBackupFolder> deviceBackupFolders) {
        mRootFolderId = rootFolderId;
        mDeviceBackupFolders = deviceBackupFolders;
    }

    public String getRootFolderId() {
        return mRootFolderId;
    }

    public List<DataUnitBackupFolder> getDeviceBackupFolders() {
        return mDeviceBackupFolders;
    }
}
