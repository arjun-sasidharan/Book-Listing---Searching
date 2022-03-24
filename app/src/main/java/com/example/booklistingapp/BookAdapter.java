package com.example.booklistingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {

    //constructor
    public BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //check if existing view is used, if it is null inflate new view
        View listItemView= convertView;
        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list, parent, false);
        }

        //get the object located at the position in the list
        Book currentBook = getItem(position);

        TextView bookNameView = (TextView) listItemView.findViewById(R.id.book_name);
        bookNameView.setText(currentBook.getTitle());

        TextView authorView = (TextView) listItemView.findViewById(R.id.author);
        String[] authorArray = currentBook.getAuthors();
        String authors = Arrays.toString(authorArray); //here it contain '[', ']' at start and end
        String trimAuthors = authors.substring(1,authors.length()-1);
        authorView.setText("Author: " + trimAuthors);

        TextView publisherView = (TextView) listItemView.findViewById(R.id.publisher);
        publisherView.setText("Publisher: " + currentBook.getPublisher());

        TextView dateView = (TextView) listItemView.findViewById(R.id.published_date);
        dateView.setText("Published Date: " + currentBook.getPublishedDate());

        return listItemView;
    }

}

