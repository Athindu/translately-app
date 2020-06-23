package com.example.translateapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper sInstance;
    public static final String DATABASE_NAME = "translateDB";
    public static final String TABLE_NAME = "phraseTable";          //table which stores all the entered words
    public static final String TABLELANG = "languages";             //table which stores all the language list from IBM
    public static final String TABLE_POSITIONS = "positions";       //table which stores all the subscribed languages
    public static final String FRENCH_TABLE = "frenchWords";        //table which stores all the french words for offline use
    public static final String SPANISH_TABLE = "spanishWords";      //table which stores all the spanish words for offline use
    public static final String RUSSIAN_TABLE = "russianWords";      //table which stores all the russian words for offline use
    public static final String GERMAN_TABLE = "germanWords";        //table which stores all the german words for offline use
    public static final String COL_1 = "ID";                        //primary key
    public static final String COL_2 = "phrasesSave";               //saved words
    public static final String COL_3 = "langName";                  //language name
    public static final String COL_4 = "langTAG";                   //language tag
    public static final String COL_5 = "langSubs";                  //subscribed language position
    public static final String COL_6 = "status";                    //status of the relevant language after starting the activity
    public static final String COL_7 = "lang";                      //subscribed language
    public static final String COL_8 = "subsTag";                   //subscribed language tag
    public static final String COL_9 = "frenchWords";
    public static final String COL_10 = "spanishWords";
    public static final String COL_11 = "russianWords";
    public static final String COL_12 = "germanWords";

    public static synchronized DatabaseHelper getInstance(Context context){         //avoid data leaking by calling the method
        if (sInstance==null){                                                   //https://stackoverflow.com/questions/18147354/sqlite-connection-leaked-although-everything-closed/18148718#18148718
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + "( ID INTEGER PRIMARY KEY AUTOINCREMENT, phrasesSave TEXT UNIQUE )");             //table which stores words
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLELANG + "( ID INTEGER PRIMARY KEY AUTOINCREMENT, langName TEXT , langTAG TEXT  )");            //table which stores all the languages
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_POSITIONS + "( ID INTEGER PRIMARY KEY AUTOINCREMENT, langSubs INTEGER , status TEXT , lang TEXT , subsTag TEXT )");      //tables which stores the subscribed language positions
        sqLiteDatabase.execSQL("CREATE TABLE " + FRENCH_TABLE + "( ID INTEGER PRIMARY KEY AUTOINCREMENT, frenchWords TEXT  )");                 //tables which stores all the french translations
        sqLiteDatabase.execSQL("CREATE TABLE " + SPANISH_TABLE + "( ID INTEGER PRIMARY KEY AUTOINCREMENT, spanishWords TEXT )");                //tables which stores all the spanish translations
        sqLiteDatabase.execSQL("CREATE TABLE " + RUSSIAN_TABLE + "( ID INTEGER PRIMARY KEY AUTOINCREMENT, russianWords TEXT )");                //tables which stores all the russian translations
        sqLiteDatabase.execSQL("CREATE TABLE " + GERMAN_TABLE + "( ID INTEGER PRIMARY KEY AUTOINCREMENT, germanWords TEXT )");                  //tables which stores all the german translations
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLELANG);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_POSITIONS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FRENCH_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SPANISH_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RUSSIAN_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GERMAN_TABLE);
        onCreate(sqLiteDatabase);
    }

    public Boolean insertData(String phrasesSave){                          //insert the words to the database
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,phrasesSave);
        long result = database.insert(TABLE_NAME,null,contentValues);
        System.out.println(result);
        if (result==-1)                                         //check if the item is entered to the db
            return false;
        else
            return true;
    }

    public Cursor retrieveData(){                       //get all the words from db  and return a cursor containing words
        System.out.println("retrieve");
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME , null);
        return cursor;
    }


    public void updateData(String newName, int id, String oldName){             //update the word after editing is done
        SQLiteDatabase db = this.getWritableDatabase();                         //get the id of the relevant row and word and replace it by the newName
        String query = "UPDATE " + TABLE_NAME + " SET " + COL_2 +
                " = '" + newName + "' WHERE " + COL_1 + " = '" + id + "'" +
                " AND " + COL_2 + " = '" + oldName + "'";

        db.execSQL(query);
    }


    public Cursor getItemID(String name){                                   //get the id of the selected word, used for updating (updateData method)
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL_1 + " FROM " + TABLE_NAME +
                " WHERE " + COL_2 + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Boolean enterLang(String langName,String langTAG){               //insert the language and relevant tag to the db
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_3,langName);
        contentValues.put(COL_4,langTAG);
        long result = database.insert(TABLELANG,null,contentValues);

        if (result==-1)
            return false;                               //check if the item is entered to the db
        else
            return true;
    }


    public Integer deleteData(String phrasesSave){                              //delete the selected word
        SQLiteDatabase database = this.getWritableDatabase();
        return database.delete(TABLE_NAME,"phrasesSave = ?", new String[] {phrasesSave});           //https://stackoverflow.com/questions/7510219/deleting-row-in-sqlite-in-android
    }

    public Cursor retriveLang(){                                        //returning the languages as a cursor
        System.out.println("retrieve");
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLELANG, null);
        return cursor;
    }

    public Boolean subscribedLang(int langSubs,String status, String lang, String subsTag){             //inserting the selected languages,tag,id of the row,status if it is selected or not
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_5,langSubs);
        contentValues.put(COL_6,status);
        contentValues.put(COL_7,lang);
        contentValues.put(COL_8,subsTag);
        long result = database.insert(TABLE_POSITIONS,null,contentValues);

        if (result==-1)
            return false;                           //check if the item is entered to the db
        else
            return true;
    }


    public Cursor retrivePosition(){                                    //returns cursor which contains subscribed languages
        System.out.println("Subscribed positions");
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursorNw = database.rawQuery("SELECT * FROM " + TABLE_POSITIONS, null);

        return cursorNw;
    }


    public Boolean translatedFrench(String french){                             //inserting the translated french word to the db
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_9,french);
        long result = database.insert(FRENCH_TABLE,null,contentValues);
        System.out.println(result);
        if (result==-1)
            return false;
        else
            return true;
    }

    public Boolean translatedSpanish(String spanish){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_10,spanish);
        long result = database.insert(SPANISH_TABLE,null,contentValues);
        System.out.println(result);
        if (result==-1)
            return false;
        else
            return true;
    }


    public Boolean translatedRussian(String russian){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_11,russian);
        long result = database.insert(RUSSIAN_TABLE,null,contentValues);
        System.out.println(result);
        if (result==-1)
            return false;
        else
            return true;
    }

    public Boolean translatedGerman(String german){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_12,german);
        long result = database.insert(GERMAN_TABLE,null,contentValues);
        System.out.println(result);
        if (result==-1)
            return false;
        else
            return true;
    }

    public Cursor retrieveFrench(){                                     //returning translated french words to display
        System.out.println("retrieve");
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + FRENCH_TABLE, null);
        return cursor;
    }

    public Cursor retrieveSpanish(){
        System.out.println("retrieve");
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + SPANISH_TABLE, null);
        return cursor;
    }

    public Cursor retrieveRussian(){
        System.out.println("retrieve");
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + RUSSIAN_TABLE, null);
        return cursor;
    }

    public Cursor retrieveGerman(){
        System.out.println("retrieve");
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + GERMAN_TABLE, null);
        return cursor;
    }

    public void deleteFrench(){                                 //deleting tables
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("delete from "+ FRENCH_TABLE);     //https://www.tutorialspoint.com/sqlite/sqlite_delete_query.htm
    }

    public void deleteSpanish(){
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("delete from "+ SPANISH_TABLE);
    }

    public void deleteRussian(){
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("delete from "+ RUSSIAN_TABLE);
    }

    public void deleteGerman(){
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("delete from "+ GERMAN_TABLE);
    }

}

