package com.example.ian.mobile_oki.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: need to implement as singleton
 * Created by Ian on 6/30/2017.
 */

public class CharacterDatabase extends SQLiteAssetHelper implements DatabaseInterface {
    // COMPLETED : Implement SQLite database
    // COMPLETED : Create Database class, extending SQLiteAssetHelper

    // database is available after first call to getReadable/WritableDatabase
    // use setForcedUpgrade() in constructor to overwrite local db with assets folder's db

    /**
     * Should be the same as the game version. <br/><br/>
     * <i><b>Example:</b> Game version is 2.50, Database version is 250</i>
     */
    private static final int DATABASE_VERSION = 250;
    private static final String DATABASE_NAME = "character_data.sqlite";

    public CharacterDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        setForcedUpgrade(); // SQLiteAssetHelper function
    }

    // COMPLETED : Create query method(s) which return a Cursor
    // COMPLETED : Convert Cursor to List<CharacterListItem>


    /**
     * Pulls the list of character names and 3-letter character codes (table names) from the database.
     * @return listOfCharacters - the list items which will populate the list (contains names and codes)
     */
    public List<CharacterListItem> getCharacterNamesAndCodes() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        String[] projection = {"code_name", "full_name"}; // column names
        builder.setTables("NamesTable"); // Table name is NamesTable

        Cursor cursor = builder.query(db, projection,
                null, null, null, null, null);

        List<CharacterListItem> listOfCharacters = new ArrayList<>(cursor.getCount());

        while (cursor.moveToNext()){
            String charCode = cursor.getString(cursor.getColumnIndex("code_name"));
            String charName = cursor.getString(cursor.getColumnIndex("full_name"));
            CharacterListItem listItem = new CharacterListItem(charName, charCode);

            listOfCharacters.add(listItem);
        }

        return listOfCharacters;
    }
}
