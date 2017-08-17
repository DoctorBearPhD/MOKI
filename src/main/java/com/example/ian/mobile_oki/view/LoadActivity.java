package com.example.ian.mobile_oki.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.ian.mobile_oki.OkiApp;
import com.example.ian.mobile_oki.R;
import com.example.ian.mobile_oki.contracts.LoadDataContract;
import com.example.ian.mobile_oki.data.CharacterListItem;
import com.example.ian.mobile_oki.logic.LoadDataPresenter;

import java.util.ArrayList;

/**
 * Created by Ian on 8/17/2017.
 */

public class LoadActivity extends AppCompatActivity
        implements LoadDataContract.View,
        AdapterView.OnItemSelectedListener {


    LoadDataContract.Presenter mPresenter;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        // Create presenter, which attaches this view to itself
        if (mPresenter == null)
            new LoadDataPresenter(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();

        super.onDestroy();
    }


    /*------------------------*\
    * View Interface Functions *
    \*------------------------*/

    @Override
    public void setPresenter(LoadDataContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void populateCharacterSpinner() {
        // TODO: Populate [character select spinner]

          // Find Spinner and RecyclerView
        Spinner spinner = (Spinner) findViewById(R.id.sp_load_char_spinner);
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv_load_oki_setup_list);

          // Get array of names
        ArrayList<CharacterListItem> characterList = mPresenter.getCharacters();

        String[] fullNames = new String[characterList.size()];
        String c = mPresenter.getCurrentCharacter();
        fullNames[0] = c == null ? "Character" : c;

        for (int i = 1; i < characterList.size(); i++) {
            fullNames[i] = characterList.get(i-1).getCharacterName();
            //Log.d("*v*v*", "populateCharacterSpinner: " + fullNames[i]);
        }
          // make an adapter
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                OkiApp.getContext(),
                R.layout.load_spinner_item,
                fullNames);
          // set layout of dropdown list
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          // set adapter
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);



    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
        // TODO: Handle selection of new item.
          // Update RecyclerView
        // adapter.notifyDataSetChanged();
        // rv.setAdapter(adapter);
        // parent.getItemAtPosition(pos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
//
//    /*-------------*\
//    * Inner Classes *
//    \*-------------*/
//
//    /**
//     * Generic Adapter used for filling the RecyclerView lists. <p>
//     * Adapted from {@link CharacterSelectActivity}.
//     */
//
//    class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.MyListItemViewHolder> {
//
//        private ArrayList<CharacterListItem> mList;
//
//        MyListAdapter(ArrayList<CharacterListItem> list) {
//            mList = list;
//        }
//
//
//        @Override
//        public MyListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//            View view = inflater.inflate(R.layout.character_select_list_item, parent, false);
//
//            return new MyListItemViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(MyListItemViewHolder holder, int position) {
//            // get element from dataset at this position
//
//            CharacterListItem listItemData;
//
//            // if we're within the domain of the list
//            if (position >= 0 && position < getItemCount())
//                listItemData = mList.get(position);
//            else return;
//
//            // bind the data to the view
//            TextView listItemView = (TextView) holder.itemView.findViewById(R.id.tv_character);
//
//            listItemView.setText(listItemData.getCharacterName());
//            listItemView.setTag(listItemData.getCharacterCode());
//            listItemView.setOnClickListener(holder);
//            // alternate bg colors
//            listItemView.setBackgroundColor( (position % 2 == 0) ?
//                    OkiUtil.getColor(R.color.bgAccent) :
//                    OkiUtil.getColor(R.color.bgLight));
//        }
//
//        @Override
//        public int getItemCount() {
//            return mList.size();
//        }
//
//        /**
//         * Custom ViewHolder class.
//         */
//        class MyListItemViewHolder
//                extends RecyclerView.ViewHolder
//                implements View.OnClickListener {
//
//            private TextView mSelection;
//
//
//            MyListItemViewHolder(View itemLayoutView) {
//                super(itemLayoutView);
//
//                mSelection = (TextView) itemLayoutView.findViewById(R.id.tv_character);
//
//                mSelection.setOnClickListener(this); // this class implements OnClickListener, so pass this
//            }
//
//            @Override
//            public void onClick(View view) {
//
//            }
//        }
//    }
}
