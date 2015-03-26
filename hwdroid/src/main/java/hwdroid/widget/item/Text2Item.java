
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

import com.hw.droid.R;

/**
 * An {@link hwdroid.widget.item.Item} that contains two Strings : a text and a subtitle. The
 * representation of this {@link hwdroid.widget.item.Item} is a view containing two lines of text.
 * If you want to be sure, the subtitle can occupy more than a single line,
 * please use a {@link SubtextItem}
 */
public class Text2Item extends TextItem {


    /**
     * @hide
     */
    public Text2Item() {
    }

    /**
     * Construct a new SubtitleItem with the specified text and subtitle.
     *
     * @param text     The text for this item
     * @param subtitle The item's subtitle
     */
    public Text2Item(String text) {
        this(text, "");
    }

    /**
     * Construct a new SubtitleItem with the specified text and subtitle.
     *
     * @param text     The text for this item
     * @param subtitle The item's subtext
     */
    public Text2Item(String text, String subtext) {
        super(text);
        this.mSubText = subtext;
    }

    @Override
    public ItemView newView(Context context, ViewGroup parent) {
        return createCellFromXml(context, R.layout.hw_text_2_item_view, parent);
    }

    @Override
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs);

        TypedArray a = r.obtainAttributes(attrs, R.styleable.Text2Item);
        mSubText = a.getString(R.styleable.Text2Item_hw_subtext);
        a.recycle();
    }
}
