package com.costs.newcosts.services.realisation.backup.tasks;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.util.Xml;

import com.costs.newcosts.Constants;
import com.costs.newcosts.DB_Costs;
import com.costs.newcosts.DataUnitTableCostNames;
import com.costs.newcosts.DataUnitTableCostValues;
import com.costs.newcosts.XmlTags;
import com.costs.newcosts.services.realisation.backup.callbacks.CreateDeviceBackupCompleted;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * TODO: Add a class header comment
 */
public class CreateDeviceBackupTask extends AsyncTask<Object, Object, Object> {
    private static final String TAG = "tag";

    private Drive mGoogleDriveService;
    private String mRootFolderId;
    private String mDeviceBackupFolderId;
    private DB_Costs mCostsDb;
    private CreateDeviceBackupCompleted mCompletionCallback;

    private String mRootFolderName = "EXPENSES_BACKUP";

    private String COST_VALUES_FILE_NAME = "cost_values_data.xml";
    private String COST_NAME_FILE_NAME = "cost_names_data.xml";


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
        createBackup(generateTableCostNamesXml(), generateTableCostValuesXml());
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

    private ByteArrayOutputStream generateTableCostNamesXml() {
        // Получаем содержимое TABLE_COST_NAMES
        List<DataUnitTableCostNames> tableCostNamesData = mCostsDb.getAllTableCostNames();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
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
            return null;
        }

        return outputStream;
    }

    private ByteArrayOutputStream generateTableCostValuesXml() {
        // Получаем содержимое TABLE_COST_VALUES
        List<DataUnitTableCostValues> tableCostValuesData = mCostsDb.getAllTableCostValues();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
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
            return null;
        }

        return outputStream;
    }

    private void createBackup(ByteArrayOutputStream costNamesOutputStream, ByteArrayOutputStream costValuesOutputStream) {
        if (costNamesOutputStream == null || costValuesOutputStream == null) {
            Log.d(TAG, "CreateDeviceBackupTask.createBackup()->STREAM_IS_NULL");
            return;
        }

        if (mRootFolderId == null || mRootFolderId.isEmpty()) {
            createRootFolder();
        }
        if (mRootFolderId == null || mRootFolderId.isEmpty()) {
            Log.d(TAG, "CreateDeviceBackupTask.createBackup()->UNABLE_TO_CREATE_ROOT_FOLDER");
            return;
        }

        Calendar calendar = new GregorianCalendar();
        String backupUserComment = "";

        // Формируем название новой папки резервной копии
        String deviceBackupFolderName = Build.MANUFACTURER + Constants.BACKUP_FOLDER_NAME_DELIMITER
                + Build.MODEL + Constants.BACKUP_FOLDER_NAME_DELIMITER
                + Build.ID + Constants.BACKUP_FOLDER_NAME_DELIMITER
                + calendar.get(Calendar.DAY_OF_MONTH) + "."
                + calendar.get(Calendar.MONTH) + "."
                + calendar.get(Calendar.YEAR) + Constants.BACKUP_FOLDER_NAME_DELIMITER
                + calendar.getTimeInMillis() + Constants.BACKUP_FOLDER_NAME_DELIMITER
                + backupUserComment;

        // Создаём папку, в которой будет находиться резервная копия.
        mDeviceBackupFolderId = createFolder(deviceBackupFolderName);
        if (mDeviceBackupFolderId == null || mDeviceBackupFolderId.isEmpty()) {
            Log.d(TAG, "CreateDeviceBackupTask.createBackup()->UNABLE_TO_CREATE_DEVICE_BACKUP_FOLDER");
            return;
        }

        // Сохраняем таблицу с названиями категорий расходов.
        try {
            File fileMetadata = new File();
            fileMetadata.setName(COST_NAME_FILE_NAME);
            fileMetadata.setParents(Collections.singletonList(mDeviceBackupFolderId));
            fileMetadata.setMimeType("text/xml");

            File file = mGoogleDriveService.files().create(fileMetadata, new ByteArrayContent("", costNamesOutputStream.toByteArray()))
                    .setFields("id")
                    .execute();
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }

        // Сохраняем таблицу со значениями расходов по соответствующей категории.
        try {
            File fileMetadata = new File();
            fileMetadata.setName(COST_VALUES_FILE_NAME);
            fileMetadata.setParents(Collections.singletonList(mDeviceBackupFolderId));
            fileMetadata.setMimeType("text/xml");

            File file = mGoogleDriveService.files().create(fileMetadata, new ByteArrayContent("", costValuesOutputStream.toByteArray()))
                    .setFields("id")
                    .execute();
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }
    }

    private void createRootFolder() {
        try {
            File fileMetadata = new File();
            fileMetadata.setName(mRootFolderName);
            fileMetadata.setParents(Collections.singletonList("appDataFolder"));
            fileMetadata.setMimeType("application/vnd.google-apps.folder");

            File file = mGoogleDriveService.files().create(fileMetadata)
                    .setFields("id")
                    .execute();

            mRootFolderId = file.getId();
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }
    }

    private String createFolder(String folderName) {
        String folderId = null;

        try {
            File fileMetadata = new File();
            fileMetadata.setName(folderName);
            fileMetadata.setParents(Collections.singletonList(mRootFolderId));
            fileMetadata.setMimeType("application/vnd.google-apps.folder");

            File file = mGoogleDriveService.files().create(fileMetadata)
                    .setFields("id")
                    .execute();

            folderId = file.getId();

        } catch (IOException e) {
            Log.d(TAG, e.toString());
            return folderId;
        }

        return folderId;
    }
}
