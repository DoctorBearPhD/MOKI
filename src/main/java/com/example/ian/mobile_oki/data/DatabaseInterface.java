package com.example.ian.mobile_oki.data;

import java.util.ArrayList;

/**
 * Created by Ian on 7/2/2017.
 */

public interface DatabaseInterface {
    ArrayList<CharacterListItem> getCharacterNamesAndCodes();

    ArrayList<KDMoveListItem> getKDMoves(String codeName);
}
