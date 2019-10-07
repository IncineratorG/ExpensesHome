package com.costs.newcosts.services.realisation.backup.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import com.costs.newcosts.DB_Costs;
import com.costs.newcosts.DataUnitTableCostNames;
import com.costs.newcosts.DataUnitTableCostValues;
import com.costs.newcosts.XmlTags;
import com.costs.newcosts.services.realisation.backup.callbacks.RestoreDataBaseFromBackupProgress;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add a class header comment
 */
public class RestoreDataBaseFromBackupTask extends AsyncTask<Object, String, Object> {
    private static final String TAG = "tag";

    private Drive mGoogleDriveService;
    private String mBackupFolderId;
    private DB_Costs mCostsDb;

    private RestoreDataBaseFromBackupProgress mProgressCallback;

    private String COST_VALUES_FILE_NAME = "cost_values_data.xml";
    private String COST_NAME_FILE_NAME = "cost_names_data.xml";

    private ByteArrayInputStream mCostValuesStream;
    private ByteArrayInputStream mCostNamesStream;

    private List<DataUnitTableCostNames> mTableCostNamesDataList;
    private List<DataUnitTableCostValues> mTableCostValuesDataList;

    private boolean mTableCostNamesRetrieved = false;
    private boolean mTableCostValuesRetrieved = false;


    public RestoreDataBaseFromBackupTask(Drive googleDriveService,
                                         String backupFolderId,
                                         DB_Costs costsDb,
                                         RestoreDataBaseFromBackupProgress progressCallback) {
        mGoogleDriveService = googleDriveService;
        mBackupFolderId = backupFolderId;
        mCostsDb = costsDb;
        mProgressCallback = progressCallback;

        mTableCostNamesDataList = new ArrayList<>();
        mTableCostValuesDataList = new ArrayList<>();
    }

    @Override
    protected Object doInBackground(Object... objects) {
        publishProgress(TaskRunner.TaskStartedStatus);

        getBackupContent();
        restoreDataBase();

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        mProgressCallback.publishProgress(values[0]);
    }

    @Override
    protected void onCancelled() {
        mProgressCallback.publishProgress(TaskRunner.TaskInterruptedStatus);
    }

    @Override
    protected void onPostExecute(Object o) {
        mProgressCallback.publishProgress(TaskRunner.TaskCompletedStatus);
    }

    private void getBackupContent() {
        FileList files = null;

        try {
            files = mGoogleDriveService.files().list()
                    .setSpaces("appDataFolder")
                    .setFields("nextPageToken, files(id, name)")
                    .setQ("mimeType='text/xml' and '" + mBackupFolderId + "' in parents")
                    .setPageSize(1000)
                    .execute();
        } catch (IOException e) {
            Log.d(TAG, "RestoreDataBaseFromBackupTask->getBackupContent()->IOEXCEPTION: " + e.getMessage());
            return;
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
                    mCostValuesStream = new ByteArrayInputStream(outputStream.toByteArray());
                } else if (file.getName().equals(COST_NAME_FILE_NAME)) {
                    mCostNamesStream = new ByteArrayInputStream(outputStream.toByteArray());
                }
            } catch (IOException e) {
                Log.d(TAG, "GetBackupFolderContentTask->doInBackground->IOEXCEPTION: " + e.getMessage());
                return;
            }
        }
    }

    private void restoreDataBase() {
        if (mCostNamesStream == null || mCostValuesStream == null) {
            return;
        }

        restoreCostNamesData();
        restoreCostValuesData();
        insertDataInDataBase();
    }

    private void restoreCostNamesData() {
        XmlPullParser parser = Xml.newPullParser();

        // Извлекаем данные для восстановления TABLE_COST_NAMES
        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(mCostNamesStream, null);

            String dataString = "";
            DataUnitTableCostNames costNamesDataUnit = new DataUnitTableCostNames();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase(XmlTags.COST_NAME_TAG_UNIT))
                            costNamesDataUnit = new DataUnitTableCostNames();
                        break;
                    case XmlPullParser.TEXT:
                        if (parser.isWhitespace())
                            dataString = "";
                        else
                            dataString = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        switch (tagName) {
                            case XmlTags.COST_NAME_TAG_UNIT:
                                mTableCostNamesDataList.add(costNamesDataUnit);
                                break;
                            case XmlTags.COST_NAME_TAG_ID:
                                costNamesDataUnit.set_ID_N(Integer.parseInt(dataString));
                                break;
                            case XmlTags.COST_NAME_TAG_NAME:
                                costNamesDataUnit.set_COST_NAME(dataString);
                                break;
                            case XmlTags.COST_NAME_TAG_ISACTIVE:
                                costNamesDataUnit.set_IS_ACTIVE(Integer.parseInt(dataString));
                                break;
                        }
                        break;
                }

                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        mTableCostNamesRetrieved = true;
    }

    private void restoreCostValuesData() {
        XmlPullParser parser = Xml.newPullParser();

        // Извлекаем данные для восстановления TABLE_COST_VALUES
        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(mCostValuesStream, null);

            String dataString = "";
            DataUnitTableCostValues costValuesDataUnit = new DataUnitTableCostValues();
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase(XmlTags.COST_VALUE_TAG_UNIT))
                            costValuesDataUnit = new DataUnitTableCostValues();
                        break;
                    case XmlPullParser.TEXT:
                        if (parser.isWhitespace())
                            dataString = "";
                        else
                            dataString = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        switch (tagName) {
                            case XmlTags.COST_VALUE_TAG_UNIT:
                                mTableCostValuesDataList.add(costValuesDataUnit);
                                break;
                            case XmlTags.COST_VALUE_TAG_ID:
                                costValuesDataUnit.set_ID_C(Integer.parseInt(dataString));
                                break;
                            case XmlTags.COST_VALUE_TAG_IDFK:
                                costValuesDataUnit.set_ID_N_FK(Integer.parseInt(dataString));
                                break;
                            case XmlTags.COST_VALUE_TAG_DAY:
                                costValuesDataUnit.set_DAY(Integer.parseInt(dataString));
                                break;
                            case XmlTags.COST_VALUE_TAG_MONTH:
                                costValuesDataUnit.set_MONTH(Integer.parseInt(dataString));
                                break;
                            case XmlTags.COST_VALUE_TAG_YEAR:
                                costValuesDataUnit.set_YEAR(Integer.parseInt(dataString));
                                break;
                            case XmlTags.COST_VALUE_TAG_MILLISECONDS:
                                costValuesDataUnit.set_DATE_IN_MILLISECONDS(Long.parseLong(dataString));
                                break;
                            case XmlTags.COST_VALUE_TAG_VALUE:
                                costValuesDataUnit.set_COST_VALUE(Double.parseDouble(dataString));
                                break;
                            case XmlTags.COST_VALUE_TAG_TEXT:
                                costValuesDataUnit.set_TEXT(dataString);
                                break;
                        }
                        break;
                }

                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        mTableCostValuesRetrieved = true;
    }

    private void insertDataInDataBase() {
        if (!mTableCostNamesRetrieved || !mTableCostValuesRetrieved) {
            mProgressCallback.publishProgress(TaskRunner.TaskErrorOccurredStatus);
            return;
        }

        mCostsDb.restoreTableCostNames(mTableCostNamesDataList);
        mCostsDb.deleteTableCostValues();
        for (int i = 0; i < mTableCostValuesDataList.size(); ++i) {
            if (!isCancelled()) {
                mCostsDb.restoreTableCostValues(mTableCostValuesDataList.get(i));
                if (i == 0 || (i % 10 == 1 && i != 1) || i == mTableCostValuesDataList.size() - 1) {
                    publishProgress("Восстановлены" +
                            " " +
                            i + "/" + (mTableCostValuesDataList.size() - 1));
                }
            } else {
                Log.i(TAG, "TASK_CANCELLED i = " + i);
                break;
            }
        }
    }
}
