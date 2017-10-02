package com.example.ian.mobile_oki.view;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.kd_sort_def:
            case R.id.kd_sort_move:
            case R.id.kd_sort_startup:
            case R.id.kd_sort_kda:
            case R.id.kd_sort_kdra:
            case R.id.kd_sort_kdbra:
                mPresenter.setSortOrder(item.getTitle());
                return true;
            case R.id.kd_list_toggle:
                toggleListDetail(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.kd_list_toggle).setChecked(mPresenter.getKdDetailLevel());
        updateToggleIcon(item, item.isChecked());
        return super.onPrepareOptionsMenu(menu);
    }

    private void toggleListDetail(MenuItem item) {
        //toggle icon
        item.setChecked(!item.isChecked());
        updateToggleIcon(item, item.isChecked());

        // save scroll position
        int scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                .findFirstVisibleItemPosition();

        // toggle list detail
        mPresenter.toggleKdDetailLevel();
        displayKDMoveList(true);

        mRecyclerView.scrollToPosition(scrollPosition);
    }

    /**
     * For some reason, Android's {@link MenuItem}s can't have checkboxes (or logical equivalents).
     * This works around that limitation.
     *
     * @param detailLevel True to show the "collapse" icon, false to show the "expand" icon.
     */
    private void updateToggleIcon(MenuItem item, boolean detailLevel){
        StateListDrawable stateListDrawable =
                (StateListDrawable) getResources().getDrawable(R.drawable.list_toggle);

        int[] state = {detailLevel ? android.R.attr.state_checked : android.R.attr.state_empty};

        stateListDrawable.setState(state);
        item.setIcon(stateListDrawable.getCurrent());
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
    public void displayKDMoveList(boolean keepScrollPosition) {
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

        // update the sort order text
        ((TextView) findViewById(R.id.tv_kd_sort_order)).setText(mPresenter.getSortOrder());

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
        private boolean mOkiDetailLevel;

        MyListAdapter(ArrayList<KDMoveListItem> list) {
            mList = list;
            mOkiDetailLevel = mPresenter.getKdDetailLevel();
        }


        @Override
        public MyListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            KdmoveSelectListItemBinding binding = DataBindingUtil.inflate(
                    getLayoutInflater(),
                    R.layout.kdmove_select_list_item,
                    parent,
                    false);

            if (!mOkiDetailLevel) {
                binding.tvKda.setVisibility(View.GONE);
                binding.tvKdra.setVisibility(View.GONE);
                binding.tvKdbra.setVisibility(View.GONE);
                binding.tvStartup.setVisibility(View.GONE);
                binding.tvActive.setVisibility(View.GONE);
                binding.tvRecovery.setVisibility(View.GONE);
            } else {
                binding.tvKda.setVisibility(View.VISIBLE);
                binding.tvKdra.setVisibility(View.VISIBLE);
                binding.tvKdbra.setVisibility(View.VISIBLE);
                binding.tvStartup.setVisibility(View.VISIBLE);
                binding.tvActive.setVisibility(View.VISIBLE);
                binding.tvRecovery.setVisibility(View.VISIBLE);
            }

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
