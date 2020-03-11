package com.costs.newcosts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add a class header comment
 */

public class DB_SmsNotes extends SQLiteOpenHelper {

    private static final String tag = "DB_Tag";
    private static DB_SmsNotes dbInstance;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "smsnotes.db";

    // ***************** TABLE_COST_NOTES ***********************
    private static final String TABLE_NOTES = "notes";
    private static final String NOTES_ID = "id_notes";
    private static final String EXPENSES_ID = "id_expenses";
    private static final String NOTE_TEXT = "note_text";
    private static final String SMS_MILLIS = "sms_millis";
    private static final String VALUE = "value";
    // **********************************************************



    private DB_SmsNotes(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public static DB_SmsNotes getInstance(Context context) {
        if (dbInstance != null)
            return dbInstance;
        else {
            dbInstance = new DB_SmsNotes(context.getApplicationContext(), null, null, 1);
            return dbInstance;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableNotes = "CREATE TABLE " + TABLE_NOTES + " (" +
                NOTES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EXPENSES_ID + " INTEGER, " +
                NOTE_TEXT + " TEXT, " +
                SMS_MILLIS + " INTEGER, " +
                VALUE + " REAL)";

        db.execSQL(createTableNotes);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }


    // Добавляем новую запись
    public void addNote(int id_n, String note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues(2);
        values.put(EXPENSES_ID, id_n);
        values.put(NOTE_TEXT, note);
        db.insert(TABLE_NOTES, null, values);
    }


    public List<Integer> getTest(String string) {
        String query = "SELECT DISTINCT " + EXPENSES_ID + ", " + "LOWER(" + NOTE_TEXT + ") AS TEXT " +
                " FROM " + TABLE_NOTES +
                " WHERE " + NOTE_TEXT + " LIKE '%" + string + "%'";

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        List<Integer> idList = new ArrayList<>();

        try {
            c = db.rawQuery(query, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                idList.add(c.getInt(c.getColumnIndexOrThrow(EXPENSES_ID)));
                c.moveToNext();
            }
        } catch (SQLiteException e) {
            System.out.println("EXCEPTION IN getTest()");
            e.printStackTrace();
        } finally {
            if (db != null)
                db.close();
            if (c != null)
                c.close();
        }

        return idList;
    }

    public int getExpectedID(String expenseNoteString) {
        return 14;
    }
}
