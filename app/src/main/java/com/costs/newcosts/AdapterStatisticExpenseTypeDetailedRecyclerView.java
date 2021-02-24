package com.costs.newcosts;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class AdapterStatisticExpenseTypeDetailedRecyclerView extends RecyclerView.Adapter<AdapterStatisticExpenseTypeDetailedRecyclerView.StatisticExpenseTypeDetailedViewHolder> {
    private AdapterCurrentMonthScreenRecyclerView.OnItemClickListener clickListener;
    private List<DataUnitExpenses> data;
    private Context context;
    private Calendar calendar;


    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }



    public AdapterStatisticExpenseTypeDetailedRecyclerView(List<DataUnitExpenses> data, Context context) {
        this.data = data;
        this.context = context;
        calendar = Calendar.getInstance();
    }

    public void swapData(List<DataUnitExpenses> newDataList) {
        data = newDataList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public StatisticExpenseTypeDetailedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_statistic_expense_type_detailed_single_item, parent, false);
        return new StatisticExpenseTypeDetailedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(StatisticExpenseTypeDetailedViewHolder holder, int position) {
        // Группируем список последних введённых значений по дате занесения элементов в базу
        if (position > 0 && (data.get(position - 1).getDay() == data.get(position).getDay() &&
                data.get(position - 1).getMonth() == data.get(position).getMonth() &&
                data.get(position - 1).getYear() == data.get(position).getYear()))
        {
            holder.dateLayout.setVisibility(View.GONE);
        }
        else
        {
            // Отображаем дату
            calendar.setTimeInMillis(data.get(position).getMilliseconds());
            holder.dateLayout.setVisibility(View.VISIBLE);
            holder.dateTextView.setText(new StringBuilder()
                    .append(Constants.DAY_NAMES[calendar.get(Calendar.DAY_OF_WEEK)])
                    .append(", ")
                    .append(data.get(position).getDay())
                    .append(" ")
                    .append(Constants.SHORT_MONTH_NAMES[data.get(position).getMonth()])
                    .append(" ")
                    .append(data.get(position).getYear()));

            // Получаем величину расходов за один день
            double overallExpenseValueForDay = data.get(position).getExpenseValueDouble();
            int i = position + 1;
            while (i < data.size()
                    && data.get(i).getDay() == data.get(i - 1).getDay()
                    && data.get(i).getMonth() == data.get(i - 1).getMonth()
                    && data.get(i).getYear() == data.get(i - 1).getYear())
            {
                overallExpenseValueForDay = overallExpenseValueForDay + data.get(i).getExpenseValueDouble();
                ++i;
            }

            holder.dateOverallValueTextView.setText(Constants.formatDigit(overallExpenseValueForDay) + " " +
                                                    context.getResources().getString(R.string.rur_string) +
                                                    context.getResources().getString(R.string.dot_sign_string));
        }

        holder.expensesValueTextView.setText(data.get(position).getExpenseValueString() + " " +
                                                context.getResources().getString(R.string.rur_string) +
                                                context.getResources().getString(R.string.dot_sign_string));
        holder.expensesNoteTextView.setVisibility(View.VISIBLE);
        holder.noteSeparatorLayout.setVisibility(View.VISIBLE);;

        // Если у элемента нет заметки - убираем соответсвующее поле
        if (!data.get(position).getExpenseNoteString().equals("")) {
            holder.expensesNoteTextView.setVisibility(View.VISIBLE);
            holder.noteSeparatorLayout.setVisibility(View.VISIBLE);
            holder.expensesNoteTextView.setText(data.get(position).getExpenseNoteString());
        } else {
            holder.expensesNoteTextView.setVisibility(View.GONE);
            holder.noteSeparatorLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public void setClickListener(AdapterCurrentMonthScreenRecyclerView.OnItemClickListener listener) {
        this.clickListener = listener;
    }

    // ===================================== View Holder ===========================================
    public class StatisticExpenseTypeDetailedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LinearLayout noteSeparatorLayout;
        private LinearLayout dateLayout;
        private TextView expensesValueTextView;
        private TextView expensesNoteTextView;
        private TextView dateTextView;
        private TextView dateOverallValueTextView;


        public StatisticExpenseTypeDetailedViewHolder(View itemView) {
            super(itemView);

            expensesValueTextView = (TextView) itemView.findViewById(R.id.expense_type_detailed_single_item_expenses_value_textview);
            expensesNoteTextView = (TextView) itemView.findViewById(R.id.expense_type_detailed_single_item_expenses_note_textview);
            dateTextView = (TextView) itemView.findViewById(R.id.expense_type_detailed_single_item_date_textview);
            dateOverallValueTextView = (TextView) itemView.findViewById(R.id.expense_type_detailed_single_item_date_overall_value_textview);

            noteSeparatorLayout = (LinearLayout) itemView.findViewById(R.id.expense_type_detailed_single_item_horizontal_note_separator);
            dateLayout = (LinearLayout) itemView.findViewById(R.id.expense_type_detailed_single_item_date_layout);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null)
                clickListener.onItemClick(v, getAdapterPosition());
        }

    }
}
