package com.example.ian.mobile_oki.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ian.mobile_oki.R;
import com.example.ian.mobile_oki.contracts.MainMenuContract;
import com.example.ian.mobile_oki.data.CharacterDatabase;
import com.example.ian.mobile_oki.logic.MainMenuPresenter;

// TODO: Remove logic from View Layer

// TODO : Create the KDMoveSelectActivity class and layout
// TODO : Implement the KDMoveSelectActivity into MainActivity

/**
 * Shortening the name to MOKI, since I had to make another Git repo.
 * <p>
 * <ol>
 * <li>Program starts.
 * <li>MainActivity creates a presenter, and tells the presenter to initialize the program.
 * <li>MainMenuPresenter tells MainActivity to start the character select activity.
 * <li>MainActivity requests a list of character names and 3-letter character codes from the database,
 * then sends that list to the character select activity.
 * <li>User selects a character name, and character select activity sends the character code to MainActivity.
 * <li>MainActivity uses the code to request the specified character's knockdown moves, then sends
 * that list to the KD move select activity.
 * <li>User selects a KD move, and KD move select activity sends it back to MainActivity.
 * </ol>
 * <p>
 * TODO: Remove click listener implementation unless it turns out it's needed for the coming button
 * <p>
 **/
public class MainActivity extends AppCompatActivity implements MainMenuContract.View {

    public static final String EXTRA_MESSAGE = "com.example.ian.MESSAGE";
    public static final int CHAR_SEL_REQUEST_CODE = 6969;
    public static final String CHARACTER_EXTRA = "selected-character";
    private static final String TAG = MainActivity.class.getSimpleName();

    private Bundle mSavedInstanceState;
    private Toast mToast;
    private String mSelectedCharacter;

    private MainMenuContract.Presenter mMainMenuPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSavedInstanceState = savedInstanceState;

        mToast = new Toast(getApplicationContext());


        // create presenter, which will in turn set this view's presenter
        mMainMenuPresenter = new MainMenuPresenter(this, new CharacterDatabase(this));
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
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mMainMenuPresenter.result(requestCode, resultCode, data);
        setSelectedCharacter(data.getStringExtra(CHARACTER_EXTRA));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //        Log.d(TAG, "onResume()");

        // Character Select should have sent the info back by this point.
        mMainMenuPresenter.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the character to the outState so we don't lose the selected character
        //  (i.e. on orientation change, etc.)
        String character = getSelectedCharacter();
        if (character != null) {
            //            Log.d("onSaveInstanceState", "Saving instance state to Bundle...");
            outState.putString(CHARACTER_EXTRA, character);
            //            Log.d("onSaveInstanceState", "Selected character saved");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    /*-------------------*\
    * Presenter Functions *
    \*-------------------*/

    @Override
    public void setPresenter(MainMenuContract.Presenter presenter) {
        if (mMainMenuPresenter == null)
            mMainMenuPresenter = presenter;
    }

    /*------------------------*\
    * View Interface Functions *
    \*------------------------*/

    @Override
    public boolean hasSelectedCharacter() {
        return mSelectedCharacter != null;
    }

    /**
     * Character Select starts here...
     */
    @Override
    public void showCharacterSelect() {
        Intent intent = new Intent(getApplicationContext(), CharacterSelectActivity.class);
        startActivityForResult(intent, CHAR_SEL_REQUEST_CODE);
    }

    @Override
    public void setCharacter(String stringExtra) {
        setSelectedCharacter(stringExtra);

        // temp
        ((TextView) findViewById(R.id.tv_temp)).setText(mSelectedCharacter);
    }

    //

    /*----------------*\
    * Helper Functions *
    \*----------------*/

    /**
     * A new character was selected and actions need to be performed.
     * Should also be triggered when device orientation has changed
     * (or any other time the activity is destroyed and recreated).<br/><br/>
     * (Should this be triggered by an event?)
     */
    private void newCharacterSelected(String character) {
        // Do something...

        // TEMPORARY
        Log.d("newCharacterSelected", "Setting TextView text.");
        ((TextView) findViewById(R.id.tv_temp)).setText(character);
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

    /**
     * Sets the current character.
     *
     * @param character Character to be used.
     */
    public void setSelectedCharacter(String character) {
        mSelectedCharacter = character;
    }
}
