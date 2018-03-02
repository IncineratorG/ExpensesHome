package com.costs.newcosts;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * TODO: Add a class header comment
 */

public class DialogFragmentExpensesList extends DialogFragment {
    static DataUnitExpenses[] data;

    public interface ExpenseListDialogCallback {
        void getSelectedExpense(DataUnitExpenses dataUnit);
    }

    public ExpenseListDialogCallback callback;

    static DialogFragmentExpensesList newInstance(DataUnitExpenses[] d) {
        data = d;
        return new DialogFragmentExpensesList();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (ExpenseListDialogCallback) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.expenses_list_dialog, container, false);

        final ListView expensesListView = (ListView) v.findViewById(R.id.expenses_list_dialog_listview);

        AdapterExpensesListDialogListView adapterExpensesListDialogListView = new AdapterExpensesListDialogListView(getActivity(), data);
        expensesListView.setAdapter(adapterExpensesListDialogListView);
        expensesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                callback.getSelectedExpense(data[position]);
                dismiss();
            }
        });

        return v;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        return dialog;
    }
}
