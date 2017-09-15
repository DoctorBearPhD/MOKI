package com.example.ian.mobile_oki.contracts;

import com.example.ian.mobile_oki.BasePresenter;
import com.example.ian.mobile_oki.BaseView;
import com.example.ian.mobile_oki.data.CharacterListItem;

import java.util.ArrayList;

/**
 * Created by Ian on 7/3/2017.
 */
public interface CharacterSelectContract {

    interface View extends BaseView<Presenter> {

        void showListOfNames();

        void scrollToCurrentItem(String currentCharacter);
    }

    interface Presenter extends BasePresenter {
        ArrayList<CharacterListItem> fetchListOfNames();

        void setCurrentCharacter(String codeName, String fullName);

        void displayFinished();
    }
}