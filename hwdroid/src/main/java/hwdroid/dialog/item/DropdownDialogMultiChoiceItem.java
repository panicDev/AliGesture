package hwdroid.dialog.item;

import hwdroid.widget.item.TextItem;
import hwdroid.widget.itemview.ItemView;

import android.content.Context;
import android.view.ViewGroup;

import com.hw.droid.R;

public class DropdownDialogMultiChoiceItem extends TextItem {

    public DropdownDialogMultiChoiceItem() {
    }

    public DropdownDialogMultiChoiceItem(String text) {
        this.mText = text;
    }

    public DropdownDialogMultiChoiceItem(String text, boolean status) {
        this.mText = text;
        this.setChecked(status);
    }

    @Override
    public ItemView newView(Context context, ViewGroup parent) {
        ItemView view = createCellFromXml(context, R.layout.hw_dropdown_dialog_multi_choice_item_view, parent);
        view.prepareItemView();
        return view;
    }
}
