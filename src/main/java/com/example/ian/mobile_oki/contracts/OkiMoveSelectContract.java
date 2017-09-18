package com.example.ian.mobile_oki.contracts;

import com.example.ian.mobile_oki.BasePresenter;
import com.example.ian.mobile_oki.BaseView;
import com.example.ian.mobile_oki.data.OkiMoveListItem;

import java.util.ArrayList;

/**
 * Created by Ian on 7/3/2017.
 */

public interface OkiMoveSelectContract {
    interface View extends BaseView<Presenter> {

        void attachPresenter();

        void displayOkiMoveList();

        void scrollToCurrentItem(OkiMoveListItem move);
    }

    interface Presenter extends BasePresenter {

        ArrayList<OkiMoveListItem> getListOfOkiMoves();

        void updateCurrentOkiMove(OkiMoveListItem okiMoveListItem);

        void displayFinished();

        void setSortOrder(CharSequence order);
    }
}
