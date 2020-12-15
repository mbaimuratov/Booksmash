package com.example.booksmash;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class PastBooksActivity extends AppCompatActivity {

    private PastBooksAdapter mPastBooksAdapter;

    private DatabaseReference mBooksDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;
    private ChildEventListener mChildEventListener;

    private DatabaseReference mPastBooksDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_books);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setting up db references
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mBooksDatabaseReference = mFirebaseDatabase.getReference().child("books");
        mPastBooksDatabaseReference = mBooksDatabaseReference.child("past_books");

        ListView pastBookListView = findViewById(R.id.list_view_past_books);

        List<BookItem> bookItemList = new ArrayList();
        mPastBooksAdapter = new PastBooksAdapter(this, R.layout.past_book_list_item, bookItemList);
        pastBookListView.setAdapter(mPastBooksAdapter);

        attachDatabaseReadListener(); //???

        pastBookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO: implement past_book_info_activity
                return;
            }
        });

        //TODO: create empty view
    }

    private void deleteAllBooks() {
        mPastBooksDatabaseReference.removeValue();
    }

    private void insertDummyBook() {
        BookItem bookItem = new BookItem(
                "Dandelion wine",
                "Ray Bradbury",
                1980,
                300,
                5,
                "Classic",
                600,
                300,
                "dummyURl"
        );
        mPastBooksDatabaseReference.push().setValue(bookItem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_past_books_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_insert_dummy_data) {
            insertDummyBook();
        } else if (item.getItemId() == R.id.action_delete_all_book) {
            deleteAllBooks();
        }
        return super.onOptionsItemSelected(item);
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    BookItem bookItem = dataSnapshot.getValue(BookItem.class);
                    mPastBooksAdapter.add(bookItem);
                    Log.v("onChildAdded", "onChildAdded");
                }
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    mPastBooksAdapter.clear();
                }
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                public void onCancelled(DatabaseError databaseError) {}
            };
            mPastBooksDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mPastBooksDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    protected void onPause() {
        detachDatabaseReadListener();
        super.onPause();
    }
}