package com.costs.newcosts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class DB_Costs extends SQLiteOpenHelper {

    /*  В базе данных месяца начинаются с 0  */
    private static final String tag = "CostsDbTag";

    private static DB_Costs dbInstance;


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "costs.db";

    // ***************** TABLE_COST_NAMES ***********************
    private static final String TABLE_COST_NAMES = "costnames";
    private static final String ID_N = "id_n";
    private static final String COST_NAME = "costname";
    private static final String IS_ACTIVE = "isactive";
    // **********************************************************

    // ***************** TABLE_COST_VALUES **********************
    private static final String TABLE_COST_VALUES = "costvalues";
    private static final String ID_C = "id_c";
    private static final String ID_N_FK = "id_nfk";
    private static final String DAY = "day";
    private static final String MONTH = "month";
    private static final String YEAR = "year";
    private static final String DATE_IN_MILLISECONDS = "dateinmilliseconds";
    private static final String COST_VALUE = "costvalue";
    private static final String IMAGE = "image";
    private static final String TEXT = "text";
    // **********************************************************


    private DB_Costs(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public static synchronized DB_Costs getInstance(Context context) {
        if (dbInstance != null)
            return dbInstance;
        else {
            dbInstance = new DB_Costs(context.getApplicationContext(), null, null, 1);
            return dbInstance;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Таблица, хранящая названия статей расходов
        String createTableCostsNames = "CREATE TABLE " + TABLE_COST_NAMES + " (" +
                ID_N + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COST_NAME + " TEXT, " +
                IS_ACTIVE + " INTEGER)";

        // Таблица, хранящая записи о расходах по соответствующей статье расходов
        String createTableCostsValues = "CREATE TABLE " + TABLE_COST_VALUES + " (" +
                ID_C + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ID_N_FK + " INTEGER, " +
                DAY + " INTEGER, " +
                MONTH + " INTEGER, " +
                YEAR + " INTEGER, " +
                DATE_IN_MILLISECONDS + " INTEGER, " +
                COST_VALUE + " REAL, " +
                IMAGE + " BLOB, " +
                TEXT + " TEXT)";

        db.execSQL(createTableCostsNames);
        db.execSQL(createTableCostsValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COST_NAMES);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COST_VALUES);
//        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean COSTS_DB_IS_EMPTY() {
        String query = "SELECT * FROM " + TABLE_COST_NAMES;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        boolean costsDbIsEmpty = true;

        try {
            c = db.rawQuery(query, null);
            if (c.moveToFirst())
                costsDbIsEmpty = false;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return costsDbIsEmpty;
    }

    // Возвращает всё содержимое TABLE_COST_NAMES
    public List<DataUnitTableCostNames> getAllTableCostNames() {
        String query = "SELECT * FROM " + TABLE_COST_NAMES;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        List<DataUnitTableCostNames> tableCostNamesDataList = new ArrayList<>();

        try {
            c = db.rawQuery(query, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                DataUnitTableCostNames costNamesDataUnit = new DataUnitTableCostNames();

                costNamesDataUnit.set_ID_N(c.getInt(c.getColumnIndex(ID_N)));
                costNamesDataUnit.set_COST_NAME(c.getString(c.getColumnIndex(COST_NAME)));
                costNamesDataUnit.set_IS_ACTIVE(c.getInt(c.getColumnIndex(IS_ACTIVE)));

                tableCostNamesDataList.add(costNamesDataUnit);
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return tableCostNamesDataList;
    }

    // Возвращает всё содержимое TABLE_COST_VALUES
    public List<DataUnitTableCostValues> getAllTableCostValues() {
        String query = "SELECT * FROM " + TABLE_COST_VALUES;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        List<DataUnitTableCostValues> tableCostValuesDataList = new ArrayList<>();

        try {
            c = db.rawQuery(query, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                DataUnitTableCostValues costValuesDataUnit = new DataUnitTableCostValues();

                costValuesDataUnit.set_ID_C(c.getInt(c.getColumnIndexOrThrow(ID_C)));
                costValuesDataUnit.set_ID_N_FK(c.getInt(c.getColumnIndexOrThrow(ID_N_FK)));
                costValuesDataUnit.set_DAY(c.getInt(c.getColumnIndexOrThrow(DAY)));
                costValuesDataUnit.set_MONTH(c.getInt(c.getColumnIndexOrThrow(MONTH)));
                costValuesDataUnit.set_YEAR(c.getInt(c.getColumnIndexOrThrow(YEAR)));
                costValuesDataUnit.set_DATE_IN_MILLISECONDS(c.getLong(c.getColumnIndexOrThrow(DATE_IN_MILLISECONDS)));
                costValuesDataUnit.set_COST_VALUE(c.getDouble(c.getColumnIndexOrThrow(COST_VALUE)));
                costValuesDataUnit.set_TEXT(c.getString(c.getColumnIndexOrThrow(TEXT)));

                tableCostValuesDataList.add(costValuesDataUnit);
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return tableCostValuesDataList;
    }

    // Удаляет все данные из TABLE_COST_NAMES
    public void deleteTableCostNames() {
        String query = "DELETE FROM " + TABLE_COST_NAMES;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
//        db.execSQL("vacuum");

        if (db != null)
            db.close();
    }

    // Удаляет все данные из TABLE_COST_VALUES
    public void deleteTableCostValues() {
        String query = "DELETE FROM " + TABLE_COST_VALUES;
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
//        db.execSQL("vacuum");

        if (db != null)
            db.close();
    }

    // Заменяет все данные TABLE_COST_NAMES данными из списка restoringDataList
    public void restoreTableCostNames(List<DataUnitTableCostNames> restoringDataList) {
        deleteTableCostNames();

        SQLiteDatabase db = getWritableDatabase();

        for (DataUnitTableCostNames costNamesDataUnit : restoringDataList) {
            ContentValues values = new ContentValues(3);
            values.put(ID_N, costNamesDataUnit.get_ID_N());
            values.put(COST_NAME, costNamesDataUnit.get_COST_NAME());
            values.put(IS_ACTIVE, costNamesDataUnit.get_IS_ACTIVE());

            db.insert(TABLE_COST_NAMES, null, values);
        }

        if (db != null)
            db.close();
    }

    public void restoreTableCostValues(List<DataUnitTableCostValues> restoringDataList) {
        deleteTableCostValues();

        SQLiteDatabase db = getWritableDatabase();

        for (DataUnitTableCostValues costValuesDataUnit : restoringDataList) {
            ContentValues values = new ContentValues(8);
            values.put(ID_C, costValuesDataUnit.get_ID_C());
            values.put(ID_N_FK, costValuesDataUnit.get_ID_N_FK());
            values.put(DAY, costValuesDataUnit.get_DAY());
            values.put(MONTH, costValuesDataUnit.get_MONTH());
            values.put(YEAR, costValuesDataUnit.get_YEAR());
            values.put(DATE_IN_MILLISECONDS, costValuesDataUnit.get_DATE_IN_MILLISECONDS());
            values.put(COST_VALUE, costValuesDataUnit.get_COST_VALUE());
            values.put(TEXT, costValuesDataUnit.get_TEXT());

            db.insert(TABLE_COST_VALUES, null, values);
        }

        if (db != null)
            db.close();
    }
    public void restoreTableCostValues(DataUnitTableCostValues restoringData) {
//        deleteTableCostValues();

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues(8);
        values.put(ID_C, restoringData.get_ID_C());
        values.put(ID_N_FK, restoringData.get_ID_N_FK());
        values.put(DAY, restoringData.get_DAY());
        values.put(MONTH, restoringData.get_MONTH());
        values.put(YEAR, restoringData.get_YEAR());
        values.put(DATE_IN_MILLISECONDS, restoringData.get_DATE_IN_MILLISECONDS());
        values.put(COST_VALUE, restoringData.get_COST_VALUE());
        values.put(TEXT, restoringData.get_TEXT());

        db.insert(TABLE_COST_VALUES, null, values);

        if (db != null)
            db.close();
    }









    // Возвращает список из ID и названий всех активных статей расходов
    public DataUnitExpenses[] getActiveCostNames_V3() {
        String query = "SELECT " +
                ID_N + ", " +
                COST_NAME + " " +
                "FROM " + TABLE_COST_NAMES +
                " WHERE " + IS_ACTIVE + " = " + 1;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        List<DataUnitExpenses> dataUnitsList = new ArrayList<>();

        try {
            c = db.rawQuery(query, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                DataUnitExpenses dataUnit = new DataUnitExpenses();

                dataUnit.setExpenseId_N(c.getInt(c.getColumnIndex(ID_N)));
                dataUnit.setExpenseName(c.getString(c.getColumnIndex(COST_NAME)));

                dataUnitsList.add(dataUnit);
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        DataUnitExpenses[] dataUnitsArray = new DataUnitExpenses[dataUnitsList.size()];
        dataUnitsList.toArray(dataUnitsArray);

        return dataUnitsArray;
    }

    // Возвращает ID соответсвующей статьи расходов
    public int getExpenseIdByName(String expenseName) {
        String query = "SELECT " + ID_N +
                " FROM " + TABLE_COST_NAMES +
                " WHERE " + COST_NAME + " LIKE '" + expenseName + "'";

        SQLiteDatabase db = getWritableDatabase();
        int id_n = -1;
        Cursor c = null;

        try {
            c = db.rawQuery(query, null);
            c.moveToFirst();

            id_n = c.getInt(c.getColumnIndex(ID_N));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return id_n;
    }

    // Добавляет новую статью расходов. В случае успеха возвращает "1".
    // Если запись присутствует в базе - возвращает "0".
    public long addCostName(String costName) {
        String checkCostNameQuery = "SELECT " + COST_NAME + ", " + IS_ACTIVE + ", " + ID_N +
                " FROM " + TABLE_COST_NAMES +
                " WHERE " + COST_NAME +
                " LIKE '" + costName + "'";

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        long result = 1;

        try {
            c = db.rawQuery(checkCostNameQuery, null);

            // Если запись присутствует в базе, но не активна - делаем
            // её активной. Иначе добавляем новую запись в базу
            if (c.moveToFirst()) {
                int is_active = c.getInt(c.getColumnIndex(IS_ACTIVE));
                int id_n = c.getInt(c.getColumnIndex(ID_N));

                if (is_active == 1)
                    result = 0;
                else {
                    String makeCostNameActiveQuery = "UPDATE " + TABLE_COST_NAMES +
                            " SET " + IS_ACTIVE + " = " + 1 +
                            " WHERE " + ID_N + " = " + id_n;

                    db.execSQL(makeCostNameActiveQuery);
                }
            }
            else {
                ContentValues values = new ContentValues();
                values.put(COST_NAME, costName);
                values.put(IS_ACTIVE, 1);

                db.insert(TABLE_COST_NAMES, null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return result;
    }

    // Возвращает суммарное значение расходов для данного "id_n" за
    // заданный период. Если поле 'day' отрицательно - при выборке
    // значений учитывается только указанные месяц, год и тип расходов.
    public double getCostValue(int day, int month, int year, int id_n) {
        String getCostValueQuery = null;

        if (day >= 0) {
            getCostValueQuery = "SELECT " + COST_VALUE +
                    " FROM " + TABLE_COST_VALUES +
                    " WHERE " + DAY + " = " + day +
                    " AND " + MONTH + " = " + month +
                    " AND " + YEAR + " = " + year +
                    " AND " + ID_N_FK + " = " + id_n;
        } else {
            getCostValueQuery = "SELECT " + COST_VALUE +
                    " FROM " + TABLE_COST_VALUES +
                    " WHERE " + MONTH + " = " + month +
                    " AND " + YEAR + " = " + year +
                    " AND " + ID_N_FK + " = " + id_n;
        }

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        double sum = 0.0;

        try {
            c = db.rawQuery(getCostValueQuery, null);

            c.moveToFirst();
            while (!c.isAfterLast()) {
                sum = sum + c.getDouble(c.getColumnIndex(COST_VALUE));
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return sum;
    }

    // Добавляет новую запись по статье расходов с соответсвующим id_n в базу
    public void addCosts(double costValue, int id_n, String note) {
        Calendar calendar = Calendar.getInstance();

        int day =  calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        long currentDateInMilliseconds = calendar.getTimeInMillis();

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DAY, day);
        values.put(MONTH, month);
        values.put(YEAR, year);
        values.put(DATE_IN_MILLISECONDS, currentDateInMilliseconds);
        values.put(ID_N_FK, id_n);
        values.put(COST_VALUE, costValue);
        if (note != null && !"".equals(note))
            values.put(TEXT, note);
        db.insert(TABLE_COST_VALUES, null, values);

        db.close();
    }

    // Удаляет (делает не активной) запись в таблице TABLE_COST_NAMES
    // с соответствующим ID
    public boolean deleteCostName(int id_n) {
        String deleteCostNameQuery = "UPDATE " + TABLE_COST_NAMES +
                " SET " + IS_ACTIVE + " = " + 0 +
                " WHERE " + ID_N + " = " + id_n;

        SQLiteDatabase db = getWritableDatabase();
        boolean result = true;

        try {
            db.execSQL(deleteCostNameQuery);
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            if (db != null)
                db.close();
        }

        return result;
    }

    // Возвращает массив не активных записей в таблице TABLE_COST_NAMES
    public String[] getNonActiveCostNames() {
        String getNonActiveCostNamesQuery = "SELECT " + COST_NAME +
                " FROM " + TABLE_COST_NAMES +
                " WHERE " + IS_ACTIVE + " = " + 0;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        List<String> listOfNonActiveCostNames = new ArrayList<>();

        try {
            c = db.rawQuery(getNonActiveCostNamesQuery, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                listOfNonActiveCostNames.add(c.getString(c.getColumnIndex(COST_NAME)));

                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        String[] arrayOfNonActiveCostNames = new String[listOfNonActiveCostNames.size()];
        listOfNonActiveCostNames.toArray(arrayOfNonActiveCostNames);

        return arrayOfNonActiveCostNames;
    }

    // Переименовывает статью расходов с данным id_n на newCostName. В случае
    // успеха возвращает 0. Если запись с таким названием есть в базе, но
    // неактивна - возвращает 1. Если запись есть в базе и активна - возвращает 2
    public int renameCostName(int id_n, String newCostName) {
        String checkNewCostNameQuery = "SELECT " + COST_NAME + ", " + IS_ACTIVE +
                " FROM " + TABLE_COST_NAMES +
                " WHERE " + COST_NAME + " LIKE '" + newCostName + "'";

        String renameQuery = "UPDATE " + TABLE_COST_NAMES +
                " SET " + COST_NAME + " = '" + newCostName +
                "' WHERE " + ID_N + " = " + id_n;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        int result = 0;

        try {
            c = db.rawQuery(checkNewCostNameQuery, null);

            if (c.moveToFirst()) {
                int is_active = c.getInt(c.getColumnIndex(IS_ACTIVE));
                if (is_active == 0)
                    result = 1;
                else if (is_active == 1)
                    result = 2;
            } else
                db.execSQL(renameQuery);
        } catch (Exception e){
            e.printStackTrace();
            result = 2;
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return result;
    }

    // Возвращает заданное число последних введённых сумм расходов
    public List<DataUnitExpenses> getLastEntries_V3(int numberOfEntries) {
        String getLastEntriesQuery = "SELECT " +
                ID_N + ", " +
                DAY + ", " +
                MONTH + ", " +
                YEAR + ", " +
                COST_NAME + ", " +
                COST_VALUE + ", " +
                DATE_IN_MILLISECONDS + ", " +
                TEXT +
                " FROM " + TABLE_COST_VALUES +
                " INNER JOIN " + TABLE_COST_NAMES +
                " ON " + TABLE_COST_VALUES + "." + ID_N_FK + " = " + TABLE_COST_NAMES + "." + ID_N +
                " ORDER BY " + DATE_IN_MILLISECONDS + " DESC " +
                " LIMIT " + numberOfEntries;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        List<DataUnitExpenses> listOfEntries = new ArrayList<>();

        try {
            c = db.rawQuery(getLastEntriesQuery, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                DataUnitExpenses singleUnit = new DataUnitExpenses();

                singleUnit.setExpenseId_N(c.getInt(c.getColumnIndex(ID_N)));
                singleUnit.setDay(c.getInt(c.getColumnIndex(DAY)));
                singleUnit.setMonth(c.getInt(c.getColumnIndex(MONTH)) + 1);
                singleUnit.setYear(c.getInt(c.getColumnIndex(YEAR)));
                singleUnit.setExpenseName(c.getString(c.getColumnIndex(COST_NAME)));
                double costValueDouble = c.getDouble(c.getColumnIndex(COST_VALUE));
                singleUnit.setExpenseValueDouble(costValueDouble);
                singleUnit.setExpenseValueString(Constants.formatDigit(costValueDouble));
                singleUnit.setMilliseconds(c.getLong(c.getColumnIndex(DATE_IN_MILLISECONDS)));
                singleUnit.setExpenseNoteString(c.getString(c.getColumnIndex(TEXT)));

                listOfEntries.add(singleUnit);
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return listOfEntries;
    }

    // Возвращает заданное число ('numberOfEntries') записей (сумм расходов), занесённых до 'milliseconds'
    public List<DataUnitExpenses> getEntriesBeforeDateInMilliseconds(long milliseconds, int numberOfEntries) {
        String getLastEntriesQuery = "SELECT " +
                ID_N + ", " +
                DAY + ", " +
                MONTH + ", " +
                YEAR + ", " +
                COST_NAME + ", " +
                COST_VALUE + ", " +
                DATE_IN_MILLISECONDS + ", " +
                TEXT +
                " FROM " + TABLE_COST_VALUES +
                " INNER JOIN " + TABLE_COST_NAMES +
                " ON " + TABLE_COST_VALUES + "." + ID_N_FK + " = " + TABLE_COST_NAMES + "." + ID_N +
                " WHERE " + DATE_IN_MILLISECONDS + " < " + milliseconds +
                " ORDER BY " + DATE_IN_MILLISECONDS + " DESC " +
                " LIMIT " + numberOfEntries;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        List<DataUnitExpenses> listOfEntries = new ArrayList<>();

        try {
            c = db.rawQuery(getLastEntriesQuery, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                DataUnitExpenses singleUnit = new DataUnitExpenses();

                singleUnit.setExpenseId_N(c.getInt(c.getColumnIndex(ID_N)));
                singleUnit.setDay(c.getInt(c.getColumnIndex(DAY)));
                singleUnit.setMonth(c.getInt(c.getColumnIndex(MONTH)) + 1);
                singleUnit.setYear(c.getInt(c.getColumnIndex(YEAR)));
                singleUnit.setExpenseName(c.getString(c.getColumnIndex(COST_NAME)));
                double costValueDouble = c.getDouble(c.getColumnIndex(COST_VALUE));
                singleUnit.setExpenseValueDouble(costValueDouble);
                singleUnit.setExpenseValueString(Constants.formatDigit(costValueDouble));
                singleUnit.setMilliseconds(c.getLong(c.getColumnIndex(DATE_IN_MILLISECONDS)));
                singleUnit.setExpenseNoteString(c.getString(c.getColumnIndex(TEXT)));

                listOfEntries.add(singleUnit);
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return listOfEntries;
    }

    // Загружает все записи, внесённые после даты в миллисекундах 'milliseconds',
    // плюс 20 дополнительных записей, внесённых до 'milliseconds' (число 20 взято просто так).
    // Запись, соответствующая 'milliseconds', также загружается
    public List<DataUnitExpenses> getEntriesAfterDateInMilliseconds(long milliseconds) {
        String getLastEntriesQuery = "SELECT " +
                ID_N + ", " +
                DAY + ", " +
                MONTH + ", " +
                YEAR + ", " +
                COST_NAME + ", " +
                COST_VALUE + ", " +
                DATE_IN_MILLISECONDS + ", " +
                TEXT +
                " FROM " + TABLE_COST_VALUES +
                " INNER JOIN " + TABLE_COST_NAMES +
                " ON " + TABLE_COST_VALUES + "." + ID_N_FK + " = " + TABLE_COST_NAMES + "." + ID_N +
                " WHERE " + DATE_IN_MILLISECONDS + " >= " + milliseconds +
                " ORDER BY " + DATE_IN_MILLISECONDS + " DESC ";
//                " UNION ALL " +
//                " SELECT " +
//                ID_N + ", " +
//                DAY + ", " +
//                MONTH + ", " +
//                YEAR + ", " +
//                COST_NAME + ", " +
//                COST_VALUE + ", " +
//                DATE_IN_MILLISECONDS + ", " +
//                TEXT +
//                " FROM " + TABLE_COST_VALUES +
//                " INNER JOIN " + TABLE_COST_NAMES +
//                " ON " + TABLE_COST_VALUES + "." + ID_N_FK + " = " + TABLE_COST_NAMES + "." + ID_N +
//                " WHERE " + DATE_IN_MILLISECONDS + " < " + milliseconds +
//                " ORDER BY " + DATE_IN_MILLISECONDS + " DESC " +
//                " LIMIT " + 20;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        List<DataUnitExpenses> listOfEntries = new ArrayList<>();

        try {
            c = db.rawQuery(getLastEntriesQuery, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                DataUnitExpenses singleUnit = new DataUnitExpenses();

                singleUnit.setExpenseId_N(c.getInt(c.getColumnIndex(ID_N)));
                singleUnit.setDay(c.getInt(c.getColumnIndex(DAY)));
                singleUnit.setMonth(c.getInt(c.getColumnIndex(MONTH)) + 1);
                singleUnit.setYear(c.getInt(c.getColumnIndex(YEAR)));
                singleUnit.setExpenseName(c.getString(c.getColumnIndex(COST_NAME)));
                double costValueDouble = c.getDouble(c.getColumnIndex(COST_VALUE));
                singleUnit.setExpenseValueDouble(costValueDouble);
                singleUnit.setExpenseValueString(Constants.formatDigit(costValueDouble));
                singleUnit.setMilliseconds(c.getLong(c.getColumnIndex(DATE_IN_MILLISECONDS)));
                singleUnit.setExpenseNoteString(c.getString(c.getColumnIndex(TEXT)));

                listOfEntries.add(singleUnit);
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        getLastEntriesQuery = "SELECT " +
                ID_N + ", " +
                DAY + ", " +
                MONTH + ", " +
                YEAR + ", " +
                COST_NAME + ", " +
                COST_VALUE + ", " +
                DATE_IN_MILLISECONDS + ", " +
                TEXT +
                " FROM " + TABLE_COST_VALUES +
                " INNER JOIN " + TABLE_COST_NAMES +
                " ON " + TABLE_COST_VALUES + "." + ID_N_FK + " = " + TABLE_COST_NAMES + "." + ID_N +
                " WHERE " + DATE_IN_MILLISECONDS + " < " + milliseconds +
                " ORDER BY " + DATE_IN_MILLISECONDS + " DESC " +
                " LIMIT " + 20;

        db = getWritableDatabase();
        c = null;

        try {
            c = db.rawQuery(getLastEntriesQuery, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                DataUnitExpenses singleUnit = new DataUnitExpenses();

                singleUnit.setExpenseId_N(c.getInt(c.getColumnIndex(ID_N)));
                singleUnit.setDay(c.getInt(c.getColumnIndex(DAY)));
                singleUnit.setMonth(c.getInt(c.getColumnIndex(MONTH)) + 1);
                singleUnit.setYear(c.getInt(c.getColumnIndex(YEAR)));
                singleUnit.setExpenseName(c.getString(c.getColumnIndex(COST_NAME)));
                double costValueDouble = c.getDouble(c.getColumnIndex(COST_VALUE));
                singleUnit.setExpenseValueDouble(costValueDouble);
                singleUnit.setExpenseValueString(Constants.formatDigit(costValueDouble));
                singleUnit.setMilliseconds(c.getLong(c.getColumnIndex(DATE_IN_MILLISECONDS)));
                singleUnit.setExpenseNoteString(c.getString(c.getColumnIndex(TEXT)));

                listOfEntries.add(singleUnit);
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return listOfEntries;
    }

    // Удаляет запись из таблицы TABLE_COST_VALUES, дата в миллисекундах
    // для которой равна "dateInMilliseconds"
    public boolean removeCostValue(long dateInMillisecond) {

        String deleteQuery = "DELETE FROM " + TABLE_COST_VALUES +
                " WHERE " + DATE_IN_MILLISECONDS + " = " + dateInMillisecond;

        SQLiteDatabase db = getWritableDatabase();
        boolean result = true;

        try {
            db.execSQL(deleteQuery);
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            if (db != null)
                db.close();
        }

        return result;
    }

    // Возвращает массив строк, состоящих из месяца, года и суммарного значения расходов
    // за эти месяц и год, сгруппированные по месяцу и году в формате: 5$1989$575
    public List<DataUnitExpenses> getSumByMonthsList() {
        String query = "SELECT " +
                MONTH + ", " +
                YEAR +
                ", SUM(" + COST_VALUE + ") AS SUM " +
                " FROM " + TABLE_COST_VALUES +
                " GROUP BY " + MONTH + ", " + YEAR +
                " ORDER BY " + DATE_IN_MILLISECONDS + " DESC";

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        List<DataUnitExpenses> listOfEnteries = new ArrayList<>();

        try {
            c = db.rawQuery(query, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                DataUnitExpenses dataUnit = new DataUnitExpenses();
                dataUnit.setMonth(c.getInt(c.getColumnIndex(MONTH)));
                dataUnit.setYear(c.getInt(c.getColumnIndex(YEAR)));
                dataUnit.setExpenseValueDouble(c.getDouble(c.getColumnIndex("SUM")));
                dataUnit.setExpenseValueString(Constants.formatDigit(c.getDouble(c.getColumnIndex("SUM"))));

                listOfEnteries.add(dataUnit);
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return listOfEnteries;
    }

    // Возвращает массив строк, состоящих из названия статьи расходов и суммарного
    // значения расходов по этой статье за выбранный месяц в формате: Продукты$102520
    public List<DataUnitExpenses> getCostValuesArrayOnDate_V3(int month, int year) {
        String query = "SELECT " +
                TABLE_COST_NAMES + "." + COST_NAME +
                ", SUM(" + TABLE_COST_VALUES + "." + COST_VALUE + ") AS SUM, " +
                TABLE_COST_VALUES + "." + MONTH + ", " +
                TABLE_COST_VALUES + "." + YEAR + ", " +
                TABLE_COST_VALUES + "." + DATE_IN_MILLISECONDS + ", " +
                TABLE_COST_VALUES + "." + ID_N_FK + ", " +
                TABLE_COST_NAMES + "." + ID_N +
                " FROM " + TABLE_COST_VALUES +
                " INNER JOIN " + TABLE_COST_NAMES +
                " ON " + TABLE_COST_VALUES + "." + ID_N_FK + " = " + TABLE_COST_NAMES + "." + ID_N +
                " WHERE " + MONTH + " = " + month +
                " AND " + YEAR + " = " + year +
                " GROUP BY " + ID_N_FK +
                " ORDER BY " + "SUM" + " DESC";

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        List<DataUnitExpenses> dataUnitList = new ArrayList<>();

        try {
            c = db.rawQuery(query, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                DataUnitExpenses dataUnit = new DataUnitExpenses();
                dataUnit.setExpenseName(c.getString(c.getColumnIndex(COST_NAME)));
                dataUnit.setExpenseValueDouble(c.getDouble(c.getColumnIndex("SUM")));
                dataUnit.setExpenseValueString(Constants.formatDigit(c.getDouble(c.getColumnIndex("SUM"))));
                dataUnit.setMonth(c.getInt(c.getColumnIndex(MONTH)));
                dataUnit.setYear(c.getInt(c.getColumnIndex(YEAR)));
                dataUnit.setExpenseId_N(c.getInt(c.getColumnIndex(ID_N_FK)));

                dataUnitList.add(dataUnit);
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return dataUnitList;
    }


    public List<DataUnitExpenses> getCostValuesArrayOnDateAndCostName_V2(int month, int year, int id_n, String expenseName) {
        String query = "SELECT " +
                COST_VALUE + ", " +
                DAY + ", " +
                DATE_IN_MILLISECONDS + ", " +
                TEXT +
                " FROM " + TABLE_COST_VALUES +
                " WHERE " + MONTH + " = " + month +
                " AND " + YEAR + " = " + year +
                " AND " + ID_N_FK + " = " + id_n +
                " ORDER BY " + DATE_IN_MILLISECONDS + " ASC";

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        List<DataUnitExpenses> dataUnitList = new ArrayList<>();

        try {
            c = db.rawQuery(query, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                DataUnitExpenses dataUnit = new DataUnitExpenses();
                dataUnit.setExpenseId_N(id_n);
                dataUnit.setExpenseName(expenseName);
                dataUnit.setMonth(month);
                dataUnit.setYear(year);
                dataUnit.setDay(c.getInt(c.getColumnIndex(DAY)));
                dataUnit.setExpenseNoteString(c.getString(c.getColumnIndex(TEXT)));
                dataUnit.setExpenseValueDouble(c.getDouble(c.getColumnIndex(COST_VALUE)));
                dataUnit.setExpenseValueString(Constants.formatDigit(c.getDouble(c.getColumnIndex(COST_VALUE))));
                dataUnit.setMilliseconds(c.getLong(c.getColumnIndex(DATE_IN_MILLISECONDS)));

                dataUnitList.add(dataUnit);
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return dataUnitList;
    }

    public List<DataUnitExpenses> getCostsBetweenDatesByName_V2(long initialDateInMilliseconds, long endingDateInMilliseconds, String expenseName, int id_n) {
        String query = "SELECT " +
                COST_VALUE + ", " +
                DAY + ", " +
                MONTH + ", " +
                YEAR + ", " +
                DATE_IN_MILLISECONDS + ", " +
                TEXT +
                " FROM " + TABLE_COST_VALUES +
                " WHERE " + DATE_IN_MILLISECONDS + " BETWEEN " + initialDateInMilliseconds + " AND " + endingDateInMilliseconds +
                " AND " + ID_N_FK + " = " + id_n +
                " ORDER BY " + DATE_IN_MILLISECONDS + " ASC";

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        List<DataUnitExpenses> expensesDataUnitList = new ArrayList<>();

        try {
            c = db.rawQuery(query, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                DataUnitExpenses dataUnit = new DataUnitExpenses();
                dataUnit.setExpenseId_N(id_n);
                dataUnit.setExpenseName(expenseName);
                dataUnit.setMonth(c.getInt(c.getColumnIndex(MONTH)));
                dataUnit.setYear(c.getInt(c.getColumnIndex(YEAR)));
                dataUnit.setDay(c.getInt(c.getColumnIndex(DAY)));
                dataUnit.setExpenseNoteString(c.getString(c.getColumnIndex(TEXT)));
                dataUnit.setExpenseValueDouble(c.getDouble(c.getColumnIndex(COST_VALUE)));
                dataUnit.setExpenseValueString(Constants.formatDigit(c.getDouble(c.getColumnIndex(COST_VALUE))));
                dataUnit.setMilliseconds(c.getLong(c.getColumnIndex(DATE_IN_MILLISECONDS)));

                expensesDataUnitList.add(dataUnit);
                c.moveToNext();;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return expensesDataUnitList;
    }

    public List<DataUnitExpenses> getCostsBetweenDates_V3(long initialDateInMilliseconds, long endingDateInMilliseconds) {
        String query = "SELECT " +
                TABLE_COST_NAMES + "." + COST_NAME +
                ", SUM(" + TABLE_COST_VALUES + "." + COST_VALUE + ") AS SUM, " +
                TABLE_COST_VALUES + "." + MONTH + ", " +
                TABLE_COST_VALUES + "." + YEAR + ", " +
                TABLE_COST_VALUES + "." + DATE_IN_MILLISECONDS + ", " +
                TABLE_COST_VALUES + "." + ID_N_FK + ", " +
                TABLE_COST_NAMES + "." + ID_N +
                " FROM " + TABLE_COST_VALUES +
                " INNER JOIN " + TABLE_COST_NAMES +
                " ON " + TABLE_COST_VALUES + "." + ID_N_FK + " = " + TABLE_COST_NAMES + "." + ID_N +
                " WHERE " + DATE_IN_MILLISECONDS + " BETWEEN " + initialDateInMilliseconds + " AND " + endingDateInMilliseconds +
                " GROUP BY " + ID_N_FK +
                " ORDER BY " + "SUM" + " DESC";

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        List<DataUnitExpenses> expensesDataUnitList = new ArrayList<>();

        try {
            c = db.rawQuery(query, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                DataUnitExpenses dataUnit = new DataUnitExpenses();
                dataUnit.setExpenseName(c.getString(c.getColumnIndex(COST_NAME)));
                dataUnit.setExpenseValueDouble(c.getDouble(c.getColumnIndex("SUM")));
                dataUnit.setExpenseValueString(Constants.formatDigit(c.getDouble(c.getColumnIndex("SUM"))));
                dataUnit.setMonth(c.getInt(c.getColumnIndex(MONTH)));
                dataUnit.setYear(c.getInt(c.getColumnIndex(YEAR)));
                dataUnit.setExpenseId_N(c.getInt(c.getColumnIndex(ID_N_FK)));

                expensesDataUnitList.add(dataUnit);
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return expensesDataUnitList;
    }


    public long addCostInMilliseconds(int id_n, String costValue, long milliseconds, String note) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        SQLiteDatabase db = getWritableDatabase();
        while (true) {
            String query = "SELECT " + DATE_IN_MILLISECONDS +
                    " FROM " + TABLE_COST_VALUES +
                    " WHERE " + DATE_IN_MILLISECONDS + " = " + milliseconds;

            try {
                if (db.rawQuery(query, null).moveToFirst())
                    ++milliseconds;
                else break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ContentValues values = new ContentValues();
        values.put(DAY, day);
        values.put(MONTH, month);
        values.put(YEAR, year);
        values.put(DATE_IN_MILLISECONDS, milliseconds);
        values.put(ID_N_FK, id_n);
        values.put(COST_VALUE, costValue);
        values.put(TEXT, note);
        db.insert(TABLE_COST_VALUES, null, values);

        db.close();

        return milliseconds;
    }

    public List<Long> getLastEnteredValuesByMilliseconds(long fromMilliseconds) {
        String getLastEntriesQuery = "SELECT " +
                DATE_IN_MILLISECONDS +
                " FROM " + TABLE_COST_VALUES +
                " WHERE " + DATE_IN_MILLISECONDS + " > " + fromMilliseconds;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        List<Long> millisecondsList = new ArrayList<>();

        try {
            c = db.rawQuery(getLastEntriesQuery, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                millisecondsList.add(c.getLong(c.getColumnIndex(DATE_IN_MILLISECONDS)));
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                db.close();
        }

        return millisecondsList;
    }
}
