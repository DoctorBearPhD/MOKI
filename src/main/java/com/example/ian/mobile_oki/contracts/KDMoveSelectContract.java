package com.example.ian.mobile_oki.contracts;

import com.example.ian.mobile_oki.BasePresenter;
import com.example.ian.mobile_oki.BaseView;
import com.example.ian.mobile_oki.data.KDMoveListItem;

import java.util.List;

/**
 * Created by Ian on 7/3/2017.
 */

public interface KDMoveSelectContract {

    interface View extends BaseView<Presenter> {

        void cacheKDMoveList(List<KDMoveListItem> kdMoveListItems);

        void displayKDMoveList();
    }

    interface Presenter extends BasePresenter {

        void getListOfKDMoves();
    }
}
