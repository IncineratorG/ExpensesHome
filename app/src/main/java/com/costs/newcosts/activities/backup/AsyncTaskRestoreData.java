package com.costs.newcosts.activities.backup;


/**
 * TODO: Add a class header comment
 */

public class AsyncTaskRestoreData /*extends AsyncTask<Void, String, Void>*/ {

//    private static final String TAG = "tag";
//    private CountDownLatch countDownLatch;
//    private DB_Costs cdb;
//    private GoogleApiClient googleApiClient;
//    private DriveFile tableCostNamesBackupFile;
//    private DriveFile tableCostValuesBackupFile;
//    private TextView statusTextView;
//    private DataRestoredCallback dataRestoredCallback;
//
//    private boolean tableCostNamesRetrieved = false;
//    private boolean tableCostValuesRetrieved = false;
//    private boolean dataRestored = false;
//
//    private List<DataUnitTableCostNames> tableCostNamesDataList;
//    private List<DataUnitTableCostValues> tableCostValuesDataList;
//
//    private String errorString = "";
//    private boolean taskCancelled = false;
//    private Context context;
//    private TextView restoringDialogTextView;
//    private AlertDialog restoringProgressDialog;
//
//
//    public interface DataRestoredCallback {
//        void dataRestored(boolean b);
//    }
//
//
//    public AsyncTaskRestoreData(GoogleApiClient googleApiClient, Context context,
//                                DriveFile tableCostNamesBackupFile, DriveFile tableCostValuesBackupFile,
//                                TextView statusTextView)
//    {
//        cdb = DB_Costs.getInstance(context);
//        this.googleApiClient = googleApiClient;
//        this.tableCostNamesBackupFile = tableCostNamesBackupFile;
//        this.tableCostValuesBackupFile = tableCostValuesBackupFile;
//        this.statusTextView = statusTextView;
//        dataRestoredCallback = (DataRestoredCallback) context;
//        this.context = context;
//
//        countDownLatch = new CountDownLatch(2);
//    }
//
//
//    @Override
//    protected void onPreExecute() {
//        AlertDialog.Builder restoringProgressDialogBuilder = new AlertDialog.Builder(context);
//        restoringProgressDialogBuilder.setCancelable(false);
//        restoringProgressDialogBuilder.setTitle(context.getResources().getString(R.string.atrd_restoringProgressDialogBuilder_Title_string));
//        restoringProgressDialogBuilder.setMessage(context.getResources().getString(R.string.atrd_restoringProgressDialogBuilder_Message_string));
//        restoringProgressDialogBuilder.setPositiveButton(context.getResources().getString(R.string.atrd_restoringProgressDialogBuilder_Cancel_string), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                AsyncTaskRestoreData.this.cancel(true);
//            }
//        });
//
//        restoringProgressDialog = restoringProgressDialogBuilder.create();
//        restoringProgressDialog.show();
//
//        restoringDialogTextView = (TextView) restoringProgressDialog.findViewById(android.R.id.message);
//        restoringDialogTextView.setGravity(Gravity.CENTER_HORIZONTAL);
//    }
//
//    @Override
//    protected Void doInBackground(Void... params) {
//        restoreCostNamesData();
//        restoreCostValuesData();
//        try {
//            countDownLatch.await();
//        } catch (InterruptedException e) {
//            Log.i(TAG, "INTERRUPTED_EXCEPTION IN doInBackground()");
//            e.printStackTrace();
//        }
//        insertRetrievedDataInDb();
//
//        return null;
//    }
//
//    // Получаем данные таблицы TABLE_COST_NAMES из резервной копии
//    private void restoreCostNamesData() {
//        if (tableCostNamesBackupFile == null) {
//            countDownLatch.countDown();
//            Log.i(TAG, "tableCostNamesBackupFile IS NULL");
//            return;
//        }
//
//        tableCostNamesBackupFile.open(googleApiClient, DriveFile.MODE_READ_ONLY, null).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
//            @Override
//            public void onResult(@NonNull DriveApi.DriveContentsResult driveContentsResult) {
//                if (!driveContentsResult.getStatus().isSuccess()) {
//                    errorString = "!!ERROR OPENING TABLE_COST_NAMES BACKUP FILES!!";
//                    Log.i(TAG, "!!ERROR OPENING TABLE_COST_NAMES BACKUP FILES!!");
//                    countDownLatch.countDown();
//                    return;
//                }
//                publishProgress(context.getResources().getString(R.string.atrd_publishProgress_StageOne_string));
//
//                tableCostNamesDataList = new ArrayList<>();
//
//                DriveContents costNamesDriveContents = driveContentsResult.getDriveContents();
//                InputStream inputStream = costNamesDriveContents.getInputStream();
//                XmlPullParser parser = Xml.newPullParser();
//
//                // Извлекаем данные для восстановления TABLE_COST_NAMES
//                try {
//                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
//                    parser.setInput(inputStream, null);
//
//                    String dataString = "";
//                    DataUnitTableCostNames costNamesDataUnit = new DataUnitTableCostNames();
//                    int eventType = parser.getEventType();
//                    while (eventType != XmlPullParser.END_DOCUMENT) {
//                        String tagName = parser.getName();
//
//                        switch (eventType) {
//                            case XmlPullParser.START_TAG:
//                                if (tagName.equalsIgnoreCase(XmlTags.COST_NAME_TAG_UNIT))
//                                    costNamesDataUnit = new DataUnitTableCostNames();
//                                break;
//                            case XmlPullParser.TEXT:
//                                if (parser.isWhitespace())
//                                    dataString = "";
//                                else
//                                    dataString = parser.getText();
//                                break;
//                            case XmlPullParser.END_TAG:
//                                switch (tagName) {
//                                    case XmlTags.COST_NAME_TAG_UNIT:
//                                        tableCostNamesDataList.add(costNamesDataUnit);
//                                        break;
//                                    case XmlTags.COST_NAME_TAG_ID:
//                                        costNamesDataUnit.set_ID_N(Integer.parseInt(dataString));
//                                        break;
//                                    case XmlTags.COST_NAME_TAG_NAME:
//                                        costNamesDataUnit.set_COST_NAME(dataString);
//                                        break;
//                                    case XmlTags.COST_NAME_TAG_ISACTIVE:
//                                        costNamesDataUnit.set_IS_ACTIVE(Integer.parseInt(dataString));
//                                        break;
//                                }
//                                break;
//                        }
//
//                        eventType = parser.next();
//                    }
//                } catch (XmlPullParserException e) {
//                    e.printStackTrace();
//                    countDownLatch.countDown();
//                    return;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    countDownLatch.countDown();
//                    return;
//                }
//
//                tableCostNamesRetrieved = true;
//                countDownLatch.countDown();
//            }
//        });
//    }
//
//    // Получаем данные таблицы TABLE_COST_VALUES из резервной копии
//    private void restoreCostValuesData() {
//        if (tableCostValuesBackupFile == null) {
//            Log.i(TAG, "tableCostValuesBackupFile IS NULL");
//            countDownLatch.countDown();
//            return;
//        }
//
//        tableCostValuesBackupFile.open(googleApiClient, DriveFile.MODE_READ_ONLY, null).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
//            @Override
//            public void onResult(@NonNull DriveApi.DriveContentsResult driveContentsResult) {
//                if (!driveContentsResult.getStatus().isSuccess()) {
//                    errorString = "!!ERROR OPENING TABLE_COST_VALUES BACKUP FILES!!";
//                    Log.i(TAG, "!!ERROR OPENING TABLE_COST_VALUES BACKUP FILES!!");
//                    countDownLatch.countDown();
//                    return;
//                }
//                publishProgress(context.getResources().getString(R.string.atrd_publishProgress_StageTwo_string));
//
//                tableCostValuesDataList = new ArrayList<>();
//
//                DriveContents costNamesDriveContents = driveContentsResult.getDriveContents();
//                InputStream inputStream = costNamesDriveContents.getInputStream();
//                XmlPullParser parser = Xml.newPullParser();
//
//                // Извлекаем данные для восстановления TABLE_COST_VALUES
//                try {
//                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
//                    parser.setInput(inputStream, null);
//
//                    String dataString = "";
//                    DataUnitTableCostValues costValuesDataUnit = new DataUnitTableCostValues();
//                    int eventType = parser.getEventType();
//                    while (eventType != XmlPullParser.END_DOCUMENT) {
//                        String tagName = parser.getName();
//
//                        switch (eventType) {
//                            case XmlPullParser.START_TAG:
//                                if (tagName.equalsIgnoreCase(XmlTags.COST_VALUE_TAG_UNIT))
//                                    costValuesDataUnit = new DataUnitTableCostValues();
//                                break;
//                            case XmlPullParser.TEXT:
//                                if (parser.isWhitespace())
//                                    dataString = "";
//                                else
//                                    dataString = parser.getText();
//                                break;
//                            case XmlPullParser.END_TAG:
//                                switch (tagName) {
//                                    case XmlTags.COST_VALUE_TAG_UNIT:
//                                        tableCostValuesDataList.add(costValuesDataUnit);
//                                        break;
//                                    case XmlTags.COST_VALUE_TAG_ID:
//                                        costValuesDataUnit.set_ID_C(Integer.parseInt(dataString));
//                                        break;
//                                    case XmlTags.COST_VALUE_TAG_IDFK:
//                                        costValuesDataUnit.set_ID_N_FK(Integer.parseInt(dataString));
//                                        break;
//                                    case XmlTags.COST_VALUE_TAG_DAY:
//                                        costValuesDataUnit.set_DAY(Integer.parseInt(dataString));
//                                        break;
//                                    case XmlTags.COST_VALUE_TAG_MONTH:
//                                        costValuesDataUnit.set_MONTH(Integer.parseInt(dataString));
//                                        break;
//                                    case XmlTags.COST_VALUE_TAG_YEAR:
//                                        costValuesDataUnit.set_YEAR(Integer.parseInt(dataString));
//                                        break;
//                                    case XmlTags.COST_VALUE_TAG_MILLISECONDS:
//                                        costValuesDataUnit.set_DATE_IN_MILLISECONDS(Long.parseLong(dataString));
//                                        break;
//                                    case XmlTags.COST_VALUE_TAG_VALUE:
//                                        costValuesDataUnit.set_COST_VALUE(Double.parseDouble(dataString));
//                                        break;
//                                    case XmlTags.COST_VALUE_TAG_TEXT:
//                                        costValuesDataUnit.set_TEXT(dataString);
//                                        break;
//                                }
//                                break;
//                        }
//
//                        eventType = parser.next();
//                    }
//                } catch (XmlPullParserException e) {
//                    e.printStackTrace();
//                    countDownLatch.countDown();
//                    return;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    countDownLatch.countDown();
//                    return;
//                }
//
//                tableCostValuesRetrieved = true;
//                countDownLatch.countDown();
//            }
//        });
//    }
//
//    // Заменяеем содержимое базы данных DB_Costs полученными данными
//    private void insertRetrievedDataInDb() {
//        if (!tableCostNamesRetrieved || !tableCostValuesRetrieved) {
//            return;
//        }
//
//        cdb.restoreTableCostNames(tableCostNamesDataList);
//        cdb.deleteTableCostValues();
//        for (int i = 0; i < tableCostValuesDataList.size(); ++i) {
//            if (!isCancelled()) {
//                cdb.restoreTableCostValues(tableCostValuesDataList.get(i));
//                if (i == 0 || (i % 10 == 1 && i != 1) || i == tableCostValuesDataList.size() - 1) {
//                    publishProgress(context.getResources().getString(R.string.atrd_publishProgress_StageThree_string) +
//                                        " " +
//                                        i + "/" + (tableCostValuesDataList.size() - 1));
//                }
//            } else {
//                Log.i(TAG, "TASK_CANCELLED i = " + i);
//                taskCancelled = true;
//                break;
//            }
//        }
//        dataRestored = !taskCancelled;
//    }
//
//    @Override
//    protected void onCancelled() {
//        Log.i(TAG, "isCancelled");
//        dataRestoredCallback.dataRestored(false);
//    }
//
//    @Override
//    protected void onProgressUpdate(String... values) {
//        super.onProgressUpdate(values);
//        restoringDialogTextView.setText(values[0]);
//    }
//
//    @Override
//    protected void onPostExecute(Void aVoid) {
//        Log.i(TAG, "onPostExecute");
//        if (dataRestored)
//            dataRestoredCallback.dataRestored(true);
//        else
//            dataRestoredCallback.dataRestored(false);
//
//        restoringProgressDialog.dismiss();
//    }
}
