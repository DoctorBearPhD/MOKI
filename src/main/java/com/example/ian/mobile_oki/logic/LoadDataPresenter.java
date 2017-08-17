package com.example.ian.mobile_oki.logic;

import com.example.ian.mobile_oki.contracts.LoadDataContract;
import com.example.ian.mobile_oki.data.CharacterDatabase;
import com.example.ian.mobile_oki.data.CharacterListItem;
import com.example.ian.mobile_oki.data.DatabaseInterface;

import java.util.ArrayList;

/**
 * Created by Ian on 8/17/2017.
 */

public class LoadDataPresenter implements LoadDataContract.Presenter {
    private LoadDataContract.View mView;
    private DatabaseInterface mDB;

    public LoadDataPresenter(LoadDataContract.View view) {
        mView = view;
        mDB = CharacterDatabase.getInstance();

        attachView();
    }

    public LoadDataPresenter(LoadDataContract.View view, DatabaseInterface db){
        mView = view;
        mDB   =   db;

        attachView();
    }

    @Override
    public void start() {
        mView.populateCharacterSpinner();
        //TODO: then...
    }

    private void attachView() {
        mView.setPresenter(this);
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public ArrayList<CharacterListItem> getCharacters() {

        return mDB.getCharacterNamesAndCodes();
    }

    @Override
    public String getCurrentCharacter() {
        return mDB.getCurrentCharacter(true);
    }
}
