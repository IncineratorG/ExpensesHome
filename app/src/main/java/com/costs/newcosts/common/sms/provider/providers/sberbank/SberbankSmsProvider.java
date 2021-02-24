package com.costs.newcosts.common.sms.provider.providers.sberbank;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import androidx.loader.content.CursorLoader;
import android.util.Log;

import com.costs.newcosts.DB_Costs;
import com.costs.newcosts.DataUnitSms;
import com.costs.newcosts.common.sms.provider.SmsProvider;
import com.costs.newcosts.common.sms.reader_result.SmsExpenseReaderResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class SberbankSmsProvider implements SmsProvider {
    private static final String TAG = "tag";
    private static final String SMS_URI = "content://sms/inbox";
    private static final String SMS_DATE = "date";
    private static final String SMS_ADDRESS = "address";
    private static final String SMS_BODY = "body";
    private static final String SMS_SORT_ORDER = "date DESC";
    private static final String[] SMS_PROJECTION = { "address, body, date" };
    private static final String[] SMS_SELECTION_ARGS = null;
    private Long millis;

    public SberbankSmsProvider() {
        // Выбираем сообщения за последние два дня
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, -3);
        millis = calendar.getTimeInMillis();
    }

    @Override
    public CursorLoader cursorLoader(Context context) {
        Log.d(TAG, "SberbankSmsProvider->cursorLoader()");

        Uri smsUri = Uri.parse(SMS_URI);

        return new CursorLoader(context,
                smsUri,
                SMS_PROJECTION,
                "address = '900' and date > " + String.valueOf(millis),
                SMS_SELECTION_ARGS,
                SMS_SORT_ORDER);
    }

    @Override
    public List<SmsExpenseReaderResult> processResult(Context context, Cursor data) {
        Log.d(TAG, "SberbankSmsProvider->processResult()");

        data.moveToFirst();

        List<DataUnitSms> smsDataList = formatSmsData(context, data);
        List<SmsExpenseReaderResult> smsExpenseValues = extractValuesFromSms(context, smsDataList);

        return smsExpenseValues;
    }

    private List<DataUnitSms> formatSmsData(Context context, Cursor c) {
        // Выбираем записи из базы за последние два дня
        List<Long> lastEnteredValuesMillisecondsList = DB_Costs
                .getInstance(context)
                .getLastEnteredValuesByMilliseconds(millis);

        List<DataUnitSms> smsDataList = new ArrayList<>();

//        Log.d(TAG, "SberbankSmsProvider->formatSmsData(): " + String.valueOf(c.getCount()));
//        for (int i = 0; i < c.getCount(); ++i) {
//            try {
//                int columnIndex = c.getColumnIndex(SMS_DATE);
//                Log.d(TAG, "COLUMN_INDEX: " + String.valueOf(columnIndex));
//
//                long value = c.getLong(columnIndex);
//                Log.d(TAG, "COLUMN_VALUE: " + String.valueOf(value));
//
////                Long smsDateInMilliseconds = c.getLong(c.getColumnIndexOrThrow(SMS_DATE));
////                Log.d(TAG, "smsDateInMilliseconds: " + String.valueOf(smsDateInMilliseconds));
//            } catch (Exception e) {
//                Log.d(TAG, "ERROR: " + e.getMessage());
//            }
//        }

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

        return smsDataList;
    }

    private List<SmsExpenseReaderResult> extractValuesFromSms(Context context, List<DataUnitSms> smsDataList) {
        List<SmsExpenseReaderResult> smsValues = new ArrayList<>();

//        DB_Costs costsDB = DB_Costs.getInstance(context);

        for (int i = 0; i < smsDataList.size(); ++i) {
            DataUnitSms chosenSms = smsDataList.get(i);

            String smsBodyString = smsDataList.get(i).getSmsBody();
            String[] smsBodyContent = smsBodyString.split(" ");

            String expenseValueString = "";
            String expenseNoteString = "";

            // Выражение для поиска суммы затрат
            String regex = "^\\d+(\\.\\d+)?[р][.]?";

            // Ищем строку со значением затрат
            int expenseValueIndex = -1;
            for (int j = 3; j < smsBodyContent.length - 2; ++j) {
                if (smsBodyContent[j].matches(regex)) {
                    expenseValueString = smsBodyContent[j];
                    expenseValueIndex = j;
                    break;
                }
            }

            // Если не удалось обнаружить сумму в предполагаемом месте строки -
            // ищем сумму во всей строке
            if (expenseValueIndex == -1) {
                for (int j = 0; j < smsBodyContent.length - 1; ++j) {
                    if (smsBodyContent[j].matches(regex)) {
                        expenseValueString = smsBodyContent[j];
                    }
                }
            }

            // Удаляем из полученной строки со значением все нецифровые символы
            if (expenseValueString.toLowerCase().contains("р"))
                expenseValueString = expenseValueString.substring(0, expenseValueString.indexOf("р"));


            // Получаем строку со сведениями о покупке
            if (expenseValueIndex != -1) {
                StringBuilder sb = new StringBuilder();
                for (int j = 3; j < expenseValueIndex; ++j) {
                    sb.append(smsBodyContent[j]);
                    sb.append(" ");
                }
                for (int j = expenseValueIndex + 1; j < smsBodyContent.length - 2; ++j) {
                    sb.append(smsBodyContent[j]);
                    sb.append(" ");
                }
                expenseNoteString = sb.toString().trim();
            }

            smsValues.add(new SmsExpenseReaderResult(expenseValueString, expenseNoteString, chosenSms));
        }

        return smsValues;
    }
}
