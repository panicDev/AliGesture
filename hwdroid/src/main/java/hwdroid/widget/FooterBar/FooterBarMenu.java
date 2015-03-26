
package hwdroid.widget.FooterBar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.hw.droid.R;

import hwdroid.widget.ActionSheet;
import hwdroid.widget.ActionSheet.CommonButtonListener;

import java.util.Arrays;

/**
 * a footerbarmenu View class.
 * the view is independent
 * <p>
 * create it and add to any view group.
 * </p>
 * <p>
 * sample
 * </p>
 * <p>
 * LinearLayout layout = new LinearLayout(context);
 * FooterBarMenu menu = new FooterBarMenu(context);
 * layout.addView(menu);
 * </p>
 */
public class FooterBarMenu extends FooterBarView implements CommonButtonListener {

    private final static int MAXITEMSIZE = 10;
    private final static int SHOW_MAX_ITEM = 4;
    public final static int MORE_ITEM_ID = Integer.MIN_VALUE + 10;

    private int mShowCount = SHOW_MAX_ITEM;

    private int items_margin;
    private int menu_item_margin_top;
    private int menu_item_margin_bottom;

    private FooterBarMenuItem mMoreItem;
    private Context mContext;

    private ActionSheet mActionSheet;
    private boolean isActionSheetInit = false;

    public FooterBarMenu(Context context) {
        this(context, null);
    }

    public FooterBarMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setItemType(VERTICAL_ITEM_TYPE);
        setMaxItemSize(MAXITEMSIZE);

        mContext = this.getContext();

        items_margin = mContext.getResources().getDimensionPixelSize(
                R.dimen.hw_footerbar_menu_item__half_margin);
        menu_item_margin_top = mContext.getResources().getDimensionPixelSize(
                R.dimen.hw_footerbar_menu_item_margin_top);
        menu_item_margin_bottom = mContext.getResources().getDimensionPixelSize(
                R.dimen.hw_footerbar_menu_item_margin_bottom);

        mMoreItem = new FooterBarMenuItem(this.getContext());
        Drawable d = getResources().getDrawable(R.drawable.hw_footerbar_menu_item_more_icon);
        mMoreItem.setItemDrawableIcon(d);
        mMoreItem.setItemTitle(R.string.hw_more);
        //mMoreItem.setItemTextColor(getResources().getColor(R.color.hw_footerbar_menu_text_color));
        mMoreItem.setItemTextSize(getResources().getDimensionPixelSize((R.dimen.hw_foooterbar_menu_textsize)));
        mMoreItem.setOrientation(VERTICAL_ITEM_TYPE);
        mMoreItem.setGravity(Gravity.CENTER_HORIZONTAL);
        mMoreItem.setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.hw_footerbar_menu_item_width));

        mMoreItem.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (mOnFooterBarItemListener != null) {
                    if (onFooterBarViewClick()) return;
                    mOnFooterBarItemListener.onFooterItemClick(v, MORE_ITEM_ID);
                }

                if (!isActionSheetInit) {
                    for (int i = 0; i < mItemList.size(); i++) {
                        mItemList.get(i).setItemSelected(false);
                    }
                    int size = mItemList.size() - mShowCount;
                    String[] items = new String[size];
                    Boolean[] enables = new Boolean[size];

                    Boolean[] mark = new Boolean[size];
                    int[] markCount = new int[size];

                    for (int i = 0; i < size; i++) {
                        items[i] = mItemList.get(mShowCount + i).getItemTitle().toString();
                        enables[i] = mItemList.get(mShowCount + i).isEnabled();

                        mark[i] = mItemList.get(mShowCount + i).mViewHolder.mark;
                        markCount[i] = mItemList.get(mShowCount + i).mViewHolder.markCount;
                    }
                    mActionSheet.setCommonButtons(Arrays.asList(items), null, Arrays.asList(enables));

                    for (int j = 0; j < size; j++) {
                        if (mark[j] == true) {
                            mActionSheet.showButtonBadgeView(j, markCount[j]);
                        }
                    }
                    isActionSheetInit = true;
                }
                mActionSheet.show(mMoreItem);
            }
        });

        mActionSheet = new ActionSheet(getContext());
        mActionSheet.setCommonButtonListener(this);
        mActionSheet.setCancalButtonVisiblity(true);
    }

    @Override
    protected boolean onFooterBarViewClick() {
        return false;
    }

    protected void addItemView() {
        mContentView.removeAllViews();
        mContentView.setPadding(0, menu_item_margin_top, 0, menu_item_margin_bottom);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,
                LayoutParams.MATCH_PARENT, 1);

        boolean marginsFlag = false;

        int showItemCount = 0;
        boolean showMoreItem = false;
        if (mItemList.size() > mShowCount) {
            showItemCount = mShowCount;
            showMoreItem = true;
        } else {
            showItemCount = mItemList.size();
            showMoreItem = false;
        }

        FooterBarMenuItem item;
        for (int i = 0; i < showItemCount; i++) {
            item = (FooterBarMenuItem) mItemList.get(i);

            if (marginsFlag == true) {
                layoutParams.setMargins(items_margin, 0, items_margin, 0);

            } else {
                layoutParams.setMargins(0, 0, 0, 0);
            }

            mContentView.addView(item, layoutParams);

            marginsFlag = true;
        }

        if (showMoreItem) {
            layoutParams.setMargins(items_margin, 0, items_margin, 0);
            mContentView.addView(mMoreItem, layoutParams);
        }
    }

    public FooterBarMenuItem addItem(int itemId, CharSequence txt) {
        return addItem(itemId, txt, null, (float) 1.0);
    }

    @Override
    public FooterBarMenuItem addItem(int itemId, CharSequence txt, Drawable icon) {
        return addItem(itemId, txt, icon, (float) 1.0);
    }

    private FooterBarMenuItem addItem(int itemId, CharSequence txt, Drawable icon, float weight) {
        if (itemId == MORE_ITEM_ID) {
            throw new RuntimeException("can't add FooterBarMenuItem with id " + MORE_ITEM_ID);
        }
        if (mItemList.size() < getMaxItemSize()) {
            FooterBarMenuItem menuItem = new FooterBarMenuItem(this.getContext());
            //menuItem.setItemTextColor(getResources().getColor(R.color.hw_footerbar_menu_text_color));
            menuItem.setItemTextSize(getResources().getDimensionPixelSize((R.dimen.hw_foooterbar_menu_textsize)));
            menuItem.setItemId(itemId);
            menuItem.setItemTitle(txt);
            menuItem.setItemDrawableIcon(icon);
            menuItem.setOrientation(VERTICAL_ITEM_TYPE);
            menuItem.setOnClickListener(mOnClickListener);
            menuItem.setGravity(Gravity.CENTER_HORIZONTAL);
            menuItem.setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.hw_footerbar_menu_item_width));
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
    }

    @Override
    public void setPrimaryItemCount(int count) {
        if (count < 0) {
            throw new RuntimeException("<FooterBarMenu> count < 0 ");
        }

        if (count < SHOW_MAX_ITEM) {
            mShowCount = count;
        } else {
            mShowCount = SHOW_MAX_ITEM;
        }
    }

    @Override
    public void setOnFooterItemClick(OnFooterItemClick listener) {
        mOnFooterBarItemListener = listener;
    }

    /**
     * <p>
     * dismiss action sheet
     * </p>
     */
    public void dismissPopup() {
        mActionSheet.dismiss(false);
    }

    /**
     * <p>
     * update action sheet layout, suit for landscape and portrait screen
     * rotated.
     * </p>
     */
    public void updatePopupLayout() {
        mActionSheet.updateLayout();
    }

    @Override
    public void onDismiss(ActionSheet actionSheet) {
    }

    @Override
    public void onClick(int position) {
        if (mOnFooterBarItemListener != null && mItemList.size() >= mShowCount) {
            try {
                FooterBarItem itemView = (FooterBarItem) mItemList.get(mShowCount + position);
                mOnFooterBarItemListener.onFooterItemClick(itemView, itemView.getItemId());
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * <p>
     * set mark view
     * </p>
     *
     * @param itemId the item id.
     * @param show   true: show, false : hide
     */
    public void setMarkView(int itemId, boolean show) {
        int index = getItemIndex(itemId);
        if (index < 0 && index >= mItemList.size()) {
            return;
        }

        FooterBarMenuItem item = (FooterBarMenuItem) getItem(itemId);

        if ((index - mShowCount) > 0) {
            item.mViewHolder.mark = show;
            item.mViewHolder.markCount = -1;

            if (mMoreItem != null) {
                mMoreItem.setMarkViewEnable(show);
            }
        } else {
            item.setMarkViewEnable(show);
        }
    }

    /**
     * <p>
     * set mark view
     * </p>
     *
     * @param itemId    the item id.
     * @param show      true: show, false : hide
     * @param markCount the show's count.
     */
    public void setMarkView(int itemId, boolean show, int markCount) {
        int index = getItemIndex(itemId);

        if (index < 0 && index >= mItemList.size()) {
            return;
        }

        FooterBarMenuItem item = (FooterBarMenuItem) getItem(itemId);
        if ((index - mShowCount) >= 0) {
            item.mViewHolder.mark = show;
            item.mViewHolder.markCount = markCount;

            if (mMoreItem != null) {
                mMoreItem.setMarkViewEnable(show);
            }

        } else {
            item.setMarkViewEnable(show);
        }
    }

    @Override
    public void setItemEnable(int itemId, boolean enabled) {
        FooterBarItem item = (FooterBarItem) getItem(itemId);
        if (item != null && (item.isItemEnabled() != enabled)) {
            int itemIndex = getItemIndex(itemId);
            if (itemIndex >= mShowCount) {
                mActionSheet.setCommonButtonEnable(itemIndex - mShowCount, enabled);
            }
            item.setItemEnabled(enabled);
            item.setClickable(enabled);
            item.setEnabled(enabled);
        }
    }

    @Override
    public void updateItems() {
        super.updateItems();
        isActionSheetInit = false;
    }
}
