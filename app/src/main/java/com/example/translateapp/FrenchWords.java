package com.example.translateapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class FrenchWords extends AppCompatActivity {


    ListView englishist;
    ListView frenchList;
    DatabaseHelper translateDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_french_words);

        englishist = findViewById(R.id.englishList);
        frenchList = findViewById(R.id.frenchList);
        translateDB = DatabaseHelper.getInstance(this);
        showEnglish();

        showFrench();
    }




    public void showEnglish(){                                          //show the english words in a listview
        Cursor cursor = translateDB.retrieveData();
        System.out.println("1");
        if (cursor.getCount() == 0){
            Toast.makeText(FrenchWords.this, "No data", Toast.LENGTH_SHORT).show();
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



    private void showFrench(){                                  //show the translated words in a listview
        Cursor cursor = translateDB.retrieveFrench();
        System.out.println("4");
        if (cursor.getCount() == 0){
            Toast.makeText(FrenchWords.this, "No data", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String > list2 = new ArrayList<>();
        System.out.println("5");
        while (cursor.moveToNext()) {
            System.out.println("6");
            list2.add(cursor.getString(1));
        }

        System.out.println("french list == "+list2);

        final ArrayList<String> newFinal = new ArrayList<>();
        for (String element : list2){                                       //avoid inserting duplicate values to the listview
            if (!newFinal.contains(element)){
                newFinal.add(element);
            }
        }


        ArrayAdapter adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,newFinal);
        frenchList.setAdapter(adapter);
    }

}
