package com.example.ian.mobile_oki.logic;


import android.support.annotation.VisibleForTesting;

import com.example.ian.mobile_oki.contracts.OkiMoveSelectContract;
import com.example.ian.mobile_oki.data.CharacterDatabase;
import com.example.ian.mobile_oki.data.DatabaseInterface;
import com.example.ian.mobile_oki.data.OkiMoveListItem;

import java.util.ArrayList;

/**
 * Created by Ian on 8/5/2017.
 */

public class OkiMoveSelectPresenter implements OkiMoveSelectContract.Presenter {

    OkiMoveSelectContract.View mView;
    DatabaseInterface mDB;

    public OkiMoveSelectPresenter(OkiMoveSelectContract.View view){
        mView = view;
        mDB = CharacterDatabase.getInstance();
    }

    @VisibleForTesting
    OkiMoveSelectPresenter(OkiMoveSelectContract.View view, DatabaseInterface db){
        mView = view;
        mDB = db;
    }

    public void start() {
        mView.displayOkiMoveList();
    }

    @Override
    public ArrayList<OkiMoveListItem> getListOfOkiMoves(String codeName) {
        return mDB.getOkiMoves(codeName);
    }

    @Override
    public void updateCurrentOkiMove(int position, OkiMoveListItem okiMoveListItem) {
        mDB.setCurrentOkiMove(position, okiMoveListItem);
    }
}
