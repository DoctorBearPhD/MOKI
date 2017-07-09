package com.example.ian.mobile_oki.contracts;

import com.example.ian.mobile_oki.BasePresenter;
import com.example.ian.mobile_oki.BaseView;
import com.example.ian.mobile_oki.data.CharacterListItem;

import java.util.List;

/**
 * Created by Ian on 7/3/2017.
 */
public interface CharacterSelectContract {

    interface View extends BaseView<Presenter> {

        void showListOfNames();
    }

    interface Presenter extends BasePresenter {

        List<CharacterListItem> fetchListOfNames();
    }
}