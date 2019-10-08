package com.costs.newcosts.stores.realisation.backup_v2;

import com.costs.newcosts.stores.abstraction.Action;
import com.costs.newcosts.stores.abstraction.ActionsFactory;
import com.costs.newcosts.stores.realisation.backup_v2.actions.BuildGoogleDriveServiceAction;
import com.costs.newcosts.stores.realisation.backup_v2.actions.CheckInternetConnectionAction;
import com.costs.newcosts.stores.realisation.backup_v2.actions.ClearStoreAction;
import com.costs.newcosts.stores.realisation.backup_v2.actions.CreateDeviceBackupAction;
import com.costs.newcosts.stores.realisation.backup_v2.actions.DeleteDeviceBackupAction;
import com.costs.newcosts.stores.realisation.backup_v2.actions.GetBackupDataAction;
import com.costs.newcosts.stores.realisation.backup_v2.actions.RestoreFromBackupAction;
import com.costs.newcosts.stores.realisation.backup_v2.actions.SetBackupDataAction;
import com.costs.newcosts.stores.realisation.backup_v2.actions.SetCreateDeviceBackupStatusAction;
import com.costs.newcosts.stores.realisation.backup_v2.actions.SetDriveServiceBundleAction;
import com.costs.newcosts.stores.realisation.backup_v2.actions.SetGoogleSignInClientAction;
import com.costs.newcosts.stores.realisation.backup_v2.actions.SetRestoreStatusAction;
import com.costs.newcosts.stores.realisation.backup_v2.actions.SetSignInAction;
import com.costs.newcosts.stores.realisation.backup_v2.actions.StopCurrentAsyncTaskAction;

/**
 * TODO: Add a class header comment
 */
public class BackupActionsFactory_v2 implements ActionsFactory {
    public static final int CheckInternetConnection = 1;
    public static final int SetGoogleSignInClient = 2;
    public static final int SetSignIn = 3;
    public static final int BuildGoogleDriveService = 4;
    public static final int SetDriveServiceBundle = 5;
    public static final int ClearStore = 6;
    public static final int GetBackupData = 7;
    public static final int SetBackupData = 8;
    public static final int StopCurrentAsyncTask = 9;
    public static final int RestoreFromBackup = 10;
    public static final int SetRestoreStatus = 11;
    public static final int CreateDeviceBackup = 12;
    public static final int SetCreateDeviceBackupStatus = 13;
    public static final int DeleteDeviceBackup = 14;


    @Override
    public Action getAction(int type) {
        switch (type) {
            case CheckInternetConnection: {
                return new CheckInternetConnectionAction();
            }

            case SetGoogleSignInClient: {
                return new SetGoogleSignInClientAction();
            }

            case SetSignIn: {
                return new SetSignInAction();
            }

            case BuildGoogleDriveService: {
                return new BuildGoogleDriveServiceAction();
            }

            case SetDriveServiceBundle: {
                return new SetDriveServiceBundleAction();
            }

            case ClearStore: {
                return new ClearStoreAction();
            }

            case GetBackupData: {
                return new GetBackupDataAction();
            }

            case SetBackupData: {
                return new SetBackupDataAction();
            }

            case StopCurrentAsyncTask: {
                return new StopCurrentAsyncTaskAction();
            }

            case RestoreFromBackup: {
                return new RestoreFromBackupAction();
            }

            case SetRestoreStatus: {
                return new SetRestoreStatusAction();
            }

            case CreateDeviceBackup: {
                return new CreateDeviceBackupAction();
            }

            case SetCreateDeviceBackupStatus: {
                return new SetCreateDeviceBackupStatusAction();
            }

            case DeleteDeviceBackup: {
                return new DeleteDeviceBackupAction();
            }
        }

        return null;
    }
}
