package com.costs.newcosts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ActivityStatisticExpenseTypeDetailed extends AppCompatActivity {

    private int STATISTIC_DETAILED_ACTIVITY_MODE = -1;
    private DataUnitExpenses statisticDetailedActivityDataUnit;
    private DataUnitExpenses startingDateDataUnit;
    private DataUnitExpenses endingDateDataUnit;
    private List<DataUnitExpenses> originalDataUnitList;
    private AdapterStatisticExpenseTypeDetailedRecyclerView expenseTypeDetailedAdapter;
    private RecyclerView recyclerView;

    private int CURRENT_SORT_TYPE = 0;
    private static final int SORT_BY_DATE_ASCENDING = 0;
    private static final int SORT_BY_DATE_DESCENDING = 1;
    private static final int SORT_BY_DAILY_SUM_ASCENDING = 2;
    private static final int SORT_BY_DAILY_SUM_DESCENDING = 3;

    private List<DataUnitExpenses> currentDataUnitList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_expense_type_detailed);

        // При нажатии стрелки назад - возвращаемся к предыдущему экрану
        ImageView arrowBackImageView = (ImageView) findViewById(R.id.activity_statistic_cost_type_detailed_arrow_back);
        arrowBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToPreviousActivity();
            }
        });

        TextView toolBarTextView = (TextView) findViewById(R.id.activity_statistic_cost_type_detailed_toolbar_textview);
        TextView expenseNameTextView = (TextView) findViewById(R.id.activity_statistic_cost_type_detailed_expense_name_textview);
        TextView expenseValueTextView = (TextView) findViewById(R.id.activity_statistic_cost_type_detailed_overall_value_textview);
        TextView perDayExpensesTextView = (TextView) findViewById(R.id.activity_statistic_cost_type_detailed_per_day_textview);

        DB_Costs cdb = DB_Costs.getInstance(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.activity_statistic_cost_type_detailed_recycler_view);
        recyclerView.setLayoutManager(linearLayoutManager);

        Bundle expenseDataBundle = getIntent().getExtras();
        if (expenseDataBundle == null)
            return;

        STATISTIC_DETAILED_ACTIVITY_MODE = expenseDataBundle.getInt(Constants.STATISTIC_DETAILED_ACTIVITY_MODE);
        DataUnitExpenses chosenExpenseTypeDataUnit = expenseDataBundle.getParcelable(Constants.DATA_FOR_STATISTIC_COST_TYPE_DETAILED_ACTIVITY);

        // Отображаем название выбранной категории
        expenseNameTextView.setText(chosenExpenseTypeDataUnit.getExpenseName());

        // Отображаем суммарные расходоы по выбранной категории за выбранный период
        expenseValueTextView.setText(chosenExpenseTypeDataUnit.getExpenseValueString() + " " +
                                        getResources().getString(R.string.rur_string) +
                                        getResources().getString(R.string.dot_sign_string));

        switch (STATISTIC_DETAILED_ACTIVITY_MODE) {
            case Constants.STATISTIC_DETAILED_ACTIVITY_MODE_BY_MONTHS: {
                statisticDetailedActivityDataUnit = expenseDataBundle.getParcelable(Constants.DATA_FOR_STATISTIC_DETAILED_ACTIVITY);
                if (chosenExpenseTypeDataUnit == null)
                    return;

                // Определяем количество дней в выбранном месяце. Если выбран
                // текущий месяц - оиспользуем количество дней, прошедших с начала месяца
                Calendar calendar = new GregorianCalendar();
                int daysInMonth = calendar.get(Calendar.DAY_OF_MONTH);
                if (chosenExpenseTypeDataUnit.getMonth() != (int) calendar.get(Calendar.MONTH) ||
                        chosenExpenseTypeDataUnit.getYear() != (int) calendar.get(Calendar.YEAR))
                {
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    calendar.set(Calendar.YEAR, chosenExpenseTypeDataUnit.getYear());
                    calendar.set(Calendar.MONTH, chosenExpenseTypeDataUnit.getMonth());
                    daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                }

                // Устанавливаем средний расход в день
                double averageExpensesPerDay = chosenExpenseTypeDataUnit.getExpenseValueDouble() / daysInMonth;
                perDayExpensesTextView.setText(Constants.formatDigit(averageExpensesPerDay) + " " +
                                                getResources().getString(R.string.rur_string) +
                                                getResources().getString(R.string.dot_sign_string) + "/" +
                                                getResources().getString(R.string.asetd_perDayExpensesTextView_day_string));

                // Отображаем выбранный период просмотра
                toolBarTextView.setText(Constants.MONTH_NAMES[chosenExpenseTypeDataUnit.getMonth()] + " " +
                        chosenExpenseTypeDataUnit.getYear());

                // Получаем список всех расходов по выбранной категории за выбранной период
                originalDataUnitList = cdb.getCostValuesArrayOnDateAndCostName_V2(chosenExpenseTypeDataUnit.getMonth(),
                        chosenExpenseTypeDataUnit.getYear(),
                        chosenExpenseTypeDataUnit.getExpenseId_N(),
                        chosenExpenseTypeDataUnit.getExpenseName());

                expenseTypeDetailedAdapter = new AdapterStatisticExpenseTypeDetailedRecyclerView(originalDataUnitList, this);
                recyclerView.setAdapter(expenseTypeDetailedAdapter);
            }
            break;

            case Constants.STATISTIC_DETAILED_ACTIVITY_MODE_CUSTOM_DATE: {
                startingDateDataUnit = expenseDataBundle.getParcelable(Constants.STARTING_DATE_LABEL);
                endingDateDataUnit = expenseDataBundle.getParcelable(Constants.ENDING_DATE_LABEL);
                if (startingDateDataUnit == null || endingDateDataUnit == null || chosenExpenseTypeDataUnit == null)
                    return;

                // Подсчитывем количество дней в выбранном периоде
                long chosenAmountOfDays = TimeUnit.DAYS.convert(endingDateDataUnit.getMilliseconds() - startingDateDataUnit.getMilliseconds(), TimeUnit.MILLISECONDS);
                if (chosenAmountOfDays == 0)
                    chosenAmountOfDays = 1;

                // Устанавливаем средний расход в день
                double averageExpensesPerDay = chosenExpenseTypeDataUnit.getExpenseValueDouble() / chosenAmountOfDays;
                perDayExpensesTextView.setText(Constants.formatDigit(averageExpensesPerDay) + " " +
                                                getResources().getString(R.string.rur_string) +
                                                getResources().getString(R.string.dot_sign_string) + "/" +
                                                getResources().getString(R.string.asetd_perDayExpensesTextView_day_string));

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

                originalDataUnitList = cdb.getCostsBetweenDatesByName_V2(startingDateDataUnit.getMilliseconds(),
                        endingDateDataUnit.getMilliseconds(),
                        chosenExpenseTypeDataUnit.getExpenseName(),
                        chosenExpenseTypeDataUnit.getExpenseId_N());

                expenseTypeDetailedAdapter = new AdapterStatisticExpenseTypeDetailedRecyclerView(originalDataUnitList, this);
                recyclerView.setAdapter(expenseTypeDetailedAdapter);
            }
            break;
        }


        Spinner sortingTypeSpinner = (Spinner) findViewById(R.id.activity_statistic_cost_type_detailed_spinner);
        AdapterStatisticExpenseTypeDetailedSpinner sortingTypeSpinnerAdapter
                = new AdapterStatisticExpenseTypeDetailedSpinner(this);
        sortingTypeSpinner.setAdapter(sortingTypeSpinnerAdapter);
        sortingTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case SORT_BY_DATE_ASCENDING:
                        if (CURRENT_SORT_TYPE != SORT_BY_DATE_ASCENDING)
                            expenseTypeDetailedAdapter.swapData(originalDataUnitList);

                        CURRENT_SORT_TYPE = SORT_BY_DATE_ASCENDING;
                        break;
                    case SORT_BY_DATE_DESCENDING:
                        if (CURRENT_SORT_TYPE == SORT_BY_DATE_DESCENDING)
                            return;

                        List<DataUnitExpenses> descendingSortedDataUnitList = new ArrayList<>(originalDataUnitList.size());
                        for (int i = originalDataUnitList.size() - 1; i >= 0; --i)
                            descendingSortedDataUnitList.add(originalDataUnitList.get(i));
                        expenseTypeDetailedAdapter.swapData(descendingSortedDataUnitList);

                        CURRENT_SORT_TYPE = SORT_BY_DATE_DESCENDING;
                        break;
                    case SORT_BY_DAILY_SUM_ASCENDING:
                        if (CURRENT_SORT_TYPE == SORT_BY_DAILY_SUM_ASCENDING)
                            return;

                        List<DataUnitExpenses> ascendingSortedByDailySumDataUnitList =
                                sortExpensesByDailySum(SORT_BY_DAILY_SUM_ASCENDING);
                        expenseTypeDetailedAdapter.swapData(ascendingSortedByDailySumDataUnitList);

                        CURRENT_SORT_TYPE = SORT_BY_DAILY_SUM_ASCENDING;
                        break;
                    case SORT_BY_DAILY_SUM_DESCENDING:
                        if (CURRENT_SORT_TYPE == SORT_BY_DAILY_SUM_DESCENDING)
                            return;

                        List<DataUnitExpenses> descendingSortedByDailySumDataUnitList =
                                sortExpensesByDailySum(SORT_BY_DAILY_SUM_DESCENDING);
                        expenseTypeDetailedAdapter.swapData(descendingSortedByDailySumDataUnitList);

                        CURRENT_SORT_TYPE = SORT_BY_DAILY_SUM_DESCENDING;
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private List<DataUnitExpenses> sortExpensesByDailySum(int sortType) {
        List<DataUnitExpenses> sortedByDailySumDataUnitList = new ArrayList<>(originalDataUnitList.size());

        List<Double> sumByDaysList = new ArrayList<>();
        List<List<DataUnitExpenses>> groupedByDaysDataUnitsList = new ArrayList<>();

        // Находим суммы затрат за каждый отдельный день
        int unitListNewDayIndex = 0;
        while (true) {
            double overallValueForSingleDay = originalDataUnitList.get(unitListNewDayIndex).getExpenseValueDouble();
            List<DataUnitExpenses> expensesDataUnitsForSingleDay = new ArrayList<>();
            expensesDataUnitsForSingleDay.add(originalDataUnitList.get(unitListNewDayIndex));

            int i = 0;
            for (i = unitListNewDayIndex + 1; i < originalDataUnitList.size(); ++i) {
                if (originalDataUnitList.get(i).getDay() == originalDataUnitList.get(i - 1).getDay()
                        && originalDataUnitList.get(i).getMonth() == originalDataUnitList.get(i -1).getMonth()
                        && originalDataUnitList.get(i).getYear() == originalDataUnitList.get(i - 1).getYear())
                {
                    overallValueForSingleDay = overallValueForSingleDay + originalDataUnitList.get(i).getExpenseValueDouble();
                    expensesDataUnitsForSingleDay.add(originalDataUnitList.get(i));
                }
                else
                {
                    break;
                }
            }
            sumByDaysList.add(overallValueForSingleDay);
            groupedByDaysDataUnitsList.add(expensesDataUnitsForSingleDay);

            unitListNewDayIndex = i;
            if (unitListNewDayIndex >= originalDataUnitList.size())
                break;
        }


        switch (sortType) {
            case SORT_BY_DAILY_SUM_ASCENDING:

                for (int i = 0; i < sumByDaysList.size(); ++i) {
                    for (int j = i + 1; j < sumByDaysList.size(); ++j) {
                        if (sumByDaysList.get(j) < sumByDaysList.get(i)) {
                            double tempValueDouble = sumByDaysList.get(i);
                            List<DataUnitExpenses> tempValueList = groupedByDaysDataUnitsList.get(i);

                            sumByDaysList.set(i, sumByDaysList.get(j));
                            sumByDaysList.set(j, tempValueDouble);
                            groupedByDaysDataUnitsList.set(i, groupedByDaysDataUnitsList.get(j));
                            groupedByDaysDataUnitsList.set(j, tempValueList);
                        }
                    }
                }

                for (List<DataUnitExpenses> singleDayDataUnitList : groupedByDaysDataUnitsList) {
                    for (DataUnitExpenses singleDataUnit : singleDayDataUnitList)
                        sortedByDailySumDataUnitList.add(singleDataUnit);
                }

                break;
            case SORT_BY_DAILY_SUM_DESCENDING:

                for (int i = 0; i < sumByDaysList.size(); ++i) {
                    for (int j = i + 1; j < sumByDaysList.size(); ++j) {
                        if (sumByDaysList.get(j) > sumByDaysList.get(i)) {
                            double tempValueDouble = sumByDaysList.get(i);
                            List<DataUnitExpenses> tempValueList = groupedByDaysDataUnitsList.get(i);

                            sumByDaysList.set(i, sumByDaysList.get(j));
                            sumByDaysList.set(j, tempValueDouble);
                            groupedByDaysDataUnitsList.set(i, groupedByDaysDataUnitsList.get(j));
                            groupedByDaysDataUnitsList.set(j, tempValueList);
                        }
                    }
                }

                for (List<DataUnitExpenses> singleDayDataUnitList : groupedByDaysDataUnitsList) {
                    for (DataUnitExpenses singleDataUnit : singleDayDataUnitList)
                        sortedByDailySumDataUnitList.add(singleDataUnit);
                }

                break;
        }

        return sortedByDailySumDataUnitList;
    }

    public void reverseCurrentDataList() {
        for (int i = 0; i < currentDataUnitList.size() / 2; ++i) {
            DataUnitExpenses temp = currentDataUnitList.get(i);
            currentDataUnitList.set(i, currentDataUnitList.get(currentDataUnitList.size() - 1 - i));
            currentDataUnitList.set(currentDataUnitList.size() - 1 - i, temp);
        }
    }

    // Возвращаемся к предыдущему экрану
    private void returnToPreviousActivity() {
        Intent previousActivity = null;
        switch (STATISTIC_DETAILED_ACTIVITY_MODE) {
            case Constants.STATISTIC_DETAILED_ACTIVITY_MODE_BY_MONTHS:
                previousActivity = new Intent(this, ActivityStatisticDetailed.class);
                previousActivity.putExtra(Constants.STATISTIC_DETAILED_ACTIVITY_MODE, STATISTIC_DETAILED_ACTIVITY_MODE);
                previousActivity.putExtra(Constants.DATA_FOR_STATISTIC_DETAILED_ACTIVITY, statisticDetailedActivityDataUnit);
                previousActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            case Constants.STATISTIC_DETAILED_ACTIVITY_MODE_CUSTOM_DATE:
                previousActivity = new Intent(this, ActivityStatisticDetailed.class);
                previousActivity.putExtra(Constants.STATISTIC_DETAILED_ACTIVITY_MODE, STATISTIC_DETAILED_ACTIVITY_MODE);
                previousActivity.putExtra(Constants.STARTING_DATE_LABEL, startingDateDataUnit);
                previousActivity.putExtra(Constants.ENDING_DATE_LABEL, endingDateDataUnit);
                previousActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
        }

        startActivity(previousActivity);
    }

    @Override
    public void onBackPressed() {
        returnToPreviousActivity();
    }
}
