package hwdroid.widget.FooterBar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.hw.droid.R;

import java.util.ArrayList;

/**
 *
 *
 */
public class FooterBarView extends LinearLayout implements FooterBarType {

    public final static int VERTICAL_ITEM_TYPE = LinearLayout.VERTICAL;
    public final static int HORIZONTAL_ITEM_TYPE = LinearLayout.HORIZONTAL;
    private final int mLayoutID = R.layout.hw_footer_view_layout;

    private Context mContext;
    protected ArrayList<FooterBarItem> mItemList;
    protected OnFooterItemClick mOnFooterBarItemListener;
    protected OnClickListener mOnClickListener;
    protected LinearLayout mContentView;
    protected boolean mSelectedMode = false;

    private int mItemType = HORIZONTAL_ITEM_TYPE;
    private int mMaxItemSize = 2;

    public FooterBarView(Context context) {
        this(context, null);
    }

    public FooterBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater.from(getContext()).inflate(mLayoutID, this);
        this.setOrientation(1);
        mContentView = (LinearLayout) findViewById(R.id.hw_footerbar_content);
        mItemList = new ArrayList<FooterBarItem>();
        mOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {

                int id = ((FooterBarItem) v).getItemId();

                if (mSelectedMode) {
                    for (int i = 0; i < mItemList.size(); i++) {
                        mItemList.get(i).setItemSelected(false);
                    }

                    ((FooterBarItem) v).setItemSelected(true);
                }

                if (mOnFooterBarItemListener != null) {

                    if (onFooterBarViewClick()) return;

                    mOnFooterBarItemListener.onFooterItemClick(v, id);
                }
            }
        };

        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    protected boolean onFooterBarViewClick() {
        return false;
    }

    protected void setMaxItemSize(int size) {
        mMaxItemSize = size;
    }

    public int getMaxItemSize() {
        return mMaxItemSize;
    }

    public void setSelectedMode(boolean mode) {
        mSelectedMode = mode;
    }

    protected void setItemType(int itemType) {
        mItemType = itemType;
    }

    public FooterBarItem addItem(int itemId, CharSequence txt) {
        return addItem(itemId, txt, null, (float) 1.0);
    }

    @Override
    public FooterBarItem addItem(int itemId, CharSequence txt, Drawable icon) {
        return addItem(itemId, txt, icon, (float) 1.0);
    }

    private FooterBarItem addItem(int itemId, CharSequence txt, Drawable icon, float weight) {
        if (mItemList.size() < getMaxItemSize()) {
            FooterBarItem menuItem = new FooterBarItem(mContext);

            menuItem.setItemId(itemId);
            menuItem.setItemTitle(txt);
            menuItem.setItemDrawableIcon(icon);
            //menuItem.setItemWeight(weight);
            menuItem.setOrientation(mItemType);
            menuItem.setOnClickListener(mOnClickListener);
            menuItem.setGravity(Gravity.CENTER);

            mItemList.add(menuItem);
            return menuItem;
        } else {
            return null;
        }
    }

    @Override
    public void removeItem(int itemId) {
        FooterBarItem item = (FooterBarItem) getItem(itemId);
        mItemList.remove(item);
        mContentView.removeView(item);
    }

    public FooterBarItem getItem(int itemId) {
        for (FooterBarItem item : mItemList) {
            if (item != null && item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }

    @Override
    public void clear() {
        mItemList.clear();
        mContentView.removeAllViews();
    }

    @Override
    public void setTextColor(ColorStateList colors) {
        for (FooterBarItem item : mItemList) {
            item.setItemTextColor(colors);
        }
    }

    @Override
    public void setTextColor(int colorId) {
        for (FooterBarItem item : mItemList) {
            item.setItemTextColor(colorId);
        }
    }

    @Override
    public void setItemTextColor(int itemId, int colorId) {
        FooterBarItem item = (FooterBarItem) getItem(itemId);
        if (item != null) {
            item.setItemTextColor(colorId);
        }
    }

    @Override
    public void setItemTextColor(int itemId, ColorStateList colors) {
        FooterBarItem item = (FooterBarItem) getItem(itemId);
        if (item != null) {
            item.setItemTextColor(colors);
        }
    }

    @Override
    public void setTextSize(float size) {
        for (FooterBarItem item : mItemList) {
            item.setItemTextSize(size);
        }
    }

    @Override
    public void setItemTextSize(int itemId, float size) {
        FooterBarItem item = (FooterBarItem) getItem(itemId);
        if (item != null) {
            item.setItemTextSize(size);
        }
    }

    @Override
    public int size() {
        return mItemList.size();
    }

    @Override
    public void setItemTag(int itemId, Object tag) {
        FooterBarItem item = (FooterBarItem) getItem(itemId);
        if (item != null) {
            item.setTag(tag);
        }
    }

    @Override
    public Object getItemTag(int itemId) {
        FooterBarItem item = (FooterBarItem) getItem(itemId);
        if (item != null) {
            return item.getTag();
        }
        return null;
    }

    protected int getItemIndex(int itemId) {
        int index = 0;

        for (FooterBarItem item : mItemList) {
            if (item != null && item.getItemId() == itemId) {
                return index;
            }

            index++;
        }
        return -1;
    }

    @Override
    public void setItemEnable(int itemId, boolean enabled) {
        FooterBarItem item = (FooterBarItem) getItem(itemId);
        if (item != null) {
            item.setItemEnabled(enabled);
            item.setClickable(enabled);
            item.setEnabled(enabled);
        }
    }

    @Override
    public boolean isItemEnable(int itemId) {
        FooterBarItem item = (FooterBarItem) getItem(itemId);
        if (item != null) {
            return item.isItemEnabled();
        }
        return false;
    }

    @Override
    public void setOnFooterItemClick(OnFooterItemClick listener) {
        mOnFooterBarItemListener = listener;
    }

    @Override
    public void setItemBackgroundResource(int itemId, int resId) {
        FooterBarItem item = (FooterBarItem) getItem(itemId);
        if (item != null) {
            item.setBackgroundResource(resId);
        }
    }

    @Override
    public void setItemBackgroundColor(int itemId, int colorId) {
        FooterBarItem item = (FooterBarItem) getItem(itemId);
        if (item != null) {
            item.setBackgroundColor(colorId);
        }
    }

    @Override
    public void setFooterBarBackgroundResource(int resId) {
        for (FooterBarItem item : mItemList) {
            item.setBackgroundResource(resId);
        }
    }

    @Override
    public void setFooterBarBackgroundColor(int colorId) {
        for (FooterBarItem item : mItemList) {
            item.setBackgroundColor(colorId);
        }
    }

    @Override
    public void setItemSelected(int itemId, boolean selected) {
        FooterBarItem item = (FooterBarItem) getItem(itemId);
        if (item != null) {
            item.setSelected(selected);
        }
    }

    @Override
    public boolean isItemSelected(int itemId) {
        FooterBarItem item = (FooterBarItem) getItem(itemId);
        if (item != null) {
            return item.isSelected();
        }
        return false;
    }

    @Override
    public void updateItems() {
        addItemView();
    }


    protected void addItemView() {
        mContentView.removeAllViews();
        for (int i = 0; i < mItemList.size(); i++) {
            LayoutParams layoutParams = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
            mContentView.addView(mItemList.get(i), layoutParams);
        }
    }

    @Override
    public void setPrimaryItemCount(int count) {
    }
}
