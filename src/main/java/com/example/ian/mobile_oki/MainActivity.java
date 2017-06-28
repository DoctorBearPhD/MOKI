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
 *
 * TODO: Remove click listener implementation unless it turns out it's needed for the coming button
 **/
public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.ian.MESSAGE";

//    ActivityMainBinding mBinding;


    // TODO : Implement SQLAssetHelper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

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



}
