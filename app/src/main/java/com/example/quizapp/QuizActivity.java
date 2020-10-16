package com.example.quizapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class QuizActivity extends AppCompatActivity {

    public static final String RETURN_SCORE = "return_score";
    private static final long COUNTDOWN_IN_MILLIS = 30000;

    private static final String KEY_SCORE = "keyScore";
    private static final String KEY_QUESTION_COUNT = "keyQuestionCount";
    private static final String KEY_MILLIS_LEFT ="keyMillisLeft";
    private static final String KEY_ANSWERED ="keyAnswered";
    private static final String KEY_QUESTION_LIST ="keyQuestionList";

    private TextView tvQuestion;
    private TextView tvScore;
    private TextView tvQuestionCount;
    private TextView tvCountdown;
    private RadioGroup rGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private Button buttonConfirmNext;

    private ColorStateList textColorDefaultRb;
    private ColorStateList textColorDefaultCd;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    private ArrayList<Question> questionList;
    private int questionCounter;
    private int questionCountTotal;
    private Question currentQuestion;

    private int score;
    private boolean answered;

    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        tvQuestion = findViewById(R.id.tv_question);
        tvScore = findViewById(R.id.tv_score);
        tvQuestionCount = findViewById(R.id.tv_question_count);
        tvCountdown = findViewById(R.id.tv_countdown);
        rGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_button1);
        rb2 = findViewById(R.id.radio_button2);
        rb3 = findViewById(R.id.radio_button3);
        buttonConfirmNext = findViewById(R.id.button_confirm_next);

        textColorDefaultRb = rb1.getTextColors();
        textColorDefaultCd = tvCountdown.getTextColors();

        if (savedInstanceState == null) {
            QuizDbHelper dbHelper = new QuizDbHelper(this);
            questionList = dbHelper.getAllQuestions();

            questionCountTotal = questionList.size();
            Collections.shuffle(questionList);
            showNextQuestion();
        }else {
            questionList = savedInstanceState.getParcelableArrayList(KEY_QUESTION_LIST);
            if (questionList == null){
                finish();
            }else {
                questionCountTotal = questionList.size();
            }
            questionCounter = savedInstanceState.getInt(KEY_QUESTION_COUNT);
            currentQuestion = questionList.get(questionCounter - 1);
            score = savedInstanceState.getInt(KEY_SCORE);
            timeLeftInMillis = savedInstanceState.getLong(KEY_MILLIS_LEFT);
            answered = savedInstanceState.getBoolean(KEY_ANSWERED);

            if (!answered){
                startCountdown();
            }else {
                updateCountDownText();
            }
        }

        buttonConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!answered){
                   if(rb1.isChecked() || rb2.isChecked() || rb3.isChecked()){
                       checkAnswer();
                   }else {
                       Toast.makeText(QuizActivity.this, "Please, select one answer", Toast.LENGTH_LONG).show();
                   }
                }else {
                    showNextQuestion();
                }
            }
        });
    }

    private void showNextQuestion(){
        rb1.setTextColor(textColorDefaultRb);
        rb2.setTextColor(textColorDefaultRb);
        rb3.setTextColor(textColorDefaultRb);
        rGroup.clearCheck();

        if (questionCounter < questionCountTotal){
            currentQuestion = questionList.get(questionCounter);

            tvQuestion.setText(currentQuestion.getQuestion());
            rb1.setText(currentQuestion.getOption1());
            rb2.setText(currentQuestion.getOption2());
            rb3.setText(currentQuestion.getOption3());

            questionCounter++;
            tvQuestionCount.setText("Question: " + questionCounter + "/" + questionCountTotal);
            answered = false;
            buttonConfirmNext.setText("Confirm");

            timeLeftInMillis = COUNTDOWN_IN_MILLIS;
            startCountdown();
        }else {
            finishQuiz();
        }

    }

    private void startCountdown(){
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();

            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateCountDownText();
                checkAnswer();

            }
        }.start();
    }

    private void updateCountDownText(){
        int minutes = (int)(timeLeftInMillis / 1000) /60;
        int seconds = (int)(timeLeftInMillis / 1000) % 60;

        String timeFormat = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        tvCountdown.setText(timeFormat);
        if (timeLeftInMillis < 10000){
            tvCountdown.setTextColor(Color.RED);
        }else {
            tvCountdown.setTextColor(textColorDefaultCd);
        }

    }

    private void checkAnswer(){

        answered = true;

        countDownTimer.cancel();

        RadioButton rbSelected = findViewById(rGroup.getCheckedRadioButtonId());
        int answerNr = rGroup.indexOfChild(rbSelected) + 1;

        if(answerNr== currentQuestion.getAnswereNr()){
            score++;
            tvScore.setText("Score: " + score);

        }

        showSolution();
    }

    private void showSolution(){
        rb1.setTextColor(Color.RED);
        rb2.setTextColor(Color.RED);
        rb3.setTextColor(Color.RED);

        switch (currentQuestion.getAnswereNr()){
            case 1:
                rb1.setTextColor(Color.GREEN);
                tvQuestion.setText("Answer A is correct!");
                break;
            case 2:
                rb2.setTextColor(Color.GREEN);
                tvQuestion.setText("Answer B is correct!");
                break;
            case 3:
                rb3.setTextColor(Color.GREEN);
                tvQuestion.setText("Answer C is correct!");
                break;
        }

        if (questionCounter < questionCountTotal){
            buttonConfirmNext.setText("Next");
        } else {
            buttonConfirmNext.setText("Finish");
        }
    }

    private void finishQuiz(){
        Intent resultIntent = new Intent();
        resultIntent.putExtra(RETURN_SCORE, score);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis()){
            finishQuiz();
        }else {
            Toast.makeText(this, "Press back again to finish", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null){
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SCORE, score);
        outState.putInt(KEY_QUESTION_COUNT, questionCounter);
        outState.putLong(KEY_MILLIS_LEFT, timeLeftInMillis);
        outState.putBoolean(KEY_ANSWERED, answered);
        outState.putParcelableArrayList(KEY_QUESTION_LIST, questionList);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.btn_close){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
