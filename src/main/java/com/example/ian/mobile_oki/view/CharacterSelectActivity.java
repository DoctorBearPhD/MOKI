package com.example.ian.mobile_oki.view;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ian.mobile_oki.R;
import com.example.ian.mobile_oki.contracts.CharacterSelectContract;
import com.example.ian.mobile_oki.data.CharacterDatabase;
import com.example.ian.mobile_oki.data.CharacterListItem;
import com.example.ian.mobile_oki.logic.CharSelPresenter;

import java.util.List;

/**
 * <br/> <b><i>Character Select Screen</i></b> <br/> <br/>
 * <ol>
 * <li> Display a list of character names to the user </li>
 * <li> When a name is clicked, send the code name back to the Main Activity </li>
 * </ol>
 * <p>When a character is chosen, sends the character's name in short form
 * back to the calling activity.<p>
 */
public class CharacterSelectActivity extends AppCompatActivity implements CharacterSelectContract.View {

    // TODO : Implement AsyncTask to fill out the list in a background thread.

    // TODO : If valid, use DataBinding to fill out the list of character names (and get access to views).

    private static final String TAG = CharacterSelectActivity.class.getSimpleName();

    public MyListAdapter mAdapter;
    public RecyclerView mRecyclerView;

    private CharacterSelectContract.Presenter mCSPresenter;
    private List<CharacterListItem> listOfCharacters;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_select);


        if (mRecyclerView == null) {
//            Log.d(TAG, "onStart: mRecyclerView is null. Creating reference...");
            mRecyclerView = (RecyclerView) findViewById(R.id.rv_names);
        }
//        Log.d(TAG, "onStart: mRecyclerView not null.");
        mCSPresenter = new CharSelPresenter(
                this,
                CharacterDatabase.getInstance(getApplicationContext())
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCSPresenter.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Paused.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Stopped.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /*-------------------*\
    * Presenter Functions *
    \*-------------------*/

    @Override
    public void setPresenter(CharacterSelectContract.Presenter presenter) {
        mCSPresenter = presenter;
    }

    /**
     * Retrieves a list of {@link CharacterListItem}s and fills out the list of characters.
     */
    @Override
    public void showListOfNames() {
        // get the list
        listOfCharacters = mCSPresenter.fetchListOfNames();

        // show it!

//        Log.d(TAG, "Creating new LayoutManager...");
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);

        mRecyclerView.setHasFixedSize(true);

//        Log.d(TAG, "Creating new Adapter...");
        mAdapter = new MyListAdapter(listOfCharacters);

//        Log.d(TAG, "Setting mRecyclerView's Adapter...");
        mRecyclerView.setAdapter(mAdapter);

    } // end fillCharacterList()


    // TODO : Move to presenter?
//    @Override
    public void onListItemClick(View itemView) {
        TextView tvCharacterListItem = (TextView) itemView;
        String codeName = tvCharacterListItem.getTag().toString();

        Intent intent = new Intent();
        Log.d(TAG, "onListItemClick: new intent made");
        intent.putExtra(MainActivity.CHARACTER_EXTRA, codeName);
        setResult(RESULT_OK, intent);
        Log.d(TAG, "onListItemClick: result set, finish");
        finish();
    }



    /*-------------*\
    * Inner Classes *
    \*-------------*/

    /**
     * Created by Ian on 6/27/2017.
     * <p>
     * Generic Adapter used for filling the RecyclerView lists.
     * <p>
     * List gets filled with names properly now!
     * <p>
     * Todo: implement alternating bg colors for list items
     */

    class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.MyListItemViewHolder> {

        private List<CharacterListItem> mList;

        public MyListAdapter(List<CharacterListItem> list) {
            mList = list;
        }


        @Override
        public MyListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.character_select_list_item, parent, false);

            return new MyListItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyListItemViewHolder holder, int position) {
            // get element from dataset at this position

            CharacterListItem listItemData;

            // if we're within the domain of the list
            if (position >= 0 && position < getItemCount())
                listItemData = mList.get(position);
            else return;

            // bind the data to the view
            TextView listItemView = (TextView) holder.itemView.findViewById(R.id.tv_character);

            listItemView.setText(listItemData.getCharacterName());
            listItemView.setTag(listItemData.getCharacterCode());
            listItemView.setOnClickListener(holder);
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

            private TextView mSelection;


            MyListItemViewHolder(View itemLayoutView) {
                super(itemLayoutView);

                mSelection = (TextView) itemLayoutView.findViewById(R.id.tv_character);

                mSelection.setOnClickListener(this); // this class implements OnClickListener, so pass this
            }

            @Override
            public void onClick(View view) {
                Log.d("viewholder: ",
                        "layoutposition=" + getLayoutPosition() +
                                "; adapterposition=" + getAdapterPosition());
                onListItemClick(view);
            }
        }
    }
}
