package com.example.ian.mobile_oki;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.ian.mobile_oki.list_helpers.MyListAdapter;

public class CharacterSelectActivity extends AppCompatActivity {
    // COMPLETED : Convert this class to use RecyclerView instead of ListView.
    // COMPLETED : Make an Adapter for the RecyclerView
    // COMPLETED : Move the contents of this class to CharacterSelectActivity

    // TODO : Implement AsyncTask to fill out the list in a background thread.

    // TODO : If valid, use DataBinding to fill out the list of character names (and get access to views).

    public static final String CHARACTER_EXTRA = "selected-character";

    public MyListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_select);

        fillCharacterList();
    }

    private void fillCharacterList() {
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv_names);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rv.setLayoutManager(manager);

        rv.setHasFixedSize(true);

        // fill the RecyclerView
        mAdapter = new MyListAdapter(
                getResources().getStringArray(R.array.characters),
                getResources().getStringArray(R.array.charShort));

        rv.setAdapter(mAdapter);

    } // end fillCharacterList()
}
