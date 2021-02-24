package com.costs.newcosts;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ActivityStatisticDetailed extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_detailed);

        Bundle expenseDataBundle = getIntent().getExtras();
        if (expenseDataBundle == null)
            return;

        TextView toolBarTextView = (TextView) findViewById(R.id.activity_statistic_detailed_toolbar_textview);
        TextView overallValueTextView = (TextView) findViewById(R.id.activity_statistic_detailed_overall_value_textview);
        TextView perDayExpensesTextView = (TextView) findViewById(R.id.activity_statistic_detailed_per_day_textview);
        ListView statisticDetailedListView = (ListView) findViewById(R.id.activity_statistic_detailed_list_view);

        // При нажатии на стрелку назад - возвращаемся к предыдущему экрану
        ImageView toolbarBackArrowImageView = (ImageView) findViewById(R.id.activity_statistic_detailed_arrow_back);
        toolbarBackArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToPreviousActivity();
            }
        });

        DB_Costs cdb = DB_Costs.getInstance(this);

        final int MODE = expenseDataBundle.getInt(Constants.STATISTIC_DETAILED_ACTIVITY_MODE);
        switch (MODE) {
            case Constants.STATISTIC_DETAILED_ACTIVITY_MODE_BY_MONTHS: {
                DataUnitExpenses chosenMonthDataUnit = expenseDataBundle.getParcelable(Constants.DATA_FOR_STATISTIC_DETAILED_ACTIVITY);
                if (chosenMonthDataUnit != null) {
                    // Определяем количество дней в выбранном месяце. Если выбран
                    // текущий месяц - оиспользуем количество дней, прошедших с начала месяца
                    Calendar calendar = new GregorianCalendar();
                    int daysInMonth = calendar.get(Calendar.DAY_OF_MONTH);
                    if (chosenMonthDataUnit.getMonth() != (int) calendar.get(Calendar.MONTH) ||
                            chosenMonthDataUnit.getYear() != (int) calendar.get(Calendar.YEAR))
                    {
                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                        calendar.set(Calendar.YEAR, chosenMonthDataUnit.getYear());
                        calendar.set(Calendar.MONTH, chosenMonthDataUnit.getMonth());
                        daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    }

                    // Отображаем выбранный период просмотра
                    toolBarTextView.setText(Constants.MONTH_NAMES[chosenMonthDataUnit.getMonth()] + " " + chosenMonthDataUnit.getYear());

                    // Получаем список статей расходов и суммарные значения по ним за выбранный месяц
                    final List<DataUnitExpenses> expensesDataUnitList = cdb.getCostValuesArrayOnDate_V3(chosenMonthDataUnit.getMonth(), chosenMonthDataUnit.getYear());
                    double overallExpensesValueForChosenPeriod = 0.0;
                    for (DataUnitExpenses dataUnit : expensesDataUnitList)
                        overallExpensesValueForChosenPeriod = overallExpensesValueForChosenPeriod + dataUnit.getExpenseValueDouble();

                    // Устанавливаем средний расход в день
                    double averageExpensesPerDay = overallExpensesValueForChosenPeriod / daysInMonth;
                    perDayExpensesTextView.setText(Constants.formatDigit(averageExpensesPerDay) + " " +
                                                    getResources().getString(R.string.rur_string) +
                                                    getResources().getString(R.string.dot_sign_string) + "/" +
                                                    getResources().getString(R.string.asd_perDayExpensesTextView_day_string));


                    // Устанавливаем суммарное значение за выбранный период
                    overallValueTextView.setText(Constants.formatDigit(overallExpensesValueForChosenPeriod) + " " +
                                                    getResources().getString(R.string.rur_string) +
                                                    getResources().getString(R.string.dot_sign_string));

                    // Инициализируем ListView полученным списком статей расходов
                    ListAdapter statisticDetailedListViewAdapter = new AdapterStatisticDetailedActivityListView(this, expensesDataUnitList);
                    statisticDetailedListView.setAdapter(statisticDetailedListViewAdapter);

                    // При нажатии на элемент списка статей расходов происходит переход на экран
                    // детального просмотра затрат по выбранной статье за выбранный месяц
                    final DataUnitExpenses finalChosenMonthDataUnit = chosenMonthDataUnit;
                    statisticDetailedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent statisticCostTypeDetailedIntent = new Intent(ActivityStatisticDetailed.this, ActivityStatisticExpenseTypeDetailed.class);
                            statisticCostTypeDetailedIntent.putExtra(Constants.STATISTIC_DETAILED_ACTIVITY_MODE, MODE);
                            statisticCostTypeDetailedIntent.putExtra(Constants.DATA_FOR_STATISTIC_COST_TYPE_DETAILED_ACTIVITY, expensesDataUnitList.get(position));
                            statisticCostTypeDetailedIntent.putExtra(Constants.DATA_FOR_STATISTIC_DETAILED_ACTIVITY, finalChosenMonthDataUnit);
                            statisticCostTypeDetailedIntent.putExtra(Constants.PREVIOUS_ACTIVITY, Constants.STATISTIC_DETAILED_ACTIVITY);
                            startActivity(statisticCostTypeDetailedIntent);
                        }
                    });
                }
            }
            break;

            case Constants.STATISTIC_DETAILED_ACTIVITY_MODE_CUSTOM_DATE: {
                final DataUnitExpenses startingDateDataUnit = expenseDataBundle.getParcelable(Constants.STARTING_DATE_LABEL);
                final DataUnitExpenses endingDateDataUnit = expenseDataBundle.getParcelable(Constants.ENDING_DATE_LABEL);
                if (startingDateDataUnit == null ||  endingDateDataUnit == null)
                    return;

                // Подсчитывем количество дней в выбранном периоде
                long chosenAmountOfDays = TimeUnit.DAYS.convert(endingDateDataUnit.getMilliseconds() - startingDateDataUnit.getMilliseconds(), TimeUnit.MILLISECONDS);
                if (chosenAmountOfDays == 0)
                    chosenAmountOfDays = 1;

                // Отображаем выбранный период просмотра
                toolBarTextView.setText(new StringBuilder()
                        .append(startingDateDataUnit.getDay())
                        .append(" ")
                        .append(Constants.DECLENSION_MONTH_NAMES[startingDateDataUnit.getMonth()])
                        .append(" ")
                        .append(startingDateDataUnit.getYear())
                        .append(" -\n")
                        .append(endingDateDataUnit.getDay())
                        .append(" ")
                        .append(Constants.DECLENSION_MONTH_NAMES[endingDateDataUnit.getMonth()])
                        .append(" ")
                        .append(endingDateDataUnit.getYear())
                        .toString());

                // Получаем список статей расходов и суммарные значения по ним за выбранный период
                final List<DataUnitExpenses> expensesDataUnitList = cdb.getCostsBetweenDates_V3(startingDateDataUnit.getMilliseconds(),
                        endingDateDataUnit.getMilliseconds());
                double overallExpensesValueForChosenPeriod = 0.0;
                for (DataUnitExpenses dataUnit : expensesDataUnitList)
                    overallExpensesValueForChosenPeriod = overallExpensesValueForChosenPeriod + dataUnit.getExpenseValueDouble();

                // Устанавливаем средний расход в день
                double averageExpensesPerDay = overallExpensesValueForChosenPeriod / chosenAmountOfDays;
                perDayExpensesTextView.setText(Constants.formatDigit(averageExpensesPerDay) + " " +
                        getResources().getString(R.string.rur_string) +
                        getResources().getString(R.string.dot_sign_string) + "/" +
                        getResources().getString(R.string.asd_perDayExpensesTextView_day_string));

                // Устанавливаем суммарное значение за выбранный период
                overallValueTextView.setText(Constants.formatDigit(overallExpensesValueForChosenPeriod) + " " +
                        getResources().getString(R.string.rur_string) +
                        getResources().getString(R.string.dot_sign_string));

                // Инициализируем ListView полученным списком статей расходов
                ListAdapter statisticDetailedListViewAdapter = new AdapterStatisticDetailedActivityListView(this, expensesDataUnitList);
                statisticDetailedListView.setAdapter(statisticDetailedListViewAdapter);
                statisticDetailedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent statisticCostTypeDetailedIntent = new Intent(ActivityStatisticDetailed.this, ActivityStatisticExpenseTypeDetailed.class);
                        statisticCostTypeDetailedIntent.putExtra(Constants.STATISTIC_DETAILED_ACTIVITY_MODE, MODE);
                        statisticCostTypeDetailedIntent.putExtra(Constants.STARTING_DATE_LABEL, startingDateDataUnit);
                        statisticCostTypeDetailedIntent.putExtra(Constants.ENDING_DATE_LABEL, endingDateDataUnit);
                        statisticCostTypeDetailedIntent.putExtra(Constants.DATA_FOR_STATISTIC_COST_TYPE_DETAILED_ACTIVITY, expensesDataUnitList.get(position));
                        statisticCostTypeDetailedIntent.putExtra(Constants.PREVIOUS_ACTIVITY, Constants.STATISTIC_DETAILED_ACTIVITY);
                        startActivity(statisticCostTypeDetailedIntent);
                    }
                });
            }
            break;
        }
    }

    // Возвращаемся к предыдущему экрану
    private void returnToPreviousActivity() {
        Intent previousActivity = new Intent(this, ActivityMainWithFragments.class);
        previousActivity.putExtra(Constants.TARGET_TAB, Constants.FRAGMENT_STATISTIC_MAIN_SCREEN);
        previousActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(previousActivity);
    }

    @Override
    public void onBackPressed() {
        returnToPreviousActivity();
    }
}
