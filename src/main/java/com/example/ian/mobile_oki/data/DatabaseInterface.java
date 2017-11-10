package com.example.ian.mobile_oki.data;

import android.text.SpannedString;

import com.example.ian.mobile_oki.util.ESortOrder;

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

    void clearCharacterListCache();

    void clearKDMoveListCache();

    void clearOkiMoveListCache();

    ArrayList<CharacterListItem> getCharacterNamesAndCodes();

    ArrayList<CharacterListItem> getCharacterNamesAndCodes(String selection, String[] selectionArgs);

    ArrayList<KDMoveListItem> getKDMoves(ESortOrder sortOrder); // codeName = character's 3-letter name

    ArrayList<OkiMoveListItem> getOkiMoves(ESortOrder sortOrder);

    // --Commented out by Inspection (11/8/2017 2:22 PM):OkiSetupDataObject getCurrentSetup();

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

    ESortOrder getKdSortOrder();

    void setKdSortOrder(ESortOrder kdSortOrder);

    ESortOrder getOkiSortOrder();

    void setOkiSortOrder(ESortOrder okiSortOrder);

    void setCurrentOkiSlot(int newOkiSlot);

    boolean getOkiDetailLevel();

    void toggleOkiDetailLevel();

    boolean getKdDetailLevel();

    void toggleKdDetailLevel();

    void moveOkiMove();

    ArrayList<String> getKdCommands(String character, ArrayList<String> kdMoveString);

    ArrayList<String> getOrderedKDMoves(String character, ArrayList<String> kdMovesList);

    boolean isKdaDirty();

    void validateKda();

    void invalidateKda();

    SpannedString[] getKdaContent();

    void setKdaContent(SpannedString[] kdaContent);
}
