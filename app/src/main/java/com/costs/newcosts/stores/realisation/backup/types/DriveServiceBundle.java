package com.costs.newcosts.stores.realisation.backup.types;

import com.google.api.services.drive.Drive;

/**
 * TODO: Add a class header comment
 */
public class DriveServiceBundle {
    private String mStatus;
    private Drive mDriveService;

    public static final String NotSet = "not_set";
    public static final String Setting = "setting";
    public static final String Set = "set";


    public DriveServiceBundle() {
        mDriveService = null;
        mStatus = NotSet;
    }

    public DriveServiceBundle(Drive driveService, String status) {
        mDriveService = driveService;
        mStatus = status;
    }

    public Drive getDriveService() {
        return mDriveService;
    }

    public String getDriveServiceStatus() {
        return mStatus;
    }
}
