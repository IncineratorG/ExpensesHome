package com.costs.newcosts.services.backup.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.costs.newcosts.services.backup.callbacks.GetBackupDataCompleted;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;

/**
 * TODO: Add a class header comment
 */
public class GetBackupDataTask extends AsyncTask<Object, Object, Object> {
    private static final String TAG = "tag";

    private String mRootFolderName = "EXPENSES_BACKUP";

    private Drive mGoogleDriveService;
    private GetBackupDataCompleted mCallback;

    private String mRootFolderId;
    private FileList mFilesList;


    public GetBackupDataTask(Drive googleDriveService, GetBackupDataCompleted callback) {
        mGoogleDriveService = googleDriveService;
        mCallback = callback;
    }

    @Override
    protected Object doInBackground(Object... objects) {
        getBackupList(getRootFolderId());
        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(Object o) {
        mCallback.complete(mRootFolderId, mFilesList);
    }

    private String getRootFolderId() {
        String rootFolderId = null;

        try {
            FileList files = mGoogleDriveService.files().list()
                    .setSpaces("appDataFolder")
                    .setFields("nextPageToken, files(id, name)")
                    .setPageSize(1000)
                    .setQ("mimeType='application/vnd.google-apps.folder' and name='" + mRootFolderName + "'")
                    .execute();

            for (File file : files.getFiles()) {
                if (file.getName().equals(mRootFolderName)) {
                    rootFolderId = file.getId();
                    break;
                }
            }
        } catch (IOException e) {
            Log.d(TAG, "GetBackupDataTask.getRootFolderId()->IOEXCEPTION");
            return null;
        }

        return rootFolderId;
    }

    private void getBackupList(String rootFolderId) {
        if (rootFolderId == null || rootFolderId.isEmpty()) {
            return;
        }

        FileList files = null;

        try {
            files = mGoogleDriveService.files().list()
                    .setSpaces("appDataFolder")
                    .setFields("nextPageToken, files(id, name)")
                    .setQ("mimeType='application/vnd.google-apps.folder' and '" + rootFolderId + "' in parents")
                    .setPageSize(1000)
                    .execute();
        } catch (IOException e) {
            Log.d(TAG, "GetBackupDataTask.getBackupList()->IOEXCEPTION: " + e.getMessage());
            return;
        }

        mRootFolderId = rootFolderId;
        mFilesList = files;
    }
}
