package com.costs.newcosts;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class ActivitySmsExpensesReader extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

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
//    private DB_SmsNotes smsNotesDB;
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

//        getSupportLoaderManager().initLoader(LOADER_ID, null, this);


        // =========================================
        int permissionCheck = ContextCompat.checkSelfPermission(ActivitySmsExpensesReader.this, android.Manifest.permission.READ_SMS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "HERE");
            ActivityCompat.requestPermissions(ActivitySmsExpensesReader.this,
                                                new String[]{android.Manifest.permission.READ_SMS},
                                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            Log.d(TAG, "THERE");
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    public void returnToPreviousActivity() {
        Intent mainActivityWithFragmentsIntent = new Intent(ActivitySmsExpensesReader.this, ActivityMainWithFragments.class);
        mainActivityWithFragmentsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainActivityWithFragmentsIntent);
    }
}
