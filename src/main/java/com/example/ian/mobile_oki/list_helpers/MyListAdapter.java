package com.example.ian.mobile_oki.list_helpers;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ian.mobile_oki.R;

/**
 * Created by Ian on 6/27/2017.
 *
 * Generic Adapter used for filling the RecyclerView lists.
 */

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapterViewHolder> {

    static String[] mCharNames; // Temporary variable which holds an array of character names
    static String[] mCharShort; // Temporary variable which holds an array of character names

    public MyListAdapter(String[] chars, String[] chrs){
        mCharNames = chars;
        mCharShort = chrs;
    }


    @Override
    public MyListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.character_select_list_item, parent, false);

        return new MyListAdapterViewHolder(view, new MyListAdapterViewHolder.IMyViewHolderClicks() {
            @Override
            public void onButtonClick(Button btn) {
                Log.d("TRY IT: ","button tag is " + btn.getTag());
                // TODO (next) : Figure this out or just implement the simpler version
            }
        });
    }

    @Override
    public void onBindViewHolder(MyListAdapterViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mCharNames == null) return 0;
        return mCharNames.length;
    }
}
