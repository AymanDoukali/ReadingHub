package com.example.readinghub;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.util.HashSet;
import java.util.Vector;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity implements BooksInfoCallback {
    Vector<Book> searchBooks = new Vector<Book>();
    Vector<Book> pickedBooks = new Vector<Book>();

    LinearLayout layoutPicks, layoutSearch;
    Button btnSearch;
    TextView tVReadingProgram, tVBookTitle;
    EditText eTSearchTitle;
    private TextView testView;
    private RequestQueue requestQueue;
    String programName;
    String userName;
    String USER_NAME = "userName";
    String PROGRAM_NAME = "programName";
    TextView topMessage,tVSearchTitle;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        testView = findViewById(R.id.tVAddbookTitle);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferencesHelper sph = new SharedPreferencesHelper(this);
        programName = sph.GetUserInfoString(PROGRAM_NAME);
        userName = sph.GetUserInfoString(USER_NAME);
        requestQueue = Volley.newRequestQueue(this);
        // Widgets
        layoutPicks = findViewById(R.id.layoutPicks);
        layoutSearch = findViewById(R.id.layoutSearch);
        btnSearch = findViewById(R.id.btnSearch);
        tVReadingProgram = findViewById(R.id.tVReadingProgram);
        tVSearchTitle = findViewById(R.id.tVSearchBookTitle);
        eTSearchTitle = findViewById(R.id.editTextSearch);
        topMessage = findViewById(R.id.topMessage);
        topMessage.setText(userName+ " you can add up to 10 books");
        tVReadingProgram.setText("\uD83D\uDCDA " + programName + "\uD83D\uDCDA");
        //
        fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Handle FAB click
//                Intent intent = new Intent(getApplicationContext(),Dashboard.class);
//                startActivity(intent);
//            }
//        });
        if(sph.GetUserInfoBook("picks").isEmpty()){
            fab.setVisibility(View.INVISIBLE);
        }
        // btnNext.setVisibility(View.INVISIBLE);
        tVSearchTitle.setVisibility(View.INVISIBLE);
        //Clear layouts
        layoutSearch.removeAllViews();
        layoutPicks.removeAllViews();

        //Under Testing

        sph = new SharedPreferencesHelper(this);
        Vector<MyBook> pickedMyBooks = sph.GetUserInfoBook("picks");
        for (int i = 0; i < pickedMyBooks.size(); i++){
            addBookToPicks(pickedMyBooks.get(i).myBookToBook());
        }
        renderBooks(layoutPicks);
    }

    private void getBooksInfo(String query) {
        // Call getBooksInfo method passing query and the instance of BooksInfoCallback
        getBooksInfo(query, this);
    }

    private void getBooksInfo(String query, final BooksInfoCallback callback) {
        String url = "https://openlibrary.org/search.json?q=" + query.replace(" ", "+");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Vector<Vector<String>> booksInfo = new Vector<>();
                        try {
                            JSONArray docs = response.getJSONArray("docs");
                            for (int i = 0; i < docs.length() && i < 20; i++) {
                                JSONObject book = docs.getJSONObject(i);
                                Vector<String> bookInfo = new Vector<>();
                                bookInfo.add(book.optString("title", "Unknown Title"));
                                JSONArray authors = book.optJSONArray("author_name");
                                if (authors != null && authors.length() > 0) {
                                    bookInfo.add(authors.getString(0));
                                } else {
                                    bookInfo.add("Unknown Author");
                                }
                                bookInfo.add(book.optString("first_publish_year", "Unknown Publish Date"));
                                bookInfo.add(book.optString("number_of_pages_median", ""));
                                JSONArray isbn = book.optJSONArray("isbn");
                                if (isbn != null && isbn.length() > 0) {
                                    bookInfo.add("https://covers.openlibrary.org/b/isbn/" + isbn.getString(0) + "-M.jpg");
                                } else {
                                    bookInfo.add("");
                                }
                                booksInfo.add(bookInfo);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        callback.onBooksInfoReceived(booksInfo);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error: " + error.toString());
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onBooksInfoReceived(Vector<Vector<String>> booksInfo) {
        searchBooks = new Vector<>();
        for (Vector<String> book : booksInfo) {
            Book Searchedbook = new Book(book.get(0),book.get(1),book.get(2),book.get(3),book.get(4));
            searchBooks.add(Searchedbook);
        }

        renderBooks(layoutSearch);

    }

    public void searchBooks(View view) {
        searchBooks.clear();

        layoutSearch.removeAllViews();

        getBooksInfo(eTSearchTitle.getText().toString());

        String searchTitle = "Searching for " + eTSearchTitle.getText().toString() + "";
        tVSearchTitle.setText(searchTitle);

        tVSearchTitle.setVisibility(View.VISIBLE);
    }

    public void renderBooks(LinearLayout layout){
        Context context = getApplicationContext();
        Vector<Book> books = new Vector<Book>();
        String btnText;


        if (layout == layoutSearch){
            books = searchBooks;
            btnText = "+";
        } else{
            books = pickedBooks;
            btnText = "-";
        }

        Log.i("myApp", String.valueOf(pickedBooks.size()));

        layout.removeAllViews();

        for (int i = 0; i < books.size(); i++){
            Log.i("myApp", String.valueOf(i));
            Book book = (layout == layoutPicks) ? books.get(books.size() - 1 - i) : books.get(i);

            ContextThemeWrapper layoutPickContext = new ContextThemeWrapper(getBaseContext(), R.style.layoutPick);

            LinearLayout layoutBook = new LinearLayout(layoutPickContext);

            layout.addView(layoutBook);

            ImageView iVCover = new ImageView(getApplicationContext());
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                    200,250
            );
            iVCover.setLayoutParams(imageParams);

            String coverImageUrl = book.getCover();

            if (!coverImageUrl.isEmpty()) {
                Glide.with(context)
                        .load(coverImageUrl)
                        .into(iVCover);
            }
            else{iVCover.setImageResource(R.drawable.book_solid_48);}

            layoutBook.addView(iVCover);

            ContextThemeWrapper layoutContext = new ContextThemeWrapper(getBaseContext(), R.style.layoutBookDetails);

//            LinearLayout layoutBookDetails = new LinearLayout(context);
            LinearLayout layoutBookDetails = new LinearLayout(layoutContext);
            layoutBook.addView(layoutBookDetails);

            ContextThemeWrapper bookTitleContext = new ContextThemeWrapper(getBaseContext(), R.style.bookTitle);
            ContextThemeWrapper bookDataContext = new ContextThemeWrapper(this, R.style.bookData);

            TextView tVBookTitle = new TextView(bookTitleContext);
            tVBookTitle.setText(book.getTitle());
            TextView tVBookAuthor = new TextView(bookDataContext);
            tVBookAuthor.setText("By " + book.getAuthor());
            TextView tVPubYearTitle = new TextView(bookDataContext);
            tVPubYearTitle.setText("Year: " + book.getPubYear());
            TextView tVPgCountTitle = new TextView(bookDataContext);
            tVPgCountTitle.setText("Pages: " + book.getPgCount());

            layoutBookDetails.addView(tVBookTitle);
            layoutBookDetails.addView(tVBookAuthor);
            layoutBookDetails.addView(tVPubYearTitle);
            layoutBookDetails.addView(tVPgCountTitle);

            ContextThemeWrapper btnContext = new ContextThemeWrapper(this, R.style.btnAddOrDelete);
            Button btn = new MaterialButton(btnContext);
            btn.setText(btnText);
            layoutBook.addView(btn);

            int imageWidth = 200;
            int btnWidth = btn.getWidth();

            // Get the screen width in pixels
            WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;

            int maxWidth = width - imageWidth - btnWidth - 150;
            tVBookTitle.setMaxWidth(maxWidth);
            layoutBookDetails.setLayoutParams(new LinearLayout.LayoutParams(
                    maxWidth,  // Set a fixed width
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            ContextThemeWrapper layoutBrContext = new ContextThemeWrapper(this, R.style.splitter);
            LinearLayout br = new LinearLayout(layoutBrContext);
            LinearLayout.LayoutParams brParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,  // Set a fixed width
                    2
            );

            brParams.setMargins(20,0,20,0);
            br.setLayoutParams(brParams);
            layout.addView(br);

//            TextView tv = new TextView(getApplicationContext());
//            tv.setText(" "); br.addView(tv);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (layout==layoutSearch){
                        boolean found = false;
                        for (Book oldBook : pickedBooks){
                            if (oldBook.getTitle().equals(book.getTitle())){
                                found = true;
                            }
                        }
                        if (found){
                            Toast.makeText(context, book.getTitle() + " is already picked.", Toast.LENGTH_SHORT).show();
                        } else{
                            addBookToPicks(book);
                            Toast.makeText(context, book.getTitle() + " added to your picks", Toast.LENGTH_SHORT).show();
                        }
                    } else{
                        removeBookFromPicks(book);
                    }
                    renderBooks(layoutPicks);
                }
            });
        }
    }

    public void addBookToPicks(Book book){
        if (pickedBooks.size()>= 10){
            Toast.makeText(getApplicationContext(), "You have already added 10 books", Toast.LENGTH_SHORT).show();
        } else{
            pickedBooks.add(book);
        }
        fab.setVisibility(View.VISIBLE);
    }

    public void removeBookFromPicks(Book book){
        pickedBooks.remove(book);
        if (pickedBooks.size()==0){
            fab.setVisibility(View.INVISIBLE);
        }
    }

    public void nextScreen(View view) {
        Intent myIntent = new Intent(this, Dashboard.class);
        SharedPreferencesHelper sph = new SharedPreferencesHelper(getApplicationContext());
        Vector<MyBook> storedBooks = sph.GetUserInfoBook("picks");

        /* Amine Code
        // Use a HashSet to track titles for efficient lookup
        HashSet<String> storedTitles = new HashSet<>();
        for (MyBook storedBook : storedBooks) {
            storedTitles.add(storedBook.getTitle());
        }

        for (Book book : pickedBooks) {
            String title = book.getTitle();
            if (!storedTitles.contains(title)) {
                storedBooks.add(new MyBook(book, book.getDeadline(), book.getPagesRead()));
            }
        }

        sph.saveUserInfoBook("picks", storedBooks);*/

        Vector<MyBook> myBooks = new Vector<>();

        for (Book book : pickedBooks){
            boolean found = false;
            for (MyBook storedBook : storedBooks){
                if (book.getTitle().equals(storedBook.getTitle())){
                    myBooks.add(storedBook);
                    found = true;
                    break;
                }
            }
            if (!found){
                myBooks.add(new MyBook(book));
            }

        }
        sph.saveUserInfoBook("picks", myBooks);
        startActivity(myIntent);
    }
}