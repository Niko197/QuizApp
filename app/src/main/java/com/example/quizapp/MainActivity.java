package com.example.quizapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_QUIZ = 1;

    public static final String SHARED_PREFERENCES = "sharedPreferences";
    public static final String KEY_HIGHSCORE = "keyHighscore";

    private TextView tvHighscore;
    private int highscore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvHighscore = findViewById(R.id.tv_highscore);
        loadHighscore();

        Button buttonStartQuiz = findViewById(R.id.button_start_quiz);
        buttonStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuiz();

            }
        });
    }

    private void startQuiz(){
        Intent intent = new Intent(MainActivity.this, QuizActivity.class);
        startActivityForResult(intent, REQUEST_CODE_QUIZ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_QUIZ){
            if (resultCode == RESULT_OK){
                int score = data.getIntExtra(QuizActivity.RETURN_SCORE, 0);
                if (score > highscore){
                    updateHighscore(score);
                }
            }
        }
    }

    private void loadHighscore(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        highscore = sharedPreferences.getInt(KEY_HIGHSCORE, 0);
        tvHighscore.setText("Highscore: " + highscore);

    }

    private void updateHighscore(int highscoreNew){
        highscore = highscoreNew;
        tvHighscore.setText("Highscore: " + highscore);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_HIGHSCORE, highscore);
        editor.apply();
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
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setIconAttribute(android.R.attr.alertDialogIcon);
            builder.setTitle("Exit from app");
            builder.setCancelable(true);
            builder.setMessage("Do you want to exit from this application?");

            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }
        return true;
    }
}
