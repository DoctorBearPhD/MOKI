package com.example.ian.mobile_oki.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kdmove_select);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.kd_move_select_menu, menu);

        MenuItem item = menu.findItem(R.id.kdSortOrderSpinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.kd_sort_values,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        CharSequence sortOrderLabel = mPresenter.getSortOrder();
        int currentSortPos = 0;

        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i) != null) {
                if (adapter.getItem(i).toString().equalsIgnoreCase(sortOrderLabel.toString())) {
                    currentSortPos = i;
                    break;
                }
            }
        }

        spinner.setSelection(currentSortPos);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long rowId) {
                mPresenter.setSortOrder(adapter.getItem(pos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return true;
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

    @Override
    public void displayKDMoveList() {
        // get data from server and cache it
        mListOfKDMoves = mPresenter.getListOfKDMoves();
        // show in recyclerview
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);

        mRecyclerView.setHasFixedSize(true);

//        Log.d(TAG, "Creating new Adapter...");
        MyListAdapter adapter = new MyListAdapter(mListOfKDMoves);

//        Log.d(TAG, "Setting mRecyclerView's Adapter...");
        mRecyclerView.setAdapter(adapter);
        // scroll when displayed
        mPresenter.displayFinished();
    }

    @Override
    public void scrollToCurrentItem(KDMoveListItem move) {
        int index = mListOfKDMoves.indexOf(move);
        mRecyclerView.scrollToPosition(index);
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
