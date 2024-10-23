package com.example.readinghub;

public interface BookInterface {
        String getTitle();
        String getAuthor();
        String getPubYear();
        String getPgCount();
        String getCover();

        default String getDeadline() {
                return "01012000";
        }

        default String getPagesRead() {
                return "0";
        }
}
