package com.costs.newcosts.common.sms.provider.providers.mkb;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import androidx.loader.content.CursorLoader;
import android.util.Log;

import com.costs.newcosts.common.sms.provider.SmsProvider;
import com.costs.newcosts.common.sms.reader_result.SmsExpenseReaderResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class MkbSmsProvider implements SmsProvider {
    private static final String TAG = "tag";
    private static final String SMS_URI = "content://sms/inbox";
    //    private static final String SMS_DATE = "date";
//    private static final String SMS_ADDRESS = "address";
//    private static final String SMS_BODY = "body";
    private static final String SMS_SORT_ORDER = "date DESC";
    private static final String[] SMS_PROJECTION = { "address, body, date" };
    private static final String[] SMS_SELECTION_ARGS = null;
    private Long millis;

    public MkbSmsProvider() {
        // Выбираем сообщения за последние два дня
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, 0);
        millis = calendar.getTimeInMillis();
    }

    @Override
    public CursorLoader cursorLoader(Context context) {
        Log.d(TAG, "MkbSmsProvider->cursorLoader()");

        Uri smsUri = Uri.parse(SMS_URI);

//        return null;
        return new CursorLoader(context,
                smsUri,
                SMS_PROJECTION,
                "address = '900' and date > " + String.valueOf(millis),
                SMS_SELECTION_ARGS,
                SMS_SORT_ORDER);
    }

    @Override
    public List<SmsExpenseReaderResult> processResult(Context context, Cursor data) {
        Log.d(TAG, "MkbSmsProvider->processResult()");

        return new ArrayList<>();
    }
}
