package com.costs.newcosts.stores.realisation.backup;

import com.costs.newcosts.stores.abstraction.Action;
import com.costs.newcosts.stores.abstraction.ActionsFactory;
import com.costs.newcosts.stores.realisation.backup.actions.BuildGoogleDriveServiceAction;
import com.costs.newcosts.stores.realisation.backup.actions.CheckInternetConnectionAction;
import com.costs.newcosts.stores.realisation.backup.actions.ClearStoreAction;
import com.costs.newcosts.stores.realisation.backup.actions.GetBackupFolderContentAction;
import com.costs.newcosts.stores.realisation.backup.actions.GetBackupListAction;
import com.costs.newcosts.stores.realisation.backup.actions.GetRootFolderAction;
import com.costs.newcosts.stores.realisation.backup.actions.RestoreDbFromBackupAction;
import com.costs.newcosts.stores.realisation.backup.actions.SetBackupFilesListAction;
import com.costs.newcosts.stores.realisation.backup.actions.SetBackupFolderContentAction;
import com.costs.newcosts.stores.realisation.backup.actions.SetDriveServiceBundleAction;
import com.costs.newcosts.stores.realisation.backup.actions.SetRestoreStatusAction;
import com.costs.newcosts.stores.realisation.backup.actions.SetRootFolderIdAction;
import com.costs.newcosts.stores.realisation.backup.actions.SetSignInAction;
import com.costs.newcosts.stores.realisation.backup.actions.StopAsyncTaskAction;

/**
 * TODO: Add a class header comment
 */
public class BackupActionsFactory implements ActionsFactory {
    public static final int GetBackupList = 1;
    public static final int GetRootFolder = 2;
    public static final int CheckInternetConnection = 3;
    public static final int BuildGoogleDriveService = 4;
    public static final int SetSignIn = 5;
    public static final int ClearStore = 6;
    public static final int SetBackupFilesList = 7;
    public static final int SetRootFolderId = 8;
    public static final int SetDriveServiceBundle = 9;
    public static final int GetBackupFolderContent = 10;
    public static final int SetBackupFolderContent = 11;
    public static final int RestoreDbFromBackup = 12;
    public static final int SetRestoreStatus = 13;
    public static final int StopAsyncTask = 14;


    @Override
    public Action getAction(int type) {
        switch (type) {
            case GetBackupList: {
                return new GetBackupListAction();
            }

            case GetRootFolder: {
                return new GetRootFolderAction();
            }

            case CheckInternetConnection: {
                return new CheckInternetConnectionAction();
            }

            case BuildGoogleDriveService: {
                return new BuildGoogleDriveServiceAction();
            }

            case SetSignIn: {
                return new SetSignInAction();
            }

            case ClearStore: {
                return new ClearStoreAction();
            }

            case SetBackupFilesList: {
                return new SetBackupFilesListAction();
            }

            case SetRootFolderId: {
                return new SetRootFolderIdAction();
            }

            case SetDriveServiceBundle: {
                return new SetDriveServiceBundleAction();
            }

            case GetBackupFolderContent: {
                return new GetBackupFolderContentAction();
            }

            case SetBackupFolderContent: {
                return new SetBackupFolderContentAction();
            }

            case RestoreDbFromBackup: {
                return new RestoreDbFromBackupAction();
            }

            case SetRestoreStatus: {
                return new SetRestoreStatusAction();
            }

            case StopAsyncTask: {
                return new StopAsyncTaskAction();
            }
        }

        return null;
    }
}
