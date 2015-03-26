
package hwdroid.widget.item;

import hwdroid.widget.itemview.ItemView;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hw.droid.R;

/**
 * Base class for all items used in HWDroid. An item represents a wrapper of
 * data. Each item contains at least all the information needed to display a
 * single row in a ListView.
 */
public abstract class Item {

    /**
     * the left widget's tyle.
     */
    public enum Type {
        NORMAL_MODE,
        CHECK_MODE,
        RADIO_MODE,
        IMAGE_MODE
    }

    protected SparseArray<Object> mTags;
    protected Object mTag;

    /**
     * Set to true when this item is enabled
     */
    protected boolean mEnabled;

    /**
     * Set to true when this item is checked
     */
    protected boolean mCheckedStatus;

    /**
     * Set to type when this left widget was used.
     */
    protected Type mType;

    /**
     * Create a new item.
     */
    public Item() {
        // By default, an item is enabled
        mEnabled = true;
        mType = Type.NORMAL_MODE;
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setTypeMode(Type mode) {
        mType = mode;
    }

    public Type getTypeMode() {
        return mType;
    }

    /**
     * Set check status.
     *
     * @param status value
     */
    public void setChecked(boolean status) {
        mCheckedStatus = status;
    }

    /**
     * return check status.
     */
    public boolean isChecked() {
        return mCheckedStatus;
    }

    /**
     * Return the tag associated to that item.
     *
     * @return The tag associated to this item.
     */
    public Object getTag() {
        return mTag;
    }

    /**
     * Set the tag associated with this item. A tag is often used to store extra
     * information.
     *
     * @param tag The tag associated to this item
     */
    public void setTag(Object tag) {
        mTag = tag;
    }

    /**
     * Return the tag associated with this item and the specified key.
     *
     * @param key The key of the tag to retrieve
     * @return The tag associated to the key <em>key</em> or null if no tags are
     * associated to that key
     */
    public Object getTag(int key) {
        return (mTags == null) ? null : mTags.get(key);
    }

    /**
     * Set a tag associated with this item and a key. A tag is often used to
     * store extra information.
     *
     * @param key The key for the specified tag
     * @param tag A tag that will be associated to that item
     */
    public void setTag(int key, Object tag) {
        if (mTags == null) {
            mTags = new SparseArray<Object>();
        }
        mTags.put(key, tag);
    }

    /**
     * Return a view that is associated to the current item. The returned view
     * is normally capable of being a good recipient for all item's information.
     *
     * @param context The Context in which the {@link ItemView} will be used
     * @param parent  The parent View of that new View. The parent is usually the
     *                parent ListView and may be used to retrieve the correct
     *                LayoutParams type.
     * @return A new allocated view for the current Item
     */
    public abstract ItemView newView(Context context, ViewGroup parent);

    /**
     * Helper method to inflate a layout using a given Context and a layoutID.
     *
     * @param context  The current context
     * @param layoutID The identifier of the layout to inflate
     * @return A newly inflated view
     */
    protected static ItemView createCellFromXml(Context context, int layoutID, ViewGroup parent) {
        return (ItemView) LayoutInflater.from(context).inflate(layoutID, parent, false);
    }

    /**
     * Inflate this Item from an XML resource.
     *
     * @param r
     * @param parser
     * @param attrs
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        TypedArray a = r.obtainAttributes(attrs, R.styleable.Item);
        mEnabled = a.getBoolean(R.styleable.Item_hw_enabled, mEnabled);
        a.recycle();
    }

}
