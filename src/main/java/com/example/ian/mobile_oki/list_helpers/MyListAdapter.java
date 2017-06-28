package com.example.ian.mobile_oki.list_helpers;

import android.support.v7.widget.RecyclerView;
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

public class MyListAdapter extends RecyclerView.Adapter<MyListItemViewHolder> {

    static String[] mCharNames; // Temporary variable which holds an array of character names
    static String[] mCharShort; // Temporary variable which holds an array of character names
    static ListItemClickListener mOnClickListener;

    /**
     * Interface for receiving click events.
     */
    public interface ListItemClickListener{
        void onListItemClick(Button btn);
    }

    public MyListAdapter(String[] chars, String[] chrs, ListItemClickListener listener){
        mCharNames = chars;
        mCharShort = chrs;
        mOnClickListener = listener;
    }


    @Override
    public MyListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.character_select_list_item, parent, false);

        return new MyListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyListItemViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mCharNames == null) return 0;
        return mCharNames.length;
    }
}
