package com.example.ian.mobile_oki.logic;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.example.ian.mobile_oki.contracts.MainMenuContract;
import com.example.ian.mobile_oki.data.DatabaseInterface;
import com.example.ian.mobile_oki.view.MainActivity;


/**
 * Presenter for the {@link MainActivity}<br/>
 * Created by Ian on 7/2/2017.
 */
public class MainMenuPresenter implements MainMenuContract.Presenter{

    private final MainMenuContract.View mMainMenuView;
    private final DatabaseInterface mDB;

    public MainMenuPresenter(@NonNull MainMenuContract.View mainMenuView, @NonNull DatabaseInterface db) {
        this.mMainMenuView = mainMenuView;
        this.mDB = db;

        mMainMenuView.setPresenter(this);
    }

    @Override
    public void start() {
//
//        // ?check selected character status?
        if (mMainMenuView.hasSelectedCharacter()){
//            // proceed
            if (mMainMenuView.hasSelectedKDMove()){
                // proceed
            } else {
                mMainMenuView.showKDMoveSelect();
            }
        } else {
//            // show character select screen
            mMainMenuView.showCharacterSelect();
        }
    }

    /**
     * For handling the possible results of {@link android.app.Activity#startActivityForResult}
     * TODO: Handle RESULT_CANCELED
     * @param requestCode the activity request code
     * @param resultCode the result code representing the result status of an activity
     *                   (success, canceled, etc.)
     */
    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case MainActivity.CHAR_SEL_REQUEST_CODE:
                    // handle result of character selection
                    mMainMenuView.setCharacter(data.getStringExtra(MainActivity.CHARACTER_EXTRA));
                    break;
                case MainActivity.KD_MOVE_SEL_REQUEST_CODE:
                    mMainMenuView.setKDMove(data.getStringExtra(MainActivity.KD_MOVE_EXTRA));
                    break;
            }
        } else if (resultCode == Activity.RESULT_CANCELED){
            // handle canceled character selection
        }
    }

    @Override
    public boolean isTimelineReady() {
        return false;
    }

//    public void onListItemClick(CharacterListItem listItem){
//        mainMenuInterface.finishCharacterSelect(listItem.getCharacterCode());
//    }
}
