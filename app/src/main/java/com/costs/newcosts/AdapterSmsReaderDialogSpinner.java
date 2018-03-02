package com.costs.newcosts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class AdapterSmsReaderDialogSpinner extends BaseAdapter implements SpinnerAdapter {

    private Context context;
    private List<DataUnitExpenses> dataUnitList;
    private LayoutInflater inflater;


    public AdapterSmsReaderDialogSpinner(Context context, List<DataUnitExpenses> dataUnitList) {
        this.context = context;
        this.dataUnitList = dataUnitList;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return dataUnitList == null ? 0 : dataUnitList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataUnitList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long) dataUnitList.get(position).getExpenseId_N();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.sms_reader_dialog_spinner_single_item, parent, false);

        TextView expenseNameTextView = (TextView) view.findViewById(R.id.spinner_single_item_textview);
        expenseNameTextView.setText(dataUnitList.get(position).getExpenseName());

        return view;
    }
}
