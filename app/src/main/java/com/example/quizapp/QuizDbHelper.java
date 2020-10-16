package com.example.quizapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.quizapp.QuizContract.*;

import java.util.ArrayList;
import java.util.List;

public class QuizDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MyQuiz.db";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db;


    public QuizDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        final String CREATE_QUESTIONS_TABLE = "CREATE TABLE " +
                QuestionsTable.TABLE_NAME + " ( " +
                QuestionsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuestionsTable.COLUMN_QUESTION + (" TEXT, ") +
                QuestionsTable.COLUMN_OPTION1 + (" TEXT, ") +
                QuestionsTable.COLUMN_OPTION2 + (" TEXT, ") +
                QuestionsTable.COLUMN_OPTION3 + (" TEXT, ") +
                QuestionsTable.COLUMN_ANSWER_NR + (" INTEGER") +
                ")";

        db.execSQL(CREATE_QUESTIONS_TABLE);

        fillQuestionsTable();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + QuestionsTable.TABLE_NAME);
        onCreate(db);

    }

    private void fillQuestionsTable() {
        Question q1 = new Question("What is the capital of France?", "Paris", "Nice", "Bordeaux", 1);
        addQuestion(q1);
        Question q2 = new Question("Which is the world's largest lake?", "Lake Michigan", "Lake Superior", "Lake Victoria", 2);
        addQuestion(q2);
        Question q3 = new Question("What Is Europe's longest river?", "Danube", "Dnieper", "Volga", 3);
        addQuestion(q3);
        Question q4 = new Question("Which is the highest peak in Europe?", "Mount Elbrus", "Mont Blanc", "Monte Rosa", 1);
        addQuestion(q4);
        Question q5 = new Question("What is the capital of Australia?", "Melbourne", "Canberra", "Sydney", 2);
        addQuestion(q5);
        Question q6 = new Question("How many states are there in the United States?", "51", "49", "50", 3);
        addQuestion(q6);
        Question q7 = new Question("What is the world's largest desert?", "Gobi", "Sahara", "Kalahari", 2);
        addQuestion(q7);
        Question q8 = new Question("Which Ocean is the biggest by volume?", "Atlantic", "Indian", "Pacific", 3);
        addQuestion(q8);


    }

    private void addQuestion(Question question) {
        ContentValues cv = new ContentValues();
        cv.put(QuestionsTable.COLUMN_QUESTION, question.getQuestion());
        cv.put(QuestionsTable.COLUMN_OPTION1, question.getOption1());
        cv.put(QuestionsTable.COLUMN_OPTION2, question.getOption2());
        cv.put(QuestionsTable.COLUMN_OPTION3, question.getOption3());
        cv.put(QuestionsTable.COLUMN_ANSWER_NR, question.getAnswereNr());
        db.insert(QuestionsTable.TABLE_NAME, null, cv);
    }

    public ArrayList<Question> getAllQuestions(){
        ArrayList<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + QuestionsTable.TABLE_NAME, null);
        if (c.moveToFirst()){
            do{
                Question question = new Question();
                question.setQuestion(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_QUESTION)));
                question.setOption1(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION3)));
                question.setAnswereNr(c.getInt(c.getColumnIndex(QuestionsTable.COLUMN_ANSWER_NR)));
                questionList.add(question);
            } while (c.moveToNext());
        }
        c.close();
        return questionList;
    }
}
