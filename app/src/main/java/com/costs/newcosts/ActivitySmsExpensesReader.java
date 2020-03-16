package com.costs.newcosts;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class ActivitySmsExpensesReader
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
                    DialogSmsReader.DialogSmsReaderCallback {

    private static final String TAG = "tag";

    private static final int LOADER_ID = 1;
    private static final String SMS_URI = "content://sms/inbox";
    private static final String SMS_DATE = "date";
    private static final String SMS_ADDRESS = "address";
    private static final String SMS_BODY = "body";
    private static final String SMS_SORT_ORDER = "date DESC";
    private static final String[] SMS_PROJECTION = { "address, body, date" };
    private static final String[] SMS_SELECTION_ARGS = null;

    private RecyclerView smsReaderRecyclerView;
    private AdapterSmsReaderRecyclerView smsReaderRecyclerViewAdapter;
    private List<DataUnitSms> smsDataList = new ArrayList<>();
    private List<Long> lastEnteredValuesMillisecondsList = new ArrayList<>();
    private DB_Costs costsDB;
    private DB_SmsNotes smsNotesDB;
    private Long millis;
    private DataUnitSms chosenSms;
    private int chosenSmsPosition = -1;

    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 123;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_expenses_reader);

        // Стрелка "Назад"
        ImageView arrowBackImageView = (ImageView) findViewById(R.id.activity_sms_expenses_reader_arrow_back);
        arrowBackImageView.setOnClickListener(v -> returnToPreviousActivity());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        smsReaderRecyclerView = (RecyclerView) findViewById(R.id.activity_sms_expenses_reader_recycler_view);
        smsReaderRecyclerView.setLayoutManager(linearLayoutManager);

        // Выбираем сообщения за последние два дня
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, -3);
        millis = calendar.getTimeInMillis();

        costsDB = DB_Costs.getInstance(this);
        smsNotesDB = DB_SmsNotes.getInstance(this);

//        getSupportLoaderManager().initLoader(LOADER_ID, null, this);


        // =========================================
        int permissionCheck = ContextCompat.checkSelfPermission(ActivitySmsExpensesReader.this, android.Manifest.permission.READ_SMS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "PERMISSION_NOT_GRANTED");
            ActivityCompat.requestPermissions(ActivitySmsExpensesReader.this,
                                                new String[]{android.Manifest.permission.READ_SMS},
                                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            Log.d(TAG, "PERMISSION_GRANTED");
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        }

//        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//            if (!ActivityCompat.shouldShowRequestPermissionRationale(ActivitySmsExpensesReader.this, android.Manifest.permission.READ_SMS)) {
//                showMessageOKCancel("You need to allow access to SMS",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                System.out.println("ON_CLICK");
//
//                                ActivityCompat.requestPermissions(ActivitySmsExpensesReader.this, new String[] {android.Manifest.permission.READ_SMS},
//                                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
//                            }
//                        });
//                return;
//            }
//
//            ActivityCompat.requestPermissions(ActivitySmsExpensesReader.this, new String[] {android.Manifest.permission.READ_SMS},
//                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
//        } else {
//            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
//        }
        // ===============================================
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    System.out.println("PERMISSION_GRANTED");
                    Log.d(TAG, "PERMISSION_GRANTED");
                    getSupportLoaderManager().initLoader(LOADER_ID, null, this);
                } else {
//                    System.out.println("PERMISSION_DENIED");
                    Log.d(TAG, "PERMISSION_DENIED");
                }
            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ActivitySmsExpensesReader.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        System.out.println("onCreateLoader()");

        Uri smsUri = Uri.parse(SMS_URI);

        return new CursorLoader(this,
                smsUri,
                SMS_PROJECTION,
                "address = '900' and date > " + String.valueOf(millis),
                SMS_SELECTION_ARGS,
                SMS_SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        data.moveToFirst();
        formatSmsData(data);

        smsReaderRecyclerViewAdapter = new AdapterSmsReaderRecyclerView(smsDataList);
        smsReaderRecyclerViewAdapter.setClickListener((itemView, position) -> {
            chosenSms = smsDataList.get(position);
            chosenSmsPosition = position;

            String smsBodyString = smsDataList.get(position).getSmsBody();
            String[] smsBodyContent = smsBodyString.split(" ");

            String expenseValueString = "";
            String expenseNoteString = "";

            // Выражение для поиска суммы затрат
            String regex = "^\\d+(\\.\\d+)?[р][.]?";

            // Ищем строку со значением затрат
            int expenseValueIndex = -1;
            for (int i = 3; i < smsBodyContent.length - 2; ++i) {
                if (smsBodyContent[i].matches(regex)) {
                    expenseValueString = smsBodyContent[i];
                    expenseValueIndex = i;
                    break;
                }
            }

            // Если не удалось обнаружить сумму в предполагаемом месте строки -
            // ищем сумму во всей строке
            if (expenseValueIndex == -1) {
                for (int i = 0; i < smsBodyContent.length - 1; ++i) {
                    if (smsBodyContent[i].matches(regex)) {
                        expenseValueString = smsBodyContent[i];
                    }
                }
            }

            // Удаляем из полученной строки со знаяением все нецифровые символы
            if (expenseValueString.toLowerCase().contains("р"))
                expenseValueString = expenseValueString.substring(0, expenseValueString.indexOf("р"));


            // Получаем строку со сведениями о покупке
            if (expenseValueIndex != -1) {
                StringBuilder sb = new StringBuilder();
                for (int i = 3; i < expenseValueIndex; ++i) {
                    sb.append(smsBodyContent[i]);
                    sb.append(" ");
                }
                for (int i = expenseValueIndex + 1; i < smsBodyContent.length - 2; ++i) {
                    sb.append(smsBodyContent[i]);
                    sb.append(" ");
                }
                expenseNoteString = sb.toString().trim();
            }

            // Получаем из базы активные статьи расходов и делаем предположение,
            // к какому типу расходов относится сумма из выбранного сообщения
            DataUnitExpenses[] activeExpenseNamesArray = costsDB.getActiveCostNames_V3();
            List<DataUnitExpenses> activeExpenseNamesList = expenseNamesArrayToList(activeExpenseNamesArray, expenseNoteString);

            // Показываем диалоговое окно с полученной из SMS информацией
            DialogSmsReader smsReaderDialog = new DialogSmsReader(ActivitySmsExpensesReader.this,
                    smsDataList.get(position),
                    expenseValueString,
                    activeExpenseNamesList,
                    expenseNoteString);
            smsReaderDialog.show();

        });
        smsReaderRecyclerView.setAdapter(smsReaderRecyclerViewAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private List<DataUnitExpenses> expenseNamesArrayToList(DataUnitExpenses[] expenseNamesArray, String expenseNoteString) {
        List<DataUnitExpenses> activeExpenseNamesList = new ArrayList<>(expenseNamesArray.length);
        int expectedExpenseID = getExpectedID(expenseNoteString);

        for (int i = 0; i < expenseNamesArray.length; ++i) {
            if (expenseNamesArray[i].getExpenseId_N() == expectedExpenseID) {
                if (i != 0) {
                    DataUnitExpenses tempDataUnit = new DataUnitExpenses(expenseNamesArray[0]);
                    expenseNamesArray[0] = expenseNamesArray[i];
                    expenseNamesArray[i] = tempDataUnit;
                }
                break;
            }
        }
        activeExpenseNamesList = Arrays.asList(expenseNamesArray);

        return activeExpenseNamesList;
    }

    // Находим наиболее подходящую категорию расходов
    public int getExpectedID(String string) {
        int expectedID = -1;
        List<Integer> idList = new ArrayList<>();
        if (!string.isEmpty()) {
            String[] strArray = string.toLowerCase().split(" ");
            idList = smsNotesDB.getTest(strArray[0]);
            for (int i = 1; i < strArray.length; ++i) {
                List<Integer> currList = smsNotesDB.getTest(strArray[i]);
                if (currList.size() > 0 && currList.size() < idList.size())
                    idList = currList;
            }
        }

        int idFrequency = 0;
        List<Integer> viewedID_List = new ArrayList<>();

        for (int i = 0; i < idList.size(); ++i) {
            int currID = idList.get(i);
            int currID_Frequency = 0;

            if (!viewedID_List.contains(currID)) {
                for (int j = 0; j < idList.size(); ++j) {
                    if (idList.get(j) == currID)
                        ++currID_Frequency;
                }
                viewedID_List.add(currID);

                if (currID_Frequency >= idFrequency) {
                    expectedID = currID;
                    idFrequency = currID_Frequency;
                }
            }
        }

        return expectedID;
    }

    private void formatSmsData(Cursor c) {
        // Выбираем записи из базы за последние два дня
        lastEnteredValuesMillisecondsList = costsDB.getLastEnteredValuesByMilliseconds(millis);

        for (int i = 0; i < c.getCount(); ++i) {
            Long smsDateInMilliseconds = c.getLong(c.getColumnIndexOrThrow(SMS_DATE));
            if (lastEnteredValuesMillisecondsList.contains(smsDateInMilliseconds)) {
                c.moveToNext();
                continue;
            }

            String smsBodyString = c.getString(c.getColumnIndexOrThrow(SMS_BODY));
            if (smsBodyString.toLowerCase().contains("оплата") || smsBodyString.toLowerCase().contains("покупка")) {

                DataUnitSms smsDataUnit = new DataUnitSms();
                smsDataUnit.setSmsAddress(c.getString(c.getColumnIndexOrThrow(SMS_ADDRESS)));
                smsDataUnit.setSmsBody(c.getString(c.getColumnIndexOrThrow(SMS_BODY)));
                smsDataUnit.setSmsDateInMillis(c.getString(c.getColumnIndexOrThrow(SMS_DATE)));

                smsDataList.add(smsDataUnit);
            }
            c.moveToNext();
        }
    }


    public void returnToPreviousActivity() {
        Intent mainActivityWithFragmentsIntent = new Intent(ActivitySmsExpensesReader.this, ActivityMainWithFragments.class);
        mainActivityWithFragmentsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainActivityWithFragmentsIntent);
    }

    @Override
    public void getSmsReaderDialogResult(boolean savedInDB, String value) {
        Log.d(TAG, String.valueOf(savedInDB));

        if (savedInDB) {
            smsDataList.remove(chosenSmsPosition);
            smsReaderRecyclerViewAdapter.notifyItemRemoved(chosenSmsPosition);
            smsReaderRecyclerViewAdapter.notifyDataSetChanged();

            if (value != null) {
                Snackbar savedValueSnackbar = Snackbar.make(smsReaderRecyclerView, value + " руб. сохранено", Snackbar.LENGTH_SHORT);
                savedValueSnackbar.show();
            }
        }
    }
}
