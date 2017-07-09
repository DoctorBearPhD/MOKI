package com.example.ian.mobile_oki.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * COMPLETED: need to implement as singleton
 * Created by Ian on 6/30/2017.
 */

public class CharacterDatabase extends SQLiteAssetHelper implements DatabaseInterface {
    // COMPLETED : Implement SQLite database
    // COMPLETED : Create Database class, extending SQLiteAssetHelper

    // database is available after first call to getReadable/WritableDatabase
    // use setForcedUpgrade() in constructor to overwrite local db with assets folder's db

    private static final int DATABASE_VERSION = 251;
    private static final String DATABASE_NAME = "character_data.sqlite";

    // We won't be passing anything but the application context to this,
    // so memory leaks aren't an issue here
    private static CharacterDatabase INSTANCE;

    public CharacterDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        setForcedUpgrade(); // SQLiteAssetHelper function
    }

    // COMPLETED : Create query method(s) which return a Cursor
    // COMPLETED : Convert Cursor to List<CharacterListItem>


    /**
     * Pulls the list of character names and 3-letter character codes (table names) from the database.
     *
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

        while (cursor.moveToNext()) {
            String charCode = cursor.getString(cursor.getColumnIndex("code_name"));
            String charName = cursor.getString(cursor.getColumnIndex("full_name"));
            CharacterListItem listItem = new CharacterListItem(charName, charCode);

            listOfCharacters.add(listItem);
        }

        return listOfCharacters;
    }

    @Override
    public List<KDMoveListItem> getKDMoves() {
        return null;
    }


    // This is a singleton pattern, and it's thread-safe
    public static CharacterDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (CharacterDatabase.class) {
                if (INSTANCE == null)
                    INSTANCE = new CharacterDatabase(context);
            }
        }
        return INSTANCE;
    }
}
