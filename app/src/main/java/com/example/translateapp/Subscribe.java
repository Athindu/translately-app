package com.example.translateapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.IdentifiableLanguage;
import com.ibm.watson.language_translator.v3.model.IdentifiableLanguages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Subscribe extends AppCompatActivity {


    ListView lanList;
    DatabaseHelper translateDB;
    TextView textView;
    Button button;

    String clicked;
    int valueChecked;
    ArrayList<String> tagList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);
        translateDB = DatabaseHelper.getInstance(this);
        textView = findViewById(R.id.textView);
        lanList = findViewById(R.id.listView);
        button = findViewById(R.id.subscribeBtn);

        showLang();
    }


    public void showLang(){
        final Cursor cursor = translateDB.retriveLang();
        System.out.println("1");
        if (cursor.getCount() == 0){
            Toast.makeText(Subscribe.this, "No data", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<String> list = new ArrayList<>();

        System.out.println("2");
        while (cursor.moveToNext()) {
            list.add(cursor.getString(1));
            tagList.add(cursor.getString(2));
        }                                                                  //retrieve the languages from db and
                                                                           //displaying it as multiple choice listview

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_multiple_choice,list);

        lanList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lanList.setAdapter(arrayAdapter);

        for (int f=0; f<list.size();f++) {                          //get the size of language list and setting all the items in
            lanList.setItemChecked(f,false);                  // the list view to unchcek at the first when activity loads
        }

        Cursor cursorNw = translateDB.retrivePosition();            //get the subscribed languages data
        ArrayList<Integer> positions = new ArrayList<>();
        ArrayList<String> status = new ArrayList<>();
        while (cursorNw.moveToNext()) {

            positions.add(cursorNw.getInt(1));           //get the position in the list view of subscribed language and add it into array
            status.add(cursorNw.getString(2));           //get if it is checked or not (true or false)
        }

        System.out.println("final positions -- "+ positions);       //prints the subscribed positions

        for (int x=0; x<positions.size();x++){                      //loop through the array to make the listview

            int y = positions.get(x);                       //get the position and check the status in that corresponding position
            String stat = status.get(x);                    // if the status is true --> language has been subscribed ,
            if (stat.equals("true")) {                      // so make that position checked in the listview

                lanList.setItemChecked(y, true);
            }
            else if (stat.equals("false")){                 //if status is false --> language is not subscribed at the last attempt
                                                            // so make the position unchecked in the listview
                lanList.setItemChecked(y,false);
            }
        }

        lanList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                clicked = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(Subscribe.this,"You clicked on '"+ clicked+"' " , Toast.LENGTH_SHORT).show();
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("-------");
                SparseBooleanArray checkedItems =lanList.getCheckedItemPositions();         //get the checked items and insert into a sparseboolean array
                System.out.println("1 - " +checkedItems);
                System.out.println(checkedItems.size());

                for (int i=0;i<checkedItems.size();i++){                          //loop through sparseboolean array to check all the elements

                    if(checkedItems.get(i)){                //check the value of the item is true or false

                        valueChecked = checkedItems.keyAt(i);           //get the index of the true item
                        String subsTag = tagList.get(valueChecked);             //get the tag of the true item
                        String item = lanList.getAdapter().getItem(checkedItems.keyAt(i)).toString();       //get the language name
                        translateDB.subscribedLang(valueChecked,"true",item, subsTag);      //insert the values to db with
                        System.out.println("value: " + valueChecked);                             // status "true" as the items inside this if block are checked
                                                                                                  // status is required to display the listview is checked or unchecked
                    }else {

                        valueChecked = checkedItems.keyAt(i);
                        String subsTag = tagList.get(valueChecked);
                        String item = lanList.getAdapter().getItem(
                                checkedItems.keyAt(i)).toString();
                        translateDB.subscribedLang(valueChecked,"false",item,subsTag);        //insert the values to db with status "false" as the items are not checked
                    }
                }

                Toast.makeText(Subscribe.this,"Updated",Toast.LENGTH_SHORT).show();
                List item = Collections.singletonList(checkedItems);
                System.out.println(item);

            }
        });
    }
}



