package com.example.ian.mobile_oki.view;

import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannedString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
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
 *  COMPLETED: Implement row selection
 *  COMPLETED: Fix row height in ListView
 *  COMPLETED: Fix currentRow resetting on recreate
 *  COMPLETED: Fix Oki Header background color being set to row color
 *  COMPLETED: Fix Oki Moves list not being reset after new character selected
 *  COMPLETED: Fix - config change causes row color to reset
 *  TODO: Fix - Oki Moves can bleed out past the end of the timeline
 * <p>
 **/
public class MainActivity extends AppCompatActivity implements MainMenuContract.View {

//    public static final String EXTRA_MESSAGE = "com.example.ian.MESSAGE";
    public static final int CHAR_SEL_REQUEST_CODE = 6969;
    public static final int KD_MOVE_SEL_REQUEST_CODE = 8008;
    public static final int OKI_MOVE_SEL_REQUEST_CODE = 7175;
    public static final int MAX_TIMELINE_FRAMES = 120;

    private static final String TAG = MainActivity.class.getSimpleName();

    private MainMenuContract.Presenter mMainMenuPresenter;

    private TableLayout mTimeline;

    ActionBar mActionBar;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mNavDrawerLayout;

     /** Gives access to the generated Data Binding class for the timeline's body */
    TimelineBodyRowBinding mBodyBinding;

    ArrayList<TextView> mOkiColumns;

    Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActionBar = getSupportActionBar();

        setUpNavDrawer();

        // get or create presenter instance, which will in turn set this view's presenter
        setPresenter((MainMenuPresenter) getLastCustomNonConfigurationInstance());

        hideTimeline(); // also sets mTimeline...

        bindTimelineBody();
        storeOkiColumns();
        setUpRowSelector();

        mBodyBinding.tvBodyFramesTens.setHorizontallyScrolling(true); // allows tens-digit col to have double digits on one row

        // restore previous state data, if available
//        if (savedInstanceState != null) {
//            //set data
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mMainMenuPresenter.handleResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String character = mMainMenuPresenter.getCurrentCharacter(true);
        if (mActionBar != null && character != null)
            mActionBar.setTitle(character);

        mMainMenuPresenter.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        mMainMenuPresenter.closeStorageDb();
        mMainMenuPresenter.detachView();

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mNavDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mNavDrawerLayout.closeDrawer(GravityCompat.END);
            return;
        }
        // open nav drawer on Timeline screen
        else if (!mNavDrawerLayout.isDrawerOpen(GravityCompat.START)) { // drawer is closed
            mNavDrawerLayout.openDrawer(GravityCompat.START);      // open drawer
            return;
        } else { // show prompt, exit if pressed again
            if(mToast != null && mToast.getView().getTag() == "exit-toast" &&
                    mToast.getView().getWindowVisibility() == View.VISIBLE) {

                mToast.cancel();
                finish();
            } else {
                mToast = Toast.makeText(OkiApp.getContext(), "Press again to exit...", Toast.LENGTH_SHORT);
                mToast.getView().setTag("exit-toast");
                mToast.show();
                return;
            }
        }

        // default behavior
        super.onBackPressed();
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
        return mMainMenuPresenter.getCurrentCharacter(false) != null;
    }

    @Override
    public boolean hasSelectedKDMove() {
        return mMainMenuPresenter.getCurrentKDMove() != null;
    }

    /**
     * Character Select starts here...
     */
    @Override
    public void showCharacterSelect() {
        Intent intent = new Intent(OkiApp.getContext(), CharacterSelectActivity.class);

        startActivityForResult(intent, CHAR_SEL_REQUEST_CODE);
    }

    @Override
    public void showKDMoveSelect() {
        Intent intent = new Intent(OkiApp.getContext(), KDMoveSelectActivity.class);
        // start the KDMoveSelectActivity
        startActivityForResult(intent, KD_MOVE_SEL_REQUEST_CODE);
    }

    /**
     * Interface method for displaying the KD Move on the Timeline screen.
     * Allows the Presenter to tell the View what to set.
     *
     * @param kdMove Name of the Move
     */
    @Override
    public void setAndShowKDMove(String kdMove) {
        // TODO: Show KD Move somewhere
    }

    @Override
    public void showOkiMoveSelect() {
        Intent intent = new Intent(OkiApp.getContext(), OkiMoveSelectActivity.class);
        // start the KDMoveSelectActivity
        startActivityForResult(intent, OKI_MOVE_SEL_REQUEST_CODE);
    }

    @Override
    public void setAndShowOkiMove(OkiMoveListItem okiMove) {
        int slot = mMainMenuPresenter.getCurrentOkiSlot();

        updateOkiColumn(slot, mOkiColumns.get(slot - 1), true);
    }

    /**
     * Shows timeline if hidden, and refreshes its visuals.<br/>
     */
    @Override
    public void showTimeline() {
        if (mTimeline != null) {
            if (mTimeline.getVisibility() != View.VISIBLE)
                mTimeline.setVisibility(View.VISIBLE);

            // update columns
            updateKDAColumns();
            updateAllOkiColumns();
            updateOkiSlotColor(mMainMenuPresenter.getCurrentOkiSlot());
            updateCurrentOkiDrawer();
        }
    }

    public void updateAllOkiColumns() {
        Log.d(TAG, "updateAllOkiColumns: updating!");
        for (int i = 1; i <= 7; i++){
            TextView column = mOkiColumns.get(i-1);

            if (mMainMenuPresenter.getCurrentOkiMoveAt(i) == null)
                updateEmptyColumn(column);
            else
                updateOkiColumn(i, column, false); // TODO: should set columns dirty when update needed
        }
    }

    public void updateEmptyColumn(TextView view) {
        String dots = StringUtil.repeat(
                getString(R.string.timeline_frame_symbol)+'\n',
                MAX_TIMELINE_FRAMES);
        dots = dots.substring(0, dots.length() - 1);

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

    public void updateOkiColumn(int okiSlot, TextView column, boolean useCurrentRow) {
        column.setText(mMainMenuPresenter.getOkiColumnContent(okiSlot, useCurrentRow));
    }

    public void updateOkiSlotColor(int okiSlot) {
        TextView okiBody, okiHeader;
        int currentOkiSlot = mMainMenuPresenter.getCurrentOkiSlot();
        // If a slot is already selected...
        if (currentOkiSlot > 0 && currentOkiSlot < 8) {
            // set "selected" header and body column to "unselected"
            // Find oki slot header and body, and reset background color
            okiHeader = (TextView) findViewById(R.id.tr_header)
                    .findViewWithTag(String.valueOf(currentOkiSlot));
            okiHeader.setBackgroundColor(OkiUtil.getColor(R.color.bgTableOKI));
            // Find oki slot body and reset background color
            okiBody = (TextView) findViewById(R.id.tr_body)
                    .findViewWithTag(String.valueOf(currentOkiSlot));
            okiBody.setBackgroundColor(OkiUtil.getColor(R.color.bgTableOKI));
        }
        // set "unselected" header and body column color to "selected"
        okiHeader = (TextView) findViewById(R.id.tr_header)
                .findViewWithTag(String.valueOf(okiSlot));
        okiHeader.setBackgroundColor(OkiUtil.getColor(R.color.colorPrimaryDark));

        okiBody = (TextView) findViewById(R.id.tr_body)
                .findViewWithTag(String.valueOf(okiSlot));
        okiBody.setBackgroundColor(OkiUtil.getColor(R.color.colorPrimaryDark));
    }

    private void updateCurrentOkiDrawer(){
        OkiMoveListItem okiMove;
        String okiMoveName;
        ScrollView currentOkiDrawer = (ScrollView) findViewById(R.id.sv_currentoki_drawer);
        ArrayList<TextView> moves = new ArrayList<>();

        moves.add(0, (TextView) currentOkiDrawer.findViewById(R.id.tv_kd_item));
        moves.add(1, (TextView) currentOkiDrawer.findViewById(R.id.tv_oki1_item));
        moves.add(2, (TextView) currentOkiDrawer.findViewById(R.id.tv_oki2_item));
        moves.add(3, (TextView) currentOkiDrawer.findViewById(R.id.tv_oki3_item));
        moves.add(4, (TextView) currentOkiDrawer.findViewById(R.id.tv_oki4_item));
        moves.add(5, (TextView) currentOkiDrawer.findViewById(R.id.tv_oki5_item));
        moves.add(6, (TextView) currentOkiDrawer.findViewById(R.id.tv_oki6_item));
        moves.add(7, (TextView) currentOkiDrawer.findViewById(R.id.tv_oki7_item));

        if (mMainMenuPresenter.getCurrentKDMove() != null)
            moves.get(0).setText(mMainMenuPresenter.getCurrentKDMove());

        for (int i=1; i <= 7; i++) {
            okiMove = mMainMenuPresenter.getCurrentOkiMoveAt(i);
            if (okiMove != null) {
                okiMoveName = okiMove.getMove();
                moves.get(i).setText(okiMoveName);
            }
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


    /*----*\
    * Misc *
    \*----*/

    private void showOkiSlotWarning() {
        if (mToast != null) mToast.cancel();

        mToast = Toast.makeText(this, "Select an OKI# first!", Toast.LENGTH_LONG);
        mToast.show();
    }

    public void onHeaderClick(View view){
        int okiSlotNumber = Integer.valueOf(view.getTag().toString());

        if (okiSlotNumber != mMainMenuPresenter.getCurrentOkiSlot()) {
            updateOkiSlotColor(okiSlotNumber); // called before setting to reset the old slot's color
            mMainMenuPresenter.setCurrentOkiSlot(okiSlotNumber);
        }
        else // clicking header when already selected opens oki select screen
            showOkiMoveSelect();
    }

    private void bindTimelineBody() {
        View body = mTimeline.findViewById(R.id.tr_body);

        mBodyBinding = DataBindingUtil.bind(body);
    }

    /**
     * Store the Oki Slot columns (Views/widgets) in an array.
     */
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

    private void setUpRowSelector() {
        mBodyBinding.lvRowSelector.setDivider(null);
        mBodyBinding.lvRowSelector.setDividerHeight(0);
        // make rows
        ArrayList<String> rows = new ArrayList<>(MAX_TIMELINE_FRAMES);
        for (int i = 0; i < MAX_TIMELINE_FRAMES; i++){
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
        // ListView children aren't accessible until it's visible, so create a listener for when it becomes visible.
        mBodyBinding.lvRowSelector.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updateRowColor();

                mBodyBinding.lvRowSelector.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }


    /*-----------------*\
    * Getters / Setters *
    \*-----------------*/

    public void setCurrentRow(int okiRow, View view) {
        int previousRowNumber = mMainMenuPresenter.getCurrentRow();

        if (okiRow != mMainMenuPresenter.getCurrentRow())
            mMainMenuPresenter.setCurrentRow(okiRow);

        // If what I've read is right, ListView children are null unless visible.
        if (mBodyBinding.lvRowSelector.getVisibility() != View.VISIBLE) return;

        setRowColor(previousRowNumber, view);
    }

    private void setRowColor(int oldOkiRow, View view) {
        // reset previous row color
        mBodyBinding.lvRowSelector
                .getChildAt(oldOkiRow - 1)
                .setBackgroundColor(TRANSPARENT);
        // set current row color
        view.setBackgroundColor(OkiUtil.getColor(R.color.secLight));
        view.getBackground().setAlpha(50);
    }

    private void updateRowColor(){
        // get view of current row (in ListView)
        int rowIndex = mMainMenuPresenter.getCurrentRow() - 1;

        TextView rowView = (TextView) getRowView(rowIndex);

        rowView.setBackgroundColor(OkiUtil.getColor(R.color.secLight));
        rowView.getBackground().setAlpha(50);
    }

    private View getRowView(int rowIndex){
        // if row is not on-screen...
        if (rowIndex < mBodyBinding.lvRowSelector.getFirstVisiblePosition() ||
            rowIndex > mBodyBinding.lvRowSelector.getLastVisiblePosition() )
            return mBodyBinding.lvRowSelector.getAdapter()
                    .getView(rowIndex, null, mBodyBinding.lvRowSelector);
        else // row is on-screen
            return mBodyBinding.lvRowSelector.getChildAt(
                    rowIndex - mBodyBinding.lvRowSelector.getFirstVisiblePosition());
    }


    /*--------------------*\
    * Nav Drawer Functions *
    \*--------------------*/

    private void setUpNavDrawer() {
//        DisplayMetrics metrics = OkiApp.getContext().getResources().getDisplayMetrics();
//        float displayWidth = metrics.widthPixels / metrics.density;

        String[] menuItems = getResources().getStringArray(R.array.nav_menu_items);
        mNavDrawerLayout = (DrawerLayout) findViewById(R.id.dl_nav_drawerlayout);
        ListView navDrawerList = (ListView) findViewById(R.id.lv_nav_menu);
//        mNavDrawerList.setMinimumWidth(
//                (int) Math.min(R.dimen.drawer_max_width, displayWidth - R.attr.actionBarSize));

        // set list adapter
        navDrawerList.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_selectable_list_item, menuItems));
        navDrawerList.setDivider(null);

        // set list click listener
        navDrawerList.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        // make drawer toggleable
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mNavDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                if(drawerView == findViewById(R.id.nav_drawer)) {
                    super.onDrawerClosed(drawerView);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                if(drawerView == findViewById(R.id.nav_drawer)) {
                    super.onDrawerOpened(drawerView);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if(drawerView == findViewById(R.id.nav_drawer))
                    super.onDrawerSlide(drawerView, slideOffset);
            }
        };

        // add drawer toggle listener
        mNavDrawerLayout.addDrawerListener(mDrawerToggle);


        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    // TODO: Create enumerables for menu items.
    private void selectItem(int id) {
        // open corresponding activity
        switch (id) {
            case 0:
                showCharacterSelect();
                break;
            case 1:
                if(hasSelectedCharacter())
                    showKDMoveSelect();
                break;
            case 2:
                if(hasSelectedCharacter() && hasSelectedKDMove()) {
                    int currentOkiSlot = mMainMenuPresenter.getCurrentOkiSlot();
                    if (currentOkiSlot > 0 && currentOkiSlot < 8) // If a slot is selected...
                        showOkiMoveSelect();
                    else
                        showOkiSlotWarning();
                }
                break;
            case 3:
                if(mMainMenuPresenter.timelineNotBlank()){
                    // save
                    if (mMainMenuPresenter.saveData()) {
                        if (mToast != null) mToast.cancel();

                        mToast = Toast.makeText(OkiApp.getContext(),
                                "Saved successfully!", Toast.LENGTH_SHORT);
                        mToast.show();
                    }
                }
                break;
            case 4:
                // launch 'Load' activity
                break;
        }

        mNavDrawerLayout.closeDrawer(GravityCompat.START);
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
        if (mDrawerToggle.onOptionsItemSelected(item))
        { // close right drawer if open
            if(mNavDrawerLayout.isDrawerVisible(GravityCompat.END))
                mNavDrawerLayout.closeDrawer(GravityCompat.END);

            return true;
        }
        // handle other actionbar items selected

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