package com.example.ian.mobile_oki.contracts;

import com.example.ian.mobile_oki.BasePresenter;
import com.example.ian.mobile_oki.BaseView;
import com.example.ian.mobile_oki.data.CharacterListItem;
import com.example.ian.mobile_oki.data.OkiSetupDataObject;

import java.util.ArrayList;

/**
 * Created by Ian on 8/17/2017.
 */

public interface LoadDataContract {
    interface View extends BaseView<Presenter> {

        void populateCharacterSpinner();

        void updateListOfSetups(int index);
    }

    interface Presenter extends BasePresenter {

        void detachView();

        ArrayList<CharacterListItem> getCharacters();

        String getCurrentCharacter();

        ArrayList<OkiSetupDataObject> getListOfSetups(String tableName, String selection);

        ArrayList<Long> getIDsOfSavedSetups(String tableName);

        void setCurrentSetup(OkiSetupDataObject setup);

        void deleteData(String characterCode, long removedItemID);

        ArrayList<String> getKDCommands(String character, ArrayList<String> kdMovesList);
    }
}
