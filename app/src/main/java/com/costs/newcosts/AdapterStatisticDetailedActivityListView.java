package com.costs.newcosts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


class AdapterStatisticDetailedActivityListView extends ArrayAdapter<DataUnitExpenses> {
    private Context context;

    public AdapterStatisticDetailedActivityListView(Context context, List<DataUnitExpenses> dataUnitList) {
        super(context, R.layout.activity_statistic_detailed_single_item, dataUnitList);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View singleCostsRow = inflater.inflate(R.layout.activity_statistic_detailed_single_item, parent, false);

        DataUnitExpenses dataUnit = getItem(position);

        TextView expenseNameTextView = (TextView) singleCostsRow.findViewById(R.id.activity_statistic_detailed_single_item_expense_name_textview);
        TextView expenseValueTextView = (TextView) singleCostsRow.findViewById(R.id.activity_statistic_detailed_single_item_expense_value_textview);

        expenseNameTextView.setText(dataUnit.getExpenseName());
        expenseValueTextView.setText(dataUnit.getExpenseValueString() + " " +
                                        context.getResources().getString(R.string.rur_string) +
                                        context.getResources().getString(R.string.dot_sign_string));

        return singleCostsRow;
    }

}
