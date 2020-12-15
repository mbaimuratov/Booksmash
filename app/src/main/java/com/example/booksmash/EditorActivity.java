package com.example.booksmash;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Objects;

public class EditorActivity extends AppCompatActivity {

    private static final int RC_PHOTO_PICKER =  2;

    private TextInputLayout mPublishYearTextInputLayout;
    private TextInputEditText mPublishYearTextInputEditText;
    private EditText mBookNameEditText;
    private EditText mAuthorNameEditText;
    private EditText mPageCountEditText;
    private EditText mGenreEditText;
    private ImageView mImageView;

    private Uri mDatabaseImageUri;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mBooksDatabaseReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mBookPhotosStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setting up database reference
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mBooksDatabaseReference = mFirebaseDatabase.getReference().child("books");

        //setting up the storage
        mFirebaseStorage = FirebaseStorage.getInstance();
        mBookPhotosStorageReference = mFirebaseStorage.getReference().child("photos");

        // Initialize references to views
        mImageView = findViewById(R.id.tmpimage);
        mBookNameEditText = findViewById(R.id.book_name_et);
        mAuthorNameEditText = findViewById(R.id.book_author_et);
        mPageCountEditText = findViewById(R.id.page_count_et);
        mGenreEditText = findViewById(R.id.genre_et);
        mPublishYearTextInputLayout = findViewById(R.id.publish_year_textInputLayout);
        mPublishYearTextInputEditText = findViewById(R.id.publish_year_textInputEditText);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        mPublishYearTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                validatePublishYear(editable);
            }
        });

        mPublishYearTextInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validatePublishYear(((EditText) v).getText());
                }
            }
        });
    }

    private void validatePublishYear(Editable s) {
        if (!TextUtils.isEmpty(s)) {
            int year = Integer.parseInt(s.toString());
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            if (currentYear < year) {
                mPublishYearTextInputLayout.setError("Invalid year");
            } else {
                mPublishYearTextInputLayout.setError(null);
            }
        } else {
            mPublishYearTextInputLayout.setError(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case RC_PHOTO_PICKER:
                findViewById(R.id.add_photo_label).setVisibility(View.INVISIBLE);
                assert data != null;
                Uri selectedImageUri = data.getData();
                Glide.with(this).load(selectedImageUri).into(mImageView);
                // Get a reference to store file at chat_photos/<FILENAME>
                final StorageReference photoRef = mBookPhotosStorageReference.child(selectedImageUri.
                        getLastPathSegment());

                photoRef.putFile(selectedImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }
                        return photoRef.getDownloadUrl();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            mDatabaseImageUri = task.getResult();
                        } else {
                            Toast.makeText(EditorActivity.this, "Failure", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_save_book) {
            String bookName = mBookNameEditText.getText().toString();
            String authorName = mAuthorNameEditText.getText().toString();
            int publishYear = Integer.parseInt(String.valueOf(mPublishYearTextInputEditText.getText()));
            int pageCount = Integer.parseInt(String.valueOf(mPageCountEditText.getText()));
            String genre = mGenreEditText.getText().toString();

            BookItem bookItem = new BookItem(
                    bookName,
                    authorName,
                    publishYear,
                    pageCount,
                    0,
                    genre,
                    0,
                    0,
                    mDatabaseImageUri.toString()
            );

            mBooksDatabaseReference.child("current").setValue(bookItem);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}