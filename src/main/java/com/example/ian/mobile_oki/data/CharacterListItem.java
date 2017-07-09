package com.example.ian.mobile_oki.data;

/**
 * Created by Ian on 7/2/2017.
 */

public class CharacterListItem {
    private String characterName;
    private String characterCode;

    public CharacterListItem(String characterName, String characterCode) {
        this.characterName = characterName;
        this.characterCode = characterCode;
    }

    public String getCharacterName() {
        return characterName;
    }

    public String getCharacterCode() {
        return characterCode;
    }
}
