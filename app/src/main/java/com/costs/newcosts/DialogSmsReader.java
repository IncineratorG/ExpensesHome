package com.costs.newcosts;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class DialogSmsReader extends Dialog {

    private Context context;

    private EditText expenseValueEditText;
    private EditText expenseNoteEditText;
    private Button okButton;
    private Button cancelButton;
    private TextView smsBodyTextView;
    private Spinner expenseNamesSpinner;

    private DataUnitSms sms;
    private String expenseValueString;
    private List<DataUnitExpenses> activeExpenseNamesList;
    private DataUnitExpenses selectedExpenseNameDataUnit;
    private String noteString;
    private Toast wrongValueToast;


    DialogSmsReaderCallback Callback;
    public interface DialogSmsReaderCallback {
        void getSmsReaderDialogResult(boolean savedInDB, String value);
    }


    public DialogSmsReader(Context context, DataUnitSms sms,
                           String expenseValueString,
                           List<DataUnitExpenses> activeExpenseNamesList,
                           String noteString)
    {
        super(context);
        this.context = context;
        this.sms = sms;
        this.expenseValueString = expenseValueString;
        this.activeExpenseNamesList = activeExpenseNamesList;
        this.noteString = noteString;
        Callback = (DialogSmsReaderCallback) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sms_reader_dialog);


        expenseNamesSpinner = (Spinner) findViewById(R.id.sms_reader_dialog_expenses_spinner);
        AdapterSmsReaderDialogSpinner spinnerAdapter = new AdapterSmsReaderDialogSpinner(context, activeExpenseNamesList);
        expenseNamesSpinner.setAdapter(spinnerAdapter);
        expenseNamesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedExpenseNameDataUnit = (DataUnitExpenses) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        expenseValueEditText = (EditText) findViewById(R.id.sms_reader_dialog_value_edittext);
        expenseValueEditText.setText(expenseValueString);
        expenseValueEditText.setSelection(expenseValueEditText.getText().length());

        expenseNoteEditText = (EditText) findViewById(R.id.sms_reader_dialog_expense_note_edittext);

        smsBodyTextView = (TextView) findViewById(R.id.sms_reader_dialog_sms_body_textview);
        smsBodyTextView.setText(sms.getSmsBody());

        okButton = (Button) findViewById(R.id.sms_reader_dialog_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!expenseValueEditText.getText().toString().isEmpty()) {
                    DB_Costs cdb = DB_Costs.getInstance(context);
                    DB_SmsNotes smsNotesDB = DB_SmsNotes.getInstance(context);

                    cdb.addCostInMilliseconds(selectedExpenseNameDataUnit.getExpenseId_N(),
                            expenseValueEditText.getText().toString(),
                            sms.getSmsDateInMillisLong(),
                            expenseNoteEditText.getText().toString());

                    smsNotesDB.addNote(selectedExpenseNameDataUnit.getExpenseId_N(), noteString.toLowerCase());

                    Callback.getSmsReaderDialogResult(true, expenseValueEditText.getText().toString());
                    dismiss();
                } else {
                    wrongValueToast = Toast.makeText(context, "Введите сумму", Toast.LENGTH_SHORT);
                    wrongValueToast.setGravity(Gravity.CENTER, 0, 0);
                    wrongValueToast.show();
                }
            }
        });

        cancelButton = (Button) findViewById(R.id.sms_reader_dialog_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wrongValueToast != null)
                    wrongValueToast.cancel();
                Callback.getSmsReaderDialogResult(false, null);
                dismiss();
            }
        });



    }

}
