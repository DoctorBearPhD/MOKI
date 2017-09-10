package com.example.ian.mobile_oki.logic;

import android.support.annotation.VisibleForTesting;

import com.example.ian.mobile_oki.contracts.LoadDataContract;
import com.example.ian.mobile_oki.data.CharacterDatabase;
import com.example.ian.mobile_oki.data.CharacterListItem;
import com.example.ian.mobile_oki.data.DatabaseInterface;
import com.example.ian.mobile_oki.data.OkiSetupDataObject;
import com.example.ian.mobile_oki.data.storage.StorageInterface;

import java.util.ArrayList;

/**
 * Created by Ian on 8/17/2017.
 */

public class LoadDataPresenter implements LoadDataContract.Presenter {
    private LoadDataContract.View mView;
    private DatabaseInterface mDB;
    private StorageInterface mStorage;

    public LoadDataPresenter(LoadDataContract.View view, StorageInterface storage) {
        mView = view;
        mDB = CharacterDatabase.getInstance();
        mStorage = storage;

        attachView();
    }

    @VisibleForTesting
    LoadDataPresenter(LoadDataContract.View view, DatabaseInterface db, StorageInterface storage){
        mStorage = storage;
        mView    =    view;
        mDB      =      db;

        attachView();
    }

    @Override
    public void start() {
        mView.populateCharacterSpinner();
    }

    private void attachView() {
        mView.setPresenter(this);
    }

    @Override
    public void detachView() {
        mStorage.closeDb();
        mView = null;
    }

    @Override
    public ArrayList<CharacterListItem> getCharacters() {
        String[] selectionArgs = mStorage.getCharactersWithData();
        // if there are characters with oki setups stored, look them up...
        if (selectionArgs.length > 0) {

            String selection = "code_name IN(";

            for (String selectionArg : selectionArgs)
                selection = selection.concat("?, ");

            selection = selection.substring(0, selection.length() - 2); // removes the last ", "

            selection = selection.concat(")");

            return mDB.getCharacterNamesAndCodes(selection, selectionArgs);
        } else return new ArrayList<>();
    }

    @Override
    public String getCurrentCharacter() {
        return mDB.getCurrentCharacter(true);
    }

    @Override
    public ArrayList<OkiSetupDataObject> getListOfSetups(String tableName, String selection) {
        if (mStorage.tableExists(tableName))
            return mStorage.loadData(tableName);
        else
            return null; // TODO: show some kind of warning
    }

    @Override
    public ArrayList<Long> getIDsOfSavedSetups(String tableName) {
        return mStorage.getIDsOfSavedSetups(tableName);
    }

    public void setCurrentSetup(OkiSetupDataObject setup){
        mDB.setCurrentSetup(setup);
    }

    @Override
    public void deleteData(String characterCode, long removedItemID) {
        mStorage.deleteData(characterCode, removedItemID);
    }
}
