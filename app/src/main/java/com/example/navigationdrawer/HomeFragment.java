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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private List<String> questionList;
    private List<String> quizQuestions;
    private int currentQuestionIndex = 0;
    private int correctAnswerCount = 0;

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

        Button resultButton = view.findViewById(R.id.result_button);
        resultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResult();
            }
        });

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(getContext());

        // Retrieve the list of questions from the database
        questionList = getQuestionList();

        // Generate the quiz questions
        generateQuizQuestions();

        // Display the first question
        displayQuestion();

        return view;
    }

    private void checkAnswer(String selectedOption) {
        if (selectedOption.equals(answerString)) {
            answerTextView.setText("Awesome, your answer is right");
            correctAnswerCount++;
        } else {
            answerTextView.setText("Incorrect! The answer is " + answerString);
        }

        // Move to the next question
        currentQuestionIndex++;

        // Check if all questions have been answered
        if (currentQuestionIndex < quizQuestions.size()) {
            displayQuestion();
        } else {
            answerTextView.setText("Quiz completed. Click 'Result' .");
        }
    }

    private List<String> getQuestionList() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        List<String> questionList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String question = cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION));
                questionList.add(question);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return questionList;
    }

    private void generateQuizQuestions() {
        quizQuestions = new ArrayList<>();

        // Shuffle the question list
        Collections.shuffle(questionList);

        // Select the first 5 questions from the shuffled list
        int questionCount = Math.min(5, questionList.size());
        for (int i = 0; i < questionCount; i++) {
            quizQuestions.add(questionList.get(i));
        }
    }

    private void displayQuestion() {
        String question = quizQuestions.get(currentQuestionIndex);
        letterTextView.setText(question);

        // Reset the answer text view
        answerTextView.setText("");
        answerString = getAnswerStringFromQuestion(question);
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

    private void showResult() {
        double score = (double) correctAnswerCount / quizQuestions.size() * 100;
        String resultMessage = "Quiz Result:\n" +
                "Correct Answers: " + correctAnswerCount + "\n" +
                "Total Questions: " + quizQuestions.size() + "\n" +
                "Score: " + score + "%";

        answerTextView.setText(resultMessage);
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
