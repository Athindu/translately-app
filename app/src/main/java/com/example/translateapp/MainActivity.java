package com.example.translateapp;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.IdentifiableLanguage;
import com.ibm.watson.language_translator.v3.model.IdentifiableLanguages;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {               //private async class is implemented which inserts all the languages to the db when main activity opens


    CardView add;
    CardView display;
    CardView edit;
    CardView languages;
    CardView translate;
    CardView savedTranslated;
    CardView delete;
    CollapsingToolbarLayout collapsingToolbarLayout;
    DatabaseHelper translateDB;
    ProgressDialog progressDialog;
    Button info;
    Dialog dialog;
    TextView close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >=21){            //changing status bar by this way can be only done in higher sdk phones
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);          //https://www.youtube.com/watch?v=GYGUQYQNt2U
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));       //changing the color of the status bar
        }


        add = findViewById(R.id.addPhrase);
        display = findViewById(R.id.viewPhrase);
        edit = findViewById(R.id.editPhrase);
        languages = findViewById(R.id.languages);
        translate = findViewById(R.id.translate);
        savedTranslated = findViewById(R.id.saveTranslations);
        delete = findViewById(R.id.delete);
        info = findViewById(R.id.info);
        collapsingToolbarLayout = findViewById(R.id.collapseBar);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/croissant_one.ttf");
        Typeface tf2 = Typeface.createFromAsset(getAssets(), "fonts/croissant_one.ttf");
        collapsingToolbarLayout.setExpandedTitleTypeface(tf);
        collapsingToolbarLayout.setCollapsedTitleTypeface(tf2);

        translateDB = DatabaseHelper.getInstance(this);         //making the instance of database helper at this given instance

        addPhrase();
        displayPhrase();
        editPhrase();
        subscribe();
        translate();
        savedTrans();
        delete();
        info();

        if (isConnected()) {                    //add languages only if the phone is connected to internet

            BackgroundTask task = new BackgroundTask();
            final Cursor cursor = translateDB.retriveLang();
            if (cursor.getCount() == 0) {                                            //check if anything is in the table and if not enter the languages
                progressDialog = new ProgressDialog(MainActivity.this);      //by this it avoids adding languages everytime when the activity opens
                progressDialog.setMessage("Languages are adding...");
                progressDialog.setTitle("Please wait...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);          //progress dialog to show the user and will hide the dialog when aync task finishes
                progressDialog.show();
                progressDialog.setCancelable(false);

                task.execute();             //async task
            }
        }
        else{
            Toast.makeText(MainActivity.this,"Unable to add languages !!! \nNo internet Connection",Toast.LENGTH_LONG).show();
        }


    }

                                    //https://www.youtube.com/watch?v=VM0RR6zhYY0
    private boolean isConnected(){                      //check for internet connectivity and return true if internet connected and false if not connected
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeStatus = connectivityManager.getActiveNetworkInfo();
        return activeStatus != null && activeStatus.isConnected();
    }

    @Override
    public void onBackPressed() {               //https://stackoverflow.com/questions/6413700/android-proper-way-to-use-onbackpressed-with-toast
                                    //alert dialog box which appear when user tries to exit from the application
        System.out.println("back");
        AlertDialog.Builder exitB = new AlertDialog.Builder(MainActivity.this);
        exitB.setMessage("Ara you sure you want to exit ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {        //exit the app
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();                            //close the alert dialog
                    }
                });
        exitB.create();
        exitB.show();

    }

    private void addPhrase(){
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("ADD PHRASE");
                Intent intent = new Intent(getApplicationContext(),AddPhrase.class);
                startActivity(intent);
            }
        });
    }


    private void displayPhrase(){
        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("DISPLAY PHRASES");
                Intent intent = new Intent(getApplicationContext(),DisplayPhrase.class);
                startActivity(intent);
            }
        });
    }


    private void editPhrase(){
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("EDIT PHRASE");
                Intent intent = new Intent(getApplicationContext(),EditPhrase.class);
                startActivity(intent);
            }
        });
    }


    private void subscribe(){
        languages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("SUBSCRIBE");
                Intent intent = new Intent(getApplicationContext(),Subscribe.class);
                startActivity(intent);
            }
        });
    }


    private void translate(){
        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("TRANSLATE");
                Intent intent = new Intent(getApplicationContext(),Translate.class);
                startActivity(intent);
            }
        });
    }

    private void savedTrans(){
        savedTranslated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("TRANSLATIONS SAVED");
                Intent intent = new Intent(getApplicationContext(),SavedTranslations.class);
                startActivity(intent);
            }
        });
    }

    private void delete(){
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Delete.class);
                startActivity(intent);
            }
        });
    }


    private void info(){                                //details about the app, version etc
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Info");
                dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.app_details);
                close = dialog.findViewById(R.id.close);                            //https://stackoverflow.com/questions/10795078/dialog-with-transparent-background-in-android
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));     //make the background of the activity transparent
                dialog.show();

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();           //close the dialog box when X is clicked
                    }
                });
            }
        });
    }

    private class BackgroundTask extends AsyncTask {                //https://www.tutorialspoint.com/android-asynctask-example-and-explanation

        private List<IdentifiableLanguage> languagesArray = new ArrayList<>();          //list of languages

        @Override
        protected Object doInBackground(Object[] objects) {                         //https://ibm.github.io/dotnet-sdk-core/docs/master/html/d1/dfd/class_i_b_m_1_1_cloud_1_1_s_d_k_1_1_core_1_1_authentication_1_1_iam_1_1_iam_authenticator
                                    //used from IBM authentication mechanism
            IamAuthenticator authenticator = new IamAuthenticator("faYi7ZJg-U_HnnMMUHyhden716bUPeVDPRIa52tlUZBe");
            System.out.println("first");                //make a instance of the relevant service
            LanguageTranslator languageTranslator = new LanguageTranslator("2018-05-01", authenticator);
            System.out.println("second");
            languageTranslator.setServiceUrl("https://api.eu-gb.language-translator.watson.cloud.ibm.com/instances/dc78680e-4c13-4ab5-b8e4-a8a286f6f624");
            System.out.println("third");
            IdentifiableLanguages languages = languageTranslator.listIdentifiableLanguages().execute().getResult();
                                //get the languages and insert it into an arraylst
            languagesArray = languages.getLanguages();
            System.out.println("array - "+languagesArray);
            return languagesArray;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            for (int i = 0; i<languagesArray.size();i++){
                int size = languagesArray.size();
                System.out.println("Array size " +size);
                IdentifiableLanguage languageSelect = languagesArray.get(i);        //loop through the languagesArray and get the languages individually
                String language = languageSelect.getName();
                String langTAG = languageSelect.getLanguage();          //get the language tag and language separately to two strings
                translateDB.enterLang(language,langTAG);
                System.out.println( language );

            }
            progressDialog.dismiss();           //stop the progress dialog as all the languages are added when async task finishes
        }
    }
}
