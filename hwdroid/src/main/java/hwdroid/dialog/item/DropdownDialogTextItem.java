package hwdroid.dialog.item;

import hwdroid.widget.item.TextItem;
import hwdroid.widget.itemview.ItemView;

import android.content.Context;
import android.view.ViewGroup;

import com.hw.droid.R;

public class DropdownDialogTextItem extends TextItem {

    public DropdownDialogTextItem() {
    }

    public DropdownDialogTextItem(String text) {
        this.mText = text;
    }

    @Override
    public ItemView newView(Context context, ViewGroup parent) {
        ItemView view = createCellFromXml(context, R.layout.hw_dropdown_dialog_text_item_view, parent);
        view.prepareItemView();
        return view;
    }
}
