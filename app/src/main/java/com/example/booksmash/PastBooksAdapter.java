package com.example.booksmash;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.List;

public class PastBooksAdapter extends ArrayAdapter<BookItem> {
    public PastBooksAdapter(@NonNull Context context, int resource, List<BookItem> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext())
                    .getLayoutInflater()
                    .inflate(R.layout.past_book_list_item, parent, false);
        }

        ImageView bookImageView = convertView.findViewById(R.id.past_book_cover_image_view);
        TextView bookName = convertView.findViewById(R.id.past_book_book_name_tv);
        TextView authorName = convertView.findViewById(R.id.past_book_author_name_tv);
        RatingBar ratingBar = convertView.findViewById(R.id.past_book_rating);

        BookItem bookItem = getItem(position);

        Glide.with(bookImageView.getContext())
                .load(bookItem.getPhotoUrl())
                .into(bookImageView);
        bookName.setText(bookItem.getBookName());
        authorName.setText(bookItem.getAuthorName());
        ratingBar.setRating(bookItem.getRating());

        return convertView;
    }
}
