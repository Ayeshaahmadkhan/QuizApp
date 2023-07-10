package com.example.navigationdrawer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "quiz.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME_QUESTIONS = "questions";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_QUESTION = "question";

    private static final String TABLE_NAME_RESULTS = "results";
    private static final String COLUMN_RESULT_ID = "_id";
    private static final String COLUMN_RESULT = "result";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the questions table
        String createQuestionsTableQuery = "CREATE TABLE " + TABLE_NAME_QUESTIONS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_QUESTION + " TEXT)";
        db.execSQL(createQuestionsTableQuery);

        // Create the results table
        String createResultsTableQuery = "CREATE TABLE " + TABLE_NAME_RESULTS + " (" +
                COLUMN_RESULT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RESULT + " TEXT)";
        db.execSQL(createResultsTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the tables if they exist and create new ones
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_RESULTS);
        onCreate(db);
    }


    public static String getTableNameQuestions() {
        return TABLE_NAME_QUESTIONS;
    }

    public static String getColumnQuestion() {
        return COLUMN_QUESTION;
    }

    public static String getTableNameResults() {
        return TABLE_NAME_RESULTS;
    }

    public static String getColumnResult() {
        return COLUMN_RESULT;
    }
}
