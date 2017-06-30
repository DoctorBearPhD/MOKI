package com.example.ian.mobile_oki;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;

import com.example.ian.mobile_oki.list_helpers.MyListAdapter;

/**
 * <br/> <b><i>Character Select Screen</i></b> <br/> <br/>
 *
 * Displays a list of character names.
 *
 * <p>When a character is chosen, sends the character's name in short form
 * back to the calling activity.</p>
 */
public class CharacterSelectActivity extends AppCompatActivity
        implements MyListAdapter.ListItemClickListener {

    // TODO : Implement AsyncTask to fill out the list in a background thread.

    // TODO : If valid, use DataBinding to fill out the list of character names (and get access to views).

    public MyListAdapter mAdapter;
    public RecyclerView mRecyclerView;

    private static final String TAG = CharacterSelectActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_select);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        if (mRecyclerView == null){
//            Log.d(TAG, "onStart: mRecyclerView is null. Creating reference...");
            mRecyclerView = (RecyclerView) findViewById(R.id.rv_names);
        }
//        Log.d(TAG, "onStart: mRecyclerView not null.");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume: Filling out character list...");
        fillCharacterList();
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

    /**
     * Called by {@link CharacterSelectActivity#onResume()} to fill out the list of characters.
     */
    private void fillCharacterList() {
//        Log.d(TAG, "fillCharacterList: Creating new LayoutManager...");
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);

        mRecyclerView.setHasFixedSize(true);
//        Log.d(TAG, "fillCharacterList: Creating new Adapter...");
        // fill the RecyclerView
        mAdapter = new MyListAdapter(
                getResources().getStringArray(R.array.characters),
                getResources().getStringArray(R.array.charShort),
                this);
//        Log.d(TAG, "fillCharacterList: Setting mRecyclerView's Adapter...");
        mRecyclerView.setAdapter(mAdapter);

    } // end fillCharacterList()

    // COMPLETED : Send button tag to MainActivity and close this activity
    @Override
    public void onListItemClick(Button btn) {
        Intent intent = new Intent();
        Log.d(TAG, "onListItemClick: new intent made");
        intent.putExtra(MainActivity.CHARACTER_EXTRA, btn.getTag().toString());
        setResult(RESULT_OK, intent);
        Log.d(TAG, "onListItemClick: result set, finish");
        finish();
    }
}
