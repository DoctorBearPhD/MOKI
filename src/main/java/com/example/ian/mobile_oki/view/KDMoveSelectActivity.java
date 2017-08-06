package com.example.ian.mobile_oki.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.ian.mobile_oki.R;
import com.example.ian.mobile_oki.contracts.KDMoveSelectContract;
import com.example.ian.mobile_oki.data.CharacterDatabase;
import com.example.ian.mobile_oki.data.KDMoveListItem;
import com.example.ian.mobile_oki.databinding.KdmoveSelectListItemBinding;
import com.example.ian.mobile_oki.logic.KDMoveSelectPresenter;

import java.util.ArrayList;

public class KDMoveSelectActivity
        extends AppCompatActivity
        implements KDMoveSelectContract.View {

    private final String LIST_KEY = "kd-move-list";

    private ArrayList<KDMoveListItem> mListOfKDMoves;
    private KDMoveSelectContract.Presenter mPresenter;

    private RecyclerView mRecyclerView;

    private String mCharacterCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kdmove_select);

        // if character is not selected, finish activity with RESULT_CANCELED (or custom result?)
        Bundle intentExtras = getIntent().getExtras();

        if (intentExtras.containsKey(MainActivity.CHARACTER_EXTRA))
            mCharacterCode = intentExtras.getString(MainActivity.CHARACTER_EXTRA);
        else {
            Log.d(getClass().getSimpleName(), "onCreate: canceled");
            cancelActivity();
        }

        // find the RecyclerView
        if (mRecyclerView == null) {
            mRecyclerView = (RecyclerView) findViewById(R.id.rv_kdmoves);
        }

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.kd_sel);

        attachPresenter();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // restore stuff
        if (savedInstanceState.containsKey(MainActivity.CHARACTER_EXTRA))
            mCharacterCode = savedInstanceState.getString(MainActivity.CHARACTER_EXTRA);
        if (savedInstanceState.containsKey(LIST_KEY))
            mListOfKDMoves = savedInstanceState.getParcelableArrayList(LIST_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPresenter.start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(LIST_KEY, mListOfKDMoves);
    }

    /*------------------------*\
    * View Interface Functions *
    \*------------------------*/

    private void attachPresenter(){
        if (mPresenter == null) {
            setPresenter(new KDMoveSelectPresenter(
                    this,
                    CharacterDatabase.getInstance()
            ));
        }
    }

    @Override
    public void setPresenter(KDMoveSelectContract.Presenter presenter) {
        mPresenter = presenter;
    }

    /**
     * AKA setKDMoveList. Presenter returns a list of KD moves from the database
     * @param kdMoveList the list of KD moves
     */
    @Override
    public void cacheKDMoveList(ArrayList<KDMoveListItem> kdMoveList) {
        mListOfKDMoves = kdMoveList;
    }

    @Override
    public void displayKDMoveList() {
        // get data from server and cache it
        mPresenter.getListOfKDMoves(mCharacterCode);
        // show in recyclerview
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);

        mRecyclerView.setHasFixedSize(true);

//        Log.d(TAG, "Creating new Adapter...");
        MyListAdapter adapter = new MyListAdapter(mListOfKDMoves);

//        Log.d(TAG, "Setting mRecyclerView's Adapter...");
        mRecyclerView.setAdapter(adapter);
    }

    /*-----*\
    * Other *
    \*-----*/

    /**
     * Called by ViewHolder.
     * @param kdMoveListItem the clicked list item
     */
    public void onListItemClick(KDMoveListItem kdMoveListItem) {
        mPresenter.updateCurrentKDMove(kdMoveListItem);
        setResult(RESULT_OK);
        finish();
    }

    // TODO: Handle cancellation in MainActivity!
    private void cancelActivity() {
        setResult(RESULT_CANCELED);
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
     * Adapted from CharacterSelectActivity
     * <p>
     */

    class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.MyListItemViewHolder> {

        private ArrayList<KDMoveListItem> mList;

        MyListAdapter(ArrayList<KDMoveListItem> list) {
            mList = list;
        }


        @Override
        public MyListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            KdmoveSelectListItemBinding binding = DataBindingUtil.inflate(
                    getLayoutInflater(),
                    R.layout.kdmove_select_list_item,
                    parent,
                    false);

            return new MyListItemViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(MyListItemViewHolder holder, int position) {
            // call the view holder's bind method
            holder.bind(mList.get(position));
            holder.itemView.setOnClickListener(holder);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        /**
         * Custom ViewHolder class.
         * Includes method for getting a reference to the data binding instance of the view holder.
         */
        class MyListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


            private final KdmoveSelectListItemBinding mBinding;

            MyListItemViewHolder(KdmoveSelectListItemBinding binding) {
                super(binding.getRoot());

               mBinding = binding;
            }

            private void bind(KDMoveListItem kdmli) {
                mBinding.setKdmove(kdmli);
                mBinding.executePendingBindings(); // forces an update before the next frame (important, apparently!)
            }

            @Override
            public void onClick(View view) {
                onListItemClick(mBinding.getKdmove());
            }
        } // end of MyListItemViewHolder
    }// end of MyListAdapter
}
