package com.costs.newcosts.stores.realisation.backup;

import com.costs.newcosts.stores.abstraction.Action;
import com.costs.newcosts.stores.abstraction.ActionsFactory;
import com.costs.newcosts.stores.realisation.backup.actions.BuildGoogleDriveServiceAction;
import com.costs.newcosts.stores.realisation.backup.actions.CheckInternetConnectionAction;
import com.costs.newcosts.stores.realisation.backup.actions.ClearStoreAction;
import com.costs.newcosts.stores.realisation.backup.actions.GetBackupListAction;
import com.costs.newcosts.stores.realisation.backup.actions.GetRootFolderAction;
import com.costs.newcosts.stores.realisation.backup.actions.SetSignInAction;

/**
 * TODO: Add a class header comment
 */
public class BackupActionsFactory implements ActionsFactory {
    public static final int GetBackupList = 1;
    public static final int GetRootFolder = 2;
    public static final int CheckInternetConnection = 3;
    public static final int BuildGoogleDriveServiceAction = 4;
    public static final int SetSignInAction = 5;
    public static final int ClearStoreAction = 6;


    @Override
    public Action getAction(int type) {
        switch (type) {
            case GetBackupList:
                return new GetBackupListAction();

            case GetRootFolder:
                return  new GetRootFolderAction();

            case CheckInternetConnection:
                return new CheckInternetConnectionAction();

            case BuildGoogleDriveServiceAction:
                return new BuildGoogleDriveServiceAction();

            case SetSignInAction:
                return new SetSignInAction();

            case ClearStoreAction:
                return new ClearStoreAction();
        }

        return null;
    }
}
