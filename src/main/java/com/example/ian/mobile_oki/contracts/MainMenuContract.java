package com.example.ian.mobile_oki.contracts;

import android.content.Intent;
import android.text.SpannedString;

import com.example.ian.mobile_oki.BasePresenter;
import com.example.ian.mobile_oki.BaseView;
import com.example.ian.mobile_oki.data.OkiMoveListItem;

/**
 * <p>Interface for Main Menu and MainMenuPresenter
 * <p>
 * Created by Ian on 7/2/2017.
 */

public interface MainMenuContract {

    interface View extends BaseView<Presenter>{

        boolean hasSelectedCharacter();

        boolean hasSelectedKDMove();

        void showCharacterSelect();

        void setAndShowCharacter(String character);

        void showKDMoveSelect();

        void setAndShowKDMove(String kdMove);

        void showOkiMoveSelect();

        void setAndShowOkiMove(OkiMoveListItem okiMove);

        void showTimeline();

        void hideTimeline();

        void setCharacterWarningVisible(boolean visible);

        void setKDWarningVisible(boolean visible);
    }

    interface Presenter extends BasePresenter {

        void attachView(View view);

        void detachView();

        void handleResult(int requestCode, int resultCode, Intent intent);

        boolean isTimelineReady();

        boolean isStarting();

        SpannedString[] getKDAColumnContent();

        OkiMoveListItem getCurrentOkiMoveAt(int okiSlot);

        SpannedString getOkiColumnContent(int okiSlot, boolean useCurrentRow);

        int getCurrentRow();

        void setCurrentRow(int okiRow);

        int getCurrentOkiSlot();

        void setCurrentOkiSlot(int newOkiSlot);
    }
}
