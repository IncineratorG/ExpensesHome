package com.costs.newcosts.stores.realisation.backup;

import android.content.Context;
import android.content.Intent;
import android.renderscript.ScriptGroup;
import android.util.Log;

import com.costs.newcosts.DB_Costs;
import com.costs.newcosts.services.realisation.backup.BackupService;
import com.costs.newcosts.stores.abstraction.Action;
import com.costs.newcosts.stores.abstraction.ActionsFactory;
import com.costs.newcosts.stores.realisation.backup.types.BackupContentBundle;
import com.costs.newcosts.stores.realisation.backup.types.CreateBackupStatus;
import com.costs.newcosts.stores.realisation.backup.types.DriveServiceBundle;
import com.costs.newcosts.stores.common.Payload;
import com.costs.newcosts.stores.abstraction.State;
import com.costs.newcosts.stores.abstraction.Store;
import com.costs.newcosts.stores.realisation.backup.types.RestoreStatus;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;

import java.io.InputStream;

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

            case BackupActionsFactory.SetBackupFolderContent: {
                if (!(action.getPayload() instanceof Payload)) {
                    Log.d(TAG, "BackupActionsFactory.SetBackupFolderContent->BAD_PAYLOAD");
                    break;
                }

                Payload payload = (Payload) action.getPayload();
                BackupContentBundle backupContentBundle = null;
                if (payload.get("backupContentBundle") instanceof BackupContentBundle) {
                    backupContentBundle = (BackupContentBundle) payload.get("backupContentBundle");
                } else {
                    break;
                }

                mState.backupContentBundle.set(backupContentBundle);

                break;
            }

            case BackupActionsFactory.SetRestoreStatus: {
                if (!(action.getPayload() instanceof Payload)) {
                    Log.d(TAG, "BackupActionsFactory.SetRestoreStatus->BAD_PAYLOAD");
                    break;
                }

                Payload payload = (Payload) action.getPayload();
                RestoreStatus restoreStatus = null;
                if (payload.get("restoreStatus") instanceof RestoreStatus) {
                    restoreStatus = (RestoreStatus) payload.get("restoreStatus");
                } else {
                    break;
                }

                mState.restoreStatus.set(restoreStatus);

                break;
            }

            case BackupActionsFactory.StopAsyncTask: {
                if (!(action.getPayload() instanceof Payload)) {
                    Log.d(TAG, "BackupActionsFactory.StopAsyncTask->BAD_PAYLOAD");
                    break;
                }

                Payload payload = (Payload) action.getPayload();
                Integer type = null;
                if (payload.get("taskType") instanceof Integer) {
                    type = (Integer) payload.get("taskType");
                } else {
                    break;
                }

                mBackupService.stopTask(type);

                break;
            }

            case BackupActionsFactory.SetGoogleSignInClient: {
                if (!(action.getPayload() instanceof Payload)) {
                    Log.d(TAG, "BackupActionsFactory.SetGoogleSignInClient->BAD_PAYLOAD");
                    break;
                }

                Payload payload = (Payload) action.getPayload();
                GoogleSignInClient client = null;
                if (payload.get("googleSignInClient") instanceof GoogleSignInClient) {
                    client = (GoogleSignInClient) payload.get("googleSignInClient");
                } else {
                    break;
                }

                mState.googleSignInClient.set(client);

                break;
            }

            case BackupActionsFactory.SetCreateBackupStatus: {
                if (!(action.getPayload() instanceof Payload)) {
                    Log.d(TAG, "BackupActionsFactory.SetCreateBackupStatus->BAD_PAYLOAD");
                    break;
                }

                Payload payload = (Payload) action.getPayload();
                CreateBackupStatus createBackupStatus = null;
                if (payload.get("createBackupStatus") instanceof CreateBackupStatus) {
                    createBackupStatus = (CreateBackupStatus) payload.get("createBackupStatus");
                } else {
                    break;
                }

                mState.createBackupStatus.set(createBackupStatus);

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

                        dispatch(setBackupFilesListAction);
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

                    dispatch(setRootFolderIdAction);
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

                break;
            }

            case BackupActionsFactory.GetBackupFolderContent: {
                if (!(action.getPayload() instanceof Payload)) {
                    Log.d(TAG, "BackupActionsFactory.GetBackupFolderContent->BAD_PAYLOAD");
                    break;
                }

                Payload payload = (Payload) action.getPayload();
                Drive googleDriveService = null;
                String backupFolderId = null;

                if (payload.get("googleDriveService") instanceof Drive) {
                    googleDriveService = (Drive) payload.get("googleDriveService");
                } else {
                    break;
                }
                if (payload.get("backupFolderId") instanceof String) {
                    backupFolderId = (String) payload.get("backupFolderId");
                } else {
                    break;
                }

                BackupContentBundle backupContentBundle = new BackupContentBundle(
                        null,
                        null,
                        BackupContentBundle.Setting
                );

                Action setBackupFolderContent = mActionsFactory.getAction(BackupActionsFactory.SetBackupFolderContent);

                Payload setBackupFolderContentEmptyPayload = new Payload();
                setBackupFolderContentEmptyPayload.set("backupContentBundle", backupContentBundle);

                setBackupFolderContent.setPayload(setBackupFolderContentEmptyPayload);

                dispatch(setBackupFolderContent);

                mBackupService.getBackupFolderContent(googleDriveService, backupFolderId, (backupContent) -> {
                    BackupContentBundle finalBackupContentBundle = new BackupContentBundle(
                            backupContent.getCostValuesInputStream(),
                            backupContent.getCostNamesInputStream(),
                            BackupContentBundle.Set
                    );

                    Payload setBackupFolderContentPayload = new Payload();
                    setBackupFolderContentPayload.set("backupContentBundle", finalBackupContentBundle);

                    setBackupFolderContent.setPayload(setBackupFolderContentPayload);

                    dispatch(setBackupFolderContent);
                });

                break;
            }

            case BackupActionsFactory.RestoreDbFromBackup: {
                if (!(action.getPayload() instanceof Payload)) {
                    Log.d(TAG, "BackupActionsFactory.RestoreDbFromBackup->BAD_PAYLOAD");
                    break;
                }

                Payload payload = (Payload) action.getPayload();
                InputStream costValuesStream = null;
                InputStream costNamesStream = null;
                DB_Costs costsDb = null;

                if (payload.get("costValuesStream") instanceof InputStream) {
                    costValuesStream = (InputStream) payload.get("costValuesStream");
                } else {
                    Log.d(TAG, "1");
                    break;
                }
                if (payload.get("costNamesStream") instanceof InputStream) {
                    costNamesStream = (InputStream) payload.get("costNamesStream");
                } else {
                    Log.d(TAG, "2");
                    break;
                }
                if (payload.get("costsDb") instanceof DB_Costs) {
                    costsDb = (DB_Costs) payload.get("costsDb");
                } else {
                    Log.d(TAG, "3");
                    break;
                }


                mBackupService.restoreDataBase(costsDb, costValuesStream, costNamesStream, (progress) -> {
                    Action setRestoreStatus = mActionsFactory.getAction(BackupActionsFactory.SetRestoreStatus);

                    Payload restoreStatusPayload = new Payload();
                    restoreStatusPayload.set("restoreStatus", new RestoreStatus(progress));

                    setRestoreStatus.setPayload(restoreStatusPayload);

                    dispatch(setRestoreStatus);
                });


                break;
            }

            case BackupActionsFactory.CreateBackup: {
                if (!(action.getPayload() instanceof Payload)) {
                    Log.d(TAG, "BackupActionsFactory.CreateBackup->BAD_PAYLOAD");
                    break;
                }

                Payload payload = (Payload) action.getPayload();
                String rootFolderId = null;
                Drive googleDriveService = null;
                DB_Costs costsDb = null;
                if (payload.get("rootFolderId") instanceof String) {
                    rootFolderId = (String) payload.get("rootFolderId");
                } else {
                    break;
                }
                if (payload.get("googleDriveService") instanceof Drive) {
                    googleDriveService = (Drive) payload.get("googleDriveService");
                } else {
                    break;
                }
                if (payload.get("costsDb") instanceof DB_Costs) {
                    costsDb = (DB_Costs) payload.get("costsDb");
                } else {
                    break;
                }

                Action setCreateBackupStatus = mActionsFactory.getAction(BackupActionsFactory.SetCreateBackupStatus);

                Payload setCreateBackupStatusPayload = new Payload();
                setCreateBackupStatusPayload.set("createBackupStatus", new CreateBackupStatus(CreateBackupStatus.InProgress));

                setCreateBackupStatus.setPayload(setCreateBackupStatusPayload);

                dispatch(setCreateBackupStatus);

                mBackupService.createBackup(costsDb, googleDriveService, rootFolderId, (completed) -> {
                    if (completed) {
                        setCreateBackupStatusPayload.set("createBackupStatus", new CreateBackupStatus(CreateBackupStatus.Complete));
                    } else {
                        setCreateBackupStatusPayload.set("createBackupStatus", new CreateBackupStatus(CreateBackupStatus.NotComplete));
                    }

                    setCreateBackupStatus.setPayload(setCreateBackupStatusPayload);

                    dispatch(setCreateBackupStatus);
                });

                break;
            }
        }
    }
}
