package com.example.readinghub;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Vector;

public class SharedPreferencesHelper{
    private SharedPreferences sp;
    private Gson gson;

    public SharedPreferencesHelper(Context context){
        this.sp = context.getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        this.gson = new Gson();
    }
    public void SaveUserInfoString(String key,String value){
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putString(key, value);
        spEditor.apply();

    }

    public String GetUserInfoString(String key){
        String data = sp.getString(key,"");
        return data;
    }
    public <Mybook extends BookInterface> void saveUserInfoBook(String key, Vector<Mybook> picks) {
        SharedPreferences.Editor editor = sp.edit();
        Vector<Vector<String>> Info = new Vector<>();
        for(Mybook book : picks){
            Vector<String> bookProps = new Vector<>();
            bookProps.add(book.getTitle());
            bookProps.add(book.getAuthor());
            bookProps.add(book.getPubYear());
            bookProps.add(book.getPgCount());
            bookProps.add(book.getCover());
            bookProps.add(book.getDeadline());
            bookProps.add(book.getPagesRead());
            Info.add(bookProps);
        }
        String json = gson.toJson(Info);
        editor.putString(key, json);
        editor.apply();
    }


    public Vector<MyBook> GetUserInfoBook(String key) {
        String json = sp.getString(key, "");
        if (json.isEmpty()) {
            return new Vector<>(); // Return an empty vector if no data is found
        }

        Type type = new TypeToken<Vector<Vector<String>>>() {}.getType();
        Vector<Vector<String>> info = gson.fromJson(json, type);

        Vector<MyBook> myBooks = new Vector<>();
        for (Vector<String> bookProps : info) {
            String title = bookProps.get(0);
            String author = bookProps.get(1);
            String pubYear = bookProps.get(2);
            String pgCount = bookProps.get(3);
            String cover = bookProps.get(4);
            String deadline = bookProps.size() > 5 ? bookProps.get(5) : "01012000";
            String pagesRead = bookProps.size() > 6 ? bookProps.get(6) : "0";

            MyBook myBook = new MyBook(title, author, pubYear, pgCount, cover, deadline, pagesRead);
            myBooks.add(myBook);
        }

        return myBooks;
    }
}
