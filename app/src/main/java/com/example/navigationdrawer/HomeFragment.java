package com.example.navigationdrawer;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.Random;

public class HomeFragment extends Fragment {

    private TextView letterTextView, answerTextView;
    private char[] skyLetters = {'b', 'd', 'f', 'h', 'k', 'l', 't'};
    private char[] grassLetters = {'g', 'j', 'p', 'q', 'y'};
    private char[] rootLetters = {'a', 'c', 'e', 'i', 'm', 'n', 'o', 'r', 's', 'u', 'v', 'w', 'x', 'z'};
    private String answerString = "";
    private SQLiteDatabase quizDatabase;
    private static final String DATABASE_NAME = "quiz.db";
    private static final String TABLE_NAME = "questions";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_QUESTION = "question";

    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        letterTextView = view.findViewById(R.id.letter_text_view);
        answerTextView = view.findViewById(R.id.answer_text_view);

        Button skyButton = view.findViewById(R.id.sky_button);
        skyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer("Sky Letter");
            }
        });

        Button grassButton = view.findViewById(R.id.grass_button);
        grassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer("Grass Letter");
            }
        });

        Button rootButton = view.findViewById(R.id.root_button);
        rootButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer("Root Letter");
            }
        });

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(getContext());

        // Generate random questions and insert them into the database if it's empty
        if (getQuestionCount() == 0) {
            insertQuestionsIntoDatabase();
        }

        // Get a random question from the database
        String randomQuestion = getRandomQuestionFromDatabase();
        letterTextView.setText(randomQuestion);

        return view;
    }

    private void checkAnswer(String selectedOption) {
        if (selectedOption.equals(answerString)) {
            answerTextView.setText("Awesome, your answer is right");
        } else {
            answerTextView.setText("Incorrect! The answer is " + answerString);
        }

        // Wait for 5 seconds and create a new question
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String randomQuestion = getRandomQuestionFromDatabase();
                letterTextView.setText(randomQuestion);
                answerTextView.setText("");
            }
        }, 5000); // 5000 milliseconds = 5 seconds
    }

    private int getQuestionCount() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return count;
    }

    private void insertQuestionsIntoDatabase() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        for (char letter : skyLetters) {
            values.put(COLUMN_QUESTION, "Select the sky letter: " + letter);
            db.insert(TABLE_NAME, null, values);
        }

        for (char letter : grassLetters) {
            values.put(COLUMN_QUESTION, "Select the grass letter: " + letter);
            db.insert(TABLE_NAME, null, values);
        }

        for (char letter : rootLetters) {
            values.put(COLUMN_QUESTION, "Select the root letter: " + letter);
            db.insert(TABLE_NAME, null, values);
        }

        db.close();
    }

    private String getRandomQuestionFromDatabase() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY RANDOM() LIMIT 1", null);
        String question = "";
        if (cursor != null && cursor.moveToFirst()) {
            question = cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION));
            answerString = getAnswerStringFromQuestion(question);
            cursor.close();
        }
        db.close();
        return question;
    }

    private String getAnswerStringFromQuestion(String question) {
        if (question.contains("sky")) {
            return "Sky Letter";
        } else if (question.contains("grass")) {
            return "Grass Letter";
        } else {
            return "Root Letter";
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Close the database when the fragment is destroyed
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
