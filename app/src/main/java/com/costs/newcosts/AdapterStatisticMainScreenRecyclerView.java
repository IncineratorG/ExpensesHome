package com.costs.newcosts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class AdapterStatisticMainScreenRecyclerView extends RecyclerView.Adapter<AdapterStatisticMainScreenRecyclerView.FragmentStatisticMainScreenViewHolder> {
    private AdapterLastEnteredValuesRecyclerView.OnItemClickListener clickListener;
    private List<DataUnitExpenses> data;
    private Context context;
    private Calendar calendar;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }


    public AdapterStatisticMainScreenRecyclerView(List<DataUnitExpenses> data, Context context) {
        this.data = data;
        this.context = context;
        calendar = new GregorianCalendar();
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public FragmentStatisticMainScreenViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_statistic_main_screen_single_item, parent, false);
        return new FragmentStatisticMainScreenViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FragmentStatisticMainScreenViewHolder holder, int position) {
        // Группируем список последних введённых значений по году
        if (position > 0 && data.get(position - 1).getYear() == data.get(position).getYear()) {
            holder.dateLayout.setVisibility(View.GONE);
        }
        else {
            calculateTotalExpenseValuesForYear(holder, position);
        }

        holder.expensesMonthTextView.setText(Constants.MONTH_NAMES[data.get(position).getMonth()]);
        holder.expensesValueTextView.setText(data.get(position).getExpenseValueString() + " " +
                                                    context.getResources().getString(R.string.rur_string) +
                                                    context.getResources().getString(R.string.dot_sign_string));
    }

    // Получаем величину расходов за один год
    private void calculateTotalExpenseValuesForYear(FragmentStatisticMainScreenViewHolder holder, int position) {
        holder.dateLayout.setVisibility(View.VISIBLE);
        holder.yearTextView.setText(data.get(position).getYear() + context.getResources().getString(R.string.asmsrv_yearTextView_string));

        double overallExpenseValueForYear = data.get(position).getExpenseValueDouble();
        int i = position + 1;
        int count = 1;

        // Получаем сумму расходов за текущий месяц
        double expensesForCurrentMonth = 0.0;
        boolean hasExpensesForCurrentMonth = false;
        if (data.get(position).getYear() == (int) calendar.get(Calendar.YEAR) &&
                data.get(position).getMonth() == (int) calendar.get(Calendar.MONTH))
        {
            expensesForCurrentMonth = data.get(position).getExpenseValueDouble();
            hasExpensesForCurrentMonth = true;
        }

        // Получаем сумму расходов за все месяцы в году
        while (i < data.size() && data.get(i).getYear() == data.get(i - 1).getYear())
        {
            overallExpenseValueForYear = overallExpenseValueForYear + data.get(i).getExpenseValueDouble();
            ++i;
            count = count + 1;
        }

        // Вычитаем сумму расходов за текущий месяц из общей суммы расходов за текущий год
        if (hasExpensesForCurrentMonth)
            count = count - 1;

        // Получаем величину расходов в месяц
        double expenseValuePerMonth = 0.0;
        if (count == 0)
            expenseValuePerMonth = expensesForCurrentMonth;
        else
            expenseValuePerMonth = (overallExpenseValueForYear - expensesForCurrentMonth) / count +
                    expensesForCurrentMonth / calendar.getActualMaximum(Calendar.DAY_OF_MONTH);



        holder.perMonthValueTextView.setText(Constants.formatDigit(expenseValuePerMonth) + " " +
                context.getResources().getString(R.string.rur_string) +
                context.getResources().getString(R.string.dot_sign_string) + "/" +
                context.getResources().getString(R.string.asmsrv_perMonthValueTextView_string) +
                context.getResources().getString(R.string.dot_sign_string));
        holder.yearValueTextView.setText(Constants.formatDigit(overallExpenseValueForYear) + " " +
                context.getResources().getString(R.string.rur_string) +
                context.getResources().getString(R.string.dot_sign_string));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setClickListener(AdapterLastEnteredValuesRecyclerView.OnItemClickListener listener) {
        this.clickListener = listener;
    }



    // ===================================== View Holder ===========================================
    public class FragmentStatisticMainScreenViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LinearLayout dateLayout;
        private TextView expensesMonthTextView;
        private TextView expensesValueTextView;
        private TextView yearTextView;
        private TextView yearValueTextView;
        private TextView perMonthValueTextView;


        public FragmentStatisticMainScreenViewHolder(View itemView) {
            super(itemView);

            expensesMonthTextView = (TextView) itemView.findViewById(R.id.fragment_statistic_main_screen_month_textview);
            expensesValueTextView = (TextView) itemView.findViewById(R.id.fragment_statistic_main_screen_expenses_value_textview);
            yearTextView = (TextView) itemView.findViewById(R.id.fragment_statistic_main_screen_year_textview);
            yearValueTextView = (TextView) itemView.findViewById(R.id.fragment_statistic_main_screen_year_value_textview);
            perMonthValueTextView = (TextView) itemView.findViewById(R.id.fragment_statistic_main_screen_per_month_value_textview);

            dateLayout = (LinearLayout) itemView.findViewById(R.id.fragment_statistic_main_screen_date_layout);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null)
                clickListener.onItemClick(v, getAdapterPosition());
        }

    }
}
