package com.costs.newcosts.common.sms.provider;

import android.content.Context;
import android.database.Cursor;
import androidx.loader.content.CursorLoader;

import com.costs.newcosts.common.sms.reader_result.SmsExpenseReaderResult;

import java.util.List;

public interface SmsProvider {
    CursorLoader cursorLoader(Context context);
    List<SmsExpenseReaderResult> processResult(Context context, final Cursor data);
}
