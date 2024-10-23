package com.example.readinghub;

import java.util.Vector;

public interface BooksInfoCallback {
    void onBooksInfoReceived(Vector<Vector<String>> booksInfo);
}