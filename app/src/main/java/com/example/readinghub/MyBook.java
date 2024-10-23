package com.example.readinghub;

public class MyBook extends Book implements BookInterface{
    private String deadline = "Add a deadline";
    private String pagesRead = "0";

    public MyBook(String title, String author, String pubYear, String pgCount, String cover, String deadline, String pagesRead) {
        super(title, author, pubYear, pgCount, cover);
        this.deadline = deadline;
        this.pagesRead = pagesRead;
    }

    public MyBook(Book book, String deadline, String pagesRead) {
        super(book.getTitle(), book.getAuthor(), book.getPubYear(), book.getPgCount(), book.getCover());
    }

    public MyBook(Book book){
        super(book.getTitle(), book.getAuthor(), book.getPubYear(), book.getPgCount(), book.getCover());
        this.deadline = deadline;
        this.pagesRead = pagesRead;
    }

    @Override
    public String getDeadline() {
        return deadline;
    }

    @Override
    public String getPagesRead() {
        return pagesRead;
    }

    public void setPagesRead(String pagesRead) {
        this.pagesRead = pagesRead;
    }

    public void setDeadline(String formattedDate) {this.deadline = formattedDate;
    }

    public Book myBookToBook(){
        Book book = new Book(this.getTitle(), this.getAuthor(), this.getPubYear(), this.getPgCount(), this.getCover());
        return book;
    }
}
