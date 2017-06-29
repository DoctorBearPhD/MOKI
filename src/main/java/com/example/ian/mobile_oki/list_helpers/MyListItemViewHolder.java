package com.example.ian.mobile_oki.list_helpers;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.ian.mobile_oki.R;

/**
 * Created by Ian on 6/27/2017.
 */

public class MyListItemViewHolder
        extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    private Button mBtn;

    public MyListItemViewHolder(View view) {
        super(view);
        mBtn = (Button) view.findViewById(R.id.btn_character);
        mBtn.setOnClickListener(this); // this class implements OnClickListener, so pass this
    }

    // This method should be fast, so I should probably minimize its content... TODO!
    public void bind(int pos) {
        mBtn.setText(MyListAdapter.mCharNames[pos]);
        mBtn.setTag(MyListAdapter.mCharShort[pos]);
    }

    @Override
    public void onClick(View view) {
        if (view instanceof Button) {
            MyListAdapter.mOnClickListener.onListItemClick((Button) view);

            Log.d("viewholder: ",
                    "layoutposition=" + getLayoutPosition() +
                            "; adapterposition=" + getAdapterPosition());
        }
    }
}
