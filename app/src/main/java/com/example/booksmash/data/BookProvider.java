package com.example.booksmash.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BookProvider extends ContentProvider {

    private static final int BOOKS = 100;

    private static final int BOOK_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    static {
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    private BookDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) throws IllegalArgumentException {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getReadableDatabase();
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case BOOKS:
                cursor = sqLiteDatabase.query(BookContract.BookEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = BookContract.BookEntry._ID + "+?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = sqLiteDatabase.query(BookContract.BookEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookContract.BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookContract.BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        if (match == BOOKS) {
            assert contentValues != null;
            return insertBook(uri, contentValues);
        }
        throw new IllegalArgumentException("Insertion is not supported for " + uri);
    }

    private Uri insertBook(Uri uri, ContentValues contentValues) {
        String name = contentValues.getAsString(BookContract.BookEntry.COLUMN_BOOK_NAME);
        if (name == null)
            throw new IllegalArgumentException("Book requires a name");

        Integer page_count = contentValues.getAsInteger(BookContract.BookEntry.COLUMN_BOOK_PAGE_COUNT);
        if (page_count == null || page_count < 1)
            throw new IllegalArgumentException("Book requires valid page count");

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(BookContract.BookEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                getContext().getContentResolver().notifyChange(uri, null);
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        assert contentValues != null;
        if (contentValues.size() == 0)
            return 0;

        if (contentValues.containsKey(BookContract.BookEntry.COLUMN_BOOK_NAME)) {
            if (contentValues.getAsString(BookContract.BookEntry.COLUMN_BOOK_NAME) == null)
                throw new IllegalArgumentException("Book requires a name!");
        }

        if (contentValues.containsKey(BookContract.BookEntry.COLUMN_BOOK_PAGE_COUNT)) {
            Integer page_count = contentValues.getAsInteger(BookContract.BookEntry.COLUMN_BOOK_PAGE_COUNT);
            if (page_count == null && page_count < 1)
                throw new IllegalArgumentException("Book requires valid page count");
        }

        if (contentValues.containsKey(BookContract.BookEntry.COLUMN_BOOK_RATING)) {
            Integer rating = contentValues.getAsInteger(BookContract.BookEntry.COLUMN_BOOK_RATING);
            if (rating == null || rating < 0 || rating > 5)
                throw new IllegalArgumentException("Book requires valid rating");
        }

        if (contentValues.containsKey(BookContract.BookEntry.COLUMN_BOOK_TIME_SPENT)) {
            int time = contentValues.getAsInteger(BookContract.BookEntry.COLUMN_BOOK_TIME_SPENT);
            if (time < 0)
                throw new IllegalArgumentException("Book requires valid time spent");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(BookContract.BookEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }
}
