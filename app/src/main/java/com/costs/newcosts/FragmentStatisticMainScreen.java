package com.costs.newcosts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;


public class FragmentStatisticMainScreen extends Fragment {

    private Context context;
//    private ListView periodsListView;
    private RecyclerView recyclerView;
    private Button chooseStatisticPeriodButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View statisticMainScreenView = inflater.inflate(R.layout.fragment_statistic_main_screen, container, false);
        chooseStatisticPeriodButton = (Button) statisticMainScreenView.findViewById(R.id.statistic_main_screen_chose_period_button);
        chooseStatisticPeriodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChoseStatisticPeriodButtonClick(v);
            }
        });
        recyclerView = (RecyclerView) statisticMainScreenView.findViewById(R.id.statistic_main_screen_recycler_view);

        return statisticMainScreenView;
    }

    @Override
    public void onResume() {
        super.onResume();

        DB_Costs cdb = DB_Costs.getInstance(context);

        // Получаем суммарные значения за месяц и год
        final List<DataUnitExpenses> sumByMonthList = cdb.getSumByMonthsList();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        AdapterStatisticMainScreenRecyclerView statisticMainScreenRecyclerViewAdapter = new AdapterStatisticMainScreenRecyclerView(sumByMonthList, context);
        statisticMainScreenRecyclerViewAdapter.setClickListener(new AdapterLastEnteredValuesRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Intent statisticDetailedActivityIntent = new Intent(context, ActivityStatisticDetailed.class);
                statisticDetailedActivityIntent.putExtra(Constants.STATISTIC_DETAILED_ACTIVITY_MODE, Constants.STATISTIC_DETAILED_ACTIVITY_MODE_BY_MONTHS);
                statisticDetailedActivityIntent.putExtra(Constants.DATA_FOR_STATISTIC_DETAILED_ACTIVITY, sumByMonthList.get(position));
                startActivity(statisticDetailedActivityIntent);
            }
        });
        recyclerView.setAdapter(statisticMainScreenRecyclerViewAdapter);
        Constants.statisticMainScreenFragmentDataIsActual(true);
    }

    // Вызов диалога ручного задания периода просмотра статистики расходов
    public void onChoseStatisticPeriodButtonClick(View view) {
        DialogFragmentChooseStatisticPeriod choosePeriodDialog = DialogFragmentChooseStatisticPeriod.newInstance(context);
        choosePeriodDialog.setTargetFragment(FragmentStatisticMainScreen.this, Constants.CHOOSE_STATISTIC_PERIOD_REQUEST_CODE);;
        choosePeriodDialog.show(getFragmentManager(), Constants.CHOOSE_STATISTIC_PERIOD_DIALOG_TAG);
    }


    // Обработка результата выбора даты периода просмотра статистики
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.CHOOSE_STATISTIC_PERIOD_REQUEST_CODE) {
            if (resultCode == Constants.CHOOSE_STATISTIC_PERIOD_RESULT_CODE) {
                DataUnitExpenses startingDateDataUnit = null;
                DataUnitExpenses endingDateDataUnit = null;

                if (data == null)
                    return;

                startingDateDataUnit = data.getExtras().getParcelable(Constants.STARTING_DATE_LABEL);
                endingDateDataUnit = data.getExtras().getParcelable(Constants.ENDING_DATE_LABEL);

                Intent statisticDetailedActivityIntent = new Intent(context, ActivityStatisticDetailed.class);
                statisticDetailedActivityIntent.putExtra(Constants.STATISTIC_DETAILED_ACTIVITY_MODE, Constants.STATISTIC_DETAILED_ACTIVITY_MODE_CUSTOM_DATE);
                statisticDetailedActivityIntent.putExtra(Constants.STARTING_DATE_LABEL, startingDateDataUnit);
                statisticDetailedActivityIntent.putExtra(Constants.ENDING_DATE_LABEL, endingDateDataUnit);
                startActivity(statisticDetailedActivityIntent);
            }
        }
    }




    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
    }
}
