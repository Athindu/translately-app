package com.example.translateapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;

public class DisplayPhrase extends AppCompatActivity {

    ListView listview;
    DatabaseHelper translateDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_phrase);

        listview = findViewById(R.id.phraseView);
        translateDB = DatabaseHelper.getInstance(this);

        showData();
    }

    public void showData(){
        Cursor cursor = translateDB.retrieveData();
        System.out.println("1");
        if (cursor.getCount() == 0){                    //check if the database have any data
            Toast.makeText(DisplayPhrase.this, "No data", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String > list = new ArrayList<>();            //inserting the data from db to arraylist and print the listview using adapter
        System.out.println("2");
        while (cursor.moveToNext()) {
            System.out.println("3");
            list.add(cursor.getString(1));
        }

        System.out.println(list);       //print the word list

        ArrayAdapter adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,list);
        adapter.sort(new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return lhs.compareTo(rhs);
            }
        });
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String clicked = adapterView.getItemAtPosition(i).toString();               //checking for the clicked item in the list view
                Toast.makeText(DisplayPhrase.this,"You clicked on '"+ clicked+"' " , Toast.LENGTH_SHORT).show();
            }
        });

    }


}
