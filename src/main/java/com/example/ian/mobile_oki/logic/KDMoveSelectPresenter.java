package com.example.ian.mobile_oki.logic;

import com.example.ian.mobile_oki.contracts.KDMoveSelectContract;
import com.example.ian.mobile_oki.data.DatabaseInterface;
import com.example.ian.mobile_oki.data.KDMoveListItem;

/**
 * Created by Ian on 7/7/2017.
 */

public class KDMoveSelectPresenter implements KDMoveSelectContract.Presenter {

    private KDMoveSelectContract.View mView;
    private DatabaseInterface mDB;

    public KDMoveSelectPresenter(KDMoveSelectContract.View kdmsView, DatabaseInterface db) {
        mView = kdmsView;
        mDB   = db;
    }

    @Override
    public void start() {
        //get going!
        mView.displayKDMoveList();
    }

    @Override
    public void getListOfKDMoves(String codeName) {
        mView.cacheKDMoveList(mDB.getKDMoves(codeName));
    }

    @Override
    public void updateCurrentKDMove(KDMoveListItem kdMoveListItem) {
        mDB.setCurrentKDMove(kdMoveListItem);
    }


}
