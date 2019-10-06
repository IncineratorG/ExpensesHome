package com.costs.newcosts.stores.realisation.backup_v2;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.costs.newcosts.services.realisation.backup.BackupService;
import com.costs.newcosts.stores.abstraction.Action;
import com.costs.newcosts.stores.abstraction.ActionsFactory;
import com.costs.newcosts.stores.abstraction.State;
import com.costs.newcosts.stores.abstraction.Store;
import com.costs.newcosts.stores.common.Payload;
import com.costs.newcosts.stores.realisation.backup_v2.types.DriveServiceBundle;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.api.services.drive.Drive;

/**
 * TODO: Add a class header comment
 */
public class BackupStore_v2 extends Store {
    private static final String TAG = "tag";

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

    }
}
