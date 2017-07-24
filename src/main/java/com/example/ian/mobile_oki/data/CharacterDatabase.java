package com.example.ian.mobile_oki.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

/**
 * COMPLETED: need to implement as singleton
 * Created by Ian on 6/30/2017.
 */

public class CharacterDatabase extends SQLiteAssetHelper implements DatabaseInterface {
    // COMPLETED : Implement SQLite database
    // COMPLETED : Create Database class, extending SQLiteAssetHelper

    // database is available after first call to getReadable/WritableDatabase
    // use setForcedUpgrade() in constructor to overwrite local db with assets folder's db

    private static final int DATABASE_VERSION = 255;
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
     * TODO: Cache?
     * @return listOfCharacters - the list items which will populate the list (contains names and codes)
     */
    public ArrayList<CharacterListItem> getCharacterNamesAndCodes() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        String[] projection = {"code_name", "full_name"}; // column names
        builder.setTables("NamesTable"); // Table name is NamesTable

        Cursor cursor = builder.query(db, projection,
                null, null, null, null, null);

        ArrayList<CharacterListItem> listOfCharacters = new ArrayList<>(cursor.getCount());

        int codeNameIndex = cursor.getColumnIndex("code_name"),
                fullNameIndex = cursor.getColumnIndex("full_name");

        while (cursor.moveToNext()) {
            String charCode = cursor.getString(codeNameIndex);
            String charName = cursor.getString(fullNameIndex);
            CharacterListItem listItem = new CharacterListItem(charName, charCode);

            listOfCharacters.add(listItem);
        }

        return listOfCharacters;
    }

    @Override
    public ArrayList<KDMoveListItem> getKDMoves(String codeName) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        // Column Names
        String move = "Move", startup = "Startup", active = "Active", recovery = "Recovery",
                kda = "`KD Adv`", kdra = "`KDR Adv`", kdbra = "`KDRB Adv`"; // Note: the R and B in the column names are the reverse of what I use (BR vs RB).

        String[] projection = {move, startup, active, recovery, kda, kdra, kdbra}; // column names to get
        String selection = "`KD Adv` IS NOT NULL";
        builder.setTables(codeName); // Table name is the 3-letter character code

        Cursor cursor = builder.query(db, projection, selection,
                null, null, null, null);

        ArrayList<KDMoveListItem> listOfKDMoves = new ArrayList<>(cursor.getCount());

        int moveIndex = cursor.getColumnIndex(move),
                startupIndex = cursor.getColumnIndex(startup),
                activeIndex = cursor.getColumnIndex(active),
                recoveryIndex = cursor.getColumnIndex(recovery),
                kdaIndex = cursor.getColumnIndex("KD Adv"),
                kdraIndex = cursor.getColumnIndex("KDR Adv"),
                kdbraIndex = cursor.getColumnIndex("KDRB Adv");

        while (cursor.moveToNext()) {
            KDMoveListItem listItem = new KDMoveListItem(
                    cursor.getString(moveIndex),
                    cursor.getInt(kdaIndex),
                    cursor.getInt(kdraIndex),
                    cursor.getInt(kdbraIndex),
                    cursor.getInt(startupIndex),
                    cursor.getInt(activeIndex),
                    cursor.getInt(recoveryIndex)
            );

            listOfKDMoves.add(listItem);
        }

        return listOfKDMoves;
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
