package com.costs.newcosts;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

/**
 * TODO: Add a class header comment
 */

public class DialogFragmentEditLastEnteredValues extends DialogFragment implements View.OnClickListener {
    private static DataUnitExpenses dataUnit;

    static DialogFragmentEditLastEnteredValues newInstance(DataUnitExpenses data) {
        dataUnit = data;
        return new DialogFragmentEditLastEnteredValues();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_cost_value_dialog, container, false);

        Long milliseconds = dataUnit.getMilliseconds();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        TextView costValueTextView = (TextView) v.findViewById(R.id.edit_cost_value_dialog_costValue);
        costValueTextView.setText(dataUnit.getExpenseValueString() + " " +
                                    getResources().getString(R.string.rur_string) +
                                    getResources().getString(R.string.dot_sign_string));
        TextView costNameTextView = (TextView) v.findViewById(R.id.edit_cost_value_dialog_costName);
        costNameTextView.setText(dataUnit.getExpenseName());
        TextView costDateTextView = (TextView) v.findViewById(R.id.edit_cost_value_dialog_costDate);
        costDateTextView.setText(Constants.DAY_NAMES[calendar.get(Calendar.DAY_OF_WEEK)] + ", " +
                String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + " " +
                Constants.DECLENSION_MONTH_NAMES[calendar.get(Calendar.MONTH)] + ", " +
                String.valueOf(calendar.get(Calendar.YEAR)));
        TextView costNoteTextView = (TextView) v.findViewById(R.id.edit_cost_value_dialog_costNote);
        if (dataUnit.HAS_NOTE)
            costNoteTextView.setText(dataUnit.getExpenseNoteString());
        else
            costNoteTextView.setVisibility(View.GONE);

        Button editButton = (Button) v.findViewById(R.id.edit_cost_value_dialog_editButton);
        editButton.setOnClickListener(this);
        Button deleteButton = (Button) v.findViewById(R.id.edit_cost_value_dialog_deleteButton);
        deleteButton.setOnClickListener(this);
        Button cancelButton = (Button) v.findViewById(R.id.edit_cost_value_dialog_cancelButton);
        cancelButton.setOnClickListener(this);


        return v;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_cost_value_dialog_editButton:
                getTargetFragment().onActivityResult(Constants.EDIT_EXPENSE_RECORD_DIALOG_REQUEST_CODE, Constants.EDIT_ITEM, null);
                dismiss();
                break;
            case R.id.edit_cost_value_dialog_deleteButton:
                getTargetFragment().onActivityResult(Constants.EDIT_EXPENSE_RECORD_DIALOG_REQUEST_CODE, Constants.DELETE_ITEM, null);
                dismiss();
                break;
            case R.id.edit_cost_value_dialog_cancelButton:
                dismiss();
                break;
            default:
                break;
        }
    }




//    @Override
//    public void onResume() {
//        super.onResume();
//
//        Window window = getDialog().getWindow();
//
//        // set "origin" to top left corner
//        window.setGravity(Gravity.TOP| Gravity.START);
//
//        WindowManager.LayoutParams params = window.getAttributes();
//
//        // Just an example; edit to suit your needs.
//        params.x = x;
//        params.y = y - height;
//        System.out.println(height);
//        params.width = WindowManager.LayoutParams.MATCH_PARENT;
//
//        window.setAttributes(params);
//
//    }
//
//    public int dpToPx(float valueInDp) {
//        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
//    }
}
