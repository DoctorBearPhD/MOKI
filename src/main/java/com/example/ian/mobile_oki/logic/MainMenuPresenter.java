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
    private final StorageInterface mStorageDbHelper;

    /**
     * Tells whether the Presenter has finished the start() function.
     * Orientation change on startup caused start() to be called twice. This is a countermeasure.
     */
    private boolean STARTING = false;

    private final SpannedString mEmptyColumnContent = OkiUtil.generateEmptyColumnContent();

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
                    mDB.setCurrentKDMove(null);
                    mDB.initializeOkiSlots();
                    mDB.clearCharacterListCache();
                    mDB.clearKDMoveListCache(); // cached list of possible kd moves is invalid
                    mDB.clearOkiMoveListCache(); // cached list of possible oki moves is invalid
                    mMainMenuView.showKDMoveSelect();
                    break;
                case MainActivity.KD_MOVE_SEL_REQUEST_CODE:
                    mMainMenuView.setKDWarningVisible(false);
                    mDB.invalidateKda(); // informs view that kda columns' contents need to update
                    // ask to clear all slots if they're not empty
                    if (!mDB.isCurrentOkiMovesListEmpty())
                        mMainMenuView.showClearOkiSlotsDialogue();
                    break;
                case MainActivity.OKI_MOVE_SEL_REQUEST_CODE:
                    int okiSlot = mDB.getCurrentOkiSlot();
                    mMainMenuView.updateOkiColumn(okiSlot, true);
                    break;
                case MainActivity.LOAD_ACTIVITY_REQUEST_CODE:
                    mMainMenuView.setCharacterWarningVisible(false);
                    mMainMenuView.setKDWarningVisible(false);
            }
        }
        if (requestCode == MainActivity.KD_MOVE_SEL_REQUEST_CODE)
            mDB.clearKDMoveListCache(); // don't need the list frequently; too much memory to keep
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
        // if the content has already been retrieved and does not need to be updated...
        // i.e. if kd move is 'dirty', get new content
        if (mDB.isKdaDirty()) {
            mDB.setKdaContent(OkiUtil.generateKDAdvColumnContent(mDB.getCurrentKDMove()));
            mDB.validateKda(); // kda content is now up-to-date (or will be)
        }
        return mDB.getKdaContent();
    }

    @Override
    public OkiMoveListItem getCurrentOkiMoveAt(int okiSlot) {
        return mDB.getCurrentOkiMoveAt(okiSlot);
    }

    /**
     * @param okiSlot       The number of the Oki Slot into which the content will be inserted.
     * @param useCurrentRow Specify whether to use either the currently selected row (true) or
     *                      the row that is already being used at that Oki Slot (false).
     * @return The visualization of the Oki Move's frame data or an empty column if there's no move.
     */
    @Override
    public SpannedString getOkiColumnContent(int okiSlot, boolean useCurrentRow) {
        OkiMoveListItem move = mDB.getCurrentOkiMoveAt(okiSlot);

        if (move == null)
            return mEmptyColumnContent;

        int okiRow = (useCurrentRow) ? mDB.getCurrentRow() : mDB.getOkiRowOfSlot(okiSlot);
        if (okiRow < 1) okiRow = 1; // prevents out of bounds exception from row not being set

        return OkiUtil.generateOkiColumnContent(okiRow - 1, move);
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
    public boolean clearCurrentOkiSlot() {
        int currentOkiSlot = getCurrentOkiSlot();

        mDB.setCurrentOkiMove(currentOkiSlot, null);
        mDB.setOkiRowForSlot( currentOkiSlot, 0);

        // update Timeline to show cleared slot
        mMainMenuView.updateOkiColumn(currentOkiSlot, false);
        // update Current Oki Setup Drawer
        mMainMenuView.updateCurrentOkiDrawer();

        return true;
    }

    @Override
    public boolean clearAllOkiSlots() {
        mDB.initializeOkiSlots();

        // update Timeline to show cleared slots
        mMainMenuView.updateAllOkiColumns();
        // update Current Oki Setup Drawer
        mMainMenuView.updateCurrentOkiDrawer();

        return true;
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
    public boolean isTimelineBlank() {
        return mDB.isCurrentOkiMovesListEmpty();
    }

    @Override
    public boolean saveData() {
        // bundle data into object
        OkiSetupDataObject data = new OkiSetupDataObject(
                mDB.getCurrentCharacter(true),
                mDB.getCurrentKDMove().getMoveName(),
                mDB.getCurrentOkiMoves(),
                mDB.getCurrentOkiRows()
        );
        return mStorageDbHelper.saveData(mDB.getCurrentCharacter(false), data);
    }

    /**
     * Equation: <br/>
     * <i>KDRA - (currentRow - 1)</i>
     * <p/>
     * This represents how many frames until the opponent's first wake-up frame.<br/>
     *
     * @return The amount of frames from the current row to the opponent's first (KDR) wake-up frame.
     */
    @Override
    public int frameKillKDR() {
        return mDB.getCurrentKDMove().getKdra() - (mDB.getCurrentRow() - 1);
    }

    @Override
    public int frameKillKDBR() {
        return mDB.getCurrentKDMove().getKdbra() - (mDB.getCurrentRow() - 1);
    }

    @Override
    public int frameKillKD() {
        return mDB.getCurrentKDMove().getKda() - (mDB.getCurrentRow() - 1);
    }

    @Override
    public void moveOkiMove() {
        if (mDB.getCurrentOkiMoveAt(getCurrentOkiSlot()) != null)
            mDB.moveOkiMove();
    }
}
