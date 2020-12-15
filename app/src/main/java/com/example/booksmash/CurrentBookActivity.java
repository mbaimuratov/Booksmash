package com.example.booksmash;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;

import static java.lang.String.format;
import static java.lang.String.valueOf;

public class CurrentBookActivity extends AppCompatActivity {

    private static boolean HAS_CURRENT = false;
    private TextView mTimeCountTextView;
    private TextView mPageCountTextView;
    private TextView mAuthorNameYearTextView;
    private TextView mBookNameTextView;
    private TextView mEmptyCurrentBookTextView;
    private ConstraintLayout mCurrentBookConstraintLayout;

    private int mSecondsToday = 0;
    private int mPagesProgress = 0;
    private int mPageTotal = 1;
    private String mAuthorName;
    private int mPublishYear = 0;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCurrentBooksDatabaseReference;
    private DatabaseReference mPastBooksDatabaseReference;
    //private ChildEventListener mChildEventListener;

    private BookItem mCurrentBookItem;

    public static final int STOPWATCH_REQUEST = 8;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_book);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setting database reference
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mCurrentBooksDatabaseReference = mFirebaseDatabase.getReference().child("books/current");
        mPastBooksDatabaseReference = mFirebaseDatabase.getReference().child("books/past_books");

        //setting views
        mEmptyCurrentBookTextView = findViewById(R.id.empty_current_book_tv);
        mCurrentBookConstraintLayout = findViewById(R.id.current_book_constraintLayout);
        mTimeCountTextView = findViewById(R.id.time_today_count_tv);
        mPageCountTextView = findViewById(R.id.pages_today_count_tv);
        mBookNameTextView = findViewById(R.id.current_book_name_tv);
        mAuthorNameYearTextView = findViewById(R.id.current_book_author_and_year_tv);

        mCurrentBooksDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    mEmptyCurrentBookTextView.setVisibility(View.GONE);
                    mCurrentBookConstraintLayout.setVisibility(View.VISIBLE);
                    HAS_CURRENT = true;
                    invalidateOptionsMenu();

                    //retrieving data from db
                    mCurrentBookItem = snapshot.getValue(BookItem.class);

                    //total page count
                    mPageTotal = mCurrentBookItem.getPageCount();
                    mPagesProgress = mCurrentBookItem.getPageCountProgress();
                    if (mPagesProgress >= mPageTotal) {
                        final AlertDialog.Builder alertDialogBuilder =
                                new AlertDialog.Builder(CurrentBookActivity.this);
                        final EditText ratingEditText = new EditText(CurrentBookActivity.this);
                        ratingEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        alertDialogBuilder.setView(ratingEditText).setMessage("Give a rating to a book");
                        alertDialogBuilder.setPositiveButton(R.string.done_label,
                                new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int ratingEntered = Integer.parseInt(ratingEditText.getText().toString());
                                moveToPast(ratingEntered);
                            }})
                                .setNegativeButton(R.string.cancel_label,
                                        new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (dialog != null) {
                                            dialog.dismiss();
                                        }
                                    }
                                });
                        alertDialogBuilder.create().show();
                    }
                    mPageCountTextView.setText(String.valueOf(mPagesProgress));
                    updateChart();

                    //book name
                    mBookNameTextView.setText(mCurrentBookItem.getBookName());

                    //author name and publish year
                    mAuthorName = mCurrentBookItem.getAuthorName();
                    mPublishYear = mCurrentBookItem.getPublishYear();
                    mAuthorNameYearTextView.setText(String.format("%s, %d", mAuthorName, mPublishYear));

                    //time progress
                    mSecondsToday = mCurrentBookItem.getTimeSpent();
                    setTimeCountHelper();
                } else {
                    mEmptyCurrentBookTextView.setVisibility(View.VISIBLE);
                    mCurrentBookConstraintLayout.setVisibility(View.GONE);
                    HAS_CURRENT = false;
                    invalidateOptionsMenu();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        LinearLayout mStartTimer = findViewById(R.id.start_timer_linearLayout);
        mStartTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent stopwatchIntent = new Intent(CurrentBookActivity.this, StopwatchActivity.class);
                stopwatchIntent.putExtra("seconds", mSecondsToday);
                startActivityForResult(stopwatchIntent, STOPWATCH_REQUEST);
            }
        });

        setTimeCountHelper();
        updateChart();

        //attachDatabaseReadListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STOPWATCH_REQUEST) {
            mSecondsToday += data.getIntExtra("seconds", 0);
            mPagesProgress += data.getIntExtra("page count", 0);
            setTimeCountHelper();
            mPageCountTextView.setText(valueOf(mPagesProgress));
            updateChart();

            HashMap map = new HashMap();
            map.put("timeSpent", mSecondsToday);
            map.put("pageCountProgress", mPagesProgress);
            mCurrentBooksDatabaseReference.updateChildren(map).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(CurrentBookActivity.this, "UPDATED", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CurrentBookActivity.this, "FAILED", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void setTimeCountHelper() {
        int hours = mSecondsToday / 3600;
        int minutes = (mSecondsToday % 3600) / 60;
        int secs = mSecondsToday % 60;
        String time = format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, secs);
        mTimeCountTextView.setText(time);
    }

    private void updateChart() {
        int percent = (int) (((double) mPagesProgress / mPageTotal) * 100.0);
        ((TextView) findViewById(R.id.percent_count_tv)).setText(percent + "%");

        ProgressBar pieChart = findViewById(R.id.stats_pb);
        pieChart.setProgress(percent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_add_current_book) {
            Intent addNewCurrentBookIntent = new Intent(CurrentBookActivity.this, EditorActivity.class);
            startActivity(addNewCurrentBookIntent);
            return true;
        }
        else if (itemId == R.id.action_move_to_past) {
            moveToPast(3);
            return true;
        }
        else if (itemId == R.id.action_insert_dummy_current) {
            BookItem bookItem = new BookItem(
                    "Dummy Book",
                    "Dummy Author",
                    2020,
                    500,
                    0,
                    "Dummy genre",
                    0,
                    0,
                    "Dummy url"
            );
            mCurrentBooksDatabaseReference.setValue(bookItem);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_current_book, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_add_current_book).setVisible(!HAS_CURRENT);
        return true;
    }

    private void moveToPast(final int rating) {
        mCurrentBooksDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                BookItem bookItem = snapshot.getValue(BookItem.class);
                bookItem.setRating(rating);
                mPastBooksDatabaseReference.push().setValue(bookItem,
                        new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error,
                                                   @NonNull DatabaseReference ref) {
                                if (error != null) {
                                    Log.v("Gulia", error.getMessage());
                                } else {
                                    Log.v("Gulia", "error == null");
                                }
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        mCurrentBooksDatabaseReference.removeValue();
    }
}