package com.example.translateapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.cloud.sdk.core.service.exception.BadRequestException;
import com.ibm.cloud.sdk.core.service.exception.NotFoundException;
import com.ibm.watson.language_translator.v3.LanguageTranslator;

import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.TranslationResult;
import com.ibm.watson.language_translator.v3.util.Language;
import com.ibm.cloud.sdk.core.http.HttpMediaType;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.text_to_speech.v1.model.SynthesizeOptions;
import java.util.Calendar;
import java.util.GregorianCalendar;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Translate extends AppCompatActivity {

    ListView listview;
    DatabaseHelper translateDB;
    String clicked;
    ArrayList<String> list = new ArrayList<>();
    Spinner mySpinner;
    TextView result;
    Button translate;
    Button pronounce;
    private LanguageTranslator translationService;
    ArrayList<String> subsTAG = new ArrayList<>();
    public String finalTag;
    private StreamPlayer player = new StreamPlayer();
    private TextToSpeech textService;
    int checkClicked;
    int checkTranslated;
    Boolean spinnerCheck;
    Dialog dialog;
    ImageButton bad;
    ImageButton average;
    ImageButton good;
    int newFlag = 0;
    TextView close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);


        listview = findViewById(R.id.wordView);
        mySpinner = findViewById(R.id.mySpinner);
        result = findViewById(R.id.translated);
        translate = findViewById(R.id.translate);
        pronounce = findViewById(R.id.pronounce);
        translationService = initLanguageTranslatorService();
        textService = initTextToSpeechService();


        translateDB = DatabaseHelper.getInstance(this);

        showData();
        spinnerList();

    }

                                    //https://www.youtube.com/watch?v=VM0RR6zhYY0
    private boolean isConnected(){                      //check for internet connectivity and return true if internet connected and false if not connected
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeStatus = connectivityManager.getActiveNetworkInfo();
        return activeStatus != null && activeStatus.isConnected();
    }


    private LanguageTranslator initLanguageTranslatorService() {
                            //IBM authentication mechanism
        Authenticator authenticator = new IamAuthenticator("faYi7ZJg-U_HnnMMUHyhden716bUPeVDPRIa52tlUZBe");
        LanguageTranslator service = new LanguageTranslator("2018-05-01", authenticator);
                            //make a instance of the relevant service
        service.setServiceUrl("https://api.eu-gb.language-translator.watson.cloud.ibm.com/instances/dc78680e-4c13-4ab5-b8e4-a8a286f6f624");
        return service;
    }

    private TextToSpeech initTextToSpeechService() {
        //IBM authentication mechanism
        Authenticator authenticator = new IamAuthenticator("eDCCL2iGHgA42U2QH4kqiVgYdUpT6iKvVNKkYylWZyCb");
        TextToSpeech service = new TextToSpeech(authenticator);
        //make a instance of the relevant service
        service.setServiceUrl("https://api.eu-gb.text-to-speech.watson.cloud.ibm.com/instances/043b0c58-2ceb-4352-970e-a823a60a0cd4");
        return service;
    }


    public void showData(){                                         //show english word in a list view with single choice available
        final Cursor cursor = translateDB.retrieveData();
        System.out.println("1");
        if (cursor.getCount() == 0){
            Toast.makeText(Translate.this, "No data", Toast.LENGTH_SHORT).show();
            return;
        }

        System.out.println("2");
        while (cursor.moveToNext()) {
            list.add(cursor.getString(1));
        }

        viewList();
    }


    public void viewList(){

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_single_choice,list);
        arrayAdapter.sort(new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });

        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listview.setAdapter(arrayAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                checkClicked = 1;       //flag variable
                clicked = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(Translate.this,"You selected '"+ clicked +"' " , Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void spinnerList(){                              //get the subscribed languages to a spinner

        Cursor cursorNw = translateDB.retrivePosition();                //get the subscribed languages data

        ArrayList<String> lang = new ArrayList<>();
        ArrayList<String> status = new ArrayList<>();
        while (cursorNw.moveToNext()) {
            status.add(cursorNw.getString(2));              //get checked or not (true or false)
            lang.add(cursorNw.getString(3));                //get the subscribed language and add it into array
        }


        final ArrayList<String> finalItem = new ArrayList<>();
        System.out.println(lang);
        for (int x=0; x<lang.size();x++){
            String langItem = lang.get(x);
            String stat = status.get(x);

            if (stat.equals("true")) {                      // if the status is true --> language has been subscribed ,
                if (!finalItem.contains(langItem)) {        //avoid adding the language if it's already in the list
                    finalItem.add(langItem);                    // so add the languages to an array to set the spinner with subscribed languages
                    System.out.println(langItem);
                }
            }
            else if (stat.equals("false")){
                                                        // if the status is false --> language has been unsubscribed at the final attempt,
                finalItem.remove(langItem);             //so remove those languages
                System.out.println(langItem);
            }
        }
        System.out.println("array--"+finalItem);




        /*final ArrayList<String> newFinal = new ArrayList<>();
        newFinal.clear();
        System.out.println("array---"+newFinal);
        for (String element : finalItem){   //remove if any language has a duplicate entry and
            if (!newFinal.contains(element)){ // create an array with no duplicate entries
                newFinal.add(element);
            }
        }*/


                                                    //set the spinner with subscribed languages
        final ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(Translate.this, android.R.layout.simple_list_item_1, finalItem );
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);


        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isConnected()) {                //execute translation only if the phone is connected to internet

                    if (checkClicked != 1 | finalItem.isEmpty()) {         //error handle if button is clicked without selecting a word or a language
                        Toast.makeText(Translate.this, "Select a word and a language", Toast.LENGTH_SHORT).show();
                    } else {
                        String selectLang = mySpinner.getSelectedItem().toString();
                        System.out.println(selectLang);             //get the selected language from spinner

                        Cursor cursor = translateDB.retrivePosition();

                        ArrayList<String> checkTag = new ArrayList<>();
                        while (cursor.moveToNext()) {
                            subsTAG.add(cursor.getString(4));           //insert the tag of all languages to the array
                            checkTag.add(cursor.getString(3));          //insert the subscribed languages to the array
                        }

                        for (int p = 0; p < subsTAG.size(); p++) {

                            if (selectLang.equals(checkTag.get(p))) {           //get the p value which equals with the selected language from spinner
                                finalTag = subsTAG.get(p);                      // get the 'p' position tag which is relevant to the selected language from spinner
                                System.out.println("===== " + finalTag);
                                break;
                            }
                            System.out.println("//////////////////");
                        }
                        checkTranslated = 1;          //flag variable

                        new TranslationTask().execute(clicked);         //passing selected word to async task to translate

                    }
                }
                else {
                    Toast.makeText(Translate.this,"You're not connected to internet\nCheck you network connection",Toast.LENGTH_LONG).show();
                }
            }
        });


        pronounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isConnected()) {            //execute pronunciation only if the phone is connected to internet

                    if (checkTranslated == 1) {               //error handle if button is pressed before translating the word
                        new SynthesisTask().execute(result.getText().toString());
                    } else {
                        Toast.makeText(Translate.this, "Translate the word first", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(Translate.this,"You're not connected to internet\nCheck you network connection",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void rateApp(){                              //rate the app

        System.out.println("rand");
        Random random = new Random();
        int rateNum = random.nextInt(10);            //creating the rating option random
        System.out.println(rateNum);
        if (rateNum==1){
            System.out.println("in");
            AlertDialog.Builder builder = new AlertDialog.Builder(Translate.this);          //creating an alert dialog builder
            builder.setTitle("Translately")
                    .setMessage("Can you please take a moment to rate us...")
                    .setPositiveButton("Rate Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showRate();                                     //execute method to show the dialog box with rating
                            System.out.println("Rate Now");
                        }
                    })
                    .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }                               //close alert box
                    });
            builder.create();
            builder.show();
        }
    }


    public void showRate(){                 //rate dialog box
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.rate_dialog);
        bad = dialog.findViewById(R.id.bad);
        average = dialog.findViewById(R.id.average);
        good = dialog.findViewById(R.id.good);
        close = dialog.findViewById(R.id.close);

        dialog.show();
        bad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {               //bad rating
                Toast.makeText(Translate.this,"Oh...Sorry to hear that \nWe'll improve in near future",Toast.LENGTH_SHORT).show();
                newFlag = 1;                                    //flag variable to avoid asking for rating again
                dialog.dismiss();
            }
        });

        average.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Translate.this,"Thank you for your feedback ",Toast.LENGTH_SHORT).show();
                newFlag = 1;
                dialog.dismiss();
            }
        });

        good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Translate.this,"Wow...Thank you for your valuable response !!!",Toast.LENGTH_SHORT).show();
                newFlag = 1;
                dialog.dismiss();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private class TranslationTask extends AsyncTask<String, Void, String> {

        String firstTranslation;
        public int chngColor;

        @Override
        protected String doInBackground(String... params) {

                try {                       //solve the error of language not found
                    TranslateOptions translateOptions = new
                            TranslateOptions.Builder()
                            .addText(params[0])
                            .source(Language.ENGLISH)
                            .target(finalTag)           //selected tag
                            .build();
                    System.out.println("********** "+ finalTag);

                    TranslationResult result = translationService.translate(translateOptions).execute().getResult();
                    firstTranslation = result.getTranslations().get(0).getTranslation();
                    System.out.println(firstTranslation);

                } catch (NotFoundException e){                                                      //https://www.codota.com/code/java/classes/javassist.NotFoundException
                    System.out.println("Error: " + e.getStatusCode() + " - "+ e.getMessage());          //print the error message
                    firstTranslation = "Error: " + e.getStatusCode() + " - "+ e.getMessage();
                    chngColor = 1;      //flag

                } catch (BadRequestException e){
                    System.out.println("Error: " + e.getStatusCode() + " - "+ e.getMessage());          //print the error message
                    firstTranslation = "Error: " + e.getStatusCode() + " - "+ e.getMessage();
                    chngColor = 1;      //flag
                }

                return firstTranslation;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            result.setText(s);
            if (chngColor==1){          //error message
                result.setText(s);
                result.setTextColor(Color.parseColor("#e60000"));       //error message in red
            }
            else {
                result.setText(s);
                result.setTextColor(Color.parseColor("#0000ff"));       //translated word in blue
            }
            if (newFlag!=1) {               //check if already a rating is taken by the user
                rateApp();
            }
        }
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private class SynthesisTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            SynthesizeOptions synthesizeOptions = new SynthesizeOptions.Builder()
                    .text(params[0])
                    .voice(SynthesizeOptions.Voice.EN_US_LISAVOICE)
                    .accept(HttpMediaType.AUDIO_WAV)
                    .build();
            player.playStream(textService.synthesize(synthesizeOptions).execute().getResult());         //pronounce the translated word
            return "Did synthesize";
        }
    }

}
