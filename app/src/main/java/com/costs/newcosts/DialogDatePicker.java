package com.costs.newcosts;


import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.Calendar;

public class DialogDatePicker extends Dialog implements
        View.OnClickListener, ViewSwitcher.ViewFactory {

    private int pickedDay, pickedMonth, pickedYear;
    private long milliseconds;
    private long dateInMilliseconds = 0;
    private Calendar calendar;

    private Activity activity;

    private Button OkButton, CancelButton;
    private Button DayUpButton, DayDownButton,
                MonthUpButton, MonthDownButton,
                YearUpButton, YearDownButton;
    private TextSwitcher DayTextSwitcher,
                 MonthTextSwitcher,
                 YearTextSwitcher;
    private TextView ChosenDayMonthTextView,
                     ChosenYearTextView;


    MyDatePickerCallback Callback;
    public interface MyDatePickerCallback {
        public void getPickedDate(String pickedDate);
    }


    public DialogDatePicker(Activity activity) {
        super(activity);
        this.activity = activity;

        Callback = (MyDatePickerCallback) activity;
    }

    public DialogDatePicker(Activity activity, long dateInMilliseconds) {
        super(activity);
        this.activity = activity;
        this.dateInMilliseconds = dateInMilliseconds;

        Callback = (MyDatePickerCallback) activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.datepicker);

//        ViewGroup.LayoutParams params = getWindow().getAttributes();
//        params.width = ViewPager.LayoutParams.FILL_PARENT;
//        getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

//        findMaxTextViewWidth();

        if (dateInMilliseconds == 0) {
            calendar = Calendar.getInstance();
            pickedDay = calendar.get(Calendar.DAY_OF_MONTH);
            pickedMonth = calendar.get(Calendar.MONTH);
            pickedYear = calendar.get(Calendar.YEAR);
        } else {
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dateInMilliseconds);

            pickedDay = calendar.get(Calendar.DAY_OF_MONTH);
            pickedMonth = calendar.get(Calendar.MONTH);
            pickedYear = calendar.get(Calendar.YEAR);
        }


//        Animation in = AnimationUtils.loadAnimation(activity,
//                android.R.anim.fade_out);
//        Animation out = AnimationUtils.loadAnimation(activity,
//                android.R.anim.fade_out);


        OkButton = (Button) findViewById(R.id.calendar_ok);
        OkButton.setOnClickListener(this);
        CancelButton = (Button) findViewById(R.id.calendar_cancel);
        CancelButton.setOnClickListener(this);

        DayUpButton = (Button) findViewById(R.id.calendar_dayUp);
        DayUpButton.setOnClickListener(this);
        DayDownButton = (Button) findViewById(R.id.calendar_dayDown);
        DayDownButton.setOnClickListener(this);
        MonthUpButton = (Button) findViewById(R.id.calendar_monthUp);
        MonthUpButton.setOnClickListener(this);
        MonthDownButton = (Button) findViewById(R.id.calendar_monthDown);
        MonthDownButton.setOnClickListener(this);
        YearUpButton = (Button) findViewById(R.id.calendar_yearUp);
        YearUpButton.setOnClickListener(this);
        YearDownButton = (Button) findViewById(R.id.calendar_yearDown);
        YearDownButton.setOnClickListener(this);

        DayTextSwitcher = (TextSwitcher) findViewById(R.id.calendar_day);
//        DayTextSwitcher.setInAnimation(in);
//        DayTextSwitcher.setOutAnimation(out);
        DayTextSwitcher.setFactory(this);
        DayTextSwitcher.setText(String.valueOf(pickedDay));
        MonthTextSwitcher = (TextSwitcher) findViewById(R.id.calendar_month);
//        MonthTextSwitcher.setInAnimation(in);
//        MonthTextSwitcher.setOutAnimation(out);
        MonthTextSwitcher.setFactory(this);
        MonthTextSwitcher.setText(Constants.MONTH_NAMES[pickedMonth]);
        YearTextSwitcher = (TextSwitcher) findViewById(R.id.calendar_year);
//        YearTextSwitcher.setInAnimation(in);
//        YearTextSwitcher.setOutAnimation(out);
        YearTextSwitcher.setFactory(this);
        YearTextSwitcher.setText(String.valueOf(pickedYear));


        ChosenDayMonthTextView = (TextView) findViewById(R.id.calendar_chosenDayMonth);
        ChosenDayMonthTextView.setText(Constants.DAY_NAMES[calendar.get(Calendar.DAY_OF_WEEK)] + ", " +
                                       String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + " " +
                                       Constants.DECLENSION_MONTH_NAMES[calendar.get(Calendar.MONTH)]);

        ChosenYearTextView = (TextView) findViewById(R.id.calendar_chosenYear);
        ChosenYearTextView.setText(String.valueOf(pickedYear));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.calendar_dayDown:
                --pickedDay;
                if (pickedDay <= 0)
                    pickedDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

                DayTextSwitcher.setText(String.valueOf(pickedDay));
                calendar.set(Calendar.DAY_OF_MONTH, pickedDay);

                ChosenDayMonthTextView.setText(Constants.DAY_NAMES[calendar.get(Calendar.DAY_OF_WEEK)] + ", " +
                                               String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + " " +
                                               Constants.DECLENSION_MONTH_NAMES[calendar.get(Calendar.MONTH)]);
                break;
            case R.id.calendar_dayUp:
                ++pickedDay;
                if (pickedDay > calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                    pickedDay = 1;

                DayTextSwitcher.setText(String.valueOf(pickedDay));
                calendar.set(Calendar.DAY_OF_MONTH, pickedDay);

                ChosenDayMonthTextView.setText(Constants.DAY_NAMES[calendar.get(Calendar.DAY_OF_WEEK)] + ", " +
                                               String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + " " +
                                               Constants.DECLENSION_MONTH_NAMES[calendar.get(Calendar.MONTH)]);
                break;


            case R.id.calendar_monthDown:
                --pickedMonth;
                if (pickedMonth < 0)
                    pickedMonth = 11;

                MonthTextSwitcher.setText(Constants.MONTH_NAMES[pickedMonth]);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.MONTH, pickedMonth);

                if (pickedDay > calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    pickedDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    DayTextSwitcher.setText(String.valueOf(pickedDay));
                }
                calendar.set(Calendar.DAY_OF_MONTH, pickedDay);

                ChosenDayMonthTextView.setText(Constants.DAY_NAMES[calendar.get(Calendar.DAY_OF_WEEK)] + ", " +
                                               String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + " " +
                                               Constants.DECLENSION_MONTH_NAMES[calendar.get(Calendar.MONTH)]);
                break;
            case R.id.calendar_monthUp:
                ++pickedMonth;
                if (pickedMonth > 11)
                    pickedMonth = 0;

                MonthTextSwitcher.setText(Constants.MONTH_NAMES[pickedMonth]);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.MONTH, pickedMonth);

                if (pickedDay > calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    pickedDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    DayTextSwitcher.setText(String.valueOf(pickedDay));
                };
                calendar.set(Calendar.DAY_OF_MONTH, pickedDay);

                ChosenDayMonthTextView.setText(Constants.DAY_NAMES[calendar.get(Calendar.DAY_OF_WEEK)] + ", " +
                                               String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + " " +
                                               Constants.DECLENSION_MONTH_NAMES[calendar.get(Calendar.MONTH)]);
                break;


            case R.id.calendar_yearDown:
                --pickedYear;

                YearTextSwitcher.setText(String.valueOf(pickedYear));
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.YEAR, pickedYear);

                if (pickedDay > calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    pickedDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    DayTextSwitcher.setText(String.valueOf(pickedDay));
                }
                calendar.set(Calendar.DAY_OF_MONTH, pickedDay);

                ChosenDayMonthTextView.setText(Constants.DAY_NAMES[calendar.get(Calendar.DAY_OF_WEEK)] + ", " +
                                               String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + " " +
                                               Constants.DECLENSION_MONTH_NAMES[calendar.get(Calendar.MONTH)]);
                ChosenYearTextView.setText(String.valueOf(pickedYear));
                break;
            case R.id.calendar_yearUp:
                ++pickedYear;

                YearTextSwitcher.setText(String.valueOf(pickedYear));
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.YEAR, pickedYear);

                if (pickedDay > calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    pickedDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    DayTextSwitcher.setText(String.valueOf(pickedDay));
                }
                calendar.set(Calendar.DAY_OF_MONTH, pickedDay);

                ChosenDayMonthTextView.setText(Constants.DAY_NAMES[calendar.get(Calendar.DAY_OF_WEEK)] + ", " +
                                               String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + " " +
                                               Constants.DECLENSION_MONTH_NAMES[calendar.get(Calendar.MONTH)]);
                ChosenYearTextView.setText(String.valueOf(pickedYear));
                break;


            case R.id.calendar_ok:
                String pickedDate = String.valueOf(pickedDay) + "." +
                                    String.valueOf(pickedMonth + 1) + "." +
                                    String.valueOf(pickedYear);
                Callback.getPickedDate(pickedDate);
                dismiss();
                break;
            case R.id.calendar_cancel:
                dismiss();
                break;

            default:
                break;
        }
    }

    @Override
    public View makeView() {
        TextView t = new TextView(activity);
        t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        t.setTextSize(24);
        t.setTextColor(Color.parseColor("#000000"));
        t.setMaxLines(1);

        return t;
    }







//    private void findMaxTextViewWidth() {
//        TextView tv = new TextView(activity);
//        tv.setTextSize(18);
//        tv.setText("Апрель");
//
//        tv.measure(0, 0);
//        MaxTextViewWidth = tv.getMeasuredWidth();
//        System.out.println(MaxTextViewWidth);
//    }
}
