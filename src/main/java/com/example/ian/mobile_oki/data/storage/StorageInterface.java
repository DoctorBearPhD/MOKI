package com.example.ian.mobile_oki.data.storage;

import com.example.ian.mobile_oki.data.OkiSetupDataObject;

import java.util.ArrayList;

/**
 * The interface for accessing the repository of Saved Oki Setups.
 * <p/>
 * Created by Ian on 8/16/2017.
 */

public interface StorageInterface {

    /** Check if a table exists. */
    boolean tableExists(String tableName);

    /** Get the code-names of all Characters that have Saved Data. */
    String[] getCharactersWithData();

    /** Save an entry to the Oki Setup DB. */
    boolean saveData(String characterName, OkiSetupDataObject data);

    /** Load all entries from the Oki Setup DB. */
    ArrayList<OkiSetupDataObject> loadData(String tableName);

    /** Delete an entry in the Oki Setup DB. */
    void deleteData(String tableName, long id);

    void closeDb();

    ArrayList<Long> getIDsOfSavedSetups(String tableName);
}
