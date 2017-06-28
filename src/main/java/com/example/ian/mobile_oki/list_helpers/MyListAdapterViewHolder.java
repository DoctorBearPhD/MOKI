package com.example.ian.mobile_oki.list_helpers;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.ian.mobile_oki.R;

/**
 * Created by Ian on 6/27/2017.
 */

public class MyListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Button btn;

    public MyListAdapterViewHolder(View view){
        super(view);
        btn = (Button) view.findViewById(R.id.btn_character);
    }

    // This method should be fast, so I should probably minimize its content... TODO!
    public void bind(int pos){
        btn.setText(MyListAdapter.mCharNames[pos]);
        btn.setTag(MyListAdapter.mCharShort[pos]);
    }

    @Override
    public void onClick(View view) {
        // TODO: implement click function
    }
}
