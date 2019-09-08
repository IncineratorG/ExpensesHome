package com.costs.newcosts.stores.realisation.backup;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.costs.newcosts.services.realisation.backup.BackupService;
import com.costs.newcosts.stores.abstraction.Action;
import com.costs.newcosts.stores.abstraction.ActionsFactory;
import com.costs.newcosts.stores.abstraction.Payload;
import com.costs.newcosts.stores.abstraction.State;
import com.costs.newcosts.stores.abstraction.Store;
import com.google.api.services.drive.Drive;

/**
 * TODO: Add a class header comment
 */
public class BackupStore extends Store {
    private static final String TAG = "tag";

    private BackupState mState;
    private BackupActionsFactory mActionsFactory;

    private BackupService mBackupService;


    public BackupStore() {
        mState = new BackupState();
        mState.googleDriveServiceSet.set(false);
        mState.googleDriveServiceStatus.set("not_set");
        mState.signedIn.set(false);
        mState.hasInternetConnection.set(false);
        mState.rootFolderId.set("");

        mActionsFactory = new BackupActionsFactory();

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
            case BackupActionsFactory.CheckInternetConnection: {
                if (!(action.getPayload() instanceof Payload)) {
                    Log.d(TAG, "BackupActionsFactory.CheckInternetConnection->BAD_PAYLOAD");
                    break;
                }

                Payload payload = (Payload) action.getPayload();
                Context context = null;
                if (payload.get("context") instanceof Context) {
                    context = (Context) payload.get("context");
                } else {
                    break;
                }

                mState.hasInternetConnection.set(mBackupService.hasInternetConnection(context));

                break;
            }

            case BackupActionsFactory.SetSignInAction: {
                mState.signedIn.set(true);

                break;
            }

            case BackupActionsFactory.ClearStoreAction: {
                mState.googleDriveServiceSet.set(false);
                mState.googleDriveServiceStatus.set("not_set");
                mState.signedIn.set(false);
                mState.hasInternetConnection.set(false);
                mState.rootFolderId.set("");

                break;
            }
        }
    }

    @Override
    protected void effect(Action action) {
        switch (action.getType()) {
            case BackupActionsFactory.GetBackupList: {
                if (!(action.getPayload() instanceof Payload)) {
                    Log.d(TAG, "BackupActionsFactory.GetBackupList->BAD_PAYLOAD");
                    break;
                }

                Payload payload = (Payload) action.getPayload();
                Drive googleDriveService = null;
                String rootFolderId = null;

                if (payload.get("googleDriveService") instanceof Drive) {
                    googleDriveService = (Drive) payload.get("googleDriveService");
                } else {
                    break;
                }
                if (payload.get("rootFolderId") instanceof String) {
                    rootFolderId = (String) payload.get("rootFolderId");
                } else {
                    break;
                }

                mBackupService.getBackupList(googleDriveService, rootFolderId, (fileList) -> {
                    if (fileList != null) {
                        mState.backupFilesList.set(fileList);
                    }
                });

                break;
            }

            case BackupActionsFactory.GetRootFolder: {
                if (!(action.getPayload() instanceof Drive)) {
                    Log.d(TAG, "BackupActionsFactory.GetRootFolder->BAD_PAYLOAD");
                    break;
                }

                Drive googleDriveService = (Drive) action.getPayload();

                mBackupService.getRootFolder(googleDriveService, (folderId) -> {
                    if (folderId == null) {
                        mState.rootFolderId.set("");
                    } else {
                        mState.rootFolderId.set(folderId);
                    }
                });

                break;
            }

            case BackupActionsFactory.BuildGoogleDriveServiceAction: {
                if (!(action.getPayload() instanceof Payload)) {
                    Log.d(TAG, "BackupActionsFactory.BuildGoogleDriveServiceAction->BAD_PAYLOAD");
                    break;
                }

                Payload payload = (Payload) action.getPayload();
                Intent intent = null;
                Context context = null;
                String appName = null;

                if (payload.get("result_intent") instanceof Intent) {
                    intent = (Intent) payload.get("result_intent");
                } else {
                    break;
                }
                if (payload.get("context") instanceof Context) {
                    context = (Context) payload.get("context");
                } else {
                    break;
                }
                if (payload.get("appLabel") instanceof String) {
                    appName = (String) payload.get("appLabel");
                } else {
                    break;
                }

                mState.googleDriveServiceStatus.set("setting");

                Context finalContext = context;
                String finalAppName = appName;
                mBackupService.getSignInAccount(intent,
                        (googleAccount -> {
                            Log.d(TAG, "Signed in as " + googleAccount.getEmail());

                            mState.googleDriveService.set(mBackupService.getGoogleDriveService(googleAccount, finalContext, finalAppName));
                            mState.googleDriveServiceSet.set(true);
                            mState.googleDriveServiceStatus.set("set");
                        }),
                        (exception -> {
                            Log.e(TAG, "Unable to sign in.", exception);

                            mState.googleDriveServiceSet.set(false);
                            mState.googleDriveServiceStatus.set("not_set");
                        }));

                break;
            }
        }
    }
}
