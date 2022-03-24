package com.example.booklistingapp;

/**
 * class for book entity
 */
public class Book {
    private String mTitle;
    private String[] mAuthors;
    private String mPublisher;
    private String mDate; //published date
    private String mPriviewLink;

    //constructor
    public Book(String title,String[] authors, String publisher, String date, String previewLink){
        mTitle = title;
        mAuthors = authors;
        mPublisher = publisher;
        mDate = date;
        mPriviewLink = previewLink;
    }
    public String getTitle(){
        return mTitle;
    }
    public String[] getAuthors(){
        return mAuthors;
    }
    public String getPublisher(){
        return mPublisher;
    }
    public String getPublishedDate(){
        return mDate;
    }
    public String getPreviewLink(){
        return mPriviewLink;
    }
}
