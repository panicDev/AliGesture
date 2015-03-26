package hwdroid.dialog.item;

import hwdroid.widget.item.TextItem;
import hwdroid.widget.itemview.ItemView;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.hw.droid.R;

public class DropdownDialogMessageItem extends TextItem {

    public DropdownDialogMessageItem() {
    }

    public DropdownDialogMessageItem(String text) {
        this.mText = text;
        Log.i("xy", "ok");
    }

    @Override
    public ItemView newView(Context context, ViewGroup parent) {
        ItemView view = createCellFromXml(context, R.layout.hw_dropdown_dialog_message_item_view, parent);
        view.prepareItemView();
        return view;
    }
}