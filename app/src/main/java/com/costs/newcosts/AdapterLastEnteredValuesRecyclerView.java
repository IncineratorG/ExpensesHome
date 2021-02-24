package com.costs.newcosts;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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

public class AdapterLastEnteredValuesRecyclerView extends RecyclerView.Adapter<AdapterLastEnteredValuesRecyclerView.FragmentLastEnteredValuesViewHolder> {

    private OnItemClickListener clickListener;
    private List<DataUnitExpenses> data;
    private Context context;
    private Calendar calendar;

    private int currentDay;
    private int currentMonth;
    private int currentYear;

    private int yesterdayDay;
    private int yesterdayMonth;
    private int yesterdayYear;

    private long millisecondsOfItemToAnimate = -1;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }


    public AdapterLastEnteredValuesRecyclerView(List<DataUnitExpenses> data, Context context, long millisecondsOfItemToAnimate) {
        this.data = data;
        this.context = context;
        calendar = Calendar.getInstance();

        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        currentMonth = calendar.get(Calendar.MONTH);
        currentYear = calendar.get(Calendar.YEAR);

        calendar.add(Calendar.DAY_OF_MONTH, -1);

        yesterdayDay = calendar.get(Calendar.DAY_OF_MONTH);
        yesterdayMonth = calendar.get(Calendar.MONTH);
        yesterdayYear = calendar.get(Calendar.YEAR);

        this.millisecondsOfItemToAnimate = millisecondsOfItemToAnimate;
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public FragmentLastEnteredValuesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_last_entered_values_screen_single_item, parent, false);
        return new FragmentLastEnteredValuesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FragmentLastEnteredValuesViewHolder holder, int position) {
        // Группируем список последних введённых значений по дате занесения элементов в базу
        setHolderDateTextView(holder, position);

        holder.expensesTypeTextView.setText(data.get(position).getExpenseName());
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

        // Анимируем элемент, который мы редактировали
        if (millisecondsOfItemToAnimate != -1 &&
                data.get(position).getMilliseconds() == millisecondsOfItemToAnimate)
        {
            ObjectAnimator animation1 = ObjectAnimator.ofFloat(holder.itemContainerLayout, "translationX", 0f, 50f);
            ObjectAnimator animation2 = ObjectAnimator.ofFloat(holder.itemContainerLayout, "translationX", 50f, -50f);
            ObjectAnimator animation3 = ObjectAnimator.ofFloat(holder.itemContainerLayout, "translationX", -50f, 0f);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setStartDelay(800);
            animatorSet.playSequentially(animation1, animation2, animation3);
            animatorSet.setDuration(100);
            animatorSet.start();

            millisecondsOfItemToAnimate = -1;
        }
    }

    // Группирует список последних введённых значений по дате внесения
    private void setHolderDateTextView(FragmentLastEnteredValuesViewHolder holder, int position) {
        // Если текущая дата совпадает с предыдущей - скрываем поле с датой
        if (position > 0 && (data.get(position - 1).getDay() == data.get(position).getDay() &&
                data.get(position - 1).getMonth() == data.get(position).getMonth() &&
                data.get(position - 1).getYear() == data.get(position).getYear()))
        {
            holder.dateLayout.setVisibility(View.GONE);
        }
        // Иначе - выводим на экран дату занесения расхода
        else
        {
            calendar.setTimeInMillis(data.get(position).getMilliseconds());
            holder.dateLayout.setVisibility(View.VISIBLE);

            if (currentDay == calendar.get(Calendar.DAY_OF_MONTH) && currentMonth == calendar.get(Calendar.MONTH) && currentYear == calendar.get(Calendar.YEAR)) {
                holder.dateTextView.setText(context.getResources().getString(R.string.flev_today_string));
            } else if (yesterdayDay == calendar.get(Calendar.DAY_OF_MONTH) && yesterdayMonth == calendar.get(Calendar.MONTH) && yesterdayYear == calendar.get(Calendar.YEAR)) {
                holder.dateTextView.setText(context.getResources().getString(R.string.flev_yesterday_string));
            } else {
                holder.dateTextView.setText(new StringBuilder().append(Constants.DAY_NAMES[calendar.get(Calendar.DAY_OF_WEEK)])
                        .append(", ")
                        .append(data.get(position).getDay())
                        .append(" ")
                        .append(Constants.DECLENSION_MONTH_NAMES[data.get(position).getMonth() - 1]));
            }
        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }






    // ===================================== View Holder ===========================================
    public class FragmentLastEnteredValuesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LinearLayout noteSeparatorLayout;
        private LinearLayout dateLayout;
        private LinearLayout itemContainerLayout;
        private TextView expensesTypeTextView;
        private TextView expensesValueTextView;
        private TextView expensesNoteTextView;
        private TextView dateTextView;


        public FragmentLastEnteredValuesViewHolder(View itemView) {
            super(itemView);

            expensesTypeTextView = (TextView) itemView.findViewById(R.id.fragment_last_entered_values_expenses_type_textview);
            expensesValueTextView = (TextView) itemView.findViewById(R.id.fragment_last_entered_values_expenses_value_textview);
            expensesNoteTextView = (TextView) itemView.findViewById(R.id.fragment_last_entered_values_expenses_note_textview);
            dateTextView = (TextView) itemView.findViewById(R.id.fragment_last_entered_values_date_textview);

            noteSeparatorLayout = (LinearLayout) itemView.findViewById(R.id.fragment_last_entered_values_layout_horizontal_note_separator);
            dateLayout = (LinearLayout) itemView.findViewById(R.id.fragment_last_entered_values_date_layout);
            itemContainerLayout = (LinearLayout) itemView.findViewById(R.id.fragment_last_entered_values_layout_with_text);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null)
                clickListener.onItemClick(v, getAdapterPosition());
        }

    }
}
