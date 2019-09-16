package com.costs.newcosts.services.realisation.backup.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.costs.newcosts.services.realisation.backup.callbacks.GetBackupListCompleted;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;

/**
 * TODO: Add a class header comment
 */
public class GetBackupListTask extends AsyncTask<Object, Object, FileList> {
    private static final String TAG = "tag";

    private Drive mGoogleDriveService = null;
    private String mRootFolderId = "";
    private GetBackupListCompleted mCompletionCallback;


    public GetBackupListTask(Drive googleDriveService, String rootFolderId, GetBackupListCompleted callback) {
        mGoogleDriveService = googleDriveService;
        mRootFolderId = rootFolderId;
        mCompletionCallback = callback;
    }

    @Override
    protected FileList doInBackground(Object... params) {
        FileList files = null;

        try {
            files = mGoogleDriveService.files().list()
                    .setSpaces("appDataFolder")
                    .setFields("nextPageToken, files(id, name)")
                    .setQ("mimeType='application/vnd.google-apps.folder' and '" + mRootFolderId + "' in parents")
                    .setPageSize(1000)
                    .execute();
        } catch (IOException e) {
            Log.d(TAG, "GetBackupListTask->doInBackground->IOEXCEPTION: " + e.getMessage());
            return files;
        }

        return files;
    }

    @Override
    protected void onPostExecute(FileList fileList) {
        mCompletionCallback.complete(fileList);
    }
}
