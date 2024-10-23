package com.example.readinghub;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import org.intellij.lang.annotations.Language;

import java.util.Vector;

public class Dashboard extends AppCompatActivity {
    SharedPreferencesHelper sph;
    Vector<MyBook> myBooks;
    TextView tVUserName, tVReadingProgram;
    LinearLayout layoutMyBooks;
    String PROGRAM_NAME = "programName";
    private Calendar selectedDate;
    private String formattedDate;
//    Vector<MyBook> myBooks = new Vector<MyBook>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sph = new SharedPreferencesHelper(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle FAB click
               Intent intent =new Intent(getApplicationContext(),MainActivity.class);
               startActivity(intent);
            }
        });

        if (sph.GetUserInfoString("picks").equals("")){
            Intent intent = new Intent(getApplicationContext(),WelcomePage.class);
            startActivity(intent);
        }
        else{
        tVUserName = findViewById(R.id.tVUserName);
        layoutMyBooks = findViewById(R.id.layoutMyBooks);
        tVReadingProgram = findViewById(R.id.tVReadingProgram);

        String name = sph.GetUserInfoString("userName");
        tVUserName.setText("Welcome "+name +" \uD83D\uDC4B");
        tVReadingProgram.setText( "\uD83D\uDCDA " + sph.GetUserInfoString(PROGRAM_NAME) + "\uD83D\uDCDA");

        layoutMyBooks.removeAllViews();
        //Tests
//        MyBook myBook1 = new MyBook("title1", "author1", "2000","300", "", "08/06/2004", "0");
//        MyBook myBook2= new MyBook("title2", "author2", "2000","300", "", "08/06/2004", "100");
//        MyBook myBook3 = new MyBook("title3", "author3", "2000","300", "", "08/06/2004", "200");
//
//        myBooks.add(myBook1);
//        myBooks.add(myBook2);
//        myBooks.add(myBook3);

        myBooks = sph.GetUserInfoBook("picks");
//        Log.i("myApp", String.valueOf(myBooks.get(0).getAuthor()));

        renderMyBooks();}

    }

    public void renderMyBooks(){
        for (int i = 0; i < myBooks.size(); i++){
            Context context = getApplicationContext();
            MyBook myBook = myBooks.get(i);

            ContextThemeWrapper layoutMyBookContext = new ContextThemeWrapper(context, R.style.layoutMyBook);
            LinearLayout layoutMyBook = new LinearLayout(layoutMyBookContext);

            layoutMyBooks.addView(layoutMyBook);

            ImageView iVCover = new ImageView(context);

            //Test
            //iVCover.setImageResource(R.drawable.book_solid_48);
            String coverImageUrl = myBook.getCover();

            if (!coverImageUrl.isEmpty()) {
                Glide.with(context)
                        .load(coverImageUrl)
                        .into(iVCover);
            }
            else{iVCover.setImageResource(R.drawable.book_solid_48);}

            layoutMyBook.addView(iVCover);

            ContextThemeWrapper layoutTitleDeadlineContext = new ContextThemeWrapper(context, R.style.layoutTitleDeadline);
            LinearLayout layoutTitleDeadline = new LinearLayout(layoutTitleDeadlineContext);

            layoutMyBook.addView(layoutTitleDeadline);

            ContextThemeWrapper tVTitleContext = new ContextThemeWrapper(context, R.style.bookTitle);
            TextView tVTitle = new TextView(tVTitleContext);
            tVTitle.setText(myBook.getTitle());
            layoutTitleDeadline.addView(tVTitle);

            ContextThemeWrapper tVDeadlineContext = new ContextThemeWrapper(context, R.style.bookData);
            TextView tVDeadline = new TextView(tVDeadlineContext);
            tVDeadline.setText("Finish by : " + myBook.getDeadline());
            layoutTitleDeadline.addView(tVDeadline);

            LinearLayout layoutProgress = new LinearLayout(context);
            layoutProgress.setOrientation(LinearLayout.VERTICAL);
            layoutProgress.setGravity(Gravity.CENTER);
            layoutMyBook.addView(layoutProgress);

            ContextThemeWrapper pBContext = new ContextThemeWrapper(context, R.style.progressBar);
            ProgressBar pB = new ProgressBar(pBContext, null, android.R.attr.progressBarStyleHorizontal);

            int pagesRead = Integer.parseInt(myBook.getPagesRead());
            int pgCount = (!myBook.getPgCount().equals("")) ? Integer.parseInt(myBook.getPgCount()) : 100;
            pB.setProgress(pagesRead);
            pB.setMax(pgCount);

            TextView tVProgress = new TextView(tVDeadlineContext);
            tVProgress.setText(String.valueOf(pagesRead) + " / " + String.valueOf(pgCount) + "\npages read" );

            layoutProgress.addView(tVProgress);
            layoutProgress.addView(pB);

            ContextThemeWrapper layoutEditProgressContext = new ContextThemeWrapper(context, R.style.layoutEditProgress);
            LinearLayout layoutEditProgress = new LinearLayout(layoutEditProgressContext);
            layoutMyBook.addView(layoutEditProgress);

            ContextThemeWrapper btnIncrementDecrementContext = new ContextThemeWrapper(this, R.style.btnIncrementDecrementProgress);
            Button btnIncrement = new MaterialButton(btnIncrementDecrementContext);
            btnIncrement.setText("+");
            layoutEditProgress.addView(btnIncrement);

            Button btnDecrement = new MaterialButton(btnIncrementDecrementContext);
            btnDecrement.setText("-");
            layoutEditProgress.addView(btnDecrement);

            // Get the WindowManager service
            WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//
            // Get the current display
            Display display = windowManager.getDefaultDisplay();

            // Create a DisplayMetrics object
            DisplayMetrics displayMetrics = new DisplayMetrics();

            // Get the display metrics
            display.getMetrics(displayMetrics);

            // Get the screen width in pixels
            int width = displayMetrics.widthPixels;

            LinearLayout.LayoutParams layoutEditProgresstLayoutParams = new LinearLayout.LayoutParams(width*2/14, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutEditProgress.setLayoutParams(layoutEditProgresstLayoutParams);

            LinearLayout.LayoutParams layoutProgressParams = new LinearLayout.LayoutParams(width*4/14, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutProgress.setLayoutParams(layoutProgressParams);


            LinearLayout.LayoutParams btnLayoutParams = new LinearLayout.LayoutParams(width*2/14/3,100);
            btnLayoutParams.setMargins(5,0,5,0);
            btnIncrement.setLayoutParams(btnLayoutParams);
            btnDecrement.setLayoutParams(btnLayoutParams);

            LinearLayout.LayoutParams pBLayoutParams = new LinearLayout.LayoutParams(width*4/14, 25);
            pBLayoutParams.setMargins(5,0,5,0);
            pB.setLayoutParams(pBLayoutParams);

            LinearLayout.LayoutParams layoutTitleDeadlineParams = new LinearLayout.LayoutParams(width*6/14, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutTitleDeadline.setLayoutParams(layoutTitleDeadlineParams);
            layoutTitleDeadline.setPadding(5,5,5,5);

            LinearLayout.LayoutParams titleDeadlineParams = new LinearLayout.LayoutParams(width*6/14, ViewGroup.LayoutParams.WRAP_CONTENT);
            tVTitle.setLayoutParams(titleDeadlineParams);
            tVDeadline.setLayoutParams(titleDeadlineParams);

            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                    width*2/14,200
            );

            iVCover.setLayoutParams(imageParams);

            ContextThemeWrapper layoutBrContext = new ContextThemeWrapper(this, R.style.splitter);
            LinearLayout br = new LinearLayout(layoutBrContext);
            LinearLayout.LayoutParams brLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,  // Set a fixed width
                    2);
            brLayoutParams.setMargins(20,0,20,0);
            br.setLayoutParams(brLayoutParams);
            layoutMyBooks.addView(br);

            btnIncrement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pagesRead = Integer.parseInt(myBook.getPagesRead());
                    if (pagesRead > pgCount - 10){
                        pagesRead = pgCount;
                    } else{
                        pagesRead +=10;
                    }
                    myBook.setPagesRead(String.valueOf(pagesRead));
                    sph.saveUserInfoBook("picks", myBooks);
                    pB.setProgress(pagesRead);
                    tVProgress.setText(String.valueOf(pagesRead) + " / " + String.valueOf(pgCount) + "\npages read" );

                    Toast.makeText(context, "Good Job! \uD83E\uDD13 \uD83D\uDCAA", Toast.LENGTH_SHORT).show();
                }
            });

            btnDecrement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pagesRead = Integer.parseInt(myBook.getPagesRead());
                    if (pagesRead < 10){
                        pagesRead = 0;
                    } else{
                        pagesRead -=10;
                    }

                    myBook.setPagesRead(String.valueOf(pagesRead));
                    sph.saveUserInfoBook("picks", myBooks);
                    pB.setProgress(pagesRead);
                    tVProgress.setText(String.valueOf(pagesRead) + " / " + String.valueOf(pgCount) + "\npages read" );
                }
            });

            tVDeadline.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    showCalendarDialog(tVDeadline,myBook);

                }
            });

        }



    }

    private void showCalendarDialog(TextView textView,MyBook myBook) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_calendar);

        CalendarView calendarView = dialog.findViewById(R.id.calendarView);
        Button btnSetDate = dialog.findViewById(R.id.btnSetDate);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);
            }
        });

        btnSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDate != null) {
                    String formattedDate = formatDate(selectedDate);
                    textView.setText("Finished by: " + formattedDate);
                    myBook.setDeadline(formattedDate);
                    sph.saveUserInfoBook("picks",myBooks);

                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    private void openCalendarEvent(Calendar date) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, date.getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, date.getTimeInMillis() + 60 * 60 * 1000); // +1 hour
        intent.putExtra(CalendarContract.Events.TITLE, "Deadline");
        intent.putExtra(CalendarContract.Events.DESCRIPTION, "Set your deadline");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    private String formatDate(Calendar date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(date.getTime());
    }

}