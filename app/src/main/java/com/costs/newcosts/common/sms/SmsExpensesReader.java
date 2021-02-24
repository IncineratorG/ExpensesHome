package com.costs.newcosts.common.sms;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.costs.newcosts.common.sms.provider.SmsProvider;
import com.costs.newcosts.common.sms.reader_callbacks.SmsExpenseReaderResultCallback;
import com.costs.newcosts.common.sms.reader_result.SmsExpenseReaderResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmsExpensesReader implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "tag";

    private Context mContext;
    private List<SmsProvider> mSmsProviders;
    private Map<Integer, SmsProvider> mSmsProvidersMap;
    private SmsExpenseReaderResultCallback mResultCallback;
    private int mActiveCursors;
    private List<SmsExpenseReaderResult> mSmsValues;

    public SmsExpensesReader(Context context,
                             List<SmsProvider> smsProviders,
                             SmsExpenseReaderResultCallback onResult) {
        mContext = context;
        mSmsProviders = smsProviders == null ? new ArrayList<>() : smsProviders;
        mResultCallback = onResult == null ? (result -> {}) : onResult;
        mActiveCursors = 0;

        mSmsValues = new ArrayList<>();
        mSmsProvidersMap = new HashMap<>();

        for (int i = 0; i < mSmsProviders.size(); ++i) {
            mSmsProvidersMap.put(i + 5, mSmsProviders.get(i));
        }
    }

    public void read(LoaderManager loaderManager) {
        mActiveCursors = 0;
        mSmsValues = new ArrayList<>();

        List<Integer> smsProvidersIds = new ArrayList<>(mSmsProvidersMap.keySet());
        for (int i = 0; i < smsProvidersIds.size(); ++i) {
            loaderManager.initLoader(smsProvidersIds.get(i), null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "SmsExpensesReader->onCreateLoader()");

        if (mSmsProvidersMap.containsKey(id)) {
            CursorLoader cursorLoader = mSmsProvidersMap.get(id).cursorLoader(mContext);
            if (cursorLoader != null) {
                ++mActiveCursors;
            }

            return cursorLoader;
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "SmsExpensesReader->onLoadFinished()");

        if (mSmsProvidersMap.containsKey(loader.getId())) {
            List<SmsExpenseReaderResult> providerResult = mSmsProvidersMap
                    .get(loader.getId())
                    .processResult(mContext, data);

            mSmsValues.addAll(providerResult);

            --mActiveCursors;
        }

        if (mActiveCursors == 0) {
            mResultCallback.onResult(mSmsValues);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "SmsExpensesReader->onLoaderReset()");
    }
}
