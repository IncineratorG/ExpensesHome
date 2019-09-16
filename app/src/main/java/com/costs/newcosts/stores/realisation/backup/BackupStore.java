package com.costs.newcosts.stores.realisation.backup;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.costs.newcosts.services.realisation.backup.BackupService;
import com.costs.newcosts.stores.abstraction.Action;
import com.costs.newcosts.stores.abstraction.ActionsFactory;
import com.costs.newcosts.stores.realisation.backup.types.DriveServiceBundle;
import com.costs.newcosts.stores.common.Payload;
import com.costs.newcosts.stores.abstraction.State;
import com.costs.newcosts.stores.abstraction.Store;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;

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

            case BackupActionsFactory.SetSignIn: {
                mState.signedIn.set(true);

                break;
            }

            case BackupActionsFactory.ClearStore: {
                mState.googleDriveServiceBundle.set(new DriveServiceBundle(null, DriveServiceBundle.NotSet));
                mState.signedIn.set(false);
                mState.hasInternetConnection.set(false);
                mState.rootFolderId.set("");

                break;
            }

            case BackupActionsFactory.SetBackupFilesList: {
                if (!(action.getPayload() instanceof Payload)) {
                    Log.d(TAG, "BackupActionsFactory.SetBackupFilesList->BAD_PAYLOAD");
                    break;
                }

                Payload payload = (Payload) action.getPayload();
                FileList filesList = null;
                if (payload.get("filesList") instanceof FileList) {
                    filesList = (FileList) payload.get("filesList");
                } else {
                    break;
                }

                mState.backupFilesList.set(filesList);

                break;
            }

            case BackupActionsFactory.SetRootFolderId: {
                if (!(action.getPayload() instanceof Payload)) {
                    Log.d(TAG, "BackupActionsFactory.SetRootFolderId->BAD_PAYLOAD");
                    break;
                }

                Payload payload = (Payload) action.getPayload();
                String folderId = null;
                if (payload.get("folderId") instanceof String) {
                    folderId = (String) payload.get("folderId");
                } else {
                    break;
                }

                mState.rootFolderId.set(folderId);

                break;
            }

            case BackupActionsFactory.SetDriveServiceBundle: {
                if (!(action.getPayload() instanceof DriveServiceBundle)) {
                    Log.d(TAG, "BackupActionsFactory.SetDriveServiceBundle->BAD_PAYLOAD");
                    break;
                }

                DriveServiceBundle driveServiceBundle = (DriveServiceBundle) action.getPayload();

                mState.googleDriveServiceBundle.set(driveServiceBundle);

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
                        Payload filesListPayload = new Payload();
                        filesListPayload.set("filesList", fileList);

                        Action setBackupFilesListAction = mActionsFactory.getAction(BackupActionsFactory.SetBackupFilesList);
                        setBackupFilesListAction.setPayload(filesListPayload);

                        reduce(setBackupFilesListAction);
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
                    Payload payload = new Payload();

                    if (folderId == null) {
                        payload.set("folderId", "");
                    } else {
                        payload.set("folderId", folderId);
                    }

                    Action setRootFolderIdAction = mActionsFactory.getAction(BackupActionsFactory.SetRootFolderId);
                    setRootFolderIdAction.setPayload(payload);

                    reduce(setRootFolderIdAction);
                });

                break;
            }

            case BackupActionsFactory.BuildGoogleDriveService: {
                if (!(action.getPayload() instanceof Payload)) {
                    Log.d(TAG, "BackupActionsFactory.BuildGoogleDriveService->BAD_PAYLOAD");
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


                DriveServiceBundle driveServiceBundle = new DriveServiceBundle(null, DriveServiceBundle.Setting);

                Action setDriveServiceBundleAction = mActionsFactory.getAction(BackupActionsFactory.SetDriveServiceBundle);
                setDriveServiceBundleAction.setPayload(driveServiceBundle);

                reduce(setDriveServiceBundleAction);


                Context finalContext = context;
                String finalAppName = appName;
                mBackupService.getSignInAccount(intent,
                        (googleAccount -> {
                            Log.d(TAG, "Signed in as " + googleAccount.getEmail());

                            Drive driveService = mBackupService.getGoogleDriveService(googleAccount, finalContext, finalAppName);
                            DriveServiceBundle finalDriveServiceBundle = new DriveServiceBundle(driveService, DriveServiceBundle.Set);
                            setDriveServiceBundleAction.setPayload(finalDriveServiceBundle);

                            reduce(setDriveServiceBundleAction);
                        }),
                        (exception -> {
                            Log.e(TAG, "Unable to sign in.", exception);

                            DriveServiceBundle finalDriveServiceBundle = new DriveServiceBundle(null, DriveServiceBundle.NotSet);
                            setDriveServiceBundleAction.setPayload(finalDriveServiceBundle);

                            reduce(setDriveServiceBundleAction);
                        }));

                break;
            }
        }
    }
}
