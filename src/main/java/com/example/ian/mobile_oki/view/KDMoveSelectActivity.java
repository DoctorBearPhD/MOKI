package com.example.ian.mobile_oki.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ian.mobile_oki.R;
import com.example.ian.mobile_oki.contracts.KDMoveSelectContract;
import com.example.ian.mobile_oki.data.CharacterDatabase;
import com.example.ian.mobile_oki.data.KDMoveListItem;
import com.example.ian.mobile_oki.databinding.KdmoveSelectListItemBinding;
import com.example.ian.mobile_oki.logic.KDMoveSelectPresenter;

import java.util.List;

public class KDMoveSelectActivity
        extends AppCompatActivity
        implements KDMoveSelectContract.View {

    private List<KDMoveListItem> mListOfKDMoves;
    private KDMoveSelectContract.Presenter mPresenter;

    private RecyclerView mRecyclerView;
    private MyListAdapter mAdapter;
    private String mCharacterCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kdmove_select);

        // if character is not selected, finish activity with RESULT_CANCELED (or custom result?)
        Bundle intentExtras = getIntent().getExtras();

        if (intentExtras.containsKey(MainActivity.CHARACTER_EXTRA))
            mCharacterCode = intentExtras.getString(MainActivity.CHARACTER_EXTRA);
        else cancelActivity();

        // find the RecyclerView
        if (mRecyclerView == null) {
//            Log.d(TAG, "onStart: mRecyclerView is null. Creating reference...");
            mRecyclerView = (RecyclerView) findViewById(R.id.rv_kdmoves);
        }

        // use dependency injection for this
        new KDMoveSelectPresenter(
                this,
                CharacterDatabase.getInstance(getApplicationContext())
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPresenter.start();
    }

    /*------------------------*\
    * View Interface Functions *
    \*------------------------*/

    @Override
    public void setPresenter(KDMoveSelectContract.Presenter presenter) {
        mPresenter = presenter;
    }

    /**
     * AKA setKDMoveList. Presenter returns a list of KD moves from the database
     * @param kdMoveList the list of KD moves
     */
    @Override
    public void cacheKDMoveList(List<KDMoveListItem> kdMoveList) {
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
        mAdapter = new MyListAdapter(mListOfKDMoves);

//        Log.d(TAG, "Setting mRecyclerView's Adapter...");
        mRecyclerView.setAdapter(mAdapter);
    }

    /*-----*\
    * Other *
    \*-----*/

    /**
     * Called by ViewHolder.
     * @param kdMoveListItem the clicked list item
     */
    public void onListItemClick(KDMoveListItem kdMoveListItem) {
        String moveName = kdMoveListItem.getMoveName();

        Intent intent = new Intent();

        intent.putExtra(MainActivity.KD_MOVE_EXTRA, moveName);
        setResult(RESULT_OK, intent);

        finish();
    }

    // TODO: Handle cancellation in MainActivity!
    private void cancelActivity() {
        //Intent intent = new Intent();

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

        private List<KDMoveListItem> mList;

        MyListAdapter(List<KDMoveListItem> list) {
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

            public void bind(KDMoveListItem kdmli) {
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
