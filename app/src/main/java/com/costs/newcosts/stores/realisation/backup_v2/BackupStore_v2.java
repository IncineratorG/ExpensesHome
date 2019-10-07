package com.costs.newcosts.stores.realisation.backup_v2;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.costs.newcosts.DB_Costs;
import com.costs.newcosts.activities.backup.ActivityBackupData;
import com.costs.newcosts.activities.backup.DataUnitBackupFolder;
import com.costs.newcosts.services.realisation.backup.BackupService;
import com.costs.newcosts.stores.abstraction.Action;
import com.costs.newcosts.stores.abstraction.ActionsFactory;
import com.costs.newcosts.stores.abstraction.State;
import com.costs.newcosts.stores.abstraction.Store;
import com.costs.newcosts.stores.common.Payload;
import com.costs.newcosts.stores.realisation.backup_v2.types.BackupData;
import com.costs.newcosts.stores.realisation.backup_v2.types.DriveServiceBundle;
import com.costs.newcosts.stores.realisation.backup_v2.types.RestoreStatus;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add a class header comment
 */
public class BackupStore_v2 extends Store {
    private static final String TAG = "tag";

    private static final String CLASS_NAME = "BackupStore_v2";

    private BackupState_v2 mState;
    private BackupActionsFactory_v2 mActionsFactory;

    private BackupService mBackupService;


    public BackupStore_v2() {
        mState = new BackupState_v2();
        mActionsFactory = new BackupActionsFactory_v2();
        mBackupService = new BackupService();
    }

    @Override
    public State getState() {
        return mState;
    }

    @Override
    public ActionsFactory getActionFactory() {
        return mActionsFactory;
    }

    @Override
    protected void reduce(Action action) {
        switch (action.getType()) {
            case BackupActionsFactory_v2.ClearStore: {
                clearStoreReducer(action);
                break;
            }

            case BackupActionsFactory_v2.CheckInternetConnection: {
                checkInternetConnectionReducer(action);
                break;
            }

            case BackupActionsFactory_v2.SetSignIn: {
                mState.signedIn.set(true);
                break;
            }

            case BackupActionsFactory_v2.SetGoogleSignInClient: {
                setGoogleSignInClientReducer(action);
                break;
            }

            case BackupActionsFactory_v2.SetDriveServiceBundle: {
                setDriveServiceBundleReducer(action);
                break;
            }

            case BackupActionsFactory_v2.SetBackupData: {
                setBackupDataReducer(action);
                break;
            }

            case BackupActionsFactory_v2.StopCurrentAsyncTask: {
                stopCurrentAsyncTaskReducer();
                break;
            }

            case BackupActionsFactory_v2.SetRestoreStatus: {
                setRestoreStatusReducer(action);
                break;
            }
        }
    }

    @Override
    protected void effect(Action action) {
        switch (action.getType()) {
            case BackupActionsFactory_v2.BuildGoogleDriveService: {
                buildGoogleDriveServiceEffect(action);
                break;
            }

            case BackupActionsFactory_v2.GetBackupData: {
                getBackupDataEffect(action);
                break;
            }

            case BackupActionsFactory_v2.RestoreFromBackup: {
                restoreFromBackupEffect(action);
                break;
            }
        }
    }


    private void clearStoreReducer(Action action) {
        mState.driveServiceBundle.set(new DriveServiceBundle(null, DriveServiceBundle.NotSet));
        mState.googleSignInClient.set(null);
        mState.signedIn.set(false);
        mState.hasInternetConnection.set(false);
    }

    private void checkInternetConnectionReducer(Action action) {
        if (!(action.getPayload() instanceof Payload)) {
            Log.d(TAG, "BackupStore_v2.CheckInternetConnection->BAD_PAYLOAD");
            return;
        }

        Payload payload = (Payload) action.getPayload();
        Context context = null;
        if (payload.get("context") instanceof Context) {
            context = (Context) payload.get("context");
        } else {
            Log.d(TAG, "BackupStore_v2.CheckInternetConnection->BAD_PAYLOAD_DATA");
            return;
        }

        mState.hasInternetConnection.set(mBackupService.hasInternetConnection(context));
    }

    private void setGoogleSignInClientReducer(Action action) {
        if (!(action.getPayload() instanceof Payload)) {
            Log.d(TAG, "BackupStore_v2.SetGoogleSignInClient->BAD_PAYLOAD");
            return;
        }

        Payload payload = (Payload) action.getPayload();
        GoogleSignInClient client = null;
        if (payload.get("googleSignInClient") instanceof GoogleSignInClient) {
            client = (GoogleSignInClient) payload.get("googleSignInClient");
        } else {
            Log.d(TAG, "BackupStore_v2.SetGoogleSignInClient->BAD_PAYLOAD_DATA");
            return;
        }

        mState.googleSignInClient.set(client);
    }

    private void setDriveServiceBundleReducer(Action action) {
        if (!(action.getPayload() instanceof DriveServiceBundle)) {
            Log.d(TAG, "BackupStore_v2.SetDriveServiceBundle->BAD_PAYLOAD");
            return;
        }

        DriveServiceBundle driveServiceBundle = (DriveServiceBundle) action.getPayload();

        mState.driveServiceBundle.set(driveServiceBundle);
    }

    private void setBackupDataReducer(Action action) {
        if (!(action.getPayload() instanceof Payload)) {
            Log.d(TAG, "BackupStore_v2.setBackupDataReducer()->BAD_PAYLOAD");
            return;
        }

        Payload payload = (Payload) action.getPayload();
        BackupData backupData = null;
        if (payload.get("backupData") instanceof BackupData) {
            backupData = (BackupData) payload.get("backupData");
        } else {
            Log.d(TAG, "BackupStore_v2.setBackupDataReducer()->BAD_PAYLOAD_DATA");
            return;
        }

        mState.backupData.set(backupData);
    }

    private void stopCurrentAsyncTaskReducer() {
        int currentTaskType = mBackupService.stopCurrentTask();
    }

    private void setRestoreStatusReducer(Action action) {
        if (!(action.getPayload() instanceof Payload)) {
            Log.d(TAG, "BackupStore_v2.setRestoreStatusReducer()->BAD_PAYLOAD");
            return;
        }

        Payload payload = (Payload) action.getPayload();
        RestoreStatus restoreStatus = null;
        if (payload.get("restoreStatus") instanceof RestoreStatus) {
            restoreStatus = (RestoreStatus) payload.get("restoreStatus");
        } else {
            Log.d(TAG, "BackupStore_v2.setRestoreStatusReducer()->BAD_PAYLOAD_DATA");
            return;
        }

        mState.restoreStatus.set(restoreStatus);
    }


    private void buildGoogleDriveServiceEffect(Action action) {
        if (!(action.getPayload() instanceof Payload)) {
            Log.d(TAG, "BackupStore_v2.BuildGoogleDriveService->BAD_PAYLOAD");
            return;
        }

        Payload payload = (Payload) action.getPayload();
        Intent intent = null;
        Context context = null;
        String appName = null;
        if (payload.get("result_intent") instanceof Intent) {
            intent = (Intent) payload.get("result_intent");
        } else {
            return;
        }
        if (payload.get("context") instanceof Context) {
            context = (Context) payload.get("context");
        } else {
            return;
        }
        if (payload.get("appLabel") instanceof String) {
            appName = (String) payload.get("appLabel");
        } else {
            return;
        }

        DriveServiceBundle driveServiceBundle = new DriveServiceBundle(null, DriveServiceBundle.Setting);

        Action setDriveServiceBundleAction = mActionsFactory.getAction(BackupActionsFactory_v2.SetDriveServiceBundle);
        setDriveServiceBundleAction.setPayload(driveServiceBundle);

        dispatch(setDriveServiceBundleAction);

        Context finalContext = context;
        String finalAppName = appName;
        mBackupService.getSignInAccount(intent,
                (googleAccount -> {
                    Log.d(TAG, "Signed in as " + googleAccount.getEmail());

                    Drive driveService = mBackupService.getGoogleDriveService(googleAccount, finalContext, finalAppName);
                    DriveServiceBundle finalDriveServiceBundle = new DriveServiceBundle(driveService, DriveServiceBundle.Set);
                    setDriveServiceBundleAction.setPayload(finalDriveServiceBundle);

                    dispatch(setDriveServiceBundleAction);
                }),
                (exception -> {
                    Log.e(TAG, "Unable to sign in.", exception);

                    DriveServiceBundle finalDriveServiceBundle = new DriveServiceBundle(null, DriveServiceBundle.NotSet);
                    setDriveServiceBundleAction.setPayload(finalDriveServiceBundle);

                    dispatch(setDriveServiceBundleAction);
                }));
    }

    private void getBackupDataEffect(Action action) {
        if (!(action.getPayload() instanceof Payload)) {
            Log.d(TAG, "BackupStore_v2.getBackupDataEffect()->BAD_PAYLOAD");
            return;
        }

        Payload payload = (Payload) action.getPayload();
        Drive googleDriveService = null;
        if (payload.get("googleDriveService") instanceof Drive) {
            googleDriveService = (Drive) payload.get("googleDriveService");
        } else {
            Log.d(TAG, "BackupStore_v2.getBackupDataEffect()->BAD_PAYLOAD_DATA");
            return;
        }

        Payload setBackupDataPayload = new Payload();
        setBackupDataPayload.set("backupData", new BackupData(null, null, BackupData.Setting));

        Action setBackupData = mActionsFactory.getAction(BackupActionsFactory_v2.SetBackupData);
        setBackupData.setPayload(setBackupDataPayload);

        dispatch(setBackupData);

        mBackupService.getBackupData(googleDriveService, (String rootFolderId, FileList files) -> {
            List<DataUnitBackupFolder> backupFilesList = new ArrayList<>();
            if (files != null && files.getFiles() != null) {
                for (File file : files.getFiles()) {
                    DataUnitBackupFolder backupTitle = new DataUnitBackupFolder();
                    backupTitle.setTitle(file.getName());
                    backupTitle.setDriveId(file.getId());

                    backupFilesList.add(backupTitle);
                }
            }

            setBackupDataPayload.set("backupData", new BackupData(rootFolderId, backupFilesList, BackupData.Set));

            setBackupData.setPayload(setBackupDataPayload);

            dispatch(setBackupData);
        });
    }

    private void restoreFromBackupEffect(Action action) {
        if (!(action.getPayload() instanceof Payload)) {
            Log.d(TAG, "BackupStore_v2.restoreFromBackupEffect()->BAD_PAYLOAD");
            return;
        }

        Payload payload = (Payload) action.getPayload();
        Drive googleDriveService = null;
        String backupFolderId = null;
        DB_Costs costsDb = null;
        if (payload.get("googleDriveService") instanceof Drive) {
            googleDriveService = (Drive) payload.get("googleDriveService");
        } else {
            Log.d(TAG, "BackupStore_v2.restoreFromBackupEffect()->BAD_PAYLOAD_DATA");
            return;
        }
        if (payload.get("backupFolderId") instanceof String) {
            backupFolderId = (String) payload.get("backupFolderId");
        } else {
            Log.d(TAG, "BackupStore_v2.restoreFromBackupEffect()->BAD_PAYLOAD_DATA");
            return;
        }
        if (payload.get("costsDb") instanceof DB_Costs) {
            costsDb = (DB_Costs) payload.get("costsDb");
        } else {
            Log.d(TAG, "BackupStore_v2.restoreFromBackupEffect()->BAD_PAYLOAD_DATA");
            return;
        }

        mBackupService.restoreDataBaseFromBackup(googleDriveService, backupFolderId, costsDb, (progress) -> {
//            Log.d(TAG, "BackupStore_v2.restoreFromBackupEffect()->PROGRESS: " + progress);

            Action setRestoreStatus = mActionsFactory.getAction(BackupActionsFactory_v2.SetRestoreStatus);

            Payload restoreStatusPayload = new Payload();
            restoreStatusPayload.set("restoreStatus", new RestoreStatus(progress));

            setRestoreStatus.setPayload(restoreStatusPayload);

            dispatch(setRestoreStatus);
        });
    }
}
