package com.example.android.booksbooksandmorebooks;

/**
 * Created by tom.mills-mock on 20/07/2017.
 */

public class Book {

    // Title of the book
    private String Title;

    //Subtitle of the book
    private String Subtitle;

    // Author of the book
    private String Author;

    public Book(String title, String subtitle, String author) {

        Title = title;
        Subtitle = subtitle;
        Author = author;
    }

    public String getTitle() {
        return Title;
    }

    public String getSubtitle() {
        return Subtitle;
    }

    public String getAuthor() {
        return Author;
    }

}
