package com.costs.newcosts.services.realisation.backup.tasks;

import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.util.Log;

import com.costs.newcosts.DB_Costs;
import com.costs.newcosts.services.realisation.backup.callbacks.RestoreDataBaseProgress;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.io.InputStream;

/**
 * TODO: Add a class header comment
 */
public class RestoreDataBaseTask extends AsyncTask<Void, String, Void> {
    private static final String TAG = "tag";

    private DB_Costs mCostsDb;
    private InputStream mCostValuesStream;
    private InputStream mCostNamesStream;

    private RestoreDataBaseProgress mProgressCallback;


    public RestoreDataBaseTask(DB_Costs costsDb,
                               InputStream costValuesStream,
                               InputStream costNamesStream,
                               RestoreDataBaseProgress progressCallback) {
        mCostsDb = costsDb;
        mCostValuesStream = costValuesStream;
        mCostNamesStream = costNamesStream;
        mProgressCallback = progressCallback;
    }

    @Override
    protected Void doInBackground(Void... params) {

        for (int i = 0; i < 5; ++i) {
            try {
                publishProgress(String.valueOf(i));
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.d(TAG, "INTERRUPT: " + e.getMessage());
            }
        }

        return null;
    }

    @Override
    protected void onCancelled() {

    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        mProgressCallback.publishProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {

    }
}
