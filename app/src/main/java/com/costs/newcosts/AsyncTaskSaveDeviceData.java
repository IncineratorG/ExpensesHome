package com.costs.newcosts;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Xml;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * TODO: Add a class header comment
 */

public class AsyncTaskSaveDeviceData extends AsyncTask<Void, Integer, Void> {

    private static final String TAG = "tag";
    private GoogleApiClient googleApiClient;
    private DriveFolder DEVICE_BACKUP_FOLDER_FOLDER;
    private DB_Costs cdb;
    private Context context;

    private String TABLE_COST_NAMES_FILE_NAME;
    private MetadataChangeSet tableCostNamesMetadata;
    private DriveContents tableCostNamesDriveContents;
    private boolean tableCostNamesBackupCreated = false;
    private boolean tableCostNamesDataGenerated = false;

    private String TABLE_COST_VALUES_FILE_NAME;
    private MetadataChangeSet tableCostValuesMetadata;
    private DriveContents tableCostValuesDriveContents;
    private boolean tableCostValuesBackupCreated = false;
    private boolean tableCostValuesDataGenerated = false;
    private String REFERENCE_FILE_NAME;
    private long startTime;
    private CountDownLatch countDownLatch;
    private TextView statusTextView;

    private DataSavedCallback completeSavingDataListener;
    public interface DataSavedCallback {
        void dataSaved(boolean dataSavedSuccessful);
    }


    public AsyncTaskSaveDeviceData(GoogleApiClient googleApiClient,
                                   DriveFolder DEVICE_BACKUP_FOLDER_FOLDER,
                                   Context context,
                                   String TABLE_COST_NAMES_FILE_NAME,
                                   String REFERENCE_FILE_NAME,
                                   String TABLE_COST_VALUES_FILE_NAME,
                                   TextView statusTextView)
    {
        this.DEVICE_BACKUP_FOLDER_FOLDER = DEVICE_BACKUP_FOLDER_FOLDER;
        this.googleApiClient = googleApiClient;
        this.TABLE_COST_NAMES_FILE_NAME = TABLE_COST_NAMES_FILE_NAME;
        this.REFERENCE_FILE_NAME = REFERENCE_FILE_NAME;
        this.TABLE_COST_VALUES_FILE_NAME = TABLE_COST_VALUES_FILE_NAME;
        this.statusTextView = statusTextView;
        this.context = context;
        completeSavingDataListener = (DataSavedCallback) context;

        cdb = DB_Costs.getInstance(context);

        countDownLatch = new CountDownLatch(2);
    }


    @Override
    protected Void doInBackground(Void... params) {
        Log.i(TAG, "CREATING BACKUP DATA FILES");
        startTime = System.currentTimeMillis();

        publishProgress(1);

        generateTableCostNamesXml();
        generateTableCostValuesXml();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Log.i(TAG, "INTERRUPTED_EXCEPTION IN doInBackground()");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        statusTextView.setText(context.getResources().getString(R.string.atsdd_statusTextView_savingData_string));
    }

    // Генерируем xml из TABLE_COST_NAMES
    public void generateTableCostNamesXml() {

        Log.i(TAG, "GENERATING TABLE_COST_NAMES XML");
        Drive.DriveApi.newDriveContents(googleApiClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(@NonNull DriveApi.DriveContentsResult driveContentsResult) {
                if (!driveContentsResult.getStatus().isSuccess()) {
                    Log.i(TAG, "!!ERROR CREATING TABLE_COST_NAMES BACKUP DATA FILES!!");
                    tableCostNamesBackupCreated = false;
                    countDownLatch.countDown();
                    return;
                }
                Log.i(TAG, "CREATING TABLE_COST_NAMES BACKUP DATA CONTENT");

                // Получаем содержимое TABLE_COST_NAMES
                List<DataUnitTableCostNames> tableCostNamesData = cdb.getAllTableCostNames();

                // Генерируем на основе содержимого TABLE_COST_NAMES XML документ
                tableCostNamesDriveContents = driveContentsResult.getDriveContents();
                OutputStream outputStream = tableCostNamesDriveContents.getOutputStream();
                XmlSerializer serializer = Xml.newSerializer();
                try {
                    serializer.setOutput(outputStream, "UTF-8");
                    serializer.startDocument(null, true);
                    serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

                    serializer.startTag(XmlTags.COST_NAME_NAMESPACE, XmlTags.COST_NAME_TAG_BODY);
                    for (DataUnitTableCostNames costNameDataUnit : tableCostNamesData) {
                        serializer.startTag(XmlTags.COST_NAME_NAMESPACE, XmlTags.COST_NAME_TAG_UNIT);

                        serializer.startTag(XmlTags.COST_NAME_NAMESPACE, XmlTags.COST_NAME_TAG_ID);
                        serializer.text(String.valueOf(costNameDataUnit.get_ID_N()));
                        serializer.endTag(XmlTags.COST_NAME_NAMESPACE, XmlTags.COST_NAME_TAG_ID);

                        serializer.startTag(XmlTags.COST_NAME_NAMESPACE, XmlTags.COST_NAME_TAG_NAME);
                        serializer.text(costNameDataUnit.get_COST_NAME());
                        serializer.endTag(XmlTags.COST_NAME_NAMESPACE, XmlTags.COST_NAME_TAG_NAME);

                        serializer.startTag(XmlTags.COST_NAME_NAMESPACE, XmlTags.COST_NAME_TAG_ISACTIVE);
                        serializer.text(String.valueOf(costNameDataUnit.get_IS_ACTIVE()));
                        serializer.endTag(XmlTags.COST_NAME_NAMESPACE, XmlTags.COST_NAME_TAG_ISACTIVE);

                        serializer.endTag(XmlTags.COST_NAME_NAMESPACE, XmlTags.COST_NAME_TAG_UNIT);
                    }
                    serializer.endTag(XmlTags.COST_NAME_NAMESPACE, XmlTags.COST_NAME_TAG_BODY);

                    serializer.endDocument();
                    serializer.flush();
                } catch (IOException e) {
                    Log.i(TAG, "!!ERROR GENERATING TABLE_COST_NAMES XML!!");
                    e.printStackTrace();
                    countDownLatch.countDown();
                    return;
                }

                tableCostNamesMetadata = new MetadataChangeSet.Builder()
                        .setTitle(TABLE_COST_NAMES_FILE_NAME)
                        .setMimeType("text/xml")
                        .setPinned(false)
                        .build();
                tableCostNamesDataGenerated = true;
                countDownLatch.countDown();
            }
        });
    }

    // Генерируем xml из TABLE_COST_VALUES
    public void generateTableCostValuesXml() {
        Log.i(TAG, "GENERATING TABLE_COST_VALUES XML");
        Drive.DriveApi.newDriveContents(googleApiClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(@NonNull DriveApi.DriveContentsResult driveContentsResult) {
                if (!driveContentsResult.getStatus().isSuccess()) {
                    Log.i(TAG, "!!ERROR CREATING TABLE_COST_VALUES BACKUP DATA FILES!!");
                    tableCostValuesBackupCreated = false;
                    countDownLatch.countDown();
                    return;
                }
                Log.i(TAG, "CREATING TABLE_COST_VALUES BACKUP DATA CONTENT");

                // Получаем содержимое TABLE_COST_VALUES
                List<DataUnitTableCostValues> tableCostValuesData = cdb.getAllTableCostValues();

                // Генерируем на основе содержимого TABLE_COST_VALUES XML документ
                tableCostValuesDriveContents = driveContentsResult.getDriveContents();
                OutputStream outputStream = tableCostValuesDriveContents.getOutputStream();
                XmlSerializer serializer = Xml.newSerializer();
                try {
                    serializer.setOutput(outputStream, "UTF-8");
                    serializer.startDocument(null, true);
                    serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

                    serializer.startTag(XmlTags.COST_VALUE_NAMESPACE, XmlTags.COST_VALUE_TAG_BODY);
                    for (DataUnitTableCostValues costValuesDataUnit : tableCostValuesData) {
                        serializer.startTag(XmlTags.COST_VALUE_NAMESPACE, XmlTags.COST_VALUE_TAG_UNIT);

                        serializer.startTag(XmlTags.COST_VALUE_NAMESPACE, XmlTags.COST_VALUE_TAG_ID);
                        serializer.text(String.valueOf(costValuesDataUnit.get_ID_C()));
                        serializer.endTag(XmlTags.COST_VALUE_NAMESPACE, XmlTags.COST_VALUE_TAG_ID);

                        serializer.startTag(XmlTags.COST_VALUE_NAMESPACE, XmlTags.COST_VALUE_TAG_IDFK);
                        serializer.text(String.valueOf(costValuesDataUnit.get_ID_N_FK()));
                        serializer.endTag(XmlTags.COST_VALUE_NAMESPACE, XmlTags.COST_VALUE_TAG_IDFK);

                        serializer.startTag(XmlTags.COST_VALUE_NAMESPACE, XmlTags.COST_VALUE_TAG_DAY);
                        serializer.text(String.valueOf(costValuesDataUnit.get_DAY()));
                        serializer.endTag(XmlTags.COST_VALUE_NAMESPACE, XmlTags.COST_VALUE_TAG_DAY);

                        serializer.startTag(XmlTags.COST_VALUE_NAMESPACE, XmlTags.COST_VALUE_TAG_MONTH);
                        serializer.text(String.valueOf(costValuesDataUnit.get_MONTH()));
                        serializer.endTag(XmlTags.COST_VALUE_NAMESPACE, XmlTags.COST_VALUE_TAG_MONTH);

                        serializer.startTag(XmlTags.COST_VALUE_NAMESPACE, XmlTags.COST_VALUE_TAG_YEAR);
                        serializer.text(String.valueOf(costValuesDataUnit.get_YEAR()));
                        serializer.endTag(XmlTags.COST_VALUE_NAMESPACE, XmlTags.COST_VALUE_TAG_YEAR);

                        serializer.startTag(XmlTags.COST_VALUE_NAMESPACE, XmlTags.COST_VALUE_TAG_MILLISECONDS);
                        serializer.text(String.valueOf(costValuesDataUnit.get_DATE_IN_MILLISECONDS()));
                        serializer.endTag(XmlTags.COST_VALUE_NAMESPACE, XmlTags.COST_VALUE_TAG_MILLISECONDS);

                        serializer.startTag(XmlTags.COST_VALUE_NAMESPACE, XmlTags.COST_VALUE_TAG_VALUE);
                        serializer.text(String.valueOf(costValuesDataUnit.get_COST_VALUE()));
                        serializer.endTag(XmlTags.COST_VALUE_NAMESPACE, XmlTags.COST_VALUE_TAG_VALUE);

                        serializer.startTag(XmlTags.COST_VALUE_NAMESPACE, XmlTags.COST_VALUE_TAG_TEXT);
                        serializer.text(costValuesDataUnit.get_TEXT());
                        serializer.endTag(XmlTags.COST_VALUE_NAMESPACE, XmlTags.COST_VALUE_TAG_TEXT);

                        serializer.endTag(XmlTags.COST_VALUE_NAMESPACE, XmlTags.COST_VALUE_TAG_UNIT);
                    }
                    serializer.endTag(XmlTags.COST_VALUE_NAMESPACE, XmlTags.COST_VALUE_TAG_BODY);

                    serializer.endDocument();
                    serializer.flush();
                } catch (IOException e) {
                    Log.i(TAG, "!!ERROR GENERATING TABLE_COST_VALUES XML!!");
                    e.printStackTrace();
                    countDownLatch.countDown();
                    return;
                }

                tableCostValuesMetadata = new MetadataChangeSet.Builder()
                        .setTitle(TABLE_COST_VALUES_FILE_NAME)
                        .setMimeType("text/xml")
                        .setPinned(false)
                        .build();
                tableCostValuesDataGenerated = true;
                countDownLatch.countDown();
            }
        });
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        // Если в процессе генерации возникли ошибки - ничего не сохраняем
        if (!tableCostNamesDataGenerated || !tableCostValuesDataGenerated) {
            Log.i(TAG, "!!ERROR GENERATING XML!!");
            Log.i(TAG, "CREATING BACKUP DATA STOPPED");
            Log.i(TAG, "COST_NAMES: " + String.valueOf(tableCostNamesDataGenerated));
            Log.i(TAG, "COST_VALUES: " + String.valueOf(tableCostValuesDataGenerated));
            completeSavingDataListener.dataSaved(false);
            return;
        }
        Log.i(TAG, "SENDING BACKUP DATA TO DRIVE");
        statusTextView.setText(context.getResources().getString(R.string.atsdd_statusTextView_savingDataOnDisk_string));

        // Сохраняем данные на Google Disk.
        // Последовательно сохраняем TABLE_COST_NAMES -> TABLE_COST_VALUES -> REFERENCE_FILE
        DEVICE_BACKUP_FOLDER_FOLDER.createFile(googleApiClient, tableCostNamesMetadata, tableCostNamesDriveContents)
                .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                    @Override
                    public void onResult(@NonNull DriveFolder.DriveFileResult driveFileResult) {
                        if (!driveFileResult.getStatus().isSuccess()) {
                            Log.i(TAG, "!!ERROR CREATING TABLE_COST_NAMES BACKUP DATA!!");
                            Log.i(TAG, "CREATING BACKUP DATA STOPPED");
                            completeSavingDataListener.dataSaved(false);
                            return;
                        }
                        tableCostNamesBackupCreated = true;

                        // Сохраняем TABLE_COST_VALUES на диск
                        DEVICE_BACKUP_FOLDER_FOLDER.createFile(googleApiClient, tableCostValuesMetadata, tableCostValuesDriveContents)
                                .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                                    @Override
                                    public void onResult(@NonNull DriveFolder.DriveFileResult driveFileResult) {
                                        if (!driveFileResult.getStatus().isSuccess()) {
                                            Log.i(TAG, "!!ERROR CREATING TABLE_COST_VALUES BACKUP DATA!!");
                                            Log.i(TAG, "CREATING BACKUP DATA STOPPED");
                                            completeSavingDataListener.dataSaved(false);
                                            return;
                                        }
                                        tableCostValuesBackupCreated = true;

                                        // Сохраняем контрольный файл
                                        MetadataChangeSet referenceFileMetadata = new MetadataChangeSet.Builder()
                                                .setTitle(REFERENCE_FILE_NAME)
                                                .setMimeType("text/xml")
                                                .setPinned(false)
                                                .build();
                                        DEVICE_BACKUP_FOLDER_FOLDER.createFile(googleApiClient, referenceFileMetadata, null)
                                                .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                                                    @Override
                                                    public void onResult(@NonNull DriveFolder.DriveFileResult driveFileResult) {
                                                        if (driveFileResult.getStatus().isSuccess()) {
                                                            Log.i(TAG, "REFERENCE FILE CREATED");
                                                            Log.i(TAG, "BACKUP DATA CREATED");
                                                        }
                                                        else
                                                            Log.i(TAG, "!!ERROR CREATING REFERENCE FILE!!");
                                                        Log.i(TAG, "TIME SPENT FOR SAVING DATA: " + (System.currentTimeMillis() - startTime) + " MILLISECONDS");
                                                        completeSavingDataListener.dataSaved(true);
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }
}
