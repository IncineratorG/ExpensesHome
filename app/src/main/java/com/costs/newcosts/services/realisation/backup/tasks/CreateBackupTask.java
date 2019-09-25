package com.costs.newcosts.services.realisation.backup.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.costs.newcosts.DB_Costs;
import com.costs.newcosts.services.realisation.backup.callbacks.CreateBackupCompleted;
import com.costs.newcosts.services.realisation.backup.types.BackupContent;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.util.Collections;

/**
 * TODO: Add a class header comment
 */
public class CreateBackupTask extends AsyncTask<Object, Object, Object> {
    private static final String TAG = "tag";

    private Drive mGoogleDriveService = null;
    private String mRootFolderId = null;
    private DB_Costs mCostsDb = null;
    private CreateBackupCompleted mCompletionCallback;

    private String mRootFolderName = "EXPENSES_BACKUP";

//    private Drive mGoogleDriveService = null;
//    private GetBackupFolderContentCompleted mCompletionCallback;
//    private String mBackupFolderId;
//    private String COST_VALUES_FILE_NAME = "cost_values_data.xml";
//    private String COST_NAME_FILE_NAME = "cost_names_data.xml";


    public CreateBackupTask(DB_Costs costsDb, Drive googleDriveService, String rootFolderId, CreateBackupCompleted callback) {
        mCostsDb = costsDb;
        mGoogleDriveService = googleDriveService;
        mRootFolderId = rootFolderId;
        mCompletionCallback = callback;
    }

    @Override
    protected Object doInBackground(Object... objects) {
        Log.d(TAG, "CreateBackupTask->doInBackground()");

        if (mRootFolderId.isEmpty()) {
            Log.d(TAG, "CreateBackupTask->doInBackground()->ROOT_FOLDER_ID_IS_EMPTY: " + mRootFolderId);
            createRootFolder();
        } else {
            Log.d(TAG, "CreateBackupTask->doInBackground()->ROOT_FOLDER_ID_NOT_EMPTY: " + mRootFolderId);
        }

        return null;
    }

    @Override
    protected void onCancelled() {
        mCompletionCallback.complete(false);
    }

    @Override
    protected void onPostExecute(Object object) {
        mCompletionCallback.complete(true);
    }

    private void createRootFolder() {
        Log.d(TAG, "CreateBackupTask->createRootFolder()");

//        File fileMetadata = new File();
//        fileMetadata.setName(mRootFolderName);
//        fileMetadata.setMimeType("application/vnd.google-apps.folder");

        try {
            File fileMetadata = new File();
            fileMetadata.setName(mRootFolderName);
            fileMetadata.setParents(Collections.singletonList("appDataFolder"));
            java.io.File filePath = new java.io.File(mRootFolderName);
            FileContent mediaContent = new FileContent("application/json", filePath);
            File file = mGoogleDriveService.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
            System.out.println("File ID: " + file.getId());
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }
    }
}