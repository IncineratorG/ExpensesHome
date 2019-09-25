package com.costs.newcosts.services.realisation.backup.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.costs.newcosts.services.realisation.backup.callbacks.GetBackupFolderContentCompleted;
import com.costs.newcosts.services.realisation.backup.types.BackupContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * TODO: Add a class header comment
 */
public class GetBackupFolderContentTask extends AsyncTask<Object, Object, BackupContent> {
    private static final String TAG = "tag";

    private Drive mGoogleDriveService = null;
    private GetBackupFolderContentCompleted mCompletionCallback;
    private String mBackupFolderId;
    private String COST_VALUES_FILE_NAME = "cost_values_data.xml";
    private String COST_NAME_FILE_NAME = "cost_names_data.xml";


    public GetBackupFolderContentTask(Drive googleDriveService, String backupFolderId, GetBackupFolderContentCompleted callback) {
        mGoogleDriveService = googleDriveService;
        mBackupFolderId = backupFolderId;
        mCompletionCallback = callback;
    }

    @Override
    protected BackupContent doInBackground(Object... objects) {
        BackupContent backupContent = new BackupContent();

        FileList files = null;

        try {
            files = mGoogleDriveService.files().list()
                    .setSpaces("appDataFolder")
                    .setFields("nextPageToken, files(id, name)")
                    .setQ("mimeType='text/xml' and '" + mBackupFolderId + "' in parents")
                    .setPageSize(1000)
                    .execute();
        } catch (IOException e) {
            Log.d(TAG, "GetBackupFolderContentTask->doInBackground->IOEXCEPTION: " + e.getMessage());
            return null;
        }

        for (File file : files.getFiles()) {
            // Пропускаем пустой файл ('reference_file').
            if (!file.getName().contains(".")) {
                continue;
            }

            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                mGoogleDriveService.files().get(file.getId()).executeMediaAndDownloadTo(outputStream);

                if (file.getName().equals(COST_VALUES_FILE_NAME)) {
                    backupContent.setCostValuesInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
                } else if (file.getName().equals(COST_NAME_FILE_NAME)) {
                    backupContent.setCostNamesInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
                }
            } catch (IOException e) {
                Log.d(TAG, "GetBackupFolderContentTask->doInBackground->IOEXCEPTION: " + e.getMessage());
                return null;
            }
        }

        return backupContent;
    }

    @Override
    protected void onCancelled() {
        Log.d(TAG, "GET_CONTENT_TASK_CANCELLED");
    }

    @Override
    protected void onPostExecute(BackupContent backupContent) {
        mCompletionCallback.complete(backupContent);
    }
}
