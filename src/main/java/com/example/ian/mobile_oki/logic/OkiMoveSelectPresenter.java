package com.example.ian.mobile_oki.logic;


import android.support.annotation.VisibleForTesting;

import com.example.ian.mobile_oki.contracts.OkiMoveSelectContract;
import com.example.ian.mobile_oki.data.CharacterDatabase;
import com.example.ian.mobile_oki.data.DatabaseInterface;
import com.example.ian.mobile_oki.data.OkiMoveListItem;
import com.example.ian.mobile_oki.util.SortOrder;

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
    public ArrayList<OkiMoveListItem> getListOfOkiMoves() {
        return mDB.getOkiMoves(mDB.getOkiSortOrder());
    }

    @Override
    public void updateCurrentOkiMove(OkiMoveListItem okiMoveListItem) {
        int okiSlot = mDB.getCurrentOkiSlot();

        if (okiMoveListItem.getMove().equals("NONE")){
            mDB.setCurrentOkiMove(okiSlot, null);
            mDB.setOkiRowForSlot(okiSlot, 0);
        } else {
            mDB.setCurrentOkiMove(okiSlot, okiMoveListItem);
            mDB.setOkiRowForSlot(okiSlot, mDB.getCurrentRow());
        }
    }

    @Override
    public void displayFinished() {
        mView.scrollToCurrentItem(mDB.getCurrentOkiMoveAt(mDB.getCurrentOkiSlot()));
    }

    @Override
    public void setSortOrder(CharSequence order) {
        String sortValue = "ORDER_" + order.toString().toUpperCase().replace(" ", "_");
        mDB.setOkiSortOrder(SortOrder.valueOf(sortValue));
        mDB.clearOkiMoveListCache();
        mView.displayOkiMoveList();
    }


}
