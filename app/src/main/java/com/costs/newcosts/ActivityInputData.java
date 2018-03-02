package com.costs.newcosts;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;


public class ActivityInputData extends AppCompatActivity implements DialogDatePicker.MyDatePickerCallback, DialogFragmentExpensesList.ExpenseListDialogCallback {

    private TextView signTextView;
    private EditText inputValueEditText, inputNoteEditText;
    private String previousValueString;
    private LinearLayout inputValueEditTextCursor;
    private boolean previousTokenWasPlus;
    private int costID;
    private static final long CURRENT_DAY = -1;
    private static final long PREVIOUS_DAY = -2;
    private boolean hasStoredValue = false;
    private int MODE = -1;
    private int PREVIOUS_ACTIVITY_INDEX = -1;

    private DataUnitExpenses dataUnit;
    private TextView toolbarTextView;

    private DataUnitExpenses selectedDataUnit;
    private Button chooseDateButton;
    private String savedValue = "";
    private long editItemMilliseconds = -1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_data);

        // Получаем данные о выбранной статье расходов
        Bundle expenseDataBundle = getIntent().getExtras();
        if (expenseDataBundle == null)
            return;

        dataUnit = expenseDataBundle.getParcelable(Constants.EXPENSE_DATA_UNIT_LABEL);
        if (dataUnit == null)
            return;
        selectedDataUnit = dataUnit;
        editItemMilliseconds = dataUnit.getMilliseconds();

        MODE = expenseDataBundle.getInt(Constants.ACTIVITY_INPUT_DATA_MODE);
        PREVIOUS_ACTIVITY_INDEX = expenseDataBundle.getInt(Constants.PREVIOUS_ACTIVITY_INDEX);

        String costNameString = dataUnit.getExpenseName();
        costID = dataUnit.getExpenseId_N();
        String costValueString = dataUnit.getExpenseValueString();
        String costNoteString = dataUnit.getExpenseNoteString();

        // Инициализируем элементы интерфейса
        signTextView = (TextView) findViewById(R.id.activity_input_data_sign_textview);
        signTextView.setText("");

        inputValueEditTextCursor = (LinearLayout) findViewById(R.id.activity_input_data_edit_text_cursor);
        inputValueEditTextCursor.setAnimation(startBlinking());

        inputValueEditText = (EditText) findViewById(R.id.activity_input_data_input_value_edittext);
        inputValueEditText.setFilters(new FilterDecimalDigitsInput[] {new FilterDecimalDigitsInput()});
        inputNoteEditText = (EditText) findViewById(R.id.activity_input_data_input_note_edittext);

        // При нажатии на стрелку - возвращаемся к предыдущему экрану, ничего не сохраняя
        ImageView toolbarBackArrow = (ImageView) findViewById(R.id.activity_input_data_arrow_back);
        toolbarBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToPreviousActivity();
            }
        });

        // Инициализируем элементы Toolbar'а
        ImageView toolbarExpandExpensesList = (ImageView) findViewById(R.id.activity_input_data_expand_expenses_list);
        toolbarTextView = (TextView) findViewById(R.id.activity_input_data_toolbar_text_view);
        LinearLayout toolbarDataLayout = (LinearLayout) findViewById(R.id.activity_input_data_toolbar_data_layout);


        // Toolbar имеет разное наполнение в зависимости от того, редактируется ли элемент
        // или вводится новое значение расходов
        if (MODE == Constants.INPUT_MODE) {
            // При вводе нового значения заполняем toolbar названием выбранной
            // категории расходов и суммарным значением расходов по этой
            // категории за текущий месяц
            toolbarTextView.setText(costNameString + ": " +
                    costValueString + " " +
                    getResources().getString(R.string.rur_string) + getResources().getString(R.string.dot_sign_string));
            toolbarExpandExpensesList.setVisibility(View.GONE);
        }
        if (MODE == Constants.EDIT_MODE) {
            // При редактировании элемента заполняем toolbar названием выбранного элемента и
            // возможностью открыть список всех существующих категорий расходов
            toolbarTextView.setText(costNameString);
            // При нажатии на "стрелку вниз" появляется диалоговое окно, в котором можно изменить
            // название выбранного элемента расходов
            toolbarDataLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DB_Costs cdb = DB_Costs.getInstance(ActivityInputData.this);
                    DataUnitExpenses[] dataForExpensesListDialog = cdb.getActiveCostNames_V3();

                    DialogFragmentExpensesList expensesListDialogFragment = DialogFragmentExpensesList.newInstance(dataForExpensesListDialog);
                    expensesListDialogFragment.show(getSupportFragmentManager(), Constants.EXPENSES_LIST_DIALOG_TAG);
                }
            });

            // Устанавливаем значение выбранного элемента
            inputValueEditText.setText(costValueString);
            inputValueEditText.setSelection(inputValueEditText.getText().length());

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dataUnit.getMilliseconds());

            // Скрываем кнопки с выбором сегодняшнего и вчерашнего дней для внесения значения расходов.
            // Кнопку выбора даты растягиваем на всю ширину экрана и устанавливаем в ней дату
            // выбранного элемента расходов
            Button dateTodayButton = (Button) findViewById(R.id.activity_input_data_date_today_button);
            Button dateYesterdayButton = (Button) findViewById(R.id.activity_input_data_date_yesterday_button);
            chooseDateButton = (Button) findViewById(R.id.activity_input_data_choose_date_button);
            chooseDateButton.setText(new StringBuilder()
                    .append(selectedDataUnit.getDay())
                    .append(" ")
                    .append(Constants.DECLENSION_MONTH_NAMES[selectedDataUnit.getMonth() - 1])
                    .append(" ")
                    .append(selectedDataUnit.getYear()));
            chooseDateButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            dateTodayButton.setVisibility(View.GONE);
            dateYesterdayButton.setVisibility(View.GONE);

            // Если у выбранного элемента есть заметка - устанавливаем её в соответсвующее поле
            if (dataUnit.HAS_NOTE) {
                inputNoteEditText.setText(costNoteString);
                inputNoteEditText.setSelection(inputNoteEditText.getText().length());
            }
        }

        previousValueString = "";
        previousTokenWasPlus = false;
    }

    // Обработчик нажатий кнопок выбора даты внесения расходов
    public void onDateButtonsClick(View view) {
        switch (view.getId()) {
            case R.id.activity_input_data_date_yesterday_button:
                calculateInputValues();
                saveData(PREVIOUS_DAY);
                break;
            case R.id.activity_input_data_date_today_button:
                calculateInputValues();
                saveData(CURRENT_DAY);
                break;
            case R.id.activity_input_data_choose_date_button:
                calculateInputValues();
                // При редактировании элемента устанавливаем дату, соответсвующую данному элементу
                if (MODE == Constants.INPUT_MODE) {
                    DialogDatePicker datePicker = new DialogDatePicker(ActivityInputData.this);
                    datePicker.show();
                }
                if (MODE == Constants.EDIT_MODE) {
                    DialogDatePicker datePicker = new DialogDatePicker(ActivityInputData.this, selectedDataUnit.getMilliseconds());
                    datePicker.show();
                }
                break;
        }
    }

    // Обработчик нажатий кнопок цифровой клавиатуры
    public void onKeyboardClick(View view) {
        Button pressedButton = (Button) view;
        String buttonLabel = String.valueOf(pressedButton.getText());
        String inputTextString = "";

        // Если после нажатия на "+" снова нажимаем на "+" (2++++ и т.д.)
        if (view.getId() == R.id.activity_input_data_add && previousTokenWasPlus)
            return;

        // Если после нажатия на "+" вводим другое число
        if (previousTokenWasPlus && !(view.getId() == R.id.activity_input_data_equal)) {
            inputValueEditText.setText("");
            previousTokenWasPlus = false;
        }

        switch (view.getId()) {
            case R.id.activity_input_data_zero:
                inputTextString = String.valueOf(inputValueEditText.getText());
                if (!"0".equals(inputTextString))
                    inputValueEditText.append(buttonLabel);
                break;
            case R.id.activity_input_data_one:
            case R.id.activity_input_data_two:
            case R.id.activity_input_data_three:
            case R.id.activity_input_data_four:
            case R.id.activity_input_data_five:
            case R.id.activity_input_data_six:
            case R.id.activity_input_data_seven:
            case R.id.activity_input_data_eight:
            case R.id.activity_input_data_nine:
                inputTextString = String.valueOf(inputValueEditText.getText());
                if ("0".equals(inputTextString))
                    inputValueEditText.setText(buttonLabel);
                else
                    inputValueEditText.append(buttonLabel);
                break;
            case R.id.activity_input_data_dot:
                inputTextString = String.valueOf(inputValueEditText.getText());
                if (!inputTextString.contains("."))
                    inputValueEditText.append(".");
                break;
            case R.id.activity_input_data_del:
                inputTextString = String.valueOf(inputValueEditText.getText());
                if (inputTextString.length() != 0) {
                    inputTextString = inputTextString.substring(0, inputTextString.length() - 1);
                    inputValueEditText.setText(inputTextString);
                    inputValueEditText.setSelection(inputValueEditText.getText().length());
                }
                break;
            case R.id.activity_input_data_add:
                signTextView.setText("+");
                previousTokenWasPlus = true;
                // Если после ввода следующего значения  был снова нажат "+" (1+2+)
                if (hasStoredValue) {
                    // Складываем введённые значения
                    calculateInputValues();

                    // Устанавливаем полученное значение как предыдущее (так как после символа "+"
                    // ожидается ввод нового значения, с которым нужно будет сложить полученное)
                    previousValueString = inputValueEditText.getText().toString();
                    hasStoredValue = true;
                } else {
                    previousValueString = inputValueEditText.getText().toString();
                    hasStoredValue = true;
                }
                break;
            case R.id.activity_input_data_equal:
                if (hasStoredValue) {
                    signTextView.setText("");
                    previousTokenWasPlus = false;

                    // Складываем введённые значения
                    calculateInputValues();

                    // Предыдущего значения больше нет, так как оно было сложено
                    // с текущим и выведено на экран
                    hasStoredValue = false;
                    previousValueString = "";
                }
                break;
            case R.id.activity_input_data_ok:
                calculateInputValues();
                saveData(CURRENT_DAY);
                break;
        }
    }

    // Форматирует и устанавливает выбранную строку в 'inputValueEditText'
    public void setInputValueEditText(String s) {
        inputValueEditText.setText(s);
    }

    // Получает текущую строку, содеожащуюся в 'inputValueEditText'
    public String getInputValueEditTextString() {
        return inputValueEditText.getText().toString();
    }

    // Складывает текущее введённое значение с предыдущим
    private void calculateInputValues() {
        // Получаем и форматируем текущее и предыдущее введынные значения
        String currentInputEditTextValueString = inputValueEditText.getText().toString();
        if ("".equals(currentInputEditTextValueString) || ".".equals(currentInputEditTextValueString))
            currentInputEditTextValueString = "0";
        if ("".equals(previousValueString) || ".".equals(previousValueString))
            previousValueString = "0";

        double currentValue = 0.0;
        double previousValue = 0.0;

        // Приводим текущее и предыдущее значения к типу "double" и находим их сумму
        try {
            currentValue = Double.parseDouble(currentInputEditTextValueString);
            previousValue = Double.parseDouble(previousValueString);
        } catch (NumberFormatException e) {
            showAlertDialogWithMessage(getResources().getString(R.string.aid_showAlertDialogWithMessage_string));
            return;
        }
        currentValue = currentValue + previousValue;

        // Вставляем полученное значение в поле ввода значений
        inputValueEditText.setText(Constants.formatDigit(currentValue));
        inputValueEditText.setSelection(inputValueEditText.getText().length());
    }

    // Сохраняет введённое значение в базу
    private boolean saveData(long milliseconds) {
        String inputValueString = inputValueEditText.getText().toString();
        String inputNoteString = inputNoteEditText.getText().toString();
        DB_Costs cdb = DB_Costs.getInstance(this);
        double inputValue = 0.0;

        try {
            inputValue = Double.parseDouble(inputValueString);
        } catch (NumberFormatException e) {
            showAlertDialogWithMessage(getResources().getString(R.string.aid_showAlertDialogWithMessage_string));
            return false;
        }

        // Если введённое значение = 0 - не сохраняем его
        if (Double.compare(inputValue, 0.0) == 0) {
            showAlertDialogWithMessage(getResources().getString(R.string.aid_showAlertDialogWithMessage_v2_string));
            return false;
        }

        savedValue = inputValueString;

        // Если вводим новый элемент - просто сохраняем его в базу.
        // Если редактируем существующий - сначала удаляем старый элемент, затем вносим отредактированный
        if (MODE == Constants.INPUT_MODE) {
            if (milliseconds == CURRENT_DAY) {
                cdb.addCosts(inputValue, costID, inputNoteString);
                returnToPreviousActivity();
            } else if (milliseconds == PREVIOUS_DAY) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                long millis = calendar.getTimeInMillis();
                cdb.addCostInMilliseconds(costID, inputValueString, millis, inputNoteString);
                returnToPreviousActivity();
            } else {
                cdb.addCostInMilliseconds(costID, inputValueString, milliseconds, inputNoteString);
                returnToPreviousActivity();
            }
        }
        if (MODE == Constants.EDIT_MODE) {
            cdb = DB_Costs.getInstance(this);
            cdb.removeCostValue(editItemMilliseconds);
            Constants.EDITED_ITEM_MILLISECONDS = cdb.addCostInMilliseconds(selectedDataUnit.getExpenseId_N(), inputValueString,
                                                                            selectedDataUnit.getMilliseconds(), inputNoteString);
            returnToPreviousActivity();
        }

        return true;
    }

    // Показ всплывающего окна при некорректном вводе данных
    private void showAlertDialogWithMessage(String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(message);
        dialogBuilder.setCancelable(true);
        dialogBuilder.setPositiveButton(getResources().getString(R.string.aid_dialogBuilder_positive_button_string), null);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }


    // После сохранения введённого значения возвращаемся к предыдущему экрану
    private void returnToPreviousActivity() {
        if (PREVIOUS_ACTIVITY_INDEX == -1) {
            showAlertDialogWithMessage(getResources().getString(R.string.aid_showAlertDialogWithMessage_v3_string));
            return;
        }

        // Возвращаемся к предыдущему экрану
        switch (PREVIOUS_ACTIVITY_INDEX) {
            case Constants.FRAGMENT_CURRENT_MONTH_SCREEN:
                Intent currentMonthScreenIntent = new Intent(ActivityInputData.this, ActivityMainWithFragments.class);
                currentMonthScreenIntent.putExtra(Constants.PREVIOUS_ACTIVITY_INDEX, Constants.EDIT_DATA_ACTIVITY);
                currentMonthScreenIntent.putExtra(Constants.SAVED_VALUE, savedValue);
                currentMonthScreenIntent.putExtra(Constants.TARGET_TAB, Constants.FRAGMENT_CURRENT_MONTH_SCREEN);
                currentMonthScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(currentMonthScreenIntent);
                break;
            case Constants.FRAGMENT_LAST_ENTERED_VALUES_SCREEN:
                Intent lastEnteredValuesScreenIntent = new Intent(ActivityInputData.this, ActivityMainWithFragments.class);
                lastEnteredValuesScreenIntent.putExtra(Constants.PREVIOUS_ACTIVITY_INDEX, Constants.EDIT_DATA_ACTIVITY);
                lastEnteredValuesScreenIntent.putExtra(Constants.SAVED_VALUE, savedValue);
                lastEnteredValuesScreenIntent.putExtra(Constants.TARGET_TAB, Constants.FRAGMENT_LAST_ENTERED_VALUES_SCREEN);
                lastEnteredValuesScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(lastEnteredValuesScreenIntent);
                break;
            case Constants.STATISTIC_DETAILED_ACTIVITY:
                Intent statisticDetailedActivityIntent = new Intent(ActivityInputData.this, ActivityStatisticExpenseTypeDetailed.class);
                statisticDetailedActivityIntent.putExtra(Constants.PREVIOUS_ACTIVITY_INDEX, Constants.EDIT_DATA_ACTIVITY);
                statisticDetailedActivityIntent.putExtra(Constants.SAVED_VALUE, savedValue);
                statisticDetailedActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(statisticDetailedActivityIntent);
                break;
        }
    }


    // Анимация курсора ввода значения расходов
    private Animation startBlinking(){
        Animation fadeIn = new AlphaAnimation(1, 0);
        fadeIn.setInterpolator(new LinearInterpolator());
        fadeIn.setDuration(1000);
        fadeIn.setRepeatCount(-1);

        return fadeIn;
    }

    // При редактировании существующего элемента получаем название выбранной
    // статьи расходов из списка статей расходов при нажатии на "стрелку вниз"
    @Override
    public void getSelectedExpense(DataUnitExpenses dataUnit) {
        toolbarTextView.setText(dataUnit.getExpenseName());
        selectedDataUnit.setExpenseId_N(dataUnit.getExpenseId_N());
        selectedDataUnit.setExpenseName(dataUnit.getExpenseName());
    }

    @Override
    public void getPickedDate(String pickedDate) {
        String[] pickedDateArray = pickedDate.split("\\.");

        int pickedDay = Integer.valueOf(pickedDateArray[0]);
        int pickedMonth = Integer.valueOf(pickedDateArray[1]);
        int pickedYear = Integer.valueOf(pickedDateArray[2]);

        Calendar calendar = Calendar.getInstance();
        long currentTimeInMilliseconds = calendar.getTimeInMillis();

        calendar.set(Calendar.DAY_OF_MONTH, pickedDay);
        calendar.set(Calendar.MONTH, pickedMonth - 1);
        calendar.set(Calendar.YEAR, pickedYear);
        final long pickedTimeInMilliseconds = calendar.getTimeInMillis();

        if (pickedTimeInMilliseconds > currentTimeInMilliseconds) {
            String wrongPickedDateMessage = getResources().getString(R.string.aid_wrongPickedDateMessage_string);
            showAlertDialogWithMessage(wrongPickedDateMessage);
        } else {
            if (MODE == Constants.INPUT_MODE) {
                saveData(pickedTimeInMilliseconds);
            }
            if (MODE == Constants.EDIT_MODE) {
                selectedDataUnit.setDay(pickedDay);
                selectedDataUnit.setMonth(pickedMonth);
                selectedDataUnit.setYear(pickedYear);
                selectedDataUnit.setMilliseconds(pickedTimeInMilliseconds);

                chooseDateButton.setText(new StringBuilder()
                        .append(selectedDataUnit.getDay())
                        .append(" ")
                        .append(Constants.DECLENSION_MONTH_NAMES[selectedDataUnit.getMonth() - 1])
                        .append(" ")
                        .append(selectedDataUnit.getYear()));
            }
        }
    }

    @Override
    public void onBackPressed() {
        returnToPreviousActivity();
    }
}




















// ======================== 15.08.2017 ===========================
//package com.touristskaya.expenses;
//
//import android.app.AlertDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.View;
//import android.view.animation.AlphaAnimation;
//import android.view.animation.Animation;
//import android.view.animation.LinearInterpolator;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import java.util.Calendar;
//
//
//public class ActivityInputData extends AppCompatActivity implements DialogDatePicker.MyDatePickerCallback, DialogFragmentExpensesList.ExpenseListDialogCallback {
//
//    private TextView signTextView;
//    private EditText inputValueEditText, inputNoteEditText;
//    private String previousValueString;
//    private LinearLayout inputValueEditTextCursor;
//    private boolean previousTokenWasPlus;
//    private int costID;
//    private static final long CURRENT_DAY = -1;
//    private static final long PREVIOUS_DAY = -2;
//    private boolean hasStoredValue = false;
//    private int MODE = -1;
//    private int PREVIOUS_ACTIVITY_INDEX = -1;
//
//    private DataUnitExpenses dataUnit;
//    private TextView toolbarTextView;
//
//    private DataUnitExpenses selectedDataUnit;
//    private Button chooseDateButton;
//    private String savedValue = "";
//    private long editItemMilliseconds = -1;
//
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_input_data);
//
//        // Получаем данные о выбранной статье расходов
//        Bundle expenseDataBundle = getIntent().getExtras();
//        if (expenseDataBundle == null)
//            return;
//
//        dataUnit = expenseDataBundle.getParcelable(Constants.EXPENSE_DATA_UNIT_LABEL);
//        if (dataUnit == null)
//            return;
//        selectedDataUnit = dataUnit;
//        editItemMilliseconds = dataUnit.getMilliseconds();
//
//        MODE = expenseDataBundle.getInt(Constants.ACTIVITY_INPUT_DATA_MODE);
//        PREVIOUS_ACTIVITY_INDEX = expenseDataBundle.getInt(Constants.PREVIOUS_ACTIVITY_INDEX);
//
//        String costNameString = dataUnit.getExpenseName();
//        costID = dataUnit.getExpenseId_N();
//        String costValueString = dataUnit.getExpenseValueString();
//        String costNoteString = dataUnit.getExpenseNoteString();
//
//        // Инициализируем элементы интерфейса
//        signTextView = (TextView) findViewById(R.id.activity_input_data_sign_textview);
//        signTextView.setText("");
//
//        inputValueEditTextCursor = (LinearLayout) findViewById(R.id.activity_input_data_edit_text_cursor);
//        inputValueEditTextCursor.setAnimation(startBlinking());
//
//        inputValueEditText = (EditText) findViewById(R.id.activity_input_data_input_value_edittext);
//        inputValueEditText.setFilters(new FilterDecimalDigitsInput[] {new FilterDecimalDigitsInput()});
//        inputNoteEditText = (EditText) findViewById(R.id.activity_input_data_input_note_edittext);
//
//        // При нажатии на стрелку - возвращаемся к предыдущему экрану, ничего не сохраняя
//        ImageView toolbarBackArrow = (ImageView) findViewById(R.id.activity_input_data_arrow_back);
//        toolbarBackArrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                returnToPreviousActivity();
//            }
//        });
//
//        // Инициализируем элементы Toolbar'а
//        ImageView toolbarExpandExpensesList = (ImageView) findViewById(R.id.activity_input_data_expand_expenses_list);
//        toolbarTextView = (TextView) findViewById(R.id.activity_input_data_toolbar_text_view);
//        LinearLayout toolbarDataLayout = (LinearLayout) findViewById(R.id.activity_input_data_toolbar_data_layout);
//
//
//        // Toolbar имеет разное наполнение в зависимости от того, редактируется ли элемент
//        // или вводится новое значение расходов
//        if (MODE == Constants.INPUT_MODE) {
//            // При вводе нового значения заполняем toolbar названием выбранной
//            // категории расходов и суммарным значением расходов по этой
//            // категории за текущий месяц
//            toolbarTextView.setText(costNameString + ": " +
//                    costValueString + " " +
//                    getResources().getString(R.string.rur_string) + getResources().getString(R.string.dot_sign_string));
//            toolbarExpandExpensesList.setVisibility(View.GONE);
//        }
//        if (MODE == Constants.EDIT_MODE) {
//            // При редактировании элемента заполняем toolbar названием выбранного элемента и
//            // возможностью открыть список всех существующих категорий расходов
//            toolbarTextView.setText(costNameString);
//            // При нажатии на "стрелку вниз" появляется диалоговое окно, в котором можно изменить
//            // название выбранного элемента расходов
//            toolbarDataLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    DB_Costs cdb = DB_Costs.getInstance(ActivityInputData.this);
//                    DataUnitExpenses[] dataForExpensesListDialog = cdb.getActiveCostNames_V3();
//
//                    DialogFragmentExpensesList expensesListDialogFragment = DialogFragmentExpensesList.newInstance(dataForExpensesListDialog);
//                    expensesListDialogFragment.show(getSupportFragmentManager(), Constants.EXPENSES_LIST_DIALOG_TAG);
//                }
//            });
//
//            // Устанавливаем значение выбранного элемента
//            inputValueEditText.setText(costValueString);
//            inputValueEditText.setSelection(inputValueEditText.getText().length());
//
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTimeInMillis(dataUnit.getMilliseconds());
//
//            // Скрываем кнопки с выбором сегодняшнего и вчерашнего дней для внесения значения расходов.
//            // Кнопку выбора даты растягиваем на всю ширину экрана и устанавливаем в ней дату
//            // выбранного элемента расходов
//            Button dateTodayButton = (Button) findViewById(R.id.activity_input_data_date_today_button);
//            Button dateYesterdayButton = (Button) findViewById(R.id.activity_input_data_date_yesterday_button);
//            chooseDateButton = (Button) findViewById(R.id.activity_input_data_choose_date_button);
//            chooseDateButton.setText(new StringBuilder()
//                    .append(selectedDataUnit.getDay())
//                    .append(" ")
//                    .append(Constants.DECLENSION_MONTH_NAMES[selectedDataUnit.getMonth() - 1])
//                    .append(" ")
//                    .append(selectedDataUnit.getYear()));
//            chooseDateButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//
//            dateTodayButton.setVisibility(View.GONE);
//            dateYesterdayButton.setVisibility(View.GONE);
//
//            // Если у выбранного элемента есть заметка - устанавливаем её в соответсвующее поле
//            if (dataUnit.HAS_NOTE) {
//                inputNoteEditText.setText(costNoteString);
//                inputNoteEditText.setSelection(inputNoteEditText.getText().length());
//            }
//        }
//
//        previousValueString = "";
//        previousTokenWasPlus = false;
//    }
//
//    // Обработчик нажатий кнопок выбора даты внесения расходов
//    public void onDateButtonsClick(View view) {
//        switch (view.getId()) {
//            case R.id.activity_input_data_date_yesterday_button:
//                calculateInputValues();
//                saveData(PREVIOUS_DAY);
//                break;
//            case R.id.activity_input_data_date_today_button:
//                calculateInputValues();
//                saveData(CURRENT_DAY);
//                break;
//            case R.id.activity_input_data_choose_date_button:
//                calculateInputValues();
//                // При редактировании элемента устанавливаем дату, соответсвующую данному элементу
//                if (MODE == Constants.INPUT_MODE) {
//                    DialogDatePicker datePicker = new DialogDatePicker(ActivityInputData.this);
//                    datePicker.show();
//                }
//                if (MODE == Constants.EDIT_MODE) {
//                    DialogDatePicker datePicker = new DialogDatePicker(ActivityInputData.this, selectedDataUnit.getMilliseconds());
//                    datePicker.show();
//                }
//                break;
//        }
//    }
//
//    // Обработчик нажатий кнопок цифровой клавиатуры
//    public void onKeyboardClick(View view) {
//        Button pressedButton = (Button) view;
//        String buttonLabel = String.valueOf(pressedButton.getText());
//        String inputTextString = "";
//
//        // Если после нажатия на "+" снова нажимаем на "+" (2++++ и т.д.)
//        if (view.getId() == R.id.activity_input_data_add && previousTokenWasPlus)
//            return;
//
//        // Если после нажатия на "+" вводим другое число
//        if (previousTokenWasPlus && !(view.getId() == R.id.activity_input_data_equal)) {
//            inputValueEditText.setText("");
//            previousTokenWasPlus = false;
//        }
//
//        switch (view.getId()) {
//            case R.id.activity_input_data_zero:
//                inputTextString = String.valueOf(inputValueEditText.getText());
//                if (!"0".equals(inputTextString))
//                    inputValueEditText.append(buttonLabel);
//                break;
//            case R.id.activity_input_data_one:
//            case R.id.activity_input_data_two:
//            case R.id.activity_input_data_three:
//            case R.id.activity_input_data_four:
//            case R.id.activity_input_data_five:
//            case R.id.activity_input_data_six:
//            case R.id.activity_input_data_seven:
//            case R.id.activity_input_data_eight:
//            case R.id.activity_input_data_nine:
//                inputTextString = String.valueOf(inputValueEditText.getText());
//                if ("0".equals(inputTextString))
//                    inputValueEditText.setText(buttonLabel);
//                else
//                    inputValueEditText.append(buttonLabel);
//                break;
//            case R.id.activity_input_data_dot:
//                inputTextString = String.valueOf(inputValueEditText.getText());
//                if (!inputTextString.contains("."))
//                    inputValueEditText.append(".");
//                break;
//            case R.id.activity_input_data_del:
//                inputTextString = String.valueOf(inputValueEditText.getText());
//                if (inputTextString.length() != 0) {
//                    inputTextString = inputTextString.substring(0, inputTextString.length() - 1);
//                    inputValueEditText.setText(inputTextString);
//                    inputValueEditText.setSelection(inputValueEditText.getText().length());
//                }
//                break;
//            case R.id.activity_input_data_add:
//                signTextView.setText("+");
//                previousTokenWasPlus = true;
//                // Если после ввода следующего значения  был снова нажат "+" (1+2+)
//                if (hasStoredValue) {
//                    // Складываем введённые значения
//                    calculateInputValues();
//
//                    // Устанавливаем полученное значение как предыдущее (так как после символа "+"
//                    // ожидается ввод нового значения, с которым нужно будет сложить полученное)
//                    previousValueString = inputValueEditText.getText().toString();
//                    hasStoredValue = true;
//                } else {
//                    previousValueString = inputValueEditText.getText().toString();
//                    hasStoredValue = true;
//                }
//                break;
//            case R.id.activity_input_data_equal:
//                if (hasStoredValue) {
//                    signTextView.setText("");
//                    previousTokenWasPlus = false;
//
//                    // Складываем введённые значения
//                    calculateInputValues();
//
//                    // Предыдущего значения больше нет, так как оно было сложено
//                    // с текущим и выведено на экран
//                    hasStoredValue = false;
//                    previousValueString = "";
//                }
//                break;
//            case R.id.activity_input_data_ok:
//                calculateInputValues();
//                saveData(CURRENT_DAY);
//                break;
//        }
//    }
//
//    // Форматирует и устанавливает выбранную строку в 'inputValueEditText'
//    public void setInputValueEditText(String s) {
//        inputValueEditText.setText(s);
//    }
//
//    // Получает текущую строку, содеожащуюся в 'inputValueEditText'
//    public String getInputValueEditText() {
//        return inputValueEditText.getText().toString();
//    }
//
//    // Складывает текущее введённое значение с предыдущим
//    private void calculateInputValues() {
//        // Получаем и форматируем текущее и предыдущее введынные значения
//        String currentInputEditTextValueString = inputValueEditText.getText().toString();
//        if ("".equals(currentInputEditTextValueString) || ".".equals(currentInputEditTextValueString))
//            currentInputEditTextValueString = "0";
//        if ("".equals(previousValueString) || ".".equals(previousValueString))
//            previousValueString = "0";
//
//        double currentValue = 0.0;
//        double previousValue = 0.0;
//
//        // Приводим текущее и предыдущее значения к типу "double" и находим их сумму
//        try {
//            currentValue = Double.parseDouble(currentInputEditTextValueString);
//            previousValue = Double.parseDouble(previousValueString);
//        } catch (NumberFormatException e) {
//            showAlertDialogWithMessage(getResources().getString(R.string.aid_showAlertDialogWithMessage_string));
//            return;
//        }
//        currentValue = currentValue + previousValue;
//
//        // Вставляем полученное значение в поле ввода значений
//        inputValueEditText.setText(Constants.formatDigit(currentValue));
//        inputValueEditText.setSelection(inputValueEditText.getText().length());
//    }
//
//    // Сохраняет введённое значение в базу
//    private boolean saveData(long milliseconds) {
//        String inputValueString = inputValueEditText.getText().toString();
//        String inputNoteString = inputNoteEditText.getText().toString();
//        DB_Costs cdb = DB_Costs.getInstance(this);
//        double inputValue = 0.0;
//
//        try {
//            inputValue = Double.parseDouble(inputValueString);
//        } catch (NumberFormatException e) {
//            showAlertDialogWithMessage(getResources().getString(R.string.aid_showAlertDialogWithMessage_string));
//            return false;
//        }
//
//        // Если введённое значение = 0 - не сохраняем его
//        if (Double.compare(inputValue, 0.0) == 0) {
//            showAlertDialogWithMessage(getResources().getString(R.string.aid_showAlertDialogWithMessage_v2_string));
//            return false;
//        }
//
//        savedValue = inputValueString;
//
//        // Если вводим новый элемент - просто сохраняем его в базу.
//        // Если редактируем существующий - сначала удаляем старый элемент, затем вносим отредактированный
//        if (MODE == Constants.INPUT_MODE) {
//            if (milliseconds == CURRENT_DAY) {
//                cdb.addCosts(inputValue, costID, inputNoteString);
//                returnToPreviousActivity();
//            } else if (milliseconds == PREVIOUS_DAY) {
//                Calendar calendar = Calendar.getInstance();
//                calendar.add(Calendar.DAY_OF_MONTH, -1);
//                long millis = calendar.getTimeInMillis();
//                cdb.addCostInMilliseconds(costID, inputValueString, millis, inputNoteString);
//                returnToPreviousActivity();
//            } else {
//                cdb.addCostInMilliseconds(costID, inputValueString, milliseconds, inputNoteString);
//                returnToPreviousActivity();
//            }
//        }
//        if (MODE == Constants.EDIT_MODE) {
//            cdb = DB_Costs.getInstance(this);
//            cdb.removeCostValue(editItemMilliseconds);
//            Constants.EDITED_ITEM_MILLISECONDS = cdb.addCostInMilliseconds(selectedDataUnit.getExpenseId_N(), inputValueString,
//                                                                            selectedDataUnit.getMilliseconds(), inputNoteString);
//            returnToPreviousActivity();
//        }
//
//        return true;
//    }
//
//    // Показ всплывающего окна при некорректном вводе данных
//    private void showAlertDialogWithMessage(String message) {
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//        dialogBuilder.setMessage(message);
//        dialogBuilder.setCancelable(true);
//        dialogBuilder.setPositiveButton(getResources().getString(R.string.aid_dialogBuilder_positive_button_string), null);
//
//        AlertDialog alertDialog = dialogBuilder.create();
//        alertDialog.show();
//    }
//
//
//    // После сохранения введённого значения возвращаемся к предыдущему экрану
//    private void returnToPreviousActivity() {
//        if (PREVIOUS_ACTIVITY_INDEX == -1) {
//            showAlertDialogWithMessage(getResources().getString(R.string.aid_showAlertDialogWithMessage_v3_string));
//            return;
//        }
//
//        // Возвращаемся к предыдущему экрану
//        switch (PREVIOUS_ACTIVITY_INDEX) {
//            case Constants.FRAGMENT_CURRENT_MONTH_SCREEN:
//                Intent currentMonthScreenIntent = new Intent(ActivityInputData.this, ActivityMainWithFragments.class);
//                currentMonthScreenIntent.putExtra(Constants.PREVIOUS_ACTIVITY_INDEX, Constants.EDIT_DATA_ACTIVITY);
//                currentMonthScreenIntent.putExtra(Constants.SAVED_VALUE, savedValue);
//                currentMonthScreenIntent.putExtra(Constants.TARGET_TAB, Constants.FRAGMENT_CURRENT_MONTH_SCREEN);
//                currentMonthScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(currentMonthScreenIntent);
//                break;
//            case Constants.FRAGMENT_LAST_ENTERED_VALUES_SCREEN:
//                Intent lastEnteredValuesScreenIntent = new Intent(ActivityInputData.this, ActivityMainWithFragments.class);
//                lastEnteredValuesScreenIntent.putExtra(Constants.PREVIOUS_ACTIVITY_INDEX, Constants.EDIT_DATA_ACTIVITY);
//                lastEnteredValuesScreenIntent.putExtra(Constants.SAVED_VALUE, savedValue);
//                lastEnteredValuesScreenIntent.putExtra(Constants.TARGET_TAB, Constants.FRAGMENT_LAST_ENTERED_VALUES_SCREEN);
//                lastEnteredValuesScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(lastEnteredValuesScreenIntent);
//                break;
//            case Constants.STATISTIC_DETAILED_ACTIVITY:
//                Intent statisticDetailedActivityIntent = new Intent(ActivityInputData.this, ActivityStatisticExpenseTypeDetailed.class);
//                statisticDetailedActivityIntent.putExtra(Constants.PREVIOUS_ACTIVITY_INDEX, Constants.EDIT_DATA_ACTIVITY);
//                statisticDetailedActivityIntent.putExtra(Constants.SAVED_VALUE, savedValue);
//                statisticDetailedActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(statisticDetailedActivityIntent);
//                break;
//        }
//    }
//
//
//    // Анимация курсора ввода значения расходов
//    private Animation startBlinking(){
//        Animation fadeIn = new AlphaAnimation(1, 0);
//        fadeIn.setInterpolator(new LinearInterpolator());
//        fadeIn.setDuration(1000);
//        fadeIn.setRepeatCount(-1);
//
//        return fadeIn;
//    }
//
//    // При редактировании существующего элемента получаем название выбранной
//    // статьи расходов из списка статей расходов при нажатии на "стрелку вниз"
//    @Override
//    public void getSelectedExpense(DataUnitExpenses dataUnit) {
//        toolbarTextView.setText(dataUnit.getExpenseName());
//        selectedDataUnit.setExpenseId_N(dataUnit.getExpenseId_N());
//        selectedDataUnit.setExpenseName(dataUnit.getExpenseName());
//    }
//
//    @Override
//    public void getPickedDate(String pickedDate) {
//        String[] pickedDateArray = pickedDate.split("\\.");
//
//        int pickedDay = Integer.valueOf(pickedDateArray[0]);
//        int pickedMonth = Integer.valueOf(pickedDateArray[1]);
//        int pickedYear = Integer.valueOf(pickedDateArray[2]);
//
//        Calendar calendar = Calendar.getInstance();
//        long currentTimeInMilliseconds = calendar.getTimeInMillis();
//
//        calendar.set(Calendar.DAY_OF_MONTH, pickedDay);
//        calendar.set(Calendar.MONTH, pickedMonth - 1);
//        calendar.set(Calendar.YEAR, pickedYear);
//        final long pickedTimeInMilliseconds = calendar.getTimeInMillis();
//
//        if (pickedTimeInMilliseconds > currentTimeInMilliseconds) {
//            String wrongPickedDateMessage = getResources().getString(R.string.aid_wrongPickedDateMessage_string);
//            showAlertDialogWithMessage(wrongPickedDateMessage);
//        } else {
//            if (MODE == Constants.INPUT_MODE) {
//                saveData(pickedTimeInMilliseconds);
//            }
//            if (MODE == Constants.EDIT_MODE) {
//                selectedDataUnit.setDay(pickedDay);
//                selectedDataUnit.setMonth(pickedMonth);
//                selectedDataUnit.setYear(pickedYear);
//                selectedDataUnit.setMilliseconds(pickedTimeInMilliseconds);
//
//                chooseDateButton.setText(new StringBuilder()
//                        .append(selectedDataUnit.getDay())
//                        .append(" ")
//                        .append(Constants.DECLENSION_MONTH_NAMES[selectedDataUnit.getMonth() - 1])
//                        .append(" ")
//                        .append(selectedDataUnit.getYear()));
//            }
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        returnToPreviousActivity();
//    }
//}
