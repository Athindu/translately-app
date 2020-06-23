package com.example.translateapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class AddPhrase extends AppCompatActivity {

    DatabaseHelper translateDB;
    EditText editPhrase;
    Button saveBtn;
    SQLiteDatabase database;
    boolean isInserted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phrase);

        translateDB = DatabaseHelper.getInstance(this);
        editPhrase = findViewById(R.id.saveText);
        saveBtn = findViewById(R.id.saveBtn);



        saveData();

    }


    public void saveData(){
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                database = translateDB.getWritableDatabase();
                String phraseNW = editPhrase.getText().toString();              //get the word in the textbox

                if (phraseNW.length() == 0){                                //check if any word is entered to edit text
                    Toast.makeText(AddPhrase.this,"Enter a phrase and then save...",Toast.LENGTH_LONG).show();
                }
                else {

                    Cursor cursor = translateDB.retrieveData();
                    ArrayList<String > list = new ArrayList<>();            //retrieve the words from db and check if the word is already entered
                    System.out.println("2");                                //and print a toast or add to db accordingly
                    while (cursor.moveToNext()) {
                        System.out.println("3");
                        list.add(cursor.getString(1));
                    }

                    if (list.contains(phraseNW)){
                        Toast.makeText(AddPhrase.this, "Data insertion is unsuccessful, Duplicate data entry", Toast.LENGTH_LONG).show();
                    }

                    else {

                        isInserted = translateDB.insertData(phraseNW);
                        if (isInserted == true)
                            Toast.makeText(AddPhrase.this, "Data recorded successfully...", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(AddPhrase.this, "Data insertion is unsuccessful...", Toast.LENGTH_LONG).show();
                    }
                }
                editPhrase.setText("");
            }
        });
    }




}
