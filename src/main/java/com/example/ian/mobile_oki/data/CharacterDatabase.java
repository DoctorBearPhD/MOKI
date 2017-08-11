package com.example.ian.mobile_oki.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.ian.mobile_oki.OkiApp;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * COMPLETED: need to implement as singleton
 * Created by Ian on 6/30/2017.
 */

public class CharacterDatabase extends SQLiteAssetHelper implements DatabaseInterface {

    // database is available after first call to getReadable/WritableDatabase
    // use setForcedUpgrade() in constructor to overwrite local db with assets folder's db

    private static final int DATABASE_VERSION = 256;
    private static final String DATABASE_NAME = "character_data.sqlite";

    // Gets the application context from the OkiApp class,
    // so memory leaks aren't an issue here
    @SuppressLint("StaticFieldLeak")
    private static CharacterDatabase INSTANCE;

    private ArrayList<OkiMoveListItem> cachedOkiMoveList;

    private KDMoveListItem currentKDMove;
    private List<OkiMoveListItem> currentOkiMoves;
    private int[] currentOkiRows; // oki move's row / vertical position in timeline
    private int currentRow = 1; // timeline's currently selected row

    private CharacterDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        initializeOkiSlots();

        setForcedUpgrade(); // SQLiteAssetHelper function
    }
/* oki move list does the following:
 * TODO: Cache data
 * TODO: Try to get data from cache before making db queries
 * TODO: Provide method to clear cache, call before activity finish()
 */

    /**
     * Pulls the list of character names and 3-letter character codes (table names) from the database.
     *
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

        db.close();

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
        String selection = "`KD Adv` IS NOT NULL " +
                "AND `KD Adv` != \'-\'" +
                "AND `KD Adv` != \'\'";
        builder.setTables(codeName); // Table name is the 3-letter character code

        Cursor cursor = builder.query(db, projection, selection,
                null, null, null, null);

        ArrayList<KDMoveListItem> listOfKDMoves = new ArrayList<>(cursor.getCount());

        // store the column indices instead of looking them up for every get() call
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

        db.close();

        return listOfKDMoves;
    }

    @Override
    public ArrayList<OkiMoveListItem> getOkiMoves(String codeName) {
        // Don't query the database if we already have the data...
        if (cachedOkiMoveList != null)
            return cachedOkiMoveList;

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        builder.setTables(codeName);

        String move = "Move", command = "Command",
                total = "Total", startup = "Startup", active = "Active", recovery = "Recovery";

        String[] projection = {move, command, total, startup, active, recovery};
        String selection = total + " IS NOT NULL"; // total NOT NULL
        String sortOrder = "CAST(Total AS INTEGER) ASC";

        Cursor cursor = builder.query(db, projection, selection,
                null,null,null,
                sortOrder);

        ArrayList<OkiMoveListItem> list = new ArrayList<>(cursor.getCount());

        // store the column indices instead of looking them up for every get() call
        int moveIndex = cursor.getColumnIndex(move),
            commandIndex = cursor.getColumnIndex(command),
            totalIndex = cursor.getColumnIndex(total),
            startupIndex = cursor.getColumnIndex(startup),
            activeIndex = cursor.getColumnIndex(active),
            recoveryIndex = cursor.getColumnIndex(recovery);

        while (cursor.moveToNext()) {
            OkiMoveListItem listItem = new OkiMoveListItem(
                    cursor.getString(moveIndex),
                    cursor.getString(commandIndex),
                    cursor.getInt(totalIndex),
                    cursor.getInt(startupIndex),
                    cursor.getInt(activeIndex),
                    cursor.getInt(recoveryIndex)
            );

            list.add(listItem);
        }

        db.close();

        // cache the move list (remember to clear the cache!)
        cachedOkiMoveList = list;

        return list;
    }


    @Override
    public KDMoveListItem getCurrentKDMove() {
        return currentKDMove;
    }

    @Override
    public void setCurrentKDMove(KDMoveListItem currentKDMove) {
        this.currentKDMove = currentKDMove;
    }

    @Override
    public OkiMoveListItem getCurrentOkiMoveAt(int okiSlot) {
        return currentOkiMoves.get(okiSlot - 1);
    }

    @Override
    public void setCurrentOkiMove(int okiSlot, OkiMoveListItem okiMove) {
        currentOkiMoves.set(okiSlot - 1, okiMove);
    }

    @Override
    public int getOkiRowOfSlot(int okiSlot) {
        return currentOkiRows[okiSlot - 1];
    }

    @Override
    public void setOkiRowForSlot(int okiSlot, int okiRow) {
        currentOkiRows[okiSlot - 1] = okiRow;
    }

    /**
     * Reset or initialize the list of Oki Moves being used in the Timeline.<br/>
     * Also initializes the list of row numbers associated with each Oki Move.<br/>
     * Creates and fills an ArrayList with null values. Initializes an array of ints.
     */
    @Override
    public void initializeOkiSlots(){
        currentOkiMoves = new ArrayList<>();
        while (currentOkiMoves.size() < 7) currentOkiMoves.add(null);

        currentOkiRows = new int[7];
    }

    @Override
    public void clearOkiMoveListCache(){
        cachedOkiMoveList = null;
    }

    @Override
    public int getCurrentRow() {
        return currentRow;
    }

    @Override
    public void setCurrentRow(int currentRow) {
        this.currentRow = currentRow;
    }

    // This is a singleton pattern, and it's thread-safe
    public static CharacterDatabase getInstance() {
        if (INSTANCE == null) {
            synchronized (CharacterDatabase.class) {
                if (INSTANCE == null)
                    INSTANCE = new CharacterDatabase(OkiApp.getContext());
            }
        }
        return INSTANCE;
    }
}
