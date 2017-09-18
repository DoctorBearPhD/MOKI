package com.example.ian.mobile_oki.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.ian.mobile_oki.OkiApp;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

import static com.example.ian.mobile_oki.data.CharacterDBContract.*;

/**
 * The repository of character-related data. <br/>
 * This does not include characters' Oki Setups.
 *   (See {@link com.example.ian.mobile_oki.data.storage.StorageDbHelper StorageDbHelper})
 * <p/>
 *
 * Created by Ian on 6/30/2017.
 */
public class CharacterDatabase extends SQLiteAssetHelper implements DatabaseInterface {

    // database is available after first call to getReadable/WritableDatabase
    // use setForcedUpgrade() in constructor to overwrite local db with assets folder's db

    private static final int DATABASE_VERSION = 253;
    private static final String DATABASE_NAME = "converted_fat_data.sqlite";

    // Gets the application context from the OkiApp class, so memory leaks aren't an issue here
    @SuppressLint("StaticFieldLeak")
    private static CharacterDatabase INSTANCE;

    // Cache

    private ArrayList<CharacterListItem> cachedCharacterList;

    private ArrayList<KDMoveListItem> cachedKDMoveList;

    /**
     * Cached list of Oki Moves for the Oki Move Select screen.
     * @see com.example.ian.mobile_oki.view.OkiMoveSelectActivity Oki Move Select screen
     */
    private ArrayList<OkiMoveListItem> cachedOkiMoveList;

    // Current Setup Data

    private OkiSetupDataObject currentSetup;

    /**
     * The currently selected character.
     * <p>Short holds the 3-letter character code corresponding to a database table name.<br/>
     * <i>(e.g. Alex = ALX)</i>
     * <p>Full holds the full name of the character.
     */
    private String currentCharacterShort, currentCharacterFull;
    /**
     * The currently selected Knockdown Move.
     */
    private KDMoveListItem currentKDMove;
    /**
     * The currently selected Oki Moves in Oki Columns 1 through 7 of the Timeline.
     */
    private ArrayList<OkiMoveListItem> currentOkiMoves;
    /**
     * The vertical positions of the {@link #currentOkiMoves}.
     */
    private int[] currentOkiRows; // oki move's row / vertical position in timeline

    // Timeline instance data
    /**
     * The currently selected row on the Timeline.
     */
    private int currentRow     = 1; // Timeline's currently selected row
    /**
     * The currently selected column in the Timeline (Oki Column #).
     * An int from 1 to 7. Defaults to 1.
     */
    private int currentOkiSlot = 1; // Timeline's currently selected Oki Slot


    private CharacterDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        initializeOkiSlots();

        setForcedUpgrade(); // SQLiteAssetHelper function
    }

    private void updateCurrentSetup(){
        // Set character
          // Set character short
        currentCharacterShort = currentSetup.getCharacter();
          // Set character full
        Cursor c = getReadableDatabase().query("NamesTable",
                new String[]{"full_name"},
                "code_name = ?", new String[]{currentCharacterShort},
                null, null, null);
        c.moveToNext(); // goes to the first item in the result set
        currentCharacterFull = c.getString(0); // sets the character's full name to the result of the query
        c.close();
        // Set KD Move
          // Lookup KD Move in DB by name
        currentKDMove = lookupKDMove(currentSetup.getKdMove());
        // Set Oki Moves
          // Lookup Oki Moves in DB by name
        currentOkiMoves = lookupOkiMoves(currentSetup.getOkiMoves());
        // Set Oki Rows
        currentOkiRows = currentSetup.getOkiRows();
    }

    /**
     * Get the actual data associated with the Move name(s) provided. <br/>
     * Preserves any gaps between used Slots in the Setup, in case the user wants the gaps to be preserved.
     * @param okiMoves The Move names for which to look up data.
     * @return A list of Oki Move data items.
     */
    private ArrayList<OkiMoveListItem> lookupOkiMoves(String[] okiMoves) {
        String selection = "Move IN (";
        String[] selectionArgs;
        int selectionArgsSize = 0,
            okiMovesLength = okiMoves.length; // in case it's decided to allow more than 7 oki moves in a setup
        // In case of gaps between slots in the saved setup, this is used to record the indices of the non-null slots.
        int[] indexOfMove = new int[okiMovesLength];

        for (int i = 0; i < okiMovesLength; i++) {
            String move = okiMoves[i];
            if (move != null) {
                selection = selection.concat("?, ");
                indexOfMove[selectionArgsSize] = i; // [position in the Setup] of the [Move to be looked up]
                selectionArgsSize++;
            }
        }
        // There must be at least one move in the setup to save it, so the following line is safe.
        selection = selection.substring(0, selection.length()-2); // remove last 2 characters (", ")
        selection = selection.concat(")");
        selectionArgs = new String[selectionArgsSize];

        for (int i=0; i < selectionArgsSize; i++)
            selectionArgs[i] = okiMoves[indexOfMove[i]];

        ArrayList<OkiMoveListItem> unorderedList = getOkiMoves(selection, selectionArgs); // returns a reordered list with actual data
        ArrayList<OkiMoveListItem> orderedList = new ArrayList<>(okiMovesLength);
        // initialize the list with nulls
        while (orderedList.size() < okiMovesLength) {
            orderedList.add(null);
        }
        // put the list in the proper order
        for (int i=0; i < selectionArgs.length; i++){
            // add items to orderedList based on order of okiMoves array
              // find matching move name in unorderedList
            int index = 0;
            for (int j=0; j < unorderedList.size(); j++){ // for each item in the [list of actual data]
                if (unorderedList.get(j).getMove() != null &&
                      unorderedList.get(j).getMove().equals( selectionArgs[i] )){ // if the name of the item = the name of the item that was looked up
                    index = j;
                    break;
                }
            }
            orderedList.set(indexOfMove[i], unorderedList.get(index));
        }


        return orderedList;
    }

    private KDMoveListItem lookupKDMove(String moveName){
        if (moveName.substring(0, 5).toUpperCase().equals("(CH) "))
            moveName = moveName.substring(5, moveName.length());

        String selection = "Move = ?";
        String[] selectionArgs = {moveName};

        return getKDMoves(selection, selectionArgs).get(0);
    }

    /**
     * Pulls the list of character names and 3-letter character codes (table names) from the database.
     *
     * @return listOfCharacters - the list items which will populate the list (contains names and codes)
     */
    public ArrayList<CharacterListItem> getCharacterNamesAndCodes() {
        // if data isn't already cached, cache it
        if (cachedCharacterList == null)
            cachedCharacterList = getCharacterNamesAndCodes(null, null);

        return cachedCharacterList;
    }

    public ArrayList<CharacterListItem> getCharacterNamesAndCodes(String selection, String[] selectionArgs) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        String[] projection = {CHAR_CODE, CHAR_NAME}; // column names
        builder.setTables(NAMES_TABLE); // Table name is NamesTable

        Cursor cursor = builder.query(db, projection,
                selection, selectionArgs,
                null, null,
                CHAR_NAME); // order by character name

        ArrayList<CharacterListItem> listOfCharacters = new ArrayList<>(cursor.getCount());

        int codeNameIndex = cursor.getColumnIndex(CHAR_CODE),
                fullNameIndex = cursor.getColumnIndex(CHAR_NAME);

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
    public ArrayList<KDMoveListItem> getKDMoves(){
        if (cachedKDMoveList == null)
            cachedKDMoveList = getKDMoves("CAST (`KD Adv` AS INTEGER) > 0", null);

        return cachedKDMoveList;
    }

    private ArrayList<KDMoveListItem> getKDMoves(String selection, String[] selectionArgs) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        // Column Names
        String kda     = "`" + KDA     + "`",
               kdra    = "`" + KDRA    + "`",
               kdbra   = "`" + KDBRA   + "`", // Note: the R and B (KDBR vs KDRB).
               hitAdv  = "`" + HIT_ADV + "`",
               cHitAdv = "`" + CH_ADV  + "`";

        String[] projection = {MOVE_NAME, COMMAND, STARTUP, ACTIVE, RECOVERY, kda, kdra, kdbra, hitAdv, cHitAdv}; // column names to get

        builder.setTables(getCurrentCharacter(false)); // Table name is the 3-letter character code

        Cursor cursor = builder.query(db, projection, selection, selectionArgs,
                null, null, null);

        ArrayList<KDMoveListItem> listOfKDMoves = new ArrayList<>(cursor.getCount());

        // store the column indices instead of looking them up for every get() call
        int moveIndex = cursor.getColumnIndex(MOVE_NAME),
            commandIndex = cursor.getColumnIndex(COMMAND),
            startupIndex = cursor.getColumnIndex(STARTUP),
            activeIndex = cursor.getColumnIndex(ACTIVE),
            recoveryIndex = cursor.getColumnIndex(RECOVERY),
            kdaIndex = cursor.getColumnIndex(KDA),
            kdraIndex = cursor.getColumnIndex(KDRA),
            kdbraIndex = cursor.getColumnIndex(KDBRA),
            hitAdvIndex = cursor.getColumnIndex(HIT_ADV),
            cHitAdvIndex = cursor.getColumnIndex(CH_ADV);

        String moveName, hitAdvData, cHitAdvData;

        while (cursor.moveToNext()) {
            // If the hit advantage is KD, then the move doesn't need to be a counterhit to KD.
            hitAdvData = cursor.getString(hitAdvIndex);
            cHitAdvData = cursor.getString(cHitAdvIndex);
            moveName = cursor.getString(moveIndex);
            if (hitAdvData == null && cHitAdvData != null) // Move must be a counterhit to KD, so display that
                moveName = "(CH) " + cursor.getString(moveIndex);

            KDMoveListItem listItem = new KDMoveListItem(
                    moveName,
                    cursor.getString(commandIndex),
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
    public ArrayList<OkiMoveListItem> getOkiMoves(){
        // Query the database if we don't already have the data...
        if (cachedOkiMoveList == null) {
            String selection;
            if(!getCurrentCharacter(false).equals("KEN")) { // Ken can finish target combos on whiff.
                selection = STARTUP + " NOT LIKE \"%+%\"";
            } else selection = null;
            cachedOkiMoveList = getOkiMoves(selection, null);
            // add NONE option to start of list
            cachedOkiMoveList.add(0, new OkiMoveListItem("NONE", "", 0,0,"0",0));
        }

        return cachedOkiMoveList;
    }

    private ArrayList<OkiMoveListItem> getOkiMoves(String selection, String[] selectionArgs) {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        builder.setTables(getCurrentCharacter(false));

        String[] projection = {MOVE_NAME, COMMAND, TOTAL, STARTUP, ACTIVE, RECOVERY};

        String sortOrder = null; // TODO: "CAST(" + TOTAL + " AS INTEGER) ASC";

        Cursor cursor = builder.query(db, projection, selection,
                selectionArgs,null,null,
                sortOrder);

        ArrayList<OkiMoveListItem> list = new ArrayList<>(cursor.getCount());

        // store the column indices instead of looking them up for every get() call
        int moveIndex = cursor.getColumnIndex(MOVE_NAME),
            commandIndex = cursor.getColumnIndex(COMMAND),
            totalIndex = cursor.getColumnIndex(TOTAL),
            startupIndex = cursor.getColumnIndex(STARTUP),
            activeIndex = cursor.getColumnIndex(ACTIVE),
            recoveryIndex = cursor.getColumnIndex(RECOVERY);

        String activeData;
        while (cursor.moveToNext()) {
            activeData = cursor.getString(activeIndex);
            OkiMoveListItem listItem = new OkiMoveListItem(
                    cursor.getString(moveIndex),
                    cursor.getString(commandIndex),
                    cursor.getInt(totalIndex), // set data value in JSONParser
                    cursor.getInt(startupIndex),
                    (activeData != null) ? activeData : "0",
                    cursor.getInt(recoveryIndex)
            );

            list.add(listItem);
        }

        db.close();

        return list;
    }
// TODO : Unused
    @Override
    public OkiSetupDataObject getCurrentSetup() {
        return currentSetup;
    }

    @Override
    public void setCurrentSetup(OkiSetupDataObject currentSetup) {
        this.currentSetup = currentSetup;
        clearKDMoveListCache();
        clearOkiMoveListCache();
        updateCurrentSetup();
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

    /**
     * Clears the Character Select screen's cache of Characters.
     */
    @Override
    public void clearCharacterListCache(){
        cachedCharacterList = null;
    }

    /**
     * Clears the KD Move Select screen's cache of KD Moves.
     */
    @Override
    public void clearKDMoveListCache(){
        cachedKDMoveList = null;
    }

    /**
     * Clears the Oki Move Select screen's cache of Oki Moves.
     */
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
    public ArrayList<OkiMoveListItem> getCurrentOkiMoves() {
        return currentOkiMoves;
    }

    @Override
    public void setCurrentOkiMove(int okiSlot, OkiMoveListItem okiMove) {
        currentOkiMoves.set(okiSlot - 1, okiMove);
    }

    @Override
    public boolean isCurrentOkiMovesListEmpty() {
        boolean containsNonNull = false;

        if (getCurrentOkiMoves() != null) {
            for (OkiMoveListItem item : getCurrentOkiMoves()) {
                if (item != null) {
                    containsNonNull = true;
                    break;
                }
            }
        }

        return !containsNonNull;
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
    public int[] getCurrentOkiRows() {
        return currentOkiRows;
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
