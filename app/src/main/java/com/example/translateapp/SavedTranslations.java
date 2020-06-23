package com.example.translateapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.TranslationResult;
import com.ibm.watson.language_translator.v3.util.Language;

import java.util.ArrayList;

public class SavedTranslations extends AppCompatActivity {              //4 private async tasks classes are implemented to get the translated word and insert them to respective tables in db

    ImageButton spanish;
    ImageButton french;
    ImageButton russian;
    ImageButton german;

    ArrayList<String > list1 = new ArrayList<>();
    DatabaseHelper translateDB;
    LanguageTranslator translationService;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_translations);

        spanish = findViewById(R.id.spain);
        french = findViewById(R.id.french);
        russian = findViewById(R.id.russian);
        german = findViewById(R.id.german);

        translationService = initLanguageTranslatorService();
        translateDB = DatabaseHelper.getInstance(this);

        if (isConnected()) {                    //add translated to database only if the phone is connected to internet
            System.out.println("Connected");
            Cursor cursorCheck = translateDB.retrieveData();                        //get the english words in db
            System.out.println("1");
            if (cursorCheck.getCount() == 0) {
                Toast.makeText(SavedTranslations.this, "No data", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog = new ProgressDialog(SavedTranslations.this);
                progressDialog.setMessage("Translations are adding...\nSelect a language after loading completes");     //make a progress dialog and finish it when asynctask finishes
                progressDialog.setTitle("Please wait...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                progressDialog.setCancelable(false);
            }
            //delete data in the language tables
            translateDB.deleteFrench();
            translateDB.deleteSpanish();
            translateDB.deleteRussian();
            translateDB.deleteGerman();

            spanish();
            french();
            russian();
            german();
            addFrench();
            addSpanish();
            addRussian();
            addGerman();
        }
        else {
            System.out.println("Not connected");
            Cursor cursorCheck = translateDB.retrieveData();                        //get the english words in db

            if (cursorCheck.getCount() == 0) {
                Toast.makeText(SavedTranslations.this, "No data", Toast.LENGTH_SHORT).show();
            }

            spanish();
            french();
            russian();
            german();

        }

    }

                                            //https://www.youtube.com/watch?v=VM0RR6zhYY0
    private boolean isConnected(){                      //check for internet connectivity and return true if internet connected and false if not connected
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeStatus = connectivityManager.getActiveNetworkInfo();
        return activeStatus != null && activeStatus.isConnected();
    }


    public LanguageTranslator initLanguageTranslatorService() {
                        //IBM authentication mechanism
        Authenticator authenticator = new IamAuthenticator("faYi7ZJg-U_HnnMMUHyhden716bUPeVDPRIa52tlUZBe");
        LanguageTranslator service = new LanguageTranslator("2018-05-01", authenticator);
                        //make a instance of the relevant service
        service.setServiceUrl("https://api.eu-gb.language-translator.watson.cloud.ibm.com/instances/dc78680e-4c13-4ab5-b8e4-a8a286f6f624");
        return service;
    }


    private void spanish(){
        spanish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SpanishWords.class);
                startActivity(intent);
            }
        });
    }


    private void french(){
        french.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),FrenchWords.class);
                startActivity(intent);
            }
        });
    }

    private void russian(){
        russian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RussianWords.class);
                startActivity(intent);
            }
        });
    }

    private void german(){
        german.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),GermanWords.class);
                startActivity(intent);
            }
        });
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void addFrench(){
        Cursor cursorF = translateDB.retrieveData();                        //get the english words in db
        System.out.println("1");
        if (cursorF.getCount() == 0){
            Toast.makeText(SavedTranslations.this, "No data", Toast.LENGTH_SHORT).show();
            return;
        }


        System.out.println("2");
        while (cursorF.moveToNext()) {

            list1.add(cursorF.getString(1));
        }

        System.out.println("inside add french- "+list1);

        for (int x=0; x<list1.size();x++){                              //get the english words individually and pass it into the async task to translate
            String engWord = list1.get(x);
            new TranslationFrench().execute(engWord);
        }
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void addSpanish(){
        Cursor cursorS = translateDB.retrieveData();                    //get the english words in db
        System.out.println("1");
        if (cursorS.getCount() == 0){
            Toast.makeText(SavedTranslations.this, "No data", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> list2 = new ArrayList<>();
        System.out.println("2");
        while (cursorS.moveToNext()) {

            list2.add(cursorS.getString(1));
        }


        for (int x=0; x<list1.size();x++){                              //get the english words individually and pass it into the async task to translate
            String engWord1 = list2.get(x);
            new TranslationSpanish().execute(engWord1);
        }

    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private void addRussian(){
        Cursor cursorS = translateDB.retrieveData();                        //get the english words in db
        System.out.println("1");
        if (cursorS.getCount() == 0){
            Toast.makeText(SavedTranslations.this, "No data", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> list2 = new ArrayList<>();
        System.out.println("2");
        while (cursorS.moveToNext()) {

            list2.add(cursorS.getString(1));
        }


        for (int x=0; x<list1.size();x++){                                  //get the english words individually and pass it into the async task to translate
            String engWord1 = list2.get(x);
            new TranslationRussian().execute(engWord1);
        }

    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private void addGerman(){
        Cursor cursorS = translateDB.retrieveData();                        //get the english words in db
        System.out.println("1");
        if (cursorS.getCount() == 0){
            Toast.makeText(SavedTranslations.this, "No data", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> list2 = new ArrayList<>();
        System.out.println("2");
        while (cursorS.moveToNext()) {

            list2.add(cursorS.getString(1));
        }

        for (int x=0; x<list1.size();x++){                              //get the english words individually and pass it into the async task to translate
            String engWord1 = list2.get(x);
            new TranslationGerman().execute(engWord1);
        }

    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private class TranslationFrench extends AsyncTask<String, Void, String> {
        String firstTranslation;

        @Override
        protected String doInBackground(String... params) {


                TranslateOptions translateOptions = new TranslateOptions.Builder()
                        .addText(params[0])
                        .source(Language.ENGLISH)
                        .target("fr")                   //set french as the target translation
                        .build();
                TranslationResult result = translationService.translate(translateOptions).execute().getResult();
                firstTranslation = result.getTranslations().get(0).getTranslation();

            return firstTranslation;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("tanslated french - "+s);        //translated word

            translateDB.translatedFrench(s);

        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private class TranslationSpanish extends AsyncTask<String, Void, String> {
        String firstTranslation;

        @Override
        protected String doInBackground(String... params) {


            TranslateOptions translateOptions = new TranslateOptions.Builder()
                    .addText(params[0])
                    .source(Language.ENGLISH)
                    .target("es")                   //set spanish as the target translation
                    .build();                   //build the service
            TranslationResult result = translationService.translate(translateOptions).execute().getResult();
            firstTranslation = result.getTranslations().get(0).getTranslation();

            return firstTranslation;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("tanslated spanish - "+s);

            translateDB.translatedSpanish(s);


        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private class TranslationRussian extends AsyncTask<String, Void, String> {
        String firstTranslation;

        @Override
        protected String doInBackground(String... params) {


            TranslateOptions translateOptions = new TranslateOptions.Builder()
                    .addText(params[0])
                    .source(Language.ENGLISH)
                    .target("ru")                           //set russian as the target translation
                    .build();
            TranslationResult result = translationService.translate(translateOptions).execute().getResult();
            firstTranslation = result.getTranslations().get(0).getTranslation();

            return firstTranslation;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("tanslated russian - "+s);

            translateDB.translatedRussian(s);


        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private class TranslationGerman extends AsyncTask<String, Void, String> {
        String firstTranslation;

        @Override
        protected String doInBackground(String... params) {


            TranslateOptions translateOptions = new TranslateOptions.Builder()
                    .addText(params[0])
                    .source(Language.ENGLISH)
                    .target("de")                               //set german as the target translation
                    .build();
            TranslationResult result = translationService.translate(translateOptions).execute().getResult();
            firstTranslation = result.getTranslations().get(0).getTranslation();

            return firstTranslation;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("tanslated german - "+s);

            translateDB.translatedGerman(s);


            progressDialog.dismiss();
        }

    }


}
