package com.costs.newcosts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class ActivitySmsExpensesReader extends AppCompatActivity {

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
        arrowBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToPreviousActivity();
            }
        });
    }

    public void returnToPreviousActivity() {
        Intent mainActivityWithFragmentsIntent = new Intent(ActivitySmsExpensesReader.this, ActivityMainWithFragments.class);
        mainActivityWithFragmentsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainActivityWithFragmentsIntent);
    }
}
