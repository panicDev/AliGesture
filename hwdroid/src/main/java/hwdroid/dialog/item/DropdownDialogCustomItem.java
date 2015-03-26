package hwdroid.dialog.item;

import hwdroid.widget.item.Item;
import hwdroid.widget.itemview.ItemView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.hw.droid.R;

public class DropdownDialogCustomItem extends Item {
    public int mCustomView;

    public DropdownDialogCustomItem(View v) {
    }

    public DropdownDialogCustomItem(int viewId) {
        mCustomView = viewId;
    }

    @Override
    public ItemView newView(Context context, ViewGroup parent) {
        ItemView view = createCellFromXml(context, R.layout.hw_dropdown_dialog_custom_item_view, parent);
        view.prepareItemView();
        return view;
    }
}
