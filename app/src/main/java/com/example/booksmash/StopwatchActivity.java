package com.example.booksmash;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class StopwatchActivity extends AppCompatActivity {
    private static final String PAGES_TODAY_KEY = "page count";
    private static int mSeconds;

    private boolean isRunning;

    private boolean wasRunning;

    private static final String SECONDS_KEY = "seconds";
    private static final String IS_RUNNING_KEY = "running";
    private static final String WAS_RUNNING_KEY = "wasRunning";

    private static Button mStartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            mSeconds = savedInstanceState.getInt(SECONDS_KEY);
            isRunning = savedInstanceState.getBoolean(IS_RUNNING_KEY);
            wasRunning = savedInstanceState.getBoolean(WAS_RUNNING_KEY);
        }
        isRunning = true;

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra(SECONDS_KEY)) {
            mSeconds = intentThatStartedThisActivity.getIntExtra(SECONDS_KEY, 0);
        }

        mStartButton = findViewById(R.id.start_button);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning = true;
            }
        });
        runTimer();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(SECONDS_KEY, mSeconds);
        savedInstanceState.putBoolean(IS_RUNNING_KEY, isRunning);
        savedInstanceState.putBoolean(WAS_RUNNING_KEY, wasRunning);
    }

    @Override
    protected void onPause() {
        super.onPause();
        wasRunning = isRunning;
        isRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wasRunning) {
            isRunning = true;
        }
    }

    public void onClickStop(View view) {
        // Show dialog that there are unsaved changes
        isRunning = false;
        showEnterPagesRead();
    }

    public void onClickPause(View view) {
        isRunning = false;
    }

    private void runTimer() {
        final Handler handler = new Handler(Looper.getMainLooper());
        final TextView mTimeCountTextView = findViewById(R.id.stopwatch_count_tv);
        // Call the post() method, passing in a new Runnable.
        // The post() method processes code without a delay, so the code in the Runnable
        // will run almost immediately.
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = mSeconds / 3600;
                int minutes = (mSeconds % 3600) / 60;
                int secs = mSeconds % 60;

                String time = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, secs);
                mTimeCountTextView.setText(time);
                if (isRunning) {
                    mSeconds++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void showEnterPagesRead() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final EditText pagesTodayEditText = new EditText(this);
        pagesTodayEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        alertDialogBuilder.setView(pagesTodayEditText).setMessage(R.string.pages_dialog_question);
        alertDialogBuilder.setPositiveButton(R.string.done_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //mEnteredPageNumber = Integer.parseInt(pagesTodayEditText.getText().toString());
                        int pagesEntered = Integer.parseInt(pagesTodayEditText.getText().toString());
                        Intent intent = new Intent();
                        intent.putExtra(PAGES_TODAY_KEY, pagesEntered);
                        intent.putExtra(SECONDS_KEY, mSeconds);
                        setResult(8, intent);
                        finish();
                    }})
                .setNegativeButton(R.string.cancel_label, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (dialog != null) {
                            dialog.dismiss();
                            isRunning = true;
                        }
                    }
        });
        alertDialogBuilder.create().show();
    }
}