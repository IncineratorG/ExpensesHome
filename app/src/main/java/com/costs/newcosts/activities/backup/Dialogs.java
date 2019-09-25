package com.costs.newcosts.activities.backup;

import android.app.AlertDialog;

/**
 * TODO: Add a class header comment
 */
public class Dialogs {
    private static Dialogs mInstance = null;

    public static final int MainDialog = 1;


    public Dialogs() {

    }

    public static synchronized Dialogs getInstance() {
        if (mInstance != null)
            return mInstance;
        else {
            mInstance = new Dialogs();
            return mInstance;
        }
    }

    public AlertDialog getDialog(int type) {
        switch (type) {
            case MainDialog: {
                return createMainDialog();
            }
        }

        return null;
    }

    private AlertDialog createMainDialog() {


        return null;
    }
}
