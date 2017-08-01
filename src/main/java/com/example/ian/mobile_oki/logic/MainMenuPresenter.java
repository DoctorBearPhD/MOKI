package com.example.ian.mobile_oki.logic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.example.ian.mobile_oki.contracts.MainMenuContract;
import com.example.ian.mobile_oki.data.CharacterDatabase;
import com.example.ian.mobile_oki.data.DatabaseInterface;
import com.example.ian.mobile_oki.view.MainActivity;


/**
 * Presenter for the {@link MainActivity}<br/>
 * Created by Ian on 7/2/2017.
 */
public class MainMenuPresenter implements MainMenuContract.Presenter {

    private final String TAG = this.getClass().getSimpleName();
    private static MainMenuPresenter INSTANCE;
    private MainMenuContract.View mMainMenuView;
    private final DatabaseInterface mDB; // TODO: Will probably end up removing database reference from this class

    /**
     * Tells whether the Presenter has finished the start() function.
     * Orientation change on startup caused start() to be called twice. This is a countermeasure.
     */
    private boolean STARTING = false;

    MainMenuPresenter(MainMenuContract.View mainMenuView, @NonNull DatabaseInterface db) {
        this.mMainMenuView = mainMenuView;
        this.mDB = db;
    }

    public static MainMenuPresenter newInstance(MainMenuContract.View view, Context context) {
        return new MainMenuPresenter(view, CharacterDatabase.getInstance(context));
    }

    /**
     * Currently, forces Select screens to start when necessary info hasn't been selected.
     */
    @Override
    public void start() {
        if (isStarting()) return;

        STARTING = true;

        if (!mMainMenuView.hasSelectedCharacter())   mMainMenuView.showCharacterSelect();
        else if (!mMainMenuView.hasSelectedKDMove()) mMainMenuView.showKDMoveSelect();
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
     * TODO: See if you can replace the Intent with a String
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
                    mMainMenuView.setAndShowCharacter(data.getStringExtra(MainActivity.CHARACTER_EXTRA));
                    break;
                case MainActivity.KD_MOVE_SEL_REQUEST_CODE:
                    mMainMenuView.setAndShowKDMove(data.getStringExtra(MainActivity.KD_MOVE_EXTRA));
                    break;
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // handle canceled character selection
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
}