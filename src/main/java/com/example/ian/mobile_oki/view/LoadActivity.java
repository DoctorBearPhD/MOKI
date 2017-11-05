package com.example.ian.mobile_oki.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.ian.mobile_oki.OkiApp;
import com.example.ian.mobile_oki.R;
import com.example.ian.mobile_oki.contracts.LoadDataContract;
import com.example.ian.mobile_oki.data.CharacterListItem;
import com.example.ian.mobile_oki.data.OkiSetupDataObject;
import com.example.ian.mobile_oki.data.storage.StorageDbHelper;
import com.example.ian.mobile_oki.logic.LoadDataPresenter;
import com.example.ian.mobile_oki.view.load.LoadSetupChildItem;
import com.example.ian.mobile_oki.view.load.LoadSetupHeaderItem;
import com.xwray.groupie.ExpandableGroup;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;

import java.util.ArrayList;

import static android.support.design.widget.BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_MANUAL;
import static android.support.design.widget.BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_TIMEOUT;

/**
 * Activity for loading Oki Setups
 * <p/>
 * Created by Ian on 8/17/2017.
 */

public class LoadActivity extends    AppCompatActivity
                          implements LoadDataContract.View,
                                     AdapterView.OnItemSelectedListener {


//    private static final String NO_CHARACTER = "Character";

    LoadDataContract.Presenter mPresenter;

    /** A list of each Character's name and Character Code (only those with saved Setups) */
    ArrayList<CharacterListItem> mCharactersList;
    /** The currently selected Character */
    String mCharacter;
    /** The list of Saved Setups for the selected Character */
    ArrayList<OkiSetupDataObject> mSavedSetups;
    /** The row IDs of the Saved Setups in the database */
    ArrayList<Long> mSavedSetupsIDs;

    private RecyclerView mRecyclerView;
    private Snackbar      mSnackbar;

    private LoadSetupChildItem mRemovedItem;
    private ExpandableGroup    mRemovedItemGroup;
    private int                mRemovedItemGroupIndex, mRemovedItemPosition;

    private GroupAdapter mGroupAdapter;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        if (getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.load_title);

        // Create presenter, which attaches this view to itself
        if (mPresenter == null)
            new LoadDataPresenter(this, new StorageDbHelper(this));

        // Get the currently selected Character, if there is one.
        mCharacter = mPresenter.getCurrentCharacter();
        // Get the list of all Characters and their Character Code.
        mCharactersList = mPresenter.getCharacters();
        if (mCharactersList.isEmpty()) finish();

        // Store a reference to the list that displays Saved Setups
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_load_oki_setup_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();

        super.onDestroy();
    }


    /*------------------------*\
    * View Interface Functions *
    \*------------------------*/

    @Override
    public void setPresenter(LoadDataContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void populateCharacterSpinner() {
          // Find Spinner and RecyclerView
        Spinner spinner = (Spinner) findViewById(R.id.sp_load_char_spinner);

          // Get array of names
        //ArrayList<CharacterListItem> characterList = mPresenter.getCharacters();

        // Names with which to populate the Spinner
        String[] fullNames = new String[mCharactersList.size()];
//        fullNames[0] = (mCharacter != null) ? mCharacter : NO_CHARACTER;

        for (int i = 0; i < mCharactersList.size(); i++) {
            fullNames[i] = mCharactersList.get(i).getCharacterName();
//            Log.d("*v*v*", "populateCharacterSpinner: " + fullNames[i]);
        }
          // Make an Adapter to populate the Spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                OkiApp.getContext(),
                R.layout.load_spinner_item, // what the spinner will look like; not showing arrow for some reason...
                fullNames);
          // set layout of dropdown list (what it should look like)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          // set the Adapter
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);
        if (mCharacter != null) spinner.setSelection(spinnerAdapter.getPosition(mCharacter));
    }

    @Override
    public void updateListOfSetups(int index){
        String tableName = mCharactersList.get(index).getCharacterCode(),
               selection = null;    // TODO: Specify columns to select (e.g. exclude oki row)

        // Get list of setups for the character
        mSavedSetups = mPresenter.getListOfSetups(tableName, selection);
        // Initialize the associated array of IDs (rowid in database)
        mSavedSetupsIDs = mPresenter.getIDsOfSavedSetups(tableName);

        // Make a list of distinct KD Moves used in all setups
          // extract kd moves from list of saved setups

        ArrayList<String> kdMovesList = new ArrayList<>();

        for (int i=0; i < mSavedSetups.size(); i++) {
            // if not in the list already... add it
            String kdMove = mSavedSetups.get(i).getKdMove();
            if (!kdMovesList.contains(kdMove))
                kdMovesList.add(kdMove);
        }

        // Show the list of Oki Setups
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Make the GroupAdapter
        mGroupAdapter = new GroupAdapter();
        mGroupAdapter.setOnItemClickListener(onClickSetupListener);

      // Add groups for each kd move
        LoadSetupHeaderItem headerItem;
        ExpandableGroup group;
        // Copy saved setups list so items can be deleted as they are added to their groups.
        ArrayList<OkiSetupDataObject> copyOfSavedSetups = mSavedSetups;
        // Do the same for row IDs
        ArrayList<Long> copyOfSavedSetupsIDs = mSavedSetupsIDs;

        for (int i = 0; i < kdMovesList.size(); i++)
        {
            //OkiSetupDataObject osdo = mSavedSetups.get(i);
            // Make a "header item"
            headerItem = new LoadSetupHeaderItem(kdMovesList.get(i), ""); // TODO: Add command as subtitle.
            // This will involve either adding it to the OSDO, or getting it from the Database. ^

            // Add "header item" to "expandable group"
              // this sets the header item as the "parent" of the ExpandableGroup,
              // and sets the ExpandableGroup of the header item to 'group'
            group = new ExpandableGroup(headerItem);

            // Add items to the group.

            for (int j = 0; j < copyOfSavedSetups.size(); j++)
            {
                // if the KD Move of the currently checked setup matches the name of this group
                if (copyOfSavedSetups.get(j).getKdMove().equals(kdMovesList.get(i))) {
                    // add item to group
                    group.add(new LoadSetupChildItem(copyOfSavedSetups.get(j), copyOfSavedSetupsIDs.get(j)));
                    // remove item from stored lists
                    copyOfSavedSetups.remove(j);
                    copyOfSavedSetupsIDs.remove(j);
                }
            }

            // Add group to the adapter.
            mGroupAdapter.add(group);
        }

          // swap out the old adapter with a new one, if list already has one; or just set it
        if (mRecyclerView.getAdapter() != null)
            mRecyclerView.swapAdapter(mGroupAdapter, true);
        else
            mRecyclerView.setAdapter(mGroupAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, 0) {
            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return ((ViewHolder) viewHolder).getSwipeDirs();
            }

            @Override
            public boolean onMove(RecyclerView            recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition();

                if (!(mGroupAdapter.getItem(position) instanceof LoadSetupChildItem))
                    return;

                LoadSetupChildItem item = (LoadSetupChildItem) mGroupAdapter.getItem(position);

                if ( mSnackbar != null && mSnackbar.isShown() ) {
                    // manually call the dismissal callback, since it won't call immediately
                    deleteSetup(DISMISS_EVENT_MANUAL);
                }

                // Store remove item data until Snackbar is gone
                mRemovedItem            = item;
                mRemovedItemGroup       = (ExpandableGroup) mGroupAdapter.getGroup(item);
                mRemovedItemGroupIndex  = mGroupAdapter.getAdapterPosition(mRemovedItemGroup);
                mRemovedItemPosition    = mRemovedItemGroup.getPosition(item);

                // Remove item from group
                mRemovedItemGroup.remove(item);

                // Show the Snackbar
                mSnackbar = Snackbar.make(
                        findViewById(R.id.cl_load),
                        R.string.setup_deleted,
                        Snackbar.LENGTH_LONG
                );
                mSnackbar.setAction(R.string.undo_deletion, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        undoDeletion();
                    }
                });
                mSnackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);

                        deleteSetup(event);
                    }
                });
                mSnackbar.show();
            }
        });

        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void undoDeletion() {
        // insert data back where it was

          // if group was removed due to being empty, remake the group.
        if (mGroupAdapter.getAdapterPosition(mRemovedItemGroup) == -1)
            mGroupAdapter.add(mRemovedItemGroupIndex, mRemovedItemGroup); // TODO: Does this add the group and items as they were?

          // add the item back to the group in the correct position
        mRemovedItemGroup.add(mRemovedItemPosition, mRemovedItem);
        mGroupAdapter.notifyDataSetChanged();

        clearRemovedItemCache();
    }

    private void deleteSetup(int event) {
        switch (event){
            case DISMISS_EVENT_TIMEOUT:
            case DISMISS_EVENT_MANUAL:
                if (mRemovedItem != null) {
                    mPresenter.deleteData(mRemovedItem.getOSDO().getCharacter(),
                                          mRemovedItem.getRowId());

                    clearRemovedItemCache();
                    return;
                }
                break;
        }
    }

    private void clearRemovedItemCache() {
        mRemovedItem = null;
        mRemovedItemGroup = null;
        mRemovedItemPosition = mRemovedItemGroupIndex = 0;
    }



    /*----*\
    *
    \*----*/

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
        String characterInSpinner = parent.getItemAtPosition(pos).toString();

          // Update local selected Character, if a different one was selected
//        if ( !characterInSpinner.equals(NO_CHARACTER) ) {
            mCharacter = characterInSpinner;
            // Update RecyclerView
//            if (pos > 0)
                updateListOfSetups(pos); // pos - 1, if using extra list item at start of spinner

//        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /*---------*\
    * Listeners *
    \*---------*/

    private OnItemClickListener onClickSetupListener = new OnItemClickListener() {
        @Override
        public void onItemClick(@NonNull Item item, @NonNull View view) {
            if (item instanceof LoadSetupChildItem) {
                // Notify the Presenter
                mPresenter.setCurrentSetup(((LoadSetupChildItem) item).getOSDO());
                setResult(RESULT_OK);
                finish();
            }
        }
    };
} // end of LoadActivity class
