package hwdroid.dialog;

import hwdroid.dialog.item.DropdownDialogMultiChoiceItem;
import hwdroid.widget.ItemAdapter;
import hwdroid.widget.item.Item;

import android.content.Context;
import android.view.View;

public class MultiChoiceItemDialogType extends DialogType {

    public MultiChoiceItemDialogType(Context context, DropDownDialog popupWindow,
                                     DialogInterface dialogInterface,
                                     ItemAdapter adapter,
                                     View menuView) {
        super(context, popupWindow, dialogInterface, adapter, menuView);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public Item createItemView(String text, boolean selected) {
        DropdownDialogMultiChoiceItem item = new DropdownDialogMultiChoiceItem(text);
        item.setEnabled(true);
        item.setChecked(selected);
        return item;
    }

    @Override
    public void showChild() {
        for (int i = 0; i < mItemList.size(); i++) {
            Item item = mItemList.get(i);
            item.setEnabled(true);
            mAdapter.add(item);
        }
    }

    public void callBack(int index) {
        if (mMultiChoiceClickListener != null) {
            DropdownDialogMultiChoiceItem item = (DropdownDialogMultiChoiceItem) mItemList.get(index);
            item.setChecked(!item.isChecked());
            mMultiChoiceClickListener.onClick(mDialogInterface, index, item.isChecked());

            if (!mHandler.hasMessages(HANDLER_UPDATE_ADAPTER)) {
                mHandler.sendEmptyMessageDelayed(HANDLER_UPDATE_ADAPTER, 50);
            }
        }
    }

}
