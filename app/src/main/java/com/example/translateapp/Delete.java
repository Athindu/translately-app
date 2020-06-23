package com.example.translateapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;

public class Delete extends AppCompatActivity {

    ListView listview;
    DatabaseHelper translateDB;
    Button delete;
    String clicked;
    ArrayList<String> list = new ArrayList<>();
    int clickedVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        listview = findViewById(R.id.phraseView);
        delete = findViewById(R.id.deleteBtn);
        translateDB = DatabaseHelper.getInstance(this);


        showData();
        delete();

    }


    public void showData(){                                                         //retrieve the words from database and inserting words to an arraylist
        final Cursor cursor = translateDB.retrieveData();
        System.out.println("1st step");
        if (cursor.getCount() == 0){
            Toast.makeText(Delete.this, "No data", Toast.LENGTH_LONG).show();
            return;
        }

        System.out.println("2");
        while (cursor.moveToNext()) {
            list.add(cursor.getString(1));
        }

        viewList();
    }


    public void viewList(){
                                                                //using array adapter to make the view as a list and make the list view selectable only once
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_single_choice,list);
        arrayAdapter.sort(new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {                //printing the words in alphabetical order
                return lhs.compareTo(rhs);
            }
        });

        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listview.setAdapter(arrayAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                clicked = adapterView.getItemAtPosition(i).toString();                              //storing selected word in a string
                Toast.makeText(Delete.this,"You selected '"+ clicked+"' " , Toast.LENGTH_SHORT).show();

                clickedVal=1;           //flag variable
            }
        });
    }


    public void delete(){

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickedVal!=1){                         //check if any item is selected to delete before clicking button
                    Toast.makeText(Delete.this,"Select a word then delete",Toast.LENGTH_SHORT).show();
                }else {
                    Integer delRows = translateDB.deleteData(clicked);//pass the selected word to delete from db
                    System.out.println(delRows);
                    if (delRows > 0) {                          //check if item is deleted by the return value from delete method in database helper
                        list.remove(clicked);                   //removing the deleted item from the list and call the method to remove the word from listview instantly
                        viewList();
                        Toast.makeText(Delete.this, "Data is deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Delete.this, "Data is not deleted", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

}
