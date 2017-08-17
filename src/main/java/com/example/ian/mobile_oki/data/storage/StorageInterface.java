package com.example.ian.mobile_oki.data.storage;

import com.example.ian.mobile_oki.data.OkiSetupDataObject;

import java.util.ArrayList;

/**
 * Created by Ian on 8/16/2017.
 */

public interface StorageInterface {

    /** Save an entry to the Oki Setup DB. */
    boolean saveData(String characterName, OkiSetupDataObject data);

    /** Load an entry from the Oki Setup DB. */
    ArrayList<OkiSetupDataObject> loadData(String tableName);

    /** Delete an entry in the Oki Setup DB. */
    void deleteData(String tableName, int _ID);

    void closeDb();
}
