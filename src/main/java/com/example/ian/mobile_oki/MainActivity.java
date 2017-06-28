package com.example.ian.mobile_oki;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import static com.example.ian.mobile_oki.CharacterSelectActivity.CHARACTER_EXTRA;

/**
 * Shortening the name to MOKI, since I had to make another Git repo.
 * <p>
 * TODO: Need to learn about using SQLAssetHelper library.
 * <p>
 * TODO: Remove click listener implementation unless it turns out it's needed for the coming button
 **/
public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.ian.MESSAGE";
    private static final int CHAR_SEL_REQUEST_CODE = 6969;

    private String mSelectedCharacter;

//    ActivityMainBinding mBinding;


    // TODO : Implement SQLAssetHelper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        if (mSelectedCharacter == null) {
            Intent intent = new Intent(this, CharacterSelectActivity.class);
            startActivityForResult(intent, CHAR_SEL_REQUEST_CODE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null)
            mSelectedCharacter = data.getStringExtra(CHARACTER_EXTRA);

        ((TextView) findViewById(R.id.tv_temp)).setText(mSelectedCharacter);
    }
}
