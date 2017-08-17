package com.example.ian.mobile_oki.contracts;

import com.example.ian.mobile_oki.BasePresenter;
import com.example.ian.mobile_oki.BaseView;
import com.example.ian.mobile_oki.data.CharacterListItem;

import java.util.ArrayList;

/**
 * Created by Ian on 8/17/2017.
 */

public interface LoadDataContract {
    interface View extends BaseView<Presenter> {

        void populateCharacterSpinner();
    }

    interface Presenter extends BasePresenter {

        void detachView();

        ArrayList<CharacterListItem> getCharacters();

        String getCurrentCharacter();
    }
}
