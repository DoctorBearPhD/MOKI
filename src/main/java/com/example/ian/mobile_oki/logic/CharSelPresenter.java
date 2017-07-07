package com.example.ian.mobile_oki.logic;

import android.support.v7.widget.LinearLayoutManager;

import com.example.ian.mobile_oki.contracts.CharacterSelectContract;
import com.example.ian.mobile_oki.data.CharacterDatabase;
import com.example.ian.mobile_oki.data.CharacterListItem;
import com.example.ian.mobile_oki.view.CharacterSelectActivity;

import java.util.List;

/**
 * Created by Ian on 7/3/2017.
 */

public class CharSelPresenter implements CharacterSelectContract.Presenter {

    private final CharacterSelectContract.View mCharacterSelectView;
    private final CharacterDatabase mDB;

    public CharSelPresenter(CharacterSelectContract.View view, CharacterDatabase db) {
        mCharacterSelectView = view;
        mDB = db;

        mCharacterSelectView.setPresenter(this);
    }

    @Override
    public void start() {
        mCharacterSelectView.showListOfNames();
    }

    @Override
    public List<CharacterListItem> fetchListOfNames() {
        return mDB.getCharacterNamesAndCodes();
    }

}
