
package hwdroid.widget.itemview;

import hwdroid.widget.item.Item;
import hwdroid.widget.item.SeparatorItem;
import hwdroid.widget.item.TextItem;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * View representation of the {@link hwdroid.widget.item.SeparatorItem}.
 */
public class SeparatorItemView extends TextView implements ItemView {

    public SeparatorItemView(Context context) {
        this(context, null);
    }

    public SeparatorItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeparatorItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void prepareItemView() {
    }

    public void setObject(Item object) {
        final TextItem item = (TextItem) object;
        setText(item.mText);
    }

    @Override
    public void setSubTextSingleLine(boolean enabled) {
    }
}
