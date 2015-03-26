
package hwdroid.widget.item;

import hwdroid.widget.itemview.ItemView;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hw.droid.R;

/**
 * A WidgetText2CBItem displays a single Widget on the left of the itemview and a
 * description status(checkbox, radiobutton) on the right. A WidgetText2CBItem takes care of adapting itself
 * depending on the presence of its widget.
 */
public class WidgetText2CBItem extends TextItem {

    /**
     * The resource identifier for the viewgroup.
     */
    public ViewGroup leftviewgroup;
    public ViewGroup rightviewgroup;
    public int mWidgetTagKey;

    /**
     * @hide
     */
    public WidgetText2CBItem() {
        this(null);
    }

    /**
     * Constructs a new DrawableItem that has no Drawable and displays the given
     * text. Used as it, a DrawableItem is very similar to a TextItem
     *
     * @param text The text of this DrawableItem
     */
    public WidgetText2CBItem(String text) {
        this(text, "");
    }

    /**
     * Constructs a new WidgetText2Item that has no Drawable and displays the given
     * text. Used as it, a WidgetText2Item is very similar to a TextItem
     *
     * @param text The text of this WidgetText2Item
     */
    public WidgetText2CBItem(String text, String subtext) {
        this(text, subtext, null, 0);
    }

    /**
     * Constructs a new WidgetText2Item that has no Drawable and displays the given
     * text. Used as it, a WidgetText2Item is very similar to a TextItem
     *
     * @param text The text of this WidgetText2Item
     */
    public WidgetText2CBItem(String text, String subtext, LinearLayout leftviewgroup, int widgetTagKey) {
        super(text);
        this.leftviewgroup = leftviewgroup;
        this.mSubText = subtext;
        this.mWidgetTagKey = widgetTagKey;
    }

    @Override
    public ItemView newView(Context context, ViewGroup parent) {
        return createCellFromXml(context, R.layout.hw_widget_text_2_cb_item_view, parent);
    }

    @Override
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs);

        TypedArray a = r.obtainAttributes(attrs, R.styleable.DrawableText2Item);
        //drawableId = a.getResourceId(R.styleable.DrawableText2Item_drawable, 0);
        a.recycle();
    }

}
