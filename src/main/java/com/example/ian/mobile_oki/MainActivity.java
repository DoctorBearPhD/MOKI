package com.example.ian.mobile_oki;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.ian.mobile_oki.list_helpers.MyListAdapter;

/**
 * Shortening the name to MOKI, since I had to make another Git repo.
 *
 * TODO: Need to learn about using SQLAssetHelper library.
 * TODO: Move the contents of this class to CharacterSelectActivity
 * TODO: Remove click listener implementation unless it turns out it's needed for the coming button
 **/
public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.ian.MESSAGE";

    public static final int ROSTER_SIZE = 25; // TODO : Replace this hard-coded value with a value obtained from the database

    public MyListAdapter mAdapter;
//    ActivityMainBinding mBinding;


    // COMPLETED : Convert this class to use RecyclerView instead of ListView.
    // COMPLETED : Make an Adapter for the RecyclerView


    // TODO : Implement AsyncTask to fill out the list in a background thread.

    // TODO : If valid, use DataBinding to fill out the list of character names (and get access to views).

    // TODO : Implement SQLAssetHelper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        fillCharacterList();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }

//    @Override
//    public void onClick(View view) {
//        if(view instanceof Button)
//        {
//            Intent intent = new Intent(this, MoveSelectActivity.class);
//
//            //get button's text
//            String charName = view.getTag().toString();
//
//            Toast.makeText(this, charName, Toast.LENGTH_SHORT).show(); //TODO: remove before release
//
//            //append character's shorthand name to intent
//            intent.putExtra(EXTRA_MESSAGE, charName);
//
//            //go back to timeline
//            startActivity(intent);
//
//            //setContentView(R.layout.activity_move_select);
//        }
//    }


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
