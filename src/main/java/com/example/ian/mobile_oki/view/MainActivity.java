package com.example.ian.mobile_oki.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannedString;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.ian.mobile_oki.OkiApp;
import com.example.ian.mobile_oki.R;
import com.example.ian.mobile_oki.contracts.MainMenuContract;
import com.example.ian.mobile_oki.data.KDMoveListItem;
import com.example.ian.mobile_oki.data.OkiUtil;
import com.example.ian.mobile_oki.databinding.TimelineBodyRowBinding;
import com.example.ian.mobile_oki.logic.MainMenuPresenter;

/**
 * Shortening the name to MOKI, since I had to make another Git repo.
 *
 * Despite all my efforts, there seems to be no way to allow orientation changes AND remain bug-free.
 * Orientation changes during the app's startup or between activities causes the character/kd to not be
 * properly set or causes them to reset to null. Even debugging line by line, there is no visible reason
 * for why they are being reset. I'm forced to just lock orientation change. I simply can't figure it out.
 *
 * <p>
 * TODO: Lock orientation to portrait mode to [avoid/contain/quarantine] the orientation change bugs. (This won't solve keyboard config change...)
 * <p>
 * TODO: Build test classes which can handle/test the Activities.
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
    public static final int MAX_TIMELINE_FRAMES = 120;

    private static final String TAG = MainActivity.class.getSimpleName();

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

//        mToast = new Toast(getApplicationContext());

        // get or create presenter instance, which will in turn set this view's presenter
        setPresenter((MainMenuPresenter) getLastCustomNonConfigurationInstance());

        // get and hide timeline
        mTimeline = (TableLayout) findViewById(R.id.tbl_timeline);
        mTimeline.setVisibility(View.INVISIBLE);

        // restore previous state, if available
        if (savedInstanceState!=null){
            //set data
            setAndShowCharacter(savedInstanceState.getString(CHARACTER_EXTRA));
            setAndShowKDMove(savedInstanceState.getString(KD_MOVE_EXTRA));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(CHARACTER_EXTRA) && getSelectedCharacter() == null)
            setAndShowCharacter(savedInstanceState.getString(CHARACTER_EXTRA));
        if (savedInstanceState.containsKey(KD_MOVE_EXTRA) && getSelectedKDMove() == null)
            setAndShowKDMove(savedInstanceState.getString(KD_MOVE_EXTRA));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mMainMenuPresenter.handleResult(requestCode, resultCode, data);
        // If an orientation change occurs,
        // Character and KD Move are null after coming out of method for no reason!
        // Even though during the method, they are verified as being set! WHAT?
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Select screens should have sent info back by this point. (if they started)
        mMainMenuPresenter.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
    public Object onRetainCustomNonConfigurationInstance() {
        return mMainMenuPresenter;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mMainMenuPresenter.detachView();

        super.onDestroy();
    }

    /*------------------------*\
    * View Interface Functions *
    \*------------------------*/

    @Override
    public void setPresenter(MainMenuContract.Presenter presenter) {

        if (presenter == null)
            mMainMenuPresenter = MainMenuPresenter.newInstance(this);
        else
            mMainMenuPresenter = presenter;

        mMainMenuPresenter.attachView(this);
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
        Intent intent = new Intent(OkiApp.getContext(), CharacterSelectActivity.class);

        startActivityForResult(intent, CHAR_SEL_REQUEST_CODE);
    }

    /**
     * Interface method for setting the Character. Allows the Presenter to tell the View what to set.
     * @param character Character's 3-letter code for accessing its database table
     */
    @Override
    public void setAndShowCharacter(String character) {
        if (character != null) setSelectedCharacter(character);
        // temp
        ((TextView) findViewById(R.id.tv_temp)).setText(getSelectedCharacter());
    }

    @Override
    public void showKDMoveSelect() {
        Intent intent = new Intent(OkiApp.getContext(), KDMoveSelectActivity.class);
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
    public void setAndShowKDMove(String kdMove) {
        if (kdMove != null) setSelectedKDMove(kdMove);

        //temp
        String tvText = ((TextView) findViewById(R.id.tv_temp)).getText().toString();
        tvText = tvText + "\n" + getSelectedKDMove();
        ((TextView) findViewById(R.id.tv_temp)).setText(tvText);
    }

    /**
     * Shows timeline if hidden.<br/>
     * {@code rowBinding} gives access to the generated Data Binding for the timeline's body
     * <p>
     * TODO: Change to updateTimeline()
     */
    @Override
    public void showTimeline() {
//        ViewStub vs = (ViewStub) findViewById(R.id.viewStub_timeline);
//        if (vs != null)
//            mTimeline = (TableLayout) ( vs.inflate() );
//        else {
//            mTimeline = (TableLayout) findViewById(R.id.tbl_timeline);
//        }
        if (mTimeline != null) {
            mTimeline.setVisibility(View.VISIBLE);

            // Fill in timeline

            View body = mTimeline.findViewById(R.id.tr_body);
            // generate column contents (TextViews)
            TimelineBodyRowBinding rowBinding =
                    DataBindingUtil.bind(body);

             // update KDA columns
            rowBinding.tvBodyFramesTens.setHorizontallyScrolling(true);

            // get formatted text from presenter
             // (SpannedStrings allow multiple colors and styles in one TextView)
            SpannedString[] formattedTextValues = mMainMenuPresenter.getKDAColumnContent();

            rowBinding.tvBodyKd.setText(formattedTextValues[0]);
            rowBinding.tvBodyKdr.setText(formattedTextValues[1]);
            rowBinding.tvBodyKdbr.setText(formattedTextValues[2]);
        }
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
