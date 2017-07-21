package com.example.ian.mobile_oki.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewStub;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.ian.mobile_oki.R;
import com.example.ian.mobile_oki.contracts.MainMenuContract;
import com.example.ian.mobile_oki.logic.MainMenuPresenter;

// TODO : Integrate the KDMoveSelectActivity into MainActivity
// TODO***: CharacterDatabase should fetch list of KD moves for KDMoveSelectActivity
/**
 * Shortening the name to MOKI, since I had to make another Git repo.
 * <p>
 * TODO: Remove click listener implementation unless it turns out it's needed for the coming buttons
 * <p>
 **/
public class MainActivity extends AppCompatActivity implements MainMenuContract.View {

//    public static final String EXTRA_MESSAGE = "com.example.ian.MESSAGE";
    public static final int CHAR_SEL_REQUEST_CODE = 6969;
    public static final int KD_MOVE_SEL_REQUEST_CODE = 8008;
    public static final String CHARACTER_EXTRA = "selected-character";
    public static final String KD_MOVE_EXTRA = "selected-kd-move";

//    private static final String TAG = MainActivity.class.getSimpleName();

    private Bundle mSavedInstanceState;
//    private Toast mToast;

    /**
     * The currently selected character.
     * <p>Holds the 3-letter character code corresponding to a database table name.
     * <p><i>(e.g. Alex = ALX)</i>
     */
    private String mSelectedCharacter;

    /**
     * The currently selected Knockdown Move.
     * <p>Holds the entire move name as listed in the database.
     */
    private String mSelectedKDMove;

    private MainMenuContract.Presenter mMainMenuPresenter;

    private TableLayout mTimeline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSavedInstanceState = savedInstanceState;

//        mToast = new Toast(getApplicationContext());

        // get presenter instance, which will in turn set this view's presenter
        mMainMenuPresenter = MainMenuPresenter.getInstance(this, getApplicationContext());
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // check if there is a savedInstanceState (is this the program start? or restored?)
        if (mSavedInstanceState != null) {
            if (mSavedInstanceState.containsKey(CHARACTER_EXTRA) && getSelectedCharacter() == null)
                setSelectedCharacter(mSavedInstanceState.getString(CHARACTER_EXTRA));
            if (mSavedInstanceState.containsKey(KD_MOVE_EXTRA) && getSelectedKDMove() == null)
                setSelectedKDMove(mSavedInstanceState.getString(KD_MOVE_EXTRA));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mMainMenuPresenter.handleResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Character Select should have sent the info back by this point. (if it was started)

        // start the presenter if it hasn't started
        if (!mMainMenuPresenter.hasCompletedStartup())
            mMainMenuPresenter.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the user's selections
        //  (i.e. on orientation change, etc.)
        String character = getSelectedCharacter();
        if (character != null)
            outState.putString(CHARACTER_EXTRA, character);

        String kdMove = getSelectedKDMove();
        if (kdMove != null)
            outState.putString(KD_MOVE_EXTRA, kdMove);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Log.d(TAG, "onStop()");
    }

    /*------------------------*\
    * View Interface Functions *
    \*------------------------*/

    @Override
    public void setPresenter(MainMenuContract.Presenter presenter) {
        if (mMainMenuPresenter == null)
            mMainMenuPresenter = presenter;
    }

    @Override
    public boolean hasSelectedCharacter() {
        return getSelectedCharacter() != null;
    }

    @Override
    public boolean hasSelectedKDMove() {
        return getSelectedKDMove() != null;
    }

    /**
     * Character Select starts here...
     */
    @Override
    public void showCharacterSelect() {
        Intent intent = new Intent(getApplicationContext(), CharacterSelectActivity.class);
        startActivityForResult(intent, CHAR_SEL_REQUEST_CODE);
    }

    /**
     * Interface method for setting the Character. Allows the Presenter to tell the View what to set.
     * @param character Character's 3-letter code for accessing its database table
     */
    @Override
    public void setCharacter(String character) {
        setSelectedCharacter(character);

        // temp
        ((TextView) findViewById(R.id.tv_temp)).setText(getSelectedCharacter());
    }

    @Override
    public void showKDMoveSelect() {
        Intent intent = new Intent(getApplicationContext(), KDMoveSelectActivity.class);
        // pass the character code to the activity so its presenter can query the database
        intent.putExtra(CHARACTER_EXTRA, getSelectedCharacter());
        // start the KDMoveSelectActivity
        startActivityForResult(intent, KD_MOVE_SEL_REQUEST_CODE);
    }

    /**
     * Interface method for setting the KD Move. Allows the Presenter to tell the View what to set.
     * @param kdMove Name of the Move
     */
    @Override
    public void setKDMove(String kdMove) {
        setSelectedKDMove(kdMove);

        //temp
        String tvText = ((TextView) findViewById(R.id.tv_temp)).getText().toString();
        tvText = tvText + "\n" + getSelectedKDMove();
        ((TextView) findViewById(R.id.tv_temp)).setText(tvText);
    }

    @Override
    public void showTimeline() {
        mTimeline = (TableLayout) (( (ViewStub) findViewById(R.id.viewStub_timeline) ).inflate());
    }


    /*-----------------*\
    * Getters / Setters *
    \*-----------------*/

    /**
     * @return {@link MainActivity#mSelectedCharacter}
     */
    public String getSelectedCharacter() {
        return mSelectedCharacter;
    }

    public void setSelectedCharacter(String character) {
        mSelectedCharacter = character;
    }

    public String getSelectedKDMove() {
        return mSelectedKDMove;
    }

    public void setSelectedKDMove(String kdMove) {
        mSelectedKDMove = kdMove;
    }
}
