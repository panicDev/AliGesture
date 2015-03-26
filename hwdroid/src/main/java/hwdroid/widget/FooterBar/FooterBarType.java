package hwdroid.widget.FooterBar;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;

public interface FooterBarType {

    public static interface OnFooterItemClick {
        void onFooterItemClick(View view, int id);
    }

    public FooterBarItem addItem(int itemId, CharSequence txt, Drawable icon);

    /**
     * update item(redraw) when added or removed items.
     */
    public void updateItems();

    /**
     * remove item.
     *
     * @param itemId
     */
    public void removeItem(int itemId);

    /**
     * clear all items.
     */
    public void clear();

    /**
     * return items's count.
     *
     * @return : count
     */
    public int size();

    /**
     * set text color.
     *
     * @param colors : colors list.
     */
    public void setTextColor(ColorStateList colors);

    /**
     * set text color.
     *
     * @param colorId : color resource xml id.
     */
    public void setTextColor(int colorId);

    /**
     * set text color.
     *
     * @param itemId  : item id.
     * @param colorId : color resource xml id.
     */
    public void setItemTextColor(int itemId, int colorId);

    /**
     * set text clolr.
     *
     * @param itemId : item id.
     * @param colors : colors list.
     */
    public void setItemTextColor(int itemId, ColorStateList colors);

    /**
     * set text font size.
     *
     * @param size
     */
    public void setTextSize(float size);

    /**
     * set text font size.
     *
     * @param itemId
     * @param size
     */
    public void setItemTextSize(int itemId, float size);

    /**
     * @param itemId
     * @param resId
     */
    public void setItemBackgroundResource(int itemId, int resId);

    public void setItemBackgroundColor(int itemId, int colorId);

    public void setFooterBarBackgroundResource(int resId);

    public void setFooterBarBackgroundColor(int colorId);

    public void setItemTag(int itemId, Object tag);

    public Object getItemTag(int itemId);

    public void setItemEnable(int itemId, boolean enabled);

    public boolean isItemEnable(int itemId);

    public void setItemSelected(int itemId, boolean selected);

    public boolean isItemSelected(int itemId);

    public void setOnFooterItemClick(OnFooterItemClick listener);

    public void setPrimaryItemCount(int count);
}
