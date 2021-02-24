package com.costs.newcosts;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * TODO: Add a class header comment
 */

public class DialogFragmentChooseStatisticPeriod extends DialogFragment implements View.OnClickListener, ViewSwitcher.ViewFactory {

    private Context context;
    private Button OkButton, CancelButton;

    private Button startingDateDayUpButton, startingDateDayDownButton,
            startingDateMonthUpButton, startingDateMonthDownButton,
            startingDateYearUpButton, startingDateYearDownButton;
    private TextSwitcher startingDateDayTextSwitcher,
            startingDateMonthTextSwitcher,
            startingDateYearTextSwitcher;

    private Button endingDateDayUpButton, endingDateDayDownButton,
            endingDateMonthUpButton, endingDateMonthDownButton,
            endingDateYearUpButton, endingDateYearDownButton;
    private TextSwitcher endingDateDayTextSwitcher,
            endingDateMonthTextSwitcher,
            endingDateYearTextSwitcher;

    private int startingPickedDay, startingPickedMonth, startingPickedYear;
    private int endingPickedDay, endingPickedMonth, endingPickedYear;

    private long milliseconds;
    private long dateInMilliseconds = 0;
    private Calendar startingDateCalendar, endingDateCalendar;
    private Toast wrongDateToast;


    static DialogFragmentChooseStatisticPeriod newInstance(Context context) {
        return new DialogFragmentChooseStatisticPeriod();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.choose_period_dialog, container, false);

        // Получаем текущую дату
        startingDateCalendar = new GregorianCalendar();
        endingDateCalendar = new GregorianCalendar();
        startingPickedDay = endingPickedDay = startingDateCalendar.get(Calendar.DAY_OF_MONTH);
        startingPickedMonth = endingPickedMonth = startingDateCalendar.get(Calendar.MONTH);
        startingPickedYear = endingPickedYear = startingDateCalendar.get(Calendar.YEAR);

        OkButton = (Button) v.findViewById(R.id.choose_period_ok);
        OkButton.setOnClickListener(this);
        CancelButton = (Button) v.findViewById(R.id.choose_period_cancel);
        CancelButton.setOnClickListener(this);

        // Инициализация элементов выбора начальной даты
        startingDateDayUpButton = (Button) v.findViewById(R.id.choose_period_starting_date_calendar_dayUp);
        startingDateDayUpButton.setOnClickListener(this);
        startingDateDayDownButton = (Button) v.findViewById(R.id.choose_period_starting_date_calendar_dayDown);
        startingDateDayDownButton.setOnClickListener(this);
        startingDateMonthUpButton = (Button) v.findViewById(R.id.choose_period_starting_date_calendar_monthUp);
        startingDateMonthUpButton.setOnClickListener(this);
        startingDateMonthDownButton = (Button) v.findViewById(R.id.choose_period_starting_date_calendar_monthDown);
        startingDateMonthDownButton.setOnClickListener(this);
        startingDateYearUpButton = (Button) v.findViewById(R.id.choose_period_starting_date_calendar_yearUp);
        startingDateYearUpButton.setOnClickListener(this);
        startingDateYearDownButton = (Button) v.findViewById(R.id.choose_period_starting_date_calendar_yearDown);
        startingDateYearDownButton.setOnClickListener(this);

        startingDateDayTextSwitcher = (TextSwitcher) v.findViewById(R.id.choose_period_starting_date_calendar_day);
        startingDateDayTextSwitcher.setFactory(this);
        startingDateDayTextSwitcher.setText(String.valueOf(startingPickedDay));
        startingDateMonthTextSwitcher = (TextSwitcher) v.findViewById(R.id.choose_period_starting_date_calendar_month);
        startingDateMonthTextSwitcher.setFactory(this);
        startingDateMonthTextSwitcher.setText(Constants.MONTH_NAMES[startingPickedMonth]);
        startingDateYearTextSwitcher = (TextSwitcher) v.findViewById(R.id.choose_period_starting_date_calendar_year);
        startingDateYearTextSwitcher.setFactory(this);
        startingDateYearTextSwitcher.setText(String.valueOf(startingPickedYear));


        // Инициализация элементов выбора конечной даты
        endingDateDayUpButton = (Button) v.findViewById(R.id.choose_period_ending_date_calendar_dayUp);
        endingDateDayUpButton.setOnClickListener(this);
        endingDateDayDownButton = (Button) v.findViewById(R.id.choose_period_ending_date_calendar_dayDown);
        endingDateDayDownButton.setOnClickListener(this);
        endingDateMonthUpButton = (Button) v.findViewById(R.id.choose_period_ending_date_calendar_monthUp);
        endingDateMonthUpButton.setOnClickListener(this);
        endingDateMonthDownButton = (Button) v.findViewById(R.id.choose_period_ending_date_calendar_monthDown);
        endingDateMonthDownButton.setOnClickListener(this);
        endingDateYearUpButton = (Button) v.findViewById(R.id.choose_period_ending_date_calendar_yearUp);
        endingDateYearUpButton.setOnClickListener(this);
        endingDateYearDownButton = (Button) v.findViewById(R.id.choose_period_ending_date_calendar_yearDown);
        endingDateYearDownButton.setOnClickListener(this);

        endingDateDayTextSwitcher = (TextSwitcher) v.findViewById(R.id.choose_period_ending_date_calendar_day);
        endingDateDayTextSwitcher.setFactory(this);
        endingDateDayTextSwitcher.setText(String.valueOf(endingPickedDay));
        endingDateMonthTextSwitcher = (TextSwitcher) v.findViewById(R.id.choose_period_ending_date_calendar_month);
        endingDateMonthTextSwitcher.setFactory(this);
        endingDateMonthTextSwitcher.setText(Constants.MONTH_NAMES[endingPickedMonth]);
        endingDateYearTextSwitcher = (TextSwitcher) v.findViewById(R.id.choose_period_ending_date_calendar_year);
        endingDateYearTextSwitcher.setFactory(this);
        endingDateYearTextSwitcher.setText(String.valueOf(endingPickedYear));


        return v;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }






    @Override
    public View makeView() {
        TextView t = new TextView(getActivity());
        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        t.setTextSize(24);
        t.setTextColor(Color.parseColor("#000000"));
        t.setMaxLines(1);

        return t;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // =====================================================================================
            // ======================= Выбор начальной даты ==========================
            case R.id.choose_period_starting_date_calendar_dayDown:
                --startingPickedDay;
                if (startingPickedDay <= 0)
                    startingPickedDay = startingDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

                startingDateDayTextSwitcher.setText(String.valueOf(startingPickedDay));
                startingDateCalendar.set(Calendar.DAY_OF_MONTH, startingPickedDay);
                break;
            case R.id.choose_period_starting_date_calendar_dayUp:
                ++startingPickedDay;
                if (startingPickedDay > startingDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                    startingPickedDay = 1;

                startingDateDayTextSwitcher.setText(String.valueOf(startingPickedDay));
                startingDateCalendar.set(Calendar.DAY_OF_MONTH, startingPickedDay);
                break;


            case R.id.choose_period_starting_date_calendar_monthDown:
                --startingPickedMonth;
                if (startingPickedMonth < 0)
                    startingPickedMonth = 11;

                startingDateMonthTextSwitcher.setText(Constants.MONTH_NAMES[startingPickedMonth]);
                startingDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
                startingDateCalendar.set(Calendar.MONTH, startingPickedMonth);

                if (startingPickedDay > startingDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    startingPickedDay = startingDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    startingDateDayTextSwitcher.setText(String.valueOf(startingPickedDay));
                }
                startingDateCalendar.set(Calendar.DAY_OF_MONTH, startingPickedDay);
                break;
            case R.id.choose_period_starting_date_calendar_monthUp:
                ++startingPickedMonth;
                if (startingPickedMonth > 11)
                    startingPickedMonth = 0;

                startingDateMonthTextSwitcher.setText(Constants.MONTH_NAMES[startingPickedMonth]);
                startingDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
                startingDateCalendar.set(Calendar.MONTH, startingPickedMonth);

                if (startingPickedDay > startingDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    startingPickedDay = startingDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    startingDateDayTextSwitcher.setText(String.valueOf(startingPickedDay));
                };
                startingDateCalendar.set(Calendar.DAY_OF_MONTH, startingPickedDay);
                break;


            case R.id.choose_period_starting_date_calendar_yearDown:
                --startingPickedYear;

                startingDateYearTextSwitcher.setText(String.valueOf(startingPickedYear));
                startingDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
                startingDateCalendar.set(Calendar.YEAR, startingPickedYear);

                if (startingPickedDay > startingDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    startingPickedDay = startingDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    startingDateDayTextSwitcher.setText(String.valueOf(startingPickedDay));
                }
                startingDateCalendar.set(Calendar.DAY_OF_MONTH, startingPickedDay);
                break;
            case R.id.choose_period_starting_date_calendar_yearUp:
                ++startingPickedYear;

                startingDateYearTextSwitcher.setText(String.valueOf(startingPickedYear));
                startingDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
                startingDateCalendar.set(Calendar.YEAR, startingPickedYear);

                if (startingPickedDay > startingDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    startingPickedDay = startingDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    startingDateDayTextSwitcher.setText(String.valueOf(startingPickedDay));
                }
                startingDateCalendar.set(Calendar.DAY_OF_MONTH, startingPickedDay);
                break;
            // =====================================================================================
            // =====================================================================================


            // =====================================================================================
            // ======================= Выбор конечной даты ==========================
            case R.id.choose_period_ending_date_calendar_dayDown:
                --endingPickedDay;
                if (endingPickedDay <= 0)
                    endingPickedDay = endingDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

                endingDateDayTextSwitcher.setText(String.valueOf(endingPickedDay));
                endingDateCalendar.set(Calendar.DAY_OF_MONTH, endingPickedDay);
                break;
            case R.id.choose_period_ending_date_calendar_dayUp:
                ++endingPickedDay;
                if (endingPickedDay > endingDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                    endingPickedDay = 1;

                endingDateDayTextSwitcher.setText(String.valueOf(endingPickedDay));
                endingDateCalendar.set(Calendar.DAY_OF_MONTH, endingPickedDay);
                break;


            case R.id.choose_period_ending_date_calendar_monthDown:
                --endingPickedMonth;
                if (endingPickedMonth < 0)
                    endingPickedMonth = 11;

                endingDateMonthTextSwitcher.setText(Constants.MONTH_NAMES[endingPickedMonth]);
                endingDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
                endingDateCalendar.set(Calendar.MONTH, endingPickedMonth);

                if (endingPickedDay > endingDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    endingPickedDay = endingDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    endingDateDayTextSwitcher.setText(String.valueOf(endingPickedDay));
                }
                endingDateCalendar.set(Calendar.DAY_OF_MONTH, endingPickedDay);
                break;
            case R.id.choose_period_ending_date_calendar_monthUp:
                ++endingPickedMonth;
                if (endingPickedMonth > 11)
                    endingPickedMonth = 0;

                endingDateMonthTextSwitcher.setText(Constants.MONTH_NAMES[endingPickedMonth]);
                endingDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
                endingDateCalendar.set(Calendar.MONTH, endingPickedMonth);

                if (endingPickedDay > endingDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    endingPickedDay = endingDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    endingDateDayTextSwitcher.setText(String.valueOf(endingPickedDay));
                };
                endingDateCalendar.set(Calendar.DAY_OF_MONTH, endingPickedDay);
                break;


            case R.id.choose_period_ending_date_calendar_yearDown:
                --endingPickedYear;

                endingDateYearTextSwitcher.setText(String.valueOf(endingPickedYear));
                endingDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
                endingDateCalendar.set(Calendar.YEAR, endingPickedYear);

                if (endingPickedDay > endingDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    endingPickedDay = endingDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    endingDateDayTextSwitcher.setText(String.valueOf(endingPickedDay));
                }
                endingDateCalendar.set(Calendar.DAY_OF_MONTH, endingPickedDay);
                break;
            case R.id.choose_period_ending_date_calendar_yearUp:
                ++endingPickedYear;

                endingDateYearTextSwitcher.setText(String.valueOf(endingPickedYear));
                endingDateCalendar.set(Calendar.DAY_OF_MONTH, 1);
                endingDateCalendar.set(Calendar.YEAR, endingPickedYear);

                if (endingPickedDay > endingDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    endingPickedDay = endingDateCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    endingDateDayTextSwitcher.setText(String.valueOf(endingPickedDay));
                }
                endingDateCalendar.set(Calendar.DAY_OF_MONTH, endingPickedDay);
                break;
            // =====================================================================================
            // =====================================================================================



            case R.id.choose_period_ok:
                startingDateCalendar.set(Calendar.HOUR_OF_DAY, 0);
                startingDateCalendar.set(Calendar.MINUTE, 0);
                startingDateCalendar.set(Calendar.SECOND, 0);
                startingDateCalendar.set(Calendar.DAY_OF_MONTH, startingPickedDay);
                startingDateCalendar.set(Calendar.MONTH, startingPickedMonth);
                startingDateCalendar.set(Calendar.YEAR, startingPickedYear);
                long startingDateInMillis = startingDateCalendar.getTimeInMillis();

                endingDateCalendar.set(Calendar.HOUR_OF_DAY, 0);
                endingDateCalendar.set(Calendar.MINUTE, 0);
                endingDateCalendar.set(Calendar.SECOND, 0);
                endingDateCalendar.set(Calendar.DAY_OF_MONTH, endingPickedDay);
                endingDateCalendar.set(Calendar.MONTH, endingPickedMonth);
                endingDateCalendar.set(Calendar.YEAR, endingPickedYear);
                long endingDateInMillis = endingDateCalendar.getTimeInMillis() + 86400000;


                // Если выбранная конечная дата раньше начальной - показываем предупреждение
                if (endingDateInMillis < startingDateInMillis) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            wrongDateToast = Toast.makeText(getActivity(),
                                    getResources().getString(R.string.dfcsp_wrongDateToast_string),
                                    Toast.LENGTH_LONG);
                            wrongDateToast.setGravity(Gravity.CENTER, 0, 0);
                            wrongDateToast.show();
                        }
                    });
                } else {
                    DataUnitExpenses startingDateDataUnit = new DataUnitExpenses();
                    startingDateDataUnit.setDay(startingPickedDay);
                    startingDateDataUnit.setMonth(startingPickedMonth);
                    startingDateDataUnit.setYear(startingPickedYear);
                    startingDateDataUnit.setMilliseconds(startingDateInMillis);

                    DataUnitExpenses endingDateDataUnit = new DataUnitExpenses();
                    endingDateDataUnit.setDay(endingPickedDay);
                    endingDateDataUnit.setMonth(endingPickedMonth);
                    endingDateDataUnit.setYear(endingPickedYear);
                    endingDateDataUnit.setMilliseconds(endingDateInMillis);

                    Intent data = new Intent()
                            .putExtra(Constants.STARTING_DATE_LABEL, startingDateDataUnit)
                            .putExtra(Constants.ENDING_DATE_LABEL, endingDateDataUnit);
                    getTargetFragment().onActivityResult(Constants.CHOOSE_STATISTIC_PERIOD_REQUEST_CODE, Constants.CHOOSE_STATISTIC_PERIOD_RESULT_CODE, data);

                    if (wrongDateToast != null)
                        wrongDateToast.cancel();

                    dismiss();
                }
                break;
            case R.id.choose_period_cancel:
                if (wrongDateToast != null)
                    wrongDateToast.cancel();
                dismiss();
                break;

            default:
                break;
        }
    }
}
