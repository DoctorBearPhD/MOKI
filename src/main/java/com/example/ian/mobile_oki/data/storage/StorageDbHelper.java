package com.example.ian.mobile_oki.data.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.ian.mobile_oki.data.OkiSetupDataObject;
import com.example.ian.mobile_oki.data.storage.StorageSchema.CharacterOkiSetups;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.ian.mobile_oki.data.storage.StorageSchema.CharacterOkiSetups.COLUMN_NAMES;

//import com.example.ian.mobile_oki.OkiApp;

/**
 * Database helper for saving and loading Oki Setups. <br/>
 * Requires passage of a {@code TABLE_NAME} (Character Code).
 * <p/>
 * Created by Ian on 8/16/2017.
 */

public class StorageDbHelper extends SQLiteOpenHelper implements StorageInterface {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Storage.db";
    private static final LinkedHashMap<String, String> FIELDS = new LinkedHashMap<String, String>() {
        {
            put(CharacterOkiSetups.COLUMN_NAME_KD_MOVE, " TEXT");
            put(CharacterOkiSetups.COLUMN_NAME_OKI1_MOVE, " TEXT");
            put(CharacterOkiSetups.COLUMN_NAME_OKI1_ROW, " INTEGER");
            put(CharacterOkiSetups.COLUMN_NAME_OKI2_MOVE, " TEXT");
            put(CharacterOkiSetups.COLUMN_NAME_OKI2_ROW, " INTEGER");
            put(CharacterOkiSetups.COLUMN_NAME_OKI3_MOVE, " TEXT");
            put(CharacterOkiSetups.COLUMN_NAME_OKI3_ROW, " INTEGER");
            put(CharacterOkiSetups.COLUMN_NAME_OKI4_MOVE, " TEXT");
            put(CharacterOkiSetups.COLUMN_NAME_OKI4_ROW, " INTEGER");
            put(CharacterOkiSetups.COLUMN_NAME_OKI5_MOVE, " TEXT");
            put(CharacterOkiSetups.COLUMN_NAME_OKI5_ROW, " INTEGER");
            put(CharacterOkiSetups.COLUMN_NAME_OKI6_MOVE, " TEXT");
            put(CharacterOkiSetups.COLUMN_NAME_OKI6_ROW, " INTEGER");
            put(CharacterOkiSetups.COLUMN_NAME_OKI7_MOVE, " TEXT");
            put(CharacterOkiSetups.COLUMN_NAME_OKI7_ROW, " INTEGER");
        }
    };

    public StorageDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        // T/ODO: TEMP
        //OkiApp.getContext().deleteDatabase(DATABASE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // unused since version would be constantly changing and purely local
    }

    /**
     * Creates a table in which to store a character's Oki Setups, if one doesn't already exist.
     *
     * @param tableName - Character for whom to store the Oki Setup.
     */
    private void createTable(final String tableName) {
        SQLiteDatabase db = getWritableDatabase();
        String command = "CREATE TABLE IF NOT EXISTS " + tableName + "(";

        for (Map.Entry<String, String> entry : FIELDS.entrySet()) {
            command = command.concat(entry.getKey() + entry.getValue() + ", ");
        }
        command = command.substring(0,command.length() - 2); // removes final ", "
        command = command.concat(");");
        Log.d("***", "createTable: " + command);
        db.execSQL(command);
    }

    private void dropTable(final String tableName) {
        SQLiteDatabase db = getWritableDatabase();
        String command = "DROP TABLE IF EXISTS " + tableName;

        db.execSQL(command);

    }


    /*-----------------*\
    * Interface Methods *
    \*-----------------*/

    /**
     * <p>NOTE: There is no error here,
     * but Android Studio 3.0 (Canary x.x and Beta 2) does mark the SQL statement as an error
     * because it thinks the statement is an "Android Room SQL Fragment." </p>
     * It is actually a proper SQLite statement!
     */
    @Override
    public boolean tableExists(String tableName) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT name FROM sqlite_master " +
                        "WHERE type = ? AND name = ?",
                new String[]{"table", tableName});
        int count = cursor.getCount();

        cursor.close();

        return count > 0;
    }

    @Override
    public String[] getCharactersWithData() {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT name FROM sqlite_master WHERE type = 'table'", null);
        ArrayList<String> codes = new ArrayList<>();

        cursor.moveToNext(); // Skips the android_metadata table

        while (cursor.moveToNext()) {
            codes.add(cursor.getString(0));
        }

        cursor.close();

        String[] codeStrings = new String[codes.size()];
        codes.toArray(codeStrings);
        return codeStrings;
    }

    @Override
    public boolean saveData(String characterCode, OkiSetupDataObject data) {

        createTable(characterCode);

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAMES[0], data.getKdMove());

        int moveIterator = 0,
            rowIterator  = 0;
        for (int i = 1; i < COLUMN_NAMES.length; i++) {
            if (i % 2 == 0) {
                values.put(COLUMN_NAMES[i], data.getOkiRows()[rowIterator]);
                rowIterator++;
            } else {
                values.put(COLUMN_NAMES[i], data.getOkiMoves()[moveIterator]);
                moveIterator++;
            }
        }

        // -1 = error/didn't insert; otherwise, row number = success
        return db.insert(characterCode, null, values) != -1;
    }

    @Override
    public ArrayList<OkiSetupDataObject> loadData(final String tableName) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(tableName, null, null, null,
                null, null,null);

        ArrayList<OkiSetupDataObject> dataList = new ArrayList<>();
        OkiSetupDataObject data;
        String[]           moves;
        int[]              rows; // positions

        while (cursor.moveToNext()) {
            data  = new OkiSetupDataObject();
            moves = new String[7];
            rows  = new int[7];

            data.setCharacter(tableName);
            data.setKdMove(cursor.getString(0));

            int moveIterator = 0;
            int rowIterator = 0;
            for (int i = 1; i < COLUMN_NAMES.length; i++) {
                if ( i % 2 == 0) {
                    rows[rowIterator] = cursor.getInt(i);
                    rowIterator++;
                } else {
                    moves[moveIterator] = cursor.getString(i);
                    moveIterator++;
                }
            }

            data.setOkiMoves(moves);
            data.setOkiRows(rows);
            dataList.add(data);
        }

        cursor.close();
        return dataList;
    }

    @Override
    public void deleteData(final String tableName, final long id) {
        SQLiteDatabase db = getWritableDatabase();
        String selection = "rowid = ?";
        String[] selArgs = { String.valueOf(id) };

        db.delete(tableName, selection, selArgs);

        // if the table is empty now, delete the table so it doesn't show up in the character spinner
        Cursor cursor = db.rawQuery("SELECT count(*) FROM " + tableName, null);
        cursor.moveToNext();

        int count = cursor.getInt(0);
        cursor.close();

        if (count == 0)
            dropTable(tableName);
    }

    @Override
    public void closeDb() {
        close();
    }

    @Override
    public ArrayList<Long> getIDsOfSavedSetups(String tableName) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(tableName, new String[] {"rowid"}, null, null,
                null, null,null);

        ArrayList<Long> idList = new ArrayList<>(cursor.getCount());
        while(cursor.moveToNext()){
            idList.add(cursor.getLong(0));
        }

        cursor.close();

        return idList;
    }


}
