package hwdroid.widget.FooterBar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.hw.droid.R;

import hwdroid.widget.FooterBar.FooterBarType.OnFooterItemClick;
import hwdroid.widget.item.Item;
import hwdroid.widget.item.TextItem;

import java.util.ArrayList;

/**
 * @hide
 */
public class FooterBarMenuPopup {
    private PopupWindow listPopupWindow;
    private ListView mListView;
    private View mAnchorView;
    private OnFooterItemClick mOnFooterBarItemListener;
    private ArrayList<Item> mMorePopupItems;

    @SuppressWarnings("deprecation")
    public FooterBarMenuPopup(
            final Context context,
            View anchorView) {
        mAnchorView = anchorView;

        listPopupWindow = new PopupWindow(context);
        listPopupWindow.setAnimationStyle(R.style.HWDroid_Widget_PopupWindows);
        listPopupWindow.setFocusable(true);

        LinearLayout contentView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.hw_footer_popup_content_view, null);
        mListView = (ListView) contentView.findViewById(R.id.hw_footer_popup_list);
        FrameLayout placeHolder = (FrameLayout) contentView.findViewById(R.id.hw_footer_popup_placeholder);
        placeHolder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dimssWithAnimation(context);
            }
        });
        measureContent(context);
        listPopupWindow.setContentView(contentView);
        listPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.hw_footerbar_popup_window_background_color));
        mMorePopupItems = new ArrayList<Item>();
    }

    public void showPopup(final Context context,
                          final int showCount,
                          final ArrayList<FooterBarItem> items,
                          final FooterBarMenuItem moreItem) {

        mMorePopupItems.clear();

        TextItem item;
        for (int i = showCount; i < items.size(); i++) {
            item = new TextItem((String) items.get(i).getItemTitle());
            item.setEnabled(items.get(i).isEnabled());
            mMorePopupItems.add(item);
        }

        FooterBarPopupListAdapter adapter = new FooterBarPopupListAdapter(mListView, context, mMorePopupItems);
        mListView.setAdapter(adapter);
        listPopupWindow.showAsDropDown(mAnchorView);
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.hw_push_up_in);
        mListView.startAnimation(anim);

        listPopupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                if (moreItem != null) {
                    moreItem.setSelected(false);
                }
            }

        });

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                FooterBarItem itemView = null;

                try {
                    itemView = (FooterBarItem) items.get(showCount + arg2);
                } catch (Exception e) {
                    return;
                }

                if (itemView == null || !itemView.isEnabled()) return;

                dimssWithAnimation(context);

                if (mOnFooterBarItemListener != null) {
                    mOnFooterBarItemListener.onFooterItemClick(itemView, itemView.getItemId());
                }
            }
        });
    }

    public boolean dimssWithAnimation(Context context) {
        if (!listPopupWindow.isShowing()) return false;

//        Animation anim = AnimationUtils.loadAnimation(context, R.anim.hw_push_up_out);
//        anim.setAnimationListener(new AnimationListener() {
//            
//            @Override
//            public void onAnimationStart(Animation animation) {}
//            
//            @Override
//            public void onAnimationRepeat(Animation animation) {}
//            
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                listPopupWindow.dismiss();
//            }
//        });

        listPopupWindow.dismiss();
//        mListView.startAnimation(anim);
        return true;
    }

    public void setOnFooterItemClick(OnFooterItemClick listener) {
        mOnFooterBarItemListener = listener;
    }

    public void onWindowFocusChanged(Context context, boolean hasWindowFocus) {
        if (hasWindowFocus) {
            measureContent(context);
        }
    }

    private void measureContent(Context context) {
        if (listPopupWindow != null && listPopupWindow.isShowing()) {
            listPopupWindow.dismiss();
        }
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight() - statusBarHeight
                - Math.round(context.getResources().getDimension(R.dimen.hw_footerbar_height));
        listPopupWindow.setWidth(width);
        listPopupWindow.setHeight(height);
    }
}
