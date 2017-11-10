package com.example.ian.mobile_oki.logic;


import android.support.annotation.VisibleForTesting;

import com.example.ian.mobile_oki.contracts.OkiMoveSelectContract;
import com.example.ian.mobile_oki.data.CharacterDatabase;
import com.example.ian.mobile_oki.data.DatabaseInterface;
import com.example.ian.mobile_oki.data.OkiMoveListItem;
import com.example.ian.mobile_oki.util.ESortOrder;

import java.util.ArrayList;

/**
 * Created by Ian on 8/5/2017.
 */

public class OkiMoveSelectPresenter implements OkiMoveSelectContract.Presenter {

    private final OkiMoveSelectContract.View mView;
    private final DatabaseInterface mDB;

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
        mView.displayOkiMoveList(false);
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
    public CharSequence getSortOrder() {
        ESortOrder sortOrder = mDB.getOkiSortOrder();

        if (sortOrder == null) return "Default";

        return sortOrder.toString().substring(6).replace("_", " ");
    }

    @Override
    public void setSortOrder(CharSequence order) {
        if (getSortOrder().equals(order)) return;

        String sortValue = "ORDER_" + order.toString().toUpperCase().replace(" ", "_");
        mDB.setOkiSortOrder(ESortOrder.valueOf(sortValue));
        mDB.clearOkiMoveListCache();
        mView.displayOkiMoveList(false);
    }

    @Override
    public boolean getOkiDetailLevel() {
        return mDB.getOkiDetailLevel();
    }

    @Override
    public void toggleOkiDetailLevel() {
        mDB.toggleOkiDetailLevel();
    }

}
