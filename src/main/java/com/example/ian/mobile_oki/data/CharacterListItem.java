package com.example.ian.mobile_oki.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ian on 7/2/2017.
 */

public class CharacterListItem implements Parcelable {
    private final String characterName;
    private final String characterCode;

    CharacterListItem(String characterName, String characterCode) {
        this.characterName = characterName;
        this.characterCode = characterCode;
    }

    private CharacterListItem(Parcel in) {
        characterName = in.readString();
        characterCode = in.readString();
    }

    public String getCharacterName() {
        return characterName;
    }

    public String getCharacterCode() {
        return characterCode;
    }

    /**
     * Overridden to allow comparison of the character code to a given {@link String}.
     * @param obj the comparator
     * @return True, if the character codes match. Otherwise, false.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CharacterListItem) {
            return ((CharacterListItem) obj).getCharacterCode().equals(getCharacterCode());
        } else if (obj instanceof String) {
            return obj.equals(getCharacterCode());
        }
        else
            return super.equals(obj);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(characterName);
        out.writeString(characterCode);
    }

    public static final Creator<CharacterListItem> CREATOR = new Creator<CharacterListItem>() {
        @Override
        public CharacterListItem createFromParcel(Parcel in) {
            return new CharacterListItem(in);
        }

        @Override
        public CharacterListItem[] newArray(int size) {
            return new CharacterListItem[size];
        }
    };
}
