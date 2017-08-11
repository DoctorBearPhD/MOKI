package com.example.ian.mobile_oki.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.ian.mobile_oki.R;
import com.example.ian.mobile_oki.contracts.OkiMoveSelectContract;
import com.example.ian.mobile_oki.data.OkiMoveListItem;
import com.example.ian.mobile_oki.databinding.OkimoveSelectListItemBinding;
import com.example.ian.mobile_oki.logic.OkiMoveSelectPresenter;

import java.util.ArrayList;

/**
 * Select-screen for filling the Timeline with Moves.
 * Created by Ian on 8/5/2017.
 */

public class OkiMoveSelectActivity extends AppCompatActivity implements OkiMoveSelectContract.View {

    OkiMoveSelectContract.Presenter mPresenter;

    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okimove_select);

        if (mRecyclerView == null)
            mRecyclerView = (RecyclerView) findViewById(R.id.rv_okimoves);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.oki_sel);

        attachPresenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPresenter.start();
    }

    //

    /*------------------------*\
    * View Interface Functions *
    \*------------------------*/

    @Override
    public void setPresenter(OkiMoveSelectContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void attachPresenter() {
        if (mPresenter == null){
            setPresenter(new OkiMoveSelectPresenter(this));
        }
    }

    @Override
    public void displayOkiMoveList() {
        ArrayList<OkiMoveListItem> moveList = mPresenter.getListOfOkiMoves();

        // show moves
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);

        MyListAdapter adapter = new MyListAdapter(moveList);
        mRecyclerView.setAdapter(adapter);
    }

    /*----------*\
    * More Stuff *
    \*----------*/

    private void onListItemClick(OkiMoveListItem okiMove) {
        mPresenter.updateCurrentOkiMove(okiMove);
        setResult(RESULT_OK);
        finish();
    }

    /*-------------*\
    * Inner Classes *
    \*-------------*/

    /**
     * Created by Ian on 8/5/2017.
     * <p>
     * Generic Adapter used for filling the RecyclerView lists.
     * <p>
     * Adapted from {@link KDMoveSelectActivity}
     */
    class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.MyListItemViewHolder> {

        private ArrayList<OkiMoveListItem> mList;

        MyListAdapter(ArrayList<OkiMoveListItem> list) {
            mList = list;
        }

        @Override
        public MyListItemViewHolder onCreateViewHolder(ViewGroup holder, int position) {

            OkimoveSelectListItemBinding binding = DataBindingUtil.inflate(
                    getLayoutInflater(),
                    R.layout.okimove_select_list_item,
                    holder,
                    false);

            return new MyListItemViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(MyListItemViewHolder holder, int position) {
            holder.bind(mList.get(position));
            holder.itemView.setOnClickListener(holder);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class MyListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private final OkimoveSelectListItemBinding mBinding;

            MyListItemViewHolder(OkimoveSelectListItemBinding binding) {
                super(binding.getRoot());

                mBinding = binding;
            }

            void bind(OkiMoveListItem item){
                mBinding.setOkimove(item);
                mBinding.executePendingBindings();
            }

            @Override
            public void onClick(View view) {
                onListItemClick(mBinding.getOkimove());
            }
        } // end of MyListItemViewHolder
    } // end of MyListAdapter
}
