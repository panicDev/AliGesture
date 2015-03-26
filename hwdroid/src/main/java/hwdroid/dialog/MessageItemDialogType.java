package hwdroid.dialog;

import hwdroid.dialog.item.DropdownDialogMessageItem;
import hwdroid.widget.ItemAdapter;
import hwdroid.widget.item.Item;

import android.content.Context;
import android.view.View;

public class MessageItemDialogType extends DialogType {

    public MessageItemDialogType(Context context,
                                 DropDownDialog popupWindow,
                                 DialogInterface dialogInterface,
                                 ItemAdapter adapter,
                                 View menuView) {
        super(context, popupWindow, dialogInterface, adapter, menuView);
    }

    @Override
    public Item createItemView(String text, boolean selected) {
        DropdownDialogMessageItem item = new DropdownDialogMessageItem(text);
        item.setEnabled(false);
        return item;
    }

    @Override
    public void showChild() {
        for (int i = 0; i < mMessageList.size(); i++) {
            Item item = createItemView(mMessageList.get(i), false);
            mAdapter.add(item);
        }
    }

}
