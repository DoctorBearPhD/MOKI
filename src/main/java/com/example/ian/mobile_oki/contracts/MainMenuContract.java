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

        void showKDMoveSelect();

        void showOkiMoveSelect();

        void showLoadActivity();

        void showTimeline();

        void hideTimeline();

        void updateAllOkiColumns();

        void updateOkiColumn(int okiSlot, boolean useCurrentRow);

        void updateCurrentOkiDrawer();

        void showClearOkiSlotsDialogue();

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

        boolean clearCurrentOkiSlot();

        boolean clearAllOkiSlots();

        String getCurrentCharacter(boolean fullName);

        String getCurrentKDMove();

        void closeStorageDb();

        boolean isTimelineBlank();

        boolean saveData();
    }
}
