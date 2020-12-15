package com.example.booksmash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView currentBlockTextView = findViewById(R.id.current_book_tv);
        TextView futureBooksTextView = findViewById(R.id.future_books_tv);
        TextView pastBooksTextView = findViewById(R.id.past_book_tv);
        TextView recommendationsTextView = findViewById(R.id.recommendations_tv);

        currentBlockTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent currentBookIntent = new Intent(MainActivity.this, CurrentBookActivity.class);
                startActivity(currentBookIntent);
            }
        });

        futureBooksTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent futureBooksIntent = new Intent(MainActivity.this, FutureBooksActivity.class);
                startActivity(futureBooksIntent);
            }
        });

        pastBooksTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pastBooksIntent = new Intent(MainActivity.this, PastBooksActivity.class);
                startActivity(pastBooksIntent);
            }
        });

        recommendationsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}