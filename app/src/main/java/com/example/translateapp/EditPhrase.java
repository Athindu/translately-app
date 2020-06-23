package com.example.translateapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;

public class EditPhrase extends AppCompatActivity {

    ListView listview;
    DatabaseHelper translateDB;
    EditText editText;
    Button save;
    Button edit;
    int itemID;
    String clicked;
    int clickedVal;     //flag variable
    ArrayList<String> list = new ArrayList<>();
    int selectedCheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phrase);

        listview = findViewById(R.id.phraseView);
        editText = findViewById(R.id.saveText);

        save = findViewById(R.id.saveBtn);
        edit = findViewById(R.id.editBtn);
        translateDB = DatabaseHelper.getInstance(this);

        showData();
        editWord();
        saveWord();
    }


    public void showData(){                                                                     //retrieve the words from database and inserting words to an arraylist
        final Cursor cursor = translateDB.retrieveData();
        System.out.println("1");
        if (cursor.getCount() == 0){
            Toast.makeText(EditPhrase.this, "No data", Toast.LENGTH_LONG).show();
            return;
        }

        System.out.println("2");
        while (cursor.moveToNext()) {                       //inserting the phrases to a list
            list.add(cursor.getString(1));
        }

        viewList();
    }


    public void viewList(){
                                                            //using array adapter to make the view as a list and make the list view selectable only once
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_single_choice,list);
        arrayAdapter.sort(new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {                            //printing the words in alphabetical order
                return lhs.compareTo(rhs);
            }
        });

        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listview.setAdapter(arrayAdapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                clicked = adapterView.getItemAtPosition(i).toString();                  //storing selected word in a string
                Toast.makeText(EditPhrase.this,"You selected '"+ clicked+"' " , Toast.LENGTH_LONG).show();

                selectedCheck =1;           //flag
                System.out.println(clicked);
                if (clickedVal==1){                 //setting the text in edittext the clicked word after the edit button is clicked
                    editText.setText(clicked);
                }
            }
        });
    }



    public void editWord(){
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedCheck!=1){          //check if any word is selected
                    Toast.makeText(EditPhrase.this,"Select a word",Toast.LENGTH_SHORT).show();
                }
                else {
                    clickedVal = 1;                 //flag variable to check if the edit button is clicked
                    editText.setText(clicked);
                }
            }
        });
    }

    public void saveWord(){

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor selectW = translateDB.getItemID(clicked);            //getting the id or the position of the clicked item

                while (selectW.moveToNext()){
                    itemID = selectW.getInt(0);             //inserting the selected position to a variable
                    System.out.println(itemID);
                }

                String item = editText.getText().toString();            //get the new word
                System.out.println("New word --- "+item);
                if (!item.equals("")){              //check if the new word is not null

                    translateDB.updateData(item, itemID,clicked);           //pass the data to db to update
                    list.remove(clicked);
                    list.add(item);                                 //update the listview instantly
                    viewList();
                    Toast.makeText(EditPhrase.this,"Saved successfully ...",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(EditPhrase.this,"Save FAILED !!!",Toast.LENGTH_LONG).show();
                }
                editText.setText("");
            }
        });
    }
}
