package com.costs.newcosts;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class FragmentCurrentMonthScreen extends Fragment {

    private Context context;
    private RecyclerView recyclerView;
    private TextView currentMonthTextView;
    private TextView overallValueTextView;
    private AdapterCurrentMonthScreenRecyclerView currentMonthScreenAdapter;
    private int currentMonth;
    private int currentYear;
    private int currentDay;
    private DB_Costs cdb;
    private List<DataUnitExpenses> listOfActiveCostNames;
    private Snackbar deleteCategorySnackbar;
    private double overallValueForCurrentMonth;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View currentMonthScreenView = inflater.inflate(R.layout.fragment_current_month_screen, container, false);
        recyclerView = (RecyclerView) currentMonthScreenView.findViewById(R.id.current_month_screen_recycler_view);
        currentMonthTextView = (TextView) currentMonthScreenView.findViewById(R.id.current_month_screen_month_textview);
        overallValueTextView = (TextView) currentMonthScreenView.findViewById(R.id.current_month_screen_overall_value_textview);

        return currentMonthScreenView;
    }


    @Override
    public void onResume() {
        super.onResume();

        cdb = DB_Costs.getInstance(context);

        // Получаем и устанавливаем текущую дату
        Calendar calendar = Calendar.getInstance();
        currentMonth = calendar.get(Calendar.MONTH);
        currentYear = calendar.get(Calendar.YEAR);
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        currentMonthTextView.setText(getResources().getString(R.string.fcm_currentMonthTextView_string)
                                        + " "
                                        + Constants.MONTH_NAMES[currentMonth]);


        // При первом запуске программы заносим категории расходов по умолчанию
        if (cdb.COSTS_DB_IS_EMPTY()) {
            String[] initialCategoriesArray = new String[] {
                    getResources().getString(R.string.fcm_initialCategoriesArray_Products_string),
                    getResources().getString(R.string.fcm_initialCategoriesArray_Purchases_string),
                    getResources().getString(R.string.fcm_initialCategoriesArray_Street_food_string),
                    getResources().getString(R.string.fcm_initialCategoriesArray_Transport_string),
                    getResources().getString(R.string.fcm_initialCategoriesArray_Costs_for_apartment_string),
                    getResources().getString(R.string.fcm_initialCategoriesArray_Entertainment_string),
                    getResources().getString(R.string.fcm_initialCategoriesArray_Other_string)
            };

            for (String category : initialCategoriesArray)
                cdb.addCostName(category);
        }


        // Получаем массив активных категорий расходов, получаем сумму расходов по
        // каждой категории в текущем месяце и суммарное значение затрат за текущий месяц
        overallValueForCurrentMonth = 0.0;
        DataUnitExpenses[] activeExpenseNamesArray = cdb.getActiveCostNames_V3();
        listOfActiveCostNames = new ArrayList<>();
        for (int i = 0; i < activeExpenseNamesArray.length; ++i) {
            double singleUnitExpenseValue = cdb.getCostValue(-1, currentMonth, currentYear, activeExpenseNamesArray[i].getExpenseId_N());
            activeExpenseNamesArray[i].setExpenseValueDouble(singleUnitExpenseValue);
            activeExpenseNamesArray[i].setExpenseValueString(Constants.formatDigit(singleUnitExpenseValue));
            listOfActiveCostNames.add(activeExpenseNamesArray[i]);
            overallValueForCurrentMonth = overallValueForCurrentMonth + singleUnitExpenseValue;
        }
        // Последним элементом списка явлеятся пункт "Добавить новую категорию"
        DataUnitExpenses addNewCategoryDataUnit = new DataUnitExpenses();
        addNewCategoryDataUnit.setExpenseId_N(Integer.MIN_VALUE);
        addNewCategoryDataUnit.setExpenseName(getResources().getString(R.string.fcm_addNewCategoryDataUnit_string));
        addNewCategoryDataUnit.setExpenseValueString("+");
        listOfActiveCostNames.add(addNewCategoryDataUnit);
        // Устанавливаем суммарное значение затрат за текущий месяц
        overallValueTextView.setText(Constants.formatDigit(overallValueForCurrentMonth)
                                            + " "
                                            + getResources().getString(R.string.rur_string)
                                            + getResources().getString(R.string.dot_sign_string));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        currentMonthScreenAdapter = new AdapterCurrentMonthScreenRecyclerView(listOfActiveCostNames, context, this);
        // При нажатии на элемент списка расходов - переходим на экран ввода затрат по
        // выбранной категории расходов. При нажатии на пункт "Добавить новую категорию" -
        // появляется всплываюшее окно, в котором можно ввести название новой категории расходов
        currentMonthScreenAdapter.setClickListener(new AdapterCurrentMonthScreenRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                onRecyclerViewItemClick(itemView, position);
            }
        });

        // При длительном нажатии на элемент спика расходов появляется всплывающее окно, в котором
        // можно изменить название выбранной категории или удалить категорию, если по данной категории
        // нет записей расходов в текущем месяце
        currentMonthScreenAdapter.setLongClickListener(new AdapterCurrentMonthScreenRecyclerView.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, int position) {
                onRecyclerViewItemLongClick(itemView, position);
            }
        });

        recyclerView.setAdapter(currentMonthScreenAdapter);
        Constants.currentMonthFragmentDataIsActual(true);
    }

    // Обрабатываем кратковременное нажатие на элемент списка расходов
    public void onRecyclerViewItemClick(View itemView, int position) {
        DataUnitExpenses selectedDataUnit = listOfActiveCostNames.get(position);
        // Переходим на экран ввода затрат по выбранной категории
        if (selectedDataUnit.getExpenseId_N() != Integer.MIN_VALUE) {
            Constants.currentMonthFragmentDataIsActual(false);
            Intent inputDataActivityIntent = new Intent(context, ActivityInputData.class);
            inputDataActivityIntent.putExtra(Constants.EXPENSE_DATA_UNIT_LABEL, selectedDataUnit);
            inputDataActivityIntent.putExtra(Constants.ACTIVITY_INPUT_DATA_MODE, Constants.INPUT_MODE);
            inputDataActivityIntent.putExtra(Constants.PREVIOUS_ACTIVITY_INDEX, Constants.FRAGMENT_CURRENT_MONTH_SCREEN);
            startActivity(inputDataActivityIntent);
        }
        // Добавляем новую статью расходов
        else if (selectedDataUnit.getExpenseId_N() == Integer.MIN_VALUE) {
            // Далог добавления новой категории расходов
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.edit_expense_type_popup);

            // Инициализируем поле ввода названия новой статьи расходов
            final AutoCompleteTextView inputTextField = (AutoCompleteTextView) dialog.findViewById(R.id.edit_expense_type_popup_expense_type_textview);
            inputTextField.setFocusable(true);
            inputTextField.setCursorVisible(true);
            inputTextField.requestFocus();

            // Отображаем клавиатуру
            final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

            // Получаем неактивные названия категорий
            String[] nonActiveCostNames = cdb.getNonActiveCostNames();
            ArrayAdapter<String> autoCompleteTextViewAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, nonActiveCostNames);
            inputTextField.setAdapter(autoCompleteTextViewAdapter);

            // Инициализируем кнопки всплывающего окна
            Button addNewCostTypeButton = (Button) dialog.findViewById(R.id.edit_expense_type_popup_rename_expense_type_button);
            Button cancelButton = (Button) dialog.findViewById(R.id.edit_expense_type_popup_cancel_button);

            // Устанавливаем слушатели на кнопки
            addNewCostTypeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newExpenseName = inputTextField.getText().toString();
                    // Если название не введено - показываем сообщение
                    if (newExpenseName.isEmpty()) {
                        Toast emptyNameToast = Toast.makeText(context,
                                getResources().getString(R.string.fcm_emptyNameToast_string),
                                Toast.LENGTH_SHORT);
                        emptyNameToast.setGravity(Gravity.CENTER, 0, 0);
                        emptyNameToast.show();

                        return;
                    }
                    Constants.currentMonthFragmentDataIsActual(false);

                    // Добавляем новую запись в базу
                    cdb.addCostName(newExpenseName);

                    // Создаём элемент расходов с только что созданным названием
                    // и инициализируем его
                    DataUnitExpenses createdExpenseCategory = new DataUnitExpenses();
                    createdExpenseCategory.setExpenseName(newExpenseName);
                    createdExpenseCategory.setExpenseId_N(cdb.getExpenseIdByName(newExpenseName));
                    createdExpenseCategory.setExpenseValueDouble(0);
                    createdExpenseCategory.setExpenseValueString("0");

                    // Вставляем созданный элемент на последнюю позицию в списке
                    // статей расходов и обновляем представление
                    int positionInListToInsert = listOfActiveCostNames.size() - 1;
                    listOfActiveCostNames.add(positionInListToInsert, createdExpenseCategory);

                    currentMonthScreenAdapter.notifyItemInserted(positionInListToInsert);
                    currentMonthScreenAdapter.notifyDataSetChanged();

                    // Скрываем клавиатуру
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    dialog.dismiss();

                    // Сообщаем пользователю о добавлении новой категории
                    Snackbar newCategoryCreatedSnackbar = Snackbar
                            .make(recyclerView,
                                    getResources().getString(R.string.fcm_messageToUser_Category_string)
                                            + " '"
                                            + newExpenseName
                                            + "' "
                                            + getResources().getString(R.string.fcm_newCategoryCreatedSnackbar_created_string),
                                    Snackbar.LENGTH_LONG);
                    newCategoryCreatedSnackbar.show();
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    dialog.cancel();
                }
            });

            dialog.show();
        }
    }

    // Обрабатываем длительное нажатие на элемент списка расходов
    public void onRecyclerViewItemLongClick(View itemView, int position) {
        DataUnitExpenses selectedDataUnit = listOfActiveCostNames.get(position);
        if (selectedDataUnit.getExpenseId_N() == Integer.MIN_VALUE)
            return;

        DialogFragmentEditExpenseName editExpenseNameDialogFragment = DialogFragmentEditExpenseName.newInstance(listOfActiveCostNames.get(position));
        editExpenseNameDialogFragment.setTargetFragment(FragmentCurrentMonthScreen.this, Constants.EDIT_EXPENSE_NAME_REQUEST_CODE);
        editExpenseNameDialogFragment.show(getFragmentManager(), Constants.EDIT_DIALOG_TAG);
    }

    // Обработка результата нажатия кнопок в диалоговом окне, отображающемся
    // при нажатии на значок редактирования категории расходов либо при
    // длительном нажатии на элемент списка расходов
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.EDIT_EXPENSE_NAME_REQUEST_CODE) {
            // Получаем название и ID выбранной категории
            DataUnitExpenses selectedCategory = null;
            if (data != null) {
                selectedCategory = data.getExtras().getParcelable(Constants.EXPENSE_DATA_UNIT_LABEL);
            }

            switch (resultCode) {
                case Constants.DELETE_ITEM:
                    Constants.currentMonthFragmentDataIsActual(false);
                    if (selectedCategory != null) {
                        // Если по выбранной на удаление категории нет записей в
                        //  текущем месяце - её можно удалить
                        if (selectedCategory.getExpenseValueDouble() > 0) {
                            Snackbar hasRecordsInCurrentMonthSnackbar = Snackbar
                                    .make(recyclerView,
                                            getResources().getString(R.string.fcm_hasRecordsInCurrentMonthSnackbar_string),
                                            Snackbar.LENGTH_LONG);
                            hasRecordsInCurrentMonthSnackbar.show();
                        } else {
                            // Удаляем выбранную категорию расходов
                            final int selectedCategoryIndexInList = listOfActiveCostNames.indexOf(selectedCategory);
                            cdb.deleteCostName(selectedCategory.getExpenseId_N());
                            listOfActiveCostNames.remove(selectedCategoryIndexInList);
                            currentMonthScreenAdapter.notifyItemRemoved(selectedCategoryIndexInList);

                            // Отображаем сообщение об удалении выбранного элемента
                            // с возможностью его восстановления при нажатии кнопки "Отмена"
                            final DataUnitExpenses finalSelectedCategory = selectedCategory;
                            deleteCategorySnackbar = Snackbar
                                    .make(recyclerView,
                                            getResources().getString(R.string.fcm_deleteCategorySnackbar_string),
                                            Snackbar.LENGTH_LONG)
                                    .setAction(getResources().getString(R.string.fcm_deleteCategorySnackbar_action_cancel_string), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            listOfActiveCostNames.add(selectedCategoryIndexInList, finalSelectedCategory);
                                            currentMonthScreenAdapter.notifyItemInserted(selectedCategoryIndexInList);
                                            currentMonthScreenAdapter.notifyDataSetChanged();

                                            cdb.addCostName(finalSelectedCategory.getExpenseName());

                                            Snackbar restoreItemSnackbar = Snackbar
                                                    .make(recyclerView,
                                                            getResources().getString(R.string.fcm_restoreItemSnackbar_string),
                                                            Snackbar.LENGTH_LONG);
                                            restoreItemSnackbar.show();
                                        }
                                    })
                                    .setActionTextColor(ContextCompat.getColor(context, R.color.deleteRed));
                            deleteCategorySnackbar.show();
                        }
                    }
                    break;
                case Constants.EDIT_ITEM:
                    Constants.currentMonthFragmentDataIsActual(false);
                    if (selectedCategory != null) {
                        // Далог редактирования категории расходов
                        final Dialog dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.edit_expense_type_popup);

                        // Инициализируем поле переименования статьи расходов
                        final AutoCompleteTextView inputTextField = (AutoCompleteTextView) dialog.findViewById(R.id.edit_expense_type_popup_expense_type_textview);
                        inputTextField.setText(selectedCategory.getExpenseName());
                        inputTextField.setSelection(inputTextField.getText().length());
                        inputTextField.setFocusable(true);
                        inputTextField.setCursorVisible(true);
                        inputTextField.requestFocus();

                        // Отображаем клавиатуру
                        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                        // Инициализируем кнопки всплывающего окна
                        Button addNewCostTypeButton = (Button) dialog.findViewById(R.id.edit_expense_type_popup_rename_expense_type_button);
                        addNewCostTypeButton.setText(getResources().getString(R.string.fcm_addNewCostTypeButton_string));
                        Button cancelButton = (Button) dialog.findViewById(R.id.edit_expense_type_popup_cancel_button);

                        // Устанавливаем слушатели на кнопки
                        final DataUnitExpenses finalSelectedCategory1 = selectedCategory;
                        addNewCostTypeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String oldCategoryName = finalSelectedCategory1.getExpenseName();
                                String newCategoryName = inputTextField.getText().toString();
                                int result = cdb.renameCostName(finalSelectedCategory1.getExpenseId_N(), newCategoryName);

                                int selectedCategoryIndexInList = listOfActiveCostNames.indexOf(finalSelectedCategory1);
                                System.out.println(selectedCategoryIndexInList);

                                listOfActiveCostNames.get(selectedCategoryIndexInList).setExpenseName(newCategoryName);
                                currentMonthScreenAdapter.notifyDataSetChanged();

                                String messageToUser = "";
                                switch (result) {
                                    case 0:
                                        messageToUser = new StringBuilder()
                                                .append("'")
                                                .append(oldCategoryName)
                                                .append("' -> '")
                                                .append(newCategoryName)
                                                .append("'")
                                                .toString();
                                        break;
                                    case 2:
                                        messageToUser = new StringBuilder()
                                                .append(getResources().getString(R.string.fcm_messageToUser_Category_string) + " '")
                                                .append(newCategoryName)
                                                .append("' " + getResources().getString(R.string.fcm_messageToUser_already_created_string))
                                                .toString();
                                        break;
                                }
                                Snackbar renameResultSnackbar = Snackbar
                                        .make(recyclerView, messageToUser, Snackbar.LENGTH_LONG);
                                renameResultSnackbar.show();

                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                dialog.cancel();
                            }
                        });

                        cancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                dialog.cancel();
                            }
                        });

                        dialog.show();
                    }
                    break;
            }
        }
    }





    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        if (deleteCategorySnackbar != null)
            deleteCategorySnackbar.dismiss();
    }
}
