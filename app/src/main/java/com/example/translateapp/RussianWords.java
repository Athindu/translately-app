package com.example.translateapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.language_translator.v3.LanguageTranslator;

import java.util.ArrayList;

public class RussianWords extends AppCompatActivity {

    ListView englishist;
    ListView russianList;
    DatabaseHelper translateDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_russian_words);

        englishist = findViewById(R.id.englishList);
        russianList = findViewById(R.id.list);
        translateDB = DatabaseHelper.getInstance(this);
        showEnglish();

        showRussian();
    }



    public void showEnglish(){                                              //show the english words in a listview
        Cursor cursor = translateDB.retrieveData();
        System.out.println("1");
        if (cursor.getCount() == 0){
            Toast.makeText(RussianWords.this, "No data", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String > list = new ArrayList<>();
        System.out.println("2");
        while (cursor.moveToNext()) {
            System.out.println("3");
            list.add(cursor.getString(1));
        }

        System.out.println(list);

        ArrayAdapter adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,list);
        englishist.setAdapter(adapter);
    }



    private void showRussian(){                                                 //show the translated words in a listview
        Cursor cursor = translateDB.retrieveRussian();
        System.out.println("4");
        if (cursor.getCount() == 0){
            Toast.makeText(RussianWords.this, "No data", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String > list2 = new ArrayList<>();
        System.out.println("5");
        while (cursor.moveToNext()) {
            System.out.println("6");
            list2.add(cursor.getString(1));
        }


        final ArrayList<String> newFinal = new ArrayList<>();
        for (String element : list2){                                               //avoid inserting duplicate values to the listview
            if (!newFinal.contains(element)){
                newFinal.add(element);
            }
        }


        ArrayAdapter adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,newFinal);
        russianList.setAdapter(adapter);
    }
}
