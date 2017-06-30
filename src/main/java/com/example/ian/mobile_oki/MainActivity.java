package com.example.ian.mobile_oki;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Shortening the name to MOKI, since I had to make another Git repo.
 * <p>
 * TODO: Need to learn about using SQLiteAssetHelper library.
 * <p>
 * TODO : Implement SQLiteAssetHelper
 * <p>
 * TODO: Remove click listener implementation unless it turns out it's needed for the coming button
 * <p>
 * COMPLETED : Implement onSaveInstanceState() to save data between activity recreations
 * (not between application startups, though)
 * <p>
 * TODO: Handle RESULT_CANCELED
 **/
public class MainActivity extends AppCompatActivity {

    // TODO : Create the KDMoveSelectActivity class and layout
    // TODO : Implement the KDMoveSelectActivity into MainActivity

    public static final String EXTRA_MESSAGE = "com.example.ian.MESSAGE";
    private static final int CHAR_SEL_REQUEST_CODE = 6969;
    public static final String CHARACTER_EXTRA = "selected-character";
    private static final String TAG = MainActivity.class.getSimpleName();

    private Bundle mSavedInstanceState;
    private Toast mToast;
    private String mSelectedCharacter;

//    ActivityMainBinding mBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.d(TAG, "onCreate()***");
        setContentView(R.layout.activity_main);

        mSavedInstanceState = savedInstanceState;

        mToast = new Toast(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

        // check if the key is defined
        if (mSavedInstanceState != null) {
            if (mSavedInstanceState.containsKey(CHARACTER_EXTRA)) {
                // get the value and assign accordingly
                Log.d(TAG, "onStart: Setting values from Bundle...");
                mSelectedCharacter = mSavedInstanceState.getString(CHARACTER_EXTRA);
            }
        }

        Log.d(TAG, "onStart: Checking character selection...");

        // Check if character has been selected; open character select if not, otherwise set character
        selectCharacter();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("***onActivityResult", "1. Is there data?");
        switch (resultCode) {
            case RESULT_OK:
                Log.d("onActivityResult", "2. There is data. Setting values...");
                setSelectedCharacter(data.getStringExtra(CHARACTER_EXTRA));

                newCharacterSelected(getSelectedCharacter());
                break;
            case RESULT_CANCELED:
                if (mToast != null) mToast.cancel();
                mToast = Toast.makeText(this, "Character selection canceled!", Toast.LENGTH_SHORT);
                mToast.show();
                Log.d("onActivityResult", "2. RESULT_CANCELED: No data.");
                newCharacterSelected("NULL");
                break;
        }
    }

//    @Override
//    public void onClick(View view) {
//        if(view instanceof Button)
//        {
//            Intent intent = new Intent(this, MoveSelectActivity.class);
//
//            //get button's text
//            String charName = view.getTag().toString();
//
//            Toast.makeText(this, charName, Toast.LENGTH_SHORT).show(); //TODO: remove before release
//
//            //append character's shorthand name to intent
//            intent.putExtra(EXTRA_MESSAGE, charName);
//
//            //go back to timeline
//            startActivity(intent);
//
//            //setContentView(R.layout.activity_move_select);
//        }
//    }


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
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }


    /*----------------*\
    * Helper Functions *
    \*----------------*/

    /**
     * <p>If a character has not been selected, starts a
     * {@link CharacterSelectActivity} for a result, with
     * {@link MainActivity#CHAR_SEL_REQUEST_CODE} as its request code. </p>
     * If a character has been selected, updates the current character. <br/><br/>
     * <p>
     * Called during {@link MainActivity#onCreate(Bundle)}.
     */
    protected void selectCharacter() {
        Log.d("selectCharacter", "1. Is character selected?");
        if (getSelectedCharacter() == null) {
            Log.d("selectCharacter", "2. No character selected, opening Character Select.");
            Intent intent = new Intent(this, CharacterSelectActivity.class);
            startActivityForResult(intent, CHAR_SEL_REQUEST_CODE);
        } else {
            Log.d("selectCharacter", "2. Character is selected.");
            newCharacterSelected(getSelectedCharacter());

        }
    }

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
    private String getSelectedCharacter() {
        return mSelectedCharacter;
    }

    /**
     * Sets the current character.
     *
     * @param character Character to be used.
     */
    private void setSelectedCharacter(String character) {
        mSelectedCharacter = character;
    }


}
