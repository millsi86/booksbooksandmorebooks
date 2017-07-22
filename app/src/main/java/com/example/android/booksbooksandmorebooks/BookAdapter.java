package com.example.android.booksbooksandmorebooks;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by tom.mills-mock on 20/07/2017.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        // Get the book information for the current position
        Book currentBook = getItem(position);

        // check for a re-useable view
        Views views;
        View listItemView = convertView;
        if (convertView == null) {
            views = new Views();
            LayoutInflater inflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.book_list_item, parent, false);
            // find the views from the book list view
            views.title = (TextView) convertView.findViewById(R.id.title);
            views.subTitle = (TextView) convertView.findViewById(R.id.subTitle);
            views.author = (TextView) convertView.findViewById(R.id.author);
            convertView.setTag(views);
        } else {
            views = (Views) convertView.getTag();
        }

        // get the data into the views
        if (currentBook != null) {
            views.title.setText(currentBook.getTitle());
            views.subTitle.setText(currentBook.getSubtitle());
            views.author.setText(currentBook.getAuthor());
        }

        return convertView;
    }

    public static class Views {
        public TextView title;
        public TextView subTitle;
        public TextView author;
    }


}
