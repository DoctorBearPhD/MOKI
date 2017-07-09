package com.example.ian.mobile_oki.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ian.mobile_oki.R;
import com.example.ian.mobile_oki.contracts.KDMoveSelectContract;
import com.example.ian.mobile_oki.data.CharacterDatabase;
import com.example.ian.mobile_oki.data.KDMoveListItem;
import com.example.ian.mobile_oki.logic.KDMoveSelectPresenter;

import java.util.List;

public class KDMoveSelectActivity
        extends AppCompatActivity
        implements KDMoveSelectContract.View {

    private List<KDMoveListItem> mListOfKDMoves;
    private KDMoveSelectContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_select);

        // use dependency injection for this
        mPresenter = new KDMoveSelectPresenter(
                this,
                CharacterDatabase.getInstance(getApplicationContext())
        );
    }


    /*------------------------*\
    * View Interface Functions *
    \*------------------------*/

    @Override
    public void setPresenter(KDMoveSelectContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void cacheKDMoveList(List<KDMoveListItem> kdMoveListItems) {
        mListOfKDMoves = kdMoveListItems;
    }

    @Override
    public void displayKDMoveList() {

    }
}
