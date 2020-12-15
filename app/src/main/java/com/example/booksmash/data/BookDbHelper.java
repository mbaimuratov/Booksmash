package com.example.booksmash.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.booksmash.data.BookContract.BookEntry;

public class BookDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "books.db";
    private static final int DATABASE_VERSION = 1;

    public BookDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME +
                "(" +
                BookEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BookEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL," +
                BookEntry.COLUMN_BOOK_AUTHOR + " TEXT," +
                BookEntry.COLUMN_BOOK_PUBLISH_YEAR + " INTEGER," +
                BookEntry.COLUMN_BOOK_PAGE_COUNT + " INTEGER NOT NULL DEFAULT 1," +
                BookEntry.COLUMN_BOOK_RATING + " INTEGER NOT NULL DEFAULT 0," +
                BookEntry.COLUMN_BOOK_GENRE + " TEXT NOT NULL," +
                BookEntry.COLUMN_BOOK_TIME_SPENT + " INTEGER NOT NULL DEFAULT 0" +
                ");";
        sqLiteDatabase.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        }
    }
}
