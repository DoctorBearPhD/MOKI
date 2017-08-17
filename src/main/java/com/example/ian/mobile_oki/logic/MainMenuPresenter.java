package com.example.ian.mobile_oki.logic;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.SpannedString;

import com.example.ian.mobile_oki.OkiApp;
import com.example.ian.mobile_oki.contracts.MainMenuContract;
import com.example.ian.mobile_oki.data.CharacterDatabase;
import com.example.ian.mobile_oki.data.DatabaseInterface;
import com.example.ian.mobile_oki.data.KDMoveListItem;
import com.example.ian.mobile_oki.data.OkiMoveListItem;
import com.example.ian.mobile_oki.data.OkiSetupDataObject;
import com.example.ian.mobile_oki.data.storage.StorageDbHelper;
import com.example.ian.mobile_oki.data.storage.StorageInterface;
import com.example.ian.mobile_oki.util.OkiUtil;
import com.example.ian.mobile_oki.view.MainActivity;


/**
 * Presenter for the {@link MainActivity}<br/>
 * Created by Ian on 7/2/2017.
 */
public class MainMenuPresenter implements MainMenuContract.Presenter {

//    private final String TAG = this.getClass().getSimpleName();
    private MainMenuContract.View mMainMenuView;
    private final DatabaseInterface mDB;
    private StorageInterface mStorageDbHelper;

    /**
     * Tells whether the Presenter has finished the start() function.
     * Orientation change on startup caused start() to be called twice. This is a countermeasure.
     */
    private boolean STARTING = false;

    MainMenuPresenter(MainMenuContract.View mainMenuView, @NonNull DatabaseInterface db) {
        this.mMainMenuView = mainMenuView;
        this.mDB = db;
        this.mStorageDbHelper = new StorageDbHelper(OkiApp.getContext());
    }

    public static MainMenuPresenter newInstance(MainMenuContract.View view) {
        return new MainMenuPresenter(view, CharacterDatabase.getInstance());
    }

    /**
     * Currently, forces Select screens to start when necessary info hasn't been selected.
     */
    @Override
    public void start() {
        if (isStarting()) return;

        STARTING = true;

        if (!mMainMenuView.hasSelectedCharacter())   mMainMenuView.setCharacterWarningVisible(true);
        else if (!mMainMenuView.hasSelectedKDMove()) mMainMenuView.setKDWarningVisible(true);
        else                                         mMainMenuView.showTimeline();

        STARTING = false;
    }

    @Override
    public void detachView() {
        mMainMenuView = null;
    }

    @Override
    public void attachView(MainMenuContract.View view) {
        mMainMenuView = view;
    }

    /**
     * For handling the possible results of {@link android.app.Activity#startActivityForResult}
     * TODO: Handle RESULT_CANCELED
     *
     * @param requestCode the activity request code
     * @param resultCode  the result code representing the result status of an activity
     *                    (success, canceled, etc.)
     */
    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case MainActivity.CHAR_SEL_REQUEST_CODE:
                    // handle result of character selection
                    mMainMenuView.setCharacterWarningVisible(false);
                      // invalidate values and caches associated with the previous character
                    mMainMenuView.setAndShowKDMove(null);
                    mDB.initializeOkiSlots();
                    mDB.clearOkiMoveListCache();
                    mMainMenuView.showKDMoveSelect();
                    break;
                case MainActivity.KD_MOVE_SEL_REQUEST_CODE:
                    mMainMenuView.setAndShowKDMove(mDB.getCurrentKDMove().getMoveName());
                    mMainMenuView.setKDWarningVisible(false);
                    break;
                case MainActivity.OKI_MOVE_SEL_REQUEST_CODE:
                    int okiSlot = mDB.getCurrentOkiSlot();
                    mMainMenuView.setAndShowOkiMove(mDB.getCurrentOkiMoveAt(okiSlot));
                    break;
            }
        }
    }

    @Override
    public boolean isTimelineReady() {
        return mMainMenuView.hasSelectedCharacter() && mMainMenuView.hasSelectedKDMove();
    }

    /**
     * Tells whether the Presenter has entirely completed the start() function.
     *
     * @return True if both the character and KD move are selected when the presenter "starts."
     */
    @Override
    public boolean isStarting() {
        return this.STARTING;
    }

    @Override
    public SpannedString[] getKDAColumnContent() {
        return OkiUtil.generateKDAdvColumnContent(mDB.getCurrentKDMove());
    }

    @Override
    public OkiMoveListItem getCurrentOkiMoveAt(int okiSlot) {
        return mDB.getCurrentOkiMoveAt(okiSlot);
    }

    @Override
    public SpannedString getOkiColumnContent(int okiSlot, boolean useCurrentRow) {
        int okiRow;
        okiRow = (useCurrentRow) ? mDB.getCurrentRow() : mDB.getOkiRowOfSlot(okiSlot);
        okiRow = (okiRow < 1 ) ? 1 : okiRow; // prevents out of bounds exception from row not being set yet

        return OkiUtil.generateOkiColumnContent(
                okiRow - 1,
                mDB.getCurrentOkiMoveAt(okiSlot));
    }

    @Override
    public int getCurrentRow() {
        return mDB.getCurrentRow();
    }

    @Override
    public void setCurrentRow(int okiRow) {
        mDB.setCurrentRow(okiRow);
    }

    @Override
    public int getCurrentOkiSlot() {
        return mDB.getCurrentOkiSlot();
    }

    @Override
    public void setCurrentOkiSlot(int newOkiSlot) {
        mDB.setCurrentOkiSlot(newOkiSlot);
    }

    @Override
    public String getCurrentCharacter(boolean useFullName) {
        return mDB.getCurrentCharacter(useFullName);
    }

    @Override
    public String getCurrentKDMove() {
        KDMoveListItem kdMove = mDB.getCurrentKDMove();
        return kdMove != null ? kdMove.getMoveName() : null;
    }

    @Override
    public void closeStorageDb() {
        mStorageDbHelper.closeDb();
    }

    @Override
    public boolean timelineNotBlank() {
        boolean containsNonNull = false;

        if (mDB.getCurrentOkiMoves() != null) {
            for (OkiMoveListItem item : mDB.getCurrentOkiMoves()) {
                if (item != null) {
                    containsNonNull = true;
                    break;
                }
            }
        }

        return containsNonNull;
    }

    @Override
    public boolean saveData() {
        // bundle data into object
        OkiSetupDataObject data = new OkiSetupDataObject(
                mDB.getCurrentKDMove().getMoveName(),
                mDB.getCurrentOkiMoves(),
                mDB.getCurrentOkiRows()
        );
        return mStorageDbHelper.saveData(mDB.getCurrentCharacter(false), data);
    }
}
