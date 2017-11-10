package com.example.ian.mobile_oki.view.load;

import android.support.annotation.NonNull;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.ian.mobile_oki.R;
import com.example.ian.mobile_oki.data.OkiSetupDataObject;
import com.example.ian.mobile_oki.databinding.LoadListItemBinding;
import com.xwray.groupie.databinding.BindableItem;

/**
 * Created by Ian on 10/31/2017.
 */

public class LoadSetupChildItem extends BindableItem<LoadListItemBinding> {

    private final OkiSetupDataObject osdo;
    private final Long rowId;

    public LoadSetupChildItem(OkiSetupDataObject osdo, Long rowId) {
        this.osdo = osdo;
        this.rowId = rowId;
    }

    @Override
    public void bind(@NonNull LoadListItemBinding viewBinding, int position) {
        // bind the data to the views
        String itemText = "";

        for (int i = 0; i < osdo.getOkiMoves().length; i++) {
            String move = osdo.getOkiMoves()[i];
            if (move != null)
                itemText = itemText.concat( move + ", ");
        }

        itemText = itemText.substring(0, itemText.length() - 2); // removes last ", "
        viewBinding.tvLoadOkiSetupOkis.setText(itemText);

        // alternate bg colors
//        viewBinding.llLoadOkiSetup.setBackgroundColor(
//                (position % 2 == 0) ?
//                        OkiUtil.getColor(R.color.bgAccent) :
//                        OkiUtil.getColor(R.color.bgLight));
    }

    @Override
    public int getLayout() {
        return R.layout.load_list_item;
    }

    // Allow the Item to be swiped left or right.
    @Override
    public int getSwipeDirs() {
        return ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
    }

    public OkiSetupDataObject getOSDO(){
        return osdo;
    }

    public Long getRowId(){
        return rowId;
    }
}
