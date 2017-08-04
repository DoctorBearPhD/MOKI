package com.example.ian.mobile_oki.view;

import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannedString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.ian.mobile_oki.OkiApp;
import com.example.ian.mobile_oki.R;
import com.example.ian.mobile_oki.contracts.MainMenuContract;
import com.example.ian.mobile_oki.data.KDMoveListItem;
import com.example.ian.mobile_oki.databinding.TimelineBodyRowBinding;
import com.example.ian.mobile_oki.logic.MainMenuPresenter;
import com.example.ian.mobile_oki.util.StringUtil;

/**
 * Shortening the name to MOKI, since I had to make another Git repo.
 * <p>
 * COMPLETED: Add navigation.
 * COMPLETED: Add Actionbar Drawer toggle button.
 * TODO: Invalidate KD Move on Character change.
 * TODO: Close drawer on item select?
 * TODO: Go straight to KD Move Select after picking another character?
 * TODO: Allow no character/kd selected.
 * TODO: Display warning(s) and hide Timeline when no char/kd is selected.
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

    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mNavDrawerLayout;
    ListView mNavDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpNavDrawer();

        // get or create presenter instance, which will in turn set this view's presenter
        setPresenter((MainMenuPresenter) getLastCustomNonConfigurationInstance());

        hideTimeline();

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
        setSelectedKDMove(kdMove);

        //temp
        String tvText = getSelectedCharacter() + "\n" + getSelectedKDMove();
        ((TextView) findViewById(R.id.tv_temp)).setText(tvText);
    }

    @Override
    public void showOkiMoveSelect() {

    }

    @Override
    public void setAndShowOkiMove(int okiSlot, String okiMove) {

    }

    /**
     * Shows timeline if hidden.<br/>
     * {@code rowBinding} gives access to the generated Data Binding for the timeline's body
     * <p>
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
            if (mTimeline.getVisibility() != View.VISIBLE)
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
            // Set empty OKI columns
            updateEmptyColumn(rowBinding.tvBodyOki1);
            updateEmptyColumn(rowBinding.tvBodyOki2);
            updateEmptyColumn(rowBinding.tvBodyOki3);
            updateEmptyColumn(rowBinding.tvBodyOki4);
            updateEmptyColumn(rowBinding.tvBodyOki5);
            updateEmptyColumn(rowBinding.tvBodyOki6);
            updateEmptyColumn(rowBinding.tvBodyOki7);
        }
    }

    @Override
    public void hideTimeline() {
        mTimeline = (TableLayout) findViewById(R.id.tbl_timeline);
        mTimeline.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setCharacterWarningVisible(boolean shouldBeVisible) {
        TextView warning = (TextView) findViewById(R.id.tv_warning_no_char);
        if (shouldBeVisible)
            warning.setVisibility(View.VISIBLE);
        else
            warning.setVisibility(View.GONE);
    }

    @Override
    public void setKDWarningVisible(boolean shouldBeVisible) {
        TextView warning = (TextView) findViewById(R.id.tv_warning_no_kd);
        if (shouldBeVisible)
            warning.setVisibility(View.VISIBLE);
        else
            warning.setVisibility(View.GONE);
    }

    public void updateEmptyColumn(TextView view) {
        String dots = StringUtil.repeat(
                getString(R.string.timeline_frame_symbol)+'\n',
                MAX_TIMELINE_FRAMES - 1);

        view.setText(dots);
    }

    public void updateColumn(KDMoveListItem data, int okiColumn){
        // startup        data.getStartup()
        // active         data.getActive()
        // recovery       data.getRecovery()
        // fill with dots until "current row"
        // fill with "s" for each startup frame
        // fill with "A" for each active frame
        // fill with "r" for each recovery frame
        // fill with dots for remaining frames = [maxFrames - ("current row" + total) - 1)]
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


    /*--------------------*\
    * Nav Drawer Functions *
    \*--------------------*/

    private void setUpNavDrawer() {
        String[] menuItems = getResources().getStringArray(R.array.nav_menu_items);
        mNavDrawerLayout = (DrawerLayout) findViewById(R.id.dl_nav_drawerlayout);
        mNavDrawerList = (ListView) findViewById(R.id.lv_nav_menu);

        // set list adapter
        mNavDrawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_selectable_list_item, menuItems));

        // set list click listener
        mNavDrawerList.setOnItemClickListener(new NavDrawerClickListener());

        // make drawer toggleable
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mNavDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // add drawer toggle listener
        mNavDrawerLayout.addDrawerListener(mDrawerToggle);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    private class NavDrawerClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    // TODO: Create enumerables for menu items.
    private void selectItem(int position) {
        // open corresponding activity
        switch (position) {
            case 0:
                showCharacterSelect();
                break;
            case 1:
                if(hasSelectedCharacter())
                    showKDMoveSelect();
                break;
            // case 2:
                // showOkiMoveSelect();
        }
        mNavDrawerLayout.closeDrawers();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //boolean drawerOpen = mNavDrawerLayout.isDrawerOpen(mNavDrawerList);
        // Hide items in action bar if unrelated to nav menu
          // TODO: implement, if/when action bar items are added...

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // drawer toggle selected
        if (mDrawerToggle.onOptionsItemSelected(item)) return true;
        // handle other items selected

        // default
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // sync toggle state (open/closed)
        mDrawerToggle.syncState();
    }
}