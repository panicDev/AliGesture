package hwdroid.dialog;

import hwdroid.dialog.item.DropdownDialogTextItem;
import hwdroid.widget.ItemAdapter;
import hwdroid.widget.item.Item;

import android.content.Context;
import android.view.View;

public class ItemDialogType extends DialogType {

    public ItemDialogType(Context context,
                          DropDownDialog popupWindow,
                          DialogInterface dialogInterface,
                          ItemAdapter adapter,
                          View menuView) {
        super(context, popupWindow, dialogInterface, adapter, menuView);
    }

    @Override
    public Item createItemView(String text, boolean selected) {
        DropdownDialogTextItem item = new DropdownDialogTextItem(text);
        item.setEnabled(true);
        item.setTag(mOnClickListener);
        return item;
    }

    @Override
    public void showChild() {
        for (int i = 0; i < mItemList.size(); i++) {
            Item item = mItemList.get(i);
            item.setTag(mOnClickListener);
            mAdapter.add(item);
        }
    }

    @Override
    public boolean doDismiss() {
        return true;
    }

}
