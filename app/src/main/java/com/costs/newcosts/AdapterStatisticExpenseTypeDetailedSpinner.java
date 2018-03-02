package com.costs.newcosts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class AdapterStatisticExpenseTypeDetailedSpinner extends BaseAdapter implements SpinnerAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<String> sortingTypesList;
    private List<Integer> arrowImagesList;

    public AdapterStatisticExpenseTypeDetailedSpinner(Context context) {
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        sortingTypesList = new ArrayList<>(4);
        sortingTypesList.add(context.getResources().getString(R.string.sortingTypesList_byDate_string));
        sortingTypesList.add(context.getResources().getString(R.string.sortingTypesList_byDate_string));
        sortingTypesList.add(context.getResources().getString(R.string.sortingTypesList_byDailySum_string));
        sortingTypesList.add(context.getResources().getString(R.string.sortingTypesList_byDailySum_string));

        arrowImagesList = new ArrayList<>(2);
        arrowImagesList.add(R.drawable.ic_arrow_upward_black_18dp);
        arrowImagesList.add(R.drawable.ic_arrow_downward_black_18dp);
    }

    @Override
    public int getCount() {
        return sortingTypesList.size();
    }

    @Override
    public Object getItem(int position) {
        return sortingTypesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.activity_statistic_expense_type_detailed_spinner_single_item, parent, false);

        TextView sortingTypeTextView = (TextView) view.findViewById(R.id.expense_type_detailed_spinner_single_item_textview);
        sortingTypeTextView.setText(sortingTypesList.get(position));

        ImageView arrowImageView = (ImageView) view.findViewById(R.id.expense_type_detailed_spinner_single_item_imageview);
        arrowImageView.setImageResource(arrowImagesList.get(position % 2));

        return view;
    }
}
