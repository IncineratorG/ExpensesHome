package com.costs.newcosts;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * TODO: Add a class header comment
 */

public class DialogFragmentEditExpenseName extends DialogFragment implements View.OnClickListener {
    private static DataUnitExpenses dataUnit;

    static DialogFragmentEditExpenseName newInstance(DataUnitExpenses data) {
        dataUnit = data;
        return new DialogFragmentEditExpenseName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_cost_value_dialog, container, false);

        TextView categoryTextView = (TextView) v.findViewById(R.id.edit_cost_value_dialog_costDate);
        categoryTextView.setText(getResources().getString(R.string.dfeen_categoryTextView_string));
        TextView costNameTextView = (TextView) v.findViewById(R.id.edit_cost_value_dialog_costName);
        costNameTextView.setText(dataUnit.getExpenseName());
        TextView costValueTextView = (TextView) v.findViewById(R.id.edit_cost_value_dialog_costValue);
        costValueTextView.setVisibility(View.GONE);
        TextView costNoteTextView = (TextView) v.findViewById(R.id.edit_cost_value_dialog_costNote);
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
        Intent data = new Intent().putExtra(Constants.EXPENSE_DATA_UNIT_LABEL, dataUnit);

        switch (v.getId()) {
            case R.id.edit_cost_value_dialog_editButton:
                getTargetFragment().onActivityResult(Constants.EDIT_EXPENSE_NAME_REQUEST_CODE, Constants.EDIT_ITEM, data);
                dismiss();
                break;
            case R.id.edit_cost_value_dialog_deleteButton:
                getTargetFragment().onActivityResult(Constants.EDIT_EXPENSE_NAME_REQUEST_CODE, Constants.DELETE_ITEM, data);
                dismiss();
                break;
            case R.id.edit_cost_value_dialog_cancelButton:
                dismiss();
                break;
            default:
                break;
        }
    }
}
