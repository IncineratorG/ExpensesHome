package com.costs.newcosts.common.sms.reader_result;

import com.costs.newcosts.DataUnitSms;

public class SmsExpenseReaderResult {
    private String mExpenseValue;
    private String mExpenseNote;
    private DataUnitSms mSmsData;

    public SmsExpenseReaderResult(String expenseValue, String expenseNote, DataUnitSms sms) {
        mExpenseValue = expenseValue;
        mExpenseNote = expenseNote;
        mSmsData = sms;
    }

    public String expenseValue() {
        return mExpenseValue;
    }

    public String expenseNote() {
        return mExpenseNote;
    }

    public DataUnitSms smsData() {
        return mSmsData;
    }
}
