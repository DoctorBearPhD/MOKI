package com.example.ian.mobile_oki.contracts;

import com.example.ian.mobile_oki.BasePresenter;
import com.example.ian.mobile_oki.BaseView;
import com.example.ian.mobile_oki.data.KDMoveListItem;

import java.util.ArrayList;

/**
 * Created by Ian on 7/3/2017.
 */

public interface KDMoveSelectContract {

    interface View extends BaseView<Presenter> {

        void displayKDMoveList();

        void scrollToCurrentItem(KDMoveListItem move);
    }

    interface Presenter extends BasePresenter {
        ArrayList<KDMoveListItem> getListOfKDMoves();

        KDMoveListItem getCurrentKDMove();

        void updateCurrentKDMove(KDMoveListItem kdMoveListItem);

        void displayFinished();

        CharSequence getSortOrder();

        void setSortOrder(CharSequence sortOrder);
    }
}
