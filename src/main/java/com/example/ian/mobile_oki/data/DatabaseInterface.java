package com.example.ian.mobile_oki.data;

import java.util.ArrayList;

/**
 * Created by Ian on 7/2/2017.
 */

public interface DatabaseInterface {

    void initializeOkiSlots();

    void clearOkiMoveListCache();

    ArrayList<CharacterListItem> getCharacterNamesAndCodes();

    ArrayList<KDMoveListItem> getKDMoves(); // codeName = character's 3-letter name

    ArrayList<OkiMoveListItem> getOkiMoves();

    String getCurrentCharacter(boolean fullName);

    void setCurrentCharacter(String newCharacterShort, String newCharacterFull);

    KDMoveListItem getCurrentKDMove();

    void setCurrentKDMove(KDMoveListItem currentKDMove);

    OkiMoveListItem getCurrentOkiMoveAt(int okiSlot);

    void setCurrentOkiMove(int okiSlot, OkiMoveListItem okiMove);

    int getOkiRowOfSlot(int okiSlot);

    void setOkiRowForSlot(int okiSlot, int okiRow);

    int getCurrentRow();

    void setCurrentRow(int okiRow);

    int getCurrentOkiSlot();

    void setCurrentOkiSlot(int newOkiSlot);
}
