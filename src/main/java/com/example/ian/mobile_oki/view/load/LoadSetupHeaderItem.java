package com.example.ian.mobile_oki.view.load;

import android.support.annotation.NonNull;

import com.example.ian.mobile_oki.R;
import com.example.ian.mobile_oki.databinding.LoadHeaderItemBinding;
import com.xwray.groupie.databinding.BindableItem;

/**
 * Grouping item for Setups in the Load Activity.
 *
 * Created by Ian on 10/31/2017.
 */

public class LoadSetupHeaderItem extends BindableItem<LoadHeaderItemBinding>{

    private String title, subtitle;

    public LoadSetupHeaderItem(String title, String subtitle){
        this.title = title;
        this.subtitle = subtitle;
    }

    @Override
    public void bind(@NonNull LoadHeaderItemBinding viewBinding, int position) {
        viewBinding.tvLoadHeaderTitle.setText(title);
        viewBinding.tvLoadHeaderSubtitle.setText(subtitle);
    }

    @Override
    public int getLayout() {
        return R.layout.load_header_item;
    }
}
