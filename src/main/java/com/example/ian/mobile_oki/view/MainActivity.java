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
import android.widget.Toast;

import com.example.ian.mobile_oki.OkiApp;
import com.example.ian.mobile_oki.R;
import com.example.ian.mobile_oki.contracts.MainMenuContract;
import com.example.ian.mobile_oki.data.OkiMoveListItem;
import com.example.ian.mobile_oki.databinding.TimelineBodyRowBinding;
import com.example.ian.mobile_oki.logic.MainMenuPresenter;
import com.example.ian.mobile_oki.util.OkiUtil;
import com.example.ian.mobile_oki.util.StringUtil;

import java.util.ArrayList;

import static android.graphics.Color.TRANSPARENT;

/**
 * Shortening the name to MOKI, since I had to make another Git repo.
 * <p>
 * COMPLETED: Add navigation.
 * COMPLETED: Add Actionbar Drawer toggle button.
 * COMPLETED: Invalidate KD Move on Character change.
 * COMPLETED: Close drawer on item select?
 * COMPLETED: Go straight to KD Move Select after picking another character?
 * COMPLETED: Allow no character/kd selected.
 * COMPLETED: Display warning(s) and hide Timeline when no char/kd is selected.
 * COMPLETED: Fix issue:
 *  Selecting an Oki Move sets the same column content for any non-empty columns.
 *  Oki column content doesn't have dots.
 *  TODO: Implement row selection
 *  TODO: Fix row height in ListView
 *  TODO: Fix currentRow resetting on recreate
 * <p>
 **/
public class MainActivity extends AppCompatActivity implements MainMenuContract.View {

//    public static final String EXTRA_MESSAGE = "com.example.ian.MESSAGE";
    public static final int CHAR_SEL_REQUEST_CODE = 6969;
    public static final int KD_MOVE_SEL_REQUEST_CODE = 8008;
    public static final int OKI_MOVE_SEL_REQUEST_CODE = 7175;
    public static final String CHARACTER_EXTRA = "selected-character";
    public static final String KD_MOVE_EXTRA = "selected-kd-move";
    public static final String OKI_SLOT_EXTRA = "oki-number";
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

    /**
     * The currently selected column in the Timeline (Oki #). An int from 1 to 7
     */
    private int mCurrentOkiSlot;

    private int mCurrentRow;

    private MainMenuContract.Presenter mMainMenuPresenter;

    private TableLayout mTimeline;

    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mNavDrawerLayout;
    ListView mNavDrawerList;

    TimelineBodyRowBinding mBodyBinding;

    ArrayList<TextView> mOkiColumns;

    Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCurrentRow = 1;

        setUpNavDrawer();

        // get or create presenter instance, which will in turn set this view's presenter
        setPresenter((MainMenuPresenter) getLastCustomNonConfigurationInstance());

        hideTimeline(); // also sets mTimeline...

        bindTimelineBody();
        storeOkiColumns();
        setUpRowSelector();

        mBodyBinding.tvBodyFramesTens.setHorizontallyScrolling(true); // allows tens-digit col to have double digits on one row

        // restore previous state, if available
        if (savedInstanceState!=null){
            //set data
            setAndShowCharacter(savedInstanceState.getString(CHARACTER_EXTRA));
            setAndShowKDMove(savedInstanceState.getString(KD_MOVE_EXTRA));
            setCurrentOkiSlot(savedInstanceState.getInt(OKI_SLOT_EXTRA));
        }
    }

    private void setUpRowSelector() {
        mBodyBinding.lvRowSelector.setDivider(null);
//        mBodyBinding.lvRowSelector.setDividerHeight(0);
        // make rows
        ArrayList<String> rows = new ArrayList<>(120);
        for (int i = 0; i < 120; i++){
            rows.add(" ");
        }
        // set adapter
        mBodyBinding.lvRowSelector.setAdapter(new ArrayAdapter<>(this, R.layout.row_selector_item, rows));
        mBodyBinding.lvRowSelector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setCurrentRow(i + 1, view);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

//        if (savedInstanceState.containsKey(CHARACTER_EXTRA) && getSelectedCharacter() == null)
//            setAndShowCharacter(savedInstanceState.getString(CHARACTER_EXTRA));
//        if (savedInstanceState.containsKey(KD_MOVE_EXTRA) && getSelectedKDMove() == null)
//            setAndShowKDMove(savedInstanceState.getString(KD_MOVE_EXTRA));
//        if (savedInstanceState.containsKey(OKI_SLOT_EXTRA) && getCurrentOkiSlot() == 0)
//            setCurrentOkiSlot(savedInstanceState.getInt(OKI_SLOT_EXTRA));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mMainMenuPresenter.handleResult(requestCode, resultCode, data);
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

        int okiSlot = getCurrentOkiSlot();
        if (okiSlot > 0)
            outState.putInt(OKI_SLOT_EXTRA, okiSlot);
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
        Intent intent = new Intent(OkiApp.getContext(), OkiMoveSelectActivity.class);
        // pass the character code to the activity so its presenter can query the database
        intent.putExtra(CHARACTER_EXTRA, getSelectedCharacter());
        intent.putExtra(OKI_SLOT_EXTRA, getCurrentOkiSlot());
        // start the KDMoveSelectActivity
        startActivityForResult(intent, OKI_MOVE_SEL_REQUEST_CODE);
    }

    @Override
    public void setAndShowOkiMove(OkiMoveListItem okiMove) {
        String text = getSelectedCharacter()+"\n"+getSelectedKDMove()+"\n"+okiMove.getMove();
        ((TextView) findViewById(R.id.tv_temp)).setText(text);
        updateOkiColumn(getCurrentOkiSlot(), mOkiColumns.get(getCurrentOkiSlot()));
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

            // update columns
            updateKDAColumns();
            updateAllOkiColumns();
        }
    }

    public void updateAllOkiColumns() {
        Log.d(TAG, "updateAllOkiColumns: updating!");
        for (int i = 1; i <= 7; i++){
            TextView column = mOkiColumns.get(i-1);

            if (mMainMenuPresenter.getCurrentOkiMoveAt(i) == null)
                updateEmptyColumn(column);
            else
                updateOkiColumn(i, column); // TODO: should set columns dirty when update needed
        }
    }

    public void updateEmptyColumn(TextView view) {
        String dots = StringUtil.repeat(
                getString(R.string.timeline_frame_symbol)+'\n',
                MAX_TIMELINE_FRAMES - 1);

        view.setText(dots);
    }

    public void updateKDAColumns(){
        // get formatted text from presenter
        // (SpannedStrings allow multiple colors and styles in one TextView)
        SpannedString[] formattedTextValues = mMainMenuPresenter.getKDAColumnContent();

        mBodyBinding.tvBodyKd.setText(formattedTextValues[0]);
        mBodyBinding.tvBodyKdr.setText(formattedTextValues[1]);
        mBodyBinding.tvBodyKdbr.setText(formattedTextValues[2]);
    }
// TODO: Make efficient - move to presenter
    public void updateOkiColumn(int okiSlot, TextView column) {
        int row = getCurrentRow() - 1;
        row = row < 0 ? 0 : row; // prevents out of bounds exception from row not being set yet

        column.setText(mMainMenuPresenter.getOkiColumnContent(okiSlot, row));
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

    @Override
    public int getCurrentOkiSlot() {
        return mCurrentOkiSlot;
    }

    /*----*\
    * Misc *
    \*----*/

    private void showOkiSlotWarning() {
        if (mToast != null) mToast.cancel();

        mToast = Toast.makeText(this, "Select an OKI# first!", Toast.LENGTH_LONG);
        mToast.show();
    }

    public void onHeaderClick(View view){
        Log.d(TAG, "onHeaderClick: "+ (view.toString()));
        Log.d(TAG, "onHeaderClick: "+ (view.getTag().toString()));
        Log.d(TAG, "onHeaderClick: "+Integer.valueOf(view.getTag().toString()));
        setCurrentOkiSlot(Integer.valueOf(view.getTag().toString()));
        if (getCurrentRow() == 0) setCurrentRow(1, view); // default to first row if not set
    }

    private void bindTimelineBody() {
        View body = mTimeline.findViewById(R.id.tr_body);

        mBodyBinding = DataBindingUtil.bind(body);
    }

    private void storeOkiColumns() {
        mOkiColumns = new ArrayList<>();
        mOkiColumns.add(mBodyBinding.tvBodyOki1);
        mOkiColumns.add(mBodyBinding.tvBodyOki2);
        mOkiColumns.add(mBodyBinding.tvBodyOki3);
        mOkiColumns.add(mBodyBinding.tvBodyOki4);
        mOkiColumns.add(mBodyBinding.tvBodyOki5);
        mOkiColumns.add(mBodyBinding.tvBodyOki6);
        mOkiColumns.add(mBodyBinding.tvBodyOki7);
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

    public void setCurrentOkiSlot(int newOkiSlot) {
        TextView okiCol;
        // set "selected" header and body column to "unselected"
        if (mCurrentOkiSlot > 0 && mCurrentOkiSlot < 8) {
            okiCol = (TextView) findViewById(R.id.tr_header)
                    .findViewWithTag(String.valueOf(mCurrentOkiSlot));
            okiCol.setBackgroundColor(OkiUtil.getColor(R.color.bgTableOKI));
            okiCol = (TextView) findViewById(R.id.tr_body)
                    .findViewWithTag(String.valueOf(mCurrentOkiSlot));
            okiCol.setBackgroundColor(OkiUtil.getColor(R.color.bgTableOKI));
        // set "unselected" header and body column color to "selected"
        okiCol = (TextView) findViewById(R.id.tr_header)
                .findViewWithTag(String.valueOf(newOkiSlot));
        okiCol.setBackgroundColor(OkiUtil.getColor(R.color.colorPrimaryDark));

        okiCol = (TextView) findViewById(R.id.tr_body)
                .findViewWithTag(String.valueOf(newOkiSlot));
        okiCol.setBackgroundColor(OkiUtil.getColor(R.color.colorPrimaryDark));
        }

        mCurrentOkiSlot = newOkiSlot; // TODO: Send to db instead?
    }

    public int getCurrentRow() {
        return mCurrentRow;
    }

    public void setCurrentRow(int mCurrentRow, View view) {
        mBodyBinding.lvRowSelector.getChildAt(getCurrentRow()-1).setBackgroundColor(TRANSPARENT);
        this.mCurrentRow = mCurrentRow;
        view.setBackgroundColor(OkiUtil.getColor(R.color.secLight));
        view.getBackground().setAlpha(50);
        Log.d(TAG, "setCurrentRow: "+getCurrentRow());
    }



    /*--------------------*\
    * Nav Drawer Functions *
    \*--------------------*/

    private void setUpNavDrawer() {
        String[] menuItems = getResources().getStringArray(R.array.nav_menu_items);
        mNavDrawerLayout = (DrawerLayout) findViewById(R.id.dl_nav_drawerlayout);
        mNavDrawerList = (ListView) findViewById(R.id.lv_nav_menu);

        // set list adapter
        mNavDrawerList.setAdapter(new ArrayAdapter<>(this,
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
            case 2:
                if(hasSelectedCharacter() && hasSelectedKDMove()) {
                    if (mCurrentOkiSlot > 0 && mCurrentOkiSlot < 8) // between 1 & 7
                        showOkiMoveSelect();
                    else
                        showOkiSlotWarning();
                }
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