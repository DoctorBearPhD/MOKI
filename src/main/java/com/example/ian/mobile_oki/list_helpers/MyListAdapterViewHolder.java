package com.example.ian.mobile_oki.list_helpers;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.ian.mobile_oki.R;

/**
 * Created by Ian on 6/27/2017.
 */

public class MyListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Button mBtn;
    public IMyViewHolderClicks mListener;

    public interface IMyViewHolderClicks { void onButtonClick(Button btn); }

    public MyListAdapterViewHolder(View view, IMyViewHolderClicks listener){
        super(view);

        mListener = listener;

        mBtn = (Button) view.findViewById(R.id.btn_character);
        mBtn.setOnClickListener(this);
    }

    // This method should be fast, so I should probably minimize its content... TODO!
    public void bind(int pos){
        mBtn.setText(MyListAdapter.mCharNames[pos]);
        mBtn.setTag(MyListAdapter.mCharShort[pos]);
    }

    @Override
    public void onClick(View view) {
        if (view instanceof Button)
            mListener.onButtonClick((Button) view);

    }
}
