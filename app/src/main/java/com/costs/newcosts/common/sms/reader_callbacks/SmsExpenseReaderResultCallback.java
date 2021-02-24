package com.costs.newcosts.common.sms.reader_callbacks;

import com.costs.newcosts.common.sms.reader_result.SmsExpenseReaderResult;

import java.util.List;

public interface SmsExpenseReaderResultCallback {
    void onResult(List<SmsExpenseReaderResult> results);
}
