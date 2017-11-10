package com.example.ian.mobile_oki.logic;

import com.example.ian.mobile_oki.contracts.KDMoveSelectContract;
import com.example.ian.mobile_oki.data.DatabaseInterface;
import com.example.ian.mobile_oki.data.KDMoveListItem;
import com.example.ian.mobile_oki.util.ESortOrder;

import java.util.ArrayList;

/**
 * Created by Ian on 7/7/2017.
 */

public class KDMoveSelectPresenter implements KDMoveSelectContract.Presenter {

    private final KDMoveSelectContract.View mView;
    private final DatabaseInterface mDB;

    public KDMoveSelectPresenter(KDMoveSelectContract.View kdmsView, DatabaseInterface db) {
        mView = kdmsView;
        mDB   = db;
    }

    @Override
    public void start() {
        //get going!
        mView.displayKDMoveList(false);
    }

    @Override
    public ArrayList<KDMoveListItem> getListOfKDMoves() {
        return mDB.getKDMoves(mDB.getKdSortOrder());
    }

    @Override
    public KDMoveListItem getCurrentKDMove() {
        return mDB.getCurrentKDMove();
    }

    @Override
    public boolean updateCurrentKDMove(KDMoveListItem kdMoveListItem) {
        boolean changed = !kdMoveListItem.equals(mDB.getCurrentKDMove());

        if (changed)
            mDB.setCurrentKDMove(kdMoveListItem);

        return changed;
    }

    @Override
    public void displayFinished() {
        if (mDB.getCurrentKDMove() != null)
            mView.scrollToCurrentItem(mDB.getCurrentKDMove());
    }

    @Override
    public CharSequence getSortOrder() {
        ESortOrder sortOrder = mDB.getKdSortOrder();

        if (sortOrder == null) return "Default";

        return sortOrder.toString().substring(6).replace("_", " ");
    }

    @Override
    public void setSortOrder(CharSequence order) {
        if (getSortOrder().equals(order)) return;

        String sortValue = "ORDER_" + order.toString().toUpperCase().replace(" ", "_");
        mDB.setKdSortOrder(ESortOrder.valueOf(sortValue));
        mDB.clearKDMoveListCache();
        mView.displayKDMoveList(false);
    }

    @Override
    public boolean getKdDetailLevel() {
        return mDB.getKdDetailLevel();
    }

    @Override
    public void toggleKdDetailLevel() {
        mDB.toggleKdDetailLevel();
    }
}
