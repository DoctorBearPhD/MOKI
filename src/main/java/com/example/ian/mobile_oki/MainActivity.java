package com.example.ian.mobile_oki;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import static com.example.ian.mobile_oki.CharacterSelectActivity.CHARACTER_EXTRA;

/**
 * Shortening the name to MOKI, since I had to make another Git repo.
 * <p>
 * TODO: Need to learn about using SQLAssetHelper library.
 * <p>
 * TODO: Remove click listener implementation unless it turns out it's needed for the coming button
 * <p>
 * TODO : Implement SQLAssetHelper
 * <p>
 * COMPLETED : Implement onSaveInstanceState() to save data between activity recreations
 *              (not between application startups, though)
 **/
public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.ian.MESSAGE";
    private static final int CHAR_SEL_REQUEST_CODE = 6969;

    private String mSelectedCharacter;

//    ActivityMainBinding mBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Lifecycle-Update", "onCreate()***");
        setContentView(R.layout.activity_main);

//        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Check if bundle is there; if it is, assign its values
        checkBundleInfo(savedInstanceState);

        // Check if character has been selected; open character select if not, otherwise set character
        isCharacterSelected();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the character to the outState so we don't lose the selected character
        //  (i.e. on orientation change, etc.)
        String character = getSelectedCharacter();
        if (character != null) {
            outState.putString(CHARACTER_EXTRA, character);
            Log.d("onSaveInstanceState", "Selected character saved");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Lifecycle-Update", "onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Lifecycle-Update", "onStop()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Lifecycle-Update", "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Lifecycle-Update", "onResume()");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            setSelectedCharacter(data.getStringExtra(CHARACTER_EXTRA));
            newCharacterSelected();
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


    /*----------------*\
    * Helper Functions *
    \*----------------*/

    /**
     * Check if there is any data in the savedInstanceState bundle.
     * If there is data, assign values accordingly. <br/><br/>
     * <p>
     * Called during {@link MainActivity#onCreate(Bundle)}.
     *
     * @param savedInstanceState The bundle containing the data to save.
     */
    protected void checkBundleInfo(Bundle savedInstanceState) {
        if (savedInstanceState == null) return; // don't bother when nothing is here

        // check if the key is defined
        if (savedInstanceState.containsKey(CHARACTER_EXTRA)) {
            // get the value and assign accordingly
            mSelectedCharacter = savedInstanceState.getString(CHARACTER_EXTRA);
        }

    }

    /**
     * <p>If a character has not been selected, starts a
     * {@link CharacterSelectActivity} for a result, with
     * {@link MainActivity#CHAR_SEL_REQUEST_CODE} as its request code. </p>
     * If a character has been selected, updates the current character. <br/><br/>
     * <p>
     * Called during {@link MainActivity#onCreate(Bundle)}.
     */
    protected void isCharacterSelected() {
        if (getSelectedCharacter() == null) {
            Log.d("onCreate", "No character selected, opening Character Select.");
            Intent intent = new Intent(this, CharacterSelectActivity.class);
            startActivityForResult(intent, CHAR_SEL_REQUEST_CODE);
        } else newCharacterSelected();
    }

    /**
     * A new character was selected and actions need to be performed.
     * Should also be triggered when device orientation has changed
     * (or any other time the activity is destroyed and recreated).<br/><br/>
     * (Should this be triggered by an event?)
     */
    private void newCharacterSelected() {
        // Do something...

        // TEMPORARY
        ((TextView) findViewById(R.id.tv_temp)).setText(getSelectedCharacter());
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
