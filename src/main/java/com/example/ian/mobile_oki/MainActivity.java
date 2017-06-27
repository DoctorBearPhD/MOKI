package com.example.ian.mobile_oki;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ian.mobile_oki.databinding.ActivityMainBinding;

/**
 * Shortening the name to MOKI, since I had to make another Git repo.
 *
 * TODO: Need to learn about using SQLAssetHelper library.
 * TODO: Move the contents of this class to CharacterSelectActivity
 * TODO: Remove click listener implementation unless it turns out it's needed for the coming button
 **/
public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.ian.MESSAGE";

    public static final int ROSTER_SIZE = 27; // TODO : Replace this hard-coded value with a value obtained from the database

    ActivityMainBinding mBinding;

    // TODO : Convert this class to use RecyclerView instead of ListView.
    // TODO : Use DataBinding to fill out the list of character names.
    // TODO : Make an Adapter for the RecyclerView

    // TODO : Implement SQLAssetHelper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        fillCharacterList();
        //setClickListeners();
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
//        Resources res        = getResources();
//        int       rosterSize = res.getInteger(R.integer.ROSTER_SIZE);  //can't just set it to R.integer.ROSTER_SIZE
//
////        LinearLayout con = (LinearLayout) findViewById(R.id.char_btn_container);
//
//        // Get character names
//
//        String[] chars = res.getStringArray(R.array.characters);
//        String[] chr   = res.getStringArray(R.array.charShort);
//
//        for (int i = 0; i < rosterSize; i++)
//        {
//            Button btn = new Button(this);
//
//
//            // set button properties
//            btn.setText(chars[i]);
//            btn.setTag(chr[i]);
//            btn.setOnClickListener(this);
//
//            // add button to list (container)
////            con.addView(btn);
//        }

    }


}
