package com.costs.newcosts.services.realisation.backup.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.costs.newcosts.DB_Costs;
import com.costs.newcosts.services.realisation.backup.callbacks.GetBackupFolderContentCompleted;
import com.costs.newcosts.services.realisation.backup.callbacks.GetBackupListCompleted;
import com.costs.newcosts.services.realisation.backup.callbacks.GetRootFolderCompleted;
import com.costs.newcosts.services.realisation.backup.callbacks.RestoreDataBaseProgress;
import com.costs.newcosts.services.realisation.backup.types.BackupContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;

import java.io.InputStream;

/**
 * TODO: Add a class header comment
 */
public class TaskRunner {
    private static final String TAG = "tag";

    private static TaskRunner mInstance = null;
    private AsyncTask mCurrentTask;

    public static final String TaskCompletedStatus = "task_completed";
    public static final String TaskInterruptedStatus = "task_interrupted";
    public static final String TaskErrorOccurredStatus = "task_error_occurred";

    public static final int GetBackupFolderContentTask = 1;
    public static final int GetBackupListTask = 2;
    public static final int GetRootFolderTask = 3;
    public static final int RestoreDataBaseTask = 4;
    public static final int CreateBackupTask = 5;


    public TaskRunner() {
        mCurrentTask = null;
    }

    public static synchronized TaskRunner getInstance() {
        if (mInstance != null)
            return mInstance;
        else {
            mInstance = new TaskRunner();
            return mInstance;
        }
    }

    @SuppressWarnings("unchecked")
    public void runTask(AsyncTask task) {
        if (mCurrentTask != null && mCurrentTask.getStatus() == AsyncTask.Status.RUNNING) {
            mCurrentTask.cancel(true);
        }

        mCurrentTask = task;
        task.execute();
    }

    public boolean stopTask(int type) {
        switch (type) {
            case GetBackupFolderContentTask: {
                if (mCurrentTask instanceof GetBackupFolderContentTask && mCurrentTask.getStatus() == AsyncTask.Status.RUNNING) {
                    mCurrentTask.cancel(true);
                    return true;
                } else {
                    return false;
                }
            }

            case GetBackupListTask: {
                if (mCurrentTask instanceof GetBackupListTask && mCurrentTask.getStatus() == AsyncTask.Status.RUNNING) {
                    mCurrentTask.cancel(true);
                    return true;
                } else {
                    return false;
                }
            }

            case GetRootFolderTask: {
                if (mCurrentTask instanceof GetRootFolderTask && mCurrentTask.getStatus() == AsyncTask.Status.RUNNING) {
                    mCurrentTask.cancel(true);
                    return true;
                } else {
                    return false;
                }
            }

            case RestoreDataBaseTask: {
                if (mCurrentTask instanceof RestoreDataBaseTask && mCurrentTask.getStatus() == AsyncTask.Status.RUNNING) {
                    mCurrentTask.cancel(true);
                    return true;
                } else {
                    return false;
                }
            }

            case CreateBackupTask: {
                if (mCurrentTask instanceof CreateBackupTask && mCurrentTask.getStatus() == AsyncTask.Status.RUNNING) {
                    mCurrentTask.cancel(true);
                    return true;
                } else {
                    return false;
                }
            }

            default: {
                Log.d(TAG, "UNKNOWN_TASK_TYPE: " + String.valueOf(type));
            }
        }

        return false;
    }



//    public void runGetBackupFolderContentTask(Drive googleDriveService, String folderId, GetBackupFolderContentCompleted callback) {
//        mGetBackupFolderContentTask = new GetBackupFolderContentTask(googleDriveService, folderId, callback);
//        mGetBackupFolderContentTask.execute();
//    }
//
//    public void stopGetBackupFolderContentTask() {
//        if (mGetBackupFolderContentTask != null && mGetBackupFolderContentTask.getStatus() == AsyncTask.Status.RUNNING) {
//            mGetBackupFolderContentTask.cancel(true);
//        }
//    }
//
//    public void runGetBackupListTask(Drive googleDriveService, String rootFolderId, GetBackupListCompleted callback) {
//        mGetBackupListTask = new GetBackupListTask(googleDriveService, rootFolderId, callback);
//        mGetBackupListTask.execute();
//    }
//
//    public void stopGetBackupListTask() {
//        if (mGetBackupListTask != null && mGetBackupListTask.getStatus() == AsyncTask.Status.RUNNING) {
//            mGetBackupListTask.cancel(true);
//        }
//    }
//
//    public void runGetRootFolderTask(Drive googleDriveService, GetRootFolderCompleted callback) {
//        mGetRootFolderTask = new GetRootFolderTask(googleDriveService, callback);
//        mGetRootFolderTask.execute();
//    }
//
//    public void stopGetRootFolderTask() {
//        if (mGetRootFolderTask != null && mGetRootFolderTask.getStatus() == AsyncTask.Status.RUNNING) {
//            mGetRootFolderTask.cancel(true);
//        }
//    }
//
//    public void runRestoreDataBaseTask(DB_Costs costsDb, InputStream costValuesStream, InputStream costNamesStream, RestoreDataBaseProgress progressCallback) {
//        mRestoreDataBaseTask = new RestoreDataBaseTask(costsDb, costValuesStream, costNamesStream, progressCallback);
//        mRestoreDataBaseTask.execute();
//    }
//
//    public void stopRestoreDataBaseTask() {
//        if (mRestoreDataBaseTask != null && mRestoreDataBaseTask.getStatus() == AsyncTask.Status.RUNNING) {
//            mRestoreDataBaseTask.cancel(true);
//        }
//    }
}
