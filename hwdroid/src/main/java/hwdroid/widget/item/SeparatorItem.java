
package hwdroid.widget.item;

import hwdroid.widget.itemview.ItemView;

import android.content.Context;
import android.view.ViewGroup;

import com.hw.droid.R;

/**
 * Act as a separator between important ListView sections. A separator display
 * text on a single line.
 */
public class SeparatorItem extends TextItem {

    /**
     * @hide
     */
    public SeparatorItem() {
        this(null);
    }

    /**
     * Construct a SeparatorItem made of the given text
     *
     * @param text The text for this SeparatorItem
     */
    public SeparatorItem(String text) {
        super(text);
        setEnabled(false);
    }

    @Override
    public ItemView newView(Context context, ViewGroup parent) {
        return createCellFromXml(context, R.layout.hw_separator_item_view, parent);
    }

}
