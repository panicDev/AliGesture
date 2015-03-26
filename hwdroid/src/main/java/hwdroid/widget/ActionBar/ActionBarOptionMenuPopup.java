package hwdroid.widget.ActionBar;

import android.content.Context;
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

import hwdroid.widget.FooterBar.FooterBarPopupListAdapter;
import hwdroid.widget.item.Item;
import hwdroid.widget.item.TextItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @hide
 */
public class ActionBarOptionMenuPopup {

    interface ActionBarOptionMenuListener {
        void onOptionMenuClick(int pos);

        void onOptionMenuClose(boolean isClose);
    }

    private Context mContext;
    private PopupWindow mPopupWindows;
    private LinearLayout mMainView;

    ActionBarOptionMenuListener mListener;

    @SuppressWarnings("deprecation")
    public ActionBarOptionMenuPopup(
            Context context,
            ActionBarOptionMenuListener listener) {

        mContext = context;
        mListener = listener;
        mPopupWindows = new PopupWindow(context);

        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight() - statusBarHeight
                - Math.round(context.getResources().getDimension(R.dimen.hw_action_bar_height));

        mPopupWindows.setWidth(width);
        mPopupWindows.setHeight(height);

        mPopupWindows.setAnimationStyle(R.style.HWDroid_Widget_PopupWindows);
        mPopupWindows.setFocusable(true);

        mPopupWindows.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.hw_footerbar_popup_window_background_color));

        LinearLayout contentView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.hw_action_bar_option_menu_popup_layout, null);
        mMainView = (LinearLayout) contentView.findViewById(R.id.hw_actionbar_option_menu_popup_view);
        FrameLayout placeHolder = (FrameLayout) contentView.findViewById(R.id.hw_actionbar_option_menu_popup_placeholder);
        placeHolder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dimssWithAnimation(mContext);
            }
        });

        mPopupWindows.setContentView(contentView);

        mPopupWindows.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                callDismissListener(true);
            }
        });
    }

    public void showOptionView(View anchor, CharSequence[] optionItem, boolean[] disables) {
        if (anchor == null || optionItem == null) return;

        //correct the disabled array.
        if (disables == null || disables.length != optionItem.length) {
            disables = new boolean[optionItem.length];
        }

        List<Item> optionListItems = new ArrayList<Item>();

        TextItem singleItem;

        for (int i = 0; i < optionItem.length; i++) {
            singleItem = new TextItem(optionItem[i].toString());
            singleItem.setEnabled(!disables[i]);
            optionListItems.add(singleItem);
        }

        ListView listView = new ListView(anchor.getContext());
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                dimssWithAnimation(mContext);
                callOptionItemClick(arg2);
            }
        });

        FooterBarPopupListAdapter adapter = new FooterBarPopupListAdapter(listView, anchor.getContext(), optionListItems);
        listView.setAdapter(adapter);

        mPopupWindows.showAsDropDown(anchor);
        mMainView.removeAllViews();
        mMainView.addView(listView);
        Animation anim = AnimationUtils.loadAnimation(anchor.getContext(), R.anim.hw_push_down_in);
        mMainView.startAnimation(anim);
        callDismissListener(false);
    }

    public boolean dimssWithAnimation(Context context) {
        if (!isShowing()) return false;

        Animation anim = AnimationUtils.loadAnimation(context, R.anim.hw_push_down_out);
        anim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPopupWindows.dismiss();
                callDismissListener(true);
            }
        });
        mMainView.startAnimation(anim);
        return true;
    }

    public boolean isShowing() {
        if (mPopupWindows != null) {
            return mPopupWindows.isShowing();
        }

        return false;
    }

    private void callDismissListener(boolean status) {
        if (mListener != null) {
            mListener.onOptionMenuClose(status);
        }
    }

    private void callOptionItemClick(int pos) {
        if (mListener != null) {
            mListener.onOptionMenuClick(pos);
        }
    }
}
