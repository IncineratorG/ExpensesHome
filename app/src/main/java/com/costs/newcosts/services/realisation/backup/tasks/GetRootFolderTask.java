package com.costs.newcosts.services.realisation.backup.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.costs.newcosts.services.realisation.backup.callbacks.GetRootFolderCompleted;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;

/**
 * TODO: Add a class header comment
 */
public class GetRootFolderTask extends AsyncTask<Object, Object, String> {
    private static final String TAG = "tag";

    private Drive mGoogleDriveService = null;
    private GetRootFolderCompleted mCompletionCallback;

    private String mRootFolderName = "EXPENSES_BACKUP";


    public GetRootFolderTask(Drive googleDriveService, GetRootFolderCompleted callback) {
        mGoogleDriveService = googleDriveService;
        mCompletionCallback = callback;
    }

    @Override
    protected String doInBackground(Object... objects) {
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
            Log.d(TAG, "GetRootFolderTask->doInBackground->IOEXCEPTION");
            return rootFolderId;
        }

        return rootFolderId;
    }

    @Override
    protected void onPostExecute(String rootFolderId) {
        mCompletionCallback.complete(rootFolderId);
    }
}
