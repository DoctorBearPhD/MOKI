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

    private static final int DATABASE_VERSION = 258;
    private static final String DATABASE_NAME = "character_data.sqlite";

    // Gets the application context from the OkiApp class,
    // so memory leaks aren't an issue here
    @SuppressLint("StaticFieldLeak")
    private static CharacterDatabase INSTANCE;

    private ArrayList<OkiMoveListItem> cachedOkiMoveList;

    /**
     * The currently selected character.
     * <p>Short holds the 3-letter character code corresponding to a database table name.<br/>
     * <i>(e.g. Alex = ALX)</i>
     * <p>Full holds the full name of the character.
     */
    private String currentCharacterShort, currentCharacterFull;
    /**
     * The currently selected Knockdown Move.
     * <p>Holds the entire move name as listed in the database.
     */
    private KDMoveListItem currentKDMove;
    private List<OkiMoveListItem> currentOkiMoves;
    private int[] currentOkiRows; // oki move's row / vertical position in timeline
    private int currentRow     = 1; // Timeline's currently selected row
    /**
     * The currently selected column in the Timeline (Oki #). An int from 1 to 7. Defaults to 1.
     */
    private int currentOkiSlot = 1; // Timeline's currently selected Oki Slot

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
                null, null, null, null, "full_name");

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
    public ArrayList<KDMoveListItem> getKDMoves() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        // Column Names
        String move = "Move", startup = "Startup", active = "Active", recovery = "Recovery",
                kda = "`KD Adv`", kdra = "`KDR Adv`", kdbra = "`KDRB Adv`", // Note: the R and B (KDBR vs KDRB).
                hitAdv = "`Hit Advantage`";

        String[] projection = {move, startup, active, recovery, kda, kdra, kdbra, hitAdv}; // column names to get
        String selection = "CAST (`KD Adv` AS INTEGER) > 0";
        String order = "CAST (`KD Adv` AS INTEGER) DESC";
        builder.setTables(getCurrentCharacter(false)); // Table name is the 3-letter character code

        Cursor cursor = builder.query(db, projection, selection,
                null, null, null, order);

        ArrayList<KDMoveListItem> listOfKDMoves = new ArrayList<>(cursor.getCount());

        // store the column indices instead of looking them up for every get() call
        int moveIndex = cursor.getColumnIndex(move),
            startupIndex = cursor.getColumnIndex(startup),
            activeIndex = cursor.getColumnIndex(active),
            recoveryIndex = cursor.getColumnIndex(recovery),
            kdaIndex = cursor.getColumnIndex("KD Adv"),
            kdraIndex = cursor.getColumnIndex("KDR Adv"),
            kdbraIndex = cursor.getColumnIndex("KDRB Adv"),
            hitAdvIndex = cursor.getColumnIndex("Hit Advantage");

        String moveName, hitAdvData;

        while (cursor.moveToNext()) {
            // If the hit advantage is KD, then the move doesn't need to be a counterhit to KD.
            hitAdvData = cursor.getString(hitAdvIndex);
            moveName = cursor.getString(moveIndex);
            if (hitAdvData != null && !hitAdvData.equals("KD")) // Move must be a counterhit to KD, so display that
                moveName = "(CH) " + cursor.getString(moveIndex);


            KDMoveListItem listItem = new KDMoveListItem(
                    moveName,
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
    public ArrayList<OkiMoveListItem> getOkiMoves() {
        // Don't query the database if we already have the data...
        if (cachedOkiMoveList != null)
            return cachedOkiMoveList;

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        builder.setTables(getCurrentCharacter(false));

        String move = "Move", command = "Command",
                total = "Total", startup = "Startup", active = "Active", recovery = "Recovery";

        String[] projection = {move, command, total, startup, active, recovery};
        String selection = "CAST(" + total + " AS INTEGER) > 0 " +
                           "OR " + command + " LIKE 61";
        String sortOrder = "CAST(" + total + " AS INTEGER) ASC";

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

        String activeData;
        while (cursor.moveToNext()) {
            activeData = cursor.getString(activeIndex);
            OkiMoveListItem listItem = new OkiMoveListItem(
                    cursor.getString(moveIndex),
                    cursor.getString(commandIndex),
                    cursor.getInt(totalIndex),
                    cursor.getInt(startupIndex),
                    (activeData != null) ? activeData : "0",
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
    public String getCurrentCharacter(boolean fullName) {
        return fullName ? currentCharacterFull : currentCharacterShort;
    }

    @Override
    public void setCurrentCharacter(String newCharacterShort, String newCharacterFull) {
        currentCharacterShort = newCharacterShort;
        currentCharacterFull = newCharacterFull;
    }

    /**
     * Reset or initialize the list of Oki Moves being used in the Timeline.<br/>
     * Also initializes the list of starting positions of each Oki Move.<br/>
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

    @Override
    public int getCurrentRow() {
        return currentRow;
    }

    @Override
    public void setCurrentRow(int okiRow) {
        currentRow = okiRow;
    }

    @Override
    public int getCurrentOkiSlot() {
        return currentOkiSlot;
    }

    @Override
    public void setCurrentOkiSlot(int newOkiSlot) {
        currentOkiSlot = newOkiSlot;
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
