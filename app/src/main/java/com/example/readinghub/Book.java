package com.example.readinghub;

public class Book implements BookInterface{
    private final String title;
    private final String author;
    private final String pubYear;
    private String pgCount;
    private String cover;

    public Book(String title, String author, String pubYear,String pgCount,String cover) {
        this.title = title;
        this.author = author;
        this.pubYear = pubYear;
        this.pgCount = pgCount;
        this.cover = cover;
    }

    public  String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPubYear() {
        return pubYear;
    }

    public String getPgCount() {
        return pgCount;
    }

    public String getCover() {
        return cover;
    }


}
