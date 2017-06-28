package com.example.ian.mobile_oki;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;

import com.example.ian.mobile_oki.list_helpers.MyListAdapter;
import com.example.ian.mobile_oki.list_helpers.MyListItemViewHolder;

public class CharacterSelectActivity extends AppCompatActivity
        implements MyListAdapter.ListItemClickListener {
    // COMPLETED : Convert this class to use RecyclerView instead of ListView.
    // COMPLETED : Make an Adapter for the RecyclerView
    // COMPLETED : Move the contents of this class to CharacterSelectActivity

    // TODO : Implement AsyncTask to fill out the list in a background thread.

    // TODO : If valid, use DataBinding to fill out the list of character names (and get access to views).

    public static final String CHARACTER_EXTRA = "selected-character";

    public MyListAdapter mAdapter;
    public RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_select);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_names);

        fillCharacterList();
    }

    /**
     * Called by {@link CharacterSelectActivity#onCreate(Bundle)} to fill out the list of characters.
     */
    private void fillCharacterList() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);

        mRecyclerView.setHasFixedSize(true);

        // fill the RecyclerView
        mAdapter = new MyListAdapter(
                getResources().getStringArray(R.array.characters),
                getResources().getStringArray(R.array.charShort),
                this);

        mRecyclerView.setAdapter(mAdapter);

    } // end fillCharacterList()

    // COMPLETED : Send button tag to MainActivity and close this activity
    @Override
    public void onListItemClick(Button btn) {
        Intent intent = new Intent();
        intent.putExtra(CHARACTER_EXTRA, btn.getTag().toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}
