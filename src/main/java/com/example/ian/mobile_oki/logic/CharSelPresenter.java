package com.example.ian.mobile_oki.logic;

import com.example.ian.mobile_oki.contracts.CharacterSelectContract;
import com.example.ian.mobile_oki.data.CharacterListItem;
import com.example.ian.mobile_oki.data.DatabaseInterface;

import java.util.ArrayList;

/**
 * Created by Ian on 7/3/2017.
 */

public class CharSelPresenter implements CharacterSelectContract.Presenter {

    private CharacterSelectContract.View mCharacterSelectView;
    private final DatabaseInterface mDB;

    public CharSelPresenter(CharacterSelectContract.View view, DatabaseInterface db) {
        mCharacterSelectView = view;
        mDB = db;
    }

    @Override
    public void start() {
        mCharacterSelectView.showListOfNames();
    }

    @Override
    public ArrayList<CharacterListItem> fetchListOfNames() {
        return mDB.getCharacterNamesAndCodes();
    }

    @Override
    public void setCurrentCharacter(String codeName, String fullName) {
        mDB.setCurrentCharacter(codeName, fullName);
    }

    @Override
    public void displayFinished() {
        if (mDB.getCurrentCharacter(false) != null)
            mCharacterSelectView.scrollToCurrentItem(mDB.getCurrentCharacter(false));

    }
}
