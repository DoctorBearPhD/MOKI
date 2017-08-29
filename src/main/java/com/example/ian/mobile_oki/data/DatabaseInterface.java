package com.example.ian.mobile_oki.data;

import java.util.ArrayList;

/**
 * Interface for accessing the repository of character-related data. <br/>
 * This does not include characters' Oki Setups.
 *   (See {@link com.example.ian.mobile_oki.data.storage.StorageInterface StorageInterface})
 * <p/>
 * Created by Ian on 7/2/2017.
 */

public interface DatabaseInterface {

    void initializeOkiSlots();

    void clearOkiMoveListCache();

    ArrayList<CharacterListItem> getCharacterNamesAndCodes();

    ArrayList<CharacterListItem> getCharacterNamesAndCodes(String selection, String[] selectionArgs);

    ArrayList<KDMoveListItem> getKDMoves(); // codeName = character's 3-letter name

    ArrayList<OkiMoveListItem> getOkiMoves();

    OkiSetupDataObject getCurrentSetup();

    void setCurrentSetup(OkiSetupDataObject currentSetup);

    String getCurrentCharacter(boolean fullName);

    void setCurrentCharacter(String newCharacterShort, String newCharacterFull);

    KDMoveListItem getCurrentKDMove();

    void setCurrentKDMove(KDMoveListItem currentKDMove);

    OkiMoveListItem getCurrentOkiMoveAt(int okiSlot);

    ArrayList<OkiMoveListItem> getCurrentOkiMoves();

    void setCurrentOkiMove(int okiSlot, OkiMoveListItem okiMove);

    boolean isCurrentOkiMovesListEmpty();

    int getOkiRowOfSlot(int okiSlot);

    void setOkiRowForSlot(int okiSlot, int okiRow);

    int[] getCurrentOkiRows();

    int getCurrentRow();

    void setCurrentRow(int okiRow);

    int getCurrentOkiSlot();

    void setCurrentOkiSlot(int newOkiSlot);
}
