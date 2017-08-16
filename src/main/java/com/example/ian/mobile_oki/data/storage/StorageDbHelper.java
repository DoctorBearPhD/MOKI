package com.example.ian.mobile_oki.data.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ian.mobile_oki.data.OkiSetupDataObject;
import com.example.ian.mobile_oki.data.storage.StorageSchema.CharacterOkiSetups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.ian.mobile_oki.data.storage.StorageSchema.CharacterOkiSetups.COLUMN_NAMES;

/**
 * Database for saving and loading Oki Setups. Requires passage of TABLE_NAME (Character Code).
 * <p>
 * Created by Ian on 8/16/2017.
 */

public class StorageDbHelper extends SQLiteOpenHelper implements StorageInterface {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Storage.db";
    private static final HashMap<String, String> FIELDS = new HashMap<String, String>() {
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
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //TODO: String tableName = CharacterDatabase.getInstance().getCurrentCharacter(false);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // unused since version would be inconsistent
    }

    /**
     * Creates a table in which to store a character's Oki Setups, if one doesn't already exist.
     *
     * @param tableName - Character for whom to store the Oki Setup.
     */
    private void createTable(final String tableName) {
        SQLiteDatabase db = getWritableDatabase();
        String command = "CREATE TABLE IF NOT EXISTS " + tableName + "(";

        for (Map.Entry<String, String> entry : FIELDS.entrySet())
            command = command.concat(entry.getKey() + entry.getValue());

        command = command.concat(");");

        db.execSQL(command);
    }

    /*-----------------*\
    * Interface Methods *
    \*-----------------*/

    @Override
    public boolean saveData(OkiSetupDataObject data) {

        createTable(data.getCharCode());

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAMES[0], data.getKdMove());

        for (int i = 1; i < data.getOkiMoves().length; i++) {
            if (i % 2 == 0) // note: can't use inline comparison here without converting int to String (then db converts it back to int)
                 values.put(COLUMN_NAMES[i], data.getOkiRows()[i]);
            else values.put(COLUMN_NAMES[i], data.getOkiMoves()[i]);
        }

        // -1 = error/didn't insert; otherwise, row number = success
        return db.insert(data.getCharCode(), null, values) != -1;
    }

    @Override
    public ArrayList<OkiSetupDataObject> loadData(final String tableName) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(tableName, null, null, null,
                null, null,null);

        ArrayList<OkiSetupDataObject> dataList = new ArrayList<>();
        OkiSetupDataObject data;
        String[]           moves;
        int[]              rows;

        while (cursor.moveToNext()) {
            data  = new OkiSetupDataObject();
            moves = new String[7];
            rows  = new int[7];

            data.setCharCode(tableName);
            data.setKdMove(cursor.getString(0));

            for (int i = 1; i < COLUMN_NAMES.length; i++) {
                if ( i % 2 == 0) rows[i] = cursor.getInt(i);
                else            moves[i] = cursor.getString(i);
            }

            data.setOkiMoves(moves);
            data.setOkiRows(rows);
            dataList.add(data);
        }


        cursor.close();
        return dataList;
    }

    @Override
    public void deleteData(final String tableName, final int _ID) {
        // TODO
    }


}
