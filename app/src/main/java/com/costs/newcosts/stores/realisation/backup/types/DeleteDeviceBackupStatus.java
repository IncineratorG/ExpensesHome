package com.costs.newcosts.stores.realisation.backup.types;

/**
 * TODO: Add a class header comment
 */
public class DeleteDeviceBackupStatus {
    private String mStatus;

    public static final String InProgress = "in_progress";
    public static final String Complete = "complete";
    public static final String NotComplete = "not_complete";

    public DeleteDeviceBackupStatus(String s) {
        this.mStatus = s;
    }

    public String getStatus() {
        return mStatus;
    }
}