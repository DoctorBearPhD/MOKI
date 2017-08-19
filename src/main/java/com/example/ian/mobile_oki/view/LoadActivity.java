package com.example.ian.mobile_oki.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ian.mobile_oki.OkiApp;
import com.example.ian.mobile_oki.R;
import com.example.ian.mobile_oki.contracts.LoadDataContract;
import com.example.ian.mobile_oki.data.CharacterListItem;
import com.example.ian.mobile_oki.data.OkiSetupDataObject;
import com.example.ian.mobile_oki.data.storage.StorageDbHelper;
import com.example.ian.mobile_oki.logic.LoadDataPresenter;
import com.example.ian.mobile_oki.util.OkiUtil;

import java.util.ArrayList;

/**
 * TODO: Implement click handling for items in the Saved Setups List
 *      TODO: Set CharacterDatabase values when clicked, then return to Timeline
 *
 * TODO: Allow deletion of [entries in Saved Setups]
 *
 * Created by Ian on 8/17/2017.
 */

public class LoadActivity extends    AppCompatActivity
                          implements LoadDataContract.View,
                                     AdapterView.OnItemSelectedListener {


//    private static final String NO_CHARACTER = "Character";

    LoadDataContract.Presenter mPresenter;

    /** A list of each Character's name and Character Code */
    ArrayList<CharacterListItem> mAllCharacters;
    /** The currently selected Character */
    String mCharacter;
    /** The list of Saved Setups for the selected Character */
    ArrayList<OkiSetupDataObject> mSavedSetups;
    private RecyclerView mListOfSetups;
    private MyListAdapter mAdapter;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        // Create presenter, which attaches this view to itself
        if (mPresenter == null)
            new LoadDataPresenter(this, new StorageDbHelper(this));

        // Get the currently selected Character, if there is one.
        mCharacter = mPresenter.getCurrentCharacter();
        // Get the list of all Characters and their Character Code.
        mAllCharacters = mPresenter.getCharacters();

        // Store a reference to the list that displays Saved Setups
        mListOfSetups = (RecyclerView) findViewById(R.id.rv_load_oki_setup_list);
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
        // TODO: Use OkiSetupDataObject to deal with the data
          // Find Spinner and RecyclerView
        Spinner spinner = (Spinner) findViewById(R.id.sp_load_char_spinner);

          // Get array of names
        //ArrayList<CharacterListItem> characterList = mPresenter.getCharacters();

        // Names with which to populate the Spinner
        String[] fullNames = new String[mAllCharacters.size()];
//        fullNames[0] = (mCharacter != null) ? mCharacter : NO_CHARACTER;

        for (int i = 0; i < mAllCharacters.size(); i++) {
            fullNames[i] = mAllCharacters.get(i).getCharacterName();
//            Log.d("*v*v*", "populateCharacterSpinner: " + fullNames[i]);
        }
          // Make an Adapter to populate the Spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                OkiApp.getContext(),
                R.layout.load_spinner_item,
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
        String tableName = mAllCharacters.get(index).getCharacterCode(),
               selection = null;                                         // TODO: Specify columns to select (e.g. exclude oki row)

        // Get list of setups for the character
        mSavedSetups = mPresenter.getListOfSetups(tableName, selection);

        // Show the list of Oki Setups
        mListOfSetups.setLayoutManager(new LinearLayoutManager(this));
          // create an adapter
        mAdapter = new MyListAdapter(mSavedSetups);
          // swap out the old adapter with a new one, if list already has one
        if (mListOfSetups.getAdapter() != null)
            mListOfSetups.swapAdapter(mAdapter, true);
        else
            mListOfSetups.setAdapter(mAdapter);

        //TODO: Finish custom RecyclerView.Adapter
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

    /*-------------*\
    * Inner Classes *
    \*-------------*/

    /**
     * Generic Adapter used for filling the RecyclerView lists. <p>
     * Adapted from {@link CharacterSelectActivity}.
     */

    class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.MyListItemViewHolder> {

        private ArrayList<OkiSetupDataObject> mList;

        MyListAdapter(ArrayList<OkiSetupDataObject> list) {
            mList = list;
        }


        @Override
        public MyListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.load_list_item, parent, false);

            return new MyListItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyListItemViewHolder holder, int position) {
            // get element from dataset at this position

            OkiSetupDataObject listItemData;

            // if we're within the domain of the list
            if (position >= 0 && position < getItemCount())
                listItemData = mList.get(position);
            else return;

            // find the views to bind
            TextView kdItemView = (TextView) holder.itemView.findViewById(R.id.tv_load_oki_setup_kd);
            TextView okisItemView = (TextView) holder.itemView.findViewById(R.id.tv_load_oki_setup_okis);

            String itemText = listItemData.getKdMove() + ": ";

            // bind the data to the views
            kdItemView.setText(itemText);

            itemText = "";

            for (int i = 0; i < listItemData.getOkiMoves().length; i++) {
                String move = listItemData.getOkiMoves()[i];
                if (move != null)
                    itemText = itemText.concat( move + ", ");
            }

            itemText = itemText.substring(0, itemText.length() - 2); // removes last ", "
            okisItemView.setText(itemText);

//            okisItemView.setTag(listItemData); // TODO: Might want this
            okisItemView.setOnClickListener(holder);
            // alternate bg colors
            holder.itemView.findViewById(R.id.ll_load_oki_setup).setBackgroundColor(
                    (position % 2 == 0) ?
                            OkiUtil.getColor(R.color.bgAccent) :
                            OkiUtil.getColor(R.color.bgLight));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        /**
         * Custom ViewHolder class.
         */
        class MyListItemViewHolder
                extends RecyclerView.ViewHolder
                implements View.OnClickListener {

            MyListItemViewHolder(View itemLayoutView) {
                super(itemLayoutView);
            }

            @Override
            public void onClick(View view) {
                // TODO: Implement click functionality
            }
        }
    }
}
