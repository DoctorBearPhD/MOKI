package com.example.ian.mobile_oki.view.load;

import android.support.annotation.NonNull;
import android.view.View;

import com.example.ian.mobile_oki.R;
import com.example.ian.mobile_oki.databinding.LoadHeaderItemBinding;
import com.xwray.groupie.ExpandableGroup;
import com.xwray.groupie.ExpandableItem;
import com.xwray.groupie.databinding.BindableItem;

/**
 * Grouping item for Setups in the Load Activity.
 *
 * Created by Ian on 10/31/2017.
 */

public class LoadSetupHeaderItem extends BindableItem<LoadHeaderItemBinding> implements ExpandableItem{

    private String title, subtitle;
    private ExpandableGroup expandableGroup;

    public LoadSetupHeaderItem(String title, String subtitle){
        this.title = title;
        this.subtitle = subtitle;
    }

    @Override
    public void bind(@NonNull LoadHeaderItemBinding viewBinding, int position) {
        viewBinding.tvLoadHeaderTitle.setText(title);
        viewBinding.tvLoadHeaderSubtitle.setText(subtitle);

        // set click listener for toggling expand/collapse
        viewBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandableGroup.onToggleExpanded();
            }
        });
    }

    @Override
    public int getLayout() {
        return R.layout.load_header_item;
    }

    @Override
    public void setExpandableGroup(@NonNull ExpandableGroup onToggleListener) {
        this.expandableGroup = onToggleListener;
    }
}
