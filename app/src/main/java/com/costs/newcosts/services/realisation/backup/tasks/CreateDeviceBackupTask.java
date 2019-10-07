package com.costs.newcosts.services.realisation.backup.tasks;

import android.os.AsyncTask;

import com.costs.newcosts.DB_Costs;
import com.costs.newcosts.services.realisation.backup.callbacks.CreateDeviceBackupCompleted;
import com.google.api.services.drive.Drive;

/**
 * TODO: Add a class header comment
 */
public class CreateDeviceBackupTask extends AsyncTask<Object, Object, Object> {
    private static final String TAG = "tag";

    private Drive mGoogleDriveService = null;
    private String mRootFolderId = null;
    private DB_Costs mCostsDb = null;
    private CreateDeviceBackupCompleted mCompletionCallback;

    private String mRootFolderName = "EXPENSES_BACKUP";


    public CreateDeviceBackupTask(Drive googleDriveService,
                                  String rootFolderId,
                                  DB_Costs costsDb,
                                  CreateDeviceBackupCompleted callback) {
        mGoogleDriveService = googleDriveService;
        mRootFolderId = rootFolderId;
        mCostsDb = costsDb;
        mCompletionCallback = callback;
    }

    @Override
    protected Object doInBackground(Object... objects) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onCancelled() {
        mCompletionCallback.complete(false);
    }

    @Override
    protected void onPostExecute(Object o) {
        mCompletionCallback.complete(true);
    }
}
