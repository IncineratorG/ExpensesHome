package com.costs.newcosts.services.backup.tasks;

import android.os.AsyncTask;
import android.util.Log;

/**
 * TODO: Add a class header comment
 */
public class TaskRunner {
    private static final String TAG = "tag";

    private static TaskRunner mInstance = null;
    private AsyncTask mCurrentTask;

    public static final String TaskStartedStatus = "task_started";
    public static final String TaskCompletedStatus = "task_completed";
    public static final String TaskInterruptedStatus = "task_interrupted";
    public static final String TaskErrorOccurredStatus = "task_error_occurred";

    public static final int CreateBackupTask = 5;
    public static final int GetBackupDataTask = 6;
    public static final int RestoreDataBaseFromBackupTask = 7;
    public static final int CreateDeviceBackupTask = 8;
    public static final int DeleteDeviceBackupTask = 9;


    private TaskRunner() {
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
    public void run(AsyncTask task) {
        if (mCurrentTask != null && mCurrentTask.getStatus() == AsyncTask.Status.RUNNING) {
            mCurrentTask.cancel(true);
        }

        mCurrentTask = task;
        task.execute();
    }

    public int getCurrentTaskType() {
        return getTaskType(mCurrentTask);
    }

    public boolean stopTask(int type) {
        switch (type) {
            case GetBackupDataTask: {
                if (mCurrentTask instanceof GetBackupDataTask && mCurrentTask.getStatus() == AsyncTask.Status.RUNNING) {
                    mCurrentTask.cancel(true);
                    return true;
                } else {
                    return false;
                }
            }

            case RestoreDataBaseFromBackupTask: {
                if (mCurrentTask instanceof RestoreDataBaseFromBackupTask && mCurrentTask.getStatus() == AsyncTask.Status.RUNNING) {
                    mCurrentTask.cancel(true);
                    return true;
                } else {
                    return false;
                }
            }

            case CreateDeviceBackupTask: {
                if (mCurrentTask instanceof CreateDeviceBackupTask && mCurrentTask.getStatus() == AsyncTask.Status.RUNNING) {
                    mCurrentTask.cancel(true);
                    return true;
                } else {
                    return false;
                }
            }

            case DeleteDeviceBackupTask: {
                if (mCurrentTask instanceof DeleteDeviceBackupTask && mCurrentTask.getStatus() == AsyncTask.Status.RUNNING) {
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

    private int getTaskType(AsyncTask task) {
        if (task == null) {
            return -1;
        }

        if (task instanceof GetBackupDataTask) {
            return GetBackupDataTask;
        } else if (task instanceof RestoreDataBaseFromBackupTask) {
            return RestoreDataBaseFromBackupTask;
        } else if (task instanceof CreateDeviceBackupTask) {
            return CreateDeviceBackupTask;
        } else if (task instanceof DeleteDeviceBackupTask) {
            return DeleteDeviceBackupTask;
        }

        return -1;
    }
}
