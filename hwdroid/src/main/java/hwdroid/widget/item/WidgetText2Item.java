
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
 * A DrawableItem displays a single Drawable on the left of the itemview and a
 * description text on the right. A DrawableItem takes care of adapting itself
 * depending on the presence of its Drawable.
 */
public class WidgetText2Item extends TextItem {

    /**
     * The resource identifier for the viewgroup.
     */
    public ViewGroup leftviewgroup;
    public ViewGroup rightviewgroup;
    public int mWidgetTagKey;

    /**
     * @hide
     */
    public WidgetText2Item() {
        this(null);
    }

    /**
     * Constructs a new DrawableItem that has no Drawable and displays the given
     * text. Used as it, a DrawableItem is very similar to a TextItem
     *
     * @param text The text of this DrawableItem
     */
    public WidgetText2Item(String text) {
        this(text, "");
    }

    /**
     * Constructs a new WidgetText2Item that has no Drawable and displays the given
     * text. Used as it, a WidgetText2Item is very similar to a TextItem
     *
     * @param text The text of this WidgetText2Item
     */
    public WidgetText2Item(String text, String subtext) {
        this(text, subtext, null, null, 0);
    }

    /**
     * Constructs a new WidgetText2Item that has no Drawable and displays the given
     * text. Used as it, a WidgetText2Item is very similar to a TextItem
     *
     * @param text The text of this WidgetText2Item
     */
    public WidgetText2Item(String text, String subtext, LinearLayout leftviewgroup, int widgetTagKey) {
        this(text, subtext, leftviewgroup, null, widgetTagKey);
    }

    /**
     * Constructs a new WidgetText2Item that has no Drawable and displays the given
     * text. Used as it, a WidgetText2Item is very similar to a TextItem
     *
     * @param text The text of this WidgetText2Item
     */
    public WidgetText2Item(String text, String subtext, LinearLayout leftviewgroup, LinearLayout rightviewgroup, int widgetTagKey) {
        super(text);
        this.leftviewgroup = leftviewgroup;
        this.rightviewgroup = rightviewgroup;
        this.mSubText = subtext;
        this.mWidgetTagKey = widgetTagKey;
    }

    @Override
    public ItemView newView(Context context, ViewGroup parent) {
        return createCellFromXml(context, R.layout.hw_widget_text_2_item_view, parent);
    }

    @Override
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs);

        TypedArray a = r.obtainAttributes(attrs, R.styleable.DrawableText2Item);
        //drawableId = a.getResourceId(R.styleable.DrawableText2Item_drawable, 0);
        a.recycle();
    }

}
